package com.xt.mobile.terminal.log;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 类名 ：TchatUncaughtExceptionHandler
 */
public class MyUncaughtExceptionHandler implements
        UncaughtExceptionHandler {
    private UncaughtExceptionHandler mDefHandler = null;
    private Context context;

    public MyUncaughtExceptionHandler(UncaughtExceptionHandler def ,Context context) {
        mDefHandler = def;
        this.context=context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String exceptionInfor = Log.getStackTraceString(ex)
                + getBuildString();

        // 记录崩溃信息
        PLog.toCrashFile(exceptionInfor
                + "\r\n.........................................App end.........................................\r\n\r\n");
        // 停止日志记录
        LogServiceManager.getInstance(context).stopLogService();

        //异常退出清除人员状态
        String loginStatus = UserMessge.getInstans(context).getLoginStatus();
        if (loginStatus.equals("0")){
            //在线  进行下线处理
            WebSocketCommand.getInstance().onSendClearUserStatus();
            //清理缓存的数据
            UserMessge.getInstans(context).clearData();
        }

    }


    /**
     * added by chenwei record crash log field
     *
     * @return
     */
    public static String getBuildString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n\r\n------Build------\r\n")
                .append("BOARD = " + Build.BOARD + "\r\n")
                .append("BRAND = " + Build.BRAND + "\r\n")
                .append("CPU_ABI = " + Build.CPU_ABI + "\r\n")
                .append("DEVICE = " + Build.DEVICE + "\r\n")
                .append("DISPLAY = " + Build.DISPLAY + "\r\n")
                .append("FINGERPRINT = " + Build.FINGERPRINT + "\r\n")
                .append("HOST = " + Build.HOST + "\r\n")
                .append("ID = " + Build.ID + "\r\n")
                .append("MANUFACTURER = " + Build.MANUFACTURER + "\r\n")
                .append("PRODUCT = " + Build.PRODUCT + "\r\n")
                .append("TAGS = " + Build.TAGS + "\r\n")
                .append("TYPE = " + Build.TYPE + "\r\n")
                .append("USER = " + Build.USER + "\r\n")
                .append("TIME = " + Build.TIME + "\r\n")
                .append("MODEL = " + Build.MODEL + "\r\n");

        sb.append("\r\n------Build.VERSION------\r\n")
                .append("CODENAME = " + Build.VERSION.CODENAME + "\r\n")
                .append("INCREMENTAL = " + Build.VERSION.INCREMENTAL + "\r\n")
                .append("RELEASE = " + Build.VERSION.RELEASE + "\r\n")
                .append("SDK = " + Build.VERSION.SDK + "\r\n")
                .append("SDK_INT = " + Build.VERSION.SDK_INT + "\r\n");
        return sb.toString();
    }
}