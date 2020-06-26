package com.xt.mobile.terminal.network.wss;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.handshake.ServerHandshake;

import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseChairApplyMemberSpeak;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseChairApplyMemberSpeakBody;
import com.xt.mobile.terminal.network.sysim.IWebSocketClient;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToolLog;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class IWebSocketClientService extends Service {

	private static IWebSocketClientService wsClientService = null;
	public IWebSocketClient wsClient;
	private static String gURI = null;
	private int requestTag=-1;//请求标识

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		wsClientService = this;

		if (intent != null) {
			// 初始化WebSocketClient

			//wss的请求url
			gURI = intent.getStringExtra("WSS_SERVER_URL");
			initSocketClient(gURI);

			// 开启心跳检测
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		// 停止心跳
		mHandler.removeCallbacks(heartBeatRunnable);
		// 断开连接
		onDisconnect();
		super.onDestroy();
	}

	// ===单例模式
	public IWebSocketClientService() {

	}

	public static IWebSocketClientService getInstance() {
		IWebSocketClientService instance = wsClientService;
		if (instance == null) {
			load();
		}
		return wsClientService;
	}

	private static synchronized void load() {
		IWebSocketClientService instance = wsClientService;
		if (instance == null) {
			wsClientService = new IWebSocketClientService();
		}
	}

	// == 基本方法
	private void initSocketClient(String scUri) {

		URI uri = URI.create(scUri);
		wsClient = new IWebSocketClient(uri) {
			@Override
			public void onMessage(String message) {
				ToolLog.i("---websocket收到的消息：" + message);
				PLog.d("websocket","---websocket收到的消息："+ message);
				if (message.indexOf("\"uri\":476") >= 0) {
					onSend("{\"code\":732}");
					//  这个设置wss接收的标识的地方   只接受这些数据类型的
				} else if (message.indexOf(WssContant.WSS_INFORM_INIT_MEDIA) >= 0
						|| message.indexOf(WssContant.WSS_ORGANIZATION_USER) >= 0
						|| message.indexOf(WssContant.WSS_ORGANIZATION_DEVICE) >= 0
						|| message.indexOf(WssContant.WSS_MAIN_USERS_STATUS) >= 0
						|| message.indexOf(WssContant.WSS_MAIN_DEVICES_STATUS) >= 0
						|| message.indexOf(WssContant.WSS_INFORM_SHOW_MESSAGE) >= 0
						|| message.indexOf(WssContant.WSS_INFORM_SHOW_REMIND) >= 0
						|| message.indexOf(WssContant.WSS_INFORM_STARTMEDIA_BYLOCAL) >= 0
						|| message.indexOf(WssContant.WSS_JOIN_METTING) >= 0
						|| message.indexOf(WssContant.WSS_STOP_METTING) >= 0
						|| message.indexOf(WssContant.WSS_GROUP_START_COMMOND) >= 0
						|| message.indexOf(WssContant.WSS_GROUP_REFRESH_COMMOND) >= 0
						|| message.indexOf(WssContant.WSS_GROUP_STOP_COMMOND) >= 0
						|| message.indexOf(WssContant.WSS_MEETING_APPLY_SPEAK_SHOW) >= 0
						|| message.indexOf(WssContant.WSS_RECEVIE_COMMUNICATION_MESSAGE) >= 0
						|| message.indexOf(WssContant.WSS_USER_COMM_SESSION_PREVIEW) >= 0
						|| message.indexOf(WssContant.WSS_RECEVIE_COMMUNICATION_NOTIFICATION) >= 0
						|| message.indexOf(WssContant.WSS_MEETING_ZX_OUT_MEETING)>=0) {

					if (message.indexOf(WssContant.WSS_MEETING_APPLY_SPEAK_SHOW)>=0 &&
							message.indexOf("publishAnswerApplyJoinFromConference")>=0){
						// 在这里消化主席默认允许成员入会
						zxApplyJoinResult(message);
					}else {
						Intent receiveCallIntent = new Intent(BaseActivity.WEBSOCKET_MESSAGE);
						receiveCallIntent.putExtra("WS_MESSAGE", message);
						sendBroadcast(receiveCallIntent);
					}

				}
			}

			@Override
			public void onOpen(ServerHandshake handshakedata) {
				super.onOpen(handshakedata);
				ToolLog.i("---websocket连接成功");

				Intent receiveCallIntent = new Intent(BaseActivity.WEBSOCKET_CONNECT_SUCCESS);
				receiveCallIntent.putExtra("WS_CONNECT", "websocket_connected");
				sendBroadcast(receiveCallIntent);
			}
		};

		// wss模式，即加密模式
		if (gURI.contains("wss://")) {
			onWssMode();
		}

		onConnect();
	}

	/**
	 * 主席允许成员入会
	 * @param message
	 */
	private void zxApplyJoinResult(String message) {
		if (message !=null && message.length()>0){
			ParseChairApplyMemberSpeak bean = FastJsonTools.json2BeanObject(message,
					ParseChairApplyMemberSpeak.class);
			if (bean !=null){
				String body = bean.getBody();
				if (body !=null && body.length()>0){
					ParseChairApplyMemberSpeakBody object = FastJsonTools.json2BeanObject(body,
							ParseChairApplyMemberSpeakBody.class);
					if (object !=null){
						ParseChairApplyMemberSpeakBody.ParamsBeanX params = object.getParams();
						if (params !=null){
							List<ParseChairApplyMemberSpeakBody.ParamsBeanX.ButtonsBean> buttons = params.getButtons();
							if (buttons !=null && buttons.size()>0){
								ParseChairApplyMemberSpeakBody.ParamsBeanX.ButtonsBean buttonsBean = buttons.get(0);
								if (buttonsBean !=null){
									ParseChairApplyMemberSpeakBody.ParamsBeanX.ButtonsBean.CommandBean command = buttonsBean.getCommand();
									if (command !=null){
										ParseChairApplyMemberSpeakBody.ParamsBeanX.ButtonsBean.CommandBean.ParamsBean params1 = command.getParams();
										if (params1 !=null){
											String applyUserID = params1.getApplyUserID();
											String sceneID = params1.getSceneID();
											if (applyUserID !=null && applyUserID.length()>0
												&& sceneID !=null && sceneID.length()>0){
												zxApplyJoin(sceneID,applyUserID);
											}
										}
									}
								}
							}
						}
					}
				}
			}

		}

	}

	private void onWssMode() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory factory = sc.getSocketFactory();
			wsClient.setSocket(factory.createSocket());

			ToolLog.i("---onWssMode");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onSend(String msg) {
		if (wsClient != null && wsClient.isOpen()) {
			wsClient.send(msg);
			ToolLog.i("---onSend : " + msg);
		}
	}

	public void onConnect() {
		new Thread() {
			@Override
			public void run() {
				try {
					if (wsClient != null) {
						wsClient.connectBlocking();
						ToolLog.i("---onConnect");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void onDisconnect() {

		try {
			if (wsClient != null) {
				wsClient.close();
				ToolLog.i("---onDisconnect");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wsClient = null;
		}
	}

	public void onReconnect() {

		new Thread() {
			@Override
			public void run() {
				try {
					if (wsClient != null) {
						wsClient.reconnectBlocking();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ToolLog.i("---onReconnect");
			}
		}.start();
	}

	// -------------------------------------websocket心跳检测------------------------------------------------
	private static final long HEART_BEAT_RATE = 10 * 1000;// 每隔10秒进行一次对长连接的心跳检测
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {
		@Override
		public void run() {
			ToolLog.i("---心跳包检测websocket连接状态");
			if (wsClient != null) {
				if (wsClient.isClosed()) {
					onReconnect();
				}
			} else {
				// 如果client已为空，重新初始化连接
				wsClient = null;
				initSocketClient(gURI);
			}
			// 每隔一定的时间，对长连接进行一次心跳检测
			mHandler.removeCallbacks(heartBeatRunnable);
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	/**
	 * 主席允许成员进入会议（这个是在成员离开会议，又重新进入的时候）（需要验证）
	 * memberID  这个是主席接收到成员申请发言指令后服务返回给主席的数据里面有
	 * @param mettingID
	 * @param applyUserID 申请入会的成员的UserId  这个一般就是服务器返回给我的
	 */
	private void zxApplyJoin(String mettingID, String applyUserID){
		WebSocketCommand.getInstance().onWssAnswerApplyJoin(mettingID,applyUserID);
	}
}
