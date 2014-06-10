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

package com.opensource.androiddownloader.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.opensource.downloader.DownloadListener;
import com.opensource.downloader.Downloader;
import com.opensource.downloader.utils.LogUtil;

import java.io.File;

public class MainActivity extends Activity {
    private ProgressBar mPb;

    private Button mBtn;

    private TextView mTvMsg;

    private DownloadTask mDownloadTask = null;

    private boolean mIsPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mPb = (ProgressBar) findViewById(R.id.pb_progress);
        mBtn = (Button) findViewById(R.id.btn_button);
        mTvMsg = (TextView) findViewById(R.id.tv_progress);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsPause) {
                    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        download();
                        mBtn.setText(R.string.pause);
                        mIsPause = false;
                    }
                } else {
                    if(mDownloadTask != null) {
                        stop();
                        mBtn.setText(R.string.start);
                        mIsPause = true;
                    }
                }
            }
        });

        mPb.setProgress(0);
        mPb.setMax(100);
    }

    private void stop() {
        if(mDownloadTask != null) {
            mDownloadTask.pause();
            mDownloadTask = null;
        }
    }

    private void download() {
        mDownloadTask = new DownloadTask();
        mDownloadTask.execute("http://www.gzevergrandefc.com/UploadFile/photos/2013-06/fbb77294-6041-41ac-befa-37e237bd41f2.jpg", MainApplication.APP_ROOT);
    }



    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Downloader mmDownloader = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mmDownloader = new Downloader(MainActivity.this, params[0], new File(params[1]), 4);
            DownloadListener downloadListener = new DownloadListener() {
                @Override
                public void onDownloadSize(int totalSize, int downloadedSize) {
                    publishProgress(totalSize, downloadedSize);
                }
            };
            try {
                mmDownloader.download(downloadListener);
                return "下载成功";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            float percent = (float)values[1] / values[0] * 100;
            mPb.setProgress((int) percent);
            mTvMsg.setText(percent + "%");
        }

        public void pause() {
            if(mmDownloader != null) {
                mmDownloader.stop();
                mIsPause = true;
                cancel(true);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null) {
                LogUtil.i("SUCCESS", "下载成功");
                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                mPb.setProgress(0);
                mBtn.setText(R.string.start);
                mTvMsg.setText("");
                mIsPause = true;
            }
        }

    }
}
