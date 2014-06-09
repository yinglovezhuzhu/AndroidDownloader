/*
 * Copyright (C) 2014-3-19 下午2:13:07 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Auther：yinglovezhuzhu@gmail.com
 * FileName:DownloadListener.java
 * Date：2014-3-19
 * Version：v1.0
 */

package com.opensource.downloader;

/**
 * 功能：下载进度监听器
 * @author xiaoying
 *
 */
public interface DownloadListener {
	
	/**
	 * 下载进度回调
	 * @param totalSize 总大小
	 * @param downloadedSize 已下载大小
	 */
	public void onDownloadSize(int totalSize, int downloadedSize);
}
