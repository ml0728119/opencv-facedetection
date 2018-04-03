package org.opencv.samples.facedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.DebugLog;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
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
 * Todo:
 */

public class AA extends JavaCameraView implements CameraBridgeViewBase.CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";
	private Mat mRgba;
	private Mat mGray;
	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
	private static final Scalar EYE_RECT_COLOR = new Scalar(255, 0, 0, 255);
	private float mRelativeFaceSize = 0.2f;
	private int mAbsoluteFaceSize = 0;
	public static final int JAVA_DETECTOR = 0;
	public static final int NATIVE_DETECTOR = 1;
	private int mDetectorType = JAVA_DETECTOR;
	private String[] mDetectorName;
	private CascadeClassifier mFaceJavaDetector;
	private CascadeClassifier mEyeJavaDetector;

	BaseLoaderCallback mLoaderCallback;


	public AA(Context context, int cameraId) {
		super(context, cameraId);
	}

	public AA(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		mDetectorName = new String[2];
		mDetectorName[JAVA_DETECTOR] = "Java";
		mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
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
		String mCascadeFileAbsolutePath = saveCascadeFace(getContext(), "haarcascade_frontalface_alt2.xml");
		mFaceJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
		if (mFaceJavaDetector.empty()) {
			Log.e(TAG, "Failed to load cascade classifier");
			mFaceJavaDetector = null;
		} else
			Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileAbsolutePath);


	}

	private void createEyeDetector() throws IOException {
//		haarcascade_eye_tree_eyeglasses.xml 眼睛
		String mCascadeFileAbsolutePath = saveCascadeFace(getContext(), "haarcascade_eye_tree_eyeglasses.xml");
		mEyeJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
		if (mEyeJavaDetector.empty()) {
			Log.e(TAG, "Failed to load cascade classifier");
			mEyeJavaDetector = null;
		} else
			Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileAbsolutePath);

//		mFaceNativeDetector = new DetectionBasedTracker(mCascadeFileAbsolutePath, 0);
	}

	private String saveCascadeFace(Context context, String cascadeName) throws IOException {

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

	int i = 0;

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		//竖屏翻转
		Mat mRgbaT = new Mat();
		Core.transpose(mRgba, mRgbaT); //转置函数，可以水平的图像变为垂直
		Imgproc.resize(mRgbaT, mRgba, mRgba.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		Core.flip(mRgba, mRgba, 1); //根据x,y轴翻转，0-x 1-y
		Core.flip(mRgba, mRgba, 0); //根据x,y轴翻转，0-x 1-y
//
		Core.transpose(mGray, mRgbaT); //转置函数，可以水平的图像变为垂直
		Imgproc.resize(mRgbaT, mGray, mGray.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		Core.flip(mGray, mGray, 1); //根据x,y轴翻转，0-x 1-y
		Core.flip(mGray, mGray, 0); //根据x,y轴翻转，0-x 1-y

		i++;
		if (i % 2 == 1) {
			return mRgba;
		}

		detectorFace();

		return mRgba;
	}

	/**
	 * 识别脸部
	 */
	private void detectorFace() {
		if (mAbsoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * mRelativeFaceSize) > 0) {
				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
			}

		}

		MatOfRect faces = new MatOfRect();
		if (mDetectorType == JAVA_DETECTOR) {
			if (mFaceJavaDetector != null)
				mFaceJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
		}

		Rect[] facesArray = faces.toArray();
		if (facesArray.length >= 1) {
			detectorEye(faces, mRgba);
		} else {
			clearEye();
		}
		if (BuildConfig.DEBUG) {
			for (Rect aFacesArray : facesArray) {
				Imgproc.rectangle(mRgba, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);
			}
		}
	}

	/**
	 * 识别眼睛
	 *
	 * @param eyesR
	 * @param rgbaMat
	 */
	private void detectorEye(MatOfRect eyesR, Mat rgbaMat) {
		MatOfRect eyesRA = new MatOfRect();
		if (mDetectorType == JAVA_DETECTOR) {
			if (mEyeJavaDetector != null)
				mEyeJavaDetector.detectMultiScale(mGray, eyesRA, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						new Size(60, 60), new Size());
		}
		Rect[] eyeArray = eyesRA.toArray();
		if (BuildConfig.DEBUG) {
			for (Rect anEyeArray : eyeArray) {
				Imgproc.rectangle(mRgba, anEyeArray.tl(), anEyeArray.br(), EYE_RECT_COLOR, 3);
			}
		}
		blink(rgbaMat, eyeArray.length);
	}

	private ArrayList<EyeRectMat> blinkMat = new ArrayList<>();

	private int blinkLastEyeCount = -1;//识别栈最后一帧眼睛个数

	private void clearEye() {
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
			toSaveMat(rgbaMat);
			blinkMat.clear();
			handler.sendEmptyMessage(0);
		}

	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			disableView();
			return false;
		}
	});


	class EyeRectMat {
		Mat rgbaMat;
		int eyesCount;

		EyeRectMat(Mat rgbaMat, int eyesCount) {
			this.rgbaMat = rgbaMat;
			this.eyesCount = eyesCount;
		}
	}


	private void toSaveMat(Mat mat) {

		Mat dstMat = mat.clone();
		if (mat.width() > mat.height()) {
			Size rSize = new Size(mat.size().height, mat.size().width);
			Imgproc.resize(mat, dstMat, rSize, 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		}
		Bitmap mCacheBitmap = Bitmap.createBitmap(dstMat.width(), dstMat.height(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(dstMat, mCacheBitmap);

		String filePath = getContext().getCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
		DebugLog.e("CameraBridge", "file  " + filePath);
		a.saveQualityBitmap(filePath, mCacheBitmap, 100);
	}
}
