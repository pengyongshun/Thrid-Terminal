package com.xtmedia.encode;

import android.util.Log;

public class RCAuidoEncodeNative
{
	private final static int AUDIO_FORMAT_PCM_SUB_16_BIT = 0x1; /*
																 * DO NOT CHANGE
																 * - PCM signed
																 * 16 bits
																 */
	private final static int AUDIO_FORMAT_PCM = 0x00000000; /* DO NOT CHANGE */

	private static int defaultSampleRate = 8000;
	private static int defaultChannelMask = 1;
	private static int defaultFormat = 16;
	private static boolean m_bIsInit = false;

	static
	{
		try
		{
			System.loadLibrary("Faac_enc");
			System.loadLibrary("webrtc_audio_processing");
			System.loadLibrary("NativeAudioRecorder");
//			System.loadLibrary("ffmpeg");
			System.loadLibrary("xt_config");
			System.loadLibrary("rv_adapter");
			System.loadLibrary("xt_media_server");
			System.loadLibrary("xt_media_client");
			System.loadLibrary("common_lib");
			System.loadLibrary("xt_media_player");
			System.loadLibrary("xt_router");
			System.loadLibrary("SimplePlayer-jni");
		}
		catch (UnsatisfiedLinkError e)
		{
			e.printStackTrace();
		}

	}

	private native static int Init(int nSampleRate, int format, int channelMask);

	private native static long Start();

	private native static int Stop();

	private native static int UnInit();

	public native static int SetSendParam(long handler);

	public native static int SetSendParam2(long handler);

	public native static int SetSendTrackid(int trackid);

	public native static int SetSendParam3(int[] handler);

	public native static int EnableEchoProcess(boolean bEnable);

	public native static int PushRecordData(byte[] data, int len);

	public static long StartRecord()
	{
		if (false == m_bIsInit)
		{
			int nRet = Init(defaultSampleRate, defaultFormat,
					defaultChannelMask);
			if (0 == nRet)
			{
				m_bIsInit = true;
			}
		}
		SendMediaData.setAudioChannelState(SendMediaData.AUDIO_CHANNEL_CREATE);
		return Start();
	}

	public static void StopRecord()
	{
		Log.i("AudioEncodeNative", "call stop audio record");
		SendMediaData.setAudioChannelState(SendMediaData.AUDIO_CHANNEL_DESTORY);
		Stop();

		UnInit();
		m_bIsInit = false;
	}
}
