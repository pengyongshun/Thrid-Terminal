package com.xt.mobile.terminal.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.sip.SessionManager;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.XTMediaPlay;
import com.xt.mobile.terminal.view.XTMediaSource;
import com.xt.mobile.terminal.view.dailog.Dialog_TextView;

public class ActivityCalling extends BaseActivity {

	private View CallingBackground;
	private View AudioBackground;
	private View VideoBackground;

    private TextView mTvBgUser;
    private Chronometer mBgTimer;
    private ImageButton mIbBgHangup;
    private Chronometer mAudioTimer;
    private ImageButton mAudioHangup;
    private TextView mAudioUser;
    private SurfaceView mVideoSurface;
    private XTMediaPlay mXTMediaPlay;
    private ImageView mSwitchCamera;
    private TextView mTvVideoUser;
    private Chronometer mVideoTimer;
    private ImageButton mVideoHangup;
    private boolean mIsFrontCamera;

	private String mMediaType;
	private String mReceiverId;
	private String mReceiverName;
    private String mSessionId;
	private boolean mIsReceive;


	private int mDelayType = 0;
	private int mDelayTime = 30000;
    private int mExitTime = 600;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.removeCallbacksAndMessages(null);
			if (mDelayType == 0) {
                onExitDialog("呼叫超时，即将退出！");
                //onSetShowLayout();
			} else if (mDelayType == 1) {
                if (mMediaType != null && mMediaType.equals("VideoCall")) {
                    mVideoTimer.stop();
                } else if (mMediaType != null && mMediaType.equals("AudioRing")) {
                    mAudioTimer.stop();
                }
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_calling);
		// 关闭声音
		// int disableMedia = SipManager.localMedia.getDisableMedia();
		// int newDisableMedia = disableMedia | XTMediaSource.AUDIO_ENABLE;
		// SipManager.localMedia.setDisableMedia(newDisableMedia);
		if (initData() >= 0) {
			initView();
			
		} else {
			finish();
		}		
	}

	private int initData() {

        mIsFrontCamera = true;
        mSessionId = null;
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(mRunnable, mDelayTime);

		Intent intent = getIntent();
		mReceiverId = intent.getStringExtra("ReceiverId");
		mReceiverName = intent.getStringExtra("ReceiverName");
		mMediaType = intent.getStringExtra("MediaType");
		mIsReceive = intent.getBooleanExtra("IsReceive", false);

		ToolLog.i("===ActivityCalling::initData (mReceiverId[" + mReceiverId + "] mReceiverName["
				+ mReceiverName + "] mMediaType[" + mMediaType + "]  mIsReceive[" + mIsReceive
				+ "])");
        if (!mIsReceive) {

			if (mMediaType != null && mMediaType.equals("VideoCall")) {
				WebSocketCommand.getInstance().onWssStartVideoCall(mReceiverId, mReceiverName,
						false);
            } else if (mMediaType != null && mMediaType.equals("AudioCall")) {
                WebSocketCommand.getInstance().onWssStartVideoCall(mReceiverId, mReceiverName,
                        true);
			}
		}

		return 0;
	}

	private void initView() {
		CallingBackground = findViewById(R.id.rl_calling_background);
		AudioBackground = findViewById(R.id.rl_calling_audio);
		VideoBackground = findViewById(R.id.rl_calling_video);
        CallingBackground.setVisibility(View.INVISIBLE);
        VideoBackground.setVisibility(View.INVISIBLE);
        AudioBackground.setVisibility(View.INVISIBLE);
        if (!mIsReceive) {
            mTvBgUser = (TextView) findViewById(R.id.activity_calling_bg_user);

            mBgTimer = (Chronometer) findViewById(R.id.activity_calling_bg_sj);

            mIbBgHangup = (ImageButton) findViewById(R.id.activity_calling_bg_hangup);

            mIbBgHangup.setOnClickListener(this);
            mTvBgUser.setText(mReceiverName);
            setTimerStatus(mBgTimer, true);
        }
		if (mMediaType != null && mMediaType.equals("VideoCall")) {
            mXTMediaPlay = new XTMediaPlay();
            mVideoSurface = (SurfaceView) findViewById(R.id.activity_calling_video_surface);
            mXTMediaPlay.InitSurfaceView(mVideoSurface);

            mSwitchCamera = (ImageView) findViewById(R.id.activity_calling_switch_camera);
            mTvVideoUser = (TextView) findViewById(R.id.activity_calling_video_user);
            mVideoTimer = (Chronometer) findViewById(R.id.activity_calling_video_sj);
            mVideoHangup = (ImageButton) findViewById(R.id.activity_calling_video_hangup);

            mTvVideoUser.setText(mReceiverName);
            mVideoHangup.setOnClickListener(this);
            mSwitchCamera.setOnClickListener(this);
            if (!mIsReceive) {
                CallingBackground.setVisibility(View.VISIBLE);
            } else {
                VideoBackground.setVisibility(View.VISIBLE);
                setTimerStatus(mVideoTimer, true);
			}
		} else if (mMediaType != null && mMediaType.equals("AudioRing")) {
            mAudioTimer = (Chronometer) findViewById(R.id.activity_calling_audio_sj);
            mAudioHangup = (ImageButton) findViewById(R.id.activity_calling_audio_hangup);
            mAudioUser = (TextView) findViewById(R.id.activity_calling_audio_user_icon);

            mAudioHangup.setOnClickListener(this);
            mAudioUser.setText(mReceiverName);
            if (!mIsReceive) {
                CallingBackground.setVisibility(View.VISIBLE);
            } else {
			AudioBackground.setVisibility(View.VISIBLE);
                setTimerStatus(mAudioTimer, true);

			}
		}
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
        if (id == R.id.activity_calling_switch_camera) {

                mIsFrontCamera = !mIsFrontCamera;
                if (!mIsFrontCamera) {
                    Intent intent1 = new Intent(VideoService.ACTION_SWTICH_REAR_CAMERA);
                    sendBroadcast(intent1);
                    ToastUtil.showShort(ActivityCalling.this, "后置摄像头");
                } else {
                    Intent intent2 = new Intent(VideoService.ACTION_SWTICH_FRONT_CAMERA);
                    sendBroadcast(intent2);
                    ToastUtil.showShort(ActivityCalling.this, "前置摄像头");
                }
          } else if (id == R.id.activity_calling_bg_hangup) {
                onStopCall(mMediaType);
                mBgTimer.stop();
                finish();

		  }else if (id == R.id.activity_calling_video_hangup){
			ToolLog.i("===ActivityCalling::onClick (点击关闭呼叫)");
			onStopCall(mMediaType);

		  }else if (id == R.id.activity_calling_audio_hangup){
			ToolLog.i("===ActivityCalling::onClick (点击关闭对讲)");
			onStopCall(mMediaType);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ToolLog.i("===ActivityCalling::onKeyDown (按键关闭呼叫)");
			onStopCall(mMediaType);
		}
		return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mSessionId != null && mSessionId.length() > 0) {
			XTMediaSource source = SipManager.media_map.get(mSessionId);
			if (!source.isValidHandle()) {
				ToolLog.i("===ActivityCalling::onResume (重新创建接受端口)");
				source.initClientParams(Session.PLAY);
			}
            mXTMediaPlay.Play();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mSessionId != null && mSessionId.length() > 0) {
			mXTMediaPlay.stop();
        }
	}

	@Override
	protected void onReceivePlay(String sessionId) {

		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(mRunnable, mDelayTime);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {
			SessionManager.receivePlay(mReceiverId, true, true, sessionId);
			ToolLog.i("===ActivityCalling::onReceivePlay (收到呼叫)");
		} else {
			ToolLog.i("===ActivityCalling::onReceivePlay (sessionId match failed)");
		}
	}

	@Override
	protected void onPlaySuccess(String sessionId) {

		mHandler.removeCallbacksAndMessages(null);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {
			if (mMediaType != null && mMediaType.equals("VideoCall")) {
				if (SipManager.media_map != null && SipManager.media_map.size() > 0
						&& mReceiverId != null) {
					XTMediaSource source = SipManager.media_map.get(session.getSessionId());
					if (source != null) {
                        mXTMediaPlay.setMediaSource(source);
                        mXTMediaPlay.Play();
                        mSessionId = sessionId;
					}
				}
				ToolLog.i("===ActivityCalling::onPlaySuccess (呼叫成功)");
			} else if (mMediaType != null && mMediaType.equals("AudioRing")) {
					if (SipManager.media_map != null && SipManager.media_map.size() > 0
							&& mReceiverId != null) {
						XTMediaSource source = SipManager.media_map.get(session.getSessionId());
						if (source != null) {
                        mXTMediaPlay.setMediaSource(source);
                        mXTMediaPlay.Play();
                        mSessionId = sessionId;
					}
				}
				ToolLog.i("===ActivityCalling::onPlaySuccess (语音成功)");
			}
		} else {
			ToolLog.i("===ActivityCalling::onReceivePlay (sessionId match failed)");
		}
	}

	@Override
	protected void onPlayFail(String sessionId) {
		// TODO 是否需要发送上层业务通知？

		mHandler.removeCallbacksAndMessages(null);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null && session.getDestId().equals(mReceiverId)) {

			ToolLog.i("===ActivityCalling::onPlayFail (呼叫失败)");
            onClear(sessionId);

			mDelayType = 1;
            mHandler.postDelayed(mRunnable, mExitTime);
		} else {
			ToolLog.i("===ActivityCalling::onPlayFail (sessionId match failed)");
		}
	}

	@Override
	protected void onReceiveBye(String sessionId) {

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null && session.getDestId().equals(mReceiverId)) {

            onClear(sessionId);

			mDelayType = 1;
            mHandler.postDelayed(mRunnable, mExitTime);
			ToolLog.i("===ActivityCalling::onReceiveBye (呼叫bye)");
		} else {
			ToolLog.i("===ActivityPlaying::onReceiveBye (sessionId match failed)");
		}
	}

    private void onClear(String sessionId) {

		if (SipManager.media_map != null && SipManager.media_map.size() > 0 && mReceiverId != null) {
			XTMediaSource source = SipManager.media_map.remove(sessionId);
			if (source != null) {
                mXTMediaPlay.stop();
				if (!isBackground) {
					source.releaseClientPorts();
					source.clear();
				}
			}
		}
        SessionManager.clearSessionMap();
        ToolLog.i("===ActivityCalling::onClear (结束)");
	}

	private void onStopCall(String mediaType) {

		mHandler.removeCallbacksAndMessages(null);

		ToolLog.i("===ActivityCalling::onStopCall start");
		WebSocketCommand.getInstance().onWssStopVideoCall(mReceiverId, mReceiverName);
	}

    private void onSetShowLayout() {
		CallingBackground.setVisibility(View.INVISIBLE);
        mBgTimer.stop();
        if (mMediaType != null && mMediaType.equals("VideoCall")) {
            VideoBackground.setVisibility(View.VISIBLE);
            setTimerStatus(mVideoTimer, true);
        } else if (mMediaType != null && mMediaType.equals("AudioRing")) {
            AudioBackground.setVisibility(View.VISIBLE);
            setTimerStatus(mAudioTimer, true);
	}
    }

    private void setTimerStatus(Chronometer timer, boolean isStart) {

        if (timer != null) {
            if (isStart) {
                int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                timer.setFormat("0" + String.valueOf(hour) + ":%s");
		timer.setBase(SystemClock.elapsedRealtime());
		timer.start();




		} else {
                timer.stop();
			// if (mediaType == Session.CALL_VIDEO) {
			// Session session = SessionManager
			// .findSession(Session.CALL_VIDEO);
			// if (session != null
			// && session.getSessionState() == Session.SESSION_STATE_COMPLETE) {
			// // 会话完成就表示具备播放条件
			// XTMediaSource soure = SipManager.id_source.get(session
			// .getId());
			// if (!soure.isValidHandle()) {
			// soure.initClientParams(mediaType);
			// }
			// mXTremoteMediaPlay.Play();
			// if (showMyPreview) {
			// mXTlocalMediaPlay.Play();
			// SipManager.localSource.setClient_handle(0);
			// }
			// }
			// }
		}
	}

	}

	protected void onReceiveWssMessage(String msg) {

		ToolLog.i("===ActivityPlaying::onReceiveWssMessage : " + msg);

		// 严格来说，应该是onPlaySuccess和onReceiveWssMessage共同完成视频的显示
		// 但是移动端只需要显示一个分屏，所以这里可以简化不处理

        if (msg.indexOf(WssContant.WSS_INFORM_STARTMEDIA_BYLOCAL) >= 0 && !mIsReceive) {
            onSetShowLayout();
        } else if (msg.indexOf(WssContant.WSS_CLOSE_TIME) >= 0) {

            ToolLog.i("==========onReceiveWssMessage closeTime");
			try {
				JSONObject obj = new JSONObject(msg);
				JSONObject exObj = obj.getJSONObject("extend");
				String userId = exObj.getString("userID");
				if (userId != null && userId.equals(mUserId)) {

					String bodyStr = obj.getString("body");
					JSONObject bodyObj = new JSONObject(bodyStr);

					JSONObject paramsObj = bodyObj.getJSONObject("params");
                    String closeTime = paramsObj.getString("closeTime");
                    if (!closeTime.isEmpty() && closeTime.equals("5000")) {
                        String text = paramsObj.getString("text");
                        onExitDialog(text);
                    }
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    private void onExitDialog(String content) {
        Dialog_TextView dialog = new Dialog_TextView(this, content, null, "确定", new Dialog_TextView.DialogTextViewCall() {
            @Override
            public void setResult(String result) {
                onStopCall(mMediaType);
                mBgTimer.stop();
                finish();
		}
        });
        dialog.show();
	}
}