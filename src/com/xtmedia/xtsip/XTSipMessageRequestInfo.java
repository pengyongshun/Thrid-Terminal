package com.xtmedia.xtsip;

public class XTSipMessageRequestInfo
{
	public String target = ""; // 目标的sip uri值，格式为sip:user@ip[:port]
	public String content_type = ""; // 默认支持application/command+xml，其他类型支持需要在xt_sip_options中设置
	public String content = "";
	public int content_length;
	public String force_target = ""; // 强制目标，即不以request-URI为目的发送
}