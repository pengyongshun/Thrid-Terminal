package com.xt.mobile.terminal.ui.activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.face.ui.FaceLoginActivity;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;

import com.xt.mobile.terminal.network.pasre.join_metting.ParseInitMobileMedia;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseInitMobileMrdiaBody;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WebSocketUitl;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.ToolKeyBoard;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.ToolPhone;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.view.dailog.LoadingDialog;
import com.xt.mobile.terminal.view.dailog.VDialog;
import com.xtmedia.port.SendPort;

public class ActivityLogin extends BaseActivity implements RequestUitl.HttpResultCall {

	private RelativeLayout mLayoutWindow;
	private EditText et_username;
	private EditText et_password;
	private Button btn_login;
	private Button btn_faceLogin;
	private TextView tv_config;
	private String str_Username;
	private String str_Password;

	private int mFlagResourceFinish = 0;
	private int mFlagLoginFinish = 0;
	private boolean isLoginSuccess = false;
	private int mIsSendHeart = 0;
	private WebSocketUitl webSocketUitl;
	private RequestUitl instans;
	private LoadingDialog mLoadingDlg;
	private String encodePwd="123456";
	private String encodePort="182";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initData();
		initview();
		instans = RequestUitl.getInstans(this,this);
		webSocketUitl = WebSocketUitl.getInstans(this,this);
		//onCreateSdp();
		//ttJson();

		if (isServerCfg()) {
			mUserId = "";
			mUserName = "";
			mTokenKey = "";
			startJWebSClientService();
			//onGetAuthcode();
		}
	}

	private void getDeviceInfo() {
		int size[] = ToolPhone.getInstance(this).getScreenSize();
		ConstantsValues.ScreenWidth = size[0];
		ConstantsValues.ScreenHeight = size[1];

		int sdkVersion = ToolPhone.getInstance(this).getDeviceSDKVersion();
		ConstantsValues.SdkVersion = sdkVersion;

		String model = ToolPhone.getInstance(this).getDeviceModel();
		ConstantsValues.DeviceModel = model;

		String brand = ToolPhone.getInstance(this).getDeviceBrand();
		ConstantsValues.DeviceBrand = brand;

		String display = ToolPhone.getInstance(this).getDeviceDisplay();
		ConstantsValues.DeviceDisplay = display;

		String product = ToolPhone.getInstance(this).getDeviceProduct();
		ConstantsValues.DeviceProduct = product;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mFlagResourceFinish = 0;
		mFlagLoginFinish = 0;
		if (!isLoginSuccess) {
			stopJWebSClientService();
		}
		super.onStop();
	}

	private void initData() {
		// 默认保存账户密码
		str_Username = sp.getString(ConstantsValues.USERID, null);
		str_Password = sp.getString(ConstantsValues.USERPWD, null);
		String core_ip = sp.getString(ConstantsValues.CORE_IP, null);
		String core_port = sp.getString(ConstantsValues.CORE_PORT, null);

		if (core_port == null || core_port.isEmpty()) {
			ConstantsValues.v_CORE_PORT = 433;
		} else {
			ConstantsValues.v_CORE_PORT = Integer.valueOf(core_port);
		}

		mFlagLoginFinish = 0;
		mFlagResourceFinish = 0;
		String captureWidth = sp
				.getString(ConstantsValues.CaptureWidth, "640");
		String captureHeight = sp.getString(ConstantsValues.CaptureHeight,
				"480");
		String captureFrameRate = sp.getString(
				ConstantsValues.CaptureFrameRate, "20");
		String captureBitRate = sp.getString(ConstantsValues.CaptureBitRate,
				"500000");
		String cameraId = sp.getString(ConstantsValues.CaptureCameraId,
				"1");
		if (!TextUtils.isEmpty(captureWidth)) {
			ConstantsValues.vCaptureWidth = Integer.valueOf(captureWidth);
		}
		if (!TextUtils.isEmpty(captureHeight)) {
			ConstantsValues.vCaptureHeight = Integer.valueOf(captureHeight);
		}
		if (!TextUtils.isEmpty(captureFrameRate)) {
			ConstantsValues.vCaptureFrameRate = Integer
					.valueOf(captureFrameRate);
		}
		if (!TextUtils.isEmpty(captureBitRate)) {
			ConstantsValues.vCaptureBitRate = Integer.valueOf(captureBitRate);
		}
		if (!TextUtils.isEmpty(cameraId)) {
			ConstantsValues.vCaptureCameraId = Integer.valueOf(cameraId);
		}
	}

	private void initview() {

		mLayoutWindow = (RelativeLayout) findViewById(R.id.mLayoutWindow);
		mLayoutWindow.setOnClickListener(this);

		btn_login = (Button) findViewById(R.id.btn_login);
		tv_config = (TextView) findViewById(R.id.tv_config);
		btn_login.setOnClickListener(this);
		tv_config.setOnClickListener(this);

		btn_faceLogin = (Button) findViewById(R.id.btn_face_login);
		btn_faceLogin.setOnClickListener(this);
		
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);	
		et_username.setText(str_Username);
		et_password.setText(str_Password);


		if (!TextUtils.isEmpty(str_Password)) {
			et_password.setText(str_Password);
		}
		if (!TextUtils.isEmpty(str_Username)) {
			et_username.setText(str_Username);
		}

		initDaloig();
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.btn_login) {
			ToolLog.i("开始登录");
			startLogin();
		}else if (id == R.id.tv_config){
			Intent intent = new Intent(ActivityLogin.this, ActivityLoginParams.class);
			startActivityForResult(intent, 9);
		}else if (id == R.id.mLayoutWindow){
			ToolKeyBoard.onHideKeyBoard(btn_login);
		}else if (id == R.id.btn_face_login){
			//TODO 人脸识别登陆
			ActivityTools.startActivity(ActivityLogin.this,
					FaceLoginActivity.class,false);
		}

	}

	private void startLogin() {
		str_Username = et_username.getText().toString();
		str_Password = et_password.getText().toString();
		if (TextUtils.isEmpty(str_Username)) {
			showToast("用户名不能为空");
			return;
		}
		if (TextUtils.isEmpty(str_Password)) {
			showToast("密码不能为空");
			return;
		}
		// 检查服务器地址信息
		if (!isServerCfg()) {
			return;
		}
		// 存储已经登陆的账户密码
		Editor edit = sp.edit();
		edit.putString(ConstantsValues.USERID, str_Username);
		edit.putString(ConstantsValues.USERPWD, str_Password);
		edit.commit();

		// 登陆服务器
		mFlagLoginFinish = 0;
		mFlagResourceFinish = 0;
		mUserId = "";
		mUserName = "";
		mTokenKey = "";

		ToolKeyBoard.onHideKeyBoard(btn_login);
		onStartLogin();
		startJWebSClientService();
	}

	private boolean isServerCfg() {
		
		getDeviceInfo();
		
		// 只检查其中的一项是否填写完整(因为必须全部完整才能保存)
		String coreIp = sp.getString(ConstantsValues.CORE_IP, null);
		if (TextUtils.isEmpty(coreIp)) {
			showToast("请配置服务器地址");
			Intent intent = new Intent(ActivityLogin.this, ActivityLoginParams.class);
			startActivityForResult(intent, 9);
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 9) {
			initData();
			startJWebSClientService();
			//onGetAuthcode();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void onMakeUserDirctory(String id) {
		try {
			File file = new File(ConstantsValues.UserVideoPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(ConstantsValues.UserPhotoPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(ConstantsValues.UserFilePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(ConstantsValues.TempFilePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////网络请求////////////////////////

	/**
	 * 获取验证码的请求
	 */
	public void onGetAuthcode() {

		List<NameValuePair> params = MoudleParams.getLoginAuthcodeParams(this);
		if (instans !=null){
			instans.sendRequest(params,true,Constants.HTTP_GET_LOGIN_AUTHCODE);
		}

	}

	/**
	 * 登陆的网络请求
	 */
	public void onStartLogin() {
		//参数
		Map<String,String> map=new HashMap<String,String>();
		map.put("userID",str_Username);
		map.put("password",str_Password);
		map.put("vcode","aaaa");
		List<NameValuePair> params = MoudleParams.getloginParams(this, map);
		//请求
		VDialog.getDialogInstance(ActivityLogin.this).hideLoadingDialog();
		mLoadingDlg.show();
		if (instans !=null){
			instans.sendRequest(params,true,Constants.HTTP_GET_LOGIN_LOGIN);
		}
	}


	private LoadingDialog initDaloig(){
		if (mLoadingDlg ==null){
			mLoadingDlg = new LoadingDialog(this, R.style.showLoadingDialogStyle);
			mLoadingDlg.setDialogLayout(R.layout.loading_tip);
			// 如果上面传递下来是TRUE则不消失框
			mLoadingDlg.setDialogCanceable(true);
			mLoadingDlg.setTipContent("正在登陆中...");
			mLoadingDlg.setCanceledOnTouchOutside(false);
		}else {
			if (mLoadingDlg.isShowing()){
				mLoadingDlg.dismiss();
			}

			mLoadingDlg=null;

			mLoadingDlg = new LoadingDialog(this, R.style.showLoadingDialogStyle);
			mLoadingDlg.setDialogLayout(R.layout.loading_tip);
			// 如果上面传递下来是TRUE则不消失框
			mLoadingDlg.setDialogCanceable(true);
			mLoadingDlg.setTipContent("正在登陆中...");
			mLoadingDlg.setCanceledOnTouchOutside(false);

		}

		return mLoadingDlg;
	}


	///////////////////////////////////////////wss服务启动//////////////////////////
	/**
	 *
	 * 启动wss，且设置url
	 */
	private void startJWebSClientService() {
		if (webSocketUitl !=null){
			webSocketUitl.startJWebSClientService();
		}

	}

	/**
	 * 停止wss
	 */
	private void stopJWebSClientService() {
		if (webSocketUitl !=null){
			webSocketUitl.stopJWebSClientService(mTokenKey);
		}

	}

	///////////////////////////////////////////wss发送请求的地方//////////////////////////

	private void onWssSubscribeResource() {
		// 用户资源指令
		WebSocketCommand.getInstance().onSendSubscribeUser();
		// 设备资源指令
		WebSocketCommand.getInstance().onSendSubscribeDevice();
	}

	////////////////////////////////////////////////////////////////////////////////

//	protected void onLoginStatus(boolean loginSuccess) {
//		ToolLog.i("loginSuccess:" + loginSuccess);
//		if (loginSuccess) {
//			mFlagLoginFinish++;
//			if (mFlagLoginFinish >= 2) {
//				finishLogin();
//			}
//		} else {
//			showToast("登录sip服务失败");
//		}
//	}
//
//	protected void onCaptureLoginStatus(boolean loginSuccess) {
//		ToolLog.i("onCaptureLoginStatus:" + loginSuccess);
//		if (loginSuccess) {
//			mFlagLoginFinish++;
//			if (mFlagLoginFinish >= 2) {
//				finishLogin();
//			}
//		} else {
//			showToast("登录采集sip服务失败");
//		}
//	}


	protected void onLoginStatus(boolean loginSuccess) {
		ToolLog.i("loginSuccess:" + loginSuccess);
		if (loginSuccess) {
			finishLogin();
		} else {
			showToast("登录sip服务失败");
		}
	}

	protected void onCaptureLoginStatus(boolean loginSuccess) {
		ToolLog.i("onCaptureLoginStatus:" + loginSuccess);
		if (loginSuccess) {
			finishLogin();
		} else {
			showToast("登录采集sip服务失败");
		}
	}

	private void startRegister() {
		if (isLoginSuccess){
			String sipServerIp = sp.getString(ConstantsValues.SIP_SERVER_IP, "");
			String sipServerId = sp.getString(ConstantsValues.SIP_SERVER_ID, "");
			String sipServerPort = sp.getString(ConstantsValues.SIP_SERVER_PORT, "");
			String localIp = XTUtils.getLocalIpAddress();
			if (localIp == null || localIp.length() <= 0 || sipServerIp.length() <= 0
					|| sipServerId.length() <= 0 || sipServerPort.length() <= 0) {
				ToolLog.i("==========获取SIP服务信息失败: sipServerIp[" + sipServerIp + "] sipServerPort["
						+ sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
						+ "]");
				onLoginStatus(false);
			} else {
				Intent intent1 = new Intent(VideoService.ACTION_PLAY_SIP_INIT);
				intent1.putExtra("SipServerIp", sipServerIp);
				intent1.putExtra("SipServerId", sipServerId);
				intent1.putExtra("SipServerPort", sipServerPort);
				intent1.putExtra("LocalIp", localIp);
				sendBroadcast(intent1);

				String encodeId = sp.getString(ConstantsValues.ENCODE_ID, "");
				String encodePwd = sp.getString(ConstantsValues.ENCODE_PWD, "");
				String encodePort = sp.getString(ConstantsValues.ENCODE_PORT, "");
				if (encodeId.length() <= 0 || encodePwd.length() <= 0 || encodePort.length() <= 0) {
					ToolLog.i("==========获取采集设备信息失败: encodeId[" + encodeId + "] encodePwd["
							+ encodePwd + "] encodePort[" + encodePort + "]");
					onCaptureLoginStatus(false);
				} else {
					ConstantsValues.v_CAPTURE_SIP_ID = encodeId;
					Intent intent2 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_INIT);
					intent2.putExtra("SipServerIp", sipServerIp);
					intent2.putExtra("SipServerId", sipServerId);
					intent2.putExtra("SipServerPort", sipServerPort);
					intent2.putExtra("LocalIp", localIp);
					intent2.putExtra("EncodeId", encodeId);
					intent2.putExtra("EncodePwd", encodePwd);
					intent2.putExtra("EncodePort", encodePort);
					sendBroadcast(intent2);
					ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
							+ "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
							+ "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
							+ "; product:" + ConstantsValues.DeviceProduct);
				}
			}
		}

	}

//	private void startRegister() {
//
//		mFlagResourceFinish++;
//		if (mFlagResourceFinish >= 2) {
//			String sipServerIp = sp.getString(ConstantsValues.SIP_SERVER_IP, "");
//			String sipServerId = sp.getString(ConstantsValues.SIP_SERVER_ID, "");
//			String sipServerPort = sp.getString(ConstantsValues.SIP_SERVER_PORT, "");
//			String localIp = XTUtils.getLocalIpAddress();
//			if (localIp == null || localIp.length() <= 0 || sipServerIp.length() <= 0
//					|| sipServerId.length() <= 0 || sipServerPort.length() <= 0) {
//				ToolLog.i("==========获取SIP服务信息失败: sipServerIp[" + sipServerIp + "] sipServerPort["
//						+ sipServerPort + "] sipServerId[" + sipServerId + "] localIp" + localIp
//						+ "]");
//				onLoginStatus(false);
//			} else {
//				Intent intent1 = new Intent(VideoService.ACTION_PLAY_SIP_INIT);
//				intent1.putExtra("SipServerIp", sipServerIp);
//				intent1.putExtra("SipServerId", sipServerId);
//				intent1.putExtra("SipServerPort", sipServerPort);
//				intent1.putExtra("LocalIp", localIp);
//				sendBroadcast(intent1);
//
//				String encodeId = sp.getString(ConstantsValues.ENCODE_ID, "");
//				String encodePwd = sp.getString(ConstantsValues.ENCODE_PWD, "");
//				String encodePort = sp.getString(ConstantsValues.ENCODE_PORT, "");
//				if (encodeId.length() <= 0 || encodePwd.length() <= 0 || encodePort.length() <= 0) {
//					ToolLog.i("==========获取采集设备信息失败: encodeId[" + encodeId + "] encodePwd["
//							+ encodePwd + "] encodePort[" + encodePort + "]");
//					onCaptureLoginStatus(false);
//				} else {
//					ConstantsValues.v_CAPTURE_SIP_ID = encodeId;
//					Intent intent2 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_INIT);
//					intent2.putExtra("SipServerIp", sipServerIp);
//					intent2.putExtra("SipServerId", sipServerId);
//					intent2.putExtra("SipServerPort", sipServerPort);
//					intent2.putExtra("LocalIp", localIp);
//					intent2.putExtra("EncodeId", encodeId);
//					intent2.putExtra("EncodePwd", encodePwd);
//					intent2.putExtra("EncodePort", encodePort);
//					sendBroadcast(intent2);
//					ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
//							+ "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
//							+ "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
//							+ "; product:" + ConstantsValues.DeviceProduct);
//				}
//			}
//		}
//	}

	private String localSdp = "";
	private String RemoteSdp = "";

	private void onCreateSdp() {
		
		localSdp = "";
		RemoteSdp = "";
		
		localSdp +="v=0\n";
		localSdp +="o=userName 1 1 IN IP4 172.16.5.6\n";
		localSdp +="s=Play\n";
		localSdp +="i=2?videoCodecType=H.264\n";
		localSdp +="c=IN IP4 172.16.5.6\n";
		localSdp +="b=AS:1024\n";
		localSdp +="t=0 0\n";
		localSdp +="m=video 0 RTP/AVP 96\n";
		localSdp +="c=IN IP4 172.16.5.6\n";
		localSdp +="b=AS:12000\n";
		localSdp +="a=rtpmap:96 H264/90000\n";
		localSdp +="a=fmtp:96 packetization-mode=1;profile-level-id=64001F;sprop-parameter-sets=\n";
		localSdp +="a=rtpport-mux\n";
		localSdp +="a=muxid:5230\n";
		localSdp +="a=control:track1\n";
		localSdp +="a=sendonly\n";
		localSdp +="m=audio 40116 RTP/AVP 100\n";
		localSdp +="c=IN IP4 172.16.5.6\n";
		localSdp +="a=rtpmap:8 PCMA/8000\n";
		localSdp +="a=rtpport-mux\n";
		localSdp +="a=muxid:5231\n";
		localSdp +="a=control:track2\n";
		localSdp +="a=sendonly";
		
		RemoteSdp +="v=0\n";
		RemoteSdp +="o=00010000481 2890844526 2890842807 IN IP4 172.16.5.6\n";
		RemoteSdp +="s=Play\n";
		RemoteSdp +="i=stream\n";
		RemoteSdp +="c=IN IP4 172.16.5.6\n";
		RemoteSdp +="b=AS:0\n";
		RemoteSdp +="t=0 0\n";
		RemoteSdp +="m=audio 30191 RTP/AVP 102 103 104 105 106 107 126 127 100 101 116 117 118 119 120 121 122 123 8 115 109 110 111 112 113 114 124 125 0 108\n";
		RemoteSdp +="c=IN IP4 172.16.100.4\n";
		RemoteSdp +="a=rtpport-mux\n";
		RemoteSdp +="a=muxid:4283\n";
		RemoteSdp +="a=fmtp:100 bitrate=16000\n";
		RemoteSdp +="a=control:track2\n";
		RemoteSdp +="a=rtpid:4283\n";
		RemoteSdp +="a=rtpmap:8 PCMA/8000\n";
		RemoteSdp +="a=rtpmap:115 PCMA/8000/2\n";
		RemoteSdp +="a=rtpmap:116 PCMA/16000\n";
		RemoteSdp +="a=rtpmap:117 PCMA/16000/2\n";
		RemoteSdp +="a=rtpmap:118 PCMA/32000\n";
		RemoteSdp +="a=rtpmap:119 PCMA/32000/2\n";
		RemoteSdp +="a=rtpmap:120 PCMA/44100\n";
		RemoteSdp +="a=rtpmap:121 PCMA/44100/2\n";
		RemoteSdp +="a=rtpmap:122 PCMA/48000\n";
		RemoteSdp +="a=rtpmap:123 PCMA/48000/2\n";
		RemoteSdp +="a=rtpmap:0 PCMU/8000\n";
		RemoteSdp +="a=rtpmap:108 PCMU/8000/2\n";
		RemoteSdp +="a=rtpmap:109 PCMU/16000\n";
		RemoteSdp +="a=rtpmap:110 PCMU/16000/2\n";
		RemoteSdp +="a=rtpmap:111 PCMU/32000\n";
		RemoteSdp +="a=rtpmap:112 PCMU/32000/2\n";
		RemoteSdp +="a=rtpmap:113 PCMU/44100\n";
		RemoteSdp +="a=rtpmap:114 PCMU/44100/2\n";
		RemoteSdp +="a=rtpmap:124 PCMU/48000\n";
		RemoteSdp +="a=rtpmap:125 PCMU/48000/2\n";
		RemoteSdp +="a=rtpmap:100 MPEG4-GENERIC/8000\n";
		RemoteSdp +="a=rtpmap:101 MPEG4-GENERIC/8000/2\n";
		RemoteSdp +="a=rtpmap:102 MPEG4-GENERIC/16000\n";
		RemoteSdp +="a=rtpmap:103 MPEG4-GENERIC/16000/2\n";
		RemoteSdp +="a=rtpmap:104 MPEG4-GENERIC/32000\n";
		RemoteSdp +="a=rtpmap:105 MPEG4-GENERIC/32000/2\n";
		RemoteSdp +="a=rtpmap:106 MPEG4-GENERIC/44100\n";
		RemoteSdp +="a=rtpmap:107 MPEG4-GENERIC/44100/2\n";
		RemoteSdp +="a=rtpmap:126 MPEG4-GENERIC/48000\n";
		RemoteSdp +="a=rtpmap:127 MPEG4-GENERIC/48000/2\n";
		RemoteSdp +="a=recvonly\n";
		RemoteSdp +="m=video 30191 RTP/AVP 98 100\n";
		RemoteSdp +="c=IN IP4 172.16.100.4\n";
		RemoteSdp +="a=rtpport-mux\n";
		RemoteSdp +="a=muxid:4284\n";
		RemoteSdp +="a=fmtp:98 profile-level-id=640020;max-mbps=243000;max-fs=8100\n";
		RemoteSdp +="a=control:track1\n";
		RemoteSdp +="a=rtcp-fb:* ccm fir\n";
		RemoteSdp +="a=rtpid:4284\n";
		RemoteSdp +="a=rtpmap:98 H264/90000\n";
		RemoteSdp +="a=rtpmap:100 H265/90000\n";
		RemoteSdp +="a=recvonly";
		
		ToolLog.i("===localSdp : " + localSdp + "\n");
		ToolLog.i("===RemoteSdp : " + RemoteSdp + "\n");

		ConstantsValues.v_CAPTURE_SIP_ID = "00100000010";
		SipManager.me = new SipInfo("00100000010", "172.16.5.231", 5064, "user01",
				"user01", "10001", SipInfo.TYPE_MOBILE);
		SendPort.createSendPort();
		String matchSdp = SipManager.matchCaptureSdp(RemoteSdp, localSdp,
				SendPort.send_opt);
		ToolLog.i("===matchSdp : " + matchSdp + "\n");

		String matchSdp1 = SipManager.matchCaptureTransmitSdp(RemoteSdp, localSdp);
		ToolLog.i("===matchSdp1 : " + matchSdp1 + "\n");
		
	}

/////////////////////////////////////////////网络请求接收结果的地方///////////////////////////////
	@Override
	public void success(int tag, String result) {
		switch (tag){
			//获取登陆验证码
			case Constants.HTTP_GET_LOGIN_AUTHCODE:

				break;
			//获取登陆的信息
			case Constants.HTTP_GET_LOGIN_LOGIN:
				//消除加载框
				try {
					ToolLog.i("=========createUserTokenForWeb====" + result);

					JSONObject jobj = new JSONObject(result);
					String State = jobj.getString("responseCode");
					if (State != null && !State.isEmpty() && State.equals("1")) {
						JSONObject obj = jobj.getJSONObject("data");
						String userId = obj.getString("userID");
						String userName = obj.getString("userName");
						String token = obj.getString("tokenKey");
						// String ipAddress = obj.getString("ipAddress");
						String validTime = obj.getString("validTime");

						Editor edit = sp.edit();
						edit.putString(ConstantsValues.USERID, userId);
						edit.putString(ConstantsValues.USERNAME, userName);
						edit.putString(ConstantsValues.TOKENKEY, token);
						edit.putString(ConstantsValues.VALIDTIME, validTime);
						edit.commit();

						mUserId = userId;
						mUserName = userName;
						mTokenKey = token;
						isLoginSuccess=true;
						WebSocketCommand.getInstance().setmUserId(userId);
						WebSocketCommand.getInstance().setmUserName(userName);
						WebSocketCommand.getInstance().setmTokenKey(token);

						WebSocketCommand.getInstance().onSendHeart();
						WebSocketCommand.getInstance().onSendJoin();
						WebSocketCommand.getInstance().onSendAddUserStatus();
						WebSocketCommand.getInstance().onSendInitMobileTerminal();
//						WebSocketCommand.getInstance().onSendInitMedia();
//						if (mIsSendHeart == 2) {
//							WebSocketCommand.getInstance().onSendHeart();
//							WebSocketCommand.getInstance().onSendJoin();
//							WebSocketCommand.getInstance().onSendAddUserStatus();
//							WebSocketCommand.getInstance().onSendInitMedia();
//						}
						ToolLog.i("=========createUserTokenForWeb====mIsSendHeart: " + mIsSendHeart);
						ToolLog.i("===aaa===ScreenSize:" + ConstantsValues.ScreenWidth + "-" + ConstantsValues.ScreenHeight
								+ "; sdkVersion:" + ConstantsValues.SdkVersion + "; DeviceModel:" + ConstantsValues.DeviceModel
								+ "; DeviceBrand:" + ConstantsValues.DeviceBrand + "; display:" + ConstantsValues.DeviceDisplay
								+ "; product:" + ConstantsValues.DeviceProduct);
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("用户登录失败");
				}
				break;
			//退出登陆
			case Constants.HTTP_GET_LOGIN_DESTROYTOKEN:
				break;
		}
	}

	@Override
	public void faile(int tag, String error) {
		switch (tag){
			//获取登陆的信息
			case Constants.HTTP_GET_LOGIN_LOGIN:
				//消除加载框
				if (mLoadingDlg.isShowing()){
					mLoadingDlg.dismiss();
				}
				showToast("用户登录失败");
				break;
			//退出登陆
			case Constants.HTTP_GET_LOGIN_DESTROYTOKEN:
				break;
		}



	}

	//////////////////////////////////////////////wss返回结果的地方///////////////////////

	/**
	 * Wss请求连接成功与否的地方
	 * @param msg
	 */
	protected void onReceiveWssConnect(String msg) {

		if (msg.isEmpty()) {
			return;
		} else if (msg.equals("websocket_connected")) {
			// 心跳指令
			hideLoadingDialog();
			if (mUserId != null && mUserName != null && !mUserId.isEmpty() && !mUserName.isEmpty()) {
				if (isLoginSuccess){
					WebSocketCommand.getInstance().onSendHeart();
					WebSocketCommand.getInstance().onSendJoin();
					WebSocketCommand.getInstance().onSendAddUserStatus();
					//初始化媒体服务
//				WebSocketCommand.getInstance().onSendInitMedia();
					//初始化移动端媒体
					WebSocketCommand.getInstance().onSendInitMobileTerminal();
				}
				//mIsSendHeart = 1;
			} else {
				//mIsSendHeart = 2;
			}
			ToolLog.i("=========websocket_connected====mIsSendHeart: " + mIsSendHeart);
		}
	}

//
//	/**
//	 * wss请求返回的结果
//	 * @param msg
//	 */
//	protected void onReceiveWssMessage(String msg) {
//
//		if (msg.isEmpty()) {
//			return;
//		} else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {
//
//			ToolLog.i("==========onReceiveWssMessage closeTime");
//
//		} else if (msg.indexOf(WssContant.WSS_INFORM_INIT_MEDIA) >= 0) {
//			//初始化媒体服务（即获取SIP消息）
//			//在webSocketCommand中onSendInitMedia这个方法
//			// 在webSocketCommand中obj.put("funName", "publishInitMediaByCustom");
//			try {
//				JSONObject obj = new JSONObject(msg);
//				JSONObject exObj = obj.getJSONObject("extend");
//				String userId = exObj.getString("userID");
//				if (userId != null && userId.equals(str_Username)) {
//
//					String bodyStr = obj.getString("body");
//					JSONObject bodyObj = new JSONObject(bodyStr);
//
//					JSONObject paramsObj = bodyObj.getJSONObject("params");
//					String sipId = paramsObj.getString("SIPID");
//					String sipPwd = paramsObj.getString("clientPassword");
//					String sipServerIp = paramsObj.getString("serverIP");
//					String sipServerPort = paramsObj.getString("serverPort");
//					String sipServerId = paramsObj.getString("serverID");
//					ConstantsValues.v_SIP_SERVER_PORT = Integer.valueOf(sipServerPort);
//
//					Editor edit = sp.edit();
//					edit.putString(ConstantsValues.SIP_ID, sipId);
//					edit.putString(ConstantsValues.SIP_PWD, sipPwd);
//					edit.putString(ConstantsValues.SIP_SERVER_IP, sipServerIp);
//					edit.putString(ConstantsValues.SIP_SERVER_PORT, sipServerPort);
//					edit.putString(ConstantsValues.SIP_SERVER_ID, sipServerId);
//					edit.commit();
//
//					ToolLog.i("==========sipInfo: sipId[" + sipId + "] sipPwd[" + sipPwd
//							+ "] sipServerIp[" + sipServerIp + "] sipServerPort[" + sipServerPort
//							+ "] sipServerId[" + sipServerId + "]");
//
//					onWssSubscribeResource();
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0 ||
//				msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0) {
//			// 用户资源指令 OrganizationUser
//			// 设备资源指令 OrganizationDevice
//			//备注：OrganizationUser指的是方法名    在webSocketCommand中obj.put("funName", "subscribeOrganizationUser");
//			boolean isUser = true;
//			if (msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0) {
//				isUser = true;
//			} else if (msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0) {
//				isUser = false;
//			}
//
//			try {
//				JSONObject obj = new JSONObject(msg);
//				JSONObject exObj = obj.getJSONObject("extend");
//				String userId = exObj.getString("userID");
//				if (userId != null && userId.equals(str_Username)) {
//
//					String bodyStr = obj.getString("body");
//					JSONObject bodyObj = new JSONObject(bodyStr);
//
//					JSONObject paramsObj = bodyObj.getJSONObject("params");
//					JSONArray nodesObj = paramsObj.getJSONArray("nodes");
//					for (int i = 0; i < nodesObj.length(); i++) {
//						JSONObject tmpObj = nodesObj.getJSONObject(i);
//						String departmentid = tmpObj.getString("departmentid");
//						// String usercount = tmpObj.getString("usercount");
//						String name = tmpObj.getString("name");
//						String parentid = tmpObj.getString("parentid");
//
//						SipInfo info = new SipInfo("", "", 0, departmentid, name, parentid, 0);
//						info.setStatus(1);
//						if (isUser) {
//							info.setType(SipInfo.TYPE_USE_DIRECTORY);
//							ConfigureParse.addUserInfo(info);
//						} else {
//							info.setType(SipInfo.TYPE_DEV_DIRECTORY);
//							ConfigureParse.addDeviceInfo(info);
//						}
//					}
//				}
//				startRegister();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	/**
	 * wss请求返回的结果
	 * @param msg
	 */
	protected void onReceiveWssMessage(String msg) {

		if (msg.isEmpty()) {
			return;
		} else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {

			ToolLog.i("==========onReceiveWssMessage closeTime");

		} else if (msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0 ||
				msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0) {
			// 用户资源指令 OrganizationUser
			// 设备资源指令 OrganizationDevice
			//备注：OrganizationUser指的是方法名    在webSocketCommand中obj.put("funName", "subscribeOrganizationUser");
			boolean isUser = true;
			if (msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0) {
				isUser = true;
			} else if (msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0) {
				isUser = false;
			}

			try {
				JSONObject obj = new JSONObject(msg);
				JSONObject exObj = obj.getJSONObject("extend");
				String userId = exObj.getString("userID");
				if (userId != null && userId.equals(str_Username)) {

					String bodyStr = obj.getString("body");
					JSONObject bodyObj = new JSONObject(bodyStr);

					JSONObject paramsObj = bodyObj.getJSONObject("params");
					JSONArray nodesObj = paramsObj.getJSONArray("nodes");
					for (int i = 0; i < nodesObj.length(); i++) {
						JSONObject tmpObj = nodesObj.getJSONObject(i);
						String departmentid = tmpObj.getString("departmentid");
						// String usercount = tmpObj.getString("usercount");
						String name = tmpObj.getString("name");
						String parentid = tmpObj.getString("parentid");

						SipInfo info = new SipInfo("", "", 0, departmentid, name, parentid, 0);
						info.setStatus(1);
						if (isUser) {
							info.setType(SipInfo.TYPE_USE_DIRECTORY);
							ConfigureParse.addUserInfo(info);
						} else {
							info.setType(SipInfo.TYPE_DEV_DIRECTORY);
							ConfigureParse.addDeviceInfo(info);
						}
					}
				}
//				startRegister();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (msg.indexOf(WssContant.WSS_INFORM_INIT_MOBLIE_MEDIA) >= 0){
             //初始化移动端媒体服务（即获取SIP消息）
			try{
				ParseInitMobileMedia parseInitMobileMedia = FastJsonTools.
						json2BeanObject(msg, ParseInitMobileMedia.class);
				if (parseInitMobileMedia !=null){
					String body = parseInitMobileMedia.getBody();
					if (body !=null && body.length()>0){
						ParseInitMobileMrdiaBody beanObject = FastJsonTools.json2BeanObject(body,
								ParseInitMobileMrdiaBody.class);
						ParseInitMobileMrdiaBody.ParamsBean params = beanObject.getParams();
						if (params !=null){
							Editor edit = sp.edit();
							//解码端  （播放）
							edit.putString(ConstantsValues.SIP_ID, params.getDecoderSIPID());
							edit.putString(ConstantsValues.SIP_PWD,  params.getClientPassword());
							edit.putString(ConstantsValues.SIP_SERVER_IP, params.getServerIP());
							edit.putString(ConstantsValues.SIP_SERVER_PORT, params.getServerPort());
							edit.putString(ConstantsValues.SIP_SERVER_ID, params.getServerID());

							//编码端（采集）
							edit.putString(ConstantsValues.ENCODE_ID, params.getEncoderSIPID());
							edit.putString(ConstantsValues.ENCODE_PWD, encodePwd);
							edit.putString(ConstantsValues.ENCODE_PORT, encodePort);
							edit.commit();

							ToolLog.i("==========sipInfo: sipId[" + params.getDecoderSIPID() + "] sipPwd[" +  params.getClientPassword()
									+ "] sipServerIp[" + params.getServerIP() + "] sipServerPort[" + params.getServerPort()
									+ "] sipServerId[" + params.getServerID() + "]");

							//onWssSubscribeResource();
							startRegister();
						}
					}
				}
			}catch (Exception e){
				e.getStackTrace();
			}
		}
	}

	/////////////////////////////////////////登陆成功后进入主界面的入口/////
	//正式的
	private void finishLogin() {

		//onCreateSdp();
		if (mLoadingDlg.isShowing()){
			mLoadingDlg.dismiss();
		}
		ToolLog.i("finishLogin");
		onMakeUserDirctory(SipManager.me.getId());
		startActivity(ActivityMain.class);
		finish();


	}

//	//测试用的
//	private void finishLogin() {
//	if (mLoadingDlg.isShowing()){
//	    mLoadingDlg.dismiss();
//   }
//		//onCreateSdp();
//		ToolLog.i("finishLogin");
//		onMakeUserDirctory(SipManager.me.getId());
//		startActivity(RequestNetDemoActivity.class);
//		isLoginSuccess = true;
//		finish();
//	}
}