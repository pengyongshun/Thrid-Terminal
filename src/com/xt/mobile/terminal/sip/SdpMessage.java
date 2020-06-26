package com.xt.mobile.terminal.sip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.text.TextUtils;
import android.util.Base64;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.sip.SipMessage.INVITE_TYPE;
import com.xt.mobile.terminal.util.ToolLog;

public class SdpMessage {
	public SdpMessage() {
		super();
	}

	public SdpMessage(SipInfo send, SipInfo receive, INVITE_TYPE videoType,
			INVITE_TYPE audioType, int[] recvPorts, int[] sendPorts) {
		this(send, null, null, receive, videoType, audioType, recvPorts,
				sendPorts, false, false);
	}

	public SdpMessage(SipInfo send, Media sendVideoMedia, Media sendAudioMedia,
			SipInfo receive, INVITE_TYPE videoType, INVITE_TYPE audioType,
			int[] recvPorts, int[] sendPorts) {
		this(send, sendVideoMedia, sendAudioMedia, receive, videoType,
				audioType, recvPorts, sendPorts, false, false);
	}

	public SdpMessage(SipInfo send, Media sendVideoMedia, Media sendAudioMedia,
			SipInfo receive, INVITE_TYPE videoType, INVITE_TYPE audioType,
			int[] recvPorts, int[] sendPorts, boolean useSingleVideoPort,
			boolean useSingleAudioPort) {
		sendId = send.getId();
		sendIp = send.getIp();
		// sendIp = SipManager.server.getIp();
		receiveIp = receive.getIp();
		if (receiveIp == null && SipManager.server != null) {
			receiveIp = SipManager.server.getIp();
		}
		medias = createDefaultMedias(sendIp, sendVideoMedia, sendAudioMedia,
				videoType, audioType, recvPorts, sendPorts, useSingleVideoPort,
				useSingleAudioPort);
	}

	/**
	 * v字段，表示协议的版本
	 */
	private String sdpVersion = "0";

	/**
	 * o字段，视频源的一些信息，格式<username><sess-id><sess-version><nettype><addrtype><
	 * unicast-address> 我们将username、unicast-address作为单独的参数分别是发起者的id和发起者的地址
	 * nettype、addrtype作为固定值因特网的ip4协议：IN、IP4
	 * 由于此时会话还没有建立所以sess-id和sess-version写缺省值1、1
	 */
	public String sendId;// username
	public String sendIp;// unicast-address
	private String nettype = "IN";
	private String addrtype = "IP4";
	private String sessionId = "1";
	private String sessionVersion = "1";

	// /**
	// * i字段，会话信息,用视频源的id表示
	// */
	// public String sourceId;

	/**
	 * c字段，连接信息，格式<nettype><addrtype><connetion-address>
	 * 此处的nettype和addrtype和o字段的一致不需再次定义，只需要把接收地址作为参数
	 */
	public String receiveIp;

	/**
	 * b字段，带宽信息字段，格式<bandtype><bandwith>,默认值AS:1024000(其中AS表示整个会话的带宽，其值为1024K)
	 */
	private String bandInfo = "AS:1024000";

	/**
	 * t字段，会话活动时间，格式<starttime><endtime>目前用0 0表示会话永久
	 */
	private String sessionActivTime = "0 0";

	public ArrayList<String> sessionInfos = new ArrayList<String>();
	/**
	 * 媒体类型参数，包含音视频两种
	 */
	public ArrayList<Media> medias;

	public static ArrayList<Media> createDefaultMedia(MEDIA_TYPE mediaType,
			Media sendMedia, String sendIp, INVITE_TYPE mediaInviteType,
			int... ports) {
		ArrayList<Media> medias = new ArrayList<Media>();
		if (mediaType == null) {
			ToolLog.i("ERROR:没有指定媒体类型");
			return medias;
		}
		if (mediaInviteType == null) {
			ToolLog.i("ERROR:没有指定媒体流的方向");
			return medias;
		}
		if (ports.length > 1) {
			// 说明开辟了两个端口,一定是PLAY_PUSH的方向
			if (mediaInviteType != INVITE_TYPE.PLAY_PUSH) {
				ToolLog.i("ERROR:指定的端口数量和媒体方向数量不匹配");
				return medias;
			}
			// TODO 此处应该按照我的解码能力集来生成对应的接收媒体的sdp部分,为了简便这里我们使用全能力集
			Media recvMedia = new Media();
			recvMedia.name = mediaType.name;
			recvMedia.mediaIp = sendIp;
			recvMedia.type = INVITE_TYPE.PLAY;
			switch (mediaType) {
			case VIDEO:
				for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
					RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
							+ type.payload + SPACE + type.name);
					recvMedia.rtpmap.put(type.name, rtpMap);
					recvMedia.mediaInfos.add(rtpMap.rtpmapValue);
				}
				recvMedia.mediaInfos.add("control:track1");
				break;
			case AUDIO:
				for (AUDIO_TYPE type : AUDIO_TYPE.values()) {
					for (Audio_Sample sample : Audio_Sample.values()) {
						for (AUDIO_CHANNEL channel : AUDIO_CHANNEL.values()) {
							StringBuilder sb = new StringBuilder();
							sb.append(type.name);
							sb.append(sample.name);
							sb.append(channel.name);
							String rtpValue = sb.toString();
							RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
									+ type.payload + SPACE + rtpValue);
							recvMedia.rtpmap.put(rtpValue, rtpMap);
							recvMedia.mediaInfos.add(rtpMap.rtpmapValue);
						}
					}
				}
				recvMedia.mediaInfos.add("control:track2");
				break;
			}
			recvMedia.mediaPort = ports[0];
			recvMedia.mediaInfos.add("recvonly");
			medias.add(recvMedia);

			// sendMedia.mediaIp = sendIp;
			sendMedia.type = INVITE_TYPE.PUSH;
			sendMedia.mediaPort = ports[1];
			sendMedia.mediaInfos.add("sendonly");
			medias.add(sendMedia);
		} else {
			Media media = null;
			if ((mediaInviteType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
				// 单向点播或者一个端口的收发一体
				// TODO 此处应该按照我的解码能力集来生成对应的接收媒体的sdp部分,为了简便这里我们使用全能力集
				media = new Media();
				media.name = mediaType.name;
				media.mediaIp = sendIp;
				media.type = INVITE_TYPE.PLAY;
				switch (mediaType) {
				case VIDEO:
					for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
						RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
								+ type.payload + SPACE + type.name);
						media.rtpmap.put(type.name, rtpMap);
						media.mediaInfos.add(rtpMap.rtpmapValue);
					}
					break;
				case AUDIO:
					for (AUDIO_TYPE type : AUDIO_TYPE.values()) {
						for (Audio_Sample sample : Audio_Sample.values()) {
							for (AUDIO_CHANNEL channel : AUDIO_CHANNEL.values()) {
								StringBuilder sb = new StringBuilder();
								sb.append(type.name);
								sb.append(sample.name);
								sb.append(channel.name);
								String rtpValue = sb.toString();
								RtpMap rtpMap = new RtpMap(type.payload,
										"rtpmap:" + type.payload + SPACE
												+ rtpValue);
								media.rtpmap.put(rtpValue, rtpMap);
								media.mediaInfos.add(rtpMap.rtpmapValue);
							}
						}
					}
					break;
				}
				media.mediaPort = ports[0];
				if ((mediaInviteType.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
					// 单独端口的收发(这种我们应用中不会出现,只有注册生成sdp才可能是这种情况)
					media.mediaInfos.add("sendrecv");
				} else {
					media.mediaInfos.add("recvonly");
				}
			} else if ((mediaInviteType.getValue() & INVITE_TYPE.PUSH
					.getValue()) > 0) {
				// 单向推送
				media = sendMedia;
				media.mediaIp = sendIp;
				media.type = INVITE_TYPE.PUSH;
				media.mediaPort = ports[1];
				media.mediaInfos.add("sendonly");
			}
			if (media != null) {
				if (mediaInviteType == INVITE_TYPE.PLAY) {
					// 单独的点播才需要加上track字段,否则已经存在了
					switch (mediaType) {
					case VIDEO:
						media.mediaInfos.add("control:track1");
						break;
					case AUDIO:
						media.mediaInfos.add("control:track2");
						break;
					}
				}
				medias.add(media);
			}
		}
		return medias;
	}

	public static ArrayList<Media> createDefaultVideo(String sendIp,
			INVITE_TYPE videoType, int... ports) {
		ArrayList<Media> videos = new ArrayList<Media>();
		if (videoType == null) {
			ToolLog.i("ERROR:没有指定视频流的方向");
			return videos;
		}
		if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
			// 说明sdp中包含接收视频
			Media video = new Media();
			video.name = "video";
			video.mediaIp = sendIp;
			video.mediaPort = ports[0];
			for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
				RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
						+ type.payload + SPACE + type.name);
				video.rtpmap.put(type.name, rtpMap);
				video.mediaInfos.add(rtpMap.rtpmapValue);
			}
			if (ports.length > 1) {
				video.mediaInfos.add("recvonly");
			}
			videos.add(video);
		} else if ((videoType.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
			// 说明sdp中包含推送视频
			Media video = new Media();
			video.name = "video";
			video.mediaIp = sendIp;
			for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
				RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
						+ type.payload + SPACE + type.name);
				video.rtpmap.put(type.name, rtpMap);
				video.mediaInfos.add(rtpMap.rtpmapValue);
			}
			if ((videoType.getValue() & INVITE_TYPE.PLAY_PUSH.getValue()) > 0) {
				if (ports.length > 1) {
					video.mediaPort = ports[1];
					video.mediaInfos.add("sendonly");
				} else {
					video.mediaPort = ports[0];
					video.mediaInfos.add("sendrecv");
				}
			} else {
				video.mediaPort = ports[0];
				video.mediaInfos.add("sendonly");
			}
			videos.add(video);
		}
		if (videos.size() > 0) {
			videos.get(videos.size() - 1).mediaInfos.add("control:track1");
		}
		return videos;
	}

	public static ArrayList<Media> createDefaultAudio(String sendIp,
			INVITE_TYPE audioType, int... ports) {
		ArrayList<Media> audios = new ArrayList<Media>();
		if (audioType == null) {
			ToolLog.i("ERROR:没有指定音频流的方向");
			return audios;
		}
		if ((audioType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
			// 说明sdp中包含接收视频
			Media video = new Media();
			video.name = "audio";
			video.mediaIp = sendIp;
			video.mediaPort = ports[0];
			for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
				RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
						+ type.payload + SPACE + type.name);
				video.rtpmap.put(type.name, rtpMap);
				video.mediaInfos.add(rtpMap.rtpmapValue);
			}
			if (ports.length > 1) {
				video.mediaInfos.add("recvonly");
			}
			audios.add(video);
		} else if ((audioType.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
			// 说明sdp中包含推送视频
			Media video = new Media();
			video.name = "audio";
			video.mediaIp = sendIp;
			for (VIDEO_TYPE type : VIDEO_TYPE.values()) {
				RtpMap rtpMap = new RtpMap(type.payload, "rtpmap:"
						+ type.payload + SPACE + type.name);
				video.rtpmap.put(type.name, rtpMap);
				video.mediaInfos.add(rtpMap.rtpmapValue);
			}
			if ((audioType.getValue() & INVITE_TYPE.PLAY_PUSH.getValue()) > 0) {
				if (ports.length > 1) {
					video.mediaPort = ports[1];
					video.mediaInfos.add("sendonly");
				} else {
					video.mediaPort = ports[0];
					video.mediaInfos.add("sendrecv");
				}
			} else {
				video.mediaPort = ports[0];
				video.mediaInfos.add("sendonly");
			}
			audios.add(video);
		}
		if (audios.size() > 0) {
			audios.get(audios.size() - 1).mediaInfos.add("control:track2");
		}
		return audios;
	}

	public static ArrayList<Media> createDefaultMedias(String sendIp,
			Media sendVideoMedia, Media sendAudioMedia, INVITE_TYPE videoType,
			INVITE_TYPE audioType, int[] recvPorts, int[] sendPorts,
			boolean singleVideoPort, boolean singleAudioPort) {
		ArrayList<Media> medias = new ArrayList<Media>();
		int videoPortNum = 0;
		if (videoType != null && videoType != INVITE_TYPE.INACTIVE) {
			// 有视频
			if (videoType == INVITE_TYPE.PLAY_PUSH) {
				// 视频同时有收发的功能
				videoPortNum = 2;
			} else {
				videoPortNum = 1;
			}
		}
		int audioPortNum = 0;
		if (audioType != null && audioType != INVITE_TYPE.INACTIVE) {
			// 有音频
			if (audioType == INVITE_TYPE.PLAY_PUSH) {
				// 音频同时有收发的功能
				audioPortNum = 2;
			} else {
				audioPortNum = 1;
			}
		}
		int[] videoPorts = null;
		int[] audioPorts = null;

		if (sendPorts == null) {
			int num = 0;
			if (sendVideoMedia != null) {
				num++;
			}
			if (sendAudioMedia != null) {
				num++;
			}
			if (num > 0) {
				sendPorts = new int[num];
				if (sendVideoMedia != null) {
					sendPorts[0] = sendVideoMedia.mediaPort;
				}
				if (sendAudioMedia != null) {
					sendPorts[num - 1] = sendAudioMedia.mediaPort;
				}
			}
		}
		if (videoPortNum == 2) {
			if (singleVideoPort) {
				videoPorts = new int[] { recvPorts[0] };
			} else {
				videoPorts = new int[] { recvPorts[0], sendPorts[0] };
			}
		} else if (videoPortNum == 1) {
			if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
				videoPorts = new int[] { recvPorts[0] };
			} else {
				videoPorts = new int[] { sendPorts[0] };
			}
		}
		if (audioPortNum == 2) {
			if (singleAudioPort) {
				if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
					audioPorts = new int[] { recvPorts[1] };
				} else {
					audioPorts = new int[] { recvPorts[0] };
				}
			} else {
				if (videoPortNum == 0) {
					audioPorts = new int[] { recvPorts[0], sendPorts[0] };
				} else {
					int audioRecv = recvPorts[0];
					int audioSend = sendPorts[0];
					if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
						audioRecv = recvPorts[1];
					}
					if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
						audioSend = sendPorts[1];
					}
					audioPorts = new int[] { audioRecv, audioSend };
				}
			}
		} else if (audioPortNum == 1) {
			if (videoPortNum == 0) {
				if ((audioType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
					audioPorts = new int[] { recvPorts[0] };
				} else {
					audioPorts = new int[] { sendPorts[0] };
				}
			} else {
				if ((audioType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
					if ((videoType.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
						audioPorts = new int[] { recvPorts[1] };
					} else {
						audioPorts = new int[] { recvPorts[0] };
					}
				} else {
					if ((videoType.getValue() & INVITE_TYPE.PUSH.getValue()) > 0) {
						audioPorts = new int[] { sendPorts[1] };
					} else {
						audioPorts = new int[] { sendPorts[0] };
					}
				}
			}
		}
		if (videoPortNum > 0) {
			medias.addAll(createDefaultMedia(MEDIA_TYPE.VIDEO, sendVideoMedia,
					sendIp, videoType, videoPorts));
		}
		if (audioPortNum > 0) {
			medias.addAll(createDefaultMedia(MEDIA_TYPE.AUDIO, sendAudioMedia,
					sendIp, audioType, audioPorts));
		}
		return medias;
	}

	public static ArrayList<Media> createDefaultMedias(String sendIp,
			Media sendVideoMedia, Media sendAudioMedia, INVITE_TYPE videoType,
			INVITE_TYPE audioType, int[] recvPorts, int[] sendPorts) {
		return createDefaultMedias(sendIp, sendVideoMedia, sendAudioMedia,
				videoType, audioType, recvPorts, sendPorts, false, false);
	}

	public static class RtpMap {
		public int payload;
		public String rtpmapValue;

		public RtpMap(int payload, String rtpmapValue) {
			super();
			this.payload = payload;
			this.rtpmapValue = rtpmapValue;
		}

		@Override
		public String toString() {
			return "RtpMap [payload=" + payload + ", rtpmapValue="
					+ rtpmapValue + "]";
		}
	}

	public enum MEDIA_TYPE {
		VIDEO("video"), AUDIO("audio");
		public String name;

		MEDIA_TYPE(String name) {
			this.name = name;
		}
	}

	/**
	 * 视频编码类型：分为H264编码和H265编码
	 * 
	 */
	public enum VIDEO_TYPE {
		/**
		 * h264采样率90000的视频源编码类型
		 */
		H264(96, "H264/90000"),
		/**
		 * h265采样率90000的视频编码类型
		 */
		H265(97, "H265/90000");
		public String name;
		public int payload;

		VIDEO_TYPE(int payload, String name) {
			this.payload = payload;
			this.name = name;
		}
	}

	/**
	 * 音频编码类型
	 * 
	 */
	public enum AUDIO_TYPE {
		/**
		 * G711A格式音频编码
		 */
		PCMA(8, "PCMA"),
		/**
		 * G711U格式音频编码
		 */
		PCMU(0, "PCMU"),
		/**
		 * AAC格式音频编码
		 */
		AAC(98, "MPEG4-GENERIC");
		public String name;
		public int payload;

		AUDIO_TYPE(int payload, String name) {
			this.payload = payload;
			this.name = name;
		}
	}

	/**
	 * 音频采样率
	 * 
	 */
	public enum Audio_Sample {
		/**
		 * 8000的音频采样率
		 */
		RATE_8K("/8000"),
		/**
		 * 16000的音频采样率
		 */
		RATE_16K("/16000"),
		/**
		 * 32000的音频采样率
		 */
		RATE_32K("/32000"),
		/**
		 * 44100的音频采样率
		 */
		RATE_44_1K("/44100");
		public String name;

		Audio_Sample(String name) {
			this.name = name;
		}
	}

	/**
	 * 声道类型（单声道和立体声）
	 * 
	 */
	public enum AUDIO_CHANNEL {
		/**
		 * 单声道音频
		 */
		MONO(""),
		/**
		 * 立体声音频
		 */
		STEREO("/2");
		public String name;

		AUDIO_CHANNEL(String name) {
			this.name = name;
		}
	}

	public static class Media implements Comparable<Media> {
		/**
		 * 流的方向
		 */
		public INVITE_TYPE type;
		/**
		 * m字段，媒体描述字段，格式<media><port>/<number of port><proto><fmt>
		 * media目前只能是video或者audio，port目前也只有一个该值是发起者的接收端口
		 * proto可以是UDP、RTP/AVP、RTP/SAVP,目前使用RTP/AVP
		 * fme是标准格式的映射如：96代表H264编码视频、8代表PCMU音频。上报可以是多个但是点播只能是1个
		 */
		public String name;
		public int mediaPort;
		private String proto = "RTP/AVP";

		// m字段中声明rtpmap的键，a字段中定义具体属性值
		public TreeMap<String, RtpMap> rtpmap = new TreeMap<String, RtpMap>();
		/**
		 * c字段，与SdpMessage的c字段一致，该字段可以缺省
		 * 
		 */
		public String mediaIp;
		
		public boolean isDemux;
		
		public int denuxId;

		/**
		 * a字段，描述媒体信息的，可以有很多，目前定义的一个必须的是流的方向，有三种：sendrecv、sendonly、recvonly
		 * 目前封装sdp就直接根据INVITE_TYPE来定义
		 */
		public ArrayList<String> mediaInfos = new ArrayList<String>();

		@Override
		public int compareTo(Media another) {
			if (another == null) {
				return 1;
			}
			if (another.type.getValue() != type.getValue()) {
				return another.type.getValue() - type.getValue();
			}
			if (!name.equals(another.name)) {
				if (MEDIA_TYPE.AUDIO.name.equals(name)) {
					return 1;
				} else {
					return -1;
				}
			}
			return 0;
		}

		@Override
		public String toString() {
			return "Media [type=" + type + ", name=" + name + ", mediaPort="
					+ mediaPort + ", proto=" + proto + ", rtpmap=" + rtpmap
					+ ", mediaIp=" + mediaIp + ", mediaInfos=" + mediaInfos
					+ "]";
		}
	}

	private static final String NEWLINE = "\n";
	private static final String SPACE = " ";

	public SpsStructure sps;
	public PpsStructure pps;

	public String createSdp() {
		// 首先检查必要字段是否存在
		if (TextUtils.isEmpty(sendId)
				|| (TextUtils.isEmpty(sendIp) && TextUtils.isEmpty(receiveIp))
				|| medias == null || medias.size() == 0) {
			ToolLog.i("ERROR！程序出了错！sdp缺少必要字段");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("v=").append(sdpVersion);// v
		sb.append(NEWLINE);

		sb.append("o=").append(sendId).append(SPACE).append(sessionId)
				.append(SPACE).append(sessionVersion).append(SPACE)
				.append(nettype).append(SPACE).append(addrtype).append(SPACE)
				.append(sendIp);// o
		sb.append(NEWLINE);

		sb.append("s=Play");// s
		sb.append(NEWLINE);

		sb.append("i=stream");// i
		sb.append(NEWLINE);

		if (receiveIp != null) {
			sb.append("c=").append(nettype).append(SPACE).append(addrtype)
					.append(SPACE).append(receiveIp);// c
			sb.append(NEWLINE);
		}

		sb.append("b=").append(bandInfo);// b
		sb.append(NEWLINE);

		sb.append("t=").append(sessionActivTime);// t
		sb.append(NEWLINE);

		if (sessionInfos.size() > 0) {
			for (String info : sessionInfos) {
				sb.append("a=").append(info);// a
				sb.append(NEWLINE);
			}
		}

		// 媒体描述字段
		for (Media media : medias) {
			sb.append("m=").append(media.name).append(SPACE)
					.append(media.mediaPort).append(SPACE).append(media.proto);
			for (Map.Entry<String, RtpMap> entry : media.rtpmap.entrySet()) {
				sb.append(SPACE);
				RtpMap value = entry.getValue();
				sb.append(value.payload);
			}
			sb.append(NEWLINE);

			if (!TextUtils.isEmpty(media.mediaIp)) {
				sb.append("c=").append(nettype).append(SPACE).append(addrtype)
						.append(SPACE).append(media.mediaIp);// c
				sb.append(NEWLINE);
			}

			if (media.mediaInfos.size() > 0) {
				for (String info : media.mediaInfos) {
					if ((info.endsWith("only")) || (info.endsWith("recv"))) {
						continue;
					}
					sb.append("a=").append(info);// a
					sb.append(NEWLINE);
				}
			}
			switch (media.type) {
			case PLAY:
				sb.append("a=").append("recvonly");// a
				sb.append(NEWLINE);
				break;
			case PUSH:
				sb.append("a=").append("sendonly");// a
				sb.append(NEWLINE);
				break;
			case PLAY_PUSH:
				sb.append("a=").append("sendrecv");// a
				sb.append(NEWLINE);
				break;
			case INACTIVE:
				sb.append("a=").append("inactive");// a
				sb.append(NEWLINE);
				break;
			}
		}
		return sb.toString();
	}

	public static SdpMessage parseSdp(String sdp) {
		if (TextUtils.isEmpty(sdp)) {
			return null;
		}
		if (!sdp.startsWith("v=")) {
			return null;
		}
		SdpMessage message = new SdpMessage();
		String[] split = sdp.split(NEWLINE);
		String line = "";
		int lineNum = 0;
		for (; lineNum < split.length; lineNum++) {
			line = split[lineNum].trim();
			if (line.startsWith("m=")) {
				break;
			}
			fillSessionField(message, line);
		}
		if (lineNum > 0) {
			message.medias = new ArrayList<Media>();
		}
		while (lineNum > 0 && lineNum < split.length) {
			Media media = new Media();
			for (int i = lineNum; lineNum < split.length; lineNum++) {
				line = split[lineNum].trim();
				if (line.startsWith("m=") && lineNum > i) {
					break;
				}
				fillMediaField(media, line);
			}
			if (isActive(media)) {
				message.medias.add(media);
				if ("video".equals(media.name)) {
					for (String info : media.mediaInfos) {
						if (info.contains("sprop-parameter-sets")) {
							String[] params = info.split(";");
							for (String param : params) {
								if (param.startsWith("sprop-parameter-sets=")) {
									int index = param.indexOf("=");
									String substring = param
											.substring(index + 1);
									String[] pbsp = substring.split(",");
									if (pbsp != null && pbsp.length == 2) {
										// 解析sps字段
										byte[] spsDecode = Base64.decode(
												pbsp[0], Base64.DEFAULT);
										StringBuilder spsSb = new StringBuilder();
										for (byte b : spsDecode) {
											spsSb.append(byte2Str(b));
										}
										message.sps = parserSps(spsSb);
										// 解析pps字段
										byte[] ppsDecode = Base64.decode(
												pbsp[1], Base64.DEFAULT);
										StringBuilder ppsSb = new StringBuilder();
										for (byte b : ppsDecode) {
											ppsSb.append(byte2Str(b));
										}
										message.pps = parserPps(ppsSb);
									}
								}
							}
						}
					}
				}
			}
		}
		return message;
	}

	public SdpMessage getVideoPlayPart(INVITE_TYPE type) {
		Iterator<Media> it = medias.iterator();
		while (it.hasNext()) {
			Media next = it.next();
			if ((next.type.getValue() & type.getValue()) > 0
					&& MEDIA_TYPE.VIDEO.name.equals(next.name)) {
				// 如果是sendrecv将其转换成recvonly
				next.type = type;
				// if (next.mediaIp == null)
				// {
				// next.mediaIp = receiveIp;
				// }
				// 1.技勤返回的200ok居然是sendrecv！
				// 2.给编码器的recvonly带fmtp字段，编码器居然会死机！
				if (type == INVITE_TYPE.PLAY) {
					Iterator<String> infosIt = next.mediaInfos.iterator();
					while (infosIt.hasNext()) {
						String info = infosIt.next();
						if (info.startsWith("fmtp")) {
							infosIt.remove();
						}
					}
				}
			} else {
				it.remove();
			}
		}
		return this;
	}

	/**
	 * 将不是点播的部分裁剪掉
	 * 
	 * @return
	 */
	public SdpMessage cutNonPlayPart() {
		Iterator<Media> it = medias.iterator();
		while (it.hasNext()) {
			Media next = it.next();
			if ((next.type.getValue() & INVITE_TYPE.PLAY.getValue()) > 0) {
				// 如果是sendrecv将其转换成recvonly
				next.type = INVITE_TYPE.PLAY;
				// if (next.mediaIp == null)
				// {
				// next.mediaIp = receiveIp;
				// }
				// 1.技勤返回的200ok居然是sendrecv！
				// 2.给编码器的recvonly带fmtp字段，编码器居然会死机！
				Iterator<String> infosIt = next.mediaInfos.iterator();
				while (infosIt.hasNext()) {
					String info = infosIt.next();
					if (info.startsWith("fmtp")) {
						infosIt.remove();
					}
				}
			} else {
				it.remove();
			}
		}
		return this;
	}

	private static boolean isActive(Media media) {
		boolean active = false;
		if (media != null && media.mediaInfos != null) {
			for (String info : media.mediaInfos) {
				if (info.equals("inactive")) {
					return active;
				}
			}
			active = true;
		}
		return active;
	}

	private static void fillSessionField(SdpMessage message, String line) {
		// 这里为了简便直接使用switch(String)算了
		if (line.length() > 2) {
			int indexOf = line.indexOf("=");
			if (indexOf == 1) {
				String fieldName = line.substring(0, 1);
				String value = line.substring(2);
				if ("v".equalsIgnoreCase(fieldName)) {
					message.sdpVersion = value;
				} else if ("o".equalsIgnoreCase(fieldName)) {
					String[] split = value.split(SPACE);
					if (split.length == 6) {
						message.sendId = split[0];
						message.sessionId = split[1];
						message.sessionVersion = split[2];
						message.nettype = split[3];
						message.addrtype = split[4];
						message.sendIp = split[5];
					}
				} else if ("c".equalsIgnoreCase(fieldName)) {
					String[] split = value.split(SPACE);
					if (split.length == 3) {
						message.receiveIp = split[2];
					}
				} else if ("b".equalsIgnoreCase(fieldName)) {
					message.bandInfo = value;
				} else if ("t".equalsIgnoreCase(fieldName)) {
					message.sessionActivTime = value;
				} else if ("a".equalsIgnoreCase(fieldName)) {
					message.sessionInfos.add(value);
				}
			}
		}
	}

	private static void fillMediaField(Media media, String line) {
		if (line.length() > 2) {
			int indexOf = line.indexOf("=");
			if (indexOf == 1) {
				String fieldName = line.substring(0, 1);
				String value = line.substring(2);
				if ("m".equalsIgnoreCase(fieldName)) {
					String[] split = value.split(SPACE);
					if (split.length >= 4) {
						media.name = split[0];
						media.mediaPort = Integer.parseInt(split[1]);
					}
				} else if ("c".equalsIgnoreCase(fieldName)) {
					String[] split = value.split(SPACE);
					if (split.length == 3) {
						media.mediaIp = split[2];
					}
				} else if ("a".equalsIgnoreCase(fieldName)) {
					if (value.startsWith("rtpmap:")) {
						String[] split = value.split(SPACE);
						if (split.length > 0) {
							String key = split[0].substring(7);
							int payload = Integer.parseInt(key);
							String rtpValue = split[1];
							RtpMap rtpMap = new RtpMap(payload, value);
							media.rtpmap.put(rtpValue, rtpMap);
						}
					} else if (value.startsWith("muxid:")) {
						String[] split = value.split(":");
						if (split.length > 0) {
							media.isDemux = true;
							media.denuxId = Integer.parseInt(split[1]);
						}
					} else {
						if (value.equals("sendonly")) {
							media.type = INVITE_TYPE.PUSH;
						} else if (value.equals("recvonly")) {
							media.type = INVITE_TYPE.PLAY;
						} else if (value.equals("sendrecv")) {
							media.type = INVITE_TYPE.PLAY_PUSH;
						}
					}
					media.mediaInfos.add(value);
				}
			}
		}
	}

	public void sort() {
		if (medias != null) {
			Collections.sort(medias);
		}
	}

	public static PpsStructure parserPps(StringBuilder sb) {
		PpsStructure pps = new PpsStructure();
		int spsStart = read(sb, 8);
		if ((spsStart & 0x1f) != 8) {
			// pps字段的第一个字节是校验码,低4位必须是8
			return null;
		}
		// pic_parameter_set_id ue(v)
		pps.pic_parameter_set_id = decodeUGolom(sb);
		// seq_parameter_set_id ue(v)
		pps.seq_parameter_set_id = decodeUGolom(sb);
		// entropy_coding_mode_flag u(1)
		pps.entropy_coding_mode_flag = read(sb, 1);
		// pic_order_present_flag u(1)
		pps.pic_order_present_flag = read(sb, 1);
		// num_slice_groups_minus1 ue(v)
		pps.num_slice_groups_minus1 = decodeUGolom(sb);
		if (pps.num_slice_groups_minus1 > 0) {
			// slice_group_map_type ue(v)
			pps.slice_group_map_type = decodeUGolom(sb);
			if (pps.slice_group_map_type == 0) {
				pps.run_length_minus1 = new int[pps.num_slice_groups_minus1];
				for (int iGroup = 0; iGroup <= pps.num_slice_groups_minus1; iGroup++) {
					// run_length_minus1[iGroup] ue(v)
					pps.run_length_minus1[iGroup] = decodeUGolom(sb);
				}
			} else if (pps.slice_group_map_type == 2) {
				pps.top_left = new int[pps.num_slice_groups_minus1];
				pps.bottom_right = new int[pps.num_slice_groups_minus1];
				for (int iGroup = 0; iGroup <= pps.num_slice_groups_minus1; iGroup++) {
					// top_left[iGroup] ue(v)
					// bottom_right[iGroup] ue(v)
					pps.top_left[iGroup] = decodeUGolom(sb);
					pps.bottom_right[iGroup] = decodeUGolom(sb);
				}
			} else if (pps.slice_group_map_type == 3
					|| pps.slice_group_map_type == 4
					|| pps.slice_group_map_type == 5) {
				// slice_group_change_direction_flag u(1)
				pps.slice_group_change_direction_flag = read(sb, 1);
				// slice_group_change_rate_minus1 ue(v)
				pps.slice_group_change_rate_minus1 = decodeUGolom(sb);
			} else if (pps.slice_group_map_type == 6) {
				// pic_size_in_map_units_minus1 ue(v)
				pps.pic_size_in_map_units_minus1 = decodeUGolom(sb);
				pps.slice_group_id = new int[pps.pic_size_in_map_units_minus1];
				for (int i = 0; i <= pps.pic_size_in_map_units_minus1; i++) {
					// slice_group_id[i] ue(v)
					pps.slice_group_id[i] = decodeUGolom(sb);
				}
			}
		}
		// num_ref_idx_l0_active_minus1 ue(v)
		pps.num_ref_idx_l0_active_minus1 = decodeUGolom(sb);
		// num_ref_idx_l1_active_minus1 ue(v)
		pps.num_ref_idx_l1_active_minus1 = decodeUGolom(sb);
		// weighted_pred_flag u(1)
		pps.weighted_pred_flag = read(sb, 1);
		// weighted_bipred_idc u(2)
		pps.weighted_bipred_idc = read(sb, 2);
		// pic_init_qp_minus26 /* relative to 26 */ se(v)
		pps.pic_init_qp_minus26 = decodeSGolom(sb);
		// pic_init_qs_minus26/* relative to 26 */ se(v)
		pps.pic_init_qs_minus26 = decodeSGolom(sb);
		// chroma_qp_index_offset se(v)
		pps.chroma_qp_index_offset = decodeSGolom(sb);
		// deblocking_filter_control_present_flag u(1)
		pps.deblocking_filter_control_present_flag = read(sb, 1);
		// constrained_intra_pred_flag u(1)
		pps.constrained_intra_pred_flag = read(sb, 1);
		// redundant_pic_cnt_present_flag u(1)
		pps.redundant_pic_cnt_present_flag = read(sb, 1);
		if (sb.length() > 8) {
			// transform_8x8_mode_flag u(1)
			pps.transform_8x8_mode_flag = read(sb, 1);
			// pic_scaling_matrix_present_flag u(1)
			pps.pic_scaling_matrix_present_flag = read(sb, 1);
			if (pps.pic_scaling_matrix_present_flag == 1) {
				pps.pic_scaling_list_present_flag = new int[6 + 2 * pps.transform_8x8_mode_flag];
				for (int i = 0; i < 6 + 2 * pps.transform_8x8_mode_flag; i++) {
					// pic_scaling_list_present_flag[i] u(1)
					pps.pic_scaling_list_present_flag[i] = read(sb, 1);
				}
			}
			// second_chroma_qp_index_offset se(v)
			pps.second_chroma_qp_index_offset = decodeSGolom(sb);
		}
		return pps;
	}

	/**
	 * 解析sps
	 * 
	 * @param sb
	 * @return
	 */
	public static SpsStructure parserSps(StringBuilder sb) {
		SpsStructure sps = new SpsStructure();
		int spsStart = read(sb, 8);
		if ((spsStart & 0x1f) != 7) {
			// sps字段的第一个字节是校验码,低4位必须是7(pps的低4位是8)
			return null;
		}
		// profile_idc u(8)
		sps.profile_idc = read(sb, 8);
		// constraint_set0_flag u(1)
		sps.constraint_set0_flag = read(sb, 1);
		// constraint_set1_flag u(1)
		sps.constraint_set1_flag = read(sb, 1);
		// constraint_set2_flag u(1)
		sps.constraint_set2_flag = read(sb, 1);
		// constraint_set3_flag u(1)
		sps.constraint_set3_flag = read(sb, 1);
		// reserved_zero_4bits /* equal to 0 */ u(4)
		sps.reserved_zero_4bits = read(sb, 4);
		// level_idc u(8)
		sps.level_idc = read(sb, 8);
		// seq_parameter_set_id ue(v)无符号指数哥伦布编码
		sps.seq_parameter_set_id = decodeUGolom(sb);

		if (sps.profile_idc == 100 || sps.profile_idc == 110
				|| sps.profile_idc == 122 || sps.profile_idc == 144) {
			// chroma_format_idc ue(v)
			sps.chroma_format_idc = decodeUGolom(sb);
			if (sps.chroma_format_idc == 3) {
				// residual_colour_transform_flag u(1)
				sps.residual_colour_transform_flag = read(sb, 1);
			}
			// bit_depth_luma_minus8 ue(v)
			sps.bit_depth_luma_minus8 = decodeUGolom(sb);
			// bit_depth_chroma_minus8 ue(v)
			sps.bit_depth_chroma_minus8 = decodeUGolom(sb);
			// qpprime_y_zero_transform_bypass_flag u(1)
			sps.qpprime_y_zero_transform_bypass_flag = read(sb, 1);
			// seq_scaling_matrix_present_flag u(1)
			sps.seq_scaling_matrix_present_flag = read(sb, 1);
			if (sps.seq_scaling_matrix_present_flag == 1) {
				sps.seq_scaling_list_present_flag = new int[8];
				for (int i = 0; i < 8; i++) {
					// seq_scaling_list_present_flag[i] u(1)
					sps.seq_scaling_list_present_flag[i] = read(sb, 1);
				}
			}
		}
		// log2_max_frame_num_minus4 ue(v)
		sps.log2_max_frame_num_minus4 = decodeUGolom(sb);
		// pic_order_cnt_type ue(v)
		sps.pic_order_cnt_type = decodeUGolom(sb);
		if (sps.pic_order_cnt_type == 0) {
			// log2_max_pic_order_cnt_lsb_minus4 ue(v)
			sps.log2_max_pic_order_cnt_lsb_minus4 = decodeUGolom(sb);
		} else if (sps.pic_order_cnt_type == 1) {
			sps.delta_pic_order_always_zero_flag = read(sb, 1);
			// offset_for_non_ref_pic se(v) 有符号指数哥伦布编码
			sps.offset_for_non_ref_pic = decodeSGolom(sb);
			// offset_for_top_to_bottom_field se(v)
			sps.offset_for_top_to_bottom_field = decodeSGolom(sb);
			// num_ref_frames_in_pic_order_cnt_cycle ue(v)
			sps.num_ref_frames_in_pic_order_cnt_cycle = decodeUGolom(sb);
			sps.offset_for_ref_frame = new int[sps.num_ref_frames_in_pic_order_cnt_cycle];
			for (int i = 0; i < sps.num_ref_frames_in_pic_order_cnt_cycle; i++) {
				// offset_for_ref_frame se(v)
				sps.offset_for_ref_frame[i] = decodeSGolom(sb);
			}
		}
		// num_ref_frames ue(v)
		sps.num_ref_frames = decodeUGolom(sb);
		// gaps_in_frame_num_value_allowed_flag u(1)
		sps.gaps_in_frame_num_value_allowed_flag = read(sb, 1);
		// pic_width_in_mbs_minus1 ue(v)
		sps.pic_width_in_mbs_minus1 = decodeUGolom(sb);
		// pic_height_in_map_units_minus1 ue(v)
		sps.pic_height_in_map_units_minus1 = decodeUGolom(sb);
		// frame_mbs_only_flag u(1)
		sps.frame_mbs_only_flag = read(sb, 1);
		if (sps.frame_mbs_only_flag == 0) {
			// mb_adaptive_frame_field_flag u(1)
			sps.mb_adaptive_frame_field_flag = read(sb, 1);
		}
		// direct_8x8_inference_flag u(1)
		sps.direct_8x8_inference_flag = read(sb, 1);
		// frame_cropping_flag u(1)
		sps.frame_cropping_flag = read(sb, 1);
		if (sps.frame_cropping_flag == 1) {
			// frame_crop_left_offset ue(v)
			// frame_crop_right_offset ue(v)
			// frame_crop_top_offset ue(v)
			// frame_crop_bottom_offset ue(v)
			sps.frame_crop_left_offset = decodeUGolom(sb);
			sps.frame_crop_right_offset = decodeUGolom(sb);
			sps.frame_crop_top_offset = decodeUGolom(sb);
			sps.frame_crop_bottom_offset = decodeUGolom(sb);
		}
		// vui_parameters_present_flag u(1)
		sps.vui_parameters_present_flag = read(sb, 1);
		return sps;
	}

	/**
	 * 反编码有符号指数哥伦布编码
	 * 
	 * @param sb
	 * @return
	 */
	public static int decodeSGolom(StringBuilder sb) {
		int codeNum = decodeUGolom(sb);
		return (int) (Math.pow(-1, codeNum + 1) * Math.ceil(codeNum / 2.0));
	}

	/**
	 * 反编码无符号指数哥伦布编码
	 * 
	 * @param sb
	 */
	public static int decodeUGolom(StringBuilder sb) {
		int zeroNum = 0;
		while (read(sb, 1) == 0) {
			zeroNum++;
		}
		if (zeroNum == 0) {
			return 0;
		} else {
			return (int) (Math.pow(2, zeroNum) - 1 + read(sb, zeroNum));
		}
	}

	public static int read(StringBuilder sb, int len) {
		String substring = sb.substring(0, len);
		sb.delete(0, len);
		return Integer.parseInt(substring, 2);
	}

	public static String byte2Str(byte b) {
		StringBuilder sb = new StringBuilder();
		String binaryString = Integer.toBinaryString(0xff & b);
		int len = 8 - binaryString.length();
		String format = "00000000";
		sb.append(format.substring(0, len));
		sb.append(binaryString);
		return sb.toString();
	}

	@Override
	public String toString() {
		return "SdpMessage [sdpVersion=" + sdpVersion + ", sendId=" + sendId
				+ ", sendIp=" + sendIp + ", nettype=" + nettype + ", addrtype="
				+ addrtype + ", sessionId=" + sessionId + ", sessionVersion="
				+ sessionVersion + ", receiveIp=" + receiveIp + ", bandInfo="
				+ bandInfo + ", sessionActivTime=" + sessionActivTime
				+ ", sessionInfos=" + sessionInfos + ", medias=" + medias
				+ ", sps=" + sps + ", pps=" + pps + "]";
	}
}