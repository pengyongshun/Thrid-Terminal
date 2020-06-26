package com.xt.mobile.terminal.domain;

import java.util.Arrays;

public class RoleItem {
	public String ResourceID;
	public String CenterID;
	public String RoleID;
	public String IdentityID;
	public String Name;
	public int Type;
	public int Class;
	public String IP;
	public String[] RightIDList;

	@Override
	public String toString() {
		return "RoleItem [ResourceID=" + ResourceID + ", CenterID=" + CenterID + ", RoleID=" + RoleID + ", IdentityID="
				+ IdentityID + ", Name=" + Name + ", Type=" + Type + ", Class=" + Class + ", IP=" + IP
				+ ", RightIDList=" + Arrays.toString(RightIDList) + "]";
	}
}