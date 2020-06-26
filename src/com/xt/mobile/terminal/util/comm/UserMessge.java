package com.xt.mobile.terminal.util.comm;

import android.content.Context;
import android.content.SharedPreferences;

import com.xt.mobile.terminal.contants.ConstantsValues;


/**
 * Created by 彭永顺 on 2020/5/16.
 */
public class UserMessge {
    private static UserMessge userMessge;
    private  SharedPreferences sp;
    private Context context;


    ////////////单例模式//////////////////////////////////////
    public static UserMessge getInstans(Context context){
        if (null==userMessge){
            userMessge=new UserMessge(context);
        }
        return userMessge;
    }
    private UserMessge(Context context){
        this.context=context;
        if (null==sp){
            sp = context.getSharedPreferences(
                    ConstantsValues.DEFAULT_SP,
                    context.MODE_PRIVATE);
        }

    }
    /**
     * 获取存储用户信息的sp
     */
    public SharedPreferences getUserSp(){
        if (sp !=null){
            return sp;
        }else {
            return null;
        }

    }

    ///////////////////////////////////////////////////////////////////////
    /**
     * 设置编码端的ENCODE_ID  采集sip信息
     */
    public void setEncodeId(String encodeId){

        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (encodeId !=null && encodeId.length()>0){
                edit.putString(ConstantsValues.ENCODE_ID, encodeId);
            }else {
                edit.putString(ConstantsValues.ENCODE_ID, "");
            }

            edit.commit();

        }


    }

    /**
     * 设置编码端的密码 ENCODE_PWD  采集sip信息
     */
    public void setEncodePwd(String encodePwd){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (encodePwd !=null && encodePwd.length()>0){
                edit.putString(ConstantsValues.ENCODE_PWD, encodePwd);
            }else {
                edit.putString(ConstantsValues.ENCODE_PWD, "");
            }

            edit.commit();

        }


    }


    /**
     * 设置编码端的端口号 ENCODE_PORT  采集sip信息
     */
    public void setEncodePort(String encodePport){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (encodePport !=null && encodePport.length()>0){
                edit.putString(ConstantsValues.ENCODE_PORT, encodePport);
            }else {
                edit.putString(ConstantsValues.ENCODE_PORT, "");
            }

            edit.commit();
        }

    }


    /**
     * 设置解码端的  decodeId
     */
    public void setDecodeId(String decodeId){

        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (decodeId !=null && decodeId.length()>0){
                edit.putString(ConstantsValues.SIP_ID, decodeId);
            }else {
                edit.putString(ConstantsValues.SIP_ID, "");
            }

            edit.commit();

        }


    }

    /**
     * 设置解码端的  decodePwd
     */
    public void setDecodePwd(String decodePwd){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (decodePwd !=null && decodePwd.length()>0){
                edit.putString(ConstantsValues.SIP_PWD, decodePwd);
            }else {
                edit.putString(ConstantsValues.SIP_PWD, "");
            }

            edit.commit();

        }


    }


    /**
     * 设置解码端的  SIP_SERVER_PORT
     */
    public void setDecodeServerPort(String decodePort){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (decodePort !=null && decodePort.length()>0){
                edit.putString(ConstantsValues.SIP_SERVER_PORT, decodePort);
            }else {
                edit.putString(ConstantsValues.SIP_SERVER_PORT, "");
            }

            edit.commit();

        }


    }


    /**
     * 设置解码端的  SIP_SERVER_IP
     */
    public void setDecodeServerIP(String decodeServerIP){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (decodeServerIP !=null && decodeServerIP.length()>0){
                edit.putString(ConstantsValues.SIP_SERVER_IP, decodeServerIP);
            }else {
                edit.putString(ConstantsValues.SIP_SERVER_IP, "");
            }

            edit.commit();

        }

    }
    /**
     * 设置解码端的  SIP_SERVER_ID
     */
    public void setDecodeServerID(String decodeServerID){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (decodeServerID !=null && decodeServerID.length()>0){
                edit.putString(ConstantsValues.SIP_SERVER_ID, decodeServerID);
            }else {
                edit.putString(ConstantsValues.SIP_SERVER_ID, "");
            }

            edit.commit();

        }

    }


    /**
     * 设置服务ip
     */
    public void setCoreIP(String coreIP){

        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (coreIP !=null && coreIP.length()>0){
                edit.putString(ConstantsValues.CORE_IP, coreIP);
            }else {
                edit.putString(ConstantsValues.CORE_IP, "");
            }

            edit.commit();

        }


    }

    /**
     * 设置服务端口
     */
    public void setCorePort(String corePort){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (corePort !=null && corePort.length()>0){
                edit.putString(ConstantsValues.CORE_PORT, corePort);
            }else {
                edit.putString(ConstantsValues.CORE_PORT, "");
            }

            edit.commit();

        }


    }



    /**
     * 添加存储用户信息  密码
     */
    public void setUserPwd(String userPwd){

        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (userPwd !=null && userPwd.length()>0){
                edit.putString(ConstantsValues.USERPWD, userPwd);
            }else {
                edit.putString(ConstantsValues.USERPWD, "");
            }

            edit.commit();

        }


    }




    /**
     * 添加存储用户信息   用户姓名
     */
    public void setUserName(String userName){

        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (userName !=null && userName.length()>0){
                edit.putString(ConstantsValues.USERNAME, userName);
            }else {
                edit.putString(ConstantsValues.USERNAME, "");
            }

            edit.commit();

        }

    }


    /**
     * 获取存储用户信息   用户ID
     */
    public void setUserID(String mUserId){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (mUserId !=null && mUserId.length()>0){
                edit.putString(ConstantsValues.USERID, mUserId);
            }else {
                edit.putString(ConstantsValues.USERID, "");
            }

            edit.commit();

        }

    }


    /**
     * 获取存储用户信息   用户Tokenkey
     */
    public void setUserTokenkey(String tokenkey){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (tokenkey !=null && tokenkey.length()>0){
                edit.putString(ConstantsValues.TOKENKEY, tokenkey);
            }else {
                edit.putString(ConstantsValues.TOKENKEY, "");
            }

            edit.commit();

        }

    }





    /**
     * 设置屏幕分辨率
     */
    public void setFbl(String fbl){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (fbl !=null && fbl.length()>0){
                edit.putString(ConstantsValues.MEDIA_FBL, fbl);
            }else {
                edit.putString(ConstantsValues.MEDIA_FBL, "");
            }

            edit.commit();

        }

    }


    /**
     * 设置屏幕帧率
     */
    public void setZl(String zl){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (zl !=null && zl.length()>0){
                edit.putString(ConstantsValues.MEDIA_ZL, zl);
            }else {
                edit.putString(ConstantsValues.MEDIA_ZL, "");
            }

            edit.commit();

        }

    }


    /**
     * 设置屏幕码率
     */
    public void setMl(String ml){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (ml !=null && ml.length()>0){
                edit.putString(ConstantsValues.MEDIA_ML, ml);
            }else {
                edit.putString(ConstantsValues.MEDIA_ML, "");
            }

            edit.commit();

        }

    }


    /**
     * 设置是否开启铃声
     */
    public void setOpenVoice(boolean isOpenVoice){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.SP_KEY_CallIn_Voice, isOpenVoice);
            edit.commit();

        }

    }


    /**
     * 设置是否开启震动
     */
    public void setOpenVibrate(boolean isOpenVibrate){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.SP_KEY_CallIn_Vibrate, isOpenVibrate);
            edit.commit();

        }

    }


    /**
     * 设置是否保存日志
     */
    public void setSaveLog(boolean isSaveLog){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.SP_KEY_SAVE_LOG, isSaveLog);
            edit.commit();

        }

    }

    /**
     * 设置图像优化/图质增强
     */
    public void setTzzq(int progress){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(ConstantsValues.MEDIA_TZ_ZQ, progress);
            edit.commit();

        }

    }

    /**
     * 设置图像优化/图像增强
     */
    public void setPhotoZQ(int progress){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(ConstantsValues.MEDIA_TX_ZQ, progress);
            edit.commit();

        }

    }


    /**
     * 设置音频的音量大小
     */
    public void setVoiceAdd(int progress){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(ConstantsValues.MEDIA_VOICE_ADD, progress);
            edit.commit();

        }

    }


    /**
     * 设置音频是否降噪
     */
    public void setVoiceDenoise(boolean isVoiceDenoise){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.MEDIA_VOICE_DENOISE, isVoiceDenoise);
            edit.commit();

        }
    }


    /**
     * 设置用户的设备id
     */
    public void setUserDeviceId(String userDeviceId){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.BIND_DEVICE_ID, userDeviceId);
            edit.commit();

        }
    }

    /**
     * 设置用户的设备名称
     */
    public void setUserDeviceName(String userDeviceName){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.BIND_DEVICE_NAME, userDeviceName);
            edit.commit();

        }
    }

    /**
     * 保存第三方的会议id
     */
    public void setThirdPartyMeetingId(String thirdPartyMeetingId){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_MEETINGID, thirdPartyMeetingId);
            edit.commit();

        }
    }

    /**
     * 保存第三方的UserName
     */
    public void setThirdPartyUserName(String thirdPartyUserName){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_USERNAME, thirdPartyUserName);
            edit.commit();

        }
    }


    /**
     * 保存第三方的群id
     */
    public void setThirdPartyGroupId(String thirdPartyGroupId){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_GROUP_ID, thirdPartyGroupId);
            edit.commit();

        }
    }

    /**
     * 保存第三方的主席userID
     */
    public void setThirdPartyChairmanID(String thirdPartyChairmanID){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_CHAIRMAN_ID, thirdPartyChairmanID);
            edit.commit();

        }
    }

    /**
     * 保存第三方的password
     */
    public void setThirdPartyPwd(String thirdPartyPwd){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_USERPWD, thirdPartyPwd);
            edit.commit();

        }
    }
    /**
     * 保存第三方的Tokenkey
     */
    public void setThirdPartyTokenkey(String thirdPartyTokenkey){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(ConstantsValues.THIRDPARTY_TOKENKEY, thirdPartyTokenkey);
            edit.commit();

        }
    }
    /**
     * 保存第三方的创建会议状态
     */
    public void setThirdPartyCreatMeeting(boolean isCreatMeeting){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.THIRDPARTY_ISCREATMEETING, isCreatMeeting);
            edit.commit();

        }
    }


    /**
     * 保存第三方的发送广播的标识
     */
    public void setThirdPartyBroadcastTag(String action){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (action !=null && action.length()>0){
                edit.putString(ConstantsValues.THIRDPARTY_BROADCAST_TAG, action);
            }else {
                edit.putString(ConstantsValues.THIRDPARTY_BROADCAST_TAG, "");
            }

            edit.commit();

        }
    }


    /**
     * 保存第三方的通讯录的信息
     */
    public void setThirdPartyPeoples(String json){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (json !=null && json.length()>0){
                edit.putString(ConstantsValues.THIRDPARTY_CONTACTS_DATA, json);
            }else {
                edit.putString(ConstantsValues.THIRDPARTY_CONTACTS_DATA, "");
            }

            edit.commit();

        }
    }


    /**
     * 保存登陆状态
     */
    public void setLoginStatus(String status){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (status !=null && status.length()>0){
                edit.putString(ConstantsValues.LOGIN_STATUS, status);
            }else {
                edit.putString(ConstantsValues.LOGIN_STATUS, "");
            }

            edit.commit();

        }


    }


    /**
     * 保存通话超时状态
     * 单位：秒
     */
    public void setCallOutTime(String outTime){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            if (outTime !=null && outTime.length()>0){
                edit.putString(ConstantsValues.CALL_OUT_TIME, outTime);
            }else {
                edit.putString(ConstantsValues.CALL_OUT_TIME, "");
            }

            edit.commit();
        }

    }

    /**
     * 保存倒计时是否结束
     */
    public void setCountOver(boolean isOver){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConstantsValues.COUNT_OVER, isOver);

            edit.commit();
        }
    }


    /**
     * 保存在会人员的信息
     */
    public void setonLinePeople(int json){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(ConstantsValues.ONLINE_PEOPLE_MESSGE, json);

            edit.commit();
        }

    }

    /**
     * 保存倒计时信息
     */
    public void setTimeCount(int count){
        if (sp !=null){
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt(ConstantsValues.TIME_COUNT, count);

            edit.commit();
        }

    }

    ///////////////////////////////////获取值//////////////////////////////////////
    /**
     * 获取存储用户信息  密码
     */
    public String getUserPassWord(){
        String passWord="";
        if (sp !=null){
            passWord = sp.getString(ConstantsValues.USERPWD, "");
        }
        return passWord;

    }


    /**
     * 获取分辨率
     */
    public String getFbl(){
        String fbl="";
        if (sp !=null){
            fbl = sp.getString(ConstantsValues.MEDIA_FBL, "");
        }
        return fbl;

    }
    /**
     * 获取帧率
     */
    public String getZl(){
        String zl="";
        if (sp !=null){
            zl = sp.getString(ConstantsValues.MEDIA_ZL, "");
        }
        return zl;

    }
    /**
     * 获取码率
     */
    public String getMl(){
        String ml="";
        if (sp !=null){
            ml = sp.getString(ConstantsValues.MEDIA_ML, "");
        }
        return ml;

    }


    /**
     * 获取是否开启铃声
     */
    public boolean isOpenVoice(){
        boolean isOpenVoice=true;
        if (sp !=null){
            isOpenVoice = sp.getBoolean(ConstantsValues.SP_KEY_CallIn_Voice, true);
        }
        return isOpenVoice;

    }

    /**
     * 获取是否开启震动
     */
    public boolean isOpenVibrate(){
        boolean isOpenVibrate=true;
        if (sp !=null){
            isOpenVibrate = sp.getBoolean(ConstantsValues.SP_KEY_CallIn_Vibrate, true);
        }
        return isOpenVibrate;

    }

    /**
     * 获取是否保存日志
     */
    public boolean isSaveLog(){
        boolean isSaveLog=true;
        if (sp !=null){
            isSaveLog = sp.getBoolean(ConstantsValues.SP_KEY_SAVE_LOG, true);
        }
        return isSaveLog;

    }


    /**
     * 获取图像增强位置
     */
    public int getPhotoZQ(){
        int progress=0;
        if (sp !=null){
            progress = sp.getInt(ConstantsValues.MEDIA_TX_ZQ, 0);
        }
        return progress;

    }


    /**
     * 获取图质增强位置
     */
    public int getTzZQ(){
        int progress=0;
        if (sp !=null){
            progress = sp.getInt(ConstantsValues.MEDIA_TZ_ZQ, 0);
        }
        return progress;

    }


    /**
     * 获取音频是否降噪
     */
    public boolean isVoiceDenoise(){
        boolean isVoiceDenoise=true;
        if (sp !=null){
            isVoiceDenoise = sp.getBoolean(ConstantsValues.MEDIA_VOICE_DENOISE, false);
        }
        return isVoiceDenoise;

    }

    /**
     * 获取音频音量的位置
     */
    public int getVoiceAdd(){
        int progress=0;
        if (sp !=null){
            progress = sp.getInt(ConstantsValues.MEDIA_VOICE_ADD, 20);
        }
        return progress;

    }

    /**
     * 获取用户的设备id
     */
    public String getUserDeviceId(){
        String userDeviceId="";
        if (sp !=null){
            userDeviceId = sp.getString(ConstantsValues.BIND_DEVICE_ID, "");
        }
        return userDeviceId;

    }

    /**
     * 获取用户的设备名称
     */
    public String getUserDeviceName(){
        String userDeviceName="";
        if (sp !=null){
            userDeviceName = sp.getString(ConstantsValues.BIND_DEVICE_NAME, "");
        }
        return userDeviceName;

    }



    /**
     * 获取第三方的会议id
     */
    public String getThirdPartyMeetingId(){
        String thirdPartyMeetingId="";
        if (sp !=null){
            thirdPartyMeetingId = sp.getString(ConstantsValues.THIRDPARTY_MEETINGID, "");
        }
        return thirdPartyMeetingId;
    }

    /**
     * 获取第三方的UserName
     */
    public String getThirdPartyUserName(){
        String thirdPartyUserName="";
        if (sp !=null){
            thirdPartyUserName = sp.getString(ConstantsValues.THIRDPARTY_USERNAME, "");
        }
        return thirdPartyUserName;
    }


    /**
     * 获取第三方的主席的会议id
     */
    public String getThirdPartyChairmanID(){
        String thirdPartyChairmanID="";
        if (sp !=null){
            thirdPartyChairmanID = sp.getString(ConstantsValues.THIRDPARTY_CHAIRMAN_ID, "");
        }
        return thirdPartyChairmanID;
    }

    /**
     * 获取第三方的群id
     */
    public String getThirdPartyGroupID(){
        String thirdPartyUserName="";
        if (sp !=null){
            thirdPartyUserName = sp.getString(ConstantsValues.THIRDPARTY_GROUP_ID, "");
        }
        return thirdPartyUserName;
    }
    /**
     * 获取第三方的password
     */
    public String getThirdPartyPwd(){
        String thirdPartyPwd="";
        if (sp !=null){
            thirdPartyPwd = sp.getString(ConstantsValues.THIRDPARTY_USERPWD, "");
        }
        return thirdPartyPwd;
    }
    /**
     * 获取第三方的Tokenkey
     */
    public String getThirdPartyTokenkey(){
        String thirdPartyTokenkey="";
        if (sp !=null){
            thirdPartyTokenkey = sp.getString(ConstantsValues.THIRDPARTY_TOKENKEY, "");
        }
        return thirdPartyTokenkey;
    }
    /**
     * 获取第三方的创建会议状态
     */
    public Boolean isThirdPartyCreatMeeting(){
        boolean isCreatMeeting=false;
        if (sp !=null){
            isCreatMeeting = sp.getBoolean(ConstantsValues.THIRDPARTY_ISCREATMEETING, false);
        }
        return isCreatMeeting;
    }



    /**
     * 保存第三方的发送广播的标识
     */
    public String getThirdPartyBroadcastTag(){
        String action="";
        if (sp !=null){
            action = sp.getString(ConstantsValues.THIRDPARTY_BROADCAST_TAG, "");
        }
        return action;
    }



    /**
     * 获取存储用户信息   用户姓名
     */
    public String getUserName(){
        String mUserName="";
        if (sp !=null){
            mUserName = sp.getString(ConstantsValues.USERNAME, "");
        }
        return mUserName;

    }


    /**
     * 获取存储用户信息   用户ID
     */
    public String getUserID(){
        String mUserId="";
        if (sp !=null){
            mUserId = sp.getString(ConstantsValues.USERID, "");
        }
        return mUserId;

    }


    /**
     * 获取存储用户信息   用户Tokenkey
     */
    public String getUserTokenkey(){
        String mTokenkey="";
        if (sp !=null){
            mTokenkey = sp.getString(ConstantsValues.TOKENKEY,  "");
        }
        return mTokenkey;

    }




    /**
     * 获取服务ip
     */
    public String getCoreIP(){
        String coreIP="";
        if (sp !=null){
            coreIP = sp.getString(ConstantsValues.CORE_IP, "");
        }
        return coreIP;

    }

    /**
     * 获取服务端口
     */
    public String getCorePort(){
        String coreIP="";
        if (sp !=null){
            coreIP = sp.getString(ConstantsValues.CORE_PORT, "");
        }
        return coreIP;

    }




    /**
     * 保存编码端的ENCODE_ID
     */
    public String getEncodeId(){

        String encodeId="";
        if (sp !=null){
            encodeId = sp.getString(ConstantsValues.ENCODE_ID, "");
        }
        return encodeId;

    }

    /**
     * 保存编码端的密码 ENCODE_PWD
     */
    public String getEncodePwd(){
        String encodePwd="";
        if (sp !=null){
            encodePwd = sp.getString(ConstantsValues.ENCODE_PWD, "");
        }
        return encodePwd;

    }


    /**
     * 保存编码端的端口号 ENCODE_PORT
     */
    public String getEncodePort(){

        String encodePort="";
        if (sp !=null){
            encodePort = sp.getString(ConstantsValues.ENCODE_PORT, "");
        }
        return encodePort;

    }




    /**
     * 获取解码端的  decodeId
     */
    public String getDecodeId(){

        String decodeId="";
        if (sp !=null){
            decodeId = sp.getString(ConstantsValues.SIP_ID, "");
        }
        return decodeId;

    }

    /**
     * 获取解码端的  decodePwd
     */
    public String getDecodePwd(){

        String decodePwd="";
        if (sp !=null){
            decodePwd = sp.getString(ConstantsValues.SIP_PWD, "");
        }
        return decodePwd;


    }


    /**
     * 获取解码端的  SIP_SERVER_PORT
     */
    public String getDecodeServerPort(){
        String decodeServerPort="";
        if (sp !=null){
            decodeServerPort = sp.getString(ConstantsValues.SIP_SERVER_PORT, "");
        }
        return decodeServerPort;

    }


    /**
     * 获取解码端的  SIP_SERVER_IP
     */
    public String getDecodeServerIP(){
        String decodeServerIP="";
        if (sp !=null){
            decodeServerIP = sp.getString(ConstantsValues.SIP_SERVER_IP, "");
        }
        return decodeServerIP;
    }
    /**
     * 获取解码端的  SIP_SERVER_ID
     */
    public String getDecodeServerID(){
        String decodeServerID="";
        if (sp !=null){
            decodeServerID = sp.getString(ConstantsValues.SIP_SERVER_ID, "");
        }
        return decodeServerID;

    }


    /**
     * 获取第三方传过来的通讯录信息  THIRDPARTY_CONTACTS_DATA
     */
    public String getThridPartPeoples(){
        String peoples="";
        if (sp !=null){
            peoples = sp.getString(ConstantsValues.THIRDPARTY_CONTACTS_DATA, "");
        }
        return peoples;

    }

    /**
     * 获取登陆状态
     */
    public String getLoginStatus(){
        String login_status="";
        if (sp !=null){
            login_status = sp.getString(ConstantsValues.LOGIN_STATUS, "");
        }
        return login_status;

    }

    /**
     * 获取通话超时状态
     */
    public String getCallOutTime(){
        String call_out_time="";
        if (sp !=null){
            call_out_time = sp.getString(ConstantsValues.CALL_OUT_TIME, "");
        }
        return call_out_time;

    }


    /**
     * 获取在会人员的信息
     */
    public int getOnLinePeople(){
        int json=0;
        if (sp !=null){
            json = sp.getInt(ConstantsValues.ONLINE_PEOPLE_MESSGE, 0);
        }
        return json;
    }


    /**
     * 保存倒计时信息
     */
    public int getTimeCount(){

        int json=0;
        if (sp !=null){
            json = sp.getInt(ConstantsValues.TIME_COUNT, 1);
        }
        return json;

    }

    /**
     * 保存倒计时是否结束
     */
    public boolean isCountOver(){

        boolean isCountOver=false;
        if (sp !=null){
            isCountOver = sp.getBoolean(ConstantsValues.COUNT_OVER, false);
        }
        return isCountOver;

    }

    /**
     * 清除缓存数据
     */
    public void clearData(){
        setEncodeId("");
        setEncodePwd("");
        setEncodePort("");
        setDecodeId("");
        setDecodePwd("");
        setDecodeServerPort("");
        setDecodeServerIP("");
        setDecodeServerID("");
        setCoreIP("");
        setCorePort("");
        setUserPwd("");
        setUserName("");
        setUserID("");
        setUserTokenkey("");
        setThirdPartyMeetingId("");
        setThirdPartyUserName("");
        setThirdPartyGroupId("");
        setThirdPartyChairmanID("");
        setThirdPartyPwd("");
        setThirdPartyTokenkey("");
        setThirdPartyCreatMeeting(true);
        setThirdPartyBroadcastTag("");
        setThirdPartyPeoples("");
        setLoginStatus("1");
        setCallOutTime("60");
        setCountOver(false);
        setonLinePeople(0);
        setTimeCount(60);

        //设置屏幕和媒体参数
        setFbl("");
        setZl("");
        setMl("");
        setOpenVoice(true);
        setOpenVibrate(true);
        setSaveLog(true);
        setTzzq(20);
        setPhotoZQ(50);
        setVoiceAdd(50);
        setVoiceDenoise(true);

        //设备
        setUserDeviceId("");
        setUserDeviceName("");

    }
}
