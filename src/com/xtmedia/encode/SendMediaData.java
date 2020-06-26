package com.xtmedia.encode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.SystemClock;

import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.util.ToolLog;
import com.xtmedia.port.SendPort;
import com.xtmedia.xtview.GlRenderNative;

public class SendMediaData {

	public static long handle = -1; // 转发源
	private static int iChunkCount = 0;
	private static int iChunkCount1 = 0;
	public static boolean isOpenVideo = true;
	public static boolean isOpenAudio = true;
	private static int mVideoCnt = 0;
	private static int mAudioCnt = 0;
	private static final int mMedioSendCnt = 100;
	// private static int ts;
	private static final int MAX = 24 + 3 * 1024 * 1024;
	private static byte[] sendData = new byte[MAX];
	private static byte[] sendBuffer = new byte[MAX];

	/* 创建音频通道 */
	public static final boolean AUDIO_CHANNEL_CREATE = true;
	public static final boolean AUDIO_CHANNEL_DESTORY = false;
	public static boolean audioChannelState = AUDIO_CHANNEL_DESTORY;

	/* 创建视频通道 */
	public static final boolean VIDEO_CHANNEL_CREATE = true;
	public static final boolean VIDEO_CHANNEL_DESTORY = false;
	public static boolean videoChannelState = VIDEO_CHANNEL_DESTORY;
	public static long videoStartTime = 0;

	public static boolean CreateMeidaRouterEx(SvrInfoEx[] sInfo, String sdp) {

		handle = SendPort.getSrc_chan()[0];// 唯一的发送端口
		// TODO 核对这个方法到底有什么作用，看是否可以删除(后期是否可以将编码和转发分离)
		int ret0 = GlRenderNative
				.mediaServerSetKey((int) handle, sdp.getBytes(), sdp.length(), 172);

		ToolLog.i("===mediaServerSetKey srn[" + handle + "] ret0[" + ret0 + "] sdpLen["
				+ sdp.length() + "] sdp[" + sdp + "]\n\n");

		for (int i = 0; i < sInfo.length; i++) {

			int ret = GlRenderNative.addSend(SendPort.getSrc_chan()[0], sInfo[i].trackName,
					sInfo[i].ip, sInfo[i].rtp_send_port, sInfo[i].isDemux, sInfo[i].demuxId);

			ToolLog.i("===addSend srn[" + SendPort.getSrc_chan()[0] + "] trackName["
					+ sInfo[i].trackName + "] ip[" + sInfo[i].ip + "] port["
					+ sInfo[i].rtp_send_port + "] isDemux[" + sInfo[i].isDemux + "] demuxId["
					+ sInfo[i].demuxId + "] ret[" + ret + "]");
		}
		
		int ret1 = GlRenderNative.setPayload((int) handle, "audio", 8, true);
		int ret2 = GlRenderNative.setPayload((int) handle, "video", 98, true);

		ToolLog.i("===setPayload ret1[" + ret1 + "] ret2[" + ret2 + "]");
		
		// RCAuidoEncodeNative.SetSendParam2(handle);
		// RCAuidoEncodeNative.SetSendTrackid(2);
		return true;
	}

	public static void destroyMediaRouter() {
		if (SendPort.getSrc_chan()[0] != -2) {
			if (SipManager.videoMediaSendInfo != null) {

				int ret = GlRenderNative.delSend(SendPort.getSrc_chan()[0], "video",
						SipManager.videoMediaSendInfo.remote_rtp_ip,
						SipManager.videoMediaSendInfo.rtp_port,
						SipManager.videoMediaSendInfo.isDemux,
						SipManager.videoMediaSendInfo.demuxId);

				ToolLog.i("===delSend[video] srn[" + SendPort.getSrc_chan()[0]
						+ "] trackName[video] ip[" + SipManager.videoMediaSendInfo.remote_rtp_ip
						+ "] port[" + SipManager.videoMediaSendInfo.rtp_port + "] isDemux["
						+ SipManager.videoMediaSendInfo.isDemux + "] demuxId["
						+ SipManager.videoMediaSendInfo.demuxId + "] ret[" + ret + "]");
			}
			if (SipManager.audioMediaSendInfo != null) {

				int ret = GlRenderNative.delSend(SendPort.getSrc_chan()[0], "audio",
						SipManager.audioMediaSendInfo.remote_rtp_ip,
						SipManager.audioMediaSendInfo.rtp_port,
						SipManager.audioMediaSendInfo.isDemux,
						SipManager.audioMediaSendInfo.demuxId);

				ToolLog.i("===delSend[audio] srn[" + SendPort.getSrc_chan()[0]
						+ "] trackName[audio] ip[" + SipManager.audioMediaSendInfo.remote_rtp_ip
						+ "] port[" + SipManager.audioMediaSendInfo.rtp_port + "] isDemux["
						+ SipManager.audioMediaSendInfo.isDemux + "] demuxId["
						+ SipManager.audioMediaSendInfo.demuxId + "] ret[" + ret + "]");
			}
		}
		handle = -1;
	}

	public static boolean is_Iframe(byte nal_type) {
		return ((5 == nal_type) || (6 == nal_type) || (7 == nal_type) || (8 == nal_type));
	}

	public static void sendVideoData(byte[] sps, byte[] pps,
									 byte[] h264buff, int length, int ts) {
		if (isOpenVideo){
			if (-1 != handle) {
				if (videoStartTime == 0) {
					videoStartTime = SystemClock.elapsedRealtime();
				}

				int headsize = 0;
				if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 1)) {
					headsize = 3;
				} else if ((h264buff[0] == 0) && (h264buff[1] == 0) && (h264buff[2] == 0)
						&& (h264buff[3] == 1)) {
					headsize = 4;
				}

				int frame = 0, bufferLength = 0, XTHeaderLen = 24, sLen = 0, pLen = 0;
				try {
					if (is_Iframe((byte) (h264buff[headsize] & 0x1F))) {
						if (sps != null && pps != null) {
							sLen = sps.length;
							pLen = pps.length;
							bufferLength = XTHeaderLen + sLen + pLen + length;
						} else {
							bufferLength = XTHeaderLen + length;
						}
						frame = 0;
					} else {
						bufferLength = XTHeaderLen + length;
						frame = 0x00010000;
					}

					if (sLen > 0) {
						System.arraycopy(sps, 0, sendBuffer, XTHeaderLen, sLen);
					}
					if (pLen > 0) {
						System.arraycopy(pps, 0, sendBuffer, XTHeaderLen + sLen, pLen);
					}
					System.arraycopy(h264buff, 0, sendBuffer, XTHeaderLen + sLen + pLen, length);

					byte[] head = buildHead(bufferLength - 24, frame, iChunkCount, ts);
					iChunkCount++;
					System.arraycopy(head, 0, sendBuffer, 0, 24);
					// int ret = GlRenderNative.mediaServerSendStdData((int) handle,
					// 1, sendBuffer, (long) bufferLength, frame, 172);
					int ret = GlRenderNative.rtSendDataStd((int) handle, 1, sendBuffer, bufferLength,
							frame, ts);

					mVideoCnt ++;
					if (mVideoCnt % mMedioSendCnt == 0) {
						mVideoCnt = 0;
					ToolLog.i("[video]send data srcno=" + handle + " trackid=1 dataLen=" + bufferLength
							+ " frametype=" + frame + " timestamp=" + ts + " ret=" + ret);
					}
				} catch (Exception e) {
				}
			}
		}

	}

	public static void sendAudioData(byte[] h264buff, int length) {
        if (isOpenAudio){
			if (-1 != handle) {
				int ts = (int) SystemClock.elapsedRealtime() * 8;
				System.arraycopy(h264buff, 0, sendBuffer, 0, length);
				byte[] headCopy = buildHead(length, 1, iChunkCount1, ts);
				iChunkCount1++;
				System.arraycopy(headCopy, 0, sendData, 0, 24);
				System.arraycopy(sendBuffer, 0, sendData, 24, length);
				int ret = GlRenderNative.rtSendDataStd((int) handle, 2, sendData, length + 24,
						0x00000001, ts);
				// int ret = GlRenderNative.mediaServerSendStdData((int) handle, 2,
				// sendData, length + 24, 0x00000001, 172);
				mAudioCnt ++;
				if (mAudioCnt % mMedioSendCnt == 0) {
					mAudioCnt = 0;
				ToolLog.i("[audio]send data srcno=" + handle + " trackid=2 dataLen=" + length
						+ " frametype=" + 0x00000001 + " ret=" + ret);
			}
		}
		}
	}

	/**
	 * 构造一个私有头
	 * 
	 * 
	 * @param length
	 *            发送的一帧数据的长度
	 * @return 返回一个私有头byte数组
	 */
	private static byte[] head = new byte[24];
	private static int[] tmp = { 0, 24, 0, 0, 0, 0 };

	private static byte[] buildHead(int length, int frameType, int chunkCount, int ts) {
		for (int i = 0; i < head.length; i++) {
			head[i] = 0;
		}
		tmp[0] = 0;
		tmp[1] = 24;
		tmp[2] = frameType;
		tmp[3] = chunkCount;
		tmp[4] = length;
		tmp[5] = ts;
		ByteBuffer.wrap(head).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(tmp);
		head[0] = 'C';
		head[1] = 'H';
		head[2] = 'U';
		head[3] = 'H';
		return head;
	}

	/**
	 * 
	 * @param videoChannelState
	 *            VIDEO_CHANNEL_CREATE or VIDEO_CHANNEL_DESTORY
	 */
	public static void setVideoChannelState(boolean videoChannelState) {
		SendMediaData.videoChannelState = videoChannelState;
	}

	/**
	 * 
	 * @param audioChannelState
	 *            AUDIO_CHANNEL_CREATE or AUDIO_CHANNEL_DESTORY
	 */
	public static void setAudioChannelState(boolean audioChannelState) {
		SendMediaData.audioChannelState = audioChannelState;
	}

}
