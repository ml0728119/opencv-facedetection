package com.hxqc.facedetect;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.DebugLog;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created 胡俊杰
 * 2018/3/28.
 * Todo:
 */

public class SaveMat {


	/**
	 * 保存图片
	 *
	 * @param strPath
	 * @param patamBitmap
	 * @return
	 */
	protected static boolean saveQualityBitmap(String strPath, Bitmap patamBitmap) throws IOException {

//		makeFileDirs(strPath);
		File localFile = new File(strPath);

		FileOutputStream localFileOutputStream = new FileOutputStream(
				localFile);
		patamBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
				localFileOutputStream);
		localFileOutputStream.flush();
		localFileOutputStream.close();

		return true;
	}

	protected static String toSaveMat(Context context, Mat mat) {

		Mat dstMat = mat.clone();
		if (mat.width() > mat.height()) {
			Size rSize = new Size(mat.size().height, mat.size().width);
			Imgproc.resize(mat, dstMat, rSize, 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
		}
		Bitmap mCacheBitmap = Bitmap.createBitmap(dstMat.width(), dstMat.height(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(dstMat, mCacheBitmap);

		String filePath = context.getCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
		DebugLog.e("CameraBridge", "file  " + filePath);
		try {
			SaveMat.saveQualityBitmap(filePath, mCacheBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
}
