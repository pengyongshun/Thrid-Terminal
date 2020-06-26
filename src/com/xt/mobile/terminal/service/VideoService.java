package com.xt.mobile.terminal.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.sipcapture.CaptureSipManager;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.sip.SessionManager;
import com.xt.mobile.terminal.sip.SipCallback;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.ToolSaveRunLog;
import com.xt.mobile.terminal.sip.SipManager;
import com.xtmedia.encode.MediaEncode;

public class VideoService extends Service {

	private Handler mainHandler;
	private ServiceBroadcastReceiver receiver;
	private SharedPreferences sp;
	
	//播放sip初始化
	public static final String ACTION_PLAY_SIP_INIT = "play_sip_init";
	//同步采集进程数据
	public static final String ACTION_SYNCH_SHOW_DATA = "action_synch_show_data";
	//搜集日志广播
	public static String ACTION_START_NEW_DAY_LOG = "action_start_new_day_log";
	private ToolSaveRunLog mSaveRunLog = null;
	private boolean mSaveLog = false;
	
	//采集视图悬浮框
	private static WindowManager mWinManager;
    private static WindowManager.LayoutParams mWinParams;
	private View mLayoutWindow;
    private SurfaceView mCaptureSurfaceView;
	private int mFloatWinWidth;
	private int mFloatWinHeight;
    public static final String ACTION_START_CAPTURE = "action_start_capture";
	public static final String ACTION_STOP_CAPTURE = "action_stop_capture";
	public static final String ACTION_SHOW_CAPTURE_WINDOW = "action_capture_show_window";
	public static final String ACTION_HIDE_CAPTURE_WINDOW = "action_capture_hide_window";
	//切换前置
	public static final String ACTION_SWTICH_FRONT_CAMERA = "action_swtich_front_camera";
	//切换后置
	public static final String ACTION_SWTICH_REAR_CAMERA = "action_swtich_rear_camera";
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
		
		sp = getSharedPreferences(ConstantsValues.DEFAULT_SP, MODE_PRIVATE);

		initHandler();
		initBroadcastReceiver();
		createCaptureFloatWindow();
		
		mSaveLog = sp.getBoolean(ConstantsValues.SP_KEY_SAVE_LOG, true);
		if (mSaveLog) {
			mSaveRunLog = new ToolSaveRunLog(this, getPackageName());
			if (mSaveRunLog != null) {
				mSaveRunLog.startSaveLog();
			}
		}
	}

	private void initHandler() {
		mainHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SipCallback.ON_REGISTER_STATUS:
					Intent loginSipIntent = new Intent(BaseActivity.ACTION_LOGIN_SIP);
					if ((Boolean) msg.obj) {
						SipManager.me.setStatus(1);

						loginSipIntent.putExtra("loginStatus", true);
						SipManager.initCapabilitySet(null, null);
					} else {
						loginSipIntent.putExtra("loginStatus", false);
					}
					sendBroadcast(loginSipIntent);
					break;
				case SipCallback.ON_RECEIVE_PLAY:
					Intent receivePlay = new Intent(BaseActivity.ACTION_RECEIVE_PLAY);
					receivePlay.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(receivePlay);
					break;
				case SipCallback.ON_PLAY_SUCCESS:
					Intent playSuccess = new Intent(BaseActivity.ACTION_PLAY_SUCCESS);
					playSuccess.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(playSuccess);
					break;
				case SipCallback.ON_PLAY_FAIL:
					Intent playFail = new Intent(BaseActivity.ACTION_PLAY_FAIL);
					playFail.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(playFail);
					break;
				case SipCallback.ON_RECEIVE_BYE:
					Intent receiveBye = new Intent(BaseActivity.ACTION_RECEIVE_BYE);
					receiveBye.putExtra("SessionId", (String) msg.obj);
					sendBroadcast(receiveBye);
					break;
				}
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWinManager != null && mLayoutWindow != null) {
			mWinManager.removeView(mLayoutWindow);
		}
		unregisterReceiver(receiver);
	}

	private void initBroadcastReceiver() {
		// 提高Service级别的通知
		receiver = new ServiceBroadcastReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction(ACTION_PLAY_SIP_INIT);
		filter.addAction(ACTION_SYNCH_SHOW_DATA);
		filter.addAction(ACTION_START_CAPTURE);
		filter.addAction(ACTION_STOP_CAPTURE);
		filter.addAction(ACTION_SHOW_CAPTURE_WINDOW);
		filter.addAction(ACTION_HIDE_CAPTURE_WINDOW);

		filter.addAction(ACTION_SWTICH_FRONT_CAMERA);
		filter.addAction(ACTION_SWTICH_REAR_CAMERA);

		if (mSaveLog) {
			filter.addAction(ACTION_START_NEW_DAY_LOG);
		}
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
			ToolLog.i("收到VideoService的广播action：" + action);
			if (ACTION_PLAY_SIP_INIT.equalsIgnoreCase(action)) {
				// 准备初始化
				String sipId = sp.getString(ConstantsValues.SIP_ID, "");
				String sipPwd = sp.getString(ConstantsValues.SIP_PWD, "");
				String userid = sp.getString(ConstantsValues.USERID, null);
				String username = sp.getString(ConstantsValues.USERNAME, null);
				String sipServerIp = intent.getStringExtra("SipServerIp");
				String sipServerId = intent.getStringExtra("SipServerId");
				String sipServerPort = intent.getStringExtra("SipServerPort");
				String localIp = intent.getStringExtra("LocalIp");

				SipManager.me = new SipInfo(sipId, localIp, 5064, userid,
						username, sipServerId, SipInfo.TYPE_MOBILE);
				SipManager.server = new SipInfo(sipServerId, sipServerIp,
						Integer.valueOf(sipServerPort), sipServerId, "sip中心", "",
						SipInfo.TYPE_SERVER);
				SipManager.me.setStatus(1);
				PLog.d("*********开始注册解码***********");
				PLog.d("*********开始注册解码***********");
				PLog.d("me="+SipManager.me.toString()+",server="+SipManager.server.toString());
				PLog.d("*********开始注册解码***********");
				PLog.d("*********开始注册解码***********");
				PLog.d("==========SIP=====", "----------SIP服务信息------->me:"+ SipManager.me.toString());
				PLog.d("==========SIP=====", "----------SIP服务信息------->server:"+SipManager.server.toString());
				ToolLog.i("===SipManager.me : " + SipManager.me.toString());
				ToolLog.i("===SipManager.server : " + SipManager.server.toString());

				if (SipManager.sipInit(context, sipPwd, new SipCallback(mainHandler))) {
					PLog.d("*********注册解码完成***********");
					PLog.d("*********注册解码完成***********");
					PLog.d("me="+SipManager.me.toString()+",server="+SipManager.server.toString());
					PLog.d("*********开始注册解码***********");
					PLog.d("*********开始注册解码***********");
					ToolLog.i("========create sipHandle success!!!");
					PLog.d("==========SIP=====", "----------SIP服务信息------->create sipHandle success");
					SipManager.register();
				} else {
					// 初始化就失败了,反馈给界面
					ToolLog.i("========create sipHandle faile!!!");
					Intent loginSipIntent = new Intent(BaseActivity.ACTION_LOGIN_SIP);
					loginSipIntent.putExtra("loginStatus", false);
					sendBroadcast(loginSipIntent);
				}
			} else if (ACTION_START_NEW_DAY_LOG.equals(action)) {
				if (mSaveRunLog != null) {
					mSaveRunLog.startSaveLog();
				}
			} else if (ACTION_SYNCH_SHOW_DATA.equals(action)) {
				Session session = (Session) intent.getSerializableExtra("SynchData");
				if (session != null) {
					SipManager.getInfofromSDP(session.getSdp());

					SessionManager.sendingPlay(session.getSessionId(), session.getSdp(),
							session.getSipch());

					Intent receivePlay = new Intent(BaseActivity.ACTION_CAPTURE_RECEIVE_PLAY);
					receivePlay.putExtra("SessionId", session.getSessionId());
					sendBroadcast(receivePlay);

				} else {
					ToolLog.i("========SynchData failed (session == null)");
				}
			} else if (ACTION_START_CAPTURE.equalsIgnoreCase(action)) {

				LayoutParams tmpParams = mCaptureSurfaceView.getLayoutParams();
				tmpParams.width = 1;
				tmpParams.height = 1;
				mCaptureSurfaceView.setLayoutParams(tmpParams);
				
				mWinParams.width = 1;
				mWinParams.height = 1;
				mWinParams.x = ConstantsValues.ScreenWidth - 1;
				mWinParams.y = 0;
				mWinManager.updateViewLayout(mLayoutWindow, mWinParams);
				
				onStartCapture(1);
				ToolLog.i("===VideoService::onReceive (开始采集)");
				
			} else if (ACTION_STOP_CAPTURE.equalsIgnoreCase(action)) {

				MediaEncode.stopEncodeSipEx(true, true);
				ToolLog.i("===VideoService::onReceive (停止采集)");
				
			} else if (ACTION_SHOW_CAPTURE_WINDOW.equalsIgnoreCase(action)) {

				LayoutParams tmpParams = mCaptureSurfaceView.getLayoutParams();
				tmpParams.width = mFloatWinWidth;
				tmpParams.height = mFloatWinHeight;
				mCaptureSurfaceView.setLayoutParams(tmpParams);

				mWinParams.width = mFloatWinWidth;
				mWinParams.height = mFloatWinHeight;
				mWinParams.x = ConstantsValues.ScreenWidth - mFloatWinWidth - 50;
				mWinParams.y = mFloatWinHeight * 3 / 5;
				mWinManager.updateViewLayout(mLayoutWindow, mWinParams);
				ToolLog.i("===VideoService::onReceive (显示窗口)");
				ToolLog.i("===VideoService::width:" + mWinParams.width + "; height:"
						+ mWinParams.height + "; x:" + mWinParams.x + "; y:" + mWinParams.y);
				
			} else if (ACTION_HIDE_CAPTURE_WINDOW.equalsIgnoreCase(action)) {
				
				LayoutParams tmpParams = mCaptureSurfaceView.getLayoutParams();
				tmpParams.width = 1;
				tmpParams.height = 1;
				mCaptureSurfaceView.setLayoutParams(tmpParams);

				mWinParams.width = 1;
				mWinParams.height = 1;
				mWinParams.x = ConstantsValues.ScreenWidth - 1;
				mWinParams.y =0;
				mWinManager.updateViewLayout(mLayoutWindow, mWinParams);
				ToolLog.i("===VideoService::onReceive (隐藏窗口)");
			} else if (ACTION_SWTICH_FRONT_CAMERA.equalsIgnoreCase(action)) {
                //切换到前置摄像头  1  默认前置
				//先停止采集
				MediaEncode.stopEncodeSipEx(true, true);
				ToolLog.i("===VideoService::onReceive (停止采集)");
				//切换前置摄像头
				onStartCapture(1);
				ToolLog.i("===VideoService::onReceive (开始采集)");

			} else if (ACTION_SWTICH_REAR_CAMERA.equalsIgnoreCase(action)) {
				//切换到后置摄像头  0
				//先停止采集
				MediaEncode.stopEncodeSipEx(true, true);
				ToolLog.i("===VideoService::onReceive (停止采集)");
				//切换后置摄像头
				onStartCapture(0);
				ToolLog.i("===VideoService::onReceive (开始采集)");

			}
		}
	}
	
	private void createCaptureFloatWindow() {
		
		if (mWinManager != null && mLayoutWindow != null) {
			mWinManager.removeView(mLayoutWindow);
		}
		
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutWindow = inflater.inflate(R.layout.float_capture_window, null);	
		mCaptureSurfaceView = (SurfaceView)mLayoutWindow.findViewById(R.id.mCaptureSurfaceView);

        mWinManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mWinParams = new WindowManager.LayoutParams();

		// 设置Window Type
		if (Build.VERSION.SDK_INT >= 26) { /*android7.0不能用TYPE_TOAST*/
			mWinParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
			ToolLog.i("====sdk version:26===");
		} else if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
			mWinParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			ToolLog.i("====sdk version:24===");
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = this.getPackageName();
            PackageManager pm = this.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
            	mWinParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
            	mWinParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
			ToolLog.i("====sdk version:other===");
        }

		mFloatWinWidth = ConstantsValues.ScreenWidth / 5;
		mFloatWinHeight = ConstantsValues.ScreenHeight / 5;

        // 设置悬浮框不可触摸
        mWinParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应
        mWinParams.format = PixelFormat.RGBA_8888;
        // 设置悬浮框的宽高
        mWinParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWinParams.width = 1;
        mWinParams.height = 1;
        mWinParams.x = ConstantsValues.ScreenWidth - 1;
        mWinParams.y = 0;
        // 设置悬浮框的Touch监听
        mLayoutWindow.setOnTouchListener(new View.OnTouchListener() {
            //保存悬浮框最后位置的变量
            int lastX, lastY;
            int paramX, paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mWinParams.x;
                        paramY = mWinParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mWinParams.x = paramX + dx;
                        mWinParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        mWinManager.updateViewLayout(mLayoutWindow, mWinParams);
                        break;
                }
                return true;
            }
        });
        mWinManager.addView(mLayoutWindow, mWinParams);
 
		ToolLog.i("===VideoService::onStartCapture (创建窗口采集)");
    }

	/**
	 * @param type  1-前置  0-后置
	 */
	private void onStartCapture(int type) {
		
		int width = ConstantsValues.vCaptureWidth;
		int height = ConstantsValues.vCaptureHeight;
		int frameSize = ConstantsValues.vCaptureBitRate;//width*height*3/2;
		int frameRate = ConstantsValues.vCaptureFrameRate;			
		MediaEncode.startEncodeSipEx(mCaptureSurfaceView, width, height, type,
				frameRate, frameSize);
		
		ToolLog.i("===VideoService::onStartCapture (开始采集编码)");
	}
}