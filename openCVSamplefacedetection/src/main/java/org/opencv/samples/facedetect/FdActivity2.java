//package org.opencv.samples.facedetect;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//
//import org.opencv.DebugLog;
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
//public class FdActivity2 extends Activity implements CvCameraViewListener2 {
//
//	private static final String TAG = "OCVSample::Activity";
//	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
//	private static final Scalar EYE_RECT_COLOR = new Scalar(255, 0, 0, 255);
//	public static final int JAVA_DETECTOR = 0;
//	public static final int NATIVE_DETECTOR = 1;
//
//	private MenuItem mItemFace50;
//	private MenuItem mItemFace40;
//	private MenuItem mItemFace30;
//	private MenuItem mItemFace20;
//	private MenuItem mItemType;
//
//	private Mat mRgba;
//	private Mat mGray;
//
//	private CascadeClassifier mFaceJavaDetector;
//	private CascadeClassifier mEyeJavaDetector;
//	private DetectionBasedTracker mFaceNativeDetector;
//
//	private int mDetectorType = JAVA_DETECTOR;
//	private String[] mDetectorName;
//
//	private float mRelativeFaceSize = 0.2f;
//	private int mAbsoluteFaceSize = 0;
//
//	private CameraBridgeViewBase mOpenCvCameraView;
//
//	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//		@Override
//		public void onManagerConnected(int status) {
//			switch (status) {
//				case LoaderCallbackInterface.SUCCESS: {
//					Log.i(TAG, "OpenCV loaded successfully");
//
//					// Load native library after(!) OpenCV initialization
//					System.loadLibrary("detection_based_tracker");
//
//					try {
//						// load cascade file from application resources
//						createFaceDetector();
//						createEyeDetector();
//
//					} catch (IOException e) {
//						e.printStackTrace();
//						Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
//					}
//
//					mOpenCvCameraView.enableView();
//				}
//				break;
//				default: {
//					super.onManagerConnected(status);
//				}
//				break;
//			}
//		}
//	};
//
//	private void createFaceDetector() throws IOException {
////		haarcascade_frontalface_alt2.xml  脸
//
//		String mCascadeFileAbsolutePath = saveCascadeFace("haarcascade_frontalface_alt2.xml");
//		mFaceJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
//		if (mFaceJavaDetector.empty()) {
//			Log.e(TAG, "Failed to load cascade classifier");
//			mFaceJavaDetector = null;
//		} else
//			Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileAbsolutePath);
//
//		mFaceNativeDetector = new DetectionBasedTracker(mCascadeFileAbsolutePath, 0);
//	}
//
//	private void createEyeDetector() throws IOException {
////		haarcascade_eye_tree_eyeglasses.xml 眼睛
//		String mCascadeFileAbsolutePath = saveCascadeFace("haarcascade_eye_tree_eyeglasses.xml");
//		mEyeJavaDetector = new CascadeClassifier(mCascadeFileAbsolutePath);
//		if (mEyeJavaDetector.empty()) {
//			Log.e(TAG, "Failed to load cascade classifier");
//			mEyeJavaDetector = null;
//		} else
//			Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileAbsolutePath);
//
////		mFaceNativeDetector = new DetectionBasedTracker(mCascadeFileAbsolutePath, 0);
//	}
//
//	private String saveCascadeFace(String cascadeName) throws IOException {
//
//		int resID = getResources().getIdentifier(cascadeName.replace(".xml", ""), "raw", this.getPackageName());
//
////		R.raw.lbpcascade_frontalface
//		InputStream is = getResources().openRawResource(resID);
//		File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//		Log.i("Tag", "cascadeDir  " + cascadeDir.getAbsolutePath());
//		File mCascadeFile = new File(cascadeDir, cascadeName);
//		FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//		byte[] buffer = new byte[4096];
//		int bytesRead;
//		while ((bytesRead = is.read(buffer)) != -1) {
//			os.write(buffer, 0, bytesRead);
//		}
//		is.close();
//		os.close();
//		Log.i("Tag", "mCascadeFile.getAbsolutePath()   " + mCascadeFile.getAbsolutePath());
//		return mCascadeFile.getAbsolutePath();
//	}
//
//	public FdActivity2() {
//		mDetectorName = new String[2];
//		mDetectorName[JAVA_DETECTOR] = "Java";
//		mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
//
//		Log.i(TAG, "Instantiated new " + this.getClass());
//	}
//
//	/**
//	 * Called when the activity is first created.
//	 */
//	@SuppressLint("WrongConstant")
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		Log.i(TAG, "called onCreate");
//		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//		setContentView(R.layout.face_detect_surface_view);
//		DebugLog.e("JavaCameraView", "etDefaultDisplay().getRotatio()" + this.getWindowManager().getDefaultDisplay().getRotation());
//
//		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
//		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
//		mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
//		mOpenCvCameraView.setMaxFrameSize(720, 720);
//		mOpenCvCameraView.setCvCameraViewListener(this);
//		mOpenCvCameraView.enableFpsMeter();
//
//
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (mOpenCvCameraView != null)
//			mOpenCvCameraView.disableView();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (!OpenCVLoader.initDebug()) {
//			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//		} else {
//			Log.d(TAG, "OpenCV library found inside package. Using it!");
//			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//		}
//	}
//
//	public void onDestroy() {
//		super.onDestroy();
//		mOpenCvCameraView.disableView();
//	}
//
//	public void onCameraViewStarted(int width, int height) {
//		mGray = new Mat();
//		mRgba = new Mat();
//	}
//
//	public void onCameraViewStopped() {
//		mGray.release();
//		mRgba.release();
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		Log.i(TAG, "called onCreateOptionsMenu");
//		mItemFace50 = menu.add("Face size 50%");
//		mItemFace40 = menu.add("Face size 40%");
//		mItemFace30 = menu.add("Face size 30%");
//		mItemFace20 = menu.add("Face size 20%");
//		mItemType = menu.add(mDetectorName[mDetectorType]);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//		if (item == mItemFace50)
//			setMinFaceSize(0.5f);
//		else if (item == mItemFace40)
//			setMinFaceSize(0.4f);
//		else if (item == mItemFace30)
//			setMinFaceSize(0.3f);
//		else if (item == mItemFace20)
//			setMinFaceSize(0.2f);
//		else if (item == mItemType) {
//			int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
//			item.setTitle(mDetectorName[tmpDetectorType]);
//			setDetectorType(tmpDetectorType);
//		}
//		return true;
//	}
//
//	int i = 0;
//
//	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//
//
//		mRgba = inputFrame.rgba();
//		mGray = inputFrame.gray();
//		//竖屏翻转
//		Mat mRgbaT = new Mat();
//		Core.transpose(mRgba, mRgbaT); //转置函数，可以水平的图像变为垂直
//		Imgproc.resize(mRgbaT, mRgba, mRgba.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
//		Core.flip(mRgba, mRgba, 1); //根据x,y轴翻转，0-x 1-y
//		Core.flip(mRgba, mRgba, 0); //根据x,y轴翻转，0-x 1-y
////
//		Core.transpose(mGray, mRgbaT); //转置函数，可以水平的图像变为垂直
//		Imgproc.resize(mRgbaT, mGray, mGray.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
//		Core.flip(mGray, mGray, 1); //根据x,y轴翻转，0-x 1-y
//		Core.flip(mGray, mGray, 0); //根据x,y轴翻转，0-x 1-y
//
//		i++;
//		if (i % 2 == 1) {
//			return mRgba;
//		}
//
//		if (mAbsoluteFaceSize == 0) {
//			int height = mGray.rows();
//			if (Math.round(height * mRelativeFaceSize) > 0) {
//				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//			}
//			mFaceNativeDetector.setMinFaceSize(100);
//		}
//
//		MatOfRect faces = new MatOfRect();
//		if (mDetectorType == JAVA_DETECTOR) {
//			if (mFaceJavaDetector != null)
//				mFaceJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//						new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//		} else if (mDetectorType == NATIVE_DETECTOR) {
//			if (mFaceNativeDetector != null)
//				mFaceNativeDetector.detect(mGray, faces);
//		} else {
//			Log.e(TAG, "Detection method is not selected!");
//		}
//
//		Rect[] facesArray = faces.toArray();
//		faceLength = facesArray.length;
//
//		if (facesArray.length >= 1) {
//			detectorEye(faces, mRgba);
//		} else {
//			clearEye();
//		}
//		if (BuildConfig.DEBUG) {
//			for (Rect aFacesArray : facesArray) {
//				Imgproc.rectangle(mRgba, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);
//			}
//		}
//
//		return mRgba;
//	}
//
//
//	private void detectorEye(MatOfRect eyesR, Mat rgbaMat) {
//		MatOfRect eyesRA = new MatOfRect();
//		if (mDetectorType == JAVA_DETECTOR) {
//			if (mEyeJavaDetector != null)
//				mEyeJavaDetector.detectMultiScale(mGray, eyesRA, 1.1, 2, 0, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//						new Size(60, 60), new Size());
//		}
//		Rect[] eyeArray = eyesRA.toArray();
//		if (BuildConfig.DEBUG) {
//			for (Rect anEyeArray : eyeArray) {
//				Imgproc.rectangle(mRgba, anEyeArray.tl(), anEyeArray.br(), EYE_RECT_COLOR, 3);
//			}
//		}
//		blink(rgbaMat, eyeArray.length);
//	}
//
//	ArrayList<EyeRectMat> blinkMat = new ArrayList<>();
//
//	int blinkLastEyeCount = -1;//识别栈最后一帧眼睛个数
//
//	private void clearEye() {
//		blinkMat.clear();
//		blinkLastEyeCount = -1;
//	}
//
//	private synchronized void blink(Mat rgbaMat, int eyesCount) {
//
//		EyeRectMat eyeRectMat = new EyeRectMat(rgbaMat, eyesCount);
//		if (eyesCount != 0) {
//			DebugLog.i("Activity", "size  " + blinkMat.size() + "  eyeCount  " + eyesCount + "  " + eyeRectMat.hashCode());
//		} else {
//			DebugLog.e("Activity", "size  " + blinkMat.size() + "  eyeCount  " + eyesCount + "  " + eyeRectMat.hashCode());
//		}
//		if (!(blinkLastEyeCount == 0 && eyesCount == 0)) {
//			blinkMat.add(eyeRectMat);
//			blinkLastEyeCount = eyesCount;
//			if (blinkMat.size() > 4) {
//				blinkMat.remove(0);
//			}
//		}
//
//		if (BuildConfig.DEBUG) {
//			StringBuilder abc = new StringBuilder();
//			for (int i = 0; i < blinkMat.size(); i++) {
//				abc.append("eyeCount  ").append(blinkMat.get(i).eyesCount).append(";  ");
//			}
//			DebugLog.i("Activity", abc.toString());
//		}
//		if (blinkMat.size() > 3 && blinkMat.get(0).eyesCount > 0 &&
//				blinkMat.get(1).eyesCount == 0 && blinkMat.get(2).eyesCount > 0 &&
//				blinkMat.get(3).eyesCount > 0) {
//			DebugLog.e("Activity", "---------------识别成功");
//			toSaveMat(rgbaMat);
//			blinkMat.clear();
//			handler.sendEmptyMessage(0);
//		}
//
//	}
//
//	Handler handler = new Handler(new Handler.Callback() {
//		@Override
//		public boolean handleMessage(Message msg) {
//			mOpenCvCameraView.disableView();
//			return false;
//		}
//	});
//
//	class EyeRectMat {
//		Mat rgbaMat;
//		int eyesCount;
//
//		EyeRectMat(Mat rgbaMat, int eyesCount) {
//			this.rgbaMat = rgbaMat;
//			this.eyesCount = eyesCount;
//		}
//	}
//
//	private void setMinFaceSize(float faceSize) {
//		mRelativeFaceSize = faceSize;
//		mAbsoluteFaceSize = 0;
//	}
//
//	private void setDetectorType(int type) {
//		if (mDetectorType != type) {
//			mDetectorType = type;
//
//			if (type == NATIVE_DETECTOR) {
//				Log.i(TAG, "Detection Based Tracker enabled");
//				mFaceNativeDetector.start();
//			} else {
//				Log.i(TAG, "Cascade detector enabled");
//				mFaceNativeDetector.stop();
//			}
//		}
//	}
//
//	boolean isSave = true;
//	int faceLength = 0;
//
//	public void save(View view) {
//		isSave = true;
//	}
//
//	private void toSaveMat(Mat mat) {
//
//
//		Mat dstMat = mat.clone();
//		if (mat.width() > mat.height()) {
//			Size rSize = new Size(mat.size().height, mat.size().width);
//			Imgproc.resize(mat, dstMat, rSize, 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
//		}
//		Bitmap mCacheBitmap = Bitmap.createBitmap(dstMat.width(), dstMat.height(), Bitmap.Config.ARGB_8888);
//		Utils.matToBitmap(dstMat, mCacheBitmap);
//
//		String filePath = FdActivity2.this.getCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
//		DebugLog.e("CameraBridge", "file  " + filePath);
//		a.saveQualityBitmap(filePath, mCacheBitmap, 100);
//		isSave = false;
//
//	}
//
//	public void stop(View view) {
//
//		mOpenCvCameraView.disableView();
//	}
//
//	public void start(View view) {
//		mOpenCvCameraView.enableView();
//	}
//
//
//}
