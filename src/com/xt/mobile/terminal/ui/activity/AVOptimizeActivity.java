package com.xt.mobile.terminal.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.media.MediaUtils;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.FileUtils;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.io.File;
import java.util.UUID;


public class AVOptimizeActivity extends BaseActivity {
    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private UserMessge userMessge;


    private ImageView mVoiceGatherIv;
    private SeekBar mVoiceAddSkb;
    private CheckBox mVoiceDenoiseCb;

    private String duration;
    private boolean isCancel;

    public static final String SoundPath= Environment
            .getExternalStorageDirectory()
            .getAbsolutePath()+"/terminal/Audio";
    private MediaUtils mediaUtils;
    private Chronometer chronometer;
    private boolean isOutTime=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avoptimize);
        userMessge = UserMessge.getInstans(this);
        initView();
        initMedia();
    }

    private void initMedia() {
        //创建音频保存目录
        String fileDir = FileUtils.createFileDir(SoundPath);
        if (fileDir.length()>0){
            mediaUtils = new MediaUtils(this);
            mediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
            mediaUtils.setTargetDir(new File(fileDir));
            mediaUtils.setTargetName(UUID.randomUUID() + ".m4a");
            mVoiceGatherIv.setOnTouchListener(touchListener);
        }else {
            Toast.makeText(this, "检查sd卡是否正常", Toast.LENGTH_SHORT).show();
        }
    }


    private void initView() {
        initTop();
        initControl();
    }

    private void initControl() {
        chronometer = (Chronometer) findViewById(R.id.activity_avoptimizeavoptimize_voice_gather_time_display);
        mVoiceGatherIv = (ImageView) findViewById(R.id.activity_avoptimizeavoptimize_voice_gather_iv);
        mVoiceAddSkb = (SeekBar) findViewById(R.id.activity_avoptimizeavoptimize_voice_add_skb);
        mVoiceDenoiseCb = (CheckBox) findViewById(R.id.activity_avoptimizeavoptimize_voice_denoise_cb);

        chronometer.setVisibility(View.GONE);
        chronometer.setOnChronometerTickListener(tickListener);

        mVoiceAddSkb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //保存位置
                userMessge.setVoiceAdd(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mVoiceAddSkb.setProgress(userMessge.getVoiceAdd());

        boolean voice =userMessge.isVoiceDenoise();
        mVoiceDenoiseCb.setChecked(voice);
        mVoiceDenoiseCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userMessge.setVoiceDenoise(isChecked);
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
        mTitleTv.setText(R.string.audio_resolution);

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
        }

    }


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean ret = false;
            float downY = 0;
            int action = event.getAction();

            if (v.getId() == R.id.activity_avoptimizeavoptimize_voice_gather_iv) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        chronometer.setVisibility(View.VISIBLE);
                        startGatherTime();
                        mediaUtils.record();
                        ret = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        stopGatherTime();
                        chronometer.setVisibility(View.GONE);
                        if (isCancel) {
                            isCancel = false;
                            mediaUtils.stopRecordUnSave();
                            Toast.makeText(AVOptimizeActivity.this, "取消保存", Toast.LENGTH_SHORT).show();
                        } else {
                            int duration = getDuration(chronometer.getText().toString());
                            switch (duration) {
                                case -1:
                                    break;
                                case -2:
                                    mediaUtils.stopRecordUnSave();
                                    Toast.makeText(AVOptimizeActivity.this, "时间太短", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    if (!isOutTime){
                                        mediaUtils.stopRecordSave();
                                        String path = mediaUtils.getTargetFilePath();
                                        Toast.makeText(AVOptimizeActivity.this, "文件以保存至：" + path, Toast.LENGTH_SHORT).show();
                                        //播放
                                        playAudio(path);
                                    }

                                    break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getY();
                        if (downY - currentY > 10) {
                            isCancel = true;
                        } else {
                            isCancel = false;
                        }
                        break;
                }
            }
            return ret;
        }
    };


    private int getDuration(String str) {
        String a = str.substring(0, 1);
        String b = str.substring(1, 2);
        String c = str.substring(3, 4);
        String d = str.substring(4);
        if (a.equals("0") && b.equals("0")) {
            if (c.equals("0") && Integer.valueOf(d) < 1) {
                return -2;
            } else if (c.equals("0") && Integer.valueOf(d) > 1) {
                duration = d;
                return Integer.valueOf(d);
            } else {
                duration = c + d;
                return Integer.valueOf(c + d);
            }
        } else {
            duration = "60";
            return -1;
        }

    }



    Chronometer.OnChronometerTickListener tickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (SystemClock.elapsedRealtime() - chronometer.getBase() > 10 * 1000) {
                isOutTime=true;
                mediaUtils.stopRecordSave();
                Toast.makeText(AVOptimizeActivity.this, "录音超时", Toast.LENGTH_SHORT).show();

                String path = mediaUtils.getTargetFilePath();
                Toast.makeText(AVOptimizeActivity.this, "文件以保存至：" + path, Toast.LENGTH_SHORT).show();
                //播放
                playAudio(path);
            }else {
                isOutTime=false;
            }
        }
    };


    /**
     * 开始记录时间
     */
    private void startGatherTime(){
        if (chronometer !=null){
            mVoiceGatherIv.setImageDrawable(getResources().
                    getDrawable(R.drawable.icon_autdio_pr));
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setFormat("%S");
            chronometer.start();
        }

    }

    /**
     * 结束记录时间
     */
    private void stopGatherTime(){
        if (chronometer !=null){
            mVoiceGatherIv.setImageDrawable(getResources().
                    getDrawable(R.drawable.icon_autdio_nor));
            chronometer.stop();
        }

    }

    /**
     * 播放音频
     * @param url
     */
    private void playAudio(String url){
        MediaPlayer mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
