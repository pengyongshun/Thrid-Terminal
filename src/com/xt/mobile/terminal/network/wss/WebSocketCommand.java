package com.xt.mobile.terminal.network.wss;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.xt.mobile.terminal.bean.OutLoginBean;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.addparmer.WssGreatMeeting;
import com.xt.mobile.terminal.network.addparmer.YqcyParms;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.ToolLog;

import java.util.ArrayList;
import java.util.List;

//===websocket关键字
//int validCode1 = 1 << 8 | 220; 476 heart         S--B 心跳消息
//int validCode2 = 2 << 8 | 220; 732 pont          B--S 心跳消息
//int validCode3 = 3 << 8 | 220; 988 system        S--B 系统消息
//int validCode4 = 4 << 8 | 220; 1244 error
//int validCode5 = 5 << 8 | 220; 1500              B--S 认证消息
//int validCode6 = 6 << 8 | 220; 1756 broadcast    双向       普通数据
//int validCode7 = 7 << 8 | 220; 2012 exit         B--S 退出消息

//===业务状态
//enumBussStatus: {
//  Playing		: 1,//播放中
//  Calling		: 2,//呼叫中
//  Meeting		: 3,//会议中
//  Speaking	: 4,//对讲中
//  Transmiting	: 5,//转发中
//  
//  PlayWaiting		: 11,//播放等待中
//  CallWaiting		: 12,//呼叫等待中
//  MeetWaiting		: 13,//会议等待中
//  SpeakWaiting    : 14,//对讲等待中
//  TransmitWaiting	: 15,//转发等待中
//}

//===绑定编码器
//message UserCustom {
//  string user_id = 1;
//  AcceptCallType call_item = 2;
//  string encoder_item = 3;
//  string decoder_item = 4;
//  AcceptCallType meeting_item = 5;
//}
//
//enum AcceptCallType {
//    AUTO = 0;// 自动
//	MANUAL = 1;// 手动
//}

public class WebSocketCommand {

	private String mUserId;
	private String mUserName;
	private String mTokenKey;

	private static WebSocketCommand instance;

	private WebSocketCommand() {
		super();
	}

	public static WebSocketCommand getInstance() {
		if (instance == null) {
			instance = new WebSocketCommand();
		}
		return instance;
	}

	public String getmUserId() {
		return mUserId;
	}

	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getmTokenKey() {
		return mTokenKey;
	}

	public void setmTokenKey(String mTokenKey) {
		this.mTokenKey = mTokenKey;
	}

	/**
	 * 发送心跳
	 **/
	public void onSendHeart() {

		if (mUserId == null || mUserName == null) {
			ToolLog.i("===WebSocketCommand::onSendHeart (mUserId == null || mUserName == null)");
			return;
		}

		String cmd = null;
		try {
			// int validCode = 5 << 8 | 220;
			JSONObject obj = new JSONObject();
			obj.put("code", "1500");
			obj.put("userID", mUserId);
			obj.put("userName", mUserName);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendHeart (cmd == null)");
		}
	}

	/**
	 * 发送Join
	 **/
	public void onSendJoin() {
		if (mUserId == null || mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendJoin (mUserId == null || mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_JOIN);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendJoin (cmd == null)");
		}
	}

	/**
	 * 发送退出
	 **/
	public void onSendExit() {
		String leave = "{\"code\":2012}";
		IWebSocketClientService.getInstance().onSend(leave);
	}

	/**
	 * 退出登陆
	 **/
	public void onOutLogin() {
		OutLoginBean bean=new OutLoginBean();
		bean.setCode(1756);
		bean.setFunName("leave");
		bean.setUserID(mUserId);
		OutLoginBean.ParamsBean paramsBean=new OutLoginBean.ParamsBean();
		paramsBean.setOperatorToken(mTokenKey);
		bean.setParams(paramsBean);
		String cmd = FastJsonTools.bean2Json(bean);
		IWebSocketClientService.getInstance().onSend(cmd);
	}


	/**
	 * 添加人员状态
	 **/
	public void onSendAddUserStatus() {
		if (mUserId == null || mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendAddUserStatus (mUserId == null || mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("status", "0");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_USERSTATUS);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendAddUserStatus (cmd == null)");
		}
	}

	/**
	 * 清除人员状态
	 **/
	public void onSendClearUserStatus() {
		if (mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendClearUserStatus (mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("status", "1");

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_USERSTATUS);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendClearUserStatus (cmd == null)");
		}
	}

	/**
	 * 初始化媒体服务（即获取SIP消息）
	 **/
	public void onSendInitMedia() {
		if (mUserId == null || mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendInitMedia (mUserId == null || mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_INITMEDIA_BYCUSTOM);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendInitMedia (cmd == null)");
		}
	}
	
	/**
	 * 初始化移动端编码器部分
	 * 
	 * 返回结果：
	 * {funName:"informInitMobileTerminal",
	 * params:{SIPID:"",serverId:"",serverIP:"",serverPort:"",clientPassword:""}}
	 **/
	public void onSendInitMobileTerminal() {
		if (mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendInitMobileTerminal (mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("clientPassword", "123456");

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_MOBILE_TERMINAL);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendInitMobileTerminal (cmd == null)");
		}
	}

	/**
	 * 订阅人员目录
	 **/
	public void onSendSubscribeUser() {
		if (mUserId == null || mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendSubscribeUser (mUserId == null || mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("organizationID", "");
			subObj.put("subscribeID", "MainOrganizationUser");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_SUBSCRIBE_ORGANIZATION_USER);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendSubscribeUser (cmd == null)");
		}
	}

	/**
	 * 订阅设备目录
	 **/
	public void onSendSubscribeDevice() {
		if (mUserId == null || mTokenKey == null) {
			ToolLog.i("===WebSocketCommand::onSendSubscribeDevice (mUserId == null || mTokenKey == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("organizationID", "");
			subObj.put("subscribeID", "MainOrganizationDevice");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_SUBSCRIBE_ORGANIZATION_DEVICE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendSubscribeDevice (cmd == null)");
		}
	}

	// ===============下面的方法没有判断mUserId和mTokenKey，是因为登录成功后mUserId和mTokenKey不可能为空
	/**
	 * 请求目录下的资源
	 **/
	public void onSendRequestResource(boolean isUser, String dirID) {
		if (dirID == null) {
			ToolLog.i("===WebSocketCommand::onSendRequestResource (funName == null || subscribeID == null || subscribeID == dirID)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);

			subObj.put("dirID", dirID);
			if (isUser){
				//人员
				subObj.put("subscribeID", "MainUsersStatus");
				obj.put("funName", WssUrl.WSS_SUBSCRIBE_USERSSTATUS);
			}else {
				//设备
				subObj.put("subscribeID", "MainDevicesStatus");
				obj.put("funName", WssUrl.WSS_SUBSCRIBE_DEVICESSTATUS);
			}

			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendRequestResource (cmd == null)");
		}
	}

	/**
	 * 发送点播
	 **/
	public void onSendRequestStartPlay(String deviceID, String deviceName) {
		if (deviceID == null || deviceID.isEmpty() || deviceName == null || deviceName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onSendRequestStartPlay (deviceID == null || deviceName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("receiverID", deviceID);
			subObj.put("receiverName", deviceName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_STARTPLAY_DEVICE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendRequestStartPlay (cmd == null)");
		}
	}

	/**
	 * 发送停止点播
	 **/
	public void onSendRequestStopPlay(String deviceID, String deviceName) {
		if (deviceID == null || deviceID.isEmpty() || deviceName == null || deviceName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onSendRequestStopPlay (deviceID == null || deviceName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("receiverID", deviceID);
			subObj.put("receiverName", deviceName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_STOPPLAY_DEVICE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onSendRequestStopPlay (cmd == null)");
		}
	}

	/**
	 * 开启呼叫
	 **/
	public void onWssStartVideoCall(String receiverId, String receiverName, boolean onlyVolice) {

		if (receiverId == null || receiverId.isEmpty() || receiverName == null
				|| receiverName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssStartVideoCall (receiverId == null || receiverName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("onlyVolice", String.valueOf(onlyVolice));
			subObj.put("senderName", mUserName);
			subObj.put("receiverID", receiverId);
			subObj.put("receiverName", receiverName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_STARTCALL);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssStartVideoCall (cmd == null)");
		}
	}

	/**
	 * 停止呼叫
	 **/
	public void onWssStopVideoCall(String receiverId, String receiverName) {

		if (receiverId == null || receiverId.isEmpty() || receiverName == null
				|| receiverName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssStopVideoCall (receiverId == null || receiverName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("senderName", mUserName);
			subObj.put("receiverID", receiverId);
			subObj.put("receiverName", receiverName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_STOPCALL);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssStopVideoCall (cmd == null)");
		}
	}

	/**
	 * 接受呼叫
	 **/
	public void onWssAcceptVideoCall(String receiverId, String receiverName) {

		if (receiverId == null || receiverId.isEmpty() || receiverName == null
				|| receiverName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssAcceptVideoCall (receiverId == null || receiverName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("senderID", mUserId);
			subObj.put("senderName", mUserName);
			subObj.put("receiverID", receiverId);
			subObj.put("receiverName", receiverName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_ACCEPTCALL);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssAcceptVideoCall (cmd == null)");
		}
	}

	/**
	 * 拒绝呼叫
	 **/
	public void onWssRefuseVideoCall(String receiverId, String receiverName) {

		if (receiverId == null || receiverId.isEmpty() || receiverName == null
				|| receiverName.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssRefuseVideoCall (receiverId == null || receiverName == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("senderID", mUserId);
			subObj.put("senderName", mUserName);
			subObj.put("receiverID", receiverId);
			subObj.put("receiverName", receiverName);
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_PUBLISH_REFUSECALL);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssRefuseVideoCall (cmd == null)");
		}
	}

	/**
	 * 取消呼叫
	 **/
	public void onWssCancelVideoCall(String receiverId) {

		if (receiverId == null || receiverId.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssCancelVideoCall (receiverId == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("resourceID", receiverId);
			subObj.put("resourceType", "0");
			subObj.put("type", "Call");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_CANCEL_BUSINESS);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssCancelVideoCall (cmd == null)");
		}
	}


	//=================会议接口================================================

	/**
	 * 创建分组会议
	 *
	 * schemeID：会议ID
	 * isMuted:true静音，false取消静音
	 **/
	public void onWssStartGroupMeeting(String meetingGroupID) {

		if (meetingGroupID == null || meetingGroupID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssStartGroupMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("meetingGroupID", meetingGroupID);
			subObj.put("mediaType", "AV");
			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", "publishStartGroupConference");
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssStartGroupMeeting (cmd == null)");
		}
	}
	/**
	 * 创建会议
	 *
	 * sceneName：会议名称
	 * description：会议描述
	 * schemeID：会议使用方案，默认为空，采用默认方案
	 * isMediaProcessing：是否拼接会议
	 * operatorName：会议开启者名称
	 * needPassword：成员加入会议是否需要密码
	 * password：会议密码
	 * members：成员列表
	 * [{index:0,userID:"", userName:"", resourceType:""}
	 * {index:1, userID:"",userName:"",resourceType:""}]
	 **/
	public void onWssCreateMeeting(String sceneName, String description,
								   boolean isMediaProcessing, String operatorName,
								   boolean needPassword, String password, String members) {

		boolean oldWay = true;
		String cmd = null;
		try {
			if (!oldWay) {
				WssGreatMeeting wssGreatMeeting=new WssGreatMeeting();
				wssGreatMeeting.setCode(1756);
				wssGreatMeeting.setFunName(WssUrl.WSS_METTING_CREATE);
				wssGreatMeeting.setUserID(mUserId);
				WssGreatMeeting.ParamsBean paramsBean=new WssGreatMeeting.ParamsBean();
				paramsBean.setDescription(description);
				paramsBean.setIsMediaProcessing(String.valueOf(isMediaProcessing));
				paramsBean.setMediaType("");
				paramsBean.setNeedPassword(String.valueOf(needPassword));
				paramsBean.setPassword(password);
				paramsBean.setOperatorName(operatorName);
				paramsBean.setSceneName(sceneName);
				List<?> spectators=new ArrayList<>();
				paramsBean.setSpectator(spectators);
				List<?> devices=new ArrayList<>();
				paramsBean.setDevice(devices);
				List<WssGreatMeeting.ParamsBean.MembersBean> been=new ArrayList<>();
				paramsBean.setMembers(been);
				paramsBean.setOperatorToken(mTokenKey);
				paramsBean.setSchemeID("");
				wssGreatMeeting.setParams(paramsBean);

				cmd=FastJsonTools.bean2Json(wssGreatMeeting);
				Log.i("----mTokenKey-----", "onWssCreateMeeting: mTokenKey="+mTokenKey);
			} else {
				JSONObject subObj = new JSONObject();
				subObj.put("sceneName", sceneName);
				subObj.put("description", description);
				subObj.put("schemeID", "");
				subObj.put("isMediaProcessing", String.valueOf(isMediaProcessing));
				subObj.put("operatorName", operatorName);
				subObj.put("needPassword", String.valueOf(needPassword));
				subObj.put("password", password);
				if (members !=null && members.length()>0){
					subObj.put("members", members);
				}else {
					subObj.put("members", new JSONArray("[]"));
				}

				subObj.put("device", new JSONArray("[]"));
				subObj.put("spectator", new JSONArray("[]"));
				subObj.put("mediaType", "AV");
				subObj.put("operatorToken", mTokenKey);

				JSONObject obj = new JSONObject();
				obj.put("code", "1756");
				obj.put("userID", mUserId);
				obj.put("funName", WssUrl.WSS_METTING_CREATE);
				obj.put("params", subObj);

				cmd = obj.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssCreateMeeting (cmd == null)");
		}
	}

	/**
	 * 主席停止会议
	 *
	 * schemeID：会议ID
	 **/
	public void onWssStopMeeting(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssStopMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();

			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_STOP);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssStopMeeting (cmd == null)");
		}
	}

	/**
	 * 主席添加成员
	 *
	 * schemeID：会议ID
	 * members：成员列表
	 * [{index:0, userID:"",userName:"", resourceType:""}
	 * {index:1, userID:"", userName:"",resourceType:""}]
	 **/
	public void onWssAddMembers(String schemeID, ArrayList<SipInfo> members) {

		if (schemeID == null || schemeID.isEmpty() || members == null
				|| members.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssAddMembers (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			ArrayList<YqcyParms> list=new ArrayList<YqcyParms>();
			if (members.size()>0){
				YqcyParms yqcyParms=new YqcyParms();
				YqcyParms.ParamsBean params = new YqcyParms.ParamsBean();
				params.setSceneID(schemeID);
				params.setOperatorToken(mTokenKey);
				List<YqcyParms.ParamsBean.MembersBean> been=new ArrayList<YqcyParms.ParamsBean.MembersBean>();
				for (int i = 0; i < members.size(); i++) {

					YqcyParms.ParamsBean.MembersBean bean=new YqcyParms.ParamsBean.MembersBean();
					bean.setUserID(members.get(i).getUserid());
					bean.setIndex(i);
					bean.setUserName(members.get(i).getUsername());
					bean.setResourceType("User");
					been.add(bean);
				}
				params.setMembers(been);
				yqcyParms.setCode(1756);
				yqcyParms.setUserID(mUserId);
				yqcyParms.setFunName(WssUrl.WSS_METTING_ZX_ADD_PEOPLE);
				yqcyParms.setParams(params);
				cmd = FastJsonTools.bean2Json(yqcyParms);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssAddMembers (cmd == null)");
		}
	}

	/**
	 * 主席删除成员
	 *
	 * schemeID：会议ID
	 * memberIDs：成员列表集合["","",...]
	 **/
	public void onWssRemoveMembers(String schemeID, List<String> memberIDs) {

		if (schemeID == null || schemeID.isEmpty() || memberIDs == null
				|| memberIDs.size()==0) {
			ToolLog.i("===WebSocketCommand::onWssRemoveMembers (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			Gson gson=new Gson();
			subObj.put("memberIDs", gson.toJson(memberIDs));

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_DELET_PEOPLE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssRemoveMembers (cmd == null)");
		}
	}

	/**
	 * 主席指定发言人
	 *
	 * schemeID：会议ID
	 * memberID：成员ID
	 **/
	public void onWssSetSpeaker(String schemeID, String memberID) {

		if (schemeID == null || schemeID.isEmpty() || memberID == null
				|| memberID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssSetSpeaker (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("memberID", memberID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_ZDFYR);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssSetSpeaker (cmd == null)");
		}
	}

	/**
	 * 主席收回发言
	 *
	 * schemeID：会议ID
	 **/
	public void onWssCancelSpeaker(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssCancelSpeaker (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_SHFY);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssCancelSpeaker (cmd == null)");
		}
	}

	/**
	 * 主席接受申请发言
	 *
	 * schemeID：会议ID
	 * memberID：成员ID
	 **/
	public void onWssAcceptSpeaker(String schemeID, String memberID) {

		if (schemeID == null || schemeID.isEmpty() || memberID == null
				|| memberID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssAcceptSpeaker (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("memberID", memberID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_JSSQFY);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssAcceptSpeaker (cmd == null)");
		}
	}

	/**
	 * 主席拒绝发言申请
	 *
	 * schemeID：会议ID
	 * memberID：成员ID
	 *
	 * 此接口暂未实现
	 **/
	public void onWssRefuseSpeaker(String schemeID, String memberID) {

		if (schemeID == null || schemeID.isEmpty() || memberID == null
				|| memberID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssRefuseSpeaker (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("memberID", memberID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_ZX_JJFYSQ);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssRefuseSpeaker (cmd == null)");
		}
	}

	/**
	 * 成员加入会议（目前不用）
	 *
	 * schemeID：会议ID
	 **/
	public void onWssMemberJoinMeeting(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberJoinMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("isSpectator", "false");

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_FSSY_JOIN);
			obj.put("params", subObj);

			cmd = obj.toString();
			PLog.d("WebSocketCommand","---------成员加入会议的参数------>cmd ="+cmd);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberJoinMeeting (cmd == null)");
		}
	}

	/**
	 * 成员申请发言
	 *
	 * schemeID：会议ID
	 **/
	public void onWssMemberApplySpeak(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberApplySpeak (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("isCancel", String.valueOf(false));

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_CY_SQFY);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberApplySpeak (cmd == null)");
		}
	}

	/**
	 * 成员取消发言
	 *
	 * schemeID：会议ID
	 **/
	public void onWssMemberCancelSpeak(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberCancelSpeak (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("isCancel", String.valueOf(true));

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_CY_QXFY);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberCancelSpeak (cmd == null)");
		}
	}

	/**
	 * 成员退出会议
	 *
	 * schemeID：会议ID
	 **/
	public void onWssMemberLeaveMeeting(String schemeID) {

		if (schemeID == null || schemeID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_CY_LEAVE_METTING);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (cmd == null)");
		}
	}

	public void onWssSendCommunicationMessage(List<String> receiverList, String content) {
		if (receiverList == null || receiverList.size() < 0) {
			ToolLog.i("===WebSocketCommand::onWssSendCommunicationMessage (receiverIDs == null)");
			return;
		}
		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			JSONArray jsonArray = new JSONArray(receiverList);
			subObj.put("receiverIDs", jsonArray);
			subObj.put("content", content);
			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_SEND_COMMUNICATION_MESSAGE);
			obj.put("params", subObj);
			cmd = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssSendCommunicationMessage (cmd == null)");
		}
	}
	public void onWssSendCommunicationMessageFromSession(String sessionID, String content) {
		if (sessionID == null || sessionID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssSendCommunicationMessageFromSession (sessionID == null)");
			return;
		}
		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sessionID", sessionID);
			subObj.put("content", content);
			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_SEND_COMMUNICATION_MESSAGE_FROM_SESSION);
			obj.put("params", subObj);
			cmd = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssSendCommunicationMessage (cmd == null)");
		}
	}

	/**
	 * 通过关键字查询人员
	 *
	 * userKey：设备key
	 **/
	public void onWssQueryDeviceByKey(String deviceKey) {

		if (deviceKey == null || deviceKey.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("key", deviceKey);
			subObj.put("subscribeID", "MainDevicesStatusByKey");
			subObj.put("organizationID", "");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_CONTACTS_QUERY_DEVICE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (cmd == null)");
		}
	}


	/**
	 * 通过关键字查询设备
	 *
	 * userKey：人员key
	 **/
	public void onWssQueryPeopleByKey(String userKey) {

		if (userKey == null || userKey.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (schemeID == null)");
			return;
		}

		String cmd = null;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("key", userKey);
			subObj.put("subscribeID", "MainUsersStatusByKey");
			subObj.put("organizationID", "");
			subObj.put("operatorToken", mTokenKey);

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_CONTACTS_QUERY_PEOPLE);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssMemberLeaveMeeting (cmd == null)");
		}
	}
//	/**
//	 * 成员申请入会
//	 *
//	 * schemeID：会议ID
//	 * isSpectator:是否为旁观者，填false
//	 * applyUserName:申请者名称
//	 * password:会议密码
//	 **/
//	public void onWssApplyJoinMeeting(String schemeID, boolean isSpectator,
//									  String applyUserName, String password) {
//
//		if (schemeID == null || schemeID.isEmpty() || applyUserName == null
//				|| applyUserName.isEmpty()) {
//			ToolLog.i("===WebSocketCommand::onWssApplyJoinMeeting (schemeID == null)");
//			return;
//		}
//
//		String cmd = null;
//		try {
//			JSONObject subObj = new JSONObject();
//			subObj.put("operatorToken", mTokenKey);
//			subObj.put("sceneID", schemeID);
//			subObj.put("isSpectator", String.valueOf(isSpectator));
//			subObj.put("applyUserName", applyUserName);
//			subObj.put("password", password);
//
//			JSONObject obj = new JSONObject();
//			obj.put("code", "1756");
//			obj.put("userID", mUserId);
//			obj.put("funName", WssUrl.WSS_METTING_CY_JOIN);
//			obj.put("params", subObj);
//
//			cmd = obj.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			cmd = null;
//		}
//		if (cmd != null) {
//			IWebSocketClientService.getInstance().onSend(cmd);
//		} else {
//			ToolLog.i("===WebSocketCommand::onWssApplyJoinMeeting (cmd == null)");
//		}
//	}

	/**
	 * 发送同意成员入会（主席端后台实现，界面无感知）
	 *
	 * schemeID：会议ID
	 * applyUserID：允许的用户ID
	 * isAgree：true-同意 false-拒绝
	 *
	 **/
	public void onWssAnswerApplyJoin(String schemeID, String applyUserID) {

		if (schemeID == null || schemeID.isEmpty() || applyUserID == null
				|| applyUserID.isEmpty()) {
			ToolLog.i("===WebSocketCommand::onWssAnswerApplyJoin (schemeID == null)");
			return;
		}

		String cmd = null;
		boolean isAgress=true;
		try {
			JSONObject subObj = new JSONObject();
			subObj.put("operatorToken", mTokenKey);
			subObj.put("sceneID", schemeID);
			subObj.put("applyUserID", applyUserID);
			subObj.put("isAgress", String.valueOf(isAgress));

			JSONObject obj = new JSONObject();
			obj.put("code", "1756");
			obj.put("userID", mUserId);
			obj.put("funName", WssUrl.WSS_METTING_FSTY_CY_JOIN);
			obj.put("params", subObj);

			cmd = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			cmd = null;
		}
		if (cmd != null) {
			IWebSocketClientService.getInstance().onSend(cmd);
		} else {
			ToolLog.i("===WebSocketCommand::onWssAnswerApplyJoin (cmd == null)");
		}
	}

//	/**
//	 * 发送受邀入会指令
//	 *
//	 * schemeID：会议ID
//	 * isSpectator:true旁观者，false非旁观者
//	 **/
//	public void onWssInviteJoin(String schemeID, boolean isSpectator) {
//
//		if (schemeID == null || schemeID.isEmpty()) {
//			ToolLog.i("===WebSocketCommand::onWssInviteJoin (schemeID == null)");
//			return;
//		}
//
//		String cmd = null;
//		try {
//			JSONObject subObj = new JSONObject();
//			subObj.put("operatorToken", mTokenKey);
//			subObj.put("sceneID", schemeID);
//			subObj.put("isSpectator", String.valueOf(isSpectator));
//
//			JSONObject obj = new JSONObject();
//			obj.put("code", "1756");
//			obj.put("userID", mUserId);
//			obj.put("funName", WssUrl.WSS_METTING_FSSY_JOIN);
//			obj.put("params", subObj);
//
//			cmd = obj.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			cmd = null;
//		}
//		if (cmd != null) {
//			IWebSocketClientService.getInstance().onSend(cmd);
//		} else {
//			ToolLog.i("===WebSocketCommand::onWssInviteJoin (cmd == null)");
//		}
//	}

//	/**
//	 * 设置会议暂停
//	 *
//	 * schemeID：会议ID
//	 * isPaused：true暂停，false取消暂停
//	 **/
//	public void onWssSetPaused(String schemeID, boolean isPaused) {
//
//		if (schemeID == null || schemeID.isEmpty()) {
//			ToolLog.i("===WebSocketCommand::onWssSetPaused (schemeID == null)");
//			return;
//		}
//
//		String cmd = null;
//		try {
//			JSONObject subObj = new JSONObject();
//			subObj.put("operatorToken", mTokenKey);
//			subObj.put("sceneID", schemeID);
//			subObj.put("isPaused", String.valueOf(isPaused));
//
//			JSONObject obj = new JSONObject();
//			obj.put("code", "1756");
//			obj.put("userID", mUserId);
//			obj.put("funName", WssUrl.WSS_METTING_SETTING_STOP);
//			obj.put("params", subObj);
//
//			cmd = obj.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			cmd = null;
//		}
//		if (cmd != null) {
//			IWebSocketClientService.getInstance().onSend(cmd);
//		} else {
//			ToolLog.i("===WebSocketCommand::onWssSetPaused (cmd == null)");
//		}
//	}

//	/**
//	 * 设置会议静音
//	 *
//	 * schemeID：会议ID
//	 * isMuted:true静音，false取消静音
//	 **/
//	public void onWssSetMuted(String schemeID, boolean isMuted) {
//
//		if (schemeID == null || schemeID.isEmpty()) {
//			ToolLog.i("===WebSocketCommand::onWssSetMuted (schemeID == null)");
//			return;
//		}
//
//		String cmd = null;
//		try {
//			JSONObject subObj = new JSONObject();
//			subObj.put("operatorToken", mTokenKey);
////			subObj.put("schemeID", schemeID);
//			subObj.put("sceneID", schemeID);
//			subObj.put("isMuted", String.valueOf(isMuted));
//
//			JSONObject obj = new JSONObject();
//			obj.put("code", "1756");
//			obj.put("userID", mUserId);
//			obj.put("funName", WssUrl.WSS_METTING_SETTING_JY);
//			obj.put("params", subObj);
//
//			cmd = obj.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			cmd = null;
//		}
//		if (cmd != null) {
//			IWebSocketClientService.getInstance().onSend(cmd);
//		} else {
//			ToolLog.i("===WebSocketCommand::onWssSetMuted (cmd == null)");
//		}
//	}
}
