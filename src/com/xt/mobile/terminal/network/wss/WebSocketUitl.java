package com.xt.mobile.terminal.network.wss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.comm.UserMessge;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/16.
 */
public class WebSocketUitl {
    private Activity context;
    private static WebSocketUitl webSocketUitl;
    private  RequestUitl requestUitl;
    private RequestUitl.HttpResultCall httpResultCall;

    ////////////单例模式//////////////////////////////////////
    public static WebSocketUitl getInstans(Activity context, RequestUitl.HttpResultCall httpResultCall){
        if (null==webSocketUitl){
            webSocketUitl=new WebSocketUitl(context,httpResultCall);
        }
        return webSocketUitl;
    }
    private WebSocketUitl(Activity context , RequestUitl.HttpResultCall httpResultCall){
        this.context=context;
        this.httpResultCall=httpResultCall;
        if (requestUitl ==null){
            requestUitl = RequestUitl.getInstans(context,httpResultCall);
        }

    }
    /**
     *
     * 启动wss，且设置url
     */
    public void startJWebSClientService() {

        Intent intent = new Intent(context, IWebSocketClientService.class);
        intent.putExtra("WSS_SERVER_URL", ConstantsValues.getWssBase(context));
        context.startService(intent);
    }

    /**
     * 停止wss
     */
    public void stopJWebSClientService(String mTokenKey) {
        UserMessge.getInstans(context).setLoginStatus("1");
        WebSocketCommand.getInstance().onSendClearUserStatus();
        WebSocketCommand.getInstance().onSendExit();
        //退出登陆
       // WebSocketCommand.getInstance().onOutLogin();
        if (mTokenKey != null && !mTokenKey.isEmpty()) {
            List<NameValuePair> params = MoudleParams.getDestroyTokenParams(context,mTokenKey);
            if (requestUitl !=null){
                requestUitl.sendRequest(params,true,Constants.HTTP_GET_LOGIN_DESTROYTOKEN);
            }

        } else {
            ToolLog.i("===ActivityLogin::stopJWebSClientService (mTokenKey isEmpty)");
        }

        Intent intent = new Intent(context, IWebSocketClientService.class);
        context.stopService(intent);
        //清理缓存的数据
        UserMessge.getInstans(context).clearData();
    }
}
