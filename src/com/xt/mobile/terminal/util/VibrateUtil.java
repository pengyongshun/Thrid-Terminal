package com.xt.mobile.terminal.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by 彭永顺 on 2020/6/24.
 */
public class VibrateUtil {
    /**
     * 让手机振动milliseconds毫秒
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vib.hasVibrator()){  //判断手机硬件是否有振动器
            vib.vibrate(milliseconds);
        }
    }

    /**
     * 让手机以我们自己设定的pattern[]模式振动
     * long pattern[] = {1000, 20000, 10000, 10000, 30000};
     */
    public static void vibrate(Context context, long[] pattern, int repeat){
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vib.hasVibrator()){
            vib.vibrate(pattern,repeat);
        }
    }

    /**
     * 取消震动
     */
    public static void virateCancle(Context context){
        //关闭震动
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }



    //////////////////////////////////////////使用/////////////////////////////
//    // 开启震动
//    isVirating = true;
//    VirateUtil.virate(context, new long[]{100, 200, 100, 200}, 0)
//
//    //关闭震动
//    if (isVirating) {//防止多次关闭抛出异常，这里加个参数判断一下
//        isVirating = false;
//        VirateUtil.virateCancle(context);
//    }


    ////////////////////////////////////////////////加入权限/////////////////////////
//    <uses-permission android:name="android.permission.VIBRATE" />

}
