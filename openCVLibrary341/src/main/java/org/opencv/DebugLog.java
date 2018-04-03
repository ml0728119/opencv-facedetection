//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.opencv;

import android.util.Log;

public class DebugLog {
	public static boolean debug = false;
	static String className;
	static String methodName;
	static int lineNumber;

	private DebugLog() {
	}

	public static void setDebug(boolean debug) {
		DebugLog.debug = debug;
	}

	private static void getMethodNames(StackTraceElement[] sElements) {
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	private static String createLog(String log) {
		return className + "  [" + methodName + ":" + lineNumber + "]  " + log;
	}

	public static void e(String tag, String message) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.e(tag, createLog(message));
		}
	}

	public static void e(String tag, String message, Throwable e) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.e(tag, createLog(message), e);
		}
	}


	public static void i(String tag, String message) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.i(tag, createLog(message));
		}
	}

	public static void d(String tag, String message) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.d(tag, createLog(message));
		}
	}

	public static void v(String tag, String message) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.v(tag, createLog(message));
		}
	}

	public static void w(String tag, String message) {
		if (debug) {
			getMethodNames((new Throwable()).getStackTrace());
			Log.w(tag, createLog(message));
		}
	}

	public static void eIm(String tag, String message) {
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 6)) {
				Log.e(tag, createLog(message));
			}

		} else {
			getMethodNames((new Throwable()).getStackTrace());
			Log.e(tag, createLog(message));
		}
	}

	public static void iIm(String tag, String message) {
		getMethodNames((new Throwable()).getStackTrace());
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 4)) {
				Log.i(tag, createLog(message));
			}

		} else {
			Log.i(tag, createLog(message));
		}
	}

	public static void dIm(String tag, String message) {
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 3)) {
				Log.d(tag, createLog(message));
			}

		} else {
			getMethodNames((new Throwable()).getStackTrace());
			Log.d(tag, createLog(message));
		}
	}

	public static void vIm(String tag, String message) {
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 2)) {
				Log.v(tag, createLog(message));
			}

		} else {
			getMethodNames((new Throwable()).getStackTrace());
			Log.v(tag, createLog(message));
		}
	}

	public static void wIm(String tag, String message) {
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 5)) {
				Log.w(tag, createLog(message));
			}

		} else {
			getMethodNames((new Throwable()).getStackTrace());
			Log.w(tag, createLog(message));
		}
	}

	public static void wtf(String tag, String message) {
		if (tag.length() > 20) {
			tag = tag.substring(0, 19);
		}

		if (!debug) {
			if (Log.isLoggable(tag, 5)) {
				Log.wtf(tag, createLog(message));
			}

		} else {
			getMethodNames((new Throwable()).getStackTrace());
			Log.wtf(tag, createLog(message));
		}
	}
}
