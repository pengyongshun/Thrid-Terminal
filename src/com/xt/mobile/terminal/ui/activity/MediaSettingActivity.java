package com.xt.mobile.terminal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.XTUtils;

/**
 * 媒体设置
 */
public class MediaSettingActivity extends BaseActivity {
    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private RelativeLayout mFblRl;
    private RelativeLayout mZlRl;
    private RelativeLayout mMlRl;
    private RelativeLayout mYpyhRl;
    private RelativeLayout mTxyhRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_setting);

        initView();
    }

    private void initView() {
        initTop();
        initControl();
    }

    private void initControl() {
        mFblRl = (RelativeLayout) findViewById(R.id.activity_media_setting_fbl_rl);
        mZlRl = (RelativeLayout) findViewById(R.id.activity_media_setting_zl_rl);
        mMlRl = (RelativeLayout) findViewById(R.id.activity_media_setting_ml_rl);
        mYpyhRl = (RelativeLayout) findViewById(R.id.activity_media_setting_ypyh_rl);
        mTxyhRl = (RelativeLayout) findViewById(R.id.activity_media_setting_txyh_rl);

        mFblRl.setOnClickListener(this);
        mZlRl.setOnClickListener(this);
        mMlRl.setOnClickListener(this);
        mYpyhRl.setOnClickListener(this);
        mTxyhRl.setOnClickListener(this);
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
        mTitleTv.setText(R.string.media_setting);

        mLeftIv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (v.getId() == R.id.left_iv) {
            // 返回
            finish();
        }else if (v.getId() == R.id.activity_media_setting_fbl_rl) {
            // 分辨率
            Intent fblIntent=new Intent(MediaSettingActivity.this,
                    ResolutionRatioActivity.class);
            fblIntent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_MEDIA_SETTING_FBL);
            startActivity(fblIntent);
        }else if (v.getId() == R.id.activity_media_setting_zl_rl) {
            // 帧率
            Intent zlIntent=new Intent(MediaSettingActivity.this,
                    ResolutionRatioActivity.class);
            zlIntent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_MEDIA_SETTING_ZL);
            startActivity(zlIntent);
        }else if (v.getId() == R.id.activity_media_setting_ml_rl) {
            // 码率
            Intent mlIntent=new Intent(MediaSettingActivity.this,
                    ResolutionRatioActivity.class);
            mlIntent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_MEDIA_SETTING_ML);
            startActivity(mlIntent);
        }else if (v.getId() == R.id.activity_media_setting_ypyh_rl) {
            // 音视频优化
            ActivityTools.startActivity(MediaSettingActivity.this,AVOptimizeActivity.class,false);
        }else if (v.getId() == R.id.activity_media_setting_txyh_rl) {
            // 图像优化
            ActivityTools.startActivity(MediaSettingActivity.this,
                    PhotoOptimizeActivity.class,false);
        }
    }
}
