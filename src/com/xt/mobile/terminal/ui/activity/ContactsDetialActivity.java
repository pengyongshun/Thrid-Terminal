package com.xt.mobile.terminal.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录/人员信息
 */
public class ContactsDetialActivity extends BaseActivity {

    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private TextView mNameTv;
    private TextView mDepartmentTv;
    private LinearLayout mSendMessgeLl;
    private LinearLayout mVideoCallLl;
    private LinearLayout mVoiceCallLl;
    private SipInfo mResSipInfo = null;
    private String mCompAndDepart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_detial);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);//设置状态栏背景颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
    }

    private void initView() {
        initSipInfo();
        initTop();
        initControl();
    }

    private void initSipInfo() {
        mResSipInfo = (SipInfo) getIntent().getSerializableExtra("ResSipInfo");
        mCompAndDepart = getIntent().getStringExtra("CompAndDepart");
        if (mResSipInfo == null){
            ToastUtil.showLong(ContactsDetialActivity.this,"人员信息错误");
            finish();
        }else {
            mUserName = mResSipInfo.getUsername();
            mUserId = mResSipInfo.getUserid();
        }
    }

    private void initControl() {
        mNameTv = (TextView) findViewById(R.id.activity_contacts_detial_name_tv);
        mDepartmentTv = (TextView) findViewById(R.id.activity_contacts_detial_department_tv);
        mSendMessgeLl = (LinearLayout) findViewById(R.id.activity_contacts_detial_send_messge_ll);
        mVideoCallLl = (LinearLayout) findViewById(R.id.activity_contacts_detial_video_call_ll);
        mVoiceCallLl = (LinearLayout) findViewById(R.id.activity_contacts_detial_voice_call_ll);

        mSendMessgeLl.setOnClickListener(this);
        mVideoCallLl.setOnClickListener(this);
        mVoiceCallLl.setOnClickListener(this);

        mNameTv.setText(mUserName);
        mDepartmentTv.setText(mCompAndDepart == null ? "" : mCompAndDepart);

    }

    private void initTop() {
        findViewById(R.id.inc_contacts_detail).setBackgroundResource(R.color.white);
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mRightIv = (ImageButton) findViewById(R.id.right_iv);

        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.im_top_bar_back);
        mRightTv.setVisibility(View.GONE);
        mRightIv.setVisibility(View.GONE);
        mTitleTv.setText(R.string.contacts_people_messge);
        mTitleTv.setTextColor(getResources().getColor(R.color.black));

        mLeftIv.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id== R.id.left_iv) {
            //返回
            Intent intent=new Intent(ContactsDetialActivity.this,ActivityMain.class);
            setResult(1000,intent);
            finish();

        }else if (id == R.id.activity_contacts_detial_send_messge_ll){
            //发送信息
             onMessage();
        }else if (id == R.id.activity_contacts_detial_video_call_ll){
            //视频呼叫
             onCallVideo();
        }else if (id == R.id.activity_contacts_detial_voice_call_ll){
            //音频呼叫
            onCallAudio();
        }
    }

    private void onMessage() {
        Bundle bundle = new Bundle();
        List<SipInfo> sipInfoList = new ArrayList<>();
        sipInfoList.add(mResSipInfo);
        bundle.putString(ActivityIm.PARAM_SIPINFO, JSON.toJSONString(sipInfoList));
        ActivityTools.startActivity(ContactsDetialActivity.this, ActivityIm.class, bundle, false);
    }

    private void onCallAudio() {
        ToastUtil.showShort(ContactsDetialActivity.this,"音频呼叫");
    }

    private void onCallVideo() {
        if (mResSipInfo != null) {
            Intent callIntent = new Intent(ContactsDetialActivity.this, ActivityCalling.class);
            callIntent.putExtra("ReceiverId", mResSipInfo.getUserid());
            callIntent.putExtra("ReceiverName", mResSipInfo.getUsername());
            callIntent.putExtra("MediaType", "VideoCall");
            callIntent.putExtra("IsReceive", false);
            startActivity(callIntent);
        }
    }
}
