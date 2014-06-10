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
 * usage Download log database util
 * @author yinglovezhuzhu@gmail.com
 *
 */
public class DownloadLogDBUtils {
	
	private static final String TABLE_NAME = "download_log";

    private static final String URL = "url";
    private static final String THREAD_ID = "thread_id";
    private static final String DOWNLOADED_SIZE = "downloaded_size";
    private static final String FILE = "file";
	
	/**
	 * Save the log of a file.
	 * @param context
	 * @param url
	 * @param log
	 * @return
	 */
	public static int save(Context context, String url, String file, Map<Integer, Integer> log) {
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
                values.put(FILE, file);
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
	 * Delete the log by url
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
	 * Get the log by url.
	 * @param context
	 * @param url
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Integer> getLogByUrl(Context context, String url) {
		SQLiteDatabase db = DownloadDBHelper.getReadableDatabase(context);
		Cursor cursor = db.query(TABLE_NAME, null, URL + " = ?",
                new String [] {url, }, null, null, null);
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(THREAD_ID);
				int sizeIndex = cursor.getColumnIndex(DOWNLOADED_SIZE);
				do {
					data.put(cursor.getInt(idIndex), cursor.getInt(sizeIndex));
				} while(cursor.moveToNext());
			}
			cursor.close();
		}
		db.close();
		return data;
	}
	
	/**
	 * Update a log record through thread id and url
	 * @param context
	 * @param url
	 * @param threadId
	 * @param downloadedSize
	 */
	public static int update(Context context, String url, String file, int threadId, int downloadedSize) {
		SQLiteDatabase db = DownloadDBHelper.getWriteableDatabase(context);
		int count = 0;
		try {
			db.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(DOWNLOADED_SIZE, downloadedSize);
            values.put(FILE, file);
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
