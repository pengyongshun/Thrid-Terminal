package com.xtmedia.xtsip;

public interface XTSipClientPublicationCallBack
{
	void xt_sip_client_publication_response_callback_t(long h, long msg,
													   byte success);

	void xt_sip_client_publication_removed_callback_t(long h, long msg);

	int xt_sip_client_publication_request_retry_callback_t(long h,
														   int retrysec, long msg);
}