
package com.opensource.downloader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class HttpUtils {
	
	public static final String CHARTSET = "UTF-8";
	
	public static final int BUFFER_SIZE = 1024 * 8;

	/**
	 * 获取HTML网页文本内容
	 * @param url HTML的访问URL
	 * @return HTML网页文本内容
	 * @throws org.apache.http.client.ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static StringBuilder getHtml(String url) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse resp = client.execute(get);
		InputStream is = resp.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, CHARTSET));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null) {
			sb.append(line).append(System.getProperty("line.separator"));
		}
		is.close();
		return sb;
	}
	
	/**
	 * 下载文件到目录
	 * @param path 文件网络地址
	 * @param folder 下载目录
	 * @return
	 */
	public static File downloadFile(String path, File folder) {
		File outFile = new File(folder, StringUtils.getFileName(path));
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "*/*"); // 设置客户端可以接受的返回数据类型
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", url.toString());// 设置请求的来源，便于对访问来源进行统计
			conn.setRequestProperty("Charset", "UTF-8");
			// 使用长连接
			conn.setRequestProperty("Connection", "Keep-Alive");
			// 获取远程连接的输入流
			InputStream inStream = conn.getInputStream();
			// 设置本地数据缓存的大小
			byte[] buffer = new byte[BUFFER_SIZE];
			FileOutputStream outStream = new FileOutputStream(outFile);
			int offset = -1;
			while((offset = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, offset);
			}
			outStream.flush();
			inStream.close();
			outStream.close();
			return outFile;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		outFile.deleteOnExit();
		return null;
	}
}
