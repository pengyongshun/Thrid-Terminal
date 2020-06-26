package com.xt.mobile.terminal.network.http;

import android.content.Context;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.thridpart.WelcomeActivity;
import com.xt.mobile.terminal.util.comm.UserMessge;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 每个功能模块的参数
 * Created by 彭永顺 on 2020/5/15.
 */
public class MoudleParams {

    /**
     * 将客户的userID转为兴图的userID 批量
     * @param context
     * @param kxUserID
     * @return
     */
    public static List<NameValuePair> getXTUserIdParams(Context context,String kxUserID){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (kxUserID !=null ){
            //参数
            String coreIP = UserMessge.getInstans(context).getCoreIP();
            String corePort = UserMessge.getInstans(context).getCorePort();
            String url="https://"+coreIP+":"+corePort+NetUrl.HTTP_GET_XT_USERID;
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("KXUserID", kxUserID));

        }
        return paramsList;

    }
    /**
     * 将兴图的userID转为客户的userID
     * @param context
     * @param xtUserID
     * @return
     */
    public static List<NameValuePair> getKXUserIdParams(Context context,String xtUserID){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (xtUserID !=null ){
            //参数
            String coreIP = UserMessge.getInstans(context).getCoreIP();
            String corePort = UserMessge.getInstans(context).getCorePort();
            String url="https://"+coreIP+":"+corePort+NetUrl.HTTP_GET_KX_USERIDS;
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("userIds", xtUserID));

        }
        return paramsList;

    }


    /**
     * 将创建的会议保存
     * @param context
     * @param params
     * @return
     */
    public static List<NameValuePair>saveMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (params !=null ){
            String conferenceId = params.get("conferenceId");
            String userId = params.get("userId");
            String roleType = params.get("roleType");
            //参数
            String coreIP = UserMessge.getInstans(context).getCoreIP();
            String corePort = UserMessge.getInstans(context).getCorePort();
            String url="https://"+coreIP+":"+corePort+NetUrl.HTTP_POST_SAVE_MEETING;
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("conferenceId", conferenceId));
            paramsList.add(new BasicNameValuePair("userId", userId));
            paramsList.add(new BasicNameValuePair("roleType", roleType));
        }
        return paramsList;

    }


    /**
     * 删除会议
     * @param context
     * @param params
     * @return
     */
    public static List<NameValuePair>deletMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (params !=null ){
            String userId = params.get("userId");
            //参数
            String coreIP = UserMessge.getInstans(context).getCoreIP();
            String corePort = UserMessge.getInstans(context).getCorePort();
            String url="https://"+coreIP+":"+corePort+NetUrl.HTTP_POST_DELET_MEETING;
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("userId", userId));

        }
        return paramsList;

    }

    /**
     * 登陆的参数
     * @param context
     * @param params
     * @return
     */
    public static List<NameValuePair> getloginParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (params !=null ){
                String userID = params.get("userID");
                String password = params.get("password");
                String vcode = params.get("vcode");
            //参数

            String url= ConstantsValues.getHttpUrl(context,NetUrl.
                    HTTP_CreateToken);
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("userID", userID));
            paramsList.add(new BasicNameValuePair("password", password));
            paramsList.add(new BasicNameValuePair("ipAddress", WelcomeActivity.coreIp));
            paramsList.add(new BasicNameValuePair("type", "0"));
            paramsList.add(new BasicNameValuePair("vcode", vcode));

        }
        return paramsList;

    }


    /**
     * 获取登陆验证码的参数
     * @param context
     * @return
     */
    public static List<NameValuePair> getLoginAuthcodeParams(Context context){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            String url= ConstantsValues.getHttpUrl(context,NetUrl.
                    HTTP_Authcode);
            //地址
            paramsList.add(new BasicNameValuePair("url", url));

        return paramsList;

    }

    /**
     * 退出登陆的参数
     * @param context
     * @return
     */
    public static List<NameValuePair> getDestroyTokenParams(Context context,String mTokenKey){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_DestroyToken)+mTokenKey;
        //地址
        paramsList.add(new BasicNameValuePair("url", url));

        return paramsList;

    }


    /**
     * 查询设备的参数
     * @param context
     * @param params
     * @return
     */
    public static List<NameValuePair> getQueryDeviceParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (params !=null ){
            //需要填写的参数
            String beginIndex = params.get("beginIndex");
            String count = params.get("count");
            String operatorToken = params.get("operatorToken");
            //参数

            String url= ConstantsValues.getHttpUrl(context,NetUrl.
                    HTTP_QueryDeviceList);
            //地址
            paramsList.add(new BasicNameValuePair("url", url));
            //参数
            paramsList.add(new BasicNameValuePair("deviceType", "SIPEncoder"));
            paramsList.add(new BasicNameValuePair("beginIndex", beginIndex));
            paramsList.add(new BasicNameValuePair("count", count));
            paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));

        }
        return paramsList;

    }

    public static List<NameValuePair> getQueryBindDevice(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (params !=null ){
            String operatorToken = params.get("operatorToken");
            String url= ConstantsValues.getHttpUrl(context,NetUrl.
                    HTTP_QueryBindDevice);
            paramsList.add(new BasicNameValuePair("url", url));
            paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        }
        return paramsList;
    }

//    /**
//     * 注册登陆新的用户的参数
//     * @param context
//     * @param params
//     * @return
//     */
//    public static List<NameValuePair> getRenewUserTokenParams(Context context,Map<String,String> params){
//        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
//        if (params !=null ){
//            //需要填写的参数operatorToken
//            String operatorToken = params.get("operatorToken");
//            //参数
//
//            String url= ConstantsValues.getHttpUrl(context,NetUrl.
//                    HTTP_RenewUserToken);
//            //地址
//            paramsList.add(new BasicNameValuePair("url", url));
//            //参数
//            paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
//            paramsList.add(new BasicNameValuePair("minutes", "1"));
//
//        }
//        return paramsList;
//
//    }

    /**
     * 分组会议列表
     * 需要的参数 operatorToken=""""&&type=""""&&beginIndex=0&&count=10000
     * @param context
     * @param params   有问题
     * @return
     */
    public static List<NameValuePair> getGroupMeetingListParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        String operatorToken = params.get("tokenKey");
        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_GetMeetingGroupList);

        //地址
        paramsList.add(new BasicNameValuePair("url", url));
        //参数
        paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        paramsList.add(new BasicNameValuePair("type", "Meeting"));
        paramsList.add(new BasicNameValuePair("beginIndex", "0"));
        paramsList.add(new BasicNameValuePair("count", "10000"));
        return paramsList;
    }


    /**
     * 添加会议分组
     * @param context
     * @param params  operatorToken=""""&&obj={..GroupmettingAdd..}
     * @return    有问题
     */
    public static List<NameValuePair> getAddGroupMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        //参数
        String operatorToken = params.get("tokenKey");
        String json = params.get("obj");
        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_AddMeetingGroup);
        //地址
        paramsList.add(new BasicNameValuePair("url", url));
        //参数
       // json="{\"isDefaultScheme\":\"false\",\"description\":\"你以为\",\"groupName\":\"会议名称\",\"isMediaProcessing\":\"false\",\"meetingDevices\":[{\"deviceID\":\"\",\"index\":0}],\"meetingUsers\":[{\"index\":0,\"spectator\":\"false\",\"userID\":\"wangke\"}],\"needPassword\":true,\"password\":\"123456\",\"schemeID\":\"\"}";
        paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        paramsList.add(new BasicNameValuePair("obj", json));
        return paramsList;
    }

    /**
     * 删除会议分组
     * @param context
     * @param  params  operatorToken=""""&&groupIDs=["""","""",""']
     * @return
     */
    public static List<NameValuePair> getDeletGroupMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        //参数
        String operatorToken = params.get("tokenKey");
        String groupID = params.get("groupID");
        //url
        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_DeleteMeetingGroup);
        paramsList.add(new BasicNameValuePair("url", url));
        paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        paramsList.add(new BasicNameValuePair("groupIDs", groupID));
        return paramsList;
    }


    /**
     * 编辑会议分组
     * @param context
     * @param  params operatorToken=""""&&obj={..GroupmettingAdd..}
     * @return
     */
    public static List<NameValuePair> getEditGroupMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        //参数
        String operatorToken = params.get("tokenKey");
        String json = params.get("obj");

        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_EditMeetingGroup);
        paramsList.add(new BasicNameValuePair("url", url));
       // json="{\"isDefaultScheme\":false,\"description\":\"一下子在意我\",\"groupID\":\"82794694-47c9-38c6-92bb-0b2602192ecf\",\"groupName\":\"你好\",\"isMediaProcessing\":false,\"meetingDevices\":[],\"meetingUsers\":[],\"needPassword\":false,\"password\":\"\",\"schemeID\":\"\"}";
        paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        paramsList.add(new BasicNameValuePair("obj", json));
        return paramsList;
    }

    /**
     * 查询单个会议分组详情
     * @param context
     * @param params operatorToken=""""&&groupID=""""
     * @return
     */
    public static List<NameValuePair> getDetialGroupMeetingParams(Context context,Map<String,String> params){
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        //参数
        String operatorToken = params.get("tokenKey");
        String groupID = params.get("groupID");

        String url= ConstantsValues.getHttpUrl(context,NetUrl.
                HTTP_GetMeetingGroupDetail);
        paramsList.add(new BasicNameValuePair("url", url));
        paramsList.add(new BasicNameValuePair("operatorToken", operatorToken));
        paramsList.add(new BasicNameValuePair("groupID", groupID));
        return paramsList;
    }


}
