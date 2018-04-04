package com.hxqc.facedetect;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created 胡俊杰
 * 2018/3/28.
 * Todo:
 */

public class a {


	/**
	 * 保存图片
	 *
	 * @param strPath
	 * @param patamBitmap
	 * @return
	 */
	public static boolean saveQualityBitmap(String strPath, Bitmap patamBitmap) throws IOException {

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
}
