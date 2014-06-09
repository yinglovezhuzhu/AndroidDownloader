/*
 * 文件名：StringUtil.java
 * 版权：<版权>
 * 描述：<描述>
 * 创建人：xiaoying
 * 创建时间：2013-6-9
 * 修改人：xiaoying
 * 修改时间：2013-6-9
 * 版本：v1.0
 */

package com.opensource.downloader.utils;

import java.io.File;

/**
 * 功能：
 * @author xiaoying
 *
 */
public class StringUtils {
	
	/**
	 * 字符窜是否为null或者长度为0（去空格）
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	

	/**
	 * 删除所有在startStr和endStr之间的字符串，包括startStr和endStr,即删除闭区间［startStr，endStr］
	 * @param sb
	 * @param startStr
	 * @param endStr
	 */
	public static void deleteAllIn(StringBuilder sb, String startStr, String endStr) {
		int startIndex = 0;
		int endIndex = 0;
		while((startIndex = sb.indexOf(startStr)) >= 0 && (endIndex = sb.indexOf(endStr)) >= 0) {
			sb.delete(startIndex, endIndex + endStr.length());
		}
	}

	/**
	 * 替换所有的字符窜
	 * @param source 源字符窜
	 * @param regular 将要被替换的字符窜
	 * @param replacement 替换成的字符窜
	 * @return 被替换的字符窜个数
	 */
	public static int replaceAll(StringBuilder source, String regular, String replacement) {
		int count = 0;
		int index = -1;
		while((index = source.indexOf(regular, index + 1)) != -1) {
			source.replace(index, index + regular.length(), replacement);
			count++;
		}
		return count;
	}
	
	/**
	 * 获取文件名
	 * @param path 文件路径
	 * @return 文件名称
	 */
	public static String getFileName(String path) {
//		return path.substring(path.lastIndexOf("/") + 1);
		return new File(path).getName();
	}
	
}
