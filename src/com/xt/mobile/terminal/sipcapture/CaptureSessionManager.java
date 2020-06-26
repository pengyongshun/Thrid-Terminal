package com.xt.mobile.terminal.sipcapture;

import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.util.ToolLog;
import com.xtmedia.xtsip.SipNative;

/**
 * 用于采集进程的session管理； 采集和显示在两个进程，他们的数据不同步；
 * */
public class CaptureSessionManager {

	public enum REASON {
		Bad_Request("Bad Request", 400, "不存在的人员"), Forbidden("Forbidden", 403, "正在点播我"), Not_Acceptable(
				"Not Acceptable", 406, "目前无可用设备"), Request_Timeout("Request Timeout", 408, "无应答"), Unsupported_Media_Type(
				"Unsupported Media Type", 415, "设备不支持"), Busy_Here("Busy Here", 486, "忙,请稍后再试"), Not_Acceptable_Here(
				"Not Acceptable Here", 488, "拒绝"), Not_Recvied_ACK("ACK not received", 600,
				"网络连接中断");
		public String name;
		public int code;
		public String reason;

		REASON(String name, int code, String reason) {
			this.name = name;
			this.code = code;
			this.reason = reason;
		}
	}

	private static Session tmpSendSession = null;

	public static void onSaveTmpSession(String sessionId, String sdp, long handle) {
		if (tmpSendSession == null) {
			tmpSendSession = new Session();
		}
		tmpSendSession.setSessionId(sessionId);
		tmpSendSession.setSipch(handle);
		tmpSendSession.setSdp(sdp);
		tmpSendSession.setTime(System.currentTimeMillis());
	}

	public static Session getTmpSession() {
		return tmpSendSession;
	}

	public static boolean isTmpSession(String sessionId) {
		return tmpSendSession != null && sessionId.equals(tmpSendSession.getSessionId());
	}

	public static boolean isTmpSessionIdle() {
		return tmpSendSession == null || tmpSendSession.getSessionId() == null;
	}

	public static void clear() {
		if(tmpSendSession != null) {
            //没有发送bye，是因为平台不让发（严格来说，这里需要发送bye）
			//SipNative.XtSipServerInviteHandleDelete(tmpSendSession.getSipch());
			ToolLog.i("===aaa===", "===================clearTmpSession:" + tmpSendSession.getSipch());
		}
		tmpSendSession = null;		
	}
}