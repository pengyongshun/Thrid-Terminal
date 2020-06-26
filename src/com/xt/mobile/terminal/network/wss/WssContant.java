package com.xt.mobile.terminal.network.wss;

/**
 * 用于接收wss请求结果的标识
 * Created by 彭永顺 on 2020/5/16.
 */
public class WssContant {
    ////////////////////////////////////消息类型///////////////////////
    //初始化移动端媒体信息
    public static final String WSS_INFORM_INIT_MOBLIE_MEDIA="informInitMediaTerminal";
    //初始化媒体信息
    public static final String WSS_INFORM_INIT_MEDIA="informInitMedia";
    //初始化通讯录中人员信息
    public static final String WSS_ORGANIZATION_USER="OrganizationUser";
    //通讯录中设备信息
    public static final String WSS_ORGANIZATION_DEVICE="OrganizationDevice";
    //通讯录中人员状态
    public static final String WSS_MAIN_USERS_STATUS="MainUsersStatus";
    //通讯录中设备状态
    public static final String WSS_MAIN_DEVICES_STATUS="MainDevicesStatus";
    //添加资源
    public static final String WSS_INFORM_ADD_RESOURCE="informAddResourceStatus";
    //刷新资源状态
    public static final String WSS_INFORM_REFRESH_RESOURCE="informRefreshResourceStatus";

    //消息展示
    public static final String WSS_INFORM_SHOW_MESSAGE="informShowMessage";
    //展示记住
    public static final String WSS_INFORM_SHOW_REMIND="informShowRemind";
    //开启媒体
    public static final String WSS_INFORM_STARTMEDIA_BYLOCAL="informStartMediaByLocal";

    //接收呼叫
    public static final String WSS_PUBLISH_ACCEPTCALL="publishAcceptCall";
    //拒绝呼叫
    public static final String WSS_PUBLISH_REFUSECALL="publishRefuseCall";

    //结束时间
    public static final String WSS_CLOSE_TIME="closeTime";

    //会议
    public static final String WSS_JOIN_METTING="RefreshActivedSceneDetail";

    //会议(停止)
    public static final String WSS_STOP_METTING="RefreshSceneList";

    public static final String WSS_GROUP_START_COMMOND="informGroupStartMedia";

    public static final String WSS_GROUP_REFRESH_COMMOND="informRefreshGroupMedia";

    public static final String WSS_GROUP_STOP_COMMOND="informStopGroupMedia";

    public static final String WSS_MEETING_APPLY_SPEAK_SHOW="informShowMessage";
    //主席退出会议，成员收到主席退会的信息
    public static final String WSS_MEETING_ZX_OUT_MEETING="informRemoveActivedSceneDetail";

    //即时消息
    public static final String WSS_RECEVIE_COMMUNICATION_MESSAGE="informRecevieCommunicationMessage";
    public static final String WSS_USER_COMM_SESSION_PREVIEW="informUserCommSessionPreview";
    public static final String WSS_RECEVIE_COMMUNICATION_NOTIFICATION="informRecevieCommunicationNotification";
}
