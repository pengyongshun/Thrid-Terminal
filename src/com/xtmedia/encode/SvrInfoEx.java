package com.xtmedia.encode;

public class SvrInfoEx {
	public int trackid; // 流ID
	public String trackName;
	public boolean isAudio; // true/false -- 音频/视频
	public int rtp_send_port; // rtp发送端口
	public String ip; // ip地址
	public String id; // sip用户的id
	public boolean isDemux;
	public int demuxId;

	@Override
	public String toString() {
		return "SvrInfoEx [trackid=" + trackid + ", isAudio=" + isAudio + ", rtp_send_port="
				+ rtp_send_port + ", ip=" + ip + ", id=" + id + ", isDemux=" + isDemux
				+ ", demuxId=" + demuxId + "]";
	}

}