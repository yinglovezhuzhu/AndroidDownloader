/*
 * 文件名：MainApplication.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-7-1
 * 修改人：xiaoying
 * 修改时间：2013-7-1
 * 版本：v1.0
 */

package com.opensource.androiddownloader.app;

import android.app.Application;
import android.os.Environment;

/**
 * 功能：
 * @author xiaoying
 *
 */
public class MainApplication extends Application {
	
	public static String APP_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloadDemo/";

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
