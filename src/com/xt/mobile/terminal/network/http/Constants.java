package com.xt.mobile.terminal.network.http;

/**
 * Created by 彭永顺 on 2020/5/11.
 */
public class Constants {
    ////////////////////////////////////界面跳转的标识///////////////////////////////////////
    public static final String ACTIVTY_TAG="activityTag";
    public static final String ACTIVTY_ZDFY="1";//指定发言
    public static final String ACTIVTY_JIONHY="2";//进入会议
    public static final String ACTIVTY_ADDHY="3";//加入会议
    public static final String ACTIVTY_KSHY="4";//快速会议
    public static final String ACTIVTY_YQCY="5";//会议中 点击 邀请成员
    public static final String ACTIVTY_YQCY_LIST="6";//从参会成员列表中 点击 邀请成员
    public static final String ACTIVTY_YQCY_HYSY="7";//从会议首页中弹出框中 点击 邀请成员

    public static final String ACTIVTY_GROUP_POP_EDIT="8";//从分组会议中pop中 点击 编辑
    public static final String ACTIVTY_GROUP_LIST_ADD="9";//从分组会议中 点击 新增
    public static final String ACTIVTY_GROUP_DETIAL_ADD="10";//从分组会议详情中 点击 新增
    public static final String ACTIVTY_GROUP_DETIAL_LOOK="11";//从分组会议列表中 点击 详情

    public static final String ACTIVTY_MEDIA_SETTING_FBL="12";//媒体设置/分辨率
    public static final String ACTIVTY_MEDIA_SETTING_ZL="13";//媒体设置/帧率
    public static final String ACTIVTY_MEDIA_SETTING_ML="14";//媒体设置/码率

    public static final String ACTIVTY_CY_JOIN="15";//成员收到主席邀请进入会议
    public static final String ACTIVTY_GROUP_POP_JOIN="16";//从分组会议中 点击 进入会议
    public static final String ACTIVTY_THRID_APP="17";//从第三方进来的
    //////////////////////////////网络请求的唯一标识参数////////////////////////////////////
    public static final int HTTP_GET_LOGIN_AUTHCODE=1001;//登陆时短信验证码
    public static final int HTTP_GET_LOGIN_LOGIN=1002;//登陆
    public static final int HTTP_GET_LOGIN_DESTROYTOKEN=1003;//退出登陆
    public static final int HTTP_GET_QUERY_DEVICE=1004;//设备查询
    public static final int HTTP_GET_LOGIN_DESTROYTOKEN1=1005;//退出登陆
    public static final int HTTP_GET_RE_NEWUSER_TOKEN=1006;//更新用户鉴权

    public static final int HTTP_GET_MEETINGLIST = 1007;
    public static final int HTTP_Get_MeetingDetail = 1008;
    public static final int HTTP_Post_AddMeetingGroup = 1009;
    public static final int HTTP_Post_DeleteMeetingGroup = 1010;
    public static final int HTTP_Get_MeetingGroupList = 1011;
    public static final int HTTP_Get_MeetingGroupDetail = 1012;
    public static final int HTTP_Post_EditMeetingGroup = 1013;

    public static final int HTTP_GET_QUERY_BIND_DEVICE=1014;//设备绑定查询
	public static final int HTTP_GET_KXID_SWTICH_XTID = 1015;
    public static final int HTTP_GET_XTID_SWTICH_KXID = 1016;
    public static final int HTTP_POST_SAVE_MEETING = 1017;
    public static final int HTTP_GET_DELET_MEETING = 1018;
}
