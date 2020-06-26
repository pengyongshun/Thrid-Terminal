package com.xt.mobile.terminal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.log.LogServiceManager;
import com.xt.mobile.terminal.log.MyUncaughtExceptionHandler;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.ServerInitCallback;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.util.ToolPhone;
import com.xtmedia.xtview.GlRenderNative;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by 彭永顺 on 2020/6/8.
 */
public class InitXTMobileLibrary {
    private static InitXTMobileLibrary initXTMobileLibrary;
    private SharedPreferences sp;
    private Context context;


    ////////////单例模式//////////////////////////////////////
    public static InitXTMobileLibrary getInstans(Context context){
        if (null==initXTMobileLibrary){
            initXTMobileLibrary=new InitXTMobileLibrary(context);
        }
        return initXTMobileLibrary;
    }
    private InitXTMobileLibrary(Context context){
        this.context=context;
        if (null==sp){
            sp = context.getSharedPreferences(
                    ConstantsValues.DEFAULT_SP,
                    context.MODE_PRIVATE);
        }

    }
    public void init(){
        //ignoreSSLHandshake();
        initMedia();
        getDeviceInfo();

        Intent intent1 = new Intent(context, VideoService.class);
        context.startService(intent1);
        Intent intent2 = new Intent(context, CaptureVideoService.class);
        context.startService(intent2);

        //开启日志服务
        LogServiceManager.getInstance(context).startLogService();
        // 设置异常捕获
        installExceptionCatch();

    }


    private void installExceptionCatch() {
        MyUncaughtExceptionHandler uncaughtExceptionHandler = new MyUncaughtExceptionHandler(
                Thread.getDefaultUncaughtExceptionHandler(),context);
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

    }

    private void initMedia() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // ****************************可修改的参数*********************************
                    // 接收端丢包重传
                    boolean receivePackageResent = sp.getBoolean(
                            ConstantsValues.RECEIVE_PACKAGE_RESENT, false);
                    // 发送端丢包重传
                    boolean sendPackageResent = sp.getBoolean(ConstantsValues.SEND_PACKAGE_RESENT,
                            false);
                    // mtu
                    String mtu = sp.getString(ConstantsValues.MTU,
                            ConstantsValues.DEFAULT_MTU_VALUE);
                    // resendAu
                    String resendAu = sp.getString(ConstantsValues.RESENDAU,
                            ConstantsValues.DEFAULT_RESENDAU_VALUE);
                    // wait_resend
                    String waitResend = sp.getString(ConstantsValues.WAIT_RESEND,
                            ConstantsValues.DEFAULT_WAITRESEND_VALUE);
                    // maxResend
                    String maxResend = sp.getString(ConstantsValues.MAX_RESEND,
                            ConstantsValues.DEFAULT_MAXRESEND_VALUE);
                    // priority
                    String priority = sp.getString(ConstantsValues.PRIORITY,
                            ConstantsValues.DEFAULT_PRIORITY_VALUE);
                    // priorityCache
                    String priorityCache = sp.getString(ConstantsValues.PRIORITY_CACHE,
                            ConstantsValues.DEFAULT_PRIORITY_CACHE_VALUE);
                    // 帧缓冲大小
                    String frameCache = sp.getString(ConstantsValues.FRAME_CACHE,
                            ConstantsValues.DEFAULT_FRAME_CACHE_VALUE);
                    // 包缓冲大小
                    String packageCache = sp.getString(ConstantsValues.PACKAGE_CACHE,
                            ConstantsValues.DEFAULT_PKT_CACHE_VALUE);

                    // 音视频同步
                    boolean isSync = sp.getBoolean(ConstantsValues.SYNC,
                            ConstantsValues.SYNC_DEFAULT);

                    if (receivePackageResent) {
                        GlRenderNative.PutRouterCfg(ConstantsValues.RECEIVE_RESEND_KEY, "1");
                    } else {
                        GlRenderNative.PutRouterCfg(ConstantsValues.RECEIVE_RESEND_KEY, "1");
                    }
                    if (sendPackageResent) {
                        GlRenderNative.PutRouterCfg(ConstantsValues.SEND_RESEND_KEY, "1");
                    } else {
                        GlRenderNative.PutRouterCfg(ConstantsValues.SEND_RESEND_KEY, "0");
                    }
                    GlRenderNative.PutRouterCfg(ConstantsValues.MTU_KEY, mtu);
                    GlRenderNative.PutRouterCfg(ConstantsValues.RESENDAU_KEY, resendAu);
                    GlRenderNative.PutRouterCfg(ConstantsValues.WAITRESEND_KEY, waitResend);
                    GlRenderNative.PutRouterCfg(ConstantsValues.MAXRESEND_KEY, maxResend);
                    GlRenderNative.PutRouterCfg(ConstantsValues.RESEND_PRIORITY_KEY, priority);
                    GlRenderNative.PutRouterCfg(ConstantsValues.RESEND_PRIORITY_CACHE_KEY,
                            priorityCache);
                    GlRenderNative.PutRouterCfg(ConstantsValues.FRAME_CACHE_KEY, frameCache);
                    GlRenderNative.PutRouterCfg(ConstantsValues.PKT_CACHE_KEY, packageCache);

                    // 设置音视频同步
                    int syncMode = ConstantsValues.SYNC_MODE_RTCP;
                    int syncCacheTime = ConstantsValues.DEFAULT_SYNC_CACHE_TIME;
                    if (isSync) {
                        syncMode = sp.getInt(ConstantsValues.SYNC_MODE,
                                ConstantsValues.SYNC_MODE_FRAME_RATE);
                        syncCacheTime = sp.getInt(ConstantsValues.SYNC_CACHE_TIME,
                                ConstantsValues.DEFAULT_SYNC_CACHE_TIME);
                    }
                    GlRenderNative.setSyncInfo(isSync, syncMode, syncCacheTime);

                    // **************************不可修改的固定值参数******************************
                    // RTSP心跳时间
                    GlRenderNative.PutRouterCfg("config.rtsp_config.ping_timeout", "3000");
                    // ??
                    GlRenderNative.PutRouterCfg(ConstantsValues.PACKRESEND_KEY,
                            ConstantsValues.DEFAULT_PACKRESEND_VALUE);
                    // 码率控制？
                    GlRenderNative.PutRouterCfg(ConstantsValues.BITRATE_CONTROLLER_KEY,
                            ConstantsValues.DEFAULT_BITRATE_CONTROLLER);
                    // 丢包重传帧缓冲
                    GlRenderNative.PutRouterCfg(ConstantsValues.WAIT_FRAMES_KEY,
                            ConstantsValues.DEFAULT_WAITfRAMES_VALUE);
                    GlRenderNative.SetAuidoplaybackstreamtype(AudioManager.STREAM_MUSIC);
                    GlRenderNative.mediaClientInit("0.0.0.0", 19901, 40000, 32);
                    ServerInitCallback cb = new ServerInitCallback();
//					GlRenderNative.mediaServerInit(32, "0.0.0.0", 40050, 20000, 1554, 20001, true,
//							ConstantsValues.isReport, cb);
                    GlRenderNative.mediaServerInit(32, "0.0.0.0", 40050, 0, 1554, 0, true,
                            ConstantsValues.isReport, cb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getDeviceInfo() {
        int size[] = ToolPhone.getInstance(context).getScreenSize();
        ConstantsValues.ScreenWidth = size[0];
        ConstantsValues.ScreenHeight = size[1];

        int sdkVersion = ToolPhone.getInstance(context).getDeviceSDKVersion();
        ConstantsValues.SdkVersion = sdkVersion;

        String model = ToolPhone.getInstance(context).getDeviceModel();
        ConstantsValues.DeviceModel = model;

        String brand = ToolPhone.getInstance(context).getDeviceBrand();
        ConstantsValues.DeviceBrand = brand;

        String display = ToolPhone.getInstance(context).getDeviceDisplay();
        ConstantsValues.DeviceDisplay = display;

        String product = ToolPhone.getInstance(context).getDeviceProduct();
        ConstantsValues.DeviceProduct = product;
    }


    /**
     * 忽略证书
     */
    public static void ignoreSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
}
