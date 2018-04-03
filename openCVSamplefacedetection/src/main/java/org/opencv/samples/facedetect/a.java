package org.opencv.samples.facedetect;

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
	public static boolean saveQualityBitmap(String strPath, Bitmap patamBitmap, int quality) {

//		makeFileDirs(strPath);
		File localFile = new File(strPath);
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					localFile);
			patamBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
					localFileOutputStream);
			localFileOutputStream.flush();
			localFileOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
