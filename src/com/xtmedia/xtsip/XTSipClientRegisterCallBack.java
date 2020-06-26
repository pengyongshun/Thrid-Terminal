package com.xtmedia.xtsip;

/*
 * UAC发送注册回调函数
 */

public interface XTSipClientRegisterCallBack
{
	/**
	 * uac注册响应的回调函数，success=0表示失败else成功
	 * 
	 * @param h
	 * @param msg
	 * @param success
	 */
	void xt_sip_client_register_response_callback_t(long h, long msg,
													byte success);

	// uac注销响应的回调函数
	void xt_sip_client_register_removed_callback_t(long h, long msg);

	// uac重复注册交互的回调函数，返回0表示立刻重新注册，返回<0表示不再重复注册，返回>0表示多少秒之后重新注册
	int xt_sip_client_register_request_retry_callback_t(long h, int retrysec,
														long msg);
}