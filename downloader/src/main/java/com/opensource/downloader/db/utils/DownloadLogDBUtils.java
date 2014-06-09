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
 * FileName:DownloadLogDBUtils.java
 * Date：2014-3-19
 * Version：v1.0
 */

package com.opensource.downloader.db.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.opensource.downloader.db.DownloadDBHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * 功能：下载日志数据库操作工具类
 * @author xiaoying
 *
 */
public class DownloadLogDBUtils {
	
	private static final String TABLE_NAME = "download_log";

    private static final String URL = "url";
    private static final String THREAD_ID = "thread_id";
    private static final String DOWNLOADED_SIZE = "downloaded_size";
	
	/**
	 * 保存日志，把某个URL下载的所有线程的下载信息存入表中
	 * @param context
	 * @param url
	 * @param log
	 * @return
	 */
	public static int save(Context context, String url, Map<Integer, Integer> log) {
		SQLiteDatabase db = DownloadDBHelper.getWriteableDatabase(context);
		int count = 0;
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for (Map.Entry<Integer, Integer> entry : log.entrySet()) {
				// 插入特定下载路径特定线程ID已经下载的数据
				values.clear();
				values.put(URL, url);
				values.put(THREAD_ID, entry.getKey());
				values.put(DOWNLOADED_SIZE, entry.getValue());
				db.insert(TABLE_NAME, "", values);
				count++;
			}
			// 设置事务执行的标志为成功
			db.setTransactionSuccessful();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return count;
	}
	
	/**
	 * 根据下载URL清除下载日志（下载完成后）
	 * @param context
	 * @param url
	 * @return
	 */
	public static int delete(Context context, String url) {
		SQLiteDatabase db = DownloadDBHelper.getWriteableDatabase(context);
		int count = 0;
		try {
			db.beginTransaction();
			count = db.delete(TABLE_NAME, URL + " = ?", new String [] {url, });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
		return count;
	}
	
	/**
	 * 根据URL查询该URL下载的日志
	 * @param context
	 * @param url
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Integer> getLogByUrl(Context context, String url) {
		SQLiteDatabase db = DownloadDBHelper.getReadableDatabase(context);
		// 根据下载路径查询所有线程下载数据，返回的Cursor指向第一条记录之前
		Cursor cursor = db.query(TABLE_NAME, null, URL + " = ?",
                new String [] {url, }, null, null, null);
		// 建立一个哈希表用于存放每条线程的已经下载的文件长度
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(THREAD_ID);
				int sizeIndex = cursor.getColumnIndex(DOWNLOADED_SIZE);
				// 从第一条记录开始开始遍历Cursor对象
				do {
					// 把线程id和该线程已下载的长度设置进data哈希表中
					data.put(cursor.getInt(idIndex), cursor.getInt(sizeIndex));
				} while(cursor.moveToNext());
			}
			cursor.close();
		}
		db.close();
		return data;
	}
	
	/**
	 * 根据下载URL和线程ID更新一条下载日志
	 * @param context
	 * @param url
	 * @param threadId
	 * @param downloadedSize
	 */
	public static int update(Context context, String url, int threadId, int downloadedSize) {
		SQLiteDatabase db = DownloadDBHelper.getWriteableDatabase(context);
		int count = 0;
		try {
			db.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(DOWNLOADED_SIZE, downloadedSize);
			count = db.update(TABLE_NAME, values, URL + " = ? AND " + THREAD_ID + " = ?",
                    new String [] {url, String.valueOf(threadId), });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
		return count;
	}
	
}
