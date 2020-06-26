package com.xt.mobile.terminal.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.thridpart.XTContants;
import com.xt.mobile.terminal.util.MyActivityManager;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.view.dailog.Dialog_TextView;
import com.xt.mobile.terminal.view.dailog.Dialog_TextView.DialogTextViewCall;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.ui.activity.ActivityCalling;
import com.xt.mobile.terminal.ui.activity.ActivityLogin;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.sip.SessionManager;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.dailog.MyDialog;
import com.xt.mobile.terminal.view.dailog.MyDialog.ButtonClickListener;
import com.xt.mobile.terminal.view.dailog.VDialog;
import com.xtmedia.encode.SendMediaData;

public class BaseActivity extends FragmentActivity implements OnClickListener {

	public static final String WEBSOCKET_CONNECT_SUCCESS = "websocket_connect_success";
	public static final String WEBSOCKET_MESSAGE = "websocket_message";

	public static final String ACTION_LOGIN_SIP = "action_login_sip";// 登录sip中心
	public static final String ACTION_RECEIVE_PLAY = "action_receive_play";// 点播成功
	public static final String ACTION_PLAY_SUCCESS = "action_play_success";// 点播成功
	public static final String ACTION_PLAY_FAIL = "action_play_fail";// 点播失败
	public static final String ACTION_RECEIVE_BYE = "action_receive_bye";// 收到挂断
	
	public static final String ACTION_CAPTURE_SIP = "action_capture_sip";// 登录采集sip
	public static final String ACTION_CAPTURE_RECEIVE_PLAY = "action_capture_receive_play";// 点播成功
	public static final String ACTION_CAPTURE_PLAY_SUCCESS = "action_capture_play_success";// 点播成功
	public static final String ACTION_CAPTURE_PLAY_FAIL = "action_capture_play_fail";// 点播失败
	public static final String ACTION_CAPTURE_RECEIVE_BYE = "action_capture_receive_bye";// 收到挂断

	public static final String ACTION_LOAD_SUCCESS = "action_load_success";// 播放出现第一帧

	public static LinkedList<Activity> activityList = new LinkedList<Activity>();

	int streamtype = AudioManager.STREAM_VOICE_CALL;
	private static final int CALL_DELAY_TIME = 25000;
	private RequestUitl instans;
	private VDialog dialogInstance;

	@Override
	public void onClick(View v) {

	}

	protected SharedPreferences sp;
	protected String mUserId;
	protected String mUserName;
	protected String mTokenKey;
	protected Resources resources;
	private ActivityBroadcastReceiver receiver;
	public Dialog_TextView receiveCallDialog;
	protected boolean isBackground;
	private String mFromName;
	private int mMediaType;
	private String mReceiverId;
	private String mReceiverName;

	private Handler callHandler = new Handler();

	protected void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void startActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MyActivityManager.getActivityManager().pushActivity(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		activityList.addLast(this);
		sp = getSharedPreferences(ConstantsValues.DEFAULT_SP, MODE_PRIVATE);
		mUserId = sp.getString(ConstantsValues.USERID, null);
		mUserName = sp.getString(ConstantsValues.USERNAME, null);
		mTokenKey = sp.getString(ConstantsValues.TOKENKEY, null);
		resources = getResources();
		initBroadcastReceiver();

		dialogInstance = VDialog.getDialogInstance(BaseActivity.this);


	}

	private void initBroadcastReceiver() {

		// 提高Service级别的通知
		receiver = new ActivityBroadcastReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction(ACTION_LOGIN_SIP);		
		filter.addAction(ACTION_RECEIVE_PLAY);
		filter.addAction(ACTION_PLAY_SUCCESS);
		filter.addAction(ACTION_PLAY_FAIL);
		filter.addAction(ACTION_RECEIVE_BYE);
		
		filter.addAction(ACTION_CAPTURE_SIP);
		filter.addAction(ACTION_CAPTURE_RECEIVE_PLAY);
		filter.addAction(ACTION_CAPTURE_PLAY_SUCCESS);
		filter.addAction(ACTION_CAPTURE_PLAY_FAIL);
		filter.addAction(ACTION_CAPTURE_RECEIVE_BYE);

		filter.addAction(WEBSOCKET_CONNECT_SUCCESS);
		filter.addAction(WEBSOCKET_MESSAGE);

		filter.addAction(ACTION_LOAD_SUCCESS);
		filter.addAction(XTContants.ACTION_KX_CHAIRMAN_STOP);

		filter.addAction(XTContants.ACTION_KX_MEMBER_LEAVE_REFUSE);
		registerReceiver(receiver, filter);
	}

	protected void showConfirmDialog(final String message, final String confirm) {

		Dialog_TextView dialog = new Dialog_TextView(this, message, "取消", confirm, dialogCall);
		dialog.show();
	}

	protected void confirmDialog() {

	}

	@Override
	public void finish() {
		super.finish();
		if (!activityList.isEmpty()) {
			if (activityList.getLast() == this) {
				activityList.removeLast();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (dialogInstance != null){
			dialogInstance.hideLoadingDialog();
		}
		super.onDestroy();
		MyActivityManager.getActivityManager().removeActivityFromStack(this);
		callHandler.removeCallbacksAndMessages(null);
		XTUtils.onReleaseMsgSound();
		XTUtils.onReleaseCallSound();
		XTUtils.onReleaseVibrator();
		unregisterReceiver(receiver);
	}



	private class ActivityBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (activityList.isEmpty()) {
				return;
			}
			String action = intent.getAction();
			// if (BaseActivity.this != activityList.getLast()) {
			// return;
			// }
			ToolLog.i(BaseActivity.this.getClass().getSimpleName()
					+ "收到BaseActivity的广播action：" + action);
			PLog.d("BaseActivity","----------onReceive-----" +
					"-->收到BaseActivity的广播action："+ action);

			// 点播端接口
			if (ACTION_LOGIN_SIP.equalsIgnoreCase(action)) {
				boolean loginStatus = intent.getBooleanExtra("loginStatus", false);
				PLog.d("BaseActivity","onReceive：----loginStatus---" +
						"-->"+ action);
				PLog.d("*********注册解码完成***********");
				PLog.d("*********注册解码完成***********");
				PLog.d("loginStatus---->"+loginStatus);
				PLog.d("*********注册解码完成***********");
				PLog.d("*********注册解码完成***********");
				PLog.d("==========SIP=====", "----------SIP服务信息------->loginStatus:"+ loginStatus);
				onLoginStatus(loginStatus);
			} else if (ACTION_RECEIVE_PLAY.equals(action)) {

				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----onReceivePlay---" +
						"-->"+ sessionId);
				onReceivePlay(sessionId);
			} else if (ACTION_PLAY_SUCCESS.equals(action)) {
				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----onPlaySuccess---" +
						"-->"+ sessionId);
				onPlaySuccess(sessionId);
			} else if (ACTION_PLAY_FAIL.equals(action)) {
				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----onPlayFail---" +
						"-->"+ sessionId);
				onPlayFail(sessionId);
			} else if (ACTION_RECEIVE_BYE.equals(action)) {
				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----onReceiveBye---" +
						"-->"+ sessionId);
				onReceiveBye(sessionId);
			} 
			//采集端接口
			else if (ACTION_CAPTURE_SIP.equalsIgnoreCase(action)) {
				boolean loginCaptureStatus = intent.getBooleanExtra("loginCaptureStatus", false);
				PLog.d("BaseActivity","onReceive：----loginCaptureStatus---" +
						"-->"+ loginCaptureStatus);
				PLog.d("*********注册编码完成***********");
				PLog.d("*********注册编码完成***********");
				PLog.d("loginCaptureStatus---->"+loginCaptureStatus);
				PLog.d("*********注册编码完成***********");
				PLog.d("*********注册编码完成***********");
				PLog.d("==========SIP=====", "----------CAPTURE_SIP服务信息------->loginCaptureStatus:"+ loginCaptureStatus);
				onCaptureLoginStatus(loginCaptureStatus);
			} else if (ACTION_CAPTURE_RECEIVE_PLAY.equals(action)) {
				long curTime = System.currentTimeMillis();
				long lastTime = sp.getLong("LastTime_play",0);
				sp.edit().putLong("LastTime_play", curTime).commit();

				long tmpTime = curTime - lastTime;
				if (tmpTime > 50) {
					String sessionId = intent.getStringExtra("SessionId");
					PLog.d("BaseActivity","onReceive：----capture_receive_play---" +
							"-->"+ sessionId);
					onCaptureReceivePlay(sessionId);
				}
			} else if (ACTION_CAPTURE_PLAY_SUCCESS.equals(action)) {
				long curTime = System.currentTimeMillis();
				long lastTime = sp.getLong("LastTime_success",0);
				sp.edit().putLong("LastTime_success", curTime).commit();

				long tmpTime = curTime - lastTime;
				if (tmpTime > 50) {
					String sessionId = intent.getStringExtra("SessionId");
					PLog.d("BaseActivity","onReceive：----capture_play_success---" +
							"-->"+ sessionId);
					onCapturePlaySuccess(sessionId);
				}

			} else if (ACTION_CAPTURE_PLAY_FAIL.equals(action)) {
				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----capture_play_fail---" +
						"-->"+ sessionId);
				onCapturePlayFail(sessionId);
			} else if (ACTION_CAPTURE_RECEIVE_BYE.equals(action)) {
				String sessionId = intent.getStringExtra("SessionId");
				PLog.d("BaseActivity","onReceive：----capture_receive_bye---" +
						"-->"+ sessionId);
				onCaptureReceiveBye(sessionId);
			} 
			//WebSocket接口
			else if (WEBSOCKET_CONNECT_SUCCESS.equals(action)) {
				String ws_msg = intent.getStringExtra("WS_CONNECT");
				PLog.d("BaseActivity","onReceive：----websocket_connect_success---" +
						"-->"+ ws_msg);
				onReceiveWssConnect(ws_msg);
			} else if (WEBSOCKET_MESSAGE.equals(action)) {
				String ws_msg = intent.getStringExtra("WS_MESSAGE");
				PLog.d("BaseActivity","onReceive：----websocket_message---" +
						"-->"+ ws_msg);
				onReceiveWssMessage(ws_msg);
			} 
			//进度条消失接口
			else if (ACTION_LOAD_SUCCESS.equalsIgnoreCase(action)) {
				loadSuccess();
			}

			//通知主席关闭会议
			else if (XTContants.ACTION_KX_CHAIRMAN_STOP.equalsIgnoreCase(action)) {
				String meetingId=intent.getStringExtra(XTContants.APP_BROADCAST_CHAIRMAN_STOP);
				stopChairManMeeting(meetingId);
			}

			//通知成员关闭拒绝和接收的界面
			else if (XTContants.ACTION_KX_MEMBER_LEAVE_REFUSE.equalsIgnoreCase(action)) {
				String meetingId=intent.getStringExtra(XTContants.APP_BROADCAST_MEMBER_LEAVE_REFUSE);
				leaveRefuse(meetingId);
			}
		}
	}

	// =====主席停止会议
	protected void stopChairManMeeting(String meetingId) {

	}

	// =====成员离开拒绝和接收的界面
	protected void leaveRefuse(String meetingId) {

	}

	// =====WebSocket消息
	protected void onReceiveWssConnect(String msg) {
	}

	protected void onReceiveWssMessage(String msg) {

		if (msg.indexOf(WssContant.WSS_INFORM_SHOW_MESSAGE) >= 0 &&
				msg.indexOf(WssContant.WSS_PUBLISH_ACCEPTCALL) >= 0
				&& msg.indexOf(WssContant.WSS_PUBLISH_REFUSECALL) >= 0) {

			onReceiveVideoCall(msg);
		} else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {
			PLog.d("BaseActivity","----------onReceiveWssMessage------->closeTime："+msg);
			ToolLog.i("==========onReceiveWssMessage closeTime");
			try {
				JSONObject obj = new JSONObject(msg);
				JSONObject exObj = obj.getJSONObject("extend");
				String userId = exObj.getString("userID");
				if (userId != null && userId.equals(mUserId)) {

					String bodyStr = obj.getString("body");
					JSONObject bodyObj = new JSONObject(bodyStr);

					JSONObject paramsObj = bodyObj.getJSONObject("params");
					String closeTime = paramsObj.getString("closeTime");
					if (!closeTime.isEmpty() && closeTime.equals("5000")) {
						String text = paramsObj.getString("text");
						showToast(text);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// =====基本方法
	protected void onLoginStatus(boolean loginSuccess) {
	}
	
	protected void onReceiveBye(String sessionId) {
	}

	protected void onReceivePlay(String sessionId) {
	}

	protected void onPlaySuccess(String sessionId) {
	}

	protected void onPlayFail(String sessionId) {
	}

	protected void onCaptureLoginStatus(boolean loginSuccess) {
	}

	protected void onCaptureReceiveBye(String sessionId) {
		Session session = SessionManager.getSendingSession();
		if (sessionId != null && session != null && session.getSessionId().equals(sessionId)) {

			ToolLog.i("===BaseActivity::onCaptureReceiveBye (采集bye)");
			// 销毁转发
			SendMediaData.destroyMediaRouter();
			// 隐藏预览窗口
			Intent hideWin = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
			sendBroadcast(hideWin);

			// 清除采集进程的临时session
			Intent clearData = new Intent(CaptureVideoService.ACTION_CAPTURE_CLEAR_DATA);
			sendBroadcast(clearData);

			SessionManager.clearSendingSession();
			ToolLog.i("===BaseActivity::onCaptureReceiveBye (采集bye)");
		} else {
			ToolLog.i("===BaseActivity::onCaptureReceiveBye (sessionId match failed)");
		}
	}

	protected void onCaptureReceivePlay(String sessionId) {
		Session session = SessionManager.getSendingSession();
		if (sessionId != null && session != null && session.getSessionId().equals(sessionId)) {
			SessionManager.receiveCapturePlay(null, true, true, this);
			ToolLog.i("===BaseActivity::onCaptureReceivePlay (收到采集呼叫)");
		} else {
			ToolLog.i("===BaseActivity::onCaptureReceivePlay (sessionId match failed)");
		}
	}

	protected void onCapturePlaySuccess(String sessionId) {
		Session session = SessionManager.getSendingSession();
		if (sessionId != null && session != null && session.getSessionId().equals(sessionId)) {
			SipManager.startMediaRouter(session.getSdp(), this);
			ToolLog.i("===BaseActivity::onCapturePlaySuccess (播放采集成功)");
		} else {
			ToolLog.i("===BaseActivity::onCapturePlaySuccess (sessionId match failed)");
		}
	}

	protected void onCapturePlayFail(String sessionId) {
		Session session = SessionManager.getSendingSession();
		if (sessionId != null && session != null && session.getSessionId().equals(sessionId)) {

			ToolLog.i("===BaseActivity::onCapturePlayFail (采集失败)");
			// 销毁转发
			SendMediaData.destroyMediaRouter();
			// 隐藏预览窗口
			Intent intent5 = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
			sendBroadcast(intent5);

			// 清除采集进程的临时session
			Intent clearData = new Intent(CaptureVideoService.ACTION_CAPTURE_CLEAR_DATA);
			sendBroadcast(clearData);

			SessionManager.clearSendingSession();
		} else {
			ToolLog.i("===BaseActivity::onCapturePlayFail (sessionId match failed)");
		}
	}

	private void onReceiveVideoCall(String cmd) {

		String content = null;
		try {
			JSONObject obj = new JSONObject(cmd);
			JSONObject exObj = obj.getJSONObject("extend");
			String userId = exObj.getString("userID");
			if (userId != null && userId.equals(mUserId)) {

				String bodyStr = obj.getString("body");
				JSONObject bodyObj = new JSONObject(bodyStr);

				JSONObject paramsObj = bodyObj.getJSONObject("params");
				content = paramsObj.getString("text");

				JSONArray array = paramsObj.getJSONArray("buttons");
				if (array.length() > 0) {
					JSONObject itemObj = array.getJSONObject(0);
					JSONObject cmdObj = itemObj.getJSONObject("command");
					JSONObject params = cmdObj.getJSONObject("params");
					mReceiverId = params.getString("receiverID");
					mReceiverName = params.getString("receiverName");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			content = null;
		}
		if (content != null && mReceiverId != null && mReceiverName != null && !content.isEmpty()
				&& !mReceiverId.isEmpty() && !mReceiverName.isEmpty()) {

			receiveCallDialog = new Dialog_TextView(BaseActivity.this, content, "取消", "接受",
					dialogCall);
			receiveCallDialog.setCanceledOnTouchOutside(false);
			receiveCallDialog.show();

			XTUtils.onCallSound(this, streamtype);
			XTUtils.onCallVibrator(this, false);
			if (callHandler == null) {
				callHandler = new Handler();
			}
			callHandler.removeCallbacksAndMessages(null);
			callHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					XTUtils.onReleaseCallSound();
					XTUtils.onReleaseVibrator();
					receiveCallDialog.dismiss();
				}
			}, CALL_DELAY_TIME);
		} else {
			ToolLog.i("===BaseActivity::onReceiveVideoCall (content == null || mReceiverId == null || mReceiverName == null)");
		}
	}

	private void onReceiveAcceptVideoCall(String cmd) {
	}

	private void onReceiveRefuseVideoCall(String cmd) {
	}

	private void onReceiveCancelVideoCall(String cmd) {

		XTUtils.onReleaseCallSound();
		XTUtils.onReleaseVibrator();
		if (receiveCallDialog != null) {
			receiveCallDialog.dismiss();
		}
	}

	private DialogTextViewCall dialogCall = new DialogTextViewCall() {
		@Override
		public void setResult(String result) {
			
			callHandler.removeCallbacksAndMessages(null);
			XTUtils.onReleaseCallSound();
			XTUtils.onReleaseVibrator();
			
			if (result != null && result.equals("mButton2")) {
				WebSocketCommand.getInstance().onWssAcceptVideoCall(mReceiverId, mReceiverName);
				Intent intent = new Intent(getBaseContext(), ActivityCalling.class);
				intent.putExtra("ReceiverId", mReceiverId);
				intent.putExtra("ReceiverName", mReceiverName);
				intent.putExtra("MediaType", "VideoCall");
				intent.putExtra("IsReceive", true);
				startActivity(intent);
			} else {
				WebSocketCommand.getInstance().onWssRefuseVideoCall(mReceiverId, mReceiverName);
			}
		}
	};

	/**
	 * 播放画面成功出现第一帧数据
	 */
	protected void loadSuccess() {

	}
	
	/**
	 * 被服务器强制下线
	 */
	public void forceOffline() {
		SipManager.me.setStatus(0);
		new MyDialog(this).setMessage("账户在另一终端登录,您已被强制下线")
				.setPositiveButton("知道了", new ButtonClickListener() {
					@Override
					public void onClick(MyDialog dialog, int which) {
						// 跳转到登录界面
						while (activityList.size() > 1) {
							Activity activity = activityList.removeLast();
							activity.finish();
						}
						if (activityList.size() == 1) {
							Intent intent = new Intent(BaseActivity.this, ActivityLogin.class);
							startActivity(intent);
							Activity activity = activityList.removeLast();
							activity.finish();
						}
					}
				}).show();
	}

	public Handler msgHandler = new Handler();

	@Override
	protected void onResume() {
		super.onResume();
		isBackground = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isBackground = true;
	}

	// 设置旋转角度
	public static int getOrientationAngle(int mCameraId) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);
		return info.orientation;
	}

	public void showLoadingDaliog(String mg){
		if (dialogInstance !=null){
			dialogInstance.showLoadingDialog(mg,true);
		}

	}

	public void hideLoadingDialog(){
		if (dialogInstance !=null){
			dialogInstance.hideLoadingDialog();
		}
	}


//	public void sendRequest(List<NameValuePair> paramsList,RequestUitl.HttpResultCall httpResultCall,
//							int requestTag){
//		if (instans ==null){
//
//		}
//		instans = RequestUitl.getInstans(this,httpResultCall);
//
//		instans.sendRequest(paramsList,requestTag);
//	}




}