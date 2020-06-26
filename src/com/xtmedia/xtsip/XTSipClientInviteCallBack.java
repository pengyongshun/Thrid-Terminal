package com.xtmedia.xtsip;

/*
 * UAC发送invite回调函数
 */

public interface XTSipClientInviteCallBack
{
	enum xt_sip_invite_terminated_reason_t
	{
		/**
		 * 远端拒绝会话,本地发送invite失败
		 */
		XT_SIP_TERMINATED_REASON_ERROR, XT_SIP_TERMINATED_REASON_TIMEOUT, XT_SIP_TERMINATED_REASON_REPLACED,
		/**
		 * 本地发送bye取消会话
		 */
		XT_SIP_TERMINATED_REASON_LOCAL_BYE,
		/**
		 * 接收到bye
		 */
		XT_SIP_TERMINATED_REASON_REMOTE_BYE, XT_SIP_TERMINATED_REASON_LOCAL_CANCEL, XT_SIP_TERMINATED_REASON_REMOTE_CANCEL,
		/**
		 * 本地拒绝会话
		 */
		XT_SIP_TERMINATED_REASON_REJECTED, XT_SIP_TERMINATED_REASON_REFERRED
	};

	void xt_sip_client_invite_failure_callback_t(long h, long msg);

	void xt_sip_client_invite_answer_callback_t(long h, long msg, String sdp,
												int len);

	void xt_sip_client_invite_offer_callback_t(long h, long msg, String sdp,
											   int len);

	void xt_sip_client_invite_terminated_callback_t(long h, long msg, int reason);

	void xt_sip_client_invite_info_callback_t(long h, long msg);

	void xt_sip_client_invite_info_response_callback_t(long h, long msg,
													   byte success);

	void xt_sip_client_invite_message_callback_t(long h, long msg);

	void xt_sip_client_invite_message_response_callback_t(long h, long msg,
														  byte success);

	void xt_sip_client_msg_prev_post_callback_t(long msg);
}