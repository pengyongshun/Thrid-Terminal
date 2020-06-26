package com.xt.mobile.terminal.contants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class ConstantsValues {

	public static String SP_KEY_SAVE_LOG = "save_log";// 是否开启日志收集
	public static final int SDCARD_LOG_FILE_SAVE_DAYS = 7;
	public static final boolean LogFlag = true;
	public static final boolean DEBUG = true;
	public static boolean isReport = false;
	public static final String DEFAULT_SP = "xt_terminal_config";
	public static int REGISTER_BREAK_TIME = 15000;// 注册保链时间(实际时间为该时间*3)
	public static int SESSION_BREAK_TIME = 2000;// 会话保链时间(实际时间为该时间*3)
	public static final int HEART_BEAT_MI = 1000 * 3;

	public static final int SERVER_UDP_PORT = 18100;
	public static final int CLIENT_UDP_PORT = 16100;

	public static int RETRY_REGISTER = -1;// 是否重新登录服务器(默认情况是不自动尝试值为-1，如果已经登录过此值改为0)

	// **************soldier-config*******************配置参数-----------------
	public static final String SCREEN_ORIENTATION = "screen_orientation";
	//中心服务
	public static final String CORE_IP = "core_ip";
	public static final String CORE_PORT = "core_port";
	public static int v_CORE_PORT = 433;
	//第三方的用户信息
	public static final String THIRDPARTY_ISCREATMEETING = "thirdparty_iscreatmeeting";
	public static final String THIRDPARTY_USERPWD = "thirdparty_userpwd";
	public static final String THIRDPARTY_USERNAME = "thirdparty_username";
	public static final String THIRDPARTY_MEETINGID = "thirdparty_meetingid";
	public static final String THIRDPARTY_TOKENKEY = "thirdparty_tokenkey";
	public static final String THIRDPARTY_BROADCAST_TAG = "thirdparty_broadcast_tag";
	public static final String THIRDPARTY_CONTACTS_DATA = "thirdparty_contacts_data";
	public static final String THIRDPARTY_GROUP_ID = "thirdparty_group_id";
	public static final String THIRDPARTY_CHAIRMAN_ID = "thirdparty_chairman_id";
	//用户信息
	public static final String LOGIN_STATUS= "login_status";
	public static final String USERID = "userid";
	public static final String CALL_OUT_TIME= "call_out_time";
	public static final String ONLINE_PEOPLE_MESSGE= "online_people_messge";
	public static final String TIME_COUNT= "Time_Count";
	public static final String COUNT_OVER= "count_over";
	public static final String USERPWD = "userpwd";
	public static final String USERNAME = "username";
	public static final String VALIDCODE = "validcode";
	public static final String TOKENKEY = "tokenkey";
	public static final String VALIDTIME = "validtime";
	public static final String MEDIA_FBL = "media_fbl";//媒体设置/分辨率
	public static final String MEDIA_ZL = "media_zl";//媒体设置/帧率
	public static final String MEDIA_ML = "media_ml";//媒体设置/码率
	public static final String MEDIA_TX_ZQ = "media_tx_zq";//媒体设置/图像增强
	public static final String MEDIA_TZ_ZQ = "media_tz_zq";//媒体设置/图质增强
	public static final String MEDIA_VOICE_ADD = "media_voice_add";//媒体设置/音频音量大小
	public static final String MEDIA_VOICE_DENOISE = "media_voice_denoise";//媒体设置/音频是否降噪
	//sip信息
	public static final String SIP_SERVER_ID = "sip_server_id";
	public static final String SIP_SERVER_IP = "sip_server_ip";
	public static final String SIP_SERVER_PORT = "sip_server_port";
	public static final String SIP_ID = "sip_id";
	public static final String SIP_PWD = "sip_pwd";
	public static int v_SIP_SERVER_PORT = 5060;
	//采集sip信息
	public static final String ENCODE_ID = "encode_id";
	public static final String ENCODE_PWD = "encode_pwd";
	public static final String ENCODE_PORT = "encode_port";
	public static String v_CAPTURE_SIP_ID = null;

	public static final String CaptureWidth = "capture_width";
	public static final String CaptureHeight = "capture_height";
	public static final String CaptureFrameRate = "capture_framerate";
	public static final String CaptureBitRate = "capture_bitrate";
	public static final String CaptureCameraId = "capture_cameraid";
	public static int vCaptureWidth = 640;
	public static int vCaptureHeight = 480;
	public static int vCaptureFrameRate = 20;
	public static int vCaptureBitRate = 500000;
	public static int vCaptureCameraId = 1;
	
	//绑定编码器
	public static final String BIND_DEVICE_ID = "bind_device_id";
	public static final String BIND_DEVICE_NAME = "bind_device_name";
	//抓拍录像、目录
	public static final String VIDEO_DEFAULT_NAME = "video";
	public static final String PHOTO_DEFAULT_NAME = "photo";
	public static final String FILE_NAME_DIVIDER = "_";
	public static final String FILE_FORMAT_DATE = "yyyy-MM-dd-HH-mm-ss";
	public static final String VIDEO_NAME_EXTENSION = ".mkv";
	public static final String PHOTO_NAME_EXTENSION = ".jpg";
	public static final String PHOTO_TMP_BMP = ".bmp";
	public static final String CONTACTS_XML_NAME = "Contacts.xml";
	public static String UserVideoPath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK/Records";
	public static String UserPhotoPath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK/Pictures";
	public static String UserFilePath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK/Files/";
	public static String AppFilePath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK";
	public static String CrashFilePath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK/crash";
	public static String TempFilePath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/XTXK/tmpFile";
	public static String AuthcodePicture = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/XTXK/auth_code.png";

	public static int ScreenWidth = 0;
	public static int ScreenHeight = 0;
	public static int SdkVersion = 0;
	public static String DeviceModel = null;
	public static String DeviceBrand = null;
	public static String DeviceDisplay = null;
	public static String DeviceProduct = null;
	public static String SP_KEY_CallIn_Voice = "sp_CallinVoice";
	public static String SP_KEY_CallIn_Vibrate = "sp_CallinVibrate";
	public static String SP_KEY_MsgOppositeIp = "sp_oppositeIp";
	public static String SP_KEY_MsgOppositeId = "sp_oppositeId";
	public static String SP_KEY_MsgOppositeShowId = "sp_oppositeShowId";
	public static String SP_KEY_MsgOppositeName = "sp_oppositeName";
	
	//------------------http-------------------------------
	//////////////////////////////////////获取http请求的基础部分///////////////
	private static String getHttpBase(Context context){
		SharedPreferences sp = context.getSharedPreferences(ConstantsValues.DEFAULT_SP,
				Context.MODE_PRIVATE);
		//获取中心地址的ip和端口  拼接不需要端口
		String core_ip = sp.getString(CORE_IP, "");
		String httpBase="https://" + core_ip+"/ApiGatewayService";
		return httpBase;
	}

	public static String getHttpUrl(Context context,String methed){
		//将基础部分+后一半（不带参数）
		String httpBase = getHttpBase(context);
		String url=httpBase+methed;
		return url;
	}
	/////////////////////////////////////////WSS请求部分////////////////
	public static String getWssBase(Context context){
		SharedPreferences sp = context.getSharedPreferences(ConstantsValues.DEFAULT_SP,
				Context.MODE_PRIVATE);
		//获取中心地址的ip和端口  拼接需要端口
		String core_ip = sp.getString(CORE_IP, "");
		String core_port = sp.getString(CORE_PORT, "443");
		String base="wss://" + core_ip + ":" + core_port + "/websocket";
		return base;
	}

	// -----------------高级参数设置-------------------------------
	// 丢包重传接收端
	public static final String RECEIVE_PACKAGE_RESENT = "receive_package_resent";
	/**
	 * 接收端丢包重传
	 */
	public static final String RECEIVE_RESEND_KEY = "config.sink_cfg.resend";

	// 丢包重传发送端
	public static final String SEND_PACKAGE_RESENT = "send_package_resent";
	/**
	 * 发送端丢包重传
	 */
	public static final String SEND_RESEND_KEY = "config.caster_cfg.resend";

	// 传输超时(此参数不是在传输库中设置)
	public static final String FLOW_TIME_OUT = "flow_time_out";

	// mtu
	public static final String MTU = "mtu";
	/**
	 * mtu:组包最大值
	 */
	public static final String MTU_KEY = "config.caster_cfg.MaxSize";
	// mtu默认值1024
	public static final String DEFAULT_MTU_VALUE = "1024";

	// resend_au
	public static final String RESENDAU = "resend_au";
	/**
	 * ReSendAu
	 */
	public static final String RESENDAU_KEY = "config.caster_cfg.ReSendAu";
	// ReSendAu默认值5
	public static final String DEFAULT_RESENDAU_VALUE = "5";

	// wait_resend
	public static final String WAIT_RESEND = "wait_resend";
	/**
	 * wait_resend
	 */
	public static final String WAITRESEND_KEY = "config.sink_cfg.wait_resend";
	// wait_resend默认值10
	public static final String DEFAULT_WAITRESEND_VALUE = "10";

	// max_resend
	public static final String MAX_RESEND = "max_resend";
	/**
	 * max_resend
	 */
	public static final String MAXRESEND_KEY = "config.sink_cfg.max_resend";
	// max_resend默认值
	public static final String DEFAULT_MAXRESEND_VALUE = "10";

	// priority
	public static final String PRIORITY = "priority";
	/**
	 * priority
	 */
	public static final String RESEND_PRIORITY_KEY = "config.sink_cfg.priority";
	// priority默认值
	public static final String DEFAULT_PRIORITY_VALUE = "0";

	// priority_cache
	public static final String PRIORITY_CACHE = "priority_cache";
	/**
	 * priority_cache
	 */
	public static final String RESEND_PRIORITY_CACHE_KEY = "config.sink_cfg.priority_cache";
	// priority_cache默认值
	public static final String DEFAULT_PRIORITY_CACHE_VALUE = "3";

	// 帧缓冲大小
	public static final String FRAME_CACHE = "frame_cache";
	/**
	 * 帧缓冲大小
	 */
	public static final String FRAME_CACHE_KEY = "config.sink_cfg.frame_cache";
	// 默认帧缓冲大小25帧
	public static final String DEFAULT_FRAME_CACHE_VALUE = "25";

	// 包缓冲大小
	public static final String PACKAGE_CACHE = "package_cache";
	/**
	 * 包缓冲大小
	 */
	public static final String PKT_CACHE_KEY = "config.sink_cfg.pkt_cache";
	// 包缓冲大小默认值1M
	public static final String DEFAULT_PKT_CACHE_VALUE = "1024";

	public static final String SYNC = "sync";

	public static boolean SYNC_DEFAULT = true;
	public static final String SYNC_MODE = "sync_mode";
	public static final int SYNC_MODE_RTCP = 0;// 同步模式-rtcp同步
	public static final int SYNC_MODE_TIMESTAMP = 1;// 同步模式-时间戳
	public static final int SYNC_MODE_FRAME_TIME = 2;// 同步模式-帧到达时间
	public static final int SYNC_MODE_FRAME_RATE = 3;// 同步模式-帧率

	public static final String SYNC_CACHE_TIME = "sync_cache_time";
	public static final int DEFAULT_SYNC_CACHE_TIME = 300000;// 同步缓冲时间，单位微妙，默认值为0.3秒

	// ****************************************************************** //
	/**
	 * 丢包重传帧缓冲
	 */
	public static final String WAIT_FRAMES_KEY = "config.sink_cfg.wait_frames";
	// 丢包重传帧缓冲默认值
	public static final String DEFAULT_WAITfRAMES_VALUE = "25";

	public static final String PACKRESEND_KEY = "config.caster_cfg.packresend";
	public static final String DEFAULT_PACKRESEND_VALUE = "2";

	/**
	 * 码率控制(默认为关闭)
	 */
	public static final String BITRATE_CONTROLLER_KEY = "config.caster_cfg.bitrate_controller";
	public static final String DEFAULT_BITRATE_CONTROLLER = "0";

	// ******************************************************************

	public static void setRETRY_REGISTER(int rETRY_REGISTER) {
		RETRY_REGISTER = rETRY_REGISTER;
	}



}