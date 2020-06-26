package com.xt.mobile.terminal.view;

import java.util.Arrays;

import android.util.Log;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.sip.Session;
import com.xtmedia.xtview.GlRenderNative;
import com.xtmedia.xtview.SipLinkOpt;
import com.xtmedia.xtview.SvrInfo;

public class XTMediaSource {
	public static final int AUDIO_ENABLE = 2;
	public static final int VIDEO_ENABLE = 1;
	public static final int SESSION_TYPE_RTSP = 0;
	public static final int SESSION_TYPE_SIP = 1;
	public static final int SESSION_TYPE_RTSP_AND_TRANSMIT = 2;
	public static final int SESSION_TYPE_SIP_AND_TRANSMIT = 3;

	// 默认数据类型
	public static final int TYPE_DEFAULT = 172;
	// 编码器拉去数据默认端口
	public static final int PORT_DEFAULT = 19900;

	// 解码接口需要参数
	private long client_handle = -1; // clinet link handle
	private int taskIndex = -1; // 索引号 //通过索引号可以判断当前流的状态
	private String ip; // 转发ip
	private int src_handle = -1; // 转发端口
	private long inChannel; // 点播通道
	private int disableMedia; // 是否显示音频or视频

	private String sdp; // 点播sdp，输入sdp
	private int session_type = SESSION_TYPE_SIP; // 点播类型
	private int actionType; // 媒体会话类型
	public SipLinkOpt[] sipClientLinkOpt; // 收流的参数
	public SvrInfo[] send_opt;// 发流的参数

	public XTMediaSource() {

	}

	public XTMediaSource(int type) {
		session_type = type;
	}

	/*
	 * 初始化本地接收参数(接收端口)
	 */
	public boolean initClientParams(int actionType) {
		this.actionType = actionType;
		if (actionType == Session.RING) {
			sipClientLinkOpt = new SipLinkOpt[] { new SipLinkOpt() };
			if (ConstantsValues.isReport) {
				sipClientLinkOpt[0].multiplex = 1;
			}
		} else {
			sipClientLinkOpt = new SipLinkOpt[] { new SipLinkOpt(), new SipLinkOpt() };
			if (ConstantsValues.isReport) {
				sipClientLinkOpt[0].multiplex = 1;
				sipClientLinkOpt[1].multiplex = 1;
			}
		}
		long[] handle = new long[1];

		boolean result = GlRenderNative.xtMediaClientSipLink(sipClientLinkOpt, handle);
		if (result) {
			this.client_handle = handle[0];
		}
		Log.i("createRecvPort", "XtLog:创建接收端口 recv_opt[0] : " + sipClientLinkOpt[0].toString() + " handle["
				+ handle[0] + "]");
		Log.i("createRecvPort", "XtLog:创建接收端口 recv_opt[1] : " + sipClientLinkOpt[1].toString() + " handle["
				+ handle[0] + "]");
		return result;
	}

	/*
	 * 初始化本地接收参数(发送端口)
	 */
	public boolean initSendPort(int actionType) {
		int[] trackids;
		int[] src_chan = new int[1];
		if (actionType == Session.RING) {
			trackids = new int[] { 1 };
			send_opt = new SvrInfo[] { new SvrInfo() };
		} else {
			trackids = new int[] { 1, 2 };
			send_opt = new SvrInfo[] { new SvrInfo(), new SvrInfo() };
		}

		int res = GlRenderNative.mediaServerCreateSrc(trackids.length, 0, trackids, src_chan);

		if (res == 0) {
			this.src_handle = src_chan[0];
			GlRenderNative.getSvrInfo(src_chan[0], send_opt, ConstantsValues.isReport);
		} else {
			return false;
		}
		return true;
	}

	public long GetClientHandle() {
		return client_handle;
	}

	public void setClient_handle(long client_handle) {
		this.client_handle = client_handle;
	}

	public int GetSrcHandle() {
		return src_handle;
	}

	/*
	 * 断开sip cleint链接，释放端口
	 */
	public void releaseClientPorts() {
		if (client_handle != -1) {
			GlRenderNative.MediaClientCloseLink(client_handle);
			client_handle = -1;
			sipClientLinkOpt = null;
		}
	}

	public boolean stop() {
		boolean success = false;
		if (isValidTaskIndex() && isValidHandle()) {
			GlRenderNative.closesingle(taskIndex, client_handle);// 停解码(销毁接收端口)
			success = true;
		} else {
			releaseClientPorts();
		}
		clear();
		return success;
	}

	public void clone(XTMediaSource ms) {
		session_type = ms.session_type;
		client_handle = ms.client_handle;
		taskIndex = ms.taskIndex;
		ip = ms.ip;
		src_handle = ms.src_handle;
		inChannel = ms.inChannel;
		disableMedia = ms.disableMedia;
		sdp = ms.sdp;
	}

	public void clear() {

		client_handle = -1;
		taskIndex = -1;
		sipClientLinkOpt = null;
	}

	/**
	 * 
	 * @return true 合法;false 不合法
	 */
	public boolean isValidHandle() {
		if (client_handle == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return true 合法;false 不合法
	 */
	public boolean isValidTaskIndex() {
		if (taskIndex < 0) {
			return false;
		}
		return true;
	}

	/**
	 * @return the sdp
	 */
	public String getSdp() {
		return sdp;
	}

	/**
	 * @param sdp
	 *            the sdp to set
	 */
	public void setSdp(String sdp) {
		this.sdp = sdp;
	}

	/**
	 * @return the disableMedia
	 */
	public int getDisableMedia() {
		return disableMedia;
	}

	/**
	 * @param disableMedia
	 *            the disableMedia to set
	 */
	public void setDisableMedia(int disableMedia) {
		this.disableMedia = disableMedia;
	}

	/*
	 * public long getHandle() { return handle; }
	 * 
	 * public void setHandle(long handle) { this.handle = handle; }
	 */
	public int getTaskIndex() {
		return taskIndex;
	}

	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}

	public void setSessionType(int type) {
		this.session_type = type;
	}

	public int getSessionType() {
		return session_type;
	}

	public void setActionType(int type) {
		this.actionType = type;
	}

	public int getActionType() {
		return actionType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setSrcHandle(int src_handle) {
		this.src_handle = src_handle;
	}

	public long getInChannel() {
		return inChannel;
	}

	public void setInChannel(long inChannel) {
		this.inChannel = inChannel;
	}

	@Override
	public String toString() {
		return "XTMediaSource [client_handle=" + client_handle + ", taskIndex=" + taskIndex
				+ ", ip=" + ip + ", src_handle =" + src_handle + ", inChannel=" + inChannel
				+ ", disableMedia=" + disableMedia + ", sdp=" + sdp + ", session_type="
				+ session_type + ", sipClientLinkOpt=" + Arrays.toString(sipClientLinkOpt) + "]";
	}
}