package com.xt.mobile.terminal.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;

/**
 * 通讯录/设备信息
 */
public class ContactsDetialDeviceActivity extends BaseActivity {

    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private TextView mNameTv;
    private TextView mDepartmentTv;
    private LinearLayout mVedioControlLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_detial_device);
        initView();
    }


    private void initView() {
        initTop();
        initControl();
    }


    private void initControl() {
        mNameTv = (TextView) findViewById(R.id.activity_contacts_detial_device_name_tv);
        mDepartmentTv = (TextView) findViewById(R.id.activity_contacts_detial_device_department_tv);
        mVedioControlLl = (LinearLayout) findViewById(R.id.activity_contacts_detial_device_vedio_control_ll);
        mVedioControlLl.setOnClickListener(this);


    }

    private void initTop() {
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mRightIv = (ImageButton) findViewById(R.id.right_iv);

        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mRightTv.setVisibility(View.GONE);
        mRightIv.setVisibility(View.GONE);
        mTitleTv.setText(R.string.contacts_device_messge);

        mLeftIv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.left_iv) {
            //返回
            finish();
        }else if (id == R.id.activity_contacts_detial_device_vedio_control_ll) {
            //视频控制
            ToastUtil.showShort(ContactsDetialDeviceActivity.this,"视频控制");
        }

    }
}
