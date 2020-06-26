package com.xtmedia.xtsip;

public class XTSipClientPublicationRequestInfo
{
	public String target = ""; // 目标的sip uri值，格式为sip:user@ip[:port]
	public String event_type = "";
	public String content_type = ""; // 默认支持application/command+xml，其他类型支持需要在xt_sip_options中设置
	public String content = "";
	public int content_length;
	public int expiresSeconds;
}