package com.xt.mobile.terminal.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.text.TextUtils;

import com.xt.mediarecorder.StringUtils;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xtmedia.xtview.GlRenderNative;

public class XTUtils {
	
	private static long lastClickTime = 0;
	public static long FAST_DU_TIME = 400;

	public static boolean fastClick(long duTime) {
		long now = System.currentTimeMillis();
		long du = now - lastClickTime;
		if (du > duTime) {
			lastClickTime = now;
			return false;
		}
		return true;
	}

	public static boolean fastClick() {
		long now = System.currentTimeMillis();
		long du = now - lastClickTime;
		if (du > FAST_DU_TIME) {
			lastClickTime = now;
			return false;
		}
		ToolLog.i("用户点击频率过快!");
		return true;
	}

	public static String byte2hex(byte[] b) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (int i = 0; i < b.length; i++) {
			tmp = Integer.toHexString(b[i] & 0xFF);
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString();
	}

	public static int isIpv4(String ip) {
		if (TextUtils.isEmpty(ip)) {
			return 1;
		} else if (!isIp(ip)) {
			return 2;
		}
		return 0;
	}
	public static boolean isIp(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	public static int isPort(String port) {
		if (TextUtils.isEmpty(port) || port.trim().equals("")) {
			return 1;
		}
		int portValue = -1;
		try {
			portValue = Integer.valueOf(port);
		}
		catch (NumberFormatException e) {
			return 2;
		}
		if (portValue < 0 || portValue > 65535) {
			return 2;
		}
		return 0;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(ConstantsValues.DEFAULT_SP,
				Context.MODE_PRIVATE);
	}

	// ------------------------Handle BitMap

	/**
	 * 获取压缩比例，采用高宽最小比作为压缩比值
	 * 
	 * @param options 大图片的options
	 * @param width 期望的宽
	 * @param height 期望的高
	 * @return
	 */
	private static int getInSampleSize(BitmapFactory.Options options,
			int width, int height) {
		final float outHeight = options.outHeight;
		final float outWidth = options.outWidth;
		final float reqHeight = height;
		final float reqWidth = width;
		int inSampleSize = 1;
		if (outHeight > height || outWidth > width) {
			final int heightRatio = Math.round(outHeight / reqHeight);
			final int widthRatio = Math.round(outWidth / reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			// 由于options的inSampleSize字段会默认将<=1的值当1使用，在此就不处理ratio为0的情况
		}
		return inSampleSize;
	}

	public static Bitmap compressBitmap(File file, int reqWidth, int reqHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		Bitmap bm;
		opts.inJustDecodeBounds = true;
		bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		// now opts.outWidth and opts.outHeight are the dimension of the
		// bitmap, even though bm is null
		opts.inJustDecodeBounds = false;// this will request the bm
		// scaled down
		opts.inSampleSize = getInSampleSize(opts, reqWidth, reqHeight);
		// bm = BitmapFactory.decodeStream(file, null, opts);
		bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		return bm;
	}

	public static boolean changePictureFormat(File from, File to) {
		if (from == null || to == null) {
			return false;
		}
		if (!to.exists()) {
			try {
				to.createNewFile();
			}
			catch (IOException e) {}
		}
		Bitmap bmp = BitmapFactory.decodeFile(from.getAbsolutePath());
		if (bmp == null) {
			return false;
		}
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(to);
			bmp.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
		finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			catch (IOException e) {}
		}
		if (from.exists()) {
			from.delete();
		}
		if (to.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param videoFile 视频文件
	 * @param reqWidth 期望的缩略图宽度
	 * @param reqHeight 期望的缩略图高度
	 * @param thumbnails 所有视频缩略图集合
	 * @return
	 */
	public static Bitmap getBitmapFromVideo(File videoFile, int reqWidth,
			int reqHeight, Map<String, String> thumbnails) {
		if (videoFile == null || thumbnails == null) {
			return null;
		}
		String savePath = thumbnails.get(videoFile.getAbsolutePath());
		if (thumbnails.containsKey(videoFile.getAbsolutePath())) {// 有现成的缩略图
			return BitmapFactory.decodeFile(savePath);
		}
		try {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(videoFile.getAbsolutePath());
			Bitmap thumbnail = retriever.getFrameAtTime();
			if (thumbnail == null) {
				return null;
			}
			Bitmap bitmap = Bitmap.createScaledBitmap(thumbnail, reqWidth,
					reqHeight, false);
			if (savePath != null) {
				File save = new File(savePath);
				if (!save.exists()) {
					save.createNewFile();
				}
				compressToFile(bitmap, save);
			}
			thumbnail.recycle();
			thumbnail = null;
			return bitmap;
		}
		catch (IllegalArgumentException argumentException) {}
		catch (IOException e) {}
		catch (Exception e) {}
		return null;
	}

	/**
	 * 将bitmap保存到file中
	 * 
	 * @param bitmap
	 * @param file
	 */
	private static void compressToFile(Bitmap bitmap, File file) {
		if (bitmap == null || file == null || !file.exists()) {
			return;
		}
		try {
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(CompressFormat.JPEG, 100, stream);
			stream.flush();
			stream.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
	}

	@SuppressWarnings("deprecation")
	public static long getSDCardTotalSize() {
		String sdCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		StatFs statFs = new StatFs(sdCardPath);
		long blockSize = statFs.getBlockSize();
		long blockCount = statFs.getBlockCount();
		return blockSize * blockCount / 1024 / 1024;// MB
	}

	@SuppressWarnings("deprecation")
	public static long getSDCardAvailableSize() {
		String sdCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		StatFs statFs = new StatFs(sdCardPath);
		long blockSize = statFs.getBlockSize();
		long blockCount = statFs.getAvailableBlocks();
		return blockSize * blockCount / 1024 / 1024;// MB
	}

	public static int onVideoEnable(Context context, Boolean isVideo) {
		int flag = 0;
		if (context == null) {
			return 0;
		}
		try {

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static boolean record(boolean startRecord, int playTask, String name) {
		if (startRecord) {
			// 开始录像
			if (-1 != playTask) {
				String fileName = StringUtils.getFileName(
						StringUtils.TYPE_FILE_VIDEO, name);
				GlRenderNative.StartRecord(playTask, fileName);
				return true;
			}
		} else {
			// 结束录像
			GlRenderNative.StopRecord(playTask);
			return true;
		}
		return false;
	}

	public static void takePhoto(final Context context, final int taskIndex,
			String name) {
		if (-1 != taskIndex) {
			final String fileName = StringUtils.getFileName(
					StringUtils.TYPE_FILE_PHOTO, name);
			new Thread() {
				@Override
				public void run() {
					GlRenderNative.capturepic(taskIndex, fileName);
					File bmp = new File(fileName);
					while (!bmp.exists()) {
						// Log.i(TAG, "BMP还未生成");
					}
					// 生成之后还需要等待一秒，否则解析失败
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					String jpgFile = fileName.replace("bmp", "jpg");
					File jpg = new File(jpgFile);
					XTUtils.changePictureFormat(bmp, jpg);
				}
			}.start();
		}
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				// 至获取无线网卡的ip数据
				if (!"ap0".equals(intf.getDisplayName())
						&& !"usbnet0".equals(intf.getDisplayName())) {
					ToolLog.i("当前的网卡为：" + intf.getDisplayName());
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						// 要确定是否是ipv4的地址(过滤掉ipv6)
						boolean iPv4Address = InetAddressUtils
								.isIPv4Address(inetAddress.getHostAddress());
						if (!inetAddress.isLoopbackAddress() && iPv4Address) {
							ToolLog.i("返回的本地IP地址为："
									+ inetAddress.getHostAddress().toString());

							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		}
		catch (SocketException ex) {}
		return null;
	}

	public static MediaPlayer mMsgPlayer;
	public static MediaPlayer mCallPlayer;
	public static Vibrator mVibrator;

	public static void onMessageSound(Context context, int streamtype) {
		SharedPreferences sp = context.getSharedPreferences(
				ConstantsValues.DEFAULT_SP, Context.MODE_PRIVATE);
		Boolean flag = sp.getBoolean(ConstantsValues.SP_KEY_CallIn_Voice, true);
		if (!flag) {
			return;
		}
		try {
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if (mMsgPlayer == null) {
				mMsgPlayer = new MediaPlayer();
				mMsgPlayer.setDataSource(context, alert);
				mMsgPlayer.setAudioStreamType(streamtype);
				mMsgPlayer.setLooping(false);
				try {
					mMsgPlayer.prepare();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			mMsgPlayer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onCallSound(Context context, int streamtype) {
		SharedPreferences sp = context.getSharedPreferences(
				ConstantsValues.DEFAULT_SP, Context.MODE_PRIVATE);
		Boolean flag = sp.getBoolean(ConstantsValues.SP_KEY_CallIn_Voice, true);
		if (!flag) {
			return;
		}
		try {
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if (mCallPlayer == null) {
				mCallPlayer = new MediaPlayer();
				mCallPlayer.setDataSource(context, alert);
				mCallPlayer.setAudioStreamType(streamtype);
				mCallPlayer.setLooping(false);
				try {
					mCallPlayer.prepare();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			mCallPlayer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onCallVibrator(Context context, Boolean isMessage) {
		SharedPreferences sp = context.getSharedPreferences(
				ConstantsValues.DEFAULT_SP, Context.MODE_PRIVATE);
		Boolean flag = sp.getBoolean(ConstantsValues.SP_KEY_CallIn_Vibrate,
				true);
		if (!flag) {
			return;
		}
		try {
			mVibrator = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
			if (isMessage) {
				long[] pattern = { 100, 500, 100, 500, 100, 500 };
				mVibrator.vibrate(pattern, -1);
			} else {
				long[] pattern = { 100, 30000, 100, 30000, 100, 30000 };
				mVibrator.vibrate(pattern, -1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onReleaseMsgSound() {
		try {
			if (mMsgPlayer != null) {
				mMsgPlayer.stop();
				mMsgPlayer = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onReleaseCallSound() {
		try {
			if (mCallPlayer != null) {
				mCallPlayer.stop();
				mCallPlayer = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onReleaseVibrator() {
		try {
			if (mVibrator != null) {
				mVibrator.cancel();
				mVibrator = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readFile(String fileName) {
		String res = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return null;
			}

			FileInputStream fin = new FileInputStream(fileName);
			byte[] buffer = new byte[fin.available()];
			fin.read(buffer);
			fin.close();
			res = EncodingUtils.getString(buffer, "UTF-8");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String getFileType(File file) {
		String type = "*/*";
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		String endPart = fileName.substring(dotIndex, fileName.length())
				.toLowerCase(Locale.getDefault());
		if (TextUtils.isEmpty(endPart)) {
			return type;
		}
		for (int i = 0; i < mimeMap.length; i++) {
			if (endPart.equals(mimeMap[i][0])) {
				type = mimeMap[i][1];
			}
		}
		return type;
	}

	private final static String[][] mimeMap = {
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "applcation/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "applcation/msword" },
			{ ".docx",
					"applcation/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
			{ ".htm", "text/html" }, { ".html", "text/html" },
			{ ".jar", "applcation/java-archive" }, { ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" }, { ".log", "text/plain" },
			{ ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" },
			{ ".mpeg", "video/mpeg" }, { ".pdf", "application/pdf" },
			{ ".ppt", "text/vnd.ms-powerpoint" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" },

	};
}