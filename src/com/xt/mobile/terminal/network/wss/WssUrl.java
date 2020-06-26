package com.xt.mobile.terminal.network.wss;

/**
 * wss请求的方法名
 * Created by 彭永顺 on 2020/5/16.
 */
public class WssUrl {
    //发送Join
    public static final String WSS_JOIN="join";
    //操作人员状态（添加状态、清除状态）
    public static final String WSS_PUBLISH_USERSTATUS="publishUserStatus";
    //初始化媒体服务（即获取SIP消息）
    public static final String WSS_PUBLISH_INITMEDIA_BYCUSTOM="publishInitMediaByCustom";
    //初始化编码器（即获取编码器SIP消息）
    public static final String WSS_PUBLISH_MOBILE_TERMINAL="publishInitMobileTerminal";

    ////////////////////////////////////通讯录///////////////////////////////////////
    //订阅人员目录
    public static final String WSS_SUBSCRIBE_ORGANIZATION_USER="subscribeOrganizationUser";
    //订阅设备目录
    public static final String WSS_SUBSCRIBE_ORGANIZATION_DEVICE="subscribeOrganizationDevice";

    //请求人员目录下的资源
    public static final String WSS_SUBSCRIBE_USERSSTATUS="subscribeUsersStatus";
    //请求设备目录下的资源
    public static final String WSS_SUBSCRIBE_DEVICESSTATUS="subscribeDevicesStatus";

    //////////////////////////////////////////////////点播///////////////////////////////////
    //发送点播（针对设备）
    public static final String WSS_PUBLISH_STARTPLAY_DEVICE="publishStartPlayDevice";
    //发送停止点播（针对设备）
    public static final String WSS_PUBLISH_STOPPLAY_DEVICE="publishStopPlayDevice";

    ////////////////////////////////////////////////呼叫/////////////////////////////////////
    //开启呼叫
    public static final String WSS_PUBLISH_STARTCALL="publishStartCall";
    //停止呼叫
    public static final String WSS_PUBLISH_STOPCALL="publishStopCall";
    //接受呼叫
    public static final String WSS_PUBLISH_ACCEPTCALL="publishAcceptCall";
    //拒绝呼叫
    public static final String WSS_PUBLISH_REFUSECALL="publishRefuseCall";
    //取消呼叫
    public static final String WSS_CANCEL_BUSINESS="cancelBusiness";


    ////////////////////////////////////////////////会议/////////////////////////////////////
    //创建会议
    public static final String WSS_METTING_CREATE="publishStartConference";
    //主席 停止会议
    public static final String WSS_METTING_ZX_STOP="publishStopConference";
    //主席添加成员
    public static final String WSS_METTING_ZX_ADD_PEOPLE="publishAddMembersFromConference";
    //主席删除成员
    public static final String WSS_METTING_ZX_DELET_PEOPLE="publishRemoveMembersFromConference";
    //主席指定发言人
    public static final String WSS_METTING_ZX_ZDFYR="publishSetSpeakerFromConference";
    //主席收回发言
    public static final String WSS_METTING_ZX_SHFY="publishCancelSpeakerFromConference";
    //主席接受申请发言
    public static final String WSS_METTING_ZX_JSSQFY="publishAcceptSpeakerFromConference";
    //主席拒绝发言申请
    public static final String WSS_METTING_ZX_JJFYSQ="publishRefuseSpeakerFromConference";
    //成员加入会议（目前不用）   需要考证
    public static final String WSS_METTING_CY_JOIN_METTING="publishMemberJoinFromConference";
    //成员申请发言
    public static final String WSS_METTING_CY_SQFY="publishMemberSpeakFromConference";
    //成员取消发言
    public static final String WSS_METTING_CY_QXFY="publishMemberSpeakFromConference";
    //成员退出会议
    public static final String WSS_METTING_CY_LEAVE_METTING="publishMemberLeaveFromConference";
    //成员申请入会
    public static final String WSS_METTING_CY_JOIN="publishApplyJoinFromConference";
    //发送同意成员入会（主席端后台实现，界面无感知）
    public static final String WSS_METTING_FSTY_CY_JOIN="publishAnswerApplyJoinFromConference";
    //发送受邀入会指令
    public static final String WSS_METTING_FSSY_JOIN="publishInviteJoinFromConference";
    //设置会议暂停
    public static final String WSS_METTING_SETTING_STOP="publishSetPausedFromConference";
    //设置会议静音
    public static final String WSS_METTING_SETTING_JY="publishSetMutedFromConference";
    //通过关键字查询人员
    public static final String WSS_CONTACTS_QUERY_PEOPLE="subscribeUsersStatusByKey";
    //通过关键字查询设备
    public static final String WSS_CONTACTS_QUERY_DEVICE="subscribeDevicesStatusByKey";
    ////////////////////////////////////////////////即时消息/////////////////////////////////////
    //发送即时消息
    public static final String WSS_SEND_COMMUNICATION_MESSAGE ="publishSendCommunicationMessage";
    public static final String WSS_SEND_COMMUNICATION_MESSAGE_FROM_SESSION="publishSendCommunicationMessageFromSession";
}
