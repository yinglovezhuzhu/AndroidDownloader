/*
 * Copyright (C) 2014-4-17 下午4:45:10 The Android Open Source Project
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
 * FileName:DownloadThread.java
 * Date：2014-4-17
 * Version：v1.0
 */	

package com.opensource.downloader;

import com.opensource.downloader.utils.LogUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 功能：下载线程类
 * @author xiaoying
 *
 */
public class DownloadThread extends Thread {
	
	private static final String TAG = DownloadThread.class.getSimpleName();
	
	private static final int BUFFER_SIZE = 1024 * 8;

	private Downloader mDownloader;
	private URL mUrl;
	private File mSavedFile;
	private int mBlockSize;
	private int mDownloadedSize;
	private int mThreadId = -1;

	private boolean mIsFinished = false;

	/**
	 * 构造方法
	 * 
	 * @param downloader 文件下载的FileDownloader对象
	 * @param downUrl 所下载文件的Url
	 * @param saveFile 将文件保存的路径
	 * @param block 给该线程分配的下载的长度
	 * @param downloadedLength 该线程正在的下载的长度 if(downloadLength<block) 说明未下载完成 else 下载完成
	 * @param threadId 该线程的ID标识
	 */
	public DownloadThread(Downloader downloader, URL downUrl, File saveFile, int block, int downloadedLength, int threadId) {
		this.mUrl = downUrl;
		mSavedFile = saveFile;
		this.mBlockSize = block;
		this.mDownloader = downloader;
		mThreadId = threadId;
		this.mDownloadedSize = downloadedLength;
	}

	@Override
	public void run() {
		if (mDownloadedSize < mBlockSize) {// 如果该条线程为下载完成
			try {
				HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "*/*"); // 设置客户端可以接受的返回数据类型
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Referer", mUrl.toString());// 设置请求的来源，便于对访问来源进行统计
				conn.setRequestProperty("Charset", "UTF-8");

				// 计算该线程下载的开始位置
				int startPos = mBlockSize * (mThreadId - 1) + mDownloadedSize;
				// 计算该线程下载的结束位置
				int endPos = mBlockSize * mThreadId - 1;

				// 设置获取实体数据的范围,如果超过了实体数据的大小会自动返回实际的数据大小
				conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);

				// 客户端用户代理
				conn.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0;"
								+ " Windows NT 5.2; Trident/4.0;"
								+ " .NET CLR 1.1.4322;"
								+ " .NET CLR 2.0.50727;"
								+ " .NET CLR 3.0.04506.30;"
								+ " .NET CLR 3.0.4506.2152;"
								+ " .NET CLR 3.5.30729)");

				// 使用长连接
				conn.setRequestProperty("Connection", "Keep-Alive");
				// 获取远程连接的输入流
				InputStream inStream = conn.getInputStream();
				// 设置本地数据缓存的大小
				byte[] buffer = new byte[BUFFER_SIZE];
				// 设置每次读取的数据量
				int offset = 0;
				// 打印该线程开始下载的位置
				LogUtils.i(TAG, mThreadId + " starts to download from position " + startPos);
				RandomAccessFile threadFile = new RandomAccessFile(mSavedFile, "rwd");
				// 文件指针指向开始下载的位置
				threadFile.seek(startPos);
				// 但用户没有要求停止下载，同时没有到达请求数据的末尾时候会一直循环读取数据
				// 直接把数据写到文件中
				while (!mDownloader.isStoped() && (offset = inStream.read(buffer)) != -1) {
					threadFile.write(buffer, 0, offset);
					mDownloadedSize += offset;
					// 把该线程已经下载的数据长度更新到数据库和内存哈希表中
					mDownloader.update(mThreadId, mDownloadedSize);
					// 把新下载的数据长度加入到已经下载的数据总长度中
					mDownloader.append(offset);
				}
				threadFile.close();
				inStream.close();

				if (mDownloader.isStoped()) {
					LogUtils.i(TAG, "Download thread " + mThreadId + " has been paused");
				} else {
					LogUtils.i(TAG, "Download thread " + mThreadId + " has been finished");
				}
				this.mIsFinished = true; // 设置完成标志为true，无论是下载完成还是用户主动中断下载
			} catch (Exception e) {
				// 设置该线程已经下载的长度为-1
				this.mDownloadedSize = -1;
				LogUtils.e(TAG, "Thread " + mThreadId + ":" + e);
			}
		}
	}

	/**
	 * 下载是否完成
	 * 
	 * @return true or false
	 */
	public boolean isFinished() {
		return mIsFinished;
	}

	/**
	 * 已经下载的内容大小
	 * 
	 * @return 如果返回值为-1,代表下载失败
	 */
	public long getDownloadedLength() {
		return mDownloadedSize;
	}
}
