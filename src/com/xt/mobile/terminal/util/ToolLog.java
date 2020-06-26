package com.xt.mobile.terminal.util;

import android.util.Log;

/**
 * 日志工具类
 * 
 */
public class ToolLog {
	private static boolean isPrint = true;
	// private static boolean isPrint = false;

	private static final String TAG = "XtLog";

	public static void log(String msg) {
		if (isPrint) {
			Log.v(TAG, msg);
		}
	}
	
	public static void i(String msg) {
		if (isPrint) {
			Log.i(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (isPrint) {
			Log.d(TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if (isPrint) {
			Log.e(TAG, msg);
		}
	}
	
	public static void v(String msg) {
		if (isPrint) {
			Log.v(TAG, msg);
		}
	}
	
	public static void w(String msg) {
		if (isPrint) {
			Log.w(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (isPrint) {
			Log.i(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (isPrint) {
			Log.e(tag, msg);
		}
	}	

	public static void d(String tag, String msg) {
		if (isPrint) {
			Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isPrint) {
			Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (isPrint) {
			Log.w(tag, msg);
		}
	}

}
