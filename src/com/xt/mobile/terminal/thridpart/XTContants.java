package com.xt.mobile.terminal.thridpart;

/**
 * Created by 彭永顺 on 2020/6/14.
 */
public class XTContants {
    //第三方跳转的入口
    public static final String APP_ENTRANCE="app_entrance";
    //第三方跳转的数据
    public static final String APP_ENTRANCE_DATA="app_entrance_data";
    //创建会议成功后将会议id返回给客户的
    public static final String APP_BROADCAST_CREATE_MEETING="create_meeting";
    //成员拒绝入会，成员不进aar包
    public static final String APP_BROADCAST_MEMBER_REFUSE="member_refuse";
    //成员离开会议的广播
    public static final String APP_BROADCAST_MEMBER_LEAVE="member_leave";
    //主席邀请成员的数据
    public static final String APP_BROADCAST_CHAIRMAN_INVITE="chairman_invite";
    //主席停止会议  主席成员都没有到达超时，成员在拒绝界面  主席主动停止会议
    public static final String APP_BROADCAST_CHAIRMAN_STOP_MEETING="chairman_stop_meeting";
    //主席停止会议 主席在成员未加入会议，超时时，成员在拒绝界面，主席退出
    public static final String APP_BROADCAST_TIMEOUT_NOT_OPERATING="timeout_not_operating";
    //主席向客户那边发送在会人数
    public static final String APP_BROADCAST_CHAIRMAN_INMEETING_NUM="in_meeting_number";
    //取广播的tag
    public static final String APP_BROADCAST_RESULT_TAG="broadcast_result_data";
    //主席停止会议
    public static final String APP_BROADCAST_CHAIRMAN_STOP="chairman_stop";
    public static final String ACTION_KX_CHAIRMAN_STOP = "action_kx_chairman_stop";// 通知主席停止会议

    //成员结束拒绝界面
    public static final String APP_BROADCAST_MEMBER_LEAVE_REFUSE="member_leave_refuse";
    public static final String ACTION_KX_MEMBER_LEAVE_REFUSE = "action_kx_member_leave_refuse";

    //单聊
    public static final String MEETING_SINGLE="single";
    //群聊
    public static final String MEETING_GROUP="group";
}
