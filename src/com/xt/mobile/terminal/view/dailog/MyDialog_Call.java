package com.xt.mobile.terminal.view.dailog;

import com.xt.mobile.terminal.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyDialog_Call extends Dialog implements OnClickListener {

	private VideoCallCallBack callback = null;

	private TextView mTvVideoCall;
	private TextView mTvAudioRing;
	private TextView mTvCancel;

	public MyDialog_Call(Context context, VideoCallCallBack callback) {
		super(context, R.style.MyDialog);
		// TODO Auto-generated constructor stub
		this.callback = callback;
	}

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydialog_call);
		setParams();
		onInitControl();
	}

	private void setParams() {

		Window win = getWindow();
		win.setWindowAnimations(R.style.MyDialogStyleBottom);
		win.setGravity(Gravity.BOTTOM);
	}

	/**
	 * 初始化控件
	 */
	private void onInitControl() {

		mTvVideoCall = (TextView) findViewById(R.id.mTvVideoCall);
		mTvAudioRing = (TextView) findViewById(R.id.mTvAudioRing);
		mTvCancel = (TextView) findViewById(R.id.mTvCancel);
		
		mTvVideoCall.setOnClickListener(this);
		mTvAudioRing.setOnClickListener(this);
		mTvCancel.setOnClickListener(this);

	}

	/**
	 * 按钮事件
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.mTvVideoCall) {
			callback.setResult("VideoCall");
			dismiss();
		}else if (v.getId() == R.id.mTvAudioRing){
			callback.setResult("AudioRing");
			dismiss();
		}else if (v.getId() == R.id.mTvCancel){
			dismiss();
		}
	}

	/***
	 * 回调方法
	 */
	public interface VideoCallCallBack {
		public void setResult(String result);
	}

}
