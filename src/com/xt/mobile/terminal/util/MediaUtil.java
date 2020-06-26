package com.xt.mobile.terminal.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Created by 彭永顺 on 2020/6/24.
 */
public class MediaUtil {
    private static MediaPlayer mMediaPlayer;

    //开始播放
    public static void playRing(Context context){
        try {
            //用于获取手机默认铃声的Uri
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, alert);
            //告诉mediaPlayer播放的是铃声流
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止播放
    public static void stopRing(){
        if (mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    }



    //////////////////////////////////////////使用/////////////////////////////
//    //开启
//    MediaUtil.playRing(context);
//
//    //关闭
//    MediaUtil.stopRing();


    ////////////////////////////////////////////////加入权限/////////////////////////
//    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    //Android设置响铃铃需要更改系统设置，所以在6.0以后提高了权限级别，需要使用 WRITE_SETTINGS 权限才能进行响铃铃设置。
    //记得针对6.0设备，进行动态获取权限。

}
