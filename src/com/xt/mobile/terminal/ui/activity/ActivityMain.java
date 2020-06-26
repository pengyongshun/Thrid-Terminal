package com.xt.mobile.terminal.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.XtFragmentPagerAdapter;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.fragment.FragmentContacts;
import com.xt.mobile.terminal.ui.fragment.FragmentMessage;
import com.xt.mobile.terminal.ui.fragment.FragmentMetting;
import com.xt.mobile.terminal.ui.fragment.FragmentSetting;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.wss.IWebSocketClientService;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.SystemUtil;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xt.mobile.terminal.view.dailog.VDialog;
import com.xt.mobile.terminal.widget.NoScrollViewPager;
import com.xtmedia.port.SendPort;
import com.xtmedia.xtview.GlRenderNative;

public class ActivityMain extends BaseActivity implements RequestUitl.HttpResultCall {

	private NoScrollViewPager mVpContent;
	private XtFragmentPagerAdapter mXtFPAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	private FragmentContacts mFragmentContact;
	private FragmentMessage mFragmentMessage;
	private FragmentSetting mFragmentSetting;
	private FragmentMetting mFragmentMetting;

	public static LinearLayout layoutMessge;
	private LinearLayout layoutContacts;
	private LinearLayout layoutMy;
	private LinearLayout layoutMetting;
	private ImageView ivMessge;
	private ImageView ivContacts;
	private ImageView ivMy;
	private ImageView ivMetting;
	private TextView tvMessge;
	private TextView tvContacts;
	private TextView tvMy;
	private TextView tvMetting;

	private int mOldId;
	private int mTextColorNormal = 0;
	private int mTextColorPress = 0;
	
	private long lastExitTime;
	private HttpAsyncTask mAsyncTask;
	private RequestUitl instans;
	private HttpAsyncTask mHttpAsyncTask;
	private CustomTextDialog outLoginDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instans = RequestUitl.getInstans(this,this);
		onInitFragment();
		onInitButton();
		initDaloig();
		//默认进入会议界面
		onSetImageButtonStatus(ivMetting, R.drawable.icon_metting_pr, tvMetting, mTextColorPress);
		mOldId = R.id.activity_main_metting_ll;
		//updateFragment(mFragmentMetting);
		mVpContent.setCurrentItem(1, false);

		try {

			ToolLog.i("==========================create send port===111===");
			// 创建全局唯二的两个发送端口
			SendPort.createSendPort();

			ToolLog.i("==========================create send port===222===");
			GlRenderNative.setPlayStatusEnable(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		mHandler.postDelayed(mRunnable, 30000);
		
		Intent intent = new Intent(VideoService.ACTION_START_CAPTURE);
		sendBroadcast(intent);
	}

	private void initDaloig() {

		outLoginDialog = DailogUitl.initTextDialog(ActivityMain.this, "退出提醒", "请确认是否退出登陆？","确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//退出登陆
				mHandler.removeCallbacksAndMessages(null);
				finish();
			}
		});
	}

	void PlayStatus(int nIndexTask, int value) {
		if (value == 16) {
			Intent playRemoteIntent = new Intent(BaseActivity.ACTION_LOAD_SUCCESS);
			sendBroadcast(playRemoteIntent);
		}
	}

	private void onInitFragment() {
		mTextColorNormal = getResources().getColor(R.color.common_text_color);
		mTextColorPress = getResources().getColor(R.color.tab_text);

		mFragmentMessage = new FragmentMessage();
		mFragmentContact = new FragmentContacts();
		mFragmentSetting = new FragmentSetting();
		mFragmentMetting = new FragmentMetting();

		mFragments.add(mFragmentMessage);
		mFragments.add(mFragmentMetting);

		mFragments.add(mFragmentContact);
		mFragments.add(mFragmentSetting);
	}

	private void onInitButton() {
		mVpContent = findViewById(R.id.vp_main_content);
		mVpContent.setOffscreenPageLimit(4);
		mVpContent.setNoScroll(true);
		mXtFPAdapter = new XtFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
		mVpContent.setAdapter(mXtFPAdapter);
		//消息
		layoutMessge = (LinearLayout) findViewById(R.id.activity_main_messge_ll);
		//通讯录
		layoutContacts = (LinearLayout) findViewById(R.id.activity_main_contacts_ll);
		//我的
		layoutMy = (LinearLayout) findViewById(R.id.activity_main_my_ll);
		//会议
		layoutMetting = (LinearLayout) findViewById(R.id.activity_main_metting_ll);

		ivMessge = (ImageView) findViewById(R.id.activity_main_messge_iv);
		ivContacts = (ImageView) findViewById(R.id.activity_main_contacts_iv);
		ivMy = (ImageView) findViewById(R.id.activity_main_my_iv);
		ivMetting = (ImageView) findViewById(R.id.activity_main_metting_iv);

		tvMessge = (TextView) findViewById(R.id.activity_main_messge_tv);
		tvContacts = (TextView) findViewById(R.id.activity_main_contacts_tv);
		tvMy = (TextView) findViewById(R.id.activity_main_my_tv);
		tvMetting = (TextView) findViewById(R.id.activity_main_metting_tv);

		layoutMessge.setOnClickListener(mOnClick);
		layoutContacts.setOnClickListener(mOnClick);
		layoutMy.setOnClickListener(mOnClick);
		layoutMetting.setOnClickListener(mOnClick);

	}

	private OnClickListener mOnClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (XTUtils.fastClick(250)) {
				return;
			}
			int id = arg0.getId();
			if (id == mOldId) {
				return;
			}
			onRefreshPrevButton();
			if (id == R.id.activity_main_messge_ll) {
				//消息
				onSetImageButtonStatus(ivMessge, R.drawable.icon_messge_pr, tvMessge,
						mTextColorPress);
				mOldId = R.id.activity_main_messge_ll;
				mVpContent.setCurrentItem(0, false);
			}else if (id == R.id.activity_main_contacts_ll){
				//通讯录
				onSetImageButtonStatus(ivContacts, R.drawable.icon_content_pr, tvContacts,
						mTextColorPress);
				mOldId = R.id.activity_main_contacts_ll;
				mVpContent.setCurrentItem(2, false);

			}else if (id == R.id.activity_main_my_ll){
				//我的
				onSetImageButtonStatus(ivMy, R.drawable.icon_my_pr, tvMy,
						mTextColorPress);
				mOldId = R.id.activity_main_my_ll;
				mVpContent.setCurrentItem(3, false);

			}else if (id == R.id.activity_main_metting_ll){
				//会议
				onSetImageButtonStatus(ivMetting, R.drawable.icon_metting_pr, tvMetting,
						mTextColorPress);
				mOldId = R.id.activity_main_metting_ll;
				mVpContent.setCurrentItem(1, false);

			}
		}
	};

	private void onRefreshPrevButton() {
		if (mOldId == R.id.activity_main_messge_ll) {
			onSetImageButtonStatus(ivMessge, R.drawable.icon_messge_nor, tvMessge,
					mTextColorNormal);
		} else if (mOldId == R.id.activity_main_contacts_ll) {
			onSetImageButtonStatus(ivContacts, R.drawable.icon_content_nor, tvContacts,
					mTextColorNormal);
		} else if (mOldId == R.id.activity_main_my_ll) {
			onSetImageButtonStatus(ivMy, R.drawable.icon_my_nor, tvMy,
					mTextColorNormal);
		} else if (mOldId == R.id.activity_main_metting_ll) {
			onSetImageButtonStatus(ivMetting, R.drawable.icon_metting_nor, tvMetting,
					mTextColorNormal);
		}
	}

	private void onSetImageButtonStatus(ImageView iv, int icon, TextView tv, int color) {
		iv.setImageResource(icon);
		tv.setTextColor(color);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// 后台自动登录成功
	@Override
	protected void onLoginStatus(boolean loginSuccess) {
		if (loginSuccess) {
			if (mOldId == R.id.activity_main_contacts_ll) {
				if (mFragmentContact != null) {
					mFragmentContact.update();
				}
			}else if (mOldId == R.id.activity_main_my_ll){
				if (mFragmentSetting != null) {
					mFragmentSetting.update();
				}
			}else if (mOldId == R.id.activity_main_messge_ll){
				if (mFragmentMessage != null) {
					mFragmentMessage.update();
				}
			}else if (mOldId == R.id.activity_main_metting_ll){
				if (mFragmentMetting != null) {
					mFragmentMetting.update();
				}
			}
		}
	}

	/**
	 * 播放画面成功出现第一帧数据(并且此时tab是处于用户预览)
	 */
	@Override
	public void loadSuccess() {
		if (isBackground) {
			return;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopJWebSClientService();

		SipManager.media_map.clear();
		SipManager.localMedia.clear();
		ConfigureParse.reset();
		SipManager.unregister();
		SendPort.destroysendPort();// 销毁发送端口
		msgHandler.removeCallbacksAndMessages(null);
		XTUtils.onReleaseMsgSound();

		Intent intent = new Intent(VideoService.ACTION_STOP_CAPTURE);
		sendBroadcast(intent);
		Intent intent1 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_UNINIT);
		sendBroadcast(intent1);
		
		// 这里不能停止服务,如果停止的话必须保证application也停止否则再次打开程序不会开启服务并注册广播监听，程序跑不起来
		// Intent intent = new Intent(this, VideoService.class);
		// stopService(intent);
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		// 不保存状态，让activity重建时，fragment也重建
	}

	protected void onReceiveWssMessage(String msg) {

		super.onReceiveWssMessage(msg);
		
		if (mOldId == R.id.activity_main_contacts_ll) {
			//进入通讯录
			String tmpMsg = msg;
			mFragmentContact.requestResourceAnswer(tmpMsg);
		}else if (mOldId == R.id.activity_main_metting_ll){
			//进入会议界面
			mFragmentMetting.requestResourceAnswer(msg,1000);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - lastExitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				lastExitTime = System.currentTimeMillis();
			} else {
				if (outLoginDialog !=null && !(outLoginDialog.isShowing())){
					outLoginDialog.show();
				}

			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void stopJWebSClientService() {

		WebSocketCommand.getInstance().onSendClearUserStatus();
		WebSocketCommand.getInstance().onSendExit();
		//退出登陆
		//WebSocketCommand.getInstance().onOutLogin();

		String tokenKey = sp.getString(ConstantsValues.TOKENKEY, null);
		if (!tokenKey.isEmpty()) {
			onRemoveTokenKey();
		} else {
			ToolLog.i("===ActivityMain::stopJWebSClientService (mTokenKey isEmpty)");
		}

		Intent intent = new Intent(this, IWebSocketClientService.class);
		stopService(intent);

		//清理缓存的数据
		UserMessge.getInstans(ActivityMain.this).clearData();
	}

	/**
	 * 退出登陆
	 */
	public void onRemoveTokenKey() {
		String tokenKey = sp.getString(ConstantsValues.TOKENKEY, null);
		List<NameValuePair> params = MoudleParams.getDestroyTokenParams(this, tokenKey);
		if (instans !=null){
			instans.sendRequest(params,true,Constants.HTTP_GET_LOGIN_DESTROYTOKEN1);
		}


	}

	//////===============================================
	
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.removeCallbacksAndMessages(null);
			mHandler.postDelayed(mRunnable, 30000);
			onRenewUserToken();
		}
	};

	/**
	 * 更新用户鉴权，与websocket的732消息一起链路保活
	 */
	public void onRenewUserToken() {
		//参数
		String tokenkey = UserMessge.getInstans(ActivityMain.this).getUserTokenkey();
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("operatorToken", tokenkey));
		paramsList.add(new BasicNameValuePair("minutes", "1"));
		//对参数进行格式化
		String params = URLEncodedUtils.format(paramsList, HTTP.UTF_8);
		//url
		String url= ConstantsValues.getHttpUrl(ActivityMain.this, NetUrl.
				HTTP_RenewUserToken);
		String path = url+"?" + params;
		RenewUserTokenCallBack jsonCallback = new RenewUserTokenCallBack();
		mHttpAsyncTask = new HttpAsyncTask(ActivityMain.this, path, null, jsonCallback, true, true);
		mHttpAsyncTask.execute("");
	}

	private class RenewUserTokenCallBack implements HttpAsyncTask.HttpCallBack{

		@Override
		public void setResult(String result) {
			//销毁异步任务
			if (mHttpAsyncTask != null && !mHttpAsyncTask.isCancelled()) {
				mHttpAsyncTask.cancel(true);
				mHttpAsyncTask = null;
			}

			if ((!result.equals("errorNet") && !result.contentEquals("errorData"))||
					result.contains("success")) {
				//成功
			} else {
				//异常
			}
		}
	}

	//////////////////////////////////网络请求结果的地方/////////////////////////

	@Override
	public void success(int tag, String result) {
		switch (tag){
			//退出登陆
			case Constants.HTTP_GET_LOGIN_DESTROYTOKEN1:
				break;

		}

	}

	@Override
	public void faile(int tag, String error) {
		switch (tag){
			//退出登陆
			case Constants.HTTP_GET_LOGIN_DESTROYTOKEN1:
				break;

		}

	}
}