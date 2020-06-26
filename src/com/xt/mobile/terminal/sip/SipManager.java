package com.xt.mobile.terminal.sip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.SdpMessage.AUDIO_CHANNEL;
import com.xt.mobile.terminal.sip.SdpMessage.AUDIO_TYPE;
import com.xt.mobile.terminal.sip.SdpMessage.Audio_Sample;
import com.xt.mobile.terminal.sip.SdpMessage.MEDIA_TYPE;
import com.xt.mobile.terminal.sip.SdpMessage.Media;
import com.xt.mobile.terminal.sip.SdpMessage.RtpMap;
import com.xt.mobile.terminal.sip.SdpMessage.VIDEO_TYPE;
import com.xt.mobile.terminal.sip.SipMessage.INVITE_TYPE;
import com.xt.mobile.terminal.sipcapture.CaptureSipManager;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.view.XTMediaSource;
import com.xtmedia.encode.MediaEncode;
import com.xtmedia.encode.MediaSendInfo;
import com.xtmedia.encode.SendMediaData;
import com.xtmedia.encode.SvrInfoEx;
import com.xtmedia.port.SendPort;
import com.xtmedia.xtsip.SipNative;
import com.xtmedia.xtsip.XTSipClientMessageCallBack;
import com.xtmedia.xtsip.XTSipClientSubscriptionRequestInfo;
import com.xtmedia.xtsip.XTSipMessageRequestInfo;
import com.xtmedia.xtview.SipLinkOpt;
import com.xtmedia.xtview.SvrInfo;

/**
 * 对SIP协议中的一些操作方法的封装
 * 
 * @author Administrator
 * 
 */
public class SipManager {
	/**
	 * 编码能力集(键为媒体类型,值为该类型对应的唯一的编码类型),当前的编码是唯一的类型 解码能力集(键为媒体类型,值为该类型所支持的所有解码能力集合)
	 */
	public static HashMap<MEDIA_TYPE, String> encoderCapabilitySet;
	public static HashMap<MEDIA_TYPE, ArrayList<String>> decoderCapabilitySet;

	public static int sipHandle = 0;
	public static SipInfo me;
	public static SipInfo server;
	public static SipCallback callback;

	public static XTMediaSource localMedia = new XTMediaSource(
			XTMediaSource.SESSION_TYPE_RTSP);

	public static Hashtable<String, XTMediaSource> media_map = new Hashtable<String, XTMediaSource>();

	public static MediaSendInfo videoMediaSendInfo;
	public static MediaSendInfo audioMediaSendInfo;

	/**
	 * 检查一个sdp是否为标准的纯音频
	 * 
	 * @param audioSdp
	 * @return
	 */
	public static boolean isLegalAudioSdp(String audioSdp) {
		SdpMessage parseSdp = SdpMessage.parseSdp(audioSdp);
		boolean legal = true;
		if (parseSdp.medias == null || parseSdp.medias.size() < 1) {
			legal = false;
		}
		if (legal) {
			for (Media m : parseSdp.medias) {
				if (MEDIA_TYPE.VIDEO.name().equals(m.name)) {
					legal = false;
					break;
				}
			}
		}
		return legal;
	}

	public static void initCapabilitySet(HashMap<MEDIA_TYPE, String> encodings,
			HashMap<MEDIA_TYPE, ArrayList<String>> decodings) {
		// 编码能力是唯一的
		if (encodings != null) {
			encoderCapabilitySet = encodings;
		} else {
			encoderCapabilitySet = new HashMap<MEDIA_TYPE, String>();
			encoderCapabilitySet.put(MEDIA_TYPE.VIDEO, "H264/90000");

			StringBuilder sb = new StringBuilder();
			sb.append("MPEG4-GENERIC");
			sb.append("/8000");
			sb.append("");
			String rtpValue = sb.toString();
			encoderCapabilitySet.put(MEDIA_TYPE.AUDIO, rtpValue);
		}
		// 如果解码能力不指定,默认为是全能力集的解码能力
		if (decodings != null) {
			decoderCapabilitySet = decodings;
		} else {
			decoderCapabilitySet = new HashMap<MEDIA_TYPE, ArrayList<String>>();
			MEDIA_TYPE videoType = MEDIA_TYPE.VIDEO;
			ArrayList<String> videoList = new ArrayList<String>();
			for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
				videoList.add(type.name);
			}
			decoderCapabilitySet.put(videoType, videoList);
			MEDIA_TYPE audioType = MEDIA_TYPE.AUDIO;
			ArrayList<String> audioList = new ArrayList<String>();
			for (AUDIO_TYPE type : AUDIO_TYPE.values()) {
				for (Audio_Sample sample : Audio_Sample.values()) {
					for (AUDIO_CHANNEL channel : AUDIO_CHANNEL.values()) {
						StringBuilder sb = new StringBuilder();
						sb.append(type.name);
						sb.append(sample.name);
						sb.append(channel.name);
						String rtpValue = sb.toString();
						audioList.add(rtpValue);
					}
				}
			}
			decoderCapabilitySet.put(audioType, audioList);
		}
	}

	/**
	 * @param me
	 *            the me to set
	 */
	public static void setMe(SipInfo me) {
		SipManager.me = me;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public static void setServer(SipInfo server) {
		SipManager.server = server;
	}

	/*
	 * public static String matchSdp(String inviteSdp) { return
	 * matchSdp(inviteSdp, false, false); }
	 */
	public static boolean capabilityMatch(String inviteSdp, String playingSdp) {
		if (inviteSdp == null) {
			return false;
		}
		if (playingSdp == null) {
			ToolLog.i("ERROR:转发sdp不存在");
			return false;
		}
		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		if (message.medias == null || message.medias.size() == 0) {
			ToolLog.i("ERROR:请求sdp不包含合法的媒体描述");
			return false;
		}
		SdpMessage playingMessage = SdpMessage.parseSdp(playingSdp);
		if (playingMessage.medias == null || playingMessage.medias.size() == 0) {
			ToolLog.i("ERROR:支持的转发能力集不合法");
			return false;
		}
		// 从playingSdp中提取目前支持的能力集
		HashMap<MEDIA_TYPE, String> encoderCapabilitySet = new HashMap<MEDIA_TYPE, String>();
		for (Media m : playingMessage.medias) {
			if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
				MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(m.name
						.toUpperCase(Locale.getDefault()));
				if (m.rtpmap.size() != 1) {
					ToolLog.i("ERROR:转发媒体源sdp媒体信息混乱");
					return false;
				}
				for (Map.Entry<String, RtpMap> entry : m.rtpmap.entrySet()) {
					String key = entry.getKey();
					encoderCapabilitySet.put(mediaType, key);
				}
			}
		}
		// 如果转发源无能力集,直接返回
		if (encoderCapabilitySet.size() == 0) {
			ToolLog.i("ERROR:转发源无编码能力");
			return false;
		}
		// 检查能力集是否匹配
		for (Media media : message.medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			// 先检查编码能力
			if ((media.type.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
				String encederType = encoderCapabilitySet.get(mediaType);
				if (encederType == null) {
					ToolLog.i("ERROR:" + mediaType + "无编码能力");
					return false;
				}
				boolean containsWhitSingle = true;
				if (!media.rtpmap.keySet().contains(encederType)) {
					// 说明对方发送过来的解码集合,我一个对应的编码能力都没有
					containsWhitSingle = false;
				}
				if (!containsWhitSingle) {
					if (encederType.endsWith("/1")) {
						encederType = encederType.substring(0,
								encederType.length() - 2);
					} else {
						encederType = encederType + "/1";
					}
					if (!media.rtpmap.keySet().contains(encederType)) {
						// 说明对方发送过来的解码集合,我一个对应的编码能力都没有
						ToolLog.i("ERROR:编码能力不支持");
						return false;
					}
				}
			}
			// 再检查解码能力
			if ((media.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
				if (decoderCapabilitySet == null) {
					ToolLog.i("ERROR:本地无解码能力");
					return false;
				} else {
					ArrayList<String> decodingList = decoderCapabilitySet
							.get(mediaType);
					if (media.rtpmap.keySet().size() != 1) {
						ToolLog.i("ERROR:混乱的解码格式");
						return false;
					}
					for (String encoder : media.rtpmap.keySet()) {
						if (!decodingList.contains(encoder)) {
							if (encoder.endsWith("/1")) {
								encoder = encoder.substring(0,
										encoder.length() - 2);
							} else {
								encoder = encoder + "/1";
							}
							if (!decodingList.contains(encoder)) {
								ToolLog.i("ERROR:解码能力不支持");
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/*
	 * 作为server端的能力及匹配
	 */
	public static boolean capabilityMatch(String inviteSdp) {
		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		if (message.medias == null || message.medias.size() == 0) {
			ToolLog.i("ERROR:请求sdp不包含合法的媒体描述");
			return false;
		}
		// 检查能力集是否匹配
		for (Media media : message.medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			// 先检查编码能力
			if ((media.type.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
				if (encoderCapabilitySet == null) {
					ToolLog.i("ERROR:本地无编码能力");
					return false;
				} else {
					String encederType = encoderCapabilitySet.get(mediaType);
					if (!media.rtpmap.keySet().contains(encederType)) {
						// 说明对方发送过来的解码集合,我一个对应的编码能力都没有
						ToolLog.i("ERROR:编码能力不支持");
						return false;
					}
				}
			}
			// 再检查解码能力
			if ((media.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
				if (decoderCapabilitySet == null) {
					ToolLog.i("ERROR:本地无解码能力");
					return false;
				} else {
					ArrayList<String> decodingList = decoderCapabilitySet
							.get(mediaType);
					if (media.rtpmap.keySet().size() != 1) {
						ToolLog.i("ERROR:混乱的解码格式");
						return false;
					}
					for (String encoder : media.rtpmap.keySet()) {
						if (!decodingList.contains(encoder)) {
							ToolLog.i("ERROR:解码能力不支持");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 匹配别人的sdp,根据实际情况生成一个回复的sdp
	 * 
	 * @param inviteSdp
	 *            收到的sdp(目前的点播可以直接回复编码器的sdp，不需要采用此方法，所以这里的sdp可以单独的理解为呼叫的sdp)
	 * @param sendPartSdp
	 *            自己编码部分的sdp(通常为编码器的sdp，如果是本地编码，此sdp可以不指定，参数直接为null)
	 * @param recviveOpts
	 *            自己的接收端口参数
	 * @param sendInfos
	 *            自己的发送端口参数(如果指定为本地编码，即sendPartSdp为null此参数必须有，否则此参数无用可传null)
	 * @return
	 */
	public static String matchSdp(String inviteSdp, String sendPartSdp,
			SipLinkOpt[] recviveOpts, SvrInfo[] sendInfos) {
		// 第一步解析我当前的编码能力集
		HashMap<MEDIA_TYPE, String> encoderCapabilitySet = new HashMap<MEDIA_TYPE, String>();
		boolean localEncoder = false;
		SdpMessage sendPartMessage = null;
		if (sendPartSdp == null) {
			if (sendInfos == null) {
				// 本地编码必须指定发送端口
				return null;
			}
			localEncoder = true;
			// 如果不指定我的编码能力直接默认为手机本地采集编码的格式
			encoderCapabilitySet.put(MEDIA_TYPE.VIDEO, VIDEO_TYPE.H264.name);
			StringBuilder sb = new StringBuilder();
			sb.append(AUDIO_TYPE.AAC.name);
			sb.append(Audio_Sample.RATE_8K.name);
			sb.append(AUDIO_CHANNEL.MONO.name);
			String rtpValue = sb.toString();
			encoderCapabilitySet.put(MEDIA_TYPE.AUDIO, rtpValue);
		} else {
			sendPartMessage = SdpMessage.parseSdp(sendPartSdp);
			// 从playingSdp中提取目前支持的编码能力集
			for (Media m : sendPartMessage.medias) {
				if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
					MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(m.name
							.toUpperCase(Locale.getDefault()));
					for (Map.Entry<String, RtpMap> entry : m.rtpmap.entrySet()) {
						String key = entry.getKey();
						encoderCapabilitySet.put(mediaType, key);
					}
				}
			}
		}
		// 第二部判断别人的解码能力我是否满足，并且将对方的sdp修改之后返回
		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		message.receiveIp = message.sendIp;
		message.sendId = me.getId();
		message.sendIp = me.getIp();
		ArrayList<Media> medias = message.medias;
		boolean needAddVideo = false;
		boolean needAddAudio = false;
		for (Media media : medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			if (mediaType == null) {
				ToolLog.i("ERROR:指定的媒体源名称不合法");
				return null;
			}
			INVITE_TYPE inviteType = media.type;

			switch (inviteType) {
			case PLAY:
				// 对方需要接收的部分(解码)，对应我的发送流部分(编码)
				media.type = INVITE_TYPE.PUSH;
				String encederType = encoderCapabilitySet.get(mediaType);
				// 检查我是否有对应的编码能力，如果没有直接跳过
				if (encederType == null) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				}
				if (localEncoder) {
					// 如果是本地编码，流从手机本地发出
					media.mediaIp = me.getIp();
					if (mediaType == MEDIA_TYPE.VIDEO) {
						media.mediaPort = sendInfos[0].rtp_send_port;
					} else {
						media.mediaPort = sendInfos[sendInfos.length - 1].rtp_send_port;
					}
					// 从对方的解码能力中选取手机本地编码支持的部分，修改端口后原样返回(避免单声道不加/1后缀的情况)
					RtpMap rtpMap = media.rtpmap.get(encederType);
					if (rtpMap == null) {
						if (encederType.endsWith("/1")) {
							rtpMap = media.rtpmap.get(encederType.subSequence(
									0, encederType.length() - 2));
						} else {
							rtpMap = media.rtpmap.get(encederType + "/1");
						}
					}
					media.rtpmap = new TreeMap<String, RtpMap>();
					media.rtpmap.put(encederType, rtpMap);
					ArrayList<String> newInfos = new ArrayList<String>();
					newInfos.add(rtpMap.rtpmapValue);
					for (String info : media.mediaInfos) {
						if (!info.startsWith("rtpmap:")) {
							newInfos.add(info);
						}
					}
					media.mediaInfos = newInfos;
				} else {
					// 否则编码能力的部分从sendpartsdp中获取
					for (Media m : sendPartMessage.medias) {
						if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {

							if (mediaType == MEDIA_TYPE.valueOf(m.name
									.toUpperCase(Locale.getDefault()))) {
								media.mediaInfos = m.mediaInfos;
								media.mediaIp = m.mediaIp;
								media.mediaPort = m.mediaPort;
								media.rtpmap = m.rtpmap;
							}
						}
					}
				}
				break;
			case PUSH:
				// 对方发送的部分(编码)，我接收的部分(解码)
				boolean inactive = true;
				ArrayList<String> arrayList = decoderCapabilitySet
						.get(mediaType);
				// 判断我是否具有对应的解码能力(解码能力在注册的时候初始化,通常为全能力集)
				if (arrayList != null) {
					if (media.rtpmap.size() == 1) {
						String firstKey = media.rtpmap.firstKey();
						if (arrayList.contains(firstKey)) {
							inactive = false;
						} else {
							if (firstKey.endsWith("/1")) {
								firstKey = firstKey.substring(0,
										firstKey.length() - 2);
								if (arrayList.contains(firstKey)) {
									inactive = false;
								}
							} else {
								if (arrayList.contains(firstKey + "/1")) {
									inactive = false;
								}
							}
						}
					}
				}
				// 解码能力不支持直接跳过
				if (inactive) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				} else {
					media.type = INVITE_TYPE.PLAY;
					media.mediaIp = me.getIp();
					// 携带sps、pps回复编码器ack时会导致编码器崩溃,所以这里将sdp中媒体属性字段除了rtpmap以外全部删除
					media.mediaInfos.clear();
					String info = media.rtpmap.firstEntry().getValue().rtpmapValue;
					media.mediaInfos.add(info);
					if (mediaType == MEDIA_TYPE.VIDEO) {
						media.mediaPort = recviveOpts[0].rtp_port;
					} else {
						media.mediaPort = recviveOpts[recviveOpts.length - 1].rtp_port;
					}
				}
				break;
			case PLAY_PUSH:
				// 对方为编解码公用端口sendrecv模式，我需要将对方的一个media部分拆分成两个返回
				if (mediaType == MEDIA_TYPE.VIDEO) {
					needAddVideo = true;
				} else {
					needAddAudio = true;
				}
				break;
			case INACTIVE:
				continue;
				// break;
			}
		}
		if (needAddVideo) {
			// 视频协商一拆为2
			Media remove = medias.remove(0);
			Media playVideo = new Media();
			playVideo.type = INVITE_TYPE.PLAY;
			playVideo.name = MEDIA_TYPE.VIDEO.name;
			playVideo.mediaPort = recviveOpts[0].rtp_port;
			playVideo.rtpmap = remove.rtpmap;
			playVideo.mediaIp = me.getIp();
			// playVideo.mediaInfos = remove.mediaInfos;
			playVideo.mediaInfos.add("a="
					+ playVideo.rtpmap.firstEntry().getValue().rtpmapValue);
			medias.add(0, playVideo);
			// 发送的视频部分
			if (localEncoder) {
				Media recvVideo = new Media();
				recvVideo.type = INVITE_TYPE.PUSH;
				recvVideo.name = MEDIA_TYPE.VIDEO.name;
				recvVideo.mediaPort = sendInfos[0].rtp_send_port;
				recvVideo.mediaIp = me.getIp();
				recvVideo.rtpmap = new TreeMap<String, RtpMap>();
				String videoEncederType = encoderCapabilitySet
						.get(MEDIA_TYPE.VIDEO);
				RtpMap rtpMap = remove.rtpmap.get(videoEncederType);
				recvVideo.rtpmap.put(videoEncederType, rtpMap);
				ArrayList<String> newInfos = new ArrayList<String>();
				newInfos.add(rtpMap.rtpmapValue);
				for (String info : remove.mediaInfos) {
					if (!info.startsWith("rtpmap:")) {
						newInfos.add(info);
					}
				}
				recvVideo.mediaInfos = newInfos;
				medias.add(0, recvVideo);
			} else {
				for (Media m : sendPartMessage.medias) {
					if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {

						if (MEDIA_TYPE.VIDEO == MEDIA_TYPE.valueOf(m.name
								.toUpperCase(Locale.getDefault()))) {
							medias.add(0, m);
						}
					}
				}
			}
		}
		if (needAddAudio) {
			// 音频协商一拆为2
			Media remove = medias.remove(medias.size() - 1);
			Media playAudio = new Media();
			playAudio.type = INVITE_TYPE.PLAY;
			playAudio.name = MEDIA_TYPE.AUDIO.name;
			playAudio.mediaPort = recviveOpts[recviveOpts.length - 1].rtp_port;
			playAudio.rtpmap = remove.rtpmap;
			playAudio.mediaIp = me.getIp();
			playAudio.mediaInfos = remove.mediaInfos;
			// 只携带rtpmap部分的媒体属性
			String mediaInfo = playAudio.rtpmap.firstEntry().getValue().rtpmapValue;
			playAudio.mediaInfos.add(mediaInfo);
			medias.add(medias.size(), playAudio);

			if (localEncoder) {
				// 发送的音频部分
				Media recvAudio = new Media();
				recvAudio.type = INVITE_TYPE.PUSH;
				recvAudio.name = MEDIA_TYPE.AUDIO.name;
				recvAudio.mediaPort = sendInfos[sendInfos.length - 1].rtp_send_port;
				recvAudio.mediaIp = me.getIp();

				recvAudio.rtpmap = new TreeMap<String, RtpMap>();
				String audioEncederType = encoderCapabilitySet
						.get(MEDIA_TYPE.AUDIO);
				RtpMap rtpMap = remove.rtpmap.get(audioEncederType);
				recvAudio.rtpmap.put(audioEncederType, rtpMap);
				ArrayList<String> newInfos = new ArrayList<String>();
				newInfos.add(rtpMap.rtpmapValue);
				for (String info : remove.mediaInfos) {
					if (!info.startsWith("rtpmap:")) {
						newInfos.add(info);
					}
				}
				recvAudio.mediaInfos = newInfos;
				medias.add(medias.size(), recvAudio);
			} else {
				for (Media m : sendPartMessage.medias) {
					if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {

						if (MEDIA_TYPE.AUDIO == MEDIA_TYPE.valueOf(m.name
								.toUpperCase(Locale.getDefault()))) {
							medias.add(0, m);
						}
					}
				}
			}
		}
		return message.createSdp();
	}

	public static String getPlaySdp(String inviteSdp) {

		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		message.receiveIp = message.sendIp;

		ArrayList<Media> medias = message.medias;
		for (Media media : medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			if (mediaType == null) {
				ToolLog.i("ERROR:指定的媒体源名称不合法");
				return null;
			}
			if (media.type == INVITE_TYPE.PUSH) {

				boolean inactive = true;
				if (media.rtpmap.size() > 0) {

					String tmpKey = null;
					RtpMap tmpValue = null;
					for (Map.Entry<String, RtpMap> entry : media.rtpmap
							.entrySet()) {
						tmpKey = entry.getKey();
						tmpValue = entry.getValue();
						if (tmpValue.payload == 8 || tmpValue.payload == 98) {
							break;
						}
					}
					if (tmpKey != null && tmpValue != null) {
						media.rtpmap.clear();
						media.rtpmap.put(tmpKey, tmpValue);
					} else {
						Entry<String, RtpMap> entry = media.rtpmap.firstEntry();
						media.rtpmap.clear();
						media.rtpmap.put(entry.getKey(), entry.getValue());
					}
					inactive = false;
				}

				// 解码能力不支持直接跳过
				if (inactive) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				} else {
					// 清除所有的rtpmap字段，并加载第一个
					for (int i = media.mediaInfos.size() - 1; i >= 0; i--) {
						String tmpRtp = media.mediaInfos.get(i).toString();
						if (tmpRtp.startsWith("rtpmap:")) {
							media.mediaInfos.remove(i);
						}
					}
					String info = media.rtpmap.firstEntry().getValue().rtpmapValue;
					media.mediaInfos.add(info);
				}
			}
		}
		return message.createSdp();
	}

	public static String matchReceiveSdp(String inviteSdp,
			SipLinkOpt[] recviveOpts) {

		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		message.receiveIp = message.sendIp;
		message.sendId = me.getId();
		message.sendIp = me.getIp();

		ArrayList<Media> medias = message.medias;
		for (Media media : medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			if (mediaType == null) {
				ToolLog.i("ERROR:指定的媒体源名称不合法");
				return null;
			}
			if (media.type == INVITE_TYPE.PUSH) {

				boolean inactive = true;
				if (media.rtpmap.size() > 0) {

					String tmpKey = null;
					RtpMap tmpValue = null;
					for (Map.Entry<String, RtpMap> entry : media.rtpmap
							.entrySet()) {
						tmpKey = entry.getKey();
						tmpValue = entry.getValue();
						if (tmpValue.payload == 8 || tmpValue.payload == 98) {
							break;
						}
					}
					if (tmpKey != null && tmpValue != null) {
						media.rtpmap.clear();
						media.rtpmap.put(tmpKey, tmpValue);
					} else {
						Entry<String, RtpMap> entry = media.rtpmap.firstEntry();
						media.rtpmap.clear();
						media.rtpmap.put(entry.getKey(), entry.getValue());
					}
					inactive = false;
				}

				// 解码能力不支持直接跳过
				if (inactive) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				} else {
					media.type = INVITE_TYPE.PLAY;
					media.mediaIp = me.getIp();
					// 携带sps、pps回复编码器ack时会导致编码器崩溃,所以这里将sdp中媒体属性字段除了rtpmap以外全部删除
					media.mediaInfos.clear();
					String info = media.rtpmap.firstEntry().getValue().rtpmapValue;
					media.mediaInfos.add(info);
					if (mediaType == MEDIA_TYPE.VIDEO) {
						media.mediaPort = recviveOpts[0].rtp_port;
					} else {
						media.mediaPort = recviveOpts[recviveOpts.length - 1].rtp_port;
					}
				}
			}
		}
		return message.createSdp();
	}

	public static String matchCaptureSdp(String inviteSdp, String localSdp,
			SvrInfo[] sendInfos) {

		// 第一步解析我当前的编码能力集
		HashMap<MEDIA_TYPE, String> encoderCapabilitySet = new HashMap<MEDIA_TYPE, String>();
		SdpMessage sendPartMessage = SdpMessage.parseSdp(localSdp);
		for (Media m : sendPartMessage.medias) {
			if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
				MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(m.name
						.toUpperCase(Locale.getDefault()));
				for (Map.Entry<String, RtpMap> entry : m.rtpmap.entrySet()) {
					String key = entry.getKey();
					encoderCapabilitySet.put(mediaType, key);
				}
			}
		}

		// 第二部判断别人的解码能力我是否满足，并且将对方的sdp修改之后返回
		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		message.receiveIp = message.sendIp;
		message.sendId = ConstantsValues.v_CAPTURE_SIP_ID;
		message.sendIp = me.getIp();

		ArrayList<Media> medias = message.medias;
		for (Media media : medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			if (mediaType == null) {
				ToolLog.i("ERROR:指定的媒体源名称不合法");
				return null;
			}
			if (media.type == INVITE_TYPE.PLAY) {

				// 对方需要接收的部分(解码)，对应我的发送流部分(编码)
				media.type = INVITE_TYPE.PUSH;
				String encederType = encoderCapabilitySet.get(mediaType);
				// 检查我是否有对应的编码能力，如果没有直接跳过
				if (encederType == null) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				}

				// 如果是本地编码，流从手机本地发出
				media.mediaIp = me.getIp();
				if (mediaType == MEDIA_TYPE.VIDEO) {
					media.mediaPort = sendInfos[0].rtp_send_port;
				} else {
					media.mediaPort = sendInfos[sendInfos.length - 1].rtp_send_port;
				}
				// 从对方的解码能力中选取手机本地编码支持的部分，修改端口后原样返回(避免单声道不加/1后缀的情况)
				RtpMap rtpMap = media.rtpmap.get(encederType);
				if (rtpMap == null) {
					if (encederType.endsWith("/1")) {
						rtpMap = media.rtpmap.get(encederType.subSequence(0,
								encederType.length() - 2));
					} else {
						rtpMap = media.rtpmap.get(encederType + "/1");
					}
				}
				media.rtpmap = new TreeMap<String, RtpMap>();
				media.rtpmap.put(encederType, rtpMap);
				ArrayList<String> newInfos = new ArrayList<String>();
				newInfos.add(rtpMap.rtpmapValue);

				// 这一段会加载对方的复用ID等字段
				// for (String info : media.mediaInfos) {
				// if (!info.startsWith("rtpmap:")) {
				// newInfos.add(info);
				// }
				// }

				if (mediaType == MEDIA_TYPE.VIDEO) {
					if (sendInfos[0].multiplex) {
						newInfos.add("rtpport-mux");
						newInfos.add("muxid:" + sendInfos[0].multid_s);
						// newInfos.add("rtpid:" + sendInfos[0].multid_s);
					}
				} else if (mediaType == MEDIA_TYPE.AUDIO) {
					if (sendInfos[1].multiplex) {
						newInfos.add("rtpport-mux");
						newInfos.add("muxid:" + sendInfos[1].multid_s);
						// newInfos.add("rtpid:" + sendInfos[1].multid_s);
					}
				}
				media.mediaInfos = newInfos;
			}
		}
		return message.createSdp();
	}

	public static String matchCaptureTransmitSdp(String inviteSdp,
			String localSdp) {

		// 第一步解析我当前的编码能力集
		HashMap<MEDIA_TYPE, String> encoderCapabilitySet = new HashMap<MEDIA_TYPE, String>();
		SdpMessage sendPartMessage = SdpMessage.parseSdp(localSdp);
		for (Media m : sendPartMessage.medias) {
			if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
				MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(m.name
						.toUpperCase(Locale.getDefault()));
				for (Map.Entry<String, RtpMap> entry : m.rtpmap.entrySet()) {
					String key = entry.getKey();
					encoderCapabilitySet.put(mediaType, key);
				}
			}
		}

		SdpMessage message = SdpMessage.parseSdp(inviteSdp);
		ArrayList<Media> medias = message.medias;
		for (Media media : medias) {
			MEDIA_TYPE mediaType = MEDIA_TYPE.valueOf(media.name
					.toUpperCase(Locale.getDefault()));
			if (mediaType == null) {
				ToolLog.i("ERROR:指定的媒体源名称不合法");
				return null;
			}
			if (media.type == INVITE_TYPE.PLAY) {

				// 对方需要接收的部分(解码)，对应我的发送流部分(编码)
				String encederType = encoderCapabilitySet.get(mediaType);
				// 检查我是否有对应的编码能力，如果没有直接跳过
				if (encederType == null) {
					media.type = INVITE_TYPE.INACTIVE;
					continue;
				}

				// 从对方的解码能力中选取手机本地编码支持的部分，修改端口后原样返回(避免单声道不加/1后缀的情况)
				RtpMap rtpMap = media.rtpmap.get(encederType);
				if (rtpMap == null) {
					if (encederType.endsWith("/1")) {
						rtpMap = media.rtpmap.get(encederType.subSequence(0,
								encederType.length() - 2));
					} else {
						rtpMap = media.rtpmap.get(encederType + "/1");
					}
				}
				media.rtpmap = new TreeMap<String, RtpMap>();
				media.rtpmap.put(encederType, rtpMap);

				// 删除非h264, g711A 媒体字段
				boolean isTrackId = false;
				ArrayList<String> allInfos = media.mediaInfos;
				for (int i = allInfos.size() - 1; i >= 0; i--) {
					if (allInfos.get(i).contains("8 PCMA/8000")
							|| allInfos.get(i).contains("98 H264/90000")) {
					} else if (allInfos.get(i).contains("rtpmap")) {
						allInfos.remove(i);
					} else if (allInfos.get(i).contains("track")) {
						isTrackId = true;
					}
				}

				// 添加trackid
				if (!isTrackId) {
					String tmpMedia = media.name.toLowerCase(Locale
							.getDefault());
					if (tmpMedia.contentEquals("audio")) {
						allInfos.add("control:track2");
					} else if (tmpMedia.contentEquals("video")) {
						allInfos.add("control:track1");
					}
				}
			}
		}
		return message.createSdp();
	}

	/**
	 * 初始化sip协议参数
	 */
	public static boolean sipInit(Context context, String password,
			SipCallback callback) {
		// 无网络的情况下直接返回初始化失败(主要是自组网会出现)
		if (me.getIp() == null) {
			return false;
		}
		if (sipHandle != 0) {
			SipNative.XtSipDestroy(SipManager.sipHandle);
		}
		// 一定要设置的参数
		SipManager.callback = callback;
		/**
		 * 会话保链时间90s,代表每隔90sinvite的接收方会向发送方发送UPDATE,发送端自动监听该信令,
		 * 如果90s之后发送端没有收到UPDATE就会触发xt_sip_client_invite_terminated_callback_t并且reason为3
		 * ,
		 * 如果接收端发送的UPDATE没有回复会触发xt_sip_server_invite_terminated_callback_t并且reason为0
		 * ,最后一个参数update回复的等待时间,如果超过这个时间没有收到200ok，则判定为会话断开，sip底层自动发送bye
		 */
		PLog.d("=======注册解码========","me.port="+me.getPort()+"me.port="+me.getPort()+
				"me.ip="+me.getIp()+"me.id="+me.getId()+"password="+password+"me.id="+me.getIp());
		sipHandle = SipNative.XtSipCreate(me.getPort(), me.getPort(),
				me.getIp(), me.getId(), password, me.getIp(), 90, callback,
				1000, true);// 最新又加入一个参数,是否是纯客户端(不接受register信令)，true的话就是,false表示支持register
//		sipHandle = SipNative.XtSipCreate(5064, 5064, me.getIp(), me.getId(),
//				password, me.getIp(), 90, callback, 1000, true);// 最新又加入一个参数,是否是纯客户端(不接受register信令)，true的话就是,false表示支持register

		return true;
	}

	/**
	 * 获取手机默认的音频编码媒体数据
	 * 
	 * @return
	 */
	public static Media createDefaultLocalAudio(int mediaPort) {
		Media audio = new Media();
		audio.mediaIp = me.getIp();
		audio.name = MEDIA_TYPE.AUDIO.name;
		audio.type = INVITE_TYPE.PUSH;
		audio.mediaPort = mediaPort;
		StringBuilder sb = new StringBuilder();
		sb.append(AUDIO_TYPE.AAC.name);
		sb.append(Audio_Sample.RATE_8K.name);
		sb.append(AUDIO_CHANNEL.MONO.name);
		String rtpValue = sb.toString();
		RtpMap rtpMap = new RtpMap(AUDIO_TYPE.AAC.payload, "rtpmap:"
				+ AUDIO_TYPE.AAC.payload + " " + rtpValue);
		audio.rtpmap.put(rtpValue, rtpMap);
		audio.mediaInfos.add(rtpMap.rtpmapValue);
		audio.mediaInfos.add("control:track2");

		return audio;
	}

	/**
	 * 获取手机默认的视频编码媒体数据
	 * 
	 * @return
	 */
	public static Media createDefaultLocalVideo(int mediaPort) {
		Media video = new Media();
		video.mediaIp = me.getIp();
		video.name = MEDIA_TYPE.VIDEO.name;
		video.type = INVITE_TYPE.PUSH;
		video.mediaPort = mediaPort;
		RtpMap rtpMap = new RtpMap(VIDEO_TYPE.H264.payload, "rtpmap:"
				+ VIDEO_TYPE.H264.payload + " " + VIDEO_TYPE.H264.name);
		video.rtpmap.put(VIDEO_TYPE.H264.name, rtpMap);
		video.mediaInfos.add(rtpMap.rtpmapValue);
		video.mediaInfos.add("control:track1");
		return video;
	}

	public static String parseIds(String ids) {
		if (ids != null && ids.length() > 4) {
			String id = null;
			try {
				id = ids.split("@")[0].substring(4);
			} catch (Exception e) {
				id = null;
			}
			return id;
		}
		return null;
	}

	/**
	 * 发送不带sdp的invite
	 */
	public static void sendInviteNoSdp(final SipInfo info) {

		// Session session = new Session();
		// session.setTime(System.currentTimeMillis());

		new Thread() {
			public void run() {
				SipNative.XtSipInviteNoSdp(SipManager.sipHandle, info.getIds(),
						3000, callback);
			}
		}.start();

		ToolLog.i("发送不带sdp的invite:" + info.getIds());
	}

	public static void sendInvite(final String sdp, final SipInfo info) {

		// Session session = new Session();
		// session.setTime(System.currentTimeMillis());
		new Thread() {
			public void run() {
				SipNative.XtSipInvite(SipManager.sipHandle, info.getIds(),
						me.getIds(), sdp, sdp.getBytes().length, 3000,
						callback, true);
			}
		}.start();

		ToolLog.i("发送invite成功,ids:" + info.getIds() + "  sdp:" + sdp);
	}

	public static void sendAckWithSdp(final Session session, final String sdp) {

		new Thread() {
			@Override
			public void run() {
				SipNative.XtSipInviteProvideAnswer(session.getSipch(), sdp,
						sdp.getBytes().length);
			}
		}.start();
		ToolLog.i("发送带sdp的Ack！:" + sdp);
	}

	/**
	 * 注册的时候没有做同步(同一个人可以向同一个服务器注册多次,并且会开启多次保链和传输),所以每次注册前先注销
	 */
	public static void register() {

		final String ids = "sip:" + me.getId() + "@" + server.getIp() + ":"
				+ server.getPort();
		ToolLog.i("sip", "不带SDP的注册，服务器的ids:" + ids);
		PLog.d("==========SIP=====", "----------SIP服务信息------->ids:"+ ids);
		new Thread() {
			@Override
			public void run() {
				SipNative.XtSipRegister(SipManager.sipHandle, ids, "", 0, 100,
						6000, callback);
			}
		}.start();
	}

	public static void register(final String sdp) {

		final String ids = "sip:" + me.getId() + "@" + server.getIp() + ":"
				+ server.getPort();
		ToolLog.i("sip", "带SDP的注册，服务器的ids:" + ids);

		new Thread() {
			@Override
			public void run() {
				SipNative.XtSipRegister(SipManager.sipHandle, ids, sdp, 0, 100,
						6000, callback);
			}
		}.start();
	}

	/**
	 * 这里其实做了三件事,取消订阅、注销和销毁sip(新增一件事取消监测编码器的状态)
	 */
	public static void unregister() {

		// 注销必须放在停止心跳和取消订阅的前面,否则注销会收不到
		if (registerHandler != 0) {
			SipNative.XtSipRegisterRemoveMyBindings(registerHandler);// 含有contacts域的注销方式
		}
		// SipManager.stopHeartbeat(SipManager.server.getIds());
		// if (subscriptionHandle != 0) {
		// SipNative.XtSipClientSubscriptionEnd(subscriptionHandle);
		// SipNative.XtSipClientSubscriptionHandleDelete(subscriptionHandle);
		// }
		if (sipHandle != 0) {
			SipNative.XtSipDestroy(SipManager.sipHandle);
		}
	}

	public static void acceptInvite(final Session session, final String sdp) {
		ToolLog.i("acceptInvite我回复的sdp:" + sdp);
		if (session != null) {
			if (session.getSessionId() != null) {
				SipNative.XtSipServerInviteProvideAnswer(session.getSipch(),
						sdp, sdp.getBytes().length);
			}
		}
	}

	public static void acceptInvite(final long ch, final String sdp) {
		ToolLog.i("acceptInvite我回复的sdp:" + sdp);
		SipNative
				.XtSipServerInviteProvideAnswer(ch, sdp, sdp.getBytes().length);
	}

	public static void reInvite(Session session, final String sdp) {
		SipNative.XtSipClientInviteReinvite(session.getSipch(), sdp,
				sdp.getBytes().length);
	}

	public static void refuse(final long sessionHandle, final int code) {
		new Thread() {
			@Override
			public void run() {
				ToolLog.i("拒绝invite");
				SipNative.XtSipServerInviteReject(sessionHandle, code);
				SipNative.XtSipServerInviteHandleDelete(sessionHandle);
			}
		}.start();
	}

	public static void refuse(final Session session, final int code) {
		new Thread() {
			@Override
			public void run() {
				long inviteHandle = session.getSipch();
				if (inviteHandle != 0) {
					SipNative.XtSipServerInviteReject(inviteHandle, code);
					SipNative.XtSipServerInviteHandleDelete(inviteHandle);
				}
			}
		}.start();
	}

	public static void receiveBye(final Session session) {
		// TODO 完善
		if (session == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				if (session != null) {
					// 实际发送的bye
					long inviteHandle = session.getSipch();
					if (inviteHandle != 0) {
						if (session.getDestId() != null) {
							SipNative.XtSipClientInviteEnd(inviteHandle);
							SipNative
									.XtSipClientInviteHandleDelete(inviteHandle);
						} else {
							SipNative.XtSipServerInviteEnd(inviteHandle);
							SipNative
									.XtSipServerInviteHandleDelete(inviteHandle);
						}
					}
				}
			}
		}.start();
	}

	public static void bye(final Session session) {
		ToolLog.i("挂断会话：" + session);
		new Thread() {
			@Override
			public void run() {
				if (session != null) {
					long cancelHandle = session.getCancleHandle();

					if (session.isFinish()) {
						ToolLog.i("发送bye");
						long inviteHandle = session.getSipch();
						if (inviteHandle != 0) {
							SipNative.XtSipClientInviteEnd(inviteHandle);
							SipNative
									.XtSipClientInviteHandleDelete(inviteHandle);
						}
					} else {
						ToolLog.i("发送cancle");
						if (cancelHandle != 0) {
							SipNative.XtSipMakeCancel(sipHandle, cancelHandle);
						}
					}
				}
			}
		}.start();
	}

	/**
	 * 订阅(目前只支持订阅全部)
	 */
	public static void subscript() {
		final XTSipClientSubscriptionRequestInfo info = new XTSipClientSubscriptionRequestInfo();
		info.content = "";
		// info.event_type = "rensence";
		info.event_type = "presence";
		info.content_length = info.content.getBytes().length;
		info.content_type = "application/pidf+xml";
		// info.content_type = "application/command+xml";
		info.subscriptionTime = 3600;
		info.target = "sip:" + server.getId() + "@" + server.getIp() + ":"
				+ ConstantsValues.v_SIP_SERVER_PORT;
		new Thread() {
			@Override
			public void run() {
				SipNative.XtSipMakeClientSubscription(SipManager.sipHandle,
						info, new long[] { 0 }, 3000, callback);
			}
		}.start();
	}

	public static long subscriptionHandle;
	public static long registerHandler;

	public static String createDefaultPlaySdp(SipInfo receive,
			SipLinkOpt[] recviveOpts, boolean video, boolean audio) {
		if (!video && !audio) {
			return null;
		}
		INVITE_TYPE videoType = INVITE_TYPE.INACTIVE;
		INVITE_TYPE audioType = INVITE_TYPE.INACTIVE;
		if (video) {
			videoType = INVITE_TYPE.PLAY;
		}
		if (audio) {
			audioType = INVITE_TYPE.PLAY;
		}
		if (recviveOpts != null) {
			int[] recePorts = new int[recviveOpts.length];
			for (int i = 0; i < recePorts.length; i++) {
				recePorts[i] = recviveOpts[i].rtp_port;
			}
			return new SdpMessage(me, null, null, receive, videoType,
					audioType, recePorts, null).createSdp();
		}
		return null;
	}

	public static String createDefaultRingSdp(SipInfo receive, int actionType,
			SipLinkOpt[] recviveOpts, SvrInfo[] sendInfos) {

		INVITE_TYPE videoType = null;
		INVITE_TYPE audioType = INVITE_TYPE.PLAY_PUSH;

		if (receive != null && recviveOpts != null && sendInfos != null) {
			Media sendVideo = null;
			Media sendAudio = createDefaultLocalAudio(sendInfos[sendInfos.length - 1].rtp_send_port);
			if (actionType == Session.CALL) {
				videoType = INVITE_TYPE.PLAY_PUSH;
				sendVideo = createDefaultLocalVideo(sendInfos[0].rtp_send_port);
			}
			int[] recePorts = new int[recviveOpts.length];
			for (int i = 0; i < recePorts.length; i++) {
				recePorts[i] = recviveOpts[i].rtp_port;
			}
			int[] sendPorts = new int[sendInfos.length];
			for (int i = 0; i < sendPorts.length; i++) {
				sendPorts[i] = sendInfos[i].rtp_send_port;
			}
			return new SdpMessage(me, sendVideo, sendAudio, receive, videoType,
					audioType, recePorts, sendPorts).createSdp();
		}
		return null;
	}

	public static XTMediaSource createMediaSource(int actionType) {
		// 创建播放源
		return createMediaSource(actionType, null);
	}

	public static XTMediaSource createMediaSource(int actionType, String sdp) {
		// 创建播放源
		XTMediaSource source = new XTMediaSource();
		source.initClientParams(actionType);
		source.setSdp(sdp);
		return source;
	}

	/**
	 * 开启与一个人的心跳监测
	 * 
	 * @param ids
	 *            目标ids
	 * @param durTime
	 *            每次的心跳时间间隔
	 */
	public static void sipHeartbeat(final String ids, final int durTime) {
		new Thread() {
			@Override
			public void run() {
				ToolLog.i("开启与服务器的心跳：" + ids);
				SipNative.XtSipHeartbeatAddTarget(SipManager.sipHandle, ids,
						durTime, durTime, callback);
			}
		}.start();
	}

	/**
	 * 停止与一个人的心跳监测
	 */
	public static void stopHeartbeat(final String target) {
		SipNative.XtSipHeartbeatRemoveTarget(sipHandle, target);
	}

	public static void message(final String ids, final String content,
			final XTSipClientMessageCallBack sipClientCallBack) {
		new Thread() {
			@Override
			public void run() {
				XTSipMessageRequestInfo info = new XTSipMessageRequestInfo();
				info.content_type = "Application/command+xml";
				info.content = content;
				info.content_length = info.content.getBytes().length;
				info.target = ids;
				SipNative.XtSipMakeClientMessage(SipManager.sipHandle, info,
						3000, sipClientCallBack);
				ToolLog.i("发送文本消息--ids:" + ids + ",内容：" + content);
			}
		}.start();
	}

	public static String getContactsId(long msg) {
		String ids = SipNative.XtSipMsgGetContacts(msg);
		SipInfo info = null;
		String id = null;
		if (ids != null) {
			id = SipManager.parseIds(ids);
			info = ConfigureParse.getUserInfoById(id);
		}
		if (info == null) {
			id = SipNative.XtSipMsgGetSubject(msg);
			if (id != null) {
				id = parseIds(id);
			}
			info = ConfigureParse.getUserInfoById(id);
		}
		if (info == null) {
			// 防止获取对端的ids失败
			String to = SipNative.XtSipMsgGetToStr(msg);
			if (to != null) {
				id = SipManager.parseIds(to);
			}
			if (!id.equals(SipManager.me.getId())) {
				info = ConfigureParse.getUserInfoById(id);
			}
		}
		if (info == null) {
			// 防止获取对端的ids失败
			String from = SipNative.XtSipMsgGetFromStr(msg);
			if (from != null) {
				id = SipManager.parseIds(from);
			}
			if (!id.equals(SipManager.me.getId())) {
				info = ConfigureParse.getUserInfoById(id);
			}
		}
		if (info == null) {
			String sessionId = SipNative.XtSipMsgGetCallID(msg);
			if (sessionId == null || sessionId.equals("")) {
				return null;
			}
//			Session session = SessionManager.findSessionBySessionId(sessionId);
//			if (session != null) {
//				id = session.getDestId();
//			}
		}
		return id;
	}

	public static boolean encodeAudio = false;

	public static void stopMediaEncode() {
		MediaEncode.stopEncodeSipEx(true, true);
	}

	/**
	 * 停止音频编码（只有音频）
	 */
	public static void stopAudioEncode() {
		encodeAudio = false;
		MediaEncode.stopEncodeSipEx(false, true);
	}

	/**
	 * 向服务编码传输本地音视频
	 *
	 */
	public static void startMediaRouter(String sdp, Context context) {

		boolean onlyVoice = false;
		int audiopos = sdp.indexOf("m=audio");
		int videopos = sdp.indexOf("m=video");
		if(audiopos == -1 && videopos == -1) {
			ToolLog.i("===Sipmanager::startMediaRouter (sdp == null)");
			return ;
		} else if(audiopos > 0 && videopos == -1) {
			onlyVoice = true;
		} else if(audiopos > 0 && videopos > 0) {
			onlyVoice = false;
		}
		
		SvrInfoEx[] info = null;
		if (onlyVoice) {

			if (audioMediaSendInfo == null || audioMediaSendInfo.rtp_port == 0
					|| audioMediaSendInfo.remote_rtp_ip == null) {
				ToolLog.i("===Sipmanager::startMediaRouter (audioMediaSendInfo == null)");
				return;
			}

			if (encodeAudio) {
				return;
			}
			encodeAudio = true;
			// 纯音频编码
			info = new SvrInfoEx[] { new SvrInfoEx() };
			if (audioMediaSendInfo.remote_rtp_ip != null) {
				info[0].trackid = 1;
				info[0].trackName = "audio";
				info[0].isAudio = true;
				info[0].ip = audioMediaSendInfo.remote_rtp_ip;
				info[0].rtp_send_port = audioMediaSendInfo.rtp_port;
				info[0].isDemux = audioMediaSendInfo.isDemux;
				info[0].demuxId = audioMediaSendInfo.demuxId;
			}
		} else {

			if (videoMediaSendInfo == null || videoMediaSendInfo.rtp_port == 0
					|| videoMediaSendInfo.remote_rtp_ip == null
					|| audioMediaSendInfo == null
					|| audioMediaSendInfo.rtp_port == 0
					|| audioMediaSendInfo.remote_rtp_ip == null) {
				ToolLog.i("===Sipmanager::startMediaRouter (videoMediaSendInfo == null || audioMediaSendInfo == null)");
				return;
			}

			info = new SvrInfoEx[] { new SvrInfoEx(), new SvrInfoEx() };

			info[0].rtp_send_port = videoMediaSendInfo.rtp_port;
			info[0].ip = videoMediaSendInfo.remote_rtp_ip;
			info[0].trackid = 1;
			info[0].trackName = "video";
			info[0].isAudio = false;
			info[0].isDemux = videoMediaSendInfo.isDemux;
			info[0].demuxId = videoMediaSendInfo.demuxId;

			info[1].rtp_send_port = audioMediaSendInfo.rtp_port;
			info[1].ip = audioMediaSendInfo.remote_rtp_ip;
			info[1].trackid = 2;
			info[1].trackName = "audio";
			info[1].isAudio = true;
			info[1].isDemux = audioMediaSendInfo.isDemux;
			info[1].demuxId = audioMediaSendInfo.demuxId;
			
//			Intent intent = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
//			context.sendBroadcast(intent);
			Intent intent = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
			context.sendBroadcast(intent);
		}

		SendMediaData.CreateMeidaRouterEx(info, sdp);
	}

	public static String getLocalInviteSDP() {
		StringBuilder builder = new StringBuilder();
		builder.append("v=0\n");
		builder.append(String.format("o=%s 1 1 IN IP4 %s\n", "userName",
				me.getIp()));
		builder.append("s=Play\n");
		builder.append("i=2?videoCodecType=H.264\n");
		builder.append(String.format("c=IN IP4 %s\n", me.getIp()));
		builder.append("b=AS:1024\n");
		builder.append("t=0 0\n");
        if ( SendPort.send_opt !=null){
			builder.append(String.format("m=video 0 RTP/AVP 98\n",
					SendPort.send_opt[0].rtp_send_port));
		}else {
			ToolLog.i("----getLocalInviteSDP-----SendPort.send_opt------->null");
		}
		builder.append(String.format("c=IN IP4 %s\n", me.getIp()));
		builder.append("b=AS:12000\n");
		builder.append("a=rtpmap:98 H264/90000\n");
		builder.append("a=fmtp:98 packetization-mode=1;profile-level-id=64001F;sprop-parameter-sets=\n");
		builder.append("a=control:track1\n");
		builder.append("a=sendonly\n");
		if (SendPort.send_opt !=null){
			if (SendPort.send_opt[0].multiplex) {
				builder.append("a=rtpport-mux\n");
				builder.append(String.format("a=muxid:%d\n",
						SendPort.send_opt[0].multid_s));
			}
		}else {
			ToolLog.i("----getLocalInviteSDP-----SendPort.send_opt------->null");
		}

		if (SendPort.send_opt !=null){
			builder.append(String.format("m=audio %d RTP/AVP 8\n",
					SendPort.send_opt[1].rtp_send_port));
		}else {
			ToolLog.i("----getLocalInviteSDP-----SendPort.send_opt------->null");
		}

		builder.append(String.format("c=IN IP4 %s\n", me.getIp()));
		builder.append("a=rtpmap:8 PCMA/8000\n");
		builder.append("a=control:track2\n");
		builder.append("a=sendonly\n");
		if (SendPort.send_opt !=null){
			if (SendPort.send_opt[1].multiplex) {
				builder.append("a=rtpport-mux\n");
				builder.append(String.format("a=muxid:%d\n",
						SendPort.send_opt[1].multid_s));
			}
		}else {
			ToolLog.i("----getLocalInviteSDP-----SendPort.send_opt------->null");
		}


		String sdpUTF8 = "";
		try {
			sdpUTF8 = new String(builder.toString().getBytes(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdpUTF8;
	}

	public static int getInfofromSDP(String sdp) {

		if (sdp == null || sdp.length() <= 0 || !sdp.startsWith("v=0")) {
			ToolLog.i("========getInfofromSDP (sdp == null)");
			return -1;
		}

		MediaSendInfo[] infoOpt = new MediaSendInfo[] { new MediaSendInfo(),
				new MediaSendInfo() };

		SdpMessage message = SdpMessage.parseSdp(sdp);
		if (message != null) {
			ArrayList<Media> medias = message.medias;
			for (Media media : medias) {
				if (media.name.equals("video")) {
					infoOpt[0].isAudio = false;
					infoOpt[0].remote_rtp_ip = media.mediaIp;
					infoOpt[0].rtcp_port = media.mediaPort + 1;
					infoOpt[0].rtp_port = media.mediaPort;
					infoOpt[0].isDemux = media.isDemux;
					infoOpt[0].demuxId = media.denuxId;

					String info = media.rtpmap.firstEntry().getValue().rtpmapValue;
					if (info != null) {
						String[] split = info.split(" ");
						if (split.length >= 2) {
							int end = split[1].indexOf("/");
							if (end >= 0 && end < split[1].length()) {
								infoOpt[0].szcodec = split[1].substring(0, end);
							}
						}
					}
				} else if (media.name.equals("audio")) {
					infoOpt[1].isAudio = true;
					infoOpt[1].remote_rtp_ip = media.mediaIp;
					infoOpt[1].rtcp_port = media.mediaPort + 1;
					infoOpt[1].rtp_port = media.mediaPort;
					infoOpt[1].isDemux = media.isDemux;
					infoOpt[1].demuxId = media.denuxId;

					String info = media.rtpmap.firstEntry().getValue().rtpmapValue;
					if (info != null) {
						String[] split = info.split(" ");
						if (split.length >= 2) {
							int end = split[1].indexOf("/");
							if (end >= 0 && end < split[1].length()) {
								infoOpt[1].szcodec = split[1].substring(0, end);
							}
						}
					}
				}
			}
		}

		SipManager.videoMediaSendInfo = infoOpt[0];
		SipManager.audioMediaSendInfo = infoOpt[1];

		String videoInfo = "rtp_port=" + infoOpt[0].rtp_port + ", rtcp_port="
				+ infoOpt[0].rtcp_port + ", szcodec=" + infoOpt[0].szcodec
				+ ", remote_rtp_ip=" + infoOpt[0].remote_rtp_ip + ", isAudio="
				+ infoOpt[0].isAudio + ", isDemux=" + infoOpt[0].isDemux
				+ ", demuxId=" + infoOpt[0].demuxId;

		String audioInfo = "rtp_port=" + infoOpt[1].rtp_port + ", rtcp_port="
				+ infoOpt[1].rtcp_port + ", szcodec=" + infoOpt[1].szcodec
				+ ", remote_rtp_ip=" + infoOpt[1].remote_rtp_ip + ", isAudio="
				+ infoOpt[1].isAudio + ", isDemux=" + infoOpt[1].isDemux
				+ ", demuxId=" + infoOpt[1].demuxId;

		ToolLog.i("========getInfofromSDP (VideoInfoOpt[" + videoInfo
				+ "] AudioInfoOpt[" + audioInfo + "])");

		return 0;
	}
}