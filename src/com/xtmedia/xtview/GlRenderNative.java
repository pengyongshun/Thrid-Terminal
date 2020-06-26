package com.xtmedia.xtview;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Surface;

import com.xt.mobile.terminal.view.XTMediaSource;

/**
 * 20151019日库版本移除此方法。 public static native boolean rtSendVideoData(long handle,
 * byte[] buffer,int bufferLength, int data_type, int timestamp);
 * 
 * @author XT
 * 
 */
public class GlRenderNative {
	public static final String STOP_ACTION = "com.xt.action.stopPlay";
	// private static ArrayList<Long> handles = new ArrayList<Long>();
	/* key:handle,value:ture is can use or not can use */

	@SuppressLint("UseSparseArrays")
	private static Map<Long, Boolean> handles = Collections
			.synchronizedMap(new HashMap<Long, Boolean>());

	public static Map<Long, Boolean> getHandles() {
		return handles;
	}

	private static final String TAG = GlRenderNative.class.getSimpleName();

	public static boolean isLoadLibraryOk = false;
	static {
		try {
			System.loadLibrary("avcodec-56");
			System.loadLibrary("avdevice-56");
			System.loadLibrary("avfilter-5");
			System.loadLibrary("avformat-56");
			System.loadLibrary("avutil-54");
			System.loadLibrary("swresample-1");
			System.loadLibrary("swscale-3");
			Log.i("xt_log", "init ffmpeg finish!");
			System.loadLibrary("xt_config");
			System.loadLibrary("rv_adapter");
			System.loadLibrary("xt_media_server");
			System.loadLibrary("xt_media_client");
			System.loadLibrary("common_lib");
			Log.i("xt_log", "init media finish!");
			System.loadLibrary("xt_media_player");
			Log.i("xt_log", "init player finish!");
			System.loadLibrary("xt_router");
			System.loadLibrary("MediaDecodec");
			System.loadLibrary("SimplePlayer-jni");
			System.loadLibrary("xtudp");
			System.loadLibrary("xtudt");
			Log.i("xt_log", "init lib finish!");
			isLoadLibraryOk = true;
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			isLoadLibraryOk = false;
		}
	}

	/*
	 * //
	 * **********************2016-09-22新增接口***********************************
	 * 
	 * public static native int opensingle(String ip, int nport, int channel,
	 * Surface surface, boolean bharddecode);
	 * 
	 * public static native int opensingles(String ip, int nport, int channel,
	 * Surface surface, boolean bharddecode);
	 * 
	 * public static native int opensingle2(Surface surface, int port1, int
	 * port2, boolean bharddecode);
	 * 
	 * public static native void sethkVolume(int nTaskIndex, int nVolume);
	 * 
	 * public static native int OpenSipSdps(String sdp, long handle, Surface
	 * surface, boolean bharddecode);
	 * 
	 * public static native void parseMeidaInfofromStream( boolean
	 * bParseMeidaInfofromStream);
	 * 
	 * public static native void MediaSkipToIFrame(boolean bkipIFrame, int
	 * maxFrames);
	 * 
	 * 
	 * HK接口
	 * 
	 * public static native int openhkdevice(String ip, int nport, int channel,
	 * Surface surface);
	 * 
	 * public static native void stophkdevice(int nTaskIndex);
	 * 
	 * public static native void openhkvolume(int nTaskIndex);
	 * 
	 * public static native int rnewhkview(int nTaskIndex, Surface surface);
	 * 
	 * public static native void setHkCallbackObj(Object obj);
	 * 
	 * public static native void closehkvolume(int nTaskIndex);
	 * 
	 * public static native int mediaLocalPlays(long handle, Surface surface,
	 * boolean bharddecode);
	 */

	// *****************************************************************
	/*********************************************************************************
	 * jni interface
	 * *******************************************************************************/
	/**
	 * 
	 * @param nIndexTask
	 * @return 判断是否有数据完成了一次正常的渲染
	 */
	public static native boolean render(int nIndexTask);

	public static native void harizonplay();

	public static native void vericalplay();

	public static native void setPlayStatusEnable(Object obj);

	/**
	 * 绑定一个surface
	 * 
	 * @param surface
	 * @return 0和-1是不成功，其他都是成功的
	 */
	public native static long SetVideoSurface(Surface surface);

	/**
	 * 抓拍接口：
	 * 
	 * @param nTaskIndex
	 *            任务号
	 * @param scappath
	 *            文件保存路径
	 * @return
	 */
	public static native boolean capturepic(int nTaskIndex, String scappath);

	public static native boolean StartRecord(int nTaskIndex, String scappath);

	public static native boolean StopRecord(int nTaskIndex);

	public static native boolean ClearScreen(int nTaskIndex);

	public static native void setscreenrect(int x, int y, int w, int h, int screentype);

	public static native int openurl(String url, int x, int y, int w, int h);

	public static native int setviewpoint(int nTaskIndex, int x, int y, int w, int h);

	public static native int setdisplayrect(int nTaskIndex, int x, int y, int w, int h);

	public static native int opensingleurl(String url);

	public static native int openstd(String url, long holderobj);

	public static native int opensingle(String ip, int nport, int channel, int x, int y, int w,
			int h);

	public static native int opensingles(String ip, int nport, int channel, Surface surface,
			boolean bharddecode);

	public static native int opensingleex(String ip, int nport, int channel, long holderobj);

	public static native int closesingle(int lobj, long handle);

	public static native int closesingles(int lobj);// 只停解码，不销毁链接
	public static native int stopPlaySaveLink(int lobj, long handle);// 只停解码，不销毁链接

	public static native int startplay(int channel, int x, int y, int w, int h);

	public static native void stopplay(int channel);

	public static native void mediaSetAudioCallback(int nTaskIndex, Object obj);

	public static native void mediaResetAudioCallback(int nTaskIndex);

	/**
	 * 异常断开通知函数原型为 void breaktrack(int ctx,int handle);
	 * 
	 * @param nTimeOut
	 *            超时时间（毫秒）
	 * @param obj
	 *            含有void breaktrack(int ctx,int handle) 方法的 一个类的对象实例
	 * @param ctx
	 *            mediaLocalPlay 的返回值
	 */

	public static native void rtRegisterDataBreakCallback(int nTimeOut, Object obj, long ctx);

	public static native int setStreamDownCallback(int index, int milltime);

	/*
	 * 停止rtsp播出 nTaskIndex 任务号
	 */
	public static native void stoprtspplay(int nTaskIndex);

	/*
	 * set display mode if bpps = 0, the pictute is match with src frames, or
	 * else is stretched
	 */
	public static native void setdistype(int nType, boolean bPPs);

	/*
	 * 设置分屏类型 screentype 屏幕类型 0/1/2/3/4 -- 1屏/4分屏/9分屏/16分屏/2分屏
	 */
	public static native void setscreenlayout(int srceentype);

	/*
	 * 设置全屏 nTaskIndex 任务号
	 */
	public static native void setfullsrceen(int nTaskIndex);

	/*
	 * 退出全屏 nTaskIndex 任务号
	 */
	public static native void exitfullsrceen(int nTaskIndex);

	/**
	 * 
	 * @param udp_bind_ip
	 * @param udp_bind_port
	 * @param data_port_start
	 * @param data_port_num
	 * @return
	 */
	public static native int mediaClientInit(String udp_bind_ip, int udp_bind_port,
			int data_port_start, int data_port_num);

	/**
	 * 
	 */
	public static native void mediaClientTerm();

	/**
	 * 与mediaServerTerm成对使用
	 * 
	 * @param chan_num
	 * @param local_ip
	 * @param snd_port_start
	 * @param msg_listen_port
	 * @param rtsp_listen_port
	 * @param tcp_listen_port
	 * @param snd_std_rtp
	 *            标准转发开关
	 * @param multiplex
	 * @return
	 */
	public static native int mediaServerInit(int chan_num, String local_ip, int snd_port_start,
			int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp,
			boolean multiplex, XTServerInitCallback callback);

	public static native int mediaServerInitEx(int chan_num, String local_ip, int snd_port_start,
			int msg_listen_port, int rtsp_listen_port, int tcp_listen_port, boolean snd_std_rtp,
			boolean multiplex, boolean isMulSink, XTServerInitCallback callback);

	/**
	 * 与mediaServerDestroySrc成对使用
	 * 
	 * @param tracknum
	 *            只有音频(1),只有视频(1),音频视频都有(2)
	 * 
	 * @param mediaType
	 *            音频(1),视频(2),音频视频都有(其它)
	 * 
	 * @param trackids
	 *            根据sdp获取trackid
	 * @param src_and_chan
	 *            srcno
	 * @return 0表示成功，其他值表示失败
	 */
	public static native int mediaServerCreateSrc(int tracknum, int mediaType, int[] trackids,
			int[] src_and_chan);

	/**
	 * 与mediaServerInit成对使用
	 */
	public static native void mediaServerTerm();

	/**
	 * 
	 * @param srcno
	 *            根据创建的转发源得到的srcno
	 * @return 0表示成功，其他值表示失败
	 */
	public static native int mediaServerDestroySrc(int srcno);

	/**
	 * 
	 * @param srcno
	 *            根据创建的转发源得到的srcno
	 * @param sdp
	 *            sdp
	 * @param sdp_len
	 *            sdp长度
	 * @param data_type
	 *            数据类型 172
	 * @return 0表示成功，其他值表示失败
	 */
	public static native int mediaServerSetKey(int srcno, byte[] sdp, long sdp_len, long data_type);

	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param data
	 * @param len
	 * @param frame_type
	 *            0 视频 ; 1音频
	 * @param device_type
	 *            172
	 * @return
	 */
	public static native int mediaServerSendData(int srcno, int trackid, byte[] data, long len,
			int frame_type, long device_type);

	/**
	 * 
	 * @param srcno
	 *            根据创建的转发源得到的srcno
	 * @param trackid
	 *            根据sdp获取trackid
	 * @param data
	 *            数据
	 * @param len
	 *            数据长度
	 * @param frame_type
	 *            帧类型（I帧（0）,P帧（65536）,音频（1））
	 * @param device_type
	 *            数据类型 海康（2），h264(172)
	 * @return 0表示成功，其他值表示失败
	 */
	public static native int mediaServerSendStdData(int srcno, int trackid, byte[] data, long len,
			int frame_type, long device_type);

	/**
	 * 
	 * @param chan
	 * @param ip
	 * @param port
	 * @param demux
	 * @param demuxid
	 * @return
	 */
	public static native int mediaServerAddSend(int chan, String ip, int port, boolean demux,
			int demuxid);

	/**
	 * 
	 * @param chan
	 * @param ip
	 * @param port
	 * @param demux
	 * @param demuxid
	 * @return
	 */
	public static native int mediaServerDelSend(int chan, String ip, int port, boolean demux,
			int demuxid);

	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param ip
	 * @param port
	 * @return
	 */
	public static native int mediaServerAddStdSend(int srcno, int trackid, String ip, int port);

	/**
	 * 
	 * @param srcno
	 * @param trackid
	 * @param ip
	 * @param port
	 * @return
	 */
	public static native int mediaServerDelStdSend(int srcno, int trackid, String ip, int port);

	/**
	 * <pre>
	 * rtStartPlay 与  rtStopPlay 对应
	 * </pre>
	 * 
	 * @param device_type
	 * @param ip
	 * @param port
	 * @param channel
	 * @param chan
	 * @return
	 */
	private static native long rtStartPlay(int device_type, String ip, int port, long channel,
			int[] chan);

	public static native int rtStartMulticastPlay(int device_type, String ip, int port,
			long channel, String multicastip, int multicastport, int[] chan);

	public static native int openStdMulticast(String ip, int media_type, boolean demux,
			String multicastip, int multicastport, long obj);

	/**
	 * 释放本地采集资源
	 * 
	 * <pre>
	 * rtStartPlay 与  rtStopPlay 对应
	 * </pre>
	 * 
	 * @param handle
	 *            rtCustomPlay方法返回的 src_handle
	 * @return
	 */
	public static native int rtStopPlay(long handle);

	private static native int rtRestartPlay(int device_type, String ip, int port, long channel,
			long old_chan, long[] new_chan);

	/**
	 * sdp格式 v=0 o=- 1423192406342531 1 IN IP4 172.16.3.228 s=RTSP/RTP stream
	 * from IPNC i=2?videoCodecType=H.264 t=0 0 a=tool:LIVE555 Streaming Media
	 * v2010.07.29 a=type:broadcast a=control:* a=range:npt=0-
	 * a=x-qt-text-nam:RTSP/RTP stream from IPNC
	 * a=x-qt-text-inf:2?videoCodecType=H.264 m=video 40050 RTP/AVP 96 c=IN IP4
	 * 0.0.0.0 b=AS:12000 a=rtpmap:96 H264/90000 a=fmtp:96
	 * packetization-mode=1;profile-level-id=64001F;sprop-parameter-sets=
	 * Z2QAKK2ECSZuIzSQgSTNxGaSECSZuIzSQgSTNxGaSECSZuIzSQgSTNxGaSEFTS69fX5P6
	 * /J9eutVCCppdevr8n9fk
	 * +vXWqtAsEtKkAAAB4AAAcIGBAAfQAACMob3vheEQjUAAAAB,aO48sA== a=control:track1
	 * m=audio 4 0051 RTP/AVP 96 c=IN IP4 0.0.0.0 b=AS:256 a=rtpmap:96
	 * MPEG4-GENERIC/16000/2 a=fmtp:96
	 * streamtype=5;profile-level-id=1;mode=AAC-hbr
	 * ;sizelength=13;indexlength=3;indexdeltalength=3;config=1410
	 * a=control:track2
	 * 
	 * 
	 * @param sdp
	 * @param sdp_len
	 *            sdp长度
	 * @param data_type
	 *            172
	 * @param chan
	 *            生成的通道
	 * @return 返回资源句柄src_handle(-1/-2/0为失败)
	 */
	public static native long rtCustomPlay(byte[] sdp, int sdp_len, int data_type, int[] chan);

	/**
	 * 
	 * @param handle
	 *            rtCustomPlay 返回的src_handle 值
	 * @param data
	 *            媒体数据 = 24字节私有头 + 一帧数据;
	 * @param len
	 *            媒体数据的长度
	 * @param frame_type
	 *            帧类型
	 * 
	 *            OV_FRAME_TYPE_UNKNOWN = 0xffffffff, OV_VIDEO_I = 0x00000000,
	 *            OV_AUDIO = 0x00000001, HC_HEADE = 0x68, HC_AUDIO = 0x69,
	 *            OV_HEADE = 0x80, OV_VIDEO_P = 0x00010000, OV_VIDEO_B =
	 *            0x00020000, OV_VIDEO_SEI= OV_VIDEO_P, OV_VIDEO_SPS=
	 *            OV_VIDEO_I, OV_VIDEO_PPS= OV_VIDEO_I, OV_AAC = 0x00120000,
	 * 
	 * 
	 * @param data_type
	 *            数据类型，固定为172
	 * @param timestamp
	 *            时间戳(系统时间 秒级*90000/毫秒级*90))
	 * @return
	 */
	public static native int rtSendData(long handle, byte[] data, int len, int frame_type,
			int data_type, int timestamp);

	public static native int rtSendDataStd(int srcno, int trackid, byte[] data, int len,
			int frame_type, int timestamp);

	/**
	 * add 2016122 for USBCamera
	 * 
	 * @param handle
	 * @param data
	 * @param len
	 * @param frame_type
	 * @param data_type
	 * @param timestamp
	 * @param Ssrc
	 *            默认0
	 * @return
	 */
	public static native int rtSendDataSsrc(long handle, byte[] data, int len, int frame_type,
			int data_type, int timestamp, int Ssrc);

	public static native boolean rtSendVideoData(long lh, byte[] data, int len, int data_type,
			long timestamp, String sps, String pps);

	/**
	 * 返回当前采集性能
	 * 
	 * @param handle
	 *            rtCustomPlay 返回的src_handle 值
	 * @param prof
	 *            长度为4的int的数组，用来存放查询到的信息 prof[0] 流量kbs prof[1] 局部丢包率 prof[2]丢包数量
	 *            prof[3]网络抖动
	 * @return 返回是否成功信息，0是成功
	 */
	public static native int rtQueryProf(long handle, int[] prof);

	/*
	 * prof[0]: 去花屏处理流程造成的丢帧数 prof[1]: 解码能力不够造成的丢帧数 prof[2]: 音视频同步跳过不显示的视频帧
	 */
	public static native int QueryDecodeStatus(long nTaskIndex, int[] prof);

	/**
	 * <pre>
	 * mediaLocalPlay 与  mediaLocalStop对应
	 * </pre>
	 * 
	 * @param handle
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	private static native int mediaLocalPlay(long handle, int x, int y, int w, int h);

	public static native int mediaLocalPlays(long handle, Surface surface, boolean bharddecode);

	/**
	 * 硬解播放入口
	 * 
	 * @param handle
	 * @param obj
	 * @return 合法范围是[0,8]
	 */
	public static native int mediaLocalPlayEx(long handle, long obj);

	/**
	 * 
	 * @param nTaskIndex
	 * @return
	 */
	public static native int mediaLocalStop(int nTaskIndex, long handle);

	public static native int xtSendDataByByte(int srcno, int trackid, int MediaType,
			int ChunkCount, int TimeStamp, byte[] buff, long len, int frame_type, int device_type);

	public static native int rtGetChan(long ctx);

	/**
	 * 设置传输库的缓冲大小和是否开启缓冲
	 * 
	 * @param configpath
	 *            xtrouter_config.xml 的绝对路径
	 * @return
	 */
	public static native boolean InitXmlConfig(String configpath);

	/**
	 * 与xtrouter_config.xml中的值对应
	 * 
	 * <pre>
	 * key:config.sink_cfg.resend ; value:1 or 0 1 means open 点播
	 * key:config.sink_cfg.frame_cache ; value:25
	 * key:config.sink_cfg.pkt_cache ; value:1024
	 * key:config.caster_cfg.resend ; value:1 or 0 1 means open 转发
	 * key:config.caster_cfg.MaxSize;MTU
	 * key:config.caster_cfg.packresend;2(20150725新增)
	 * key:config.caster_cfg.ReSendAu;5(20150725新增)
	 * key:config.sink_cfg.max_resend;10(20150725新增)
	 * key:config.sink_cfg.wait_resend;10(20150725新增)
	 * key:config.sink_cfg.priority; value>=0,重传优先级，值越大，优先级越低(20150805新增)
	 * key:config.sink_cfg.priority_cache; 0<= value <=200 (20150805新增)
	 * key:config.caster_cfg.bitrate_controller;(20150807速率控制开关0关闭 ,非0开启)
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 */
	public static native void PutRouterCfg(String key, String value);

	/**
	 * 20150424日新增
	 * 
	 * @param nPlayerIndex
	 *            任务号
	 * @param nType
	 *            1为视频，0为音频
	 * @param bIsEnable
	 *            true为启用，false为禁止
	 */
	public static native void EnableStream(int nPlayerIndex, int nType, boolean bIsEnable);

	public static native void setVolume(int nTaskIndex, int nVolume);

	public static native int xtRegister(String sz_ids, String sz_server_ip, int server_port,
			int millisec);

	public static native int xtStopRegister(String sz_server_ip, int server_port, int millisec);

	public static native void setRegisterCallBack(Object obj);

	public static native void setRegisterResponseCallBack(Object obj);

	public static native int OpenSipSdp(String sdp, long handle, int x, int y, int w, int h);

	public static native int OpenSipSdpEx(String sdp, long handle, long obj);

	public static native int OpenSipSdps(String sdp, long handle, Surface surface,
			boolean bharddecode, boolean bcreatetrans);

	public static native void setMecType(boolean bMecType);

	/**
	 * 为一个源开辟发送的端口
	 * 
	 * @param srcno
	 * @param opt
	 * @return
	 */
	public static native int getSvrInfo(int srcno, Object[] opt, boolean demux);

	public static native int addSend(int srcno, String track, String ip, int port, boolean demux,
			int demuxid);

	public static native int delSend(int srcno, String track, String ip, int port, boolean demux,
			int demuxid);

	public static native int getSDP(long lh, byte[] jsdp);

	/**
	 * 
	 * @param isSync
	 *            是否开启音视频同步
	 * @param SyncMode
	 *            同步模式 0(rtcp同步)/1(时间戳之差同步)/2(帧到达时间同步 TimeUs)
	 * @param TimeUs
	 *            预播放延时 单位为微妙
	 */
	public static native void setSyncInfo(boolean isSync, int SyncMode, int TimeUs);

	public static native int PraseProfile(String profile, int[] pix);

	public static native int setPayload(int srcno, String track, int payload, boolean update);

	// 新增设置编码器帧率的函数，不设置是默认值是15帧
	public static native void setFrameRate(int nFrameRate);

	/*
	 * 该函数获取因为同步的因素造成的限时内点播失败的原因,点播停止前调用 indextask (in) - 任务号 int return 0 - 成功
	 * -1 - 未收到视频RTCP -2 - 未收到音频RTCP -10 - 数据的缓存未达到设定的缓存时间长度 -11 - I帧率未到达 -12 -
	 * 未及时解码，或解码失败 -100 - 点播已关闭
	 */
	public static native int GetErrorInfo(int indextask);

	/**
	 * 虚拟出一个本地播放
	 * 
	 * @param opt
	 *            发送端口等参数
	 * @param new_handle
	 *            获取值(传空的获取后自动赋值)
	 * @return
	 */
	public static native boolean xtMediaClientSipLink(SipLinkOpt[] opt, long[] new_handle);

	/**
	 * 销毁接收端口
	 * 
	 * @param sipLink
	 */
	public static native void MediaClientCloseLink(long sipLink);

	/**
	 * 开启sip转发，利用接收端的sdp来向其转发自己的数据
	 * 
	 * @param sip_link
	 *            xtMediaClientSipLink中new_handle的填充值
	 * @param sdp
	 *            接收到的sdp字符串转换成byte
	 * @param chan
	 *            初始化源的时候填充的值
	 * @return
	 */
	public static native int rtSipPlay(long sip_link, byte[] sdp, int[] chan);

	/*********************************************************************************
	 * native Packaged
	 * *******************************************************************************/
	/**
	 * 开启传输
	 * 
	 * @param device_type
	 * @param ip
	 * @param port
	 * @param channel
	 * @param chan
	 * @return
	 */
	public static long rtStartPlayPackaged(int device_type, String ip, int port, long channel,
			int[] chan) {
		synchronized (handles) {
			long handle = rtStartPlay(device_type, ip, port, channel, chan);

			if (0 != handle && -1 != handle && -2 != handle && -3 != handle) {
				handles.put(handle, true);
			}
			return handle;
		}
	}

	/**
	 * 停止传输(停止点播下级)
	 * 
	 * @param handle
	 * @return
	 */
	public static int rtStopPlayPackaged(long handle) {
		// 没有生成这个句柄或者这个句柄已经被销毁或者这个句柄正在销毁中
		if (!handles.containsKey(handle) || !handles.get(handle)) {
			return -1;
		}
		synchronized (handles) {
			handles.put(handle, false);// 设置为不可用
			int result = rtStopPlay(handle);
			handles.remove(handle);// 移除管理列表
			return result;
		}
	}

	/**
	 * <pre>
	 * 停止播放
	 * mediaLocalPlay 和mediaLocalPlayEx与  mediaLocalStop成对使用
	 * </pre>
	 * 
	 * @param nTaskIndex
	 * @return
	 */
	public static int mediaLocalStopPackaged(int nTaskIndex, long handle) {
		// 没有生成这个句柄或者这个句柄已经被销毁或者这个句柄正在销毁中
		int stopResult = mediaLocalStop(nTaskIndex, handle);
		return stopResult;
	}

	/**
	 * 开始播放
	 * 
	 * @param handle
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public static int mediaLocalPlayPackaged(long handle, int x, int y, int w, int h) {
		// // 没有生成这个句柄或者这个句柄已经被销毁或者这个句柄正在销毁中
		if (!handles.containsKey(handle) || !handles.get(handle)) {
			return -1;
		}
		int result = mediaLocalPlay(handle, x, y, w, h);
		return result;
	}

	/**
	 * *
	 * 
	 * <pre>
	 * 1.生成Handle
	 * 2.主要是fu
	 * </pre>
	 * 
	 * @param device_type
	 *            点播类型
	 * @param ip
	 *            视频源IP
	 * @param port
	 *            端口
	 * @param inChannel
	 *            视频源的点播通道
	 * @param oldHandle
	 *            旧的Handle
	 * @param newHandle
	 *            新的Handle
	 * @return
	 */
	public static int rtRestartPlayPackaged(int device_type, String ip, int port, long inChannel,
			long oldHandle, long[] newHandle) {
		if (!handles.containsKey(oldHandle) || !handles.get(oldHandle)) {
			return -1;
		}
		int result = rtRestartPlay(device_type, ip, port, inChannel, oldHandle, newHandle);

		handles.put(newHandle[0], true);
		return result;
	}

	public static boolean pullStreamPackaged(String id, XTMediaSource source, boolean isLocal) {
		/*
		 * synchronized (handles) { long handle; System.out.println("拉取流sdp:" +
		 * source.getSdp()); String sdp = source.getSdp(); SdpMessage parseSdp =
		 * SdpMessage.parseSdp(sdp); ArrayList<Media> medias = parseSdp.medias;
		 * ArrayList<Media> newMedias = new ArrayList<Media>(); for (Media m :
		 * medias) { if ((m.type.getValue() & INVITE_TYPE.PUSH.getValue()) > 0)
		 * { newMedias.add(m); } } parseSdp.medias = newMedias; String playSdp =
		 * parseSdp.createSdp(); System.out.println("playSdp:" + playSdp); if
		 * (isLocal) { handle = rtSipPlay(SipManager.localSipLink,
		 * playSdp.getBytes(), source.getOutChannel()); } else {
		 * System.out.println("拉流id:" + id); Long remove =
		 * SipManager.remoteSipLink.remove(id); System.out.println("remove@@@:"
		 * + remove); System.out.println("source.getOutChannel():" +
		 * source.getOutChannel().length); handle =
		 * rtSipPlay(remove.longValue(), playSdp.getBytes(),
		 * source.getOutChannel()); System.out.println("handle的值：" + handle); }
		 * source.setHandle(handle); Log.i(TAG, "生成句柄:" + handle); if (0 !=
		 * handle && -1 != handle && -2 != handle && -3 != handle) {
		 * handles.put(handle, true); return true; } // 释放端口 releasePorts(id,
		 * isLocal); return false; }
		 */
		return true;
	}

	/**
	 * 向外推送媒体数据
	 * 
	 * @param srcno
	 *            用来定位是那个源
	 * @param sdp
	 *            目的端的sdp，解析之后获取目的端的ip端口信息
	 * @return
	 */
	// public static boolean pushStreamPackaged(String id, int srcno, String
	// sdp) {
	// boolean success = true;
	// SdpMessage upMessage = SdpMessage.parseSdp(sdp);
	// ArrayList<Media> medias = upMessage.medias;
	// if (medias == null || medias.size() == 0) {
	// return false;
	// }
	// GlRenderNative.mediaServerSetKey(srcno, sdp.getBytes(), sdp.length(),
	// 172);
	// ArrayList<Media> mediaList = new ArrayList<Media>();
	// for (Media media : medias)// 不可能出现null
	// {
	// if ((media.type.getValue() & INVITE_TYPE.PLAY.getValue()) == 0) {
	// continue;
	// }
	// int addSend = 0;
	// PushStream stream = new PushStream(srcno, media.name,
	// media.mediaIp, media.mediaPort);
	// HashSet<String> ids = SipManager.stream_id.get(stream);
	// if (ids == null) {
	// // 说明前面没有人推送过这个流
	// ids = new HashSet<String>();
	// addSend = GlRenderNative.addSend(srcno, media.name,
	// media.mediaIp, media.mediaPort, false, 0);
	// System.out.println("向" + media.mediaIp + "的" + media.mediaPort
	// + "端口" + "推送一路" + media.name + ",其来源为通道" + srcno);
	// } else {
	// // 说明前面有人推送过这个流
	// addSend = 1;
	// System.out.println("跳过向" + media.mediaIp + "的"
	// + media.mediaPort + "端口" + "推送" + media.name
	// + ",其来源为通道" + srcno);
	// }
	// ids.add(id);
	// SipManager.stream_id.put(stream, ids);
	//
	// HashSet<PushStream> streams = SipManager.id_stream.get(id);
	// if (streams == null) {
	// streams = new HashSet<PushStream>();
	// }
	// streams.add(stream);
	// SipManager.id_stream.put(id, streams);
	//
	// if (addSend < 0) {
	// success = false;
	// break;
	// }
	// mediaList.add(media);
	// Log.i(TAG, "addSend:" + addSend);
	// }
	// if (success) {
	// if (mediaList.size() > 2) {
	// // 无法处理
	// for (Media m : mediaList) {
	// PushStream stream = new PushStream(srcno, m.name,
	// m.mediaIp, m.mediaPort);
	// SipManager.id_stream.get(id).remove(stream);
	// HashSet<String> hashSet = SipManager.stream_id.get(stream);
	// hashSet.remove(id);
	// if (hashSet.isEmpty()) {
	// SipManager.stream_id.remove(stream);
	// GlRenderNative.delSend(srcno, m.name, m.mediaIp,
	// m.mediaPort, false, 0);
	// }
	// }
	// return false;
	// }
	// }
	// return success;
	// }

	// /**
	// * 开启传输
	// *
	// * @param srcno
	// * 视频源的编号(用getoutChannel[0]来表示)
	// * @param srcId
	// * 视频源真正来源者的id
	// * @param sdp
	// * 传输时的sdp(上级回复的sdp，因为只用到了ip、port和trackName字段)
	// * @return
	// */
	// public static boolean startTransmitPackaged(String sdp, int[] outChannel)
	// {
	// boolean success = true;
	// SdpMessage message = SdpMessage.parseSdp(sdp);
	// ArrayList<Media> medias = message.medias;
	// for (Media media : medias)// 不可能出现null
	// {
	// int addSend = addSend(outChannel[0], media.name, media.mediaIp,
	// media.mediaPort, false, 0);
	// if (addSend < 0) {
	// success = false;
	// break;
	// }
	// }
	// return success;
	// }

	// /**
	// * 开启传输
	// *
	// * @param srcno
	// * 视频源的编号(用getoutChannel[0]来表示)
	// * @param srcId
	// * 视频源真正来源者的id
	// * @param sdp
	// * 传输时的sdp(上级回复的sdp，因为只用到了ip、port和trackName字段)
	// * @return
	// */
	// public static boolean startTransmitPackaged(long handle, String sdp,
	// int[] outChannel) {
	// boolean success = true;
	// int rtSipPlay = rtSipPlay(handle, sdp.getBytes(), outChannel);
	// if (rtSipPlay < 0) {
	// return false;
	// }
	// SdpMessage message = SdpMessage.parseSdp(sdp);
	// ArrayList<Media> medias = message.medias;
	// for (Media media : medias)// 不可能出现null
	// {
	// int addSend = addSend(outChannel[0], media.name, media.mediaIp,
	// media.mediaPort, false, 0);
	// if (addSend < 0) {
	// success = false;
	// break;
	// }
	// }
	// return success;
	// }

	/**
	 * 停止向上级转发(此方法有问题,应该从sdp中获取上级的接收端口而不是写死,这样需要在Source中新增字段)
	 * 
	 * @param srcno
	 * @param ip
	 * @return
	 */
	/**
	 * 停止转发
	 * 
	 * @param srcno
	 * @param ip
	 * @return
	 */
	// public static int stopPushPackaged(String id) {
	// int result = 0;
	// if (id == null) {
	// return result;
	// }
	// HashSet<PushStream> streams = SipManager.id_stream.remove(id);
	// if (streams == null || streams.isEmpty()) {
	// return result;
	// }
	// HashMap<PushStream, HashSet<String>> stream_id = SipManager.stream_id;
	// for (PushStream stream : streams) {
	// HashSet<String> hashSet = stream_id.get(stream);
	//
	// if (hashSet != null) {
	// // 说明id用户在使用某一路流
	// hashSet.remove(id);
	// if (hashSet.isEmpty()) {
	// // 说明只有id用户在使用这个流,那么对应的流可以关闭了
	// stream_id.remove(stream);
	// int delSend = GlRenderNative.delSend(stream.getChannel(),
	// stream.getName(), stream.getIp(),
	// stream.getReceivePort(), false, 0);
	// if (delSend >= 0) {
	// result++;
	// }
	// }
	// // 到这里说明一路流被多人使用,现在只是删除一个人的使用权
	// }
	// }
	// return result;
	// }

	/*
	 * delete all send from srcno (from createsrc)
	 */
	public static native int mediaServerDelAllSendFromSrc(int srcno);

	public static native int RequestIframe(int indextask);

	public static native int rtStartSipPlayEx(long sip_link, long play_opts, int nums, int[] chan,
			long[] new_handle);

	public static native int rtCreateSrc(String sdp, int length, int[] srcno, int[] src_and_chan);

	public static native int getInfofromInvateSDP(String sdp, int length, Object[] opt);

	public static native void setFirstValidIFrame(boolean bFirstValidIFrame);

	public static native void SetRotationDegrees(int ntaskindex, int rotationDegrees);

	public static native void SetAuidoplaybackstreamtype(int streamtype);
}