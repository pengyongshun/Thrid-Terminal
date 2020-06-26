package com.xtmedia.xtsip;

/*
 * UAS处理invite回调函数
 */

public interface XTSipServerInviteCallBack
{
	// enum xt_sip_invite_terminated_reason_t
	// {
	// XT_SIP_TERMINATED_REASON_ERROR,
	// XT_SIP_TERMINATED_REASON_TIMEOUT,
	// XT_SIP_TERMINATED_REASON_REPLACED,
	// XT_SIP_TERMINATED_REASON_LOCAL_BYE,
	// XT_SIP_TERMINATED_REASON_REMOTE_BYE,
	// XT_SIP_TERMINATED_REASON_LOCAL_CANCEL,
	// XT_SIP_TERMINATED_REASON_REMOTE_CANCEL,
	// XT_SIP_TERMINATED_REASON_REJECTED,
	// XT_SIP_TERMINATED_REASON_REFERRED
	// };

	// 注册服务的相关操作
	enum _xt_sip_server_register_operation_t
	{
		XT_SIP_SERVER_REGISTER_REFRESH, // ˢ��
		XT_SIP_SERVER_REGISTER_REMOVE, // ע��
		XT_SIP_SERVER_REGISTER_REMOVE_ALL, // ע�����а�
		XT_SIP_SERVER_REGISTER_ADD, // ע��
		XT_SIP_SERVER_REGISTER_QUERY // ��ѯ����Ϣ
	};

	// 注册回调
	void xt_sip_server_register_request_callback_t(long h, long msg,
												   int operation);

	// 鉴权
	void xt_sip_server_auth_request_credential_callback_t(long h, long msg);

	int xt_sip_server_auth_requires_challenge_callback_t(long msg);

	int xt_sip_server_auth_proxy_mode_callback_t(long msg);

	// 呼叫相关
	void xt_sip_server_invite_offer_callback_t(long h, long msg, String sdp,
											   int len);

	void xt_sip_server_invite_answer_callback_t(long h, long msg, String sdp,
												int len);

	void xt_sip_server_invite_terminated_callback_t(long h, long msg, int reason);

	void xt_sip_server_invite_connected_confirmed_callback_t(long h, long msg);

	void xt_sip_server_invite_ack_received_callback_t(long h, long msg);

	void xt_sip_server_invite_offer_required_callback_t(long h, long msg);

	void xt_sip_server_invite_info_callback_t(long h, long msg);

	void xt_sip_server_invite_info_response_callback_t(long h, long msg,
													   byte success);

	void xt_sip_server_invite_message_callback_t(long h, long msg);

	void xt_sip_server_invite_message_response_callback_t(long h, long msg,
														  byte success);

	// 会话外message
	void xt_sip_server_message_arrived_callback_t(long h, long msg);

	// 会话外option
	void xt_sip_server_out_of_dialog_receive_request_cb(long h, long msg);

	// 订阅
	void xt_sip_server_subscription_new_subscription_callback_t(long h, long sub);

	void xt_sip_server_subscription_new_subscription_from_refer_callback_t(
			long h, long sub);

	void xt_sip_server_subscription_refresh_callback_t(long h, long sub);

	void xt_sip_server_subscription_terminated_callback_t(long h);

	void xt_sip_server_subscription_ready_to_send_callback_t(long h, long msg);

	void xt_sip_server_subscription_notify_rejected_callback_t(long h, long msg);

	void xt_sip_server_subscription_error_callback_t(long h, long msg);

	void xt_sip_server_subscription_expired_by_client_callback_t(long h,
																 long sub, long notify);

	void xt_sip_server_subscription_expired_callback_t(long h, long notify);

	// 发布
	void xt_sip_server_publication_initial_callback_t(long h, long pub,
													  String contents, int expires);

	void xt_sip_server_publication_expired_callback_t(long h);

	void xt_sip_server_publication_refresh_callback_t(long h, long pub,
													  String contents, int expires);

	void xt_sip_server_publication_update_callback_t(long h, long pub,
													 String contents, int expires);

	void xt_sip_server_publication_removed_callback_t(long h, long pub,
													  int expires);
}