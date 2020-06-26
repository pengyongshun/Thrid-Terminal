package com.xtmedia.xtsip;

public interface XTSipClientMessageCallBack
{
	void xt_sip_client_message_response_callback_t(long h, long msg,
												   byte success);
}