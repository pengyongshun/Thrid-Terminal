package com.xt.mobile.terminal.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.face.ui.FaceLoginActivity;
import com.xt.mobile.terminal.ui.activity.ActivityBindDevice;
import com.xt.mobile.terminal.ui.activity.ActivityLoginParams;
import com.xt.mobile.terminal.ui.activity.MediaSettingActivity;
import com.xt.mobile.terminal.ui.activity.NotificationSettingActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.util.XTUtils;

public class FragmentSetting extends Fragment implements OnClickListener {
	private View rootView;

	private TextView mLeftTv;
	private ImageButton mLeftIv;
	private TextView mTitleTv;
	private TextView mRightTv;
	private ImageButton mRightIv;
	private TextView mUsernameTv;
	private TextView mCommpanyTv;
	private RelativeLayout mServiceSettingRl;
	private RelativeLayout mFaceSettingRl;
	private RelativeLayout mMediaSettingRl;
	private RelativeLayout mNotifSettingRl;
	private Button mOutLoginBtn;
	private CustomTextDialog faceDialog;
	private CustomTextDialog outLoginDialog;
	private RelativeLayout mBindDeviceRl;
	private TextView mBindDeviceTv;

    private UserMessge userMessge;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,
			 Bundle savedInstanceState) {
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		initRoot();
	}

	private void initRoot() {
		if (rootView == null) {
			rootView = View.inflate(getActivity(), R.layout.fragment_setting, null);
		}
		initTop();
		initControl();
		initDaloig();
	}

	/**
	 * 初始化对话框
	 */
	private void initDaloig() {
		//初始化对话框
		faceDialog = DailogUitl.initTextDialog(getActivity(), "录入提醒", "已录入人脸信息,是否重新录入？","确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//重新录入
				ActivityTools.startActivity(getActivity(),
						FaceLoginActivity.class,false);
			}
		});

		outLoginDialog = DailogUitl.initTextDialog(getActivity(), "退出提醒", "请确认是否退出登陆？","确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//退出登陆
				getActivity().finish();
			}
		});
	}

	private void initTop() {
		mLeftTv = (TextView) rootView.findViewById(R.id.left_tv);
		mLeftIv = (ImageButton) rootView.findViewById(R.id.left_iv);
		mTitleTv = (TextView) rootView.findViewById(R.id.title_tv);
		mRightTv = (TextView) rootView.findViewById(R.id.right_tv);
		mRightIv = (ImageButton) rootView.findViewById(R.id.right_iv);
		mLeftTv.setVisibility(View.GONE);
		mLeftIv.setVisibility(View.GONE);
		mRightTv.setVisibility(View.GONE);
		mRightIv.setVisibility(View.GONE);
		mTitleTv.setText(R.string.my);
	}

	private void initControl() {
		mUsernameTv = (TextView) rootView.findViewById(R.id.fragment_setting_username_tv);
		mCommpanyTv = (TextView) rootView.findViewById(R.id.fragment_setting_commpany_tv);
		mServiceSettingRl = (RelativeLayout) rootView.findViewById(R.id.fragment_setting_service_setting_tv);
		mFaceSettingRl = (RelativeLayout) rootView.findViewById(R.id.fragment_setting_face_setting_tv);
		mMediaSettingRl = (RelativeLayout) rootView.findViewById(R.id.fragment_setting_media_setting_tv);
		mNotifSettingRl = (RelativeLayout) rootView.findViewById(R.id.fragment_setting_notif_setting_tv);
		mBindDeviceTv = (TextView) rootView.findViewById(R.id.fragment_setting_bind_device_tv);
		mBindDeviceRl = (RelativeLayout) rootView.findViewById(R.id.fragment_setting_bind_device_rl);
		mOutLoginBtn = (Button) rootView.findViewById(R.id.fragment_setting_out_login_btn);
		mServiceSettingRl.setOnClickListener(this);
		mFaceSettingRl.setOnClickListener(this);
		mNotifSettingRl.setOnClickListener(this);
		mOutLoginBtn.setOnClickListener(this);
		mMediaSettingRl.setOnClickListener(this);
		mBindDeviceRl.setOnClickListener(this);

		mUsernameTv.setText(UserMessge.getInstans(getActivity()).getUserName());
        mCommpanyTv.setVisibility(View.GONE);
        userMessge = UserMessge.getInstans(getActivity());
        if (userMessge != null) {
            mBindDeviceTv.setText(userMessge.getUserDeviceName());
        }
	}


	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.fragment_setting_service_setting_tv) {
			// 服务设置
			ActivityTools.startActivity(getActivity(),
					ActivityLoginParams.class,false);
		}else if (id == R.id.fragment_setting_face_setting_tv) {
			// 人脸设置
			if (faceDialog !=null && !(faceDialog.isShowing())){
				faceDialog.show();
			}
		}else if (id == R.id.fragment_setting_media_setting_tv) {
			// 媒体设置
			ActivityTools.startActivity(getActivity(), MediaSettingActivity.class,
					false);
		}else if (id == R.id.fragment_setting_notif_setting_tv) {
			// 提醒设置
			ActivityTools.startActivity(getActivity(), NotificationSettingActivity.class,
					false);
		}else if (id == R.id.fragment_setting_out_login_btn) {
			// 退出登陆
			if (outLoginDialog !=null && !(outLoginDialog.isShowing())){
				outLoginDialog.show();
			}
		}else if (id == R.id.fragment_setting_bind_device_rl) {
			// 绑定设备
			Intent intent = new Intent(getActivity(), ActivityBindDevice.class);
			startActivityForResult(intent, 9);
		}

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 9 && resultCode == 9) {
			Bundle bundle = data.getExtras();
			if (data != null && bundle != null) {
				String deviceName = bundle.getString("DeviceName");
				mBindDeviceTv.setText(deviceName);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void update() {

	}
}