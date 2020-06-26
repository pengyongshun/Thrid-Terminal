package com.xt.mobile.terminal.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;

public class NotificationSettingActivity extends BaseActivity {

    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private RelativeLayout mQdzdRl;
    private CheckBox mQdzdCb;
    private RelativeLayout mQdlsRl;
    private CheckBox mQdlsCb;
    private UserMessge userMessge;


    private RelativeLayout mSaveLogRl;
    private CheckBox mSaveLogCb;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        userMessge = UserMessge.getInstans(this);
        initView();
    }

    private void initView() {
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mRightIv = (ImageButton) findViewById(R.id.right_iv);
        mQdzdRl = (RelativeLayout) findViewById(R.id.activity_notifcation_setting_qdzd_rl);
        mQdzdCb = (CheckBox) findViewById(R.id.activity_notifcation_setting_qdzd_cb);
        mQdlsRl = (RelativeLayout) findViewById(R.id.activity_notifcation_setting_qdls_rl);
        mQdlsCb = (CheckBox) findViewById(R.id.activity_notifcation_setting_qdls_cb);
        mSaveLogRl = (RelativeLayout) findViewById(R.id.activity_notifcation_setting_save_log_rl);
        mSaveLogCb = (CheckBox) findViewById(R.id.activity_notifcation_setting_save_log_cb);

        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mRightTv.setVisibility(View.GONE);
        mRightIv.setVisibility(View.GONE);
        mTitleTv.setText(R.string.media_setting);

        mLeftIv.setOnClickListener(this);

        boolean voice =userMessge.isOpenVoice();
        boolean vibrate = userMessge.isOpenVibrate();
        boolean saveLog = userMessge.isSaveLog();
        mQdlsCb.setChecked(voice);
        mQdzdCb.setChecked(vibrate);
        mSaveLogCb.setChecked(saveLog);

        mQdzdCb.setOnCheckedChangeListener(mOnCheck);
        mQdlsCb.setOnCheckedChangeListener(mOnCheck);
        mSaveLogCb.setOnCheckedChangeListener(mOnCheck);
    }


    private CompoundButton.OnCheckedChangeListener mOnCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            if (arg0.getId() == R.id.activity_notifcation_setting_qdls_cb) {
                //铃声
                userMessge.setOpenVoice(arg1);
            }else if (arg0.getId() == R.id.activity_notifcation_setting_qdzd_cb) {
                //震动
                userMessge.setOpenVibrate(arg1);
            }else if (arg0.getId() == R.id.activity_notifcation_setting_save_log_cb) {
                //日志
                userMessge.setSaveLog(arg1);
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.left_iv) {
            //返回
            finish();
        }

    }
}
