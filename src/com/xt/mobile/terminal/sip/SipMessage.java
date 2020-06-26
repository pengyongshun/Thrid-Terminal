package com.xt.mobile.terminal.sip;

/**
 * sip信令消息
 * 
 * @author Administrator
 * 
 */
public class SipMessage
{
	/**
	 * sip协议中消息默认监听的端口
	 */
	public static final int PORT = 5060;

	/**
	 * invite的sdp类型(点播、推送),上级对下级是点播PLAY,下级对上级是推送PUSH,
	 * PROXY_PLAY和PROXY_PUSH是代理点播和代理推送,只有中间节点才有此行为(组长)
	 */
	public enum INVITE_TYPE
	{
		INACTIVE(0), PUSH(1), PLAY(2), PLAY_PUSH(3);
		private int value;

		INVITE_TYPE(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public void setValue(int value)
		{
			this.value = value;
		}
	}

}