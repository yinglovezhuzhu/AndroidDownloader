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
 * FileName:DownloadDBHelper.java
 * Date：2014-3-19
 * Version：v1.0
 */

package com.opensource.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.opensource.downloader.utils.LogUtils;


/**
 * 功能：下载日志数据库
 * @author xiaoying
 *
 */
public class DownloadDBHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "download.db";
	
	private static final int DB_VERSION = 1;
	
	public static DownloadDBHelper mDBHelper = null;
	
	public DownloadDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public static DownloadDBHelper getInstance(Context context) {
		if(mDBHelper == null) {
			mDBHelper = new DownloadDBHelper(context);
		}
		return mDBHelper;
	}
	
	public static SQLiteDatabase getReadableDatabase(Context context) {
		return new DownloadDBHelper(context).getReadableDatabase();
	}
	
	public static SQLiteDatabase getWriteableDatabase(Context context) {
		return new DownloadDBHelper(context).getWritableDatabase();
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//下载进度表，各个线程的进度
		db.execSQL("CREATE TABLE IF NOT EXISTS download_log(_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, thread_id INTEGER, downloaded_size)");
		
		//下载历史
		db.execSQL("CREATE TABLE IF NOT EXISTS download_history(_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, finish_time INTEGER)");
		
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		LogUtils.d("DonwloadDBHelper", "Database has opened ++++++++++++++++>>>>>>>>>>>>");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropAllTables(db);
		onCreate(db);
	}
	
	private void dropAllTables(SQLiteDatabase db) {
		db.execSQL("drop table if exists download_log");
		
		onCreate(db);
	}
}
