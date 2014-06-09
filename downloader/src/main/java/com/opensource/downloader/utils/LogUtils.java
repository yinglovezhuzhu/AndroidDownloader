/*
 * 文件名：LogUtil.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-5-16
 * 修改人：xiaoying
 * 修改时间：2013-5-16
 * 版本：v1.0
 */

package com.opensource.downloader.utils;

import android.util.Log;

/**
 * 功能：日志打印工具类
 * @author xiaoying
 *
 */
public class LogUtils {

	private final static boolean isPrint = true;
	
	public static void i(String tag, String msg) {
		if(isPrint) {
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, Object msg) {
		if(isPrint) {
			Log.i(tag, msg.toString());
		}
	}
	
	public static void w(String tag, String msg) {
		if(isPrint) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, Object msg) {
		if(isPrint) {
			Log.w(tag, msg.toString());
		}
	}
	
	public static void e(String tag, String msg) {
		if(isPrint) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, Object msg) {
		if(isPrint) {
			Log.e(tag, msg.toString());
		}
	}
	
	public static void d(String tag, String msg) {
		if(isPrint) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, Object msg) {
		if(isPrint) {
			Log.d(tag, msg.toString());
		}
	}
	
	public static void v(String tag, String msg) {
		if(isPrint) {
			Log.v(tag, msg);
		}
	}
	
	public static void v(String tag, Object msg) {
		if(isPrint) {
			Log.v(tag, msg.toString());
		}
	}
}
