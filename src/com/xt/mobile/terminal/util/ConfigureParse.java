package com.xt.mobile.terminal.util;

import java.util.ArrayList;

import com.xt.mobile.terminal.domain.SipInfo;

public class ConfigureParse {
	private static ArrayList<SipInfo> allDevices = new ArrayList<SipInfo>();
	private static ArrayList<SipInfo> allUsers = new ArrayList<SipInfo>();

	public static void reset() {
		allUsers.clear();
		allDevices.clear();
	}

	public static void addUserInfo(SipInfo info) {
		ConfigureParse.allUsers.add(info);
	}

	public static void addDeviceInfo(SipInfo info) {
		ConfigureParse.allDevices.add(info);
	}
	
	public static void addUserList(ArrayList<SipInfo> devicesInfos) {
		ConfigureParse.allUsers.addAll(devicesInfos);
	}

	public static void addDeviceList(ArrayList<SipInfo> usersInfos) {
		ConfigureParse.allDevices.addAll(usersInfos);
	}

	public static ArrayList<SipInfo> getAllDevices() {
		return allDevices;
	}

	public static ArrayList<SipInfo> getAllUsers() {
		return allUsers;
	}

	public static ArrayList<SipInfo> getNodeDevices(String nodeId) {

		ArrayList<SipInfo> nodeDevices = new ArrayList<SipInfo>();
		if (nodeId == null) {
			return null;
		}
		int size = allDevices.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (nodeId.equals(allDevices.get(i).getBelongsys())) {
					nodeDevices.add(allDevices.get(i));
				}
			}
		}
		return nodeDevices;
	}

	public static ArrayList<SipInfo> getNodeUsers(String nodeId) {

		ArrayList<SipInfo> nodeUsers = new ArrayList<SipInfo>();
		if (nodeId == null) {
			return null;
		}
		int size = allUsers.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (nodeId.equals(allUsers.get(i).getBelongsys())) {
					nodeUsers.add(allUsers.get(i));
				}
			}
		}
		return nodeUsers;
	}

	public static SipInfo getUserInfoById(String id) {
		SipInfo info = null;
		if (id == null) {
			return null;
		}
		int size = allUsers.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (id.equals(allUsers.get(i).getId())) {
					info = allUsers.get(i);
				}
			}
		}
		return info;
	}

	public static SipInfo getUserInfoByName(String name) {
		SipInfo info = null;
		if (name == null) {
			return null;
		}
		int size = allUsers.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (name.equals(allUsers.get(i).getUserid())) {
					info = allUsers.get(i);
				}
			}
		}
		return info;
	}

	public static SipInfo getDeviceInfoById(String id) {
		SipInfo info = null;
		if (id == null) {
			return null;
		}
		int size = allDevices.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (id.equals(allDevices.get(i).getId())) {
					info = allDevices.get(i);
				}
			}
		}
		return info;
	}

	public static SipInfo getDeviceInfoByName(String name) {
		SipInfo info = null;
		if (name == null) {
			return null;
		}
		int size = allDevices.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (name.equals(allDevices.get(i).getUserid())) {
					info = allDevices.get(i);
				}
			}
		}
		return info;
	}

}