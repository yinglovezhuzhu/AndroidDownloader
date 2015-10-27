/*
 * Copyright (C) 2014 The Android Open Source Project.
 *
 *        yinglovezhuzhu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.opensource.downloader.BuildConfig;

/**
 * usage Log print util
 * @author yinglovezhuzhu@gmail.com
 *
 */
public class LogUtil {

	private final static boolean isPrint = BuildConfig.DEBUG;
	
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
