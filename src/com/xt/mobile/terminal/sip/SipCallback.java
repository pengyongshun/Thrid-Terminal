package com.xt.mobile.terminal.sip;

import android.os.Handler;

public class SipCallback extends SipCallbackAdapter {
	private Handler mHandler;
	public static final int ON_SEND_INVITE_FAIL = 80002;
	public static final int ON_PLAY_REMOTE_SUCCESS = 80003;
	public static final int ON_RECEIVE_CALL = 80004;
	// public static final int ON_RECEIVE_CALL_SUCCESS = 80005;
	// public static final int ON_RECEIVE_CALL_FAIL = 80006;
	//
	public static final int ON_REGISTER_STATUS = 80005;
	public static final int ON_PLAY_SUCCESS = 80006;
	public static final int ON_PLAY_FAIL = 80018;
	public static final int ON_RECEIVE_BYE = 80019;
	public static final int ON_RECEIVE_PLAY = 80030;
	// public static final int ON_RECEIVE_BYE_SERVER = 80020;
	public static final int ON_RING_SUCCESS = 80007;
	public static final int ON_CALL_SUCCESS = 80008;
	public static final int ON_RECEIVE_MESSAGE = 80009;
	public static final int ON_RECEIVE_BYE_CALL = 80011;
	public static final int ON_RECEIVE_BYE_PLAY = 80012;
	public static final int ON_RECEIVE_BYE_TEMP_CALL = 80013;

	public static final int ON_PLAY_REMOTE_FAIL = 80015;
	public static final int ON_CALL_FAIL = 80016;

	// public static final int DEVICE_STATUS_CHANGED = 80020;
	public static final int STATUS_CHANGED = 80021;

	public static final int ON_RECEIVE_OPTION = 80026;

	public static final int FORCE_OFFLINE = 80027;

	public SipCallback(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void onRegisterStatus(boolean success) {
		// 注册成功上报绑定关系
		if (success) {
			mHandler.obtainMessage(ON_REGISTER_STATUS, true).sendToTarget();
		} else {
			mHandler.obtainMessage(ON_REGISTER_STATUS, false).sendToTarget();
		}
	}

	@Override
	public void onReceivePlay(String sessionId) {

		mHandler.obtainMessage(ON_RECEIVE_PLAY, sessionId).sendToTarget();
	}
	
	@Override
	public void onPlaySuccess(String sessionId) {

		mHandler.obtainMessage(ON_PLAY_SUCCESS, sessionId).sendToTarget();
	}

	@Override
	public void onPlayFail(String sessionId) {

		mHandler.obtainMessage(ON_PLAY_FAIL, sessionId).sendToTarget();
	}

	@Override
	public void onReceiveBye(String sessionId) {
		
		mHandler.obtainMessage(ON_RECEIVE_BYE, sessionId).sendToTarget();
	}
	
	// @Override
	// public void onCallSuccess(String id) {
	// mHandler.obtainMessage(ON_CALL_SUCCESS, id).sendToTarget();
	// }

	// @Override
	// public void onRingSuccess() {
	// mHandler.sendEmptyMessage(ON_RING_SUCCESS);
	// }
	//
	// @Override
	// public void onCallSuccess() {
	// mHandler.sendEmptyMessage(ON_CALL_SUCCESS);
	// }

	// @Override
	// public void onReceiveCall(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_CALL, id).sendToTarget();
	// }

	// @Override
	// public void onReceiveCallSuccess(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_CALL_SUCCESS, id).sendToTarget();
	// }

	// @Override
	// public void onPlayRemoteSuccess() {
	// mHandler.obtainMessage(ON_PLAY_REMOTE_SUCCESS).sendToTarget();
	// }
	//
	// @Override
	// public void onReceiveCallFail(String reson) {
	// mHandler.obtainMessage(ON_RECEIVE_CALL_FAIL).sendToTarget();
	// }
	//
	// @Override
	// public void onCallFail(String id, String reson) {
	// Bundle data = new Bundle();
	// data.putString("reason", reson);
	// data.putString("id", id);
	// Message msg = mHandler.obtainMessage(ON_CALL_FAIL);
	// msg.setData(data);
	// mHandler.sendMessage(msg);
	// // mHandler.obtainMessage(ON_CALL_FAIL, data).sendToTarget();
	// }

	// @Override
	// public void onPlayRemoteFail(String reson) {
	// mHandler.obtainMessage(ON_PLAY_REMOTE_FAIL, reson).sendToTarget();
	// }

	// @Override
	// public void onReceiveByeCall(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_BYE_CALL, id).sendToTarget();
	// }
	//
	// @Override
	// public void onReceiveByePlay(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_BYE_PLAY, id).sendToTarget();
	// }
	//
	// @Override
	// public void onReceiveByeTempCall(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_BYE_TEMP_CALL, id).sendToTarget();
	// }

	

	// @Override
	// public void onOptionOffline(String id) {
	// mHandler.obtainMessage(ON_RECEIVE_OPTION, id).sendToTarget();
	// }
	//
	// // public static boolean firstNotify = false;
	//
	// @Override
	// public void onReceiveNotify(String notify) {
	// Log.i(XTApplication.LOG, "接收到订阅消息:" + notify);
	// ArrayList<SipInfo> list = SipManager.parseNotifyXml(notify);
	// // if (list.size() > 1)
	// // {
	// // if (firstNotify)
	// // {
	// // return;
	// // }
	// // else
	// // {
	// // firstNotify = true;
	// // }
	// // }
	// for (SipInfo info : list) {
	// String id = info.getId();
	// if (id != null) {
	// if (id.equals(SipManager.me.getId())) {
	// if (info.getStatus() == 0) {
	// // 收到我自己的下线消息,表示服务器强制我下线
	// mHandler.sendEmptyMessage(FORCE_OFFLINE);
	// // String target = SipManager.formatIds(
	// // SipManager.server.getIds(),
	// // SipManager.server.getIp(),
	// // ConstantsValues.SERVER_SIP_PORT);
	// SipManager.stopHeartbeat(SipManager.server.getIds());
	// }
	// } else {
	// SipInfo findOne = ConfigureParse.getUserInfoById(id);
	// if (findOne != null) {
	// Log.i(XTApplication.LOG, "发现关联人员:" + findOne);
	// if (!TextUtils.isEmpty(info.getIp())) {
	// findOne.setIp(info.getIp());
	// }
	// findOne.setBelongsys(info.getBelongsys());
	//
	// findOne.setStatus(info.getStatus());
	// mHandler.obtainMessage(STATUS_CHANGED, id)
	// .sendToTarget();
	//
	// }
	//
	// }
	// }
	// }
	// }
	//
	// @Override
	// public void onReceiveMessage(String message) {
	// mHandler.obtainMessage(ON_RECEIVE_MESSAGE, message).sendToTarget();
	// }
	//
	// @Override
	// public void onReceiveRegister(String ids) {
	//
	// }

}