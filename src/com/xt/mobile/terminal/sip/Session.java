package com.xt.mobile.terminal.sip;

import java.io.Serializable;

public class Session implements Serializable, Comparable<Session> {

	private static final long serialVersionUID = 1L;

	public static final int PLAY = 0;
	public static final int RING = 1;
	public static final int CALL = 2;

	// 点播方向
	public static final int PLAY_TARGET = 0;
	public static final int TARGET_PLAY = 1;

	/**
	 * 会话的唯一标识
	 */
	private String sessionId;

	/**
	 * 通话的开始时间
	 */
	private long time;

	/**
	 * sip会话句柄
	 */
	private long sipch = 0;

	/**
	 * cancel会话句柄
	 */
	private long cancleHandle;

	/**
	 * 会话状态
	 */
	private boolean finish;

	private String sdp;
	
	private String destId;

	private int type;

	private int playDirection;

	private int videoRtpid;

	private int audioRtpid;

	private int inMapPosition;

	public int getInMapPosition() {
		return inMapPosition;
	}

	public void setInMapPosition(int inMapPosition) {
		this.inMapPosition = inMapPosition;
	}

	public int getVideoRtpid() {
		return videoRtpid;
	}

	public void setVideoRtpid(int videoRtpid) {
		this.videoRtpid = videoRtpid;
	}

	public int getAudioRtpid() {
		return audioRtpid;
	}

	public void setAudioRtpid(int audioRtpid) {
		this.audioRtpid = audioRtpid;
	}

	public String getSdp() {
		return sdp;
	}

	public void setSdp(String sdp) {
		this.sdp = sdp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCancleHandle() {
		return cancleHandle;
	}

	public void setCancleHandle(long cancleHandle) {
		this.cancleHandle = cancleHandle;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Session) {
			Session s = (Session) o;
			if (sessionId.equals(s.getSessionId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public long getSipch() {
		return sipch;
	}

	public void setSipch(long sipch) {
		this.sipch = sipch;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public String getDestId() {
		return destId;
	}

	public void setDestId(String destId) {
		this.destId = destId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPlayDirection() {
		return playDirection;
	}

	public void setPlayDirection(int direction) {
		this.playDirection = direction;
	}

	@Override
	public int compareTo(Session another) {
		if (another != null) {
			return (int) (time - another.getTime());
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Session [sessionId=" + sessionId + ", time=" + time + ", sdp=" + sdp + ", sipch="
				+ sipch + ", cancleHandle=" + cancleHandle + ", finish=" + finish + "]";
	}
}