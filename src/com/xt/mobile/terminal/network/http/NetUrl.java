package com.xt.mobile.terminal.network.http;

/**
 * 存放url
 * Created by 彭永顺 on 2020/5/15.
 */
public class NetUrl {
    /////////////////////////////////////////////////////HTTP//////////////////////////
    //登陆前的验证码的接口
    public static final String HTTP_Authcode = "/account/createVerificationCode?type=0&ipAddress=";
    //登陆的接口
    public static final String HTTP_CreateToken= "/account/createUserTokenForWeb";
    //退出登陆
    public static final String HTTP_DestroyToken = "/account/removeUserToken?operatorToken=";
    //查询设备列表
    public static final String HTTP_QueryDeviceList = "/deploy/queryDeviceList";
    //绑定设备
    public static final String HTTP_BindDevice = "/userCustom/setUserCustom";
    //查询绑定设备
    public static final String HTTP_QueryBindDevice = "/userCustom/getUserCustom";
    //更新用户鉴权
    public static final String HTTP_RenewUserToken = "/account/renewUserToken";
    //查询当前在进行会议列表
    public static final String HTTP_GetMeetingList = "/conference/queryAllConferenceListItems";
    //查询单个会议详情
    public static final String HTTP_GetMeetingDetail = "/conference/getSingleConferenceDetail";
    //添加会议分组
    public static final String HTTP_AddMeetingGroup = "/group/addMeetingGroup";
    //删除会议分组（删除业务分组）
    public static final String HTTP_DeleteMeetingGroup = "/group/removeAnyBusinessGroups";
    //获取业务分组列表
    public static final String HTTP_GetMeetingGroupList = "/group/queryBusinessGroupList";
    //查询单个会议分组详情
    public static final String HTTP_GetMeetingGroupDetail = "/group/querySingleMeetingGroup";
    //编辑会议分组
    public static final String HTTP_EditMeetingGroup = "/group/editMeetingGroup";

    ///////////////////////////////////////////////////WSS///////////////////////////

    //获取兴图的useID
    public static final String HTTP_GET_XT_USERID = "/KXGatewayService/kx/getXTUserID";
    //获取空信的useID  批量
    public static final String HTTP_GET_KX_USERIDS = "/KXGatewayService/kx/getKXUser";
    //保存会议
    public static final String HTTP_POST_SAVE_MEETING = "/KXGatewayService/kx/saveConfereceUser";
    //删除会议
    public static final String HTTP_POST_DELET_MEETING = "/KXGatewayService/kx/removeConfereceUser";
}
