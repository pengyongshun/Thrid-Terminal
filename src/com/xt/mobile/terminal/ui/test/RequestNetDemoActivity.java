package com.xt.mobile.terminal.ui.test;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.network.JsonParseUilt;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.util.comm.UserMessge;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1、http请求
 * HTTP_DOMAIN = "https://" + core_ip;
 *
 * 2、wss请求
 * WSS_DOMAIN = "wss://" + core_ip + ":" + core_port + "/websocket";
 */
public class RequestNetDemoActivity extends BaseActivity implements RequestUitl.HttpResultCall {

    private HttpAsyncTask mHttpAsyncTask;
    private TextView mResultTv;
    private UserMessge userMessge;
    private RequestUitl instans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_net_demo);
        mResultTv = (TextView) findViewById(R.id.result_tv);
        mResultTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        userMessge = UserMessge.getInstans(this);
        instans = RequestUitl.getInstans(this,this);



    }


    public void onClick(View view) {
        if (view.getId() == R.id.http_btn) {
            //http请求
            showLoadingDaliog("正在加载中...");
            //sendHttp();
            sendHttpDelet();
        }else if (view.getId() == R.id.wss_btn) {
            //wss请求
            showLoadingDaliog("正在加载中...");
            sendWss();
        }else if (view.getId() == R.id.clear_btn) {
            //清除
            mResultTv.setText("");
        }
    }
    ////////////////////////////////////////wss请求//////////////////////////

    private void sendWss() {
        //设置UserID、UserName、Tokenkey
        WebSocketCommand.getInstance().setmUserId(userMessge.getUserID());
        WebSocketCommand.getInstance().setmUserName(userMessge.getUserName());
        WebSocketCommand.getInstance().setmTokenKey(userMessge.getUserTokenkey());

        //发送请求
        // 用户资源指令
        addPeopleMetting();
        //joinMetting();
       // WebSocketCommand.getInstance().onSendSubscribeDevice();
       // WebSocketCommand.getInstance().onWssMemberJoinMeeting("380439293");

    }

    private void joinMetting() {
        String sceneName="aa";
        String description="";
        boolean isMediaProcessing=false;
        String operatorName="";
        boolean needPassword=false;
        String password="" ;
        String members="[{index:0,userID:\"chenmingjun\", userName:\"陈明军\", resourceType:\"User\"}]";
        WebSocketCommand.getInstance().onWssCreateMeeting(sceneName,description,
                isMediaProcessing,operatorName,needPassword,password,members);
    }


    private void addPeopleMetting() {
        showLoadingDaliog("正在加载中...");
        String members="[{index:0,userID:\"chenmingjun\", userName:\"陈明军\", resourceType:\"User\"}]";
//        WebSocketCommand.getInstance().onWssAddMembers1("06c763e9-ef19-4f17-9879-edac626a6281",members);
//
    }


//    /**
//     * wss请求返回的结果
//     * @param msg
//     */
//    protected void onReceiveWssMessage(String msg) {
//        hideLoadingDialog();
//        if (msg.isEmpty()) {
//            return;
//        } else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {
//            mResultTv.setText(msg);
//            PLog.d("==========WSS_CLOSE_TIME msg===》"+msg);
//        } else if (msg.indexOf(WssContant.WSS_INFORM_INIT_MEDIA) >= 0) {
//            //初始化媒体服务（即获取SIP消息）
//            mResultTv.setText(msg);
//            PLog.d("==========WSS_INFORM_INIT_MEDIA msg===》"+msg);
//        } else if (msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0 ||
//                msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0) {
//            boolean people= msg.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0;
//            boolean device= msg.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0;
//            // 用户资源指令 OrganizationUser
//            if (people){
//                mResultTv.setText(msg);
//            }else if (device){
//                mResultTv.setText(msg);
//            }
//
//
//        }
//    }

    /**
     * wss请求返回的结果
     * @param msg
     */
    protected void onReceiveWssMessage(String msg) {
        hideLoadingDialog();
        if (msg.isEmpty()) {
            return;
        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {

            if (msg.indexOf("sceneID") >= 0){
                //代表是创建会议后直接加入会议
                JoinMettingBean bean = JsonParseUilt.getMettingListBeen(msg);
                if (bean !=null){
                    String sceneID = bean.getSceneID();
                    mResultTv.setText(sceneID);
                }

            }

        }
    }

    ////////////////////////////////////////////网络请求///////////////////

    /**
     * 发请求
     */
    public void sendHttp() {
        //参数
        Map<String,String> map=new HashMap<>();
        map.put("userID",mUserId);
        map.put("password","123456");
        map.put("vcode","5hrw");
        List<NameValuePair> params = MoudleParams.getloginParams(this, map);
        //请求
        showLoadingDaliog("正在加载中...");
        if (instans !=null){
            instans.sendRequest(params,true,1001);
        }

    }

    /**
     * 移除会议
     */
    public void sendHttpDelet() {
        //参数
        Map<String,String> map=new HashMap<>();
        map.put("tokenKey",UserMessge.getInstans(RequestNetDemoActivity.this).
                getUserTokenkey());
        map.put("groupID","123456saff");
        List<NameValuePair> params = MoudleParams.getDeletGroupMeetingParams(this, map);
        //请求
        showLoadingDaliog("正在加载中...");
        if (instans !=null){
            instans.sendRequest(params,true,1001);
        }

    }

    /**
     * 网络请求接收的结果
     * @param tag
     * @param result
     */
    @Override
    public void success(int tag, String result) {

        switch (tag){
            case 1001:
                //登陆
                hideLoadingDialog();
                showToast("成功访问");
                mResultTv.setText(result);
                break;
        }

    }
    @Override
    public void faile(int tag, String error) {
        switch (tag){
            case 1001:
                //登陆
                hideLoadingDialog();
                showToast("访问失败");
                mResultTv.setText(error);
                break;
        }

    }

}
