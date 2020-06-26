package com.xt.mobile.terminal.sip;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.sip.SdpMessage.Media;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.view.XTMediaSource;
import com.xtmedia.encode.MediaSendInfo;
import com.xtmedia.port.SendPort;
import com.xtmedia.xtsip.SipNative;
import com.xtmedia.xtview.GlRenderNative;

public class SessionManager {
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

	// 发送点播的session: sendingSession
	private static Session sendingSession = null;
	// 用于保存会议点播远端session
	private static Hashtable<String, Session> session_map = new Hashtable<String, Session>();

	public static void clearSendingSession() {

		sendingSession = null;
	}

	public static void clearSessionMap() {

		for (Map.Entry<String, Session> entry : session_map.entrySet()) {
			Session session = entry.getValue();
			if (session != null) {
                //没有发送bye，是因为平台不让发（严格来说，这里需要发送bye）
				//SipNative.XtSipServerInviteHandleDelete(session.getSipch());
				ToolLog.i("===aaa===", "===================clearSessionMap:" + session.getSipch());
			}
		}
		session_map.clear();
	}

	public static void clearSingleSession(String sessionId) {

		if (sessionId != null) {
			Session session = session_map.remove(sessionId);
			if (session != null) {
                //没有发送bye，是因为平台不让发（严格来说，这里需要发送bye）
				//SipNative.XtSipServerInviteHandleDelete(session.getSipch());
				ToolLog.i("===aaa===", "===================clearSingleSession:" + session.getSipch());
			}
		}
	}

	public static void sendingPlay(String sessionId, String sdp, long handle) {
		if (sendingSession == null) {
			sendingSession = new Session();
		}
		sendingSession.setSessionId(sessionId);
		sendingSession.setSipch(handle);
		sendingSession.setSdp(sdp);
		sendingSession.setTime(System.currentTimeMillis());
	}

	public static void savePlaySessionMap(String sessionId, String sdp, long handle) {

		Session session = getSessionBySessionId(sessionId);
		if (session == null) {
			Session tmpSession = new Session();
			tmpSession.setSessionId(sessionId);
			tmpSession.setSdp(sdp);
			tmpSession.setSipch(handle);
			tmpSession.setTime(System.currentTimeMillis());
			tmpSession.setFinish(false);
			int []tmpRtpid = getRtpidBySDP(sdp);
			tmpSession.setVideoRtpid(tmpRtpid[0]);
			tmpSession.setAudioRtpid(tmpRtpid[1]);
			session_map.put(sessionId, tmpSession);
		} else {
			session.setSdp(sdp);
			session.setSipch(handle);
			session.setTime(System.currentTimeMillis());
		}
	}

	public static int[] getRtpidBySDP(String sdp) {

		int []rtpid = {0,0};
		if (sdp == null || sdp.length() <= 0 || !sdp.startsWith("v=0")) {
			ToolLog.i("========getRtpidBySDP (sdp == null)");
			return rtpid;
		}

		SdpMessage message = SdpMessage.parseSdp(sdp);
		if (message != null) {
			ArrayList<Media> medias = message.medias;
			for (Media media : medias) {
				if (media.name.equals("video")) {
					rtpid[0] = media.denuxId;
				} else if (media.name.equals("audio")) {
					rtpid[1] = media.denuxId;
				}
			}
		}

		ToolLog.i("========getRtpidBySDP (videoRtipd[" + rtpid[0] + "] audioRtpid[" + rtpid[1] + "])");
		return rtpid;
	}

	public static Session getSendingSession() {
		return sendingSession;
	}

	public static Session getSessionByRtpid(int videoRtpid, int audioRtpid) {

		for (Map.Entry<String, Session> entry : session_map.entrySet()) {
			Session session = entry.getValue();
			if (session != null
					&& (session.getVideoRtpid() == videoRtpid || session.getAudioRtpid() == audioRtpid)) {
				return session;
			}
		}
		return null;
	}

	public static Session getSessionBySessionId(String sessionId) {
		if (sessionId == null || sessionId.length() <= 0) {
			return null;
		}
		return session_map.get(sessionId);
	}

	public static void receivePlay(String userId, boolean video, boolean audio, String sessionId) {

		Session session = getSessionBySessionId(sessionId);
		if (session == null) {
			return ;
		}

		session.setType(Session.PLAY);
		
		//播放SDP与回复SDP都需要先匹配能力集，保证媒体类型相同；但是它们的IP、PORT不同，播放SDP的IP/PORT是对方的，回复SDP的IP/PORT是自己的
		String playSdp = SipManager.getPlaySdp(session.getSdp());
		ToolLog.i("===playSdp: " + playSdp + "\n\n");
	
		XTMediaSource source = SipManager.createMediaSource(session.getType(),
				playSdp);
		SipManager.media_map.put(session.getSessionId(), source);
		session.setDestId(userId);

		String matchSdp = SipManager.matchReceiveSdp(session.getSdp(),
				source.sipClientLinkOpt);

		SipManager.acceptInvite(session, matchSdp);
	}

	public static void receiveCapturePlay(String userId, boolean video, boolean audio,
			Context context) {

		if (video && audio) {
			sendingSession.setType(Session.CALL);
		} else if (video && !audio) {
			sendingSession.setType(Session.RING);
		}
		sendingSession.setDestId(userId);

		String localSdp = SipManager.getLocalInviteSDP();
		String answerSipSdp = SipManager.matchCaptureSdp(sendingSession.getSdp(), localSdp,
				SendPort.send_opt);
		String setTransmitSdp = SipManager.matchCaptureTransmitSdp(sendingSession.getSdp(), localSdp);
		sendingSession.setSdp(setTransmitSdp);

		Intent synchData = new Intent(CaptureVideoService.ACTION_CAPTURE_ACCEPT_INVITE);
		synchData.putExtra("AcceptSDP", answerSipSdp);
		context.sendBroadcast(synchData);
	}

}