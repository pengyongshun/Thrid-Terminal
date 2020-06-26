package com.xt.mobile.terminal.sipcapture;

import android.content.Context;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xtmedia.encode.MediaSendInfo;
import com.xtmedia.xtsip.SipNative;

/**
 * 采集sip管理
 * 
 * @author Administrator
 * 
 */
public class CaptureSipManager {

	public static int sipHandle = 0;
	public static SipInfo me;
	public static SipInfo server;
	public static CaptureSipCallback callback;

	public static MediaSendInfo videoMediaSendInfo;
	public static MediaSendInfo audioMediaSendInfo;

	public static long registerHandler;

	/**
	 * @param me
	 *            the me to set
	 */
	public static void setMe(SipInfo me) {
		CaptureSipManager.me = me;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public static void setServer(SipInfo server) {
		CaptureSipManager.server = server;
	}

	/**
	 * 初始化sip协议参数
	 */
	public static boolean sipInit(Context context, String password, CaptureSipCallback callback) {
		// 无网络的情况下直接返回初始化失败(主要是自组网会出现)
		if (me.getIp() == null) {
			return false;
		}
		if (sipHandle != 0) {
			SipNative.XtSipDestroy(CaptureSipManager.sipHandle);
		}
		// 一定要设置的参数
		CaptureSipManager.callback = callback;
		/**
		 * 会话保链时间90s,代表每隔90sinvite的接收方会向发送方发送UPDATE,发送端自动监听该信令,
		 * 如果90s之后发送端没有收到UPDATE就会触发xt_sip_client_invite_terminated_callback_t并且reason为3
		 * ,
		 * 如果接收端发送的UPDATE没有回复会触发xt_sip_server_invite_terminated_callback_t并且reason为0
		 * ,最后一个参数update回复的等待时间,如果超过这个时间没有收到200ok，则判定为会话断开，sip底层自动发送bye
		 */
		PLog.d("=======注册编码========","me.port="+me.getPort()+"me.port="+me.getPort()+
				"me.ip="+me.getIp()+"me.id="+me.getId()+"password="+password+"me.id="+me.getIp());
		sipHandle = SipNative.XtSipCreate(me.getPort(), me.getPort(), me.getIp(), me.getId(),
				password, me.getIp(), 90, callback, 1000, true);// 最新又加入一个参数,是否是纯客户端(不接受register信令)，true的话就是,false表示支持register
//		sipHandle = SipNative.XtSipCreate(5064, 5064, me.getIp(), me.getId(),
//				password, me.getIp(), 90, callback, 1000, true);// 最新又加入一个参数,是否是纯客户端(不接受register信令)，true的话就是,false表示支持register

		return true;
	}

	/**
	 * 注册的时候没有做同步(同一个人可以向同一个服务器注册多次,并且会开启多次保链和传输),所以每次注册前先注销
	 */
	public static void register() {

		final String ids = "sip:" + me.getId() + "@" + server.getIp() + ":" + server.getPort();
		ToolLog.i("sip", "CaptureSipManager不带SDP的注册，服务器的ids:" + ids);
		PLog.d("==========SIP=====", "----------capture_SIP服务信息------->ids:"+ids);
		new Thread() {
			@Override
			public void run() {
				SipNative.XtSipRegister(CaptureSipManager.sipHandle, ids, "", 0, 100, 6000,
						callback);
			}
		}.start();
	}

	public static void register(final String sdp) {

		final String ids = "sip:" + me.getId() + "@" + server.getIp() + ":" + server.getPort();
		ToolLog.i("sip", "CaptureSipManager带SDP的注册，服务器的ids:" + ids);
		new Thread() {
			@Override
			public void run() {

				SipNative.XtSipRegister(CaptureSipManager.sipHandle, ids, sdp, 0, 100, 6000,
						callback);
			}
		}.start();
	}

	/**
	 * 注销和销毁sip
	 */
	public static void unregister() {

		if (registerHandler != 0) {
			SipNative.XtSipRegisterRemoveMyBindings(registerHandler);// 含有contacts域的注销方式
			registerHandler = 0;
		}
		if (CaptureSipManager.sipHandle != 0) {
			SipNative.XtSipDestroy(CaptureSipManager.sipHandle);
			CaptureSipManager.sipHandle = 0;
		}
	}

	public static void acceptInvite(final Session session, final String sdp) {
		ToolLog.i("CaptureSipManager::acceptInvite(session)我回复的sdp:" + sdp);
		if (session != null) {
			if (session.getSessionId() != null) {
				SipNative.XtSipServerInviteProvideAnswer(session.getSipch(), sdp,
						sdp.getBytes().length);
			}
		}
	}

	public static void acceptInvite(final long ch, final String sdp) {
		ToolLog.i("CaptureSipManager::acceptInvite(ch)我回复的sdp:" + sdp);
		SipNative.XtSipServerInviteProvideAnswer(ch, sdp, sdp.getBytes().length);
	}

	public static void reInvite(Session session, final String sdp) {
		SipNative.XtSipClientInviteReinvite(session.getSipch(), sdp, sdp.getBytes().length);
	}

	public static void refuse(final long sessionHandle, final int code) {
		new Thread() {
			@Override
			public void run() {
				ToolLog.i("CaptureSipManager::refuse(拒绝invite)");
				SipNative.XtSipServerInviteReject(sessionHandle, code);
				SipNative.XtSipServerInviteHandleDelete(sessionHandle);
			}
		}.start();
	}

	public static void refuse(final Session session, final int code) {
		new Thread() {
			@Override
			public void run() {
				long inviteHandle = session.getSipch();
				if (inviteHandle != 0) {
					SipNative.XtSipServerInviteReject(inviteHandle, code);
					SipNative.XtSipServerInviteHandleDelete(inviteHandle);
				}
			}
		}.start();
	}

	public static void receiveBye(final Session session) {
		// TODO 完善
		if (session == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				if (session != null) {
					// 实际发送的bye
					long inviteHandle = session.getSipch();
					if (inviteHandle != 0) {
						if (session.getDestId() != null) {
							SipNative.XtSipClientInviteEnd(inviteHandle);
							SipNative.XtSipClientInviteHandleDelete(inviteHandle);
						} else {
							SipNative.XtSipServerInviteEnd(inviteHandle);
							SipNative.XtSipServerInviteHandleDelete(inviteHandle);
						}
					}
				}
			}
		}.start();
	}

	public static void bye(final Session session) {
		ToolLog.i("(capture)挂断会话：" + session);
		new Thread() {
			@Override
			public void run() {
				if (session != null) {
					long cancelHandle = session.getCancleHandle();

					if (session.isFinish()) {
						ToolLog.i("(capture)发送bye");
						long inviteHandle = session.getSipch();
						if (inviteHandle != 0) {
							SipNative.XtSipClientInviteEnd(inviteHandle);
							SipNative.XtSipClientInviteHandleDelete(inviteHandle);
						}
					} else {
						ToolLog.i("(capture)发送cancle");
						if (cancelHandle != 0) {
							SipNative.XtSipMakeCancel(sipHandle, cancelHandle);
						}
					}
				}
			}
		}.start();
	}

}