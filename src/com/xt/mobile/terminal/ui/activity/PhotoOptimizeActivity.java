package com.xt.mobile.terminal.ui.activity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.LocalMedia;
import com.xt.mobile.terminal.view.XTMediaPlay;

public class PhotoOptimizeActivity extends BaseActivity {
    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private SurfaceView mPreviewSfv;
    private SeekBar mPhotoZqSkb;
    private SeekBar mHzZqSkb;
    private XTMediaPlay mXTremoteMediaPlay;
    private LocalMedia localMedia;
    private boolean startPreview=true;
    private UserMessge userMessge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_optimize);
        userMessge = UserMessge.getInstans(this);
        initView();
    }


    private void initView() {
        initTop();
        initControl();
        if (localMedia == null) {
            localMedia = new LocalMedia(this, mPreviewSfv);
        }

    }


    private void initControl() {
        mPreviewSfv = (SurfaceView) findViewById(R.id.activity_photooptimizeavoptimize_preview_sfv);
        mPhotoZqSkb = (SeekBar) findViewById(R.id.activity_photooptimizeavoptimize_photo_zq_skb);
        mHzZqSkb = (SeekBar) findViewById(R.id.activity_photooptimizeavoptimize_hz_zq_skb);

        int photoZQ = userMessge.getPhotoZQ();
        int tzZQ = userMessge.getTzZQ();
        mPhotoZqSkb.setProgress(photoZQ);
        mHzZqSkb.setProgress(tzZQ);

        mHzZqSkb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //保存位置
                userMessge.setTzzq(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mPhotoZqSkb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //保存位置
                userMessge.setPhotoZQ(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        mTitleTv.setText(R.string.photo_resolution);

        mLeftIv.setOnClickListener(this);

    }


    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }

    @Override
    public void onResume() {
        startPreview();
        super.onResume();

    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (startPreview) {
            startPreview = false;
            localMedia.stopPreview();
            mPreviewSfv.setBackgroundResource(R.drawable.playwindow_background);
        }
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (localMedia != null) {
            startPreview = true;
            localMedia.startPreview();
        }
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
        }
    }

}
