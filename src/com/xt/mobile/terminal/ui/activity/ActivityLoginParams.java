package com.xt.mobile.terminal.ui.activity;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToolKeyBoard;
import com.xt.mobile.terminal.util.XTUtils;

public class ActivityLoginParams extends BaseActivity {

	private EditText et_core_ip;
	private EditText et_core_port;
	private EditText et_encode_id;
	private EditText et_encode_pwd;
	private EditText et_encode_port;

	private String coreIp;
	private String corePort;	
	private String encodeId;
	private String encodePwd;
	private String encodePort;

	private ImageButton left_iv;
	private TextView title_tv;
	private TextView right_tv;

	private EditText et_capture_width;
	private EditText et_capture_height;
	private EditText et_capture_FrameRate;
	private EditText et_capture_BitRate;
	private EditText et_capture_CameraId;
	private String captureWidth;
	private String captureHeight;
	private String captureFrameRate;
	private String captureBitRate;
	private String captureCameraId;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_login_params);
		initView();
		initData();
		setResult(0);
	}

	private void initTop() {
		left_iv = (ImageButton) findViewById(R.id.left_iv);
		left_iv.setVisibility(View.VISIBLE);
		left_iv.setBackgroundResource(R.drawable.login_params_back);
		left_iv.setOnClickListener(this);
		title_tv = (TextView) findViewById(R.id.title_tv);
		left_iv.setVisibility(View.VISIBLE);
		title_tv.setText(R.string.login_params);
		right_tv = (TextView) findViewById(R.id.right_tv);
		right_tv.setVisibility(View.VISIBLE);
		right_tv.setText(R.string.save);
		right_tv.setOnClickListener(this);
	}

	private void initView() {
		initTop();
		et_core_ip = (EditText) findViewById(R.id.et_core_ip);
		et_core_port = (EditText) findViewById(R.id.et_core_port);
		et_encode_id = (EditText) findViewById(R.id.et_encode_id);
		et_encode_pwd = (EditText) findViewById(R.id.et_encode_pwd);
		et_encode_port = (EditText) findViewById(R.id.et_encode_port);

		et_capture_width = (EditText) findViewById(R.id.et_capture_width);
		et_capture_height = (EditText) findViewById(R.id.et_capture_height);
		et_capture_FrameRate = (EditText) findViewById(R.id.et_capture_FrameRate);
		et_capture_BitRate = (EditText) findViewById(R.id.et_capture_BitRate);
		et_capture_CameraId = (EditText) findViewById(R.id.et_capture_CameraId);
	}

	private void initData() {
		resources = getResources();
		// 获取之前保存的值
		coreIp = sp.getString(ConstantsValues.CORE_IP, "101.37.24.254");
		corePort = sp.getString(ConstantsValues.CORE_PORT, "443");		
		encodeId = sp.getString(ConstantsValues.ENCODE_ID, null);
		encodePwd = sp.getString(ConstantsValues.ENCODE_PWD, "123456");
		encodePort = sp.getString(ConstantsValues.ENCODE_PORT, "5071");
		
		captureWidth = sp.getString(ConstantsValues.CaptureWidth, "640");
		captureHeight = sp.getString(ConstantsValues.CaptureHeight, "480");
		captureFrameRate = sp.getString(ConstantsValues.CaptureFrameRate, "20");
		captureBitRate  = sp.getString(ConstantsValues.CaptureBitRate , "500000");
		captureCameraId  = sp.getString(ConstantsValues.CaptureCameraId , "1");
		if (!TextUtils.isEmpty(captureWidth)) {
			et_capture_width.setText(captureWidth);
		}
		if (!TextUtils.isEmpty(captureHeight)) {
			et_capture_height.setText(captureHeight);
		}
		if (!TextUtils.isEmpty(captureFrameRate)) {
			et_capture_FrameRate.setText(captureFrameRate);
		}
		if (!TextUtils.isEmpty(captureBitRate)) {
			et_capture_BitRate.setText(captureBitRate);
		}
		if (!TextUtils.isEmpty(captureCameraId)) {
			et_capture_CameraId.setText(captureCameraId);
		}
		
		if (!TextUtils.isEmpty(coreIp)) {
			et_core_ip.setText(coreIp);
		}
		if (!TextUtils.isEmpty(corePort)) {
			et_core_port.setText(corePort);
		}		
		if (!TextUtils.isEmpty(encodeId)) {
			et_encode_id.setText(encodeId);
		}
		if (!TextUtils.isEmpty(encodePwd)) {
			et_encode_pwd.setText(encodePwd);
		}
		if (!TextUtils.isEmpty(encodePort)) {
			et_encode_port.setText(encodePort);
		}
	}

	/**
	 * 检查出入的内容是否合法
	 * 
	 * @return
	 */
	private void saveInfo() {
		coreIp = et_core_ip.getText().toString();
		int back = XTUtils.isIpv4(coreIp);
		if (back == 1) {
			showToast(getResources().getString(R.string.toast_core_IP_not_null));
			return;
		} else if (back == 2) {
			showToast(getResources().getString(R.string.toast_core_IP_fmt_error));
			return;
		}
		corePort = et_core_port.getText().toString();
		back = XTUtils.isPort(corePort);
		if (back == 1) {
			showToast("请填写中心端口号");
			return;
		} else if (back == 2) {
			showToast("中心端口错误(0~65535)");
			return;
		}
		
		encodeId = et_encode_id.getText().toString();
		back = XTUtils.isPort(encodeId);
		if (back == 1) {
			showToast("请填写编码ID");
			return;
		}
		encodePwd = et_encode_pwd.getText().toString();
		back = XTUtils.isPort(encodePwd);
		if (back == 1) {
			showToast("请填写编码密码");
			return;
		}
		encodePort = et_encode_port.getText().toString();
		back = XTUtils.isPort(encodePort);
		if (back == 1) {
			showToast("请填写编码端口");
			return;
		}

		captureWidth = et_capture_width.getText().toString();
		captureHeight = et_capture_height.getText().toString();
		captureFrameRate = et_capture_FrameRate.getText().toString();
		captureBitRate = et_capture_BitRate.getText().toString();
		captureCameraId = et_capture_CameraId.getText().toString();
		ConstantsValues.vCaptureWidth = Integer.valueOf(captureWidth);
		ConstantsValues.vCaptureHeight = Integer.valueOf(captureHeight);
		ConstantsValues.vCaptureFrameRate = Integer.valueOf(captureFrameRate);
		ConstantsValues.vCaptureBitRate = Integer.valueOf(captureBitRate);
		ConstantsValues.vCaptureCameraId = Integer.valueOf(captureCameraId);
		
		Editor edit = sp.edit();
		edit.putString(ConstantsValues.CORE_IP, coreIp);
		edit.putString(ConstantsValues.CORE_PORT, corePort);
		edit.putString(ConstantsValues.ENCODE_ID, encodeId);
		edit.putString(ConstantsValues.ENCODE_PWD, encodePwd);
		edit.putString(ConstantsValues.ENCODE_PORT, encodePort);
		edit.putString(ConstantsValues.CaptureWidth, captureWidth);
		edit.putString(ConstantsValues.CaptureHeight, captureHeight);
		edit.putString(ConstantsValues.CaptureFrameRate, captureFrameRate);
		edit.putString(ConstantsValues.CaptureBitRate, captureBitRate);
		edit.putString(ConstantsValues.CaptureCameraId, captureCameraId);
		edit.commit();

		ToolKeyBoard.onHideKeyBoard(right_tv);
		
		setResult(9);
		finish();
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.left_iv) {
			//取消、返回
			finish();
		}else if (id == R.id.right_tv){
			saveInfo();
		}

	}
}