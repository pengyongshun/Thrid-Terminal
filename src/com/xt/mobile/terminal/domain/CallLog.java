package com.xt.mobile.terminal.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallLog {
	public static int CALL_REFUSE = 0;
	public static int CALL_ACCEPT = 1;
	public static int RECEIVE_CALL_ACCEPT = 2;
	public static int RECEIVE_CALL_REFUSE = 3;
	public static int RECEIVE_CALL_TIMEOUT = 4;

	public CallLog() {
		super();
	}

	public CallLog(int mediaType, String name, int dirType, long time) {
		super();
		this.mediaType = mediaType;
		this.name = name;
		this.dirType = dirType;
		this.time = time;
	}

	/**
	 * 通话的媒体数据类型,0表示音视频，1表示仅仅有音频
	 */
	private int mediaType;
	/**
	 * 通话者的名字
	 */
	private String name;
	/**
	 * 通话的方向，0表示播出对方拒绝，1表示播出对方应答，2表示拨入应答，3表示拨入拒绝, 4表示未接听来电
	 */
	private int dirType;
	/**
	 * 通话的开始时间(非严格时间，取的是通话开始时的时间，或者对方拒绝时的时间，不是点击发出播出操作的时间)
	 */
	private long time;

	// private static SimpleDateFormat mmss = new SimpleDateFormat("mm:ss",
	// Locale.getDefault());

	public String getFormatTime() {
		SimpleDateFormat mmss = new SimpleDateFormat("mm:ss", Locale.getDefault());
		return mmss.format(new Date(time));
	}

	public static String getFormatTime(long time) {
		SimpleDateFormat mmss = new SimpleDateFormat("hh:mm", Locale.getDefault());
		return mmss.format(new Date(time));
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 通话的方向，0表示播出对方拒绝，1表示播出对方应答，2表示呼入应答，3表示呼入拒绝
	 */
	public int getDirType() {
		return dirType;
	}

	public void setDirType(int dirType) {
		this.dirType = dirType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "CallLog [mediaType=" + mediaType + ", name=" + name + ", dirType=" + dirType + ", time=" + time + "]";
	}
}