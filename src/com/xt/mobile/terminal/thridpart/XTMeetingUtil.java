package com.xt.mobile.terminal.thridpart;

import android.content.Context;
import android.content.Intent;


/**
 * Created by 彭永顺 on 2020/6/15.
 */
public class XTMeetingUtil {
    private static XTMeetingUtil xtMeetingUtil;
    private Context context;
    public static XTMeetingUtil getInstans(Context context){
        if (null== xtMeetingUtil){
            xtMeetingUtil =new XTMeetingUtil(context);
        }
        return xtMeetingUtil;
    }
    private XTMeetingUtil(Context context){
        this.context=context;
    }

    /**
     * 停止主席开会
     * @param meetingID
     */
    public void stopChairmanMeeting(String meetingID){
        Intent intent = new Intent(XTContants.ACTION_KX_CHAIRMAN_STOP);
        intent.putExtra(XTContants.APP_BROADCAST_CHAIRMAN_STOP,meetingID);
        context.sendBroadcast(intent);

    }


    /**
     * 成员离开拒绝和接收界面
     * @param meetingID 会议ID
     */
    public void memberLeaveRefusedUI(String meetingID){
        Intent intent = new Intent(XTContants.ACTION_KX_MEMBER_LEAVE_REFUSE);
        intent.putExtra(XTContants.APP_BROADCAST_MEMBER_LEAVE_REFUSE,meetingID);
        context.sendBroadcast(intent);
    }
}
