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
 * Usage The listener to listen download state.
 * @author yinglovezhuzhu@gmail.com
 *
 */
public interface DownloadListener {
	
	/**
	 * The callback to listen download size
	 * @param totalSize total size of downloading file.
	 * @param downloadedSize  downloaded size.
	 */
	public void onDownloadSize(int totalSize, int downloadedSize);
}
