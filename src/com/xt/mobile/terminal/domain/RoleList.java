package com.xt.mobile.terminal.domain;

import java.util.ArrayList;
import java.util.List;

public class RoleList {
	public String userName;
	public String pwd;
	public String dsuIp;
	public String dsuPort;
	public String userID;
	// response item
	public String Ret;
	public String Msg;
	public List<RoleItem> Data = new ArrayList<RoleItem>();

	@Override
	public String toString() {
		return "RoleList [userName=" + userName + ", pwd=" + pwd + ", dsuIp=" + dsuIp + ", dsuPort=" + dsuPort
				+ ", userID=" + userID + ", Ret=" + Ret + ", Msg=" + Msg + ", Data=" + Data + "]";
	}
}