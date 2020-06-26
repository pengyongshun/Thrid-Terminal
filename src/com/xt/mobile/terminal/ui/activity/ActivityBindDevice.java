package com.xt.mobile.terminal.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.BindDeviceAdapter;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask.HttpCallBack;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.util.comm.UserMessge;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityBindDevice extends BaseActivity implements RequestUitl.HttpResultCall {

	private TextView title_tv;
	private ImageView left_iv;
	private ListView lv_devices;
	private BindDeviceAdapter adapter;

	private ArrayList<SipInfo> devices = new ArrayList<SipInfo>();
	private int mSelectIndex = -1;
	private int mStepStart = 0;
	private final int mStepCount = 1024;

	private HttpAsyncTask mAsyncTask;
	private String mBindDeviceId = "";

	private long mLastClickTime = 0;
	private int mClickCnt = 0;
	private RequestUitl instans;
	private UserMessge userMessge;

	@Override
	protected void onCreate(Bundle args) {
		super.onCreate(args);
		setContentView(R.layout.activity_bind_device);
		instans = RequestUitl.getInstans(this,this);
		userMessge = UserMessge.getInstans(this);

		mClickCnt = 0;
		mTokenKey = sp.getString(ConstantsValues.TOKENKEY, null);
		mUserId = userMessge.getUserID();
		mBindDeviceId = userMessge.getUserDeviceId();
		onQueryDeviceList(mStepStart, mStepCount);
		initTop();
		initView();

	}

	private void initTop() {
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText(R.string.bind_device);

		left_iv = (ImageButton) findViewById(R.id.left_iv);
		left_iv.setVisibility(View.VISIBLE);
		left_iv.setBackgroundResource(R.drawable.login_params_back);
		left_iv.setOnClickListener(this);
	}

	private void initView() {

		Button bt_confirm = (Button) findViewById(R.id.bt_confirm);
		bt_confirm.setOnClickListener(this);

		adapter = new BindDeviceAdapter(this, devices);
		lv_devices = (ListView) findViewById(R.id.lv_devices);
		lv_devices.setAdapter(adapter);

		lv_devices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				mSelectIndex = position;
				adapter.setSelect(mSelectIndex);
				adapter.notifyDataSetInvalidated();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.bt_confirm) {
			if (mSelectIndex >= 0) {
				String deviceId = devices.get(mSelectIndex).getUserid();
				if (mBindDeviceId != null && !mBindDeviceId.isEmpty()
						&& mBindDeviceId.equals(deviceId)) {
					Intent data = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("DeviceName", devices.get(mSelectIndex).getUsername());
					data.putExtras(bundle);
					ActivityBindDevice.this.setResult(9, data);
					finish();
				} else {
					onBindDevice(deviceId);
				}
			} else {
				showToast("请选择设备");
			}
		}else if (id == R.id.left_iv){
			if (mClickCnt == 0) {
				mLastClickTime = System.currentTimeMillis();
			}
			mClickCnt++;
			long curTime = System.currentTimeMillis();
			if (curTime - mLastClickTime > 2000 || mClickCnt == 1) {
				mLastClickTime = curTime;
				mClickCnt = 1;
				showToast("您还没有绑定设备；再次点击退出。");
			} else {
				finish();
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mClickCnt == 0) {
				mLastClickTime = System.currentTimeMillis();
			}
			mClickCnt++;
			long curTime = System.currentTimeMillis();
			if (curTime - mLastClickTime > 2000 || mClickCnt == 1) {
				mLastClickTime = curTime;
				mClickCnt = 1;
				showToast("您还没有绑定设备；再次点击退出。");
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (devices != null) {
			devices.clear();
			devices = null;
		}
		super.onDestroy();
	}

	/**
	 * 设备查询的网络请求
	 * @param beginIndex
	 * @param count
	 */
	public void onQueryDeviceList(int beginIndex, int count) {

		//参数
		Map<String,String> map=new HashMap<String,String>();
		map.put("beginIndex",String.valueOf(beginIndex));
		map.put("count",String.valueOf(count));
		map.put("operatorToken",mTokenKey);
		List<NameValuePair> params = MoudleParams.getQueryDeviceParams(this, map);
		if (instans !=null){
			instans.sendRequest(params,true,Constants.HTTP_GET_QUERY_DEVICE);
		}
	}


	/**
	 * 绑定设备的网络请求
	 * @param deviceId
	 */
	public void onBindDevice(String deviceId) {

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("userID", mUserId);
			subObj.put("callItem", "MANUAL");
			subObj.put("encoderItem", deviceId);
			subObj.put("decoderItem", "");
			subObj.put("meetingItem", "MANUAL");
			cmd = "operatorToken=" + mTokenKey + "&&obj=" + subObj.toString();
		} catch (Exception e) {
			cmd = null;
		}

		String path = ConstantsValues.getHttpUrl(this, NetUrl.
				HTTP_BindDevice);

		ToolLog.i("===@@@===path: " + path);
		ToolLog.i("===@@@===cmd: " + cmd);

		BindDeviceCallback jsonCallback = new BindDeviceCallback();
		mAsyncTask = new HttpAsyncTask(this, path, cmd, jsonCallback, false, true);
		mAsyncTask.execute("");
	}

	public class BindDeviceCallback implements HttpCallBack {

		@Override
		public void setResult(String result) {
			// TODO Auto-generated method stub
			if (mAsyncTask != null && !mAsyncTask.isCancelled()) {
				mAsyncTask.cancel(true);
				mAsyncTask = null;
			}

			if (!result.equals("errorNet") && !result.contentEquals("errorData")) {
				try {
					ToolLog.i("=========onBindDevice====" + result);

					JSONObject jobj = new JSONObject(result);
					String State = jobj.getString("responseCode");
					if (State != null && !State.isEmpty() && State.equals("1")) {

						onSaveBindDevice();

						Intent data = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("DeviceName", devices.get(mSelectIndex).getUsername());
						data.putExtras(bundle);
						ActivityBindDevice.this.setResult(9, data);
						finish();
					} else {
						showToast("绑定设备失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("绑定设备失败");
				}
			}
		}
	}

	private void onSaveBindDevice() {

		String deviceId = devices.get(mSelectIndex).getUserid();
		String deviceName = devices.get(mSelectIndex).getUsername();
        userMessge.setUserDeviceId(deviceId);
		userMessge.setUserDeviceName(deviceName);

	}

	//////////////////////////////////////////////网络请求结果的地方//////////////////

	@Override
	public void success(int tag, String result) {
		switch (tag){
			//设备查询
			case Constants.HTTP_GET_QUERY_DEVICE:
				try {
					ToolLog.i("=========onQueryDeviceList====" + result);

					JSONObject jobj = new JSONObject(result);
					String State = jobj.getString("responseCode");
					if (State != null && !State.isEmpty() && State.equals("1")) {

						JSONObject obj = jobj.getJSONObject("data");
						JSONArray list = obj.getJSONArray("list");

						if (list != null && list.length() > 0) {
							for (int i = 0; i < list.length(); i++) {

								JSONObject itemObj = list.getJSONObject(i);
								String deviceId = itemObj.getString("deviceID");
								String deviceName = itemObj.getString("deviceName");

								SipInfo info = new SipInfo("", "", 0, deviceId, deviceName, "", 0);
								devices.add(info);
							}
						}

						// 当编码器多于1024时，分级加载
						if (list.length() > 0 && list.length() < mStepCount) {
							for (int i = 0; i < devices.size(); i++) {
								if (mBindDeviceId != null && !mBindDeviceId.isEmpty()
										&& mBindDeviceId.equals(devices.get(i).getUserid())) {
									//设置值
									mSelectIndex = i;
									adapter.setSelect(mSelectIndex);
								}
							}
							adapter.notifyDataSetChanged();
						} else {
							onQueryDeviceList(list.length(), mStepCount);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public void faile(int tag, String error) {
		switch (tag){
			//设备查询
			case Constants.HTTP_GET_QUERY_DEVICE:
				showToast("查询失败");
				break;
		}
	}
}