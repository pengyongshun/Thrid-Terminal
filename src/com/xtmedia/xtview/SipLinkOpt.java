package com.xtmedia.xtview;

public class SipLinkOpt
{
	public int rtp_port; // rtp端口
	public int rtcp_port; // rtcp端口
	public int multicast; // 是否组播
	public int multiplex; // 是否端口复用
	public int multiID; // 端口复用ID
	public String local_rtp_ip = "0.0.0.0"; // 本地绑定IP "0.0.0.0"
	public String multicast_ip = "0.0.0.0"; // 组播IP

	@Override
	public String toString()
	{
		return "SipLinkOpt [rtp_port=" + rtp_port + ", rtcp_port=" + rtcp_port
				+ ", multicast=" + multicast + ", multiplex=" + multiplex
				+ ", multiID=" + multiID + ", local_rtp_ip=" + local_rtp_ip
				+ ", multicast_ip=" + multicast_ip + "]";
	}
}