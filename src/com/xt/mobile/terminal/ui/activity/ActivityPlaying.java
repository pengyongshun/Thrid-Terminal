package com.xt.mobile.terminal.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.sip.SessionManager;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.XTMediaPlay;
import com.xt.mobile.terminal.view.XTMediaSource;
import com.xtmedia.xtview.GlRenderNative;

import java.lang.ref.WeakReference;
public class ActivityPlaying extends BaseActivity {
	private ImageView iv_photo;
	private ImageView iv_record;
    private TextView tv_close;
    private TextView tv_DeviceName;
    private TextView tv_department;

	private SurfaceView playWindow;
	private XTMediaPlay mXTMediaPlay;
    private LinearLayout mllPtzContext;
    private LinearLayout mllPtzPan;
    private TextView mTvOpenVoice;

    private View rl_wait;
	private TextView tv_pb_text;

    private boolean isPlaySuccess = false;
    private boolean panShow = false;
    private boolean isOpenVoice = false;
	private boolean isSync;
	private int syncMode;
	private int syncCacheTime;

	private SipInfo mTargetSipInfo;
	private String mTargetUserId;
	private String mTargetUserName;
    private String mParentName;
    private String mSessionId;
    private boolean startRecord = false;

    private static final int REQUEST_START_FAILURE = 0;
    private static final int RECEIVE_PLAY_FAILURE = 1;
    private static final int WAIT_RELEASE = 2;
    private static final int HIDE_PTZ_CONTROL = 3;
    private static final int HIDE_PTZ_PAN = 4;
    private static final int HidePTZDelayTime = 5000;
    private int mFailureDelayTime = 30000;
    private int mExitTime = 600;
    private PlayHandler mPlayHandler;
    private GestureDetector mGestureDetector;
    private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener(){
		@Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!panShow) { //不显示云台控制按钮时,才显示底部的云台控制区域
                showPTZControl();
            }else { //当显示云台控制按钮时则移除延迟隐藏云台控制按钮的消息,并立即隐藏云台控制按钮
                mPlayHandler.removeMessages(HIDE_PTZ_PAN);
                hidePTZPan();
			}
            return true;
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_playing);
        mPlayHandler = new PlayHandler(this);
		mTargetSipInfo = (SipInfo) getIntent().getSerializableExtra("DeviceSipInfo");
		if (mTargetSipInfo == null) {
			ToolLog.i("===ActivityPlaying::onCreate (mTargetSipInfo == null)");
			Toast.makeText(this, "获取点播信息失败！", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			ToolLog.i("===ActivityPlaying::onCreate userId:[" + mTargetSipInfo.getUserid()
					+ "] userName:[" + mTargetSipInfo.getUsername() + "]");
			mTargetUserId = mTargetSipInfo.getUserid();
			mTargetUserName = mTargetSipInfo.getUsername();
            SipInfo tmpInfo =  ConfigureParse.getDeviceInfoById(mTargetSipInfo.getBelongsys());
            if (tmpInfo != null) {
                mParentName = tmpInfo.getUsername();
            }
			mSessionId = null;
			initData();
			initView();
            mGestureDetector = new GestureDetector(this, mGestureListener);
		}
	}

	private void initView() {
		mXTMediaPlay = new XTMediaPlay();
		playWindow = (SurfaceView) findViewById(R.id.play_window);
		mXTMediaPlay.InitSurfaceView(playWindow);
		rl_wait = findViewById(R.id.rl_wait);
		tv_pb_text = (TextView) findViewById(R.id.tv_pb_text);
		rl_wait.setVisibility(View.VISIBLE);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_record = (ImageView) findViewById(R.id.iv_record);
        tv_close = (TextView) findViewById(R.id.tv_close);
        tv_DeviceName = (TextView) findViewById(R.id.tv_DeviceName);
        tv_department = (TextView) findViewById(R.id.tv_department);
        tv_DeviceName.setText(mTargetUserName);
        tv_department.setText(mParentName);
        mllPtzContext = findViewById(R.id.ll_ptz_content);
        mllPtzPan = findViewById(R.id.ll_ptz_pan);
        mTvOpenVoice = findViewById(R.id.tv_ptz_open_voice);
        mTvOpenVoice.setOnClickListener(this);
        findViewById(R.id.tv_ptz_control).setOnClickListener(this);
        findViewById(R.id.ptz_view).setOnClickListener(this);
		iv_photo.setOnClickListener(this);
		iv_record.setOnClickListener(this);
		tv_close.setOnClickListener(this);
	}

	private void initData() {

		if (mTargetSipInfo.getType() != SipInfo.TYPE_MOBILE) {
			isSync = sp.getBoolean(ConstantsValues.SYNC, ConstantsValues.SYNC_DEFAULT);
			if (isSync) {
				syncMode = sp
						.getInt(ConstantsValues.SYNC_MODE, ConstantsValues.SYNC_MODE_TIMESTAMP);
				syncCacheTime = sp.getInt(ConstantsValues.SYNC_CACHE_TIME,
						ConstantsValues.DEFAULT_SYNC_CACHE_TIME);
			}
			GlRenderNative.setSyncInfo(isSync, syncMode, syncCacheTime);
		}
		WebSocketCommand.getInstance().onSendRequestStartPlay(mTargetUserId, mTargetUserName);
        mPlayHandler.sendEmptyMessageDelayed(REQUEST_START_FAILURE, mFailureDelayTime);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isPlaySuccess) mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    private void showPTZControl(){
        if(mllPtzContext.getVisibility() == View.VISIBLE){
            mPlayHandler.removeMessages(HIDE_PTZ_CONTROL);
            mllPtzContext.setVisibility(View.GONE);
        }else {
            mllPtzContext.setVisibility(View.VISIBLE);
            mPlayHandler.sendEmptyMessageDelayed(HIDE_PTZ_CONTROL, HidePTZDelayTime);
        }
    }
    private void hidePTZControl(){
        mllPtzContext.setVisibility(View.GONE);
    }
    private void hidePTZPan(){
        mllPtzPan.setVisibility(View.GONE);
        panShow = false;
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick() || mSessionId == null) {
			return;
		}
		int id = v.getId();
		XTMediaSource source = SipManager.media_map.get(mSessionId);
		if (v.getId() == R.id.iv_photo) {
			if (0 != XTUtils.onVideoEnable(ActivityPlaying.this, false)) {
				return;
			}
			if (source != null) {
				Toast.makeText(ActivityPlaying.this, "抓拍成功", Toast.LENGTH_SHORT).show();
				XTUtils.takePhoto(ActivityPlaying.this, source.getTaskIndex(), mTargetUserName);
			}
		}else if (v.getId() == R.id.iv_record) {
			startRecord = !startRecord;
			if (startRecord) {
				// 开始录像
				if (0 == XTUtils.onVideoEnable(ActivityPlaying.this, true)) {
					iv_record.setImageResource(R.drawable.bt_record_closed_drawable);
					boolean record = XTUtils.record(true, source.getTaskIndex(), mTargetUserName);
					if (record) {
						Toast.makeText(ActivityPlaying.this, "录像开始", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ActivityPlaying.this, "录像失败", Toast.LENGTH_SHORT).show();
						startRecord = false;
						iv_record.setImageResource(R.drawable.bt_record_open_drawable);
					}
				} else {
					startRecord = false;
				}
			} else {
				// 停止录像
				iv_record.setImageResource(R.drawable.bt_record_open_drawable);
				boolean record = XTUtils.record(false, source.getTaskIndex(), mTargetUserName);
				if (record) {
					Toast.makeText(ActivityPlaying.this, "录像结束", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ActivityPlaying.this, "录像失败", Toast.LENGTH_SHORT).show();
				}
			}
		}else if (v.getId() == R.id.tv_close) {
			ToolLog.i("===ActivityPlaying::onClick (点击关闭点播)");
                mPlayHandler.removeMessages(HIDE_PTZ_CONTROL);
                isPlaySuccess = false;
                mllPtzContext.setVisibility(View.GONE);
			onStopPlay();
		}else if (v.getId() == R.id.tv_ptz_open_voice) {
           ptzOpenVoice();
		}else if (v.getId() == R.id.tv_ptz_control) {
           showPtzControlPan();
		}else if (v.getId() == R.id.ptz_view) {
           onPtzControl();
		}

	}
	private void delayHidePtzPan() {
        ToolLog.i("===ActivityPlaying::delayHidePtzPan");
        mPlayHandler.removeMessages(HIDE_PTZ_CONTROL);
        mPlayHandler.sendEmptyMessageDelayed(HIDE_PTZ_PAN, HidePTZDelayTime);
    }
    private void onPtzControl() {
        ToolLog.i("===ActivityPlaying::onClick PTZ clickEd");
    }
    private void showPtzControlPan(){
        ToolLog.i("===ActivityPlaying:: LG showPtzControlPan");
        mPlayHandler.removeMessages(HIDE_PTZ_CONTROL);
        mllPtzContext.setVisibility(View.GONE);
        mllPtzPan.setVisibility(View.VISIBLE);
        panShow = true;
        mPlayHandler.sendEmptyMessageDelayed(HIDE_PTZ_PAN, HidePTZDelayTime);
    }
    private void ptzOpenVoice() {
        mPlayHandler.removeMessages(HIDE_PTZ_CONTROL);
        mPlayHandler.sendEmptyMessageDelayed(HIDE_PTZ_CONTROL, HidePTZDelayTime);
        isOpenVoice = !isOpenVoice;
        int drawTopResId = isOpenVoice ? R.drawable.selector_ptz_close_voice : R.drawable.selector_ptz_open_voice;
        Drawable top = getResources().getDrawable(drawTopResId);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        mTvOpenVoice.setCompoundDrawables(null, top, null, null); //设置左图标
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (XTUtils.fastClick()) {
                return true;
            }
			ToolLog.i("===ActivityPlaying::onKeyDown (按键关闭点播)");
			onStopPlay();
            return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
    protected void onResume() {
        super.onResume();
        if (mSessionId != null && mSessionId.length() > 0) {
            XTMediaSource source = SipManager.media_map.get(mSessionId);
            if (!source.isValidHandle()) {
                ToolLog.i("===ActivityPlaying::onResume (重新创建接受端口)");
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
            XTMediaSource source = SipManager.media_map.get(mSessionId);
            if (source != null) {
                if (startRecord) {
                    startRecord = false;
                    XTUtils.record(false, source.getTaskIndex(), mTargetUserName);
                }
            }
        }
    }
    @Override
	protected void onReceivePlay(String sessionId) {

        mPlayHandler.removeMessages(REQUEST_START_FAILURE);
        mPlayHandler.sendEmptyMessageDelayed(RECEIVE_PLAY_FAILURE, mFailureDelayTime);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {
			SessionManager.receivePlay(mTargetUserId, true, true, sessionId);
			ToolLog.i("===ActivityPlaying::onReceivePlay (收到点播)");
		} else {
			ToolLog.i("===ActivityPlaying::onReceivePlay (sessionId match failed)");
		}
	}

	@Override
	protected void onPlaySuccess(String sessionId) {

        mPlayHandler.removeMessages(RECEIVE_PLAY_FAILURE);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null && session.getDestId().equals(mTargetUserId)) {
			XTMediaSource source = SipManager.media_map.get(session.getSessionId());
			if (source != null) {
				mXTMediaPlay.setMediaSource(source);
				if (isBackground) {
					source.releaseClientPorts();
				} else {
					tv_pb_text.setText(resources.getString(R.string.playing_load_waitting));
					mXTMediaPlay.Play();
				}
				mSessionId = sessionId;
			}
			ToolLog.i("===ActivityPlaying::onPlaySuccess (点播成功)");
		} else {
			ToolLog.i("===ActivityPlaying::onPlaySuccess (sessionId match failed)");
		}
	}

	@Override
	protected void onPlayFail(String sessionId) {

        mPlayHandler.removeMessages(RECEIVE_PLAY_FAILURE);

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null && session.getDestId().equals(mTargetUserId)) {

			ToolLog.i("===ActivityPlaying::onPlayFail (点播失败)");

			onClear(sessionId);
            mPlayHandler.sendEmptyMessageDelayed(WAIT_RELEASE, mExitTime);
		} else {
			ToolLog.i("===ActivityPlaying::onPlayFail (sessionId match failed)");
		}
	}

	protected void onReceiveBye(String sessionId) {

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null && session.getDestId().equals(mTargetUserId)) {

			onClear(sessionId);
            mPlayHandler.sendEmptyMessageDelayed(WAIT_RELEASE, mExitTime);
			ToolLog.i("===ActivityPlaying::onReceiveBye (收到停止点播)");
		} else {
			ToolLog.i("===ActivityPlaying::onReceiveBye (sessionId match failed)");
		}
	}

	public void loadSuccess() {
		rl_wait.setVisibility(View.INVISIBLE);
        isPlaySuccess = true;
        showPTZControl();
	}

	private void onClear(String sessionId) {

		if (rl_wait != null) {
			rl_wait.setVisibility(View.INVISIBLE);
		}

		XTMediaSource source = SipManager.media_map.remove(sessionId);
		if (source != null) {
			if (startRecord) {
				startRecord = false;
                XTUtils.record(false, source.getTaskIndex(), mTargetUserName);
			}
			mXTMediaPlay.stop();
			if (!isBackground) {
				source.releaseClientPorts();
				source.clear();
			}
		}
		SessionManager.clearSessionMap();
	}

	private void onStopPlay() {


		ToolLog.i("===ActivityPlaying::onStopPlay (发送WSS点播指令)");
		WebSocketCommand.getInstance().onSendRequestStopPlay(mTargetUserId, mTargetUserName);
	}

	protected void onReceiveWssMessage(String msg) {

		ToolLog.i("===ActivityPlaying::onReceiveWssMessage : " + msg);

		// 严格来说，应该是onPlaySuccess和onReceiveWssMessage共同完成视频的显示
		// 但是移动端只需要显示一个分屏，所以这里可以简化不处理
    }
		// if (msg.indexOf("MainDevicesStatus") >= 0) {
		//
    private static class PlayHandler extends Handler{
        WeakReference<ActivityPlaying> actPlayingWeakReference;
		// JSONObject exObj = obj.getJSONObject("extend");
		// String userId = exObj.getString("userID");
		// if (userId != null && userId.equals(mUserId)) {
		//
        public PlayHandler(ActivityPlaying activity) {
            actPlayingWeakReference = new WeakReference<ActivityPlaying>(activity);
        }
		// JSONObject paramsObj = bodyObj.getJSONObject("params");
		// JSONArray resourcesObj = paramsObj.getJSONArray("resources");
		// for (int i = 0; i < resourcesObj.length(); i++) {
		// JSONObject tmpObj = resourcesObj.getJSONObject(i);
		// String resourceID = tmpObj.getString("resourceID");
		// String busStatus = tmpObj.getString("busStatus");
		//
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ActivityPlaying ap = actPlayingWeakReference.get();
            if (ap != null && !ap.isFinishing()) {
                switch (msg.what) {
                    case REQUEST_START_FAILURE: //请求启动播放30S超时退出
                    case RECEIVE_PLAY_FAILURE://启动播放30S超时退出
                        ap.onStopPlay();
                        break;
                    case WAIT_RELEASE:
                        ap.finish();
                        break;
                    case HIDE_PTZ_CONTROL:
                        ap.hidePTZControl();
                        break;
                    case HIDE_PTZ_PAN:
                        ap.hidePTZPan();
                        break;
                }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
            }
        }
	}
}