package com.xt.mobile.terminal.ui.activity;


import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.ui.BaseActivity;

/**
 * 通讯录/设备信息/视频监控
 */
public class VedioControlDetialActivity extends BaseActivity {

    private SurfaceView mSurfaceView;
    private ImageView mMenuVoiceIv;
    private ImageView mMenuCouldControlIv;
    private TextView mNameTv;
    private TextView mDepartmentTv;
    private TextView mCloseTv;
    private LinearLayout mMenuLl;


    private LinearLayout mBigMenuLl;
    private ImageView mBigMenuIv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_control_detial);
        initView();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.activity_vedio_control_detial_sfv);
        mMenuVoiceIv = (ImageView) findViewById(R.id.activity_vedio_control_detial_menu_voice_iv);
        mMenuCouldControlIv = (ImageView) findViewById(R.id.activity_vedio_control_detial_menu_could_control_iv);
        mNameTv = (TextView) findViewById(R.id.activity_vedio_control_detial_name_tv);
        mDepartmentTv = (TextView) findViewById(R.id.activity_vedio_control_detial_department_tv);
        mCloseTv = (TextView) findViewById(R.id.activity_vedio_control_detial_close_tv);

        mMenuLl = (LinearLayout) findViewById(R.id.activity_vedio_control_detial_menu_ll);

        //点击云台控制后的布局
        mBigMenuLl = (LinearLayout) findViewById(R.id.activity_vedio_control_detial_big_menu_ll);
        mBigMenuIv = (ImageView) findViewById(R.id.activity_vedio_control_detial_big_menu_iv);
    }
}
