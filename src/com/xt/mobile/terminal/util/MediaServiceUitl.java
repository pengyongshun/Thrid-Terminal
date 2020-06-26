package com.xt.mobile.terminal.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.wss.IWebSocketClientService;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.sipcapture.CaptureVideoService;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xtmedia.port.SendPort;
import com.xtmedia.xtview.GlRenderNative;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/5.
 * 登陆成功后初始化媒体，发送心跳包，然后创建端口
 * 退出登陆后 销毁窗口
 */
public class MediaServiceUitl {

    private Activity context;
    private static MediaServiceUitl mediaServiceUitl;
    private HttpAsyncTask mHttpAsyncTask;
    private UserMessge userMessge;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(mRunnable, 30000);
            onRenewUserToken();
        }
    };
    private HttpAsyncTask mRemoveTokenAsyncTask;


    ////////////单例模式//////////////////////////////////////
    public static MediaServiceUitl getInstans(Activity context){
        if (null==mediaServiceUitl){
            mediaServiceUitl=new MediaServiceUitl(context);
        }
        return mediaServiceUitl;
    }
    private MediaServiceUitl(Activity context ){
        this.context=context;
        if (userMessge ==null){
            userMessge = UserMessge.getInstans(context);
        }

    }

    /**
     * 只有在登陆成功后才会进行初始化
     */
    public void initMediaService(String userId , String userName , String token){
        WebSocketCommand.getInstance().setmUserId(userId);
        WebSocketCommand.getInstance().setmUserName(userName);
        WebSocketCommand.getInstance().setmTokenKey(token);

        WebSocketCommand.getInstance().onSendHeart();
        WebSocketCommand.getInstance().onSendJoin();
        WebSocketCommand.getInstance().onSendAddUserStatus();
        WebSocketCommand.getInstance().onSendInitMobileTerminal();
        //上线
        UserMessge.getInstans(context).setLoginStatus("0");
//        WebSocketCommand.getInstance().onSendInitMedia();
    }

    /**
     * 创建发送端口
     */
    public void createSendPort(){
        try {

            ToolLog.i("==========================create send port===111===");
            // 创建全局唯二的两个发送端口
            SendPort.createSendPort();

            ToolLog.i("==========================create send port===222===");
            //GlRenderNative.setPlayStatusEnable(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //做保活机制
        mHandler.postDelayed(mRunnable, 30000);

        Intent intent = new Intent(VideoService.ACTION_START_CAPTURE);
        context.sendBroadcast(intent);
    }

    /**
     * 销毁发送端口
     */
    public void destorySendPort(){
        stopJWebSClientService();

        SipManager.media_map.clear();
        SipManager.localMedia.clear();
        ConfigureParse.reset();
        SipManager.unregister();
        SendPort.destroysendPort();// 销毁发送端口
        mHandler.removeCallbacksAndMessages(null);
        XTUtils.onReleaseMsgSound();

        Intent intent = new Intent(VideoService.ACTION_STOP_CAPTURE);
        context.sendBroadcast(intent);
        Intent intent1 = new Intent(CaptureVideoService.ACTION_CAPTURE_SIP_UNINIT);
        context.sendBroadcast(intent1);
    }


    /**
     * 更新用户鉴权，与websocket的732消息一起链路保活
     */
    private void onRenewUserToken() {
        //参数
        String tokenkey = UserMessge.getInstans(context).getUserTokenkey();
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("operatorToken", tokenkey));
        paramsList.add(new BasicNameValuePair("minutes", "1"));
        //对参数进行格式化
        String params = URLEncodedUtils.format(paramsList, HTTP.UTF_8);
        //url
        String url= ConstantsValues.getHttpUrl(context, NetUrl.
                HTTP_RenewUserToken);
        String path = url+"?" + params;
        RenewUserTokenCallBack jsonCallback = new RenewUserTokenCallBack();
        mHttpAsyncTask = new HttpAsyncTask(context, path, null, jsonCallback, true, true);
        mHttpAsyncTask.execute("");
    }

    private class RenewUserTokenCallBack implements HttpAsyncTask.HttpCallBack{

        @Override
        public void setResult(String result) {
            //销毁异步任务
            if (mHttpAsyncTask != null && !mHttpAsyncTask.isCancelled()) {
                mHttpAsyncTask.cancel(true);
                mHttpAsyncTask = null;
            }

            if ((!result.equals("errorNet") && !result.contentEquals("errorData"))||
                    result.contains("success")) {
                //成功
            } else {
                //异常
            }
        }
    }


    private void stopJWebSClientService() {
        //下线
        UserMessge.getInstans(context).setLoginStatus("1");
        WebSocketCommand.getInstance().onSendClearUserStatus();
        WebSocketCommand.getInstance().onSendExit();
        //退出登陆
       // WebSocketCommand.getInstance().onOutLogin();
        SharedPreferences userSp = userMessge.getUserSp();
        String tokenKey = userSp.getString(ConstantsValues.TOKENKEY, null);
        if (!tokenKey.isEmpty()) {
            onRemoveTokenKey();
        } else {
            ToolLog.i("===ActivityMain::stopJWebSClientService (mTokenKey isEmpty)");
        }

        Intent intent = new Intent(context, IWebSocketClientService.class);
        context.stopService(intent);

        //清理缓存的数据
        UserMessge.getInstans(context).clearData();
    }


    /**
     * 退出登陆
     */
    private void onRemoveTokenKey() {
        SharedPreferences userSp = userMessge.getUserSp();
        String tokenKey = userSp.getString(ConstantsValues.TOKENKEY, null);
        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_DestroyToken)+tokenKey;
        removeCallback jsonCallback = new removeCallback();
        mRemoveTokenAsyncTask = new HttpAsyncTask(context, url, null, jsonCallback, true, true);
        mRemoveTokenAsyncTask.execute("");
    }

    private class removeCallback implements HttpAsyncTask.HttpCallBack {

        @Override
        public void setResult(String result) {
            // TODO Auto-generated method stub
            if (mRemoveTokenAsyncTask != null && !mRemoveTokenAsyncTask.isCancelled()) {
                mRemoveTokenAsyncTask.cancel(true);
                mRemoveTokenAsyncTask = null;
            }
        }
    }

}
