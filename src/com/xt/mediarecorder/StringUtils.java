package com.xt.mediarecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import com.xt.mobile.terminal.contants.ConstantsValues;

import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 字符串工具类
 * 
 * @author tangjun
 * 
 */
public class StringUtils {
	public static final String EMPTY = "";

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
	/** 用于生成文件 */
	private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
	private static final double KB = 1024.0;
	private static final double MB = 1048576.0;
	private static final double GB = 1073741824.0;
	public static final SimpleDateFormat DATE_FORMAT_PART = new SimpleDateFormat("HH:mm",
			Locale.getDefault());

	public static final int TYPE_FILE_PHOTO = 1;
	public static final int TYPE_FILE_VIDEO = 2;

	/**
	 * 获取同一天的文件序号
	 */
	private static int getFileIndex(int fileType, String nameMattcher) {
		int index = 0;
		String dir = null;
		if (fileType == TYPE_FILE_PHOTO) {
			dir = ConstantsValues.UserPhotoPath;
		} else {
			dir = ConstantsValues.UserVideoPath;
		}
		File parent = new File(dir);
		String[] list = parent.list();
		if (list != null) {
			String date = getDate();
			String mattcher = nameMattcher + date;
			for (String fileName : list) {
				if (fileName.startsWith(mattcher)) {
					int indexOf = fileName.indexOf(".");
					if (indexOf < 0) {
						continue;
					}
					String sub = fileName.subSequence(0, indexOf).toString();
					String[] split = sub.split("_");
					if (split != null) {
						try {
							String indexStr = split[split.length - 1];
							index = Math.max(index, Integer.parseInt(indexStr));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return index + 1;
	}

	/**
	 * 获取一个对一个用户业务产生的文件名(包含路径)
	 * 
	 * @param fileType
	 *            文件类型(是抓拍还是录像)
	 * @param name
	 *            业务的用户中文名
	 * @return 业务产生的文件名(包含路径)
	 */
	public static String getFileName(int fileType, String name) {
		// 防止用户的中文名是空的情况
		String nameMattcher = name + "_";
		if (TextUtils.isEmpty(name)) {
			nameMattcher = "";
		}
		String dir = null;
		String suffix = null;
		if (fileType == TYPE_FILE_PHOTO) {
			dir = ConstantsValues.UserPhotoPath;
			suffix = ConstantsValues.PHOTO_TMP_BMP;
		} else {
			dir = ConstantsValues.UserVideoPath;
			suffix = ConstantsValues.VIDEO_NAME_EXTENSION;
		}
		int index = getFileIndex(fileType, nameMattcher);
		String date = getDate();
		return dir + "/" + nameMattcher + date + "_" + index + suffix;
	}

	public static String formatSubjectTime(long time) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.YEAR, today.get(Calendar.YEAR));
		today.set(Calendar.MONTH, today.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(time);
		if (current.after(today)) {
			// 时间为今天以后的时间,我们不考虑日期错误的情况统一显示HH:mm
			return current.get(Calendar.HOUR_OF_DAY) + ":"
					+ String.format("%02d", current.get(Calendar.MINUTE));
		} else {
			// 时间为今天之前，显示MM-dd
			return current.get(Calendar.MONTH) + "-" + current.get(Calendar.DAY_OF_MONTH);
		}
	}

	public static String currentTimeString() {
		return DATE_FORMAT_PART.format(Calendar.getInstance().getTime());
	}

	public static char chatAt(String pinyin, int index) {
		if (pinyin != null && pinyin.length() > 0)
			return pinyin.charAt(index);
		return ' ';
	}

	/** 获取字符串宽 */
	public static float GetTextWidth(String Sentence, float Size) {
		if (isEmpty(Sentence))
			return 0;
		TextPaint FontPaint = new TextPaint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(Sentence.trim()) + (int) (Size * 0.1); // 鐣欑偣浣欏湴
	}

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
		return format.format(date);
	}

	public static String formatDate(long date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
		return format.format(new Date(date));
	}

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @return 例如2011-3-24
	 */
	public static String formatDate(Date date) {
		return formatDate(date, DEFAULT_DATE_PATTERN);
	}

	public static String formatDate(long date) {
		return formatDate(new Date(date), DEFAULT_DATE_PATTERN);
	}

	/**
	 * 获取当前时间 格式为yyyy-MM-dd 例如2011-07-08
	 * 
	 * @return
	 */
	public static String getDate() {
		return formatDate(new Date(), DEFAULT_DATE_PATTERN);
	}

	/** 生成时间类型的文件名，不含后缀 */
	public static String createFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FILE_PATTERN, Locale.getDefault());
		return format.format(date);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDateTime() {
		return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * 格式化日期时间字符串
	 * 
	 * @param date
	 * @return 例如2011-11-30 16:06:54
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, DEFAULT_DATETIME_PATTERN);
	}

	public static String formatDateTime(long date) {
		return formatDate(new Date(date), DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * 格林威时间转字符串
	 * 
	 * @param gmt
	 * @return
	 */
	public static String formatGMTDate(String gmt) {
		TimeZone timeZoneLondon = TimeZone.getTimeZone(gmt);
		return formatDate(Calendar.getInstance(timeZoneLondon).getTimeInMillis());
	}

	/**
	 * 拼接数组
	 * 
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(final ArrayList<String> array, final String separator) {
		StringBuffer result = new StringBuffer();
		if (array != null && array.size() > 0) {
			for (String str : array) {
				result.append(str);
				result.append(separator);
			}
			result.delete(result.length() - 1, result.length());
		}
		return result.toString();
	}

	public static String join(final Iterator<String> iter, final String separator) {
		StringBuffer result = new StringBuffer();
		if (iter != null) {
			while (iter.hasNext()) {
				String key = iter.next();
				result.append(key);
				result.append(separator);
			}
			if (result.length() > 0)
				result.delete(result.length() - 1, result.length());
		}
		return result.toString();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || str.equalsIgnoreCase("null");
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? EMPTY : str.trim();
	}

	/**
	 * 转换时间显示
	 * 
	 * @param time
	 *            毫秒
	 * @return
	 */
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes,
				seconds) : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
	}

	public static boolean isBlank(String s) {
		return TextUtils.isEmpty(s);
	}

	/** 根据秒获取时间格式 */
	public static String gennerTime(int totalSeconds) {
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
	}

	/**
	 * 转换文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String generateFileSize(long size) {
		String fileSize;
		if (size < KB)
			fileSize = size + "B";
		else if (size < MB)
			fileSize = String.format("%.1f", size / KB) + "KB";
		else if (size < GB)
			fileSize = String.format("%.1f", size / MB) + "MB";
		else
			fileSize = String.format("%.1f", size / GB) + "GB";

		return fileSize;
	}

	/** 查找字符串，找到返回，没找到返回? */
	public static String findString(String search, String start, String end) {
		int start_len = start.length();
		int start_pos = StringUtils.isEmpty(start) ? 0 : search.indexOf(start);
		if (start_pos > -1) {
			int end_pos = StringUtils.isEmpty(end) ? -1 : search
					.indexOf(end, start_pos + start_len);
			if (end_pos > -1)
				return search.substring(start_pos + start.length(), end_pos);
		}
		return "";
	}

	/**
	 * 截取字符��? *
	 * 
	 * @param search
	 *            待搜索的字符��? * @param start 起始字符��?例如��?title>
	 * @param end
	 *            结束字符��?例如��?/title>
	 * @param defaultValue
	 * @return
	 */
	public static String substring(String search, String start, String end, String defaultValue) {
		int start_len = start.length();
		int start_pos = StringUtils.isEmpty(start) ? 0 : search.indexOf(start);
		if (start_pos > -1) {
			int end_pos = StringUtils.isEmpty(end) ? -1 : search
					.indexOf(end, start_pos + start_len);
			if (end_pos > -1)
				return search.substring(start_pos + start.length(), end_pos);
			else
				return search.substring(start_pos + start.length());
		}
		return defaultValue;
	}

	/**
	 * 截取字符��? *
	 * 
	 * @param search
	 *            待搜索的字符��? * @param start 起始字符��?例如��?title>
	 * @param end
	 *            结束字符��?例如��?/title>
	 * @return
	 */
	public static String substring(String search, String start, String end) {
		return substring(search, start, end, "");
	}

	/**
	 * 拼接字符? *
	 * 
	 * @param strs
	 * @return
	 */
	public static String concat(String... strs) {
		StringBuffer result = new StringBuffer();
		if (strs != null) {
			for (String str : strs) {
				if (str != null)
					result.append(str);
			}
		}
		return result.toString();
	}

	/**
	 * Helper function for making null strings safe for comparisons, etc.
	 * 
	 * @return (s == null) ? "" : s;
	 */
	public static String makeSafe(String s) {
		return (s == null) ? "" : s;
	}

	/**
	 * 将int转为高字节在前，低字节在后的byte数组
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] ReverseByte(byte b[]) {
		byte[] newb = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			newb[i] = b[b.length - i];
		}
		return newb;
	}

	/**
	 * 将int转为高字节在前，低字节在后的byte数组
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toHH(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * 将int转为低字节在前，高字节在后的byte数组
	 * 
	 * @param n
	 *            int
	 * @return byte[]
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * 将字节数组转换为String
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String bytesToString(byte[] b) {
		StringBuffer result = new StringBuffer("");
		int length = b.length;
		for (int i = 0; i < length; i++) {
			result.append((char) (b[i] & 0xff));
		}
		return result.toString();
	}

	public static String bytesToString(byte[] b, int length) {
		String nRcvString;
		StringBuffer tStringBuf = new StringBuffer();
		char[] tChars = new char[length];

		for (int i = 0; i < length; i++)
			tChars[i] = (char) b[i];

		tStringBuf.append(tChars);
		nRcvString = tStringBuf.toString();
		return nRcvString;
	}

}