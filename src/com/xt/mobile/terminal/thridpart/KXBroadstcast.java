package com.xt.mobile.terminal.thridpart;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.Gson;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.thridpart.bean.CreateMeetingBean;
import com.xt.mobile.terminal.thridpart.bean.EntranceXT;
import com.xt.mobile.terminal.thridpart.bean.InvitePeopleBean;
import com.xt.mobile.terminal.thridpart.bean.MemberLeaveBean;
import com.xt.mobile.terminal.thridpart.bean.MemberRefuseBean;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/14.
 * 向客户发送广播
 */
public class KXBroadstcast {
    /**
     * aar发送创建会议成功后，将会议id发送给空信app
     * @param activity
     * @param createMeetingBean
     */
    public static void sendBroadcastCreateMeeting(Activity activity,CreateMeetingBean createMeetingBean){
        if (activity !=null &&
                createMeetingBean !=null ){
            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_CREATE_MEETING)){
                            Intent intent = new Intent(value);
                            String json = FastJsonTools.bean2Json(createMeetingBean);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,json);
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastCreateMeeting------->json="+json);
                            PLog.d("VedioMettingActivity","----------sendBroadcastCreateMeeting------->正在向客户发送创建会议成功的广播");
                            return;
                        }
                    }

                }

            }
        }
    }


    /**
     * aar中成员拒绝入会，发送给空信app
     * @param activity
     * @param memberRefuseBean
     */
    public static void sendBroadcastMemberRefuse(Activity activity,MemberRefuseBean memberRefuseBean){
        if (activity !=null &&
                memberRefuseBean !=null ){
            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_MEMBER_REFUSE)){
                            Intent intent = new Intent(value);
                            String json = FastJsonTools.bean2Json(memberRefuseBean);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,json);
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastMemberRefuse------->json="+json);
                            PLog.d("VedioMettingActivity","----------sendBroadcastMemberRefuse------->正在向客户发送成员拒绝入会的广播");
                            return;
                        }
                    }

                }

            }

        }
    }


    /**
     * aar中成员离开会议，发送给空信app
     * @param activity
     * @param memberLeaveBean
     */
    public static void sendBroadcastMemberLeave(Activity activity,MemberLeaveBean memberLeaveBean){
        if (activity !=null &&
                memberLeaveBean !=null ){

            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_MEMBER_LEAVE)){
                            Intent intent = new Intent(value);
                            String json = FastJsonTools.bean2Json(memberLeaveBean);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,json);
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastMemberLeave------->json="+json);
                            PLog.d("VedioMettingActivity","----------sendBroadcastMemberLeave------->正在向客户发送成员离开会议的广播");
                            return;
                        }
                    }

                }

            }

        }
    }

    /**
     * aar中主席邀请成员，将选中的成员发送给空信app
     * @param activity
     * @param invitePeopleBean
     */
    public static void sendBroadcastPeoples(Activity activity,InvitePeopleBean invitePeopleBean){
        if (activity !=null && invitePeopleBean !=null ){

            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_CHAIRMAN_INVITE)){
                            Intent intent = new Intent(value);
                            String json = FastJsonTools.bean2Json(invitePeopleBean);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,json);
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastPeoples------->json="+json);
                            PLog.d("VedioMettingActivity","----------sendBroadcastPeoples------->正在向客户发送主席邀请成员的广播");
                            return;
                        }
                    }

                }

            }

        }
    }

    /**
     * aar中主席邀请成员，统计
     * @param activity
     * @param json
     */
    public static void sendBroadcastPeopleNum(Activity activity,String json){
        if (activity !=null && json !=null && json.length()>0){

            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_CHAIRMAN_INMEETING_NUM)){
                            Intent intent = new Intent(value);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,json);
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastPeopleNum------->正在向客户发送入会人员数量的广播---->num="+json);
                            return;
                        }
                    }

                }

            }

        }
    }


    /**
     * aar中主席停止会议
     * @param activity
     */
    public static void sendBroadcastChariManStopMeeting(Activity activity){
            UserMessge userMessge = UserMessge.getInstans(activity);
            if (userMessge !=null){
                String actions = userMessge.getThirdPartyBroadcastTag();
                if (actions !=null && actions.length()>0){
                    List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                    for (int i = 0; i < broadcastNames.size(); i++) {
                        String key = broadcastNames.get(i).getKey();
                        String value = broadcastNames.get(i).getValue();
                        if (key.equals(XTContants.APP_BROADCAST_CHAIRMAN_STOP_MEETING)){
                            Intent intent = new Intent(value);
                            intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,"未超时，主席手动离开会议");
                            activity.sendBroadcast(intent);
                            PLog.d("VedioMettingActivity","----------sendBroadcastChariManStopMeeting------->未超时，主席手动离开会议");
                            PLog.d("VedioMettingActivity","----------sendBroadcastChariManStopMeeting------->正在向客户发送主席停止会议的广播");
                            return;
                        }
                    }

                }

            }

    }


    /**
     * aar中主席停止会议
     * 主席在成员未加入会议，超时时，成员在拒绝界面，主席退出
     * @param activity
     */
    public static void sendBroadcastChariManOutTimeStopMeeting(Activity activity){
        UserMessge userMessge = UserMessge.getInstans(activity);
        if (userMessge !=null){
            String actions = userMessge.getThirdPartyBroadcastTag();
            if (actions !=null && actions.length()>0){
                List<EntranceXT.BroadcastName> broadcastNames = FastJsonTools.jsonToList(actions, EntranceXT.BroadcastName.class);
                for (int i = 0; i < broadcastNames.size(); i++) {
                    String key = broadcastNames.get(i).getKey();
                    String value = broadcastNames.get(i).getValue();
                    if (key.equals(XTContants.APP_BROADCAST_TIMEOUT_NOT_OPERATING)){
                        Intent intent = new Intent(value);
                        intent.putExtra(XTContants.APP_BROADCAST_RESULT_TAG,"超时，成员在拒绝界面，主席自动退出");
                        activity.sendBroadcast(intent);
                        PLog.d("VedioMettingActivity","----------sendBroadcastChariManStopMeeting------->超时，成员在拒绝界面，主席自动退出");
                        PLog.d("VedioMettingActivity","----------sendBroadcastChariManStopMeeting------->正在向客户发送主席停止会议的广播");
                        return;
                    }
                }

            }

        }

    }


}
