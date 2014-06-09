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
 * FileName:Downloader.java
 * Date：2014-3-19
 * Version：v1.0
 */

package com.opensource.downloader;

import android.content.Context;

import com.opensource.downloader.db.utils.DownloadLogDBUtils;
import com.opensource.downloader.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 功能：下载器类
 * @author xiaoying
 *
 */
public class Downloader {
	
	private static final String TAG = Downloader.class.getSimpleName();
	
	private static final String TEMP_FILE_SUFFIX = ".download";
	
	private static final int RESPONSE_OK = 200;
	private Context mContext;
	private boolean isStoped; // 停止下载标志
	private int mDownloadedSize = 0; // 已下载文件长度
	private int mFileSize = 0; // 原始文件长度
	private DownloadThread [] mTheadPool; // 根据线程数设置下载线程池
	private File mSavedFile; // 数据保存到的本地文件
	private long mUpdateTime = 1000;
	// 缓存各线程下载的长度
	private Map<Integer, Integer> mData = new ConcurrentHashMap<Integer, Integer>();
	private int mBlockSize; // 每条线程下载的长度
	private String mUrl; // 下载路径
	
	private boolean mIsFinished = false;

	/**
	 * 构建文件下载器
	 * 
	 * @param downloadUrl 下载路径
	 * @param saveFolder 文件保存目录
	 * @param threadNum 下载线程数
	 */
	public Downloader(Context context, String downloadUrl, File saveFolder, int threadNum) {
		try {
			mContext = context;
			mUrl = downloadUrl;
			mTheadPool = new DownloadThread[threadNum];

			checkDownloadFolder(saveFolder);
			
			HttpURLConnection conn = getConnection(downloadUrl);
			
			if (conn.getResponseCode() == RESPONSE_OK) {
				mFileSize = conn.getContentLength();
				// 当文件大小为小于等于零时抛出运行时异常
				if (mFileSize <= 0) {
					throw new RuntimeException("Can't get file size ");
				}
				
				String filename = getFileName(conn);
				// 根据文件保存目录和文件名构建保存文件
				mSavedFile = new File(saveFolder, filename);
				Map<Integer, Integer> logdata = DownloadLogDBUtils.getLogByUrl(mContext, downloadUrl);

				// 如果存在下载记录
				if (logdata.size() > 0) {
					for (Map.Entry<Integer, Integer> entry : logdata.entrySet()) {
						// 把各条线程已经下载的数据长度放入data中
						mData.put(entry.getKey(), entry.getValue());
					}
				}

				// 如果已经下载的数据的线程数和现在设置的线程数相同时则计算所有线程已经下载的数据总长度
				mDownloadedSize = getDownloadedSize();
				// 计算每条线程下载的数据长度
				mBlockSize = getBlockSize(mFileSize, mTheadPool.length);
			} else {
				LogUtils.w(TAG, "服务器响应错误!响应码：" + conn.getResponseCode() + "响应消息：" + conn.getResponseMessage());
				throw new RuntimeException("server response error ");
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.toString());
			throw new RuntimeException("Can't connection this url");
		}
	}
	
	/**
	 * 开始下载文件
	 * 
	 * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(DownloadListener listener) throws Exception { // 进行下载，并抛出异常给调用者，如果有异常的话
		File tempFile = new File(mSavedFile.getAbsolutePath() + TEMP_FILE_SUFFIX);//给正在下载的文件加上一个下载标志行的后缀，以免打开未完成下载的文件而出错
		try {
			RandomAccessFile randOut = new RandomAccessFile(tempFile, "rwd");
			if (mFileSize > 0) {
				randOut.setLength(mFileSize); // 设置文件的大小
			}
			randOut.close(); // 关闭该文件，使设置生效
			URL url = new URL(mUrl);
			if (mData.size() != mTheadPool.length) { // 如果原先未曾下载或者原先的下载线程数与现在的线程数不一致
				mData.clear();
				for (int i = 0; i < mTheadPool.length; i++) { // 遍历线程池
					mData.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
				}
				mDownloadedSize = 0; // 设置已经下载的长度为0
			}
			for (int i = 0; i < mTheadPool.length; i++) {// 开启线程进行下载
				int downloadedLength = mData.get(i + 1); // 通过特定的线程ID获取该线程已经下载的数据长度
				if (downloadedLength < mBlockSize && mDownloadedSize < mFileSize) {// 判断线程是否已经完成下载,否则继续下载
					mTheadPool[i] = new DownloadThread(this, url, tempFile, mBlockSize, mData.get(i + 1), i + 1); // 初始化特定id的线程
					mTheadPool[i].setPriority(7); // 设置线程的优先级，Thread.NORM_PRIORITY
													// = 5 Thread.MIN_PRIORITY =
													// 1 Thread.MAX_PRIORITY =
													// 10
					mTheadPool[i].start(); // 启动线程
					mIsFinished = false;
				} else {
					mTheadPool[i] = null; // 表明在线程已经完成下载任务
					mIsFinished = true;
				}
			}
			DownloadLogDBUtils.delete(mContext, mUrl); // 如果存在下载记录，删除它们，然后重新添加
			DownloadLogDBUtils.save(mContext, mUrl, mData); // 把已经下载的实时数据写入数据库

			boolean isDownloading = true;// 下载未完成
			while (isDownloading) {// 循环判断所有线程是否完成下载
				Thread.sleep(mUpdateTime);
				isDownloading = false;// 假定全部线程下载完成
				for (int i = 0; i < mTheadPool.length; i++) {
					if (mTheadPool[i] != null && !mTheadPool[i].isFinished()) {// 如果发现线程未完成下载
						isDownloading = true;// 设置标志为下载没有完成
						if (mTheadPool[i].getDownloadedLength() == -1) {// 如果下载失败,再重新在已经下载的数据长度的基础上下载
							mTheadPool[i] = new DownloadThread(this, url, tempFile, mBlockSize, mData.get(i + 1), i + 1); // 重新开辟下载线程
							mTheadPool[i].setPriority(7); // 设置下载的优先级
							mTheadPool[i].start(); // 开始下载线程
						}
					}
				}
				mIsFinished = true;
				if (listener != null)
					listener.onDownloadSize(mFileSize, mDownloadedSize);// 通知目前已经下载完成的数据长度
			}
			if (mDownloadedSize == mFileSize) {
				tempFile.renameTo(mSavedFile);
				DownloadLogDBUtils.delete(mContext, mUrl);// 下载完成删除记录
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.toString());// 打印错误
			throw new Exception("File downloads error"); // 抛出文件下载异常
		}
		return mDownloadedSize;
	}


	/**
	 * 是否下载完成
	 * @return
	 */
	public boolean isFinished() {
		return mIsFinished;
	}

	/**
	 * 获取线程数
	 */
	public int getThreadNum() {
		return mTheadPool.length;
	}

	/**
	 * 停止下载
	 */
	public void stop() {
		this.isStoped = true;
	}

	/**
	 * 是否停止下载
	 * @return
	 */
	public boolean isStoped() {
		return this.isStoped;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return mFileSize;
	}

	/**
	 * 设置更新频率
	 * @param updateTime
	 */
	public void setUpdateTime(long updateTime) {
		this.mUpdateTime = updateTime;
	}
	
	/**
	 * 累计已下载大小
	 * 
	 * @param size
	 */
	protected synchronized void append(int size) { // 使用同步关键字解决并发访问问题
		mDownloadedSize += size; // 把实时下载的长度加入到总下载长度中
	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId 线程id
	 * @param pos 最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		mData.put(threadId, pos); // 把制定线程ID的线程赋予最新的下载长度，以前的值会被覆盖掉
		DownloadLogDBUtils.update(mContext, mUrl, threadId, pos); // 更新数据库中指定线程的下载长度
	}

	/**
	 *根据URL获取一个HttpConnection
	 * @param downloadUrl
	 * @return
	 */
	private HttpURLConnection getConnection(String downloadUrl) throws IOException {
		URL url = new URL(downloadUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Referer", downloadUrl);
		conn.setRequestProperty("Charset", "UTF-8");
		// 设置用户代理
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; "
				+ "MSIE 8.0; Windows NT 5.2;"
				+ " Trident/4.0; .NET CLR 1.1.4322;"
				+ ".NET CLR 2.0.50727; " + ".NET CLR 3.0.04506.30;"
				+ " .NET CLR 3.0.4506.2152; " + ".NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.connect();
		LogUtils.i(TAG, getResponseHeader(conn));
		return conn;
	}
	
	/**
	 * 检查下载目录，如果目录不存在，创建目录
	 * @param folder
	 */
	private void checkDownloadFolder(File folder) {
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}
	
	/**
	 * 计算分块大小
	 * @param fileSize
	 * @param blockNum
	 * @return
	 */
	private int getBlockSize(int fileSize, int blockNum) {
		return fileSize % blockNum == 0 ? fileSize / blockNum
				: fileSize / blockNum + 1;
	}
	
	/**
	 * 计算已下载的大小
	 * @return
	 */
	private int getDownloadedSize() {
		int size = 0;
		if (mData.size() == mTheadPool.length) {
			// 遍历每条线程已经下载的数据
			Set<Integer> keys = mData.keySet();
			for (Integer key : keys) {
				size += mData.get(key);
			}
			// 打印出已经下载的数据总和
			LogUtils.i(TAG, "已经下载的长度" + size + "个字节");
		}
		return size;
	}

	/**
	 * 从链接中获取文件名
	 * @param conn
	 * @return
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = mUrl.substring(mUrl.lastIndexOf("/") + 1);

		if (null == filename || filename.length() < 1) {// 如果获取不到文件名称
			for (int i = 0;; i++) { // 循环遍历所有头属性
				String mine = conn.getHeaderField(i); // 从返回的流中获取特定索引的头字段值
				if (mine == null)
					break; // 如果遍历到了返回头末尾这退出循环
				if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase(Locale.ENGLISH))) { // 获取content-disposition返回头字段，里面可能会包含文件名
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase(Locale.ENGLISH)); // 使用正则表达式查询文件名
					if (m.find()) {
						return m.group(1); // 如果有符合正则表达规则的字符串
					}
				}
			}
			filename = UUID.randomUUID() + ".tmp";// 由网卡上的标识数字(每个网卡都有唯一的标识号)以及CPU 时钟的唯一数字生成的的一个 16字节的二进制作为文件名
		}
		return filename;
	}

	/**
	 * 获取HTTP响应头字段
	 * 
	 * @param http HttpURLConnection对象
	 * @return 返回头字段的LinkedHashMap
	 */
	private static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>(); // 使用LinkedHashMap保证写入和遍历的时候的顺序相同，而且允许空值存在
		for (int i = 0;; i++) { // 此处为无限循环，因为不知道头字段的数量
			String fieldValue = http.getHeaderField(i); // getHeaderField(int n)用于返回 第n个头字段的值。
			if (fieldValue == null) {
				break; // 如果第i个字段没有值了，则表明头字段部分已经循环完毕，此处使用Break退出循环
			}
			header.put(http.getHeaderFieldKey(i), fieldValue); // getHeaderFieldKey(int n)用于返回第n个头字段的键。
		}
		return header;
	}

	/**
	 * 打印HTTP头字段
	 * @param conn HttpURLConnection对象
	 */
	private static String getResponseHeader(HttpURLConnection conn) {
		Map<String, String> header = getHttpResponseHeader(conn);
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			sb.append(key + entry.getValue());
		}
		return sb.toString();
	}
}
