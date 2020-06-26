package com.xtmedia.xtsip;

public class SipNative {
	static {
		try {
			System.loadLibrary("xt_sip");
			System.loadLibrary("xtsip");
			System.loadLibrary("z");
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	/*
	 * 创建SIP对象，返回SIP对象
	 */
	public static native int XtSipCreate(int udpport, int tcpport,
			String realm, String user, String pwd, String domain,
			int sessiontime, XTSipServerInviteCallBack cbobj, int time_t1,
			boolean client);

	/*
	 * 销毁SIP对象
	 */
	public static native void XtSipDestroy(int sip);

	/*
	 * 注册SIP expires 注册有效时间
	 */

	public static native int XtSipRegister(int sip, String target, String sdp,
			int length, int timeout, int expires,
			XTSipClientRegisterCallBack cbobj);

	/*
	 * 拷贝SIP注册句柄
	 */
	public static native long XtSipClientRegisterHandleClone(long h);

	/*
	 * 删除SIP注册句柄
	 */
	public static native void XtSipClientRegisterHandleDelete(long h);

	/*
	 * 注销
	 */
	public static native int XtSipRegisterRemoveAll(long h);

	/*
	 * 注销
	 */
	public static native int XtSipRegisterRemoveMyBindings(long h);

	// 注册成功之后再次刷新注册信息（此函数目前只能在回调函数中调用，线程外会失败）
	public static native int XtSipClientRegisterRequestRefresh(long h,
			int expires);

	/*
	 * 发送invite命令
	 */
	public static native int XtSipInvite(int sip, String target,
			String subject, String sdp, int length, int timeout,
			XTSipClientInviteCallBack cbobj, boolean zip);

	/*
	 * 发送不带SDP的invite
	 */
	public static native int XtSipInviteNoSdp(int sip, String target,
			int timeout, XTSipClientInviteCallBack cbobj);

	/*
	 * 拷贝invite句柄
	 */
	public static native long XtSipClientInviteHandleClone(long h);

	/*
	 * 删除invite句柄
	 */
	public static native void XtSipClientInviteHandleDelete(long h);

	/*
	 * 执行bye操作，即结束会话param [in] h 呼叫会话句柄returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipClientInviteEnd(long h);

	/*
	 * 执行bye操作带原因，即结束会话param [in] h 呼叫会话句柄returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipClientInviteEndWithReason(long h,
			String reason);

	/*
	 * 收到uas的200ok带sdp的offer后调用提供answerparam [in] h 呼叫会话句柄param [in] sdpparam
	 * [in] len sdp字节长度returns
	 */
	public static native int XtSipInviteProvideAnswer(long h, String sdp,
			int len);

	/*
	 * 发送info
	 */
	public static native int XtSipClientInviteInfo(long h, String content_type,
			String content, int len);

	/*
	 * 发送message
	 */
	public static native int XtSipClientInviteMessage(long h,
			String content_type, String content, int len);

	/*
	 * 接受对端发送过来的message/info消息，即响应2xx操作
	 */
	public static native int XtSipClientInviteAcceptNIT(long h,
			String content_type, String content, int len);

	public static native int XtSipClientInviteRejectNIT(long h, int code);

	/*
	 * 重新发送invete
	 */
	public static native int XtSipClientInviteReinvite(long h, String sdp,
			int len);

	/*
	 * 重新发送不带SDP的invite
	 */
	public static native int XtSipClientInviteReinviteNoSdp(long h);

	/*********************************************** uas被呼叫函数 server ***************************************/
	/*
	 * 呼叫会话句柄的克隆函数，需要调用释放函数。回调函数中的句柄参数是不能在函数外使用的，需要通过此函数拷贝后使用 param [in] h
	 * 呼叫会话句柄 returns xt_sip_server_invite_handle_t 克隆之后的会话句柄
	 */
	public static native long XtSipServerInviteHandleClone(long h);

	/*
	 * 呼叫会话句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h 呼叫会话句柄 returns
	 */
	public static native void XtSipServerInviteHandleDelete(long h);

	/*
	 * 接受offer呼叫并响应answer操作 param [in] h 呼叫会话句柄 param [in] sdp param [in]
	 * sdp字节长度 returns xt_sip_status_t 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteProvideAnswer(long h, String sdp,
			int len);

	/*
	 * 接受request_offer呼叫并发起offer操作 param [in] h 呼叫会话句柄 param [in] sdp param [in]
	 * sdp字节长度 returns xt_sip_status_t 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteProvideOffer(long h, String sdp,
			int len);

	/*
	 * 拒绝呼叫操作不可在回调中直接调用 param [in] h 呼叫会话句柄 param [in] code 错误码 4xx returns
	 * xt_sip_status_t 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteReject(long h, int code);

	/*
	 * 执行bye操作，即结束会话 param [in] h 呼叫会话句柄 returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteEnd(long h);

	/*
	 * 执行bye操作带原因，即结束会话 param [in] h 呼叫会话句柄 returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteEndWithReason(long h,
			String reason);

	/*
	 * 发送会话内info消息 param [in] h 呼叫会话句柄 param [in] content_type 内容类型 param [in]
	 * content 内容 param [in] content_length 内容字节长度 returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteInfo(long h, String content_type,
			String content, int content_len);

	/*
	 * 发送会话内message消息 param [in] h 呼叫会话句柄 param [in] content_type 内容类型 param
	 * [in] content 内容 param [in] content_length 内容字节长度 returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipServerInviteMessage(long h,
			String content_type, String content, int content_len);

	/*
	 * 接受对端发送过来的message/info消息，即响应2xx操作param [in] h 呼叫会话句柄param [in]
	 * content_type 内容类型param [in] content 内容param [in] content_length 内容字节长度
	 * returns xt_sip_status_t
	 */
	public static native int XtSipServerInviteAcceptNIT(long h, int code,
			String content_type, String content, int content_len);

	/*
	 * 拒绝对端发送过来的message/info消息，即响应非2xx操作param [in] h 呼叫会话句柄param [in] code 响应码
	 * 4xxreturns xt_sip_status_t
	 */
	public static native int XtSipServerInviteRejecttNIT(long h, int code);

	/*
	 * 启动sip options心跳（非线程安全，需要在xt_sip库的回调函数中调用） param [in] sip sip对象句柄 param
	 * [in] ping_timeout ping超时时间 毫秒 param [in] pong_timeout pong超时时间 毫秒 param
	 * [in] cb心跳回调，当收不到pong时调用，cb返回0表示处理 param [in] ctx用户参数 returns
	 * xt_sip_status_t
	 */
	public static native int XtSipHeartbeatAddTarget(int sip, String target,
			int ping_timeout, int pong_timeout, XTSipOptionsCallBack cb);

	/*
	 * 停止sip options心跳（非线程安全，需要在xt_sip库的回调函数中调用） param [in] sip sip对象句柄 param
	 * [in] ping_timeout ping超时时间 毫秒 param [in] pong_timeout pong超时时间 毫秒 param
	 * [in] cb心跳回调，当收不到pong时调用，cb返回0表示处理 param [in] ctx用户参数 returns
	 * xt_sip_status_t
	 */
	public static native int XtSipHeartbeatRemoveTarget(int sip, String target);

	/********************************************* 会话外message *****************************************************/
	/*
	 * 发送message请求，支持同步超时，支持异步，异步超时功能暂不支持 param [in] sip sip对象句柄 param [in]
	 * request message请求 param [in,out] response message响应，为NULL时表示异步操作 param
	 * [in] timeout 超时时间，单位：毫秒 param [in] cbobj 同步、异步模式下的回调函数 returns
	 * xt_sip_status_t
	 */
	public static native int XtSipMakeClientMessage(int sip,
			XTSipMessageRequestInfo request_msg, int timeout,
			XTSipClientMessageCallBack cbobj);

	/*
	 * 会话外message句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h message会话句柄
	 */
	public static native void XtSipClientMessageHandleDelete(long h);

	/*
	 * 会话外message句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h message会话句柄
	 */
	public static native void XtSipServerMessageHandleDelete(long h);

	/*
	 * 接受message请求 param [in] h 呼叫会话句柄 param [in] code 响应码 2xx returns
	 * xt_sip_status_t
	 */
	public static native int XtSipServerMessageAccept(long h, int code);

	/*
	 * 拒绝message请求 param [in] h 呼叫会话句柄 param [in] code 响应码 4xx returns
	 * xt_sip_status_t
	 */
	public static native int XtSipServerMessageReject(long h, int code);

	/*
	 * 句柄克隆操作 param [in] h message会话句柄 xt_sip_server_message_handle_t clone之后的句柄
	 */
	public static native long XtSipServerMessageHandleClone(long h);

	// qin
	public static native long XtSipMsgClone(long msg);

	public static native void XtSipMsgDelete(long msg);

	//
	/*
	 * 通过msg句柄获取返回的from用户域
	 */
	public static native String XtSipMsgGetFromStr(long msg);

	/*
	 * 通过msg句柄获取返回的To用户域
	 */
	public static native String XtSipMsgGetToStr(long msg);

	/*
	 * 通过msg句柄获取返回的内容体
	 */
	public static native String XtSipMsgGetContentBody(long msg);

	/*
	 * 通过msg句柄获取返回的内容类型
	 */
	public static native String XtSipMsgGetContentType(long msg);

	/*
	 * 通过msg句柄获取session-id
	 */
	public static native String XtSipMsgGetCallID(long msg);

	/*
	 * 通过msg句柄获取会话被拒原因值
	 */
	public static native String XtSipMsgGetStatus(long msg);

	/*
	 * 通过msg句柄获取会话挂断原因值
	 */
	public static native String XtSipMsgGetReason(long msg);

	/*
	 * 通过msg句柄获取URI
	 */
	public static native String XtSipMsgGetURI(long msg);

	/*
	 * 通过msg句柄获取Subject
	 */
	public static native String XtSipMsgGetSubject(long msg);

	/*
	 * 通过msg句柄获取Contacts
	 */
	public static native String XtSipMsgGetContacts(long msg);

	/*
	 * 通过msg句柄获取XML中userid
	 */
	public static native String XtSipMsgGetXmlUserId(long msg);

	/*
	 * 通过msg句柄获取XML中status
	 */
	public static native String XtSipMsgGetXmlStatus(long msg);

	/************************************************* 发布与订阅 *************************************/
	/*
	 * 发送subscribe请求，支持同步超时，支持异步，异步超时功能暂不支持 param [in] sip sip对象句柄 param [in]
	 * request message请求 param [in,out] response message响应，为NULL时表示异步操作 param
	 * [in] timeout 超时时间，单位：毫秒 param [in] cbobj 同步、异步模式下的回调函数 returns
	 * xt_sip_status_t
	 */
	public static native int XtSipMakeClientSubscription(int sip,
			XTSipClientSubscriptionRequestInfo request_subscribe,
			long[] hResponse, int timeout, XTSipClientSubscriptionCallBack cbobj);

	/*
	 * client subscription句柄的克隆函数，需要调用释放函数。回调函数中的句柄参数是不能在函数外使用的，需要通过此函数拷贝后使用
	 * param [in] h 句柄 returns xt_sip_client_subscription_handle_t 克隆之后的会话句柄
	 */
	public static native long XtSipClientSubscriptionHandleClone(long h);

	/*
	 * client subscription句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h 句柄
	 */
	public static native void XtSipClientSubscriptionHandleDelete(long h);

	/*
	 * client 结束subscription param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipClientSubscriptionEnd(long h);

	/*
	 * client 接受subscription 200ok param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipClientSubscriptionAccept(long h);

	/*
	 * client 拒绝subscription param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipClientSubscriptionReject(long h);

	/**********************************************************************************************/
	/*
	 * server句柄的克隆函数，需要调用释放函数。回调函数中的句柄参数是不能在函数外使用的，需要通过此函数拷贝后使用 param [in] h 句柄
	 * returns xt_sip_server_subscription_handle_t 克隆之后的会话句柄
	 */
	public static native long XtSipServerSubscriptionHandleClone(long h);

	/*
	 * server句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h 句柄
	 */
	public static native void XtSipServerSubscriptionHandleDelete(long h);

	/*
	 * server 结束subscription param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerSubscriptionEnd(long h);

	/*
	 * server 接受subscription 200ok param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerSubscriptionAccept(long h);

	/*
	 * server 拒绝subscription param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerSubscriptionReject(long h);

	/*
	 * 设置被发送notify的target。使用tcp协议且存在NAT时，需调用此接口更改notify的target
	 */
	public static native int XtSipServerSubscriptionSetNotifyTarget(long h,
			String target);

	/*
	 * 发送notify,默认情况下target从subscribe消息的contact头中取 param [in] h 呼叫会话句柄 param
	 * [in] content_type 内容类型 param [in] content 内容 param [in] content_length
	 * 内容字节长度 returns xt_sip_status_t
	 */
	public static native int XtSipServerSubscriptionUpdate(long h,
			String content_type, String content, int content_length);

	/*******************************************************************************************************/
	/*
	 * 发送publish请求，支持同步超时，支持异步，异步超时功能暂不支持 param [in] sip sip对象句柄 param [in]
	 * request message请求 param [in,out] response message响应，为NULL时表示异步操作 param
	 * [in] timeout 超时时间，单位：毫秒 param [in] cbobj 同步、异步模式下的回调函数 returns
	 * xt_sip_status_t
	 */
	public static native int XtSipMakeClientPublication(int sip,
			XTSipClientPublicationRequestInfo request_publish,
			long[] hResponse, int timeout, XTSipClientPublicationCallBack cbobj);

	/*
	 * client publication句柄的克隆函数，需要调用释放函数。回调函数中的句柄参数是不能在函数外使用的，需要通过此函数拷贝后使用
	 * param [in] h 句柄 returns xt_sip_client_publication_handle_t 克隆之后的会话句柄
	 */
	public static native long XtSipClientPublicationHandleClone(long h);

	/*
	 * client publication句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h 句柄 returns
	 */
	public static native void XtSipClientPublicationHandleDelete(long h);

	/*
	 * client 结束publication param [in] h 句柄 returns xt_sip_status_t
	 * 错误码参考xt_sip_api_types.h
	 */
	public static native int XtSipClientPublicationEnd(long h);

	/****************************************************************************************************************/
	/*
	 * server句柄的克隆函数，需要调用释放函数。回调函数中的句柄参数是不能在函数外使用的，需要通过此函数拷贝后使用 param [in] h 句柄
	 * returns xt_sip_server_publication_handle_t 克隆之后的会话句柄
	 */
	public static native long XtSipServerPublicationHandleClone(long h);

	/*
	 * server句柄的删除函数。不要对回调函数中的句柄参数使用 param [in] h 句柄 returns
	 */
	public static native void XtSipServerPublicationHandleDelete(long h);

	/*
	 * server 结束publication param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerPublicationEnd(long h);

	/*
	 * server 接受publication 200ok param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerPublicationAccept(long h);

	/*
	 * server 拒绝publication param [in] h 句柄 returns xt_sip_status_t
	 */
	public static native int XtSipServerPublicationReject(long h);

	/*
	 * cancel
	 */
	public static native int XtSipMakeCancel(int sip, long request);

	public static native int XtSipUserAuthSetResult(long h, int mode,
			String credential, int len);

	public static native void XtSipCompress(long handle);

	public static native void XtSipUncompress(long handle);
	
	public static native String XtSipUncompressSdp(long handle);
}