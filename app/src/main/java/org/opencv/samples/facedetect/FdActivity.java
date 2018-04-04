package org.opencv.samples.facedetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.hxqc.facedetect.FaceDetectorCameraView;

import org.opencv.DebugLog;
import org.opencv.android.CameraBridgeViewBase;

public class FdActivity extends Activity {

	private static final String TAG = "OCVSample::Activity";

	private FaceDetectorCameraView mOpenCvCameraView;

	public FdActivity() {

	}

	/**
	 * Called when the activity is first created.
	 */
	@SuppressLint("WrongConstant")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DebugLog.debug = true;
		setContentView(R.layout.face_detect_surface_view);
		DebugLog.e("JavaCameraView", "etDefaultDisplay().getRotatio()" + this.getWindowManager().getDefaultDisplay().getRotation());

		mOpenCvCameraView = (FaceDetectorCameraView) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
		mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
		mOpenCvCameraView.setMaxFrameSize(720, 720);


	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		mOpenCvCameraView.onResume();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}


	public void save(View view) {
	}


	public void stop(View view) {

		mOpenCvCameraView.disableView();
	}

	public void start(View view) {
		mOpenCvCameraView.enableView();
	}


}
