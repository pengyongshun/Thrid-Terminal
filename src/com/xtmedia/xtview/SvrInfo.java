package com.xtmedia.xtview;

public class SvrInfo
{
	public int trackid;
	public boolean multiplex;
	public int rtp_send_port;
	public int rtcp_send_port;
	public int multid_s;
	public String trackname;

	@Override
	public String toString()
	{
		return "SvrInfo [trackid=" + trackid + ", multiplex=" + multiplex
				+ ", rtp_send_port=" + rtp_send_port + ", rtcp_send_port="
				+ rtcp_send_port + ", multid_s=" + multid_s + ", trackname="
				+ trackname + "]";
	}
}