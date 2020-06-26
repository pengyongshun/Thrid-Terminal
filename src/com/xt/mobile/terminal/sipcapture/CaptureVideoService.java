package com.xt.mobile.terminal.sipcapture;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToolLog;

public class CaptureVideoService extends Service {

	private Handler captureHandler;
	private ServiceBroadcastReceiver receiver;

	public static final String ACTION_CAPTURE_SIP_INIT = "capture_sip_init";
	public static final String ACTION_CAPTURE_SIP_UNINIT = "capture_sip_uninit";
	public static final String ACTION_CAPTURE_CLEAR_DATA = "action_capture_clear_data";
	public static final String ACTION_CAPTURE_ACCEPT_INVITE = "action_capture_accept_invite";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initData();
	}

	private void initData() {
		initHandler();
		initBroadcastReceiver();
	}

	private void initHandler() {
		captureHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case CaptureSipCallback.ON_REGISTER_STATUS:
					Intent loginSipIntent = new Intent(BaseActivity.ACTION_CAPTURE_SIP);
					if ((Boolean) msg.obj) {
						CaptureSipManager.me.setStatus(1);
						loginSipIntent.putExtra("loginCaptureStatus", true);
					} else {
						loginSipIntent.putExtra("loginCaptureStatus", false);
					}
					sendBroadcast(loginSipIntent);
					break;
				case CaptureSipCallback.ON_RECEIVE_PLAY:
					// 先同步数据。采集在另一个进程，数据不同步
					Intent synchData = new Intent(VideoService.ACTION_SYNCH_SHOW_DATA);
					Session session = CaptureSessionManager.getTmpSession();
					synchData.putExtra("SynchData", session);
					sendBroadcast(synchData);
					break;
				case CaptureSipCallback.ON_PLAY_SUCCESS:
					Intent playSuccess = new Intent(BaseActivity.ACTION_CAPTURE_PLAY_SUCCESS);
					playSuccess.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(playSuccess);
					break;
				case CaptureSipCallback.ON_PLAY_FAIL:
					Intent playFail = new Intent(BaseActivity.ACTION_CAPTURE_PLAY_FAIL);
					playFail.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(playFail);
					break;
				case CaptureSipCallback.ON_RECEIVE_BYE:
					Intent receiveBye = new Intent(BaseActivity.ACTION_CAPTURE_RECEIVE_BYE);
					receiveBye.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(receiveBye);
					break;

				}
			}
		};
	}

	@Override
	public void onDestroy() {

		CaptureSipManager.unregister();
		CaptureSessionManager.clear();

		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initBroadcastReceiver() {
		// 提高Service级别的通知
		receiver = new ServiceBroadcastReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction(ACTION_CAPTURE_SIP_INIT);
		filter.addAction(ACTION_CAPTURE_SIP_UNINIT);
		filter.addAction(ACTION_CAPTURE_CLEAR_DATA);
		filter.addAction(ACTION_CAPTURE_ACCEPT_INVITE);

		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);
	}

	/**
	 * 
	 * @author XT
	 * 
	 */
	private class ServiceBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			ToolLog.i("收到VideoCaptureService的广播action：" + action);
			if (ACTION_CAPTURE_SIP_INIT.equalsIgnoreCase(action)) {
				// 准备初始化
				String encodeId = intent.getStringExtra("EncodeId");
				String encodePwd = intent.getStringExtra("EncodePwd");
				String encodePort = intent.getStringExtra("EncodePort");
				String sipServerIp = intent.getStringExtra("SipServerIp");
				String sipServerId = intent.getStringExtra("SipServerId");
				String sipServerPort = intent.getStringExtra("SipServerPort");
				String localIp = intent.getStringExtra("LocalIp");

//				CaptureSipManager.me = new SipInfo(encodeId, localIp, Integer.valueOf(encodePort),
//						encodeId, "*MobileCapture", sipServerId, SipInfo.TYPE_MOBILE_CAPTURE);
				CaptureSipManager.me = new SipInfo(encodeId, localIp, 5068,
						encodeId, "*MobileCapture", sipServerId, SipInfo.TYPE_MOBILE_CAPTURE);
				CaptureSipManager.server = new SipInfo(sipServerId, sipServerIp,
						Integer.valueOf(sipServerPort), sipServerId, "sip中心", "",
						SipInfo.TYPE_SERVER);
				CaptureSipManager.me.setStatus(1);

				PLog.d("==========SIP=====", "----------capture_SIP服务信息------->me:"+CaptureSipManager.me.toString());
				PLog.d("==========SIP=====", "----------capture_SIP服务信息------->server:"+CaptureSipManager.server.toString());
				ToolLog.i("===CaptureSipManager.me : " + CaptureSipManager.me.toString());
				ToolLog.i("===CaptureSipManager.server : " + CaptureSipManager.server.toString());
				PLog.d("*********开始注册编码***********");
				PLog.d("*********开始注册编码***********");
				PLog.d("me="+CaptureSipManager.me.toString()+",server="+CaptureSipManager.server.toString());
				PLog.d("*********开始注册编码***********");
				PLog.d("*********开始注册编码***********");
				if (CaptureSipManager.sipInit(context, encodePwd, new CaptureSipCallback(
						captureHandler))) {
					PLog.d("*********注册编码完成***********");
					PLog.d("*********注册编码完成***********");
					PLog.d("注册编码完成------create captureSipHandle success");
					PLog.d("*********注册编码完成***********");
					PLog.d("*********注册编码完成***********");
					PLog.d("==========SIP=====", "----------capture_SIP服务信息------->create captureSipHandle success");
					ToolLog.i("========create captureSipHandle success!!!");
					CaptureSipManager.register();
					CaptureSessionManager.clear();
				} else {
					// 初始化就失败了,反馈给界面
					Intent loginSipIntent = new Intent(BaseActivity.ACTION_CAPTURE_SIP);
					loginSipIntent.putExtra("loginCaptureStatus", false);
					sendBroadcast(loginSipIntent);
				}
			} else if (ACTION_CAPTURE_SIP_UNINIT.equals(action)) {
				CaptureSipManager.unregister();
			} else if (ACTION_CAPTURE_CLEAR_DATA.equals(action)) {
				CaptureSessionManager.clear();
			} else if (ACTION_CAPTURE_ACCEPT_INVITE.equals(action)) {
				String sdp = intent.getStringExtra("AcceptSDP");
				long ch = CaptureSessionManager.getTmpSession().getSipch();
				CaptureSipManager.acceptInvite(ch, sdp);
			}
		}
	}
}