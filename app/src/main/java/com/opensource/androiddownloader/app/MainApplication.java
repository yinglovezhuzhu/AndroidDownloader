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
