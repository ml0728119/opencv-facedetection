package com.hxqc.facedetect;

/**
 * Created 胡俊杰
 * 2018/4/7.
 * Todo: 检测监听
 */

public interface FRCaptureFaceListener {
	/**
	 * 检测完成
	 */
	void captureFaceOK(String picPath);

	/**
	 *
	 */
	void captureFaceProgress(int status);



}
