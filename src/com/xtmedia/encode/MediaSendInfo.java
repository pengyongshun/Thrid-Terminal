package com.xtmedia.encode;

public class MediaSendInfo
{
	public boolean isAudio;      // 是否音频
	public int rtp_port;         // rtp端口
	public int rtcp_port;        // rtcp端口
	public String szcodec;       // 编码字符串 :h264,h265,pcmu...
	public String remote_rtp_ip; // ip地址
	public boolean isDemux;      // 是否端口复用
	public int demuxId;          // 端口复用ID
}