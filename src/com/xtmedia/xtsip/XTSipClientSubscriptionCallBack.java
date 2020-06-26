package com.xtmedia.xtsip;

public interface XTSipClientSubscriptionCallBack
{
	void xt_sip_client_subscription_update_pending_callback_t(long h,
															  long notify, Boolean outOfOrder);

	void xt_sip_client_subscription_update_active_callback_t(long h,
															 long notify, int outOfOrder);

	// void xt_sip_client_subscription_update_active_callback_t(long h,
	// long notify);

	void xt_sip_client_subscription_update_extension_callback_t(long h,
																long notify, Boolean outOfOrder);

	void xt_sip_client_subscription_notify_not_received_callback_t(long h);

	void xt_sip_client_subscription_terminated_callback_t(long h, long notify);

	void xt_sip_client_subscription_new_subscription_callback_t(long h,
																long notify);

	int xt_sip_client_subscription_request_retry_callback_t(long h,
															int retrysec, long notify);
}