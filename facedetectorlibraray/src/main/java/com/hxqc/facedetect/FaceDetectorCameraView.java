package com.hxqc.facedetect;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.DebugLog;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created 胡俊杰
 * 2018/4/2.
 * Todo:  人脸识别相机
 */

public class FaceDetectorCameraView extends JavaCameraView implements CameraBridgeViewBase.CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";

	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
	private static final Scalar EYE_RECT_COLOR = new Scalar(255, 0, 0, 255);
	private float mRelativeFaceSize = 0.2f;
	private int mAbsoluteFaceSize = 0;
	public static final int JAVA_DETECTOR = 0;
	public static final int NATIVE_DETECTOR = 1;
	private int mDetectorType = JAVA_DETECTOR;

	private CascadeClassifier mFaceJavaDetector;
	private CascadeClassifier mEyeJavaDetector;

	BaseLoaderCallback mLoaderCallback;
	DetectionBasedTracker mFaceNativeDetector;
	DetectionBasedTracker mEyesNativeDetector;

	public static final int FRCaptureFaceStatusOK = 0;          //检测完成
	public static final int FRCaptureFaceStatusNoFace = 1;       //未检测到脸
	public static final int FRCaptureFaceStatusMoreThanOneFace = 2;     //有多张脸
	public static final int FRCaptureFaceStatusNoBlink = 3;      //未眨眼
	public static final int FRCaptureFaceStatusNoCamera = 4;     //无权限
	public static final int FRCaptureFaceStatusIllegalData = 5;  //检测非法

	FRCaptureFaceListener mFrCaptureListener;

	public FRCaptureFaceListener getFrCaptureListener() {
		return mFrCaptureListener;
	}

	public void setFrCaptureListener(FRCaptureFaceListener mFrCaptureListener) {
		this.mFrCaptureListener = mFrCaptureListener;
	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case FRCaptureFaceStatusOK:
					disableView();
					Bundle data = msg.getData();
					String filePath = data.getString("picPath");
					if (mFrCaptureListener != null) {
						mFrCaptureListener.captureFaceOK(filePath);
					}
				default:
					if (mFrCaptureListener != null) {
						mFrCaptureListener.captureFaceProgress(msg.what);
					}
					break;
			}
			return false;
		}
	});

	public FaceDetectorCameraView(Context context, int cameraId) {
		super(context, cameraId);
		init();
		setCvCameraViewListener(this);
	}


	public FaceDetectorCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setCvCameraViewListener(this);
	}

	public void init() {
		mLoaderCallback = new LoaderCallback(getContext());
	}

	class LoaderCallback extends BaseLoaderCallback {
		public LoaderCallback(Context AppContext) {
			super(AppContext);
		}

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");
					// Load native library after(!) OpenCV initialization
					System.loadLibrary("detection_based_tracker");
					try {
						// load cascade file from application resources
						createFaceDetector();
						createEyeDetector();
					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
					}
					enableView();
				}
				break;
				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	}


	public void onResume() {

//		if (!OpenCVLoader.initDebug()) {
//			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getContext(), mLoaderCallback);
//		} else {
		Log.d(TAG, "OpenCV library found inside package. Using it!");
		mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//		}
	}

	private void createFaceDetector() throws IOException {
//		haarcascade_frontalface_alt2.xml  脸
		String mCascadeFileAbsolutePath = saveCascadeFaceModel(getContext(), "haarcascade_frontalface_alt2.xml");
		if (mDetectorType == JAVA_DETECTOR) {
			mFaceJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
			if (mFaceJavaDetector.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mFaceJavaDetector = null;
			}
		} else {
			mFaceNativeDetector = new DetectionBasedTracker(mCascadeFileAbsolutePath, 100);
			mFaceNativeDetector.start();
		}
	}

	private void createEyeDetector() throws IOException {
//		haarcascade_eye_tree_eyeglasses.xml 眼睛
		String mCascadeFileAbsolutePath = saveCascadeFaceModel(getContext(), "haarcascade_eye_tree_eyeglasses.xml");
		if (mDetectorType == JAVA_DETECTOR) {
			mEyeJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
			if (mEyeJavaDetector.empty()) {
				mEyeJavaDetector = null;
			}
		} else {
			mEyesNativeDetector = new DetectionBasedTracker(mCascadeFileAbsolutePath, 10);
			mEyesNativeDetector.start();
		}


	}

	private String saveCascadeFaceModel(Context context, String cascadeName) throws IOException {

		int resID = context.getResources().getIdentifier(cascadeName.replace(".xml", ""), "raw", context.getPackageName());
//		R.raw.lbpcascade_frontalface
		InputStream is = context.getResources().openRawResource(resID);
		File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
		File mCascadeFile = new File(cascadeDir, cascadeName);
		FileOutputStream os = new FileOutputStream(mCascadeFile);

		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		is.close();
		os.close();
		return mCascadeFile.getAbsolutePath();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	@Override
	public void onCameraViewStopped() {

		mGray.release();
		mRgba.release();
	}

	Mat mRgba;
	Mat mGray;
	int i = 0;

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		//竖屏翻转
		Mat mRgbaT = new Mat();
		Core.transpose(mRgba, mRgbaT); //转置函数，可以水平的图像变为垂直

		Imgproc.resize(mRgbaT, mRgba, mRgbaT.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		Core.flip(mRgba, mRgba, 1); //根据x,y轴翻转，0-x 1-y
		Core.flip(mRgba, mRgba, 0); //根据x,y轴翻转，0-x 1-y
////
		Core.transpose(mGray, mRgbaT); //转置函数，可以水平的图像变为垂直
		Imgproc.resize(mRgbaT, mGray, mRgbaT.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		Core.flip(mGray, mGray, 1); //根据x,y轴翻转，0-x 1-y
		Core.flip(mGray, mGray, 0); //根据x,y轴翻转，0-x 1-y

//		i++;
//		if (i % 3 == 0) {
		detectorFace(mRgba, mGray);
//			return mRgba;
//		} else {
//
//			return mRgba;
//		}
		return mRgba;
	}

	/**
	 * 识别脸部
	 */
	private void detectorFace(Mat rgbaMat, Mat grayMat) {
		if (mAbsoluteFaceSize == 0) {
			int height = grayMat.rows();
			if (Math.round(height * mRelativeFaceSize) > 0) {
				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
			}

		}

		MatOfRect faces = new MatOfRect();
		if (mDetectorType == JAVA_DETECTOR) {
			if (mFaceJavaDetector != null)
				mFaceJavaDetector.detectMultiScale(grayMat, faces, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
		} else {
			mFaceNativeDetector.detect(grayMat, faces);
		}


		Rect[] facesArray = faces.toArray();
		if (facesArray.length == 1) {
			mHandler.sendEmptyMessage(FRCaptureFaceStatusNoBlink);

			detectorEye(rgbaMat, grayMat, facesArray[0]);
		} else if (facesArray.length > 1) {
			mHandler.sendEmptyMessage(FRCaptureFaceStatusMoreThanOneFace);
			clearEyeMat();
		} else {
			mHandler.sendEmptyMessage(FRCaptureFaceStatusNoFace);
			clearEyeMat();
		}
//		if (BuildConfig.DEBUG) {
//		for (Rect aFacesArray : facesArray) {
//			Imgproc.rectangle(rgbaMat, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);
//		}
//		}
	}

	/**
	 * 识别眼睛
	 *
	 * @param rgbaMat
	 */

	private void detectorEye(Mat rgbaMat, Mat grayMat, Rect faceRect) {

		Rect eyeRe = faceRect.clone();
		if (eyeRe.x + eyeRe.width > grayMat.cols()) {
			eyeRe.width =(grayMat.cols() - eyeRe.x);
			DebugLog.e("Activity", "aaaa------------------  ");
		}
		if (eyeRe.y + eyeRe.height > grayMat.rows()) {
			eyeRe.height=(grayMat.rows()-eyeRe.y);
			DebugLog.e("Activity", "bbbbb------------------  ");
		}
		double eye[] = new double[]{
				eyeRe.x * 1.12, eyeRe.y * 1.23, eyeRe.width * 0.76, eyeRe.height * 0.43
		};

		eyeRe.set(eye);
		DebugLog.e("Activity", "faceRect  " + faceRect.x + "  " + faceRect.y + "   " + faceRect.width + "   " + faceRect.height);
		DebugLog.e("Activity", "eyeRe     " + eyeRe.x + "  " + eyeRe.y + "   " + eyeRe.width + "   " + eyeRe.height);
		DebugLog.e("Activity", "row      " + (eyeRe.x + eyeRe.width) + "  " + (eyeRe.y + eyeRe.height));
		DebugLog.e("Activity", "grayMat  " + grayMat.cols() + "  " + grayMat.rows() + "   ");
		Mat eyeMat = grayMat.submat(eyeRe);
		DebugLog.e("Activity", "eyeMat  " + eyeMat.cols() + "  " + eyeMat.rows() + "   ");
		MatOfRect eyesRA = new MatOfRect();
//
		if (mDetectorType == JAVA_DETECTOR) {
			if (mEyeJavaDetector != null) {
				mEyeJavaDetector.detectMultiScale(eyeMat, eyesRA, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						new Size(60, 60), new Size());
			}

		} else {
			mEyesNativeDetector.detect(eyeMat, eyesRA);
		}
		Rect[] eyeArray = eyesRA.toArray();
//		if (BuildConfig.DEBUG) {
//		for (Rect anEyeArray : eyeArray) {
//		Imgproc.rectangle(rgbaMat, eyeRe.tl(), eyeRe.br(), EYE_RECT_COLOR, 3);
//		}
//		}
		blink(rgbaMat, eyeArray.length);
	}

	private ArrayList<EyeRectMat> faceMat = new ArrayList<>();
	private ArrayList<EyeRectMat> blinkMat = new ArrayList<>();

	private int blinkLastEyeCount = -1;//识别栈最后一帧眼睛个数

	private void clearEyeMat() {
		blinkMat.clear();
		blinkLastEyeCount = -1;
	}

	/**
	 * 识别眨眼
	 *
	 * @param rgbaMat
	 * @param eyesCount
	 */
	private synchronized void blink(Mat rgbaMat, int eyesCount) {

		EyeRectMat eyeRectMat = new EyeRectMat(rgbaMat, eyesCount);
		if (eyesCount != 0) {
			DebugLog.i("Activity", "size  " + blinkMat.size() + "  eyeCount  " + eyesCount + "  " + eyeRectMat.hashCode());
		} else {
			DebugLog.e("Activity", "size  " + blinkMat.size() + "  eyeCount  " + eyesCount + "  " + eyeRectMat.hashCode());
		}
		if (!(blinkLastEyeCount == 0 && eyesCount == 0)) {
			blinkMat.add(eyeRectMat);
			blinkLastEyeCount = eyesCount;
			if (blinkMat.size() > 4) {
				blinkMat.remove(0);
			}
		}

		if (BuildConfig.DEBUG) {
			StringBuilder abc = new StringBuilder();
			for (int i = 0; i < blinkMat.size(); i++) {
				abc.append("eyeCount  ").append(blinkMat.get(i).eyesCount).append(";  ");
			}
			DebugLog.i("Activity", abc.toString());
		}
		if (blinkMat.size() > 3 && blinkMat.get(0).eyesCount > 0 &&
				blinkMat.get(1).eyesCount == 0 && blinkMat.get(2).eyesCount > 0 &&
				blinkMat.get(3).eyesCount > 0) {
			DebugLog.e("Activity", "---------------识别成功");
			String filePath = SaveMat.toSaveMat(getContext(), rgbaMat);
			blinkMat.clear();

			Message message = new Message();
			message.what = FRCaptureFaceStatusOK;
			Bundle bundle = new Bundle();
			bundle.putString("picPath", filePath);
			message.setData(bundle);
			mHandler.sendMessage(message);
		}

	}


	class EyeRectMat {
		Mat rgbaMat;
		Mat grayMat;
		int eyesCount;

		EyeRectMat(Mat rgbaMat, int eyesCount) {
			this.rgbaMat = rgbaMat;
			this.eyesCount = eyesCount;
		}
	}


}
