package com.xt.mobile.terminal.domain;

import java.io.Serializable;

import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.util.ToolLog;

public class SipInfo implements Serializable, Comparable<SipInfo> {

	private static final long serialVersionUID = -2846329922705584409L;

	public static final int TYPE_SERVER = 3;
	public static final int TYPE_MOBILE = 6;
	public static final int TYPE_MOBILE_CAPTURE = 9;
	public static final int TYPE_USE_DIRECTORY = 10000;
	public static final int TYPE_USE_RESOURCE = 10001;
	public static final int TYPE_DEV_DIRECTORY = 20000;
	public static final int TYPE_DEV_RESOURCE = 20001;
	private String id;
	private String ip;
	private int port;
	private int type;
	private String userid;
	private String username;
	private String belongsys;

	private int status;
	private int businesstatus;
	private boolean select;
	private boolean isShow;

	public SipInfo() {
		super();
	}

	public SipInfo(String id, String ip, int port, String userid, String username,
			String belongsys, int type) {
		super();
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.userid = userid;
		this.username = username;
		this.belongsys = belongsys;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBelongsys() {
		return belongsys;
	}

	public void setBelongsys(String belongsys) {
		this.belongsys = belongsys;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public int getBusinesstatus() {
		return businesstatus;
	}

	public void setBusinesstatus(int businesstatus) {
		this.businesstatus = businesstatus;
	}

	public String getIds() {
		// 有中心和没有中心的规则不一样
		String ids = "sip:" + id + "@" + SipManager.server.getIp() + ":"
				+ SipManager.server.getPort();
		ToolLog.i(ids);
		return ids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SipInfo other = (SipInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", ip:" + ip + ", port:" + port + ", userid:" + userid
				+ ", username:" + username + ", belongsys:" + belongsys + ", type:" + type
				+ ", status:" + status + ", select:" + select + ", isShow:" + isShow
				+ ", businesstatus:" + businesstatus+"}";
	}

	@Override
	public int compareTo(SipInfo another) {
		if (another == null) {
			return 1;
		}
		return another.getStatus() - status;
	}
}