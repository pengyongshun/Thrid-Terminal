package com.xt.mobile.terminal.sipcapture;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.util.ToolLog;
import com.xtmedia.xtsip.SipNative;
import com.xtmedia.xtsip.XTSipClientInviteCallBack;
import com.xtmedia.xtsip.XTSipClientMessageCallBack;
import com.xtmedia.xtsip.XTSipClientPublicationCallBack;
import com.xtmedia.xtsip.XTSipClientRegisterCallBack;
import com.xtmedia.xtsip.XTSipClientSubscriptionCallBack;
import com.xtmedia.xtsip.XTSipOptionsCallBack;
import com.xtmedia.xtsip.XTSipServerInviteCallBack;

public abstract class CaptureSipCallbackAdapter implements XTSipClientInviteCallBack,
		XTSipClientMessageCallBack, XTSipClientPublicationCallBack, XTSipClientRegisterCallBack,
		XTSipClientSubscriptionCallBack, XTSipOptionsCallBack, XTSipServerInviteCallBack {

	protected static final String TAG = CaptureSipCallbackAdapter.class.getSimpleName();

	/*
	 * 接收到客户端的注册请求
	 */
	@Override
	public void xt_sip_server_register_request_callback_t(long h, long msg, int operation) {
	}

	/*
	 * 接收到带sdp的呼叫请求
	 */
	@Override
	public void xt_sip_server_invite_offer_callback_t(long h, long msg, String sdp, int len) {
		ToolLog.i("===CaptureSipCallbackAdapter===invite sdp:" + sdp);
		long ch = SipNative.XtSipServerInviteHandleClone(h);
		if (!CaptureSessionManager.isTmpSessionIdle()) {
			ToolLog.i("(capture)之前收到过点播，拒绝此次点播");
			CaptureSipManager.refuse(ch, CaptureSessionManager.REASON.Not_Acceptable_Here.code);
			return;
		}
		String sessionId = SipNative.XtSipMsgGetCallID(msg);
		if (sessionId == null || sessionId.isEmpty()) {
			return;
		}
		ToolLog.i("===CaptureSipCallbackAdapter===invite sessionId:" + sessionId + "; ch" + ch);

		CaptureSessionManager.onSaveTmpSession(sessionId, sdp, ch);
		onCaptureReceivePlay(sessionId);
	}

	/*
	 * 接收到对方的ack确认消息
	 */
	@Override
	public void xt_sip_server_invite_connected_confirmed_callback_t(long h, long msg) {
		ToolLog.i("===CaptureSipCallbackAdapter===ack");
		
		String sessionId = SipNative.XtSipMsgGetCallID(msg);
		if (sessionId == null || sessionId.isEmpty()) {
			return;
		}
		ToolLog.i("===CaptureSipCallbackAdapter===ack sessionId:" + sessionId);
		
		if (!CaptureSessionManager.isTmpSessionIdle() && CaptureSessionManager.isTmpSession(sessionId)) {
			onCapturePlaySuccess(sessionId);
		} else {
			ToolLog.i("===CaptureSipCallbackAdapter===ack unknow!");
		}
	}

	@Override
	public void xt_sip_server_invite_answer_callback_t(long h, long msg, String sdp, int len) {
	}

	@Override
	public void xt_sip_server_invite_terminated_callback_t(long h, long msg, int reason) {

		ToolLog.i("===CaptureSipCallbackAdapter===server_terminated reason：" + reason);

		String sessionId = SipNative.XtSipMsgGetCallID(msg);
		if (sessionId == null || sessionId.isEmpty()) {
			return;
		}
		ToolLog.i("===CaptureSipCallbackAdapter===server_terminated sessionId:" + sessionId);

		if (CaptureSessionManager.isTmpSession(sessionId)) {
			onCaptureReceiveBye(sessionId);
		} else {
			ToolLog.i("===CaptureSipCallbackAdapter===server_terminated unknow!");
		}
	}

	@Override
	public void xt_sip_server_invite_offer_required_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_invite_info_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_invite_info_response_callback_t(long h, long msg, byte success) {
	}

	@Override
	public void xt_sip_server_invite_message_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_invite_message_response_callback_t(long h, long msg, byte success) {
	}

	/**
	 * 收到对方的200ok(不带sdp的invite请求)
	 */
	@Override
	public void xt_sip_client_invite_offer_callback_t(long h, long msg, String sdp, int len) {
		ToolLog.i("(Capture)不带sdp的invite: " + sdp);

	}

	/*
	 * 接收到对方的200ok(带sdp),发送invite成功
	 */
	@Override
	public void xt_sip_client_invite_answer_callback_t(long h, long msg, String sdp, int len) {
		ToolLog.i("(Capture)收到200ok：" + sdp);
	}

	/*
	 * 发送bye取消会话,或者接收到对方拒绝,或者本地断网(我们只关心发送invite失败的情况,不关心发送bye)
	 */
	@Override
	public void xt_sip_client_invite_terminated_callback_t(long h, long msg, int reason) {
		ToolLog.i("(Capture)客户端断开：" + reason);
	}

	// ===============注册回调
	/*
	 * 向服务端发送注册请求之后的反馈
	 */
	@Override
	public void xt_sip_client_register_response_callback_t(long h, long msg, byte success) {
		boolean b = true;
		if (success == 0) {
			b = false;
		}
		ToolLog.i("(Capture)注册回调 ：" + b);
		// 向服务器做心跳检测，只能在回调中调用
		if (b) {
			ConstantsValues.setRETRY_REGISTER(0);
			// SipManager.sipHeartbeat(SipManager.server.getIds(),
			// ConstantsValues.REGISTER_BREAK_TIME);
			CaptureSipManager.registerHandler = SipNative.XtSipClientRegisterHandleClone(h);
		}
		onCaptureRegisterStatus(b);
	}

	/**
	 * 此回调在注销之后在注册会触发
	 */
	@Override
	public void xt_sip_client_register_removed_callback_t(long h, long msg) {
		ToolLog.i("(Capture)注销成功");
		SipNative.XtSipClientRegisterHandleDelete(CaptureSipManager.registerHandler);
	}

	/**
	 * 服务端重启会触发
	 */
	@Override
	public int xt_sip_client_register_request_retry_callback_t(long h, int retrysec, long msg) {
		// 此处返回-1可以让终端不再自动重发register指令，直接返回注册失败(如果是0或正数则无限循环尝试)
		return ConstantsValues.RETRY_REGISTER;
	}

	// ===============heartbeat回调
	@Override
	public int xt_sip_heartbeat_not_pong_callback_t(String target, int count) {

		// String id = SipManager.parseIds(target);
		// Log.e(XTApplication.LOG, "心跳超时--id：" + id + ",次数：" + count);
		//
		// if (count >= 3) {
		//
		// if (id.equals(SipManager.server.getId())) {
		// // option心跳数据超过三次失败
		// SipNative.XtSipHeartbeatRemoveTarget(SipManager.sipHandle, target);
		// // 后台自动重新登录
		// SipManager.register();
		// }
		// }
		return 0;
	}

	// ===============文本消息
	/**
	 * 接收到message消息
	 */
	@Override
	public void xt_sip_server_message_arrived_callback_t(long h, long msg) {
		// SipNative.XtSipServerMessageAccept(h, 200);
		// String message = SipNative.XtSipMsgGetContentBody(msg);
		// Log.e(XTApplication.LOG, "接收到文本消息：" + message);
		// onReceiveMessage(message);
	}

	// 发出message消息的回调(消息是否发出成功)
	@Override
	public void xt_sip_client_message_response_callback_t(long h, long msg, byte success) {
		// if (success == 0) {
		// String message = SipNative.XtSipMsgGetContentBody(msg);
		// Log.e(XTApplication.LOG, "发送文本消息失败：" + message);
		// onReceiveMessage(message);
		// }
	}

	@Override
	public void xt_sip_server_out_of_dialog_receive_request_cb(long h, long msg) {
		if (true) {
			throw new RuntimeException("触发回调了！");
		}
	}

	public abstract void onCaptureRegisterStatus(boolean success);

	public abstract void onCaptureReceivePlay(String sessionId);

	public abstract void onCapturePlaySuccess(String sessionId);

	public abstract void onCapturePlayFail(String sessionId);

	public abstract void onCaptureReceiveBye(String sessionId);

	// public abstract void onReceiveRegister(String ids);

	// public abstract void onRingSuccess();
	//
	// public abstract void onCallSuccess();

	// public abstract void onCallFail(String id, String reson);
	//
	// public abstract void onReceiveCallSuccess(String id);
	//
	// public abstract void onReceiveCallFail(String reson);
	//
	// public abstract void onPlayRemoteSuccess();
	//
	// public abstract void onPlayRemoteFail(String reson);
	//
	// public abstract void onReceiveCall(String id);
	//
	// public abstract void onReceiveByeCall(String id);
	//
	// public abstract void onReceiveByePlay(String id);
	//
	// public abstract void onReceiveByeTempCall(String id);

	// public abstract void onOptionOffline(String id);

	// public abstract void onReceiveNotify(String notify);

	// public abstract void onReceiveMessage(String message);

	// ***************************client_subscription回调*************************
	@Override
	public void xt_sip_client_subscription_update_pending_callback_t(long h, long notify,
			Boolean outOfOrder) {

	}

	/*
	 * 订阅成功之后,每次有上下线设备时收到的服务端发来的notify
	 */
	@Override
	public void xt_sip_client_subscription_update_active_callback_t(long h, long notify,
			int outOfOrder) {
		// SipNative.XtSipClientSubscriptionAccept(h);
		// String notifyContent = SipNative.XtSipMsgGetContentBody(notify);
		// onReceiveNotify(notifyContent);
	}

	@Override
	public void xt_sip_client_subscription_update_extension_callback_t(long h, long notify,
			Boolean outOfOrder) {
	}

	@Override
	public void xt_sip_client_subscription_notify_not_received_callback_t(long h) {
	}

	/*
	 * 客户端取消订阅
	 */
	@Override
	public void xt_sip_client_subscription_terminated_callback_t(long h, long notify) {
	}

	/**
	 * 订阅成功的第一次返回(这个方法用来记录订阅句柄,另外一个方法用来传递notify)
	 */
	@Override
	public void xt_sip_client_subscription_new_subscription_callback_t(long h, long notify) {

	}

	@Override
	public int xt_sip_client_subscription_request_retry_callback_t(long h, int retrysec, long notify) {
		return 0;
	}

	// ***************************作为注册的客户端的多余回调*************************

	/**
	 * 发布信息的回调
	 */
	@Override
	public void xt_sip_client_publication_response_callback_t(long h, long msg, byte success) {
	}

	@Override
	public void xt_sip_client_publication_removed_callback_t(long h, long msg) {
		throw new RuntimeException("(Capture)触发回调了！");
	}

	@Override
	public int xt_sip_client_publication_request_retry_callback_t(long h, int retrysec, long msg) {
		return 0;
	}

	// ************************XTSipClientInviteCallBack客户端invite的多余接口****************************

	/*
	 * 发送invite失败
	 */
	@Override
	public void xt_sip_client_invite_failure_callback_t(long h, long msg) {
	}

	/*
	 * 接收到info
	 */
	@Override
	public void xt_sip_client_invite_info_callback_t(long h, long msg) {
		String info = SipNative.XtSipMsgGetContentBody(msg);
		ToolLog.i("(Capture)接收到info：" + info);
	}

	@Override
	public void xt_sip_client_invite_info_response_callback_t(long h, long msg, byte success) {
	}

	@Override
	public void xt_sip_client_invite_message_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_client_invite_message_response_callback_t(long h, long msg, byte success) {
		throw new RuntimeException("(Capture)触发回调了！");
	}

	@Override
	public void xt_sip_client_msg_prev_post_callback_t(long msg) {

	}

	/**
	 * 以下均为新库增加的接口部分
	 */
	@Override
	public void xt_sip_server_auth_request_credential_callback_t(long h, long msg) {
	}

	// 对请求消息是否进行鉴权,返回值：1-是 0-否（默认情况下，对所有的请求（cancel，ack除外）进行鉴权）
	@Override
	public int xt_sip_server_auth_requires_challenge_callback_t(long msg) {
		return 0;
	}

	// 鉴权时回401/407 返回值:1-407 0-401 （默认情况下，回401）
	@Override
	public int xt_sip_server_auth_proxy_mode_callback_t(long msg) {
		return 0;
	}

	@Override
	public void xt_sip_server_subscription_new_subscription_callback_t(long h, long sub) {
	}

	@Override
	public void xt_sip_server_subscription_new_subscription_from_refer_callback_t(long h, long sub) {
	}

	@Override
	public void xt_sip_server_subscription_refresh_callback_t(long h, long sub) {
	}

	@Override
	public void xt_sip_server_subscription_terminated_callback_t(long h) {
	}

	@Override
	public void xt_sip_server_subscription_ready_to_send_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_subscription_notify_rejected_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_subscription_error_callback_t(long h, long msg) {
	}

	@Override
	public void xt_sip_server_subscription_expired_by_client_callback_t(long h, long sub,
			long notify) {
	}

	@Override
	public void xt_sip_server_subscription_expired_callback_t(long h, long notify) {
	}

	@Override
	public void xt_sip_server_publication_initial_callback_t(long h, long pub, String contents,
			int expires) {
	}

	@Override
	public void xt_sip_server_publication_expired_callback_t(long h) {
	}

	@Override
	public void xt_sip_server_publication_refresh_callback_t(long h, long pub, String contents,
			int expires) {
	}

	@Override
	public void xt_sip_server_publication_update_callback_t(long h, long pub, String contents,
			int expires) {
	}

	@Override
	public void xt_sip_server_publication_removed_callback_t(long h, long pub, int expires) {
	}

	@Override
	public void xt_sip_server_invite_ack_received_callback_t(long h, long msg) {
	}
}