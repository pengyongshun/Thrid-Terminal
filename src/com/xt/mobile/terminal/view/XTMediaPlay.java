package com.xt.mobile.terminal.view;

import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.sip.SdpMessage;
import com.xt.mobile.terminal.util.ToolLog;
import com.xtmedia.decode.MediaCodecWrapper;
import com.xtmedia.xtview.GlRenderNative;

/**
 * <pre>
 * 第一步：初始化InitSurfaceView对象(如为纯音频无需此操作)
 * 第二步：设置setMediaSource媒体来
 * 第三步：调用Play()/stop()方法
 * </pre>
 * 
 * @author XT
 * 
 */
public class XTMediaPlay {

	SurfaceView mSurfaceView;

	public XTMediaSource mediaSource;
	public MediaCodecWrapper decodec;
	long lNativeWindowObj = 0;
	private boolean isPlaying = false;
	private int mPlayCnt = 0;

	/**
	 * 初始化显示窗体 如果是纯音频播放，则无需调用该函数
	 * 
	 * @param
	 */
	public void InitSurfaceView(SurfaceView sv) {
		mSurfaceView = sv;

		SurfaceHolder holder = mSurfaceView.getHolder();

		holder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				lNativeWindowObj = GlRenderNative.SetVideoSurface(holder.getSurface());
				// MediaCodecWrapper.SetSurface(holder.getSurface());
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

			}
		});
	}

	public void setMediaSource(XTMediaSource mediaSource) {
		this.mediaSource = mediaSource;
		isPlaying = false;
		mPlayCnt = 0;
	}

	public XTMediaSource getMediaSource() {
		return mediaSource;
	}

	/**
	 * 播放源
	 * 
	 * @param playSource
	 */
	public boolean Play() {
		if (mediaSource == null || mediaSource.getSdp() == null) {
			ToolLog.i("==========[mediaSource == null]");
		}
		mSurfaceView.setBackground(null);
		int taskIndex = -1;
		// 设置视频源
		switch (mediaSource.getSessionType()) {
		case XTMediaSource.SESSION_TYPE_RTSP: {
			String url = "rtsp://" + mediaSource.getIp() + ":8557/0";
			taskIndex = GlRenderNative.opensingles(url, 19900, (int) mediaSource.getInChannel(),
					mSurfaceView.getHolder().getSurface(), true);
		}
			break;
		case XTMediaSource.SESSION_TYPE_SIP: {
			String sdp = mediaSource.getSdp();
			SdpMessage message = SdpMessage.parseSdp(sdp);
			message.sort();
			taskIndex = GlRenderNative.OpenSipSdps(message.createSdp(),
					mediaSource.GetClientHandle(), mSurfaceView.getHolder().getSurface(), true, true);
		}
			break;
		case XTMediaSource.SESSION_TYPE_SIP_AND_TRANSMIT: {
			taskIndex = GlRenderNative.mediaLocalPlays(mediaSource.GetClientHandle(), mSurfaceView
					.getHolder().getSurface(), true);
		}
			break;
		}
		ToolLog.i("==========play type[" + mediaSource.getSessionType() + "] ;taskIndex[" + taskIndex
				+ "]");
		mediaSource.setTaskIndex(taskIndex);
		int disableMedia = mediaSource.getDisableMedia();
		if ((disableMedia & XTMediaSource.VIDEO_ENABLE) == XTMediaSource.VIDEO_ENABLE) {
			enableStream(XTMediaSource.VIDEO_ENABLE, false);
		}
		if ((disableMedia & XTMediaSource.AUDIO_ENABLE) == XTMediaSource.AUDIO_ENABLE) {
			enableStream(XTMediaSource.AUDIO_ENABLE, false);
		}
		if (taskIndex >= 0) {
			isPlaying = true;
			mPlayCnt ++;
		}
		return (taskIndex >= 0);
	}

	/**
	 * 停止播放源
	 * 
	 * @param playSource
	 * @return
	 */
	public boolean stop() {
		if (mediaSource == null) {
			return false;
		}
		if (mediaSource.isValidHandle() && mediaSource.isValidTaskIndex()) {
			// 设置视频源
			switch (mediaSource.getSessionType()) {
			case XTMediaSource.SESSION_TYPE_RTSP: {
				// GlRenderNative.closesingle(mediaSource.getTaskIndex());
				GlRenderNative.stoprtspplay(mediaSource.getTaskIndex());
			}
				break;
			case XTMediaSource.SESSION_TYPE_SIP: {
				GlRenderNative.closesingle(mediaSource.getTaskIndex(), mediaSource.GetClientHandle());
			}
				break;
			case XTMediaSource.SESSION_TYPE_SIP_AND_TRANSMIT: {
				GlRenderNative.mediaLocalStop(mediaSource.getTaskIndex(),
						mediaSource.GetClientHandle());
			}
				break;
			}
			mSurfaceView.setBackgroundResource(R.drawable.playwindow_background);
			mediaSource.clear();
			isPlaying = false;
			mPlayCnt = 0;
			return true;
		} else {
			// 如果播放失败这里什么都不敢在外面释放端口
			// mediaSource.clear();
			return false;
		}
	}

	public boolean onlyPlayNotCreateTransmit() {
		if (mediaSource == null || mediaSource.getSdp() == null) {
			ToolLog.i("==========[mediaSource == null]");
		}
		mSurfaceView.setBackground(null);
		int taskIndex = -1;
		if (mediaSource.getSessionType() == XTMediaSource.SESSION_TYPE_SIP) {
			String sdp = mediaSource.getSdp();
			SdpMessage message = SdpMessage.parseSdp(sdp);
			message.sort();
			taskIndex = GlRenderNative.OpenSipSdps(message.createSdp(),
					mediaSource.GetClientHandle(), mSurfaceView.getHolder().getSurface(), true, false);
		}
		ToolLog.i("==========onlyPlayNotCreateTransmit type[" + mediaSource.getSessionType() + "] ;taskIndex[" + taskIndex
				+ "]");
		mediaSource.setTaskIndex(taskIndex);
		int disableMedia = mediaSource.getDisableMedia();
		if ((disableMedia & XTMediaSource.VIDEO_ENABLE) == XTMediaSource.VIDEO_ENABLE) {
			enableStream(XTMediaSource.VIDEO_ENABLE, false);
		}
		if ((disableMedia & XTMediaSource.AUDIO_ENABLE) == XTMediaSource.AUDIO_ENABLE) {
			enableStream(XTMediaSource.AUDIO_ENABLE, false);
		}
		if (taskIndex >= 0) {
			isPlaying = true;
			mPlayCnt ++;
		}
		return (taskIndex >= 0);
	}
	public boolean onlyStopPlaySaveTransmit() {
		if (mediaSource == null) {
			return false;
		}
		if (mediaSource.isValidHandle() && mediaSource.isValidTaskIndex()) {
			if (mediaSource.getSessionType() == XTMediaSource.SESSION_TYPE_SIP) {
				GlRenderNative.stopPlaySaveLink(mediaSource.getTaskIndex(), mediaSource.GetClientHandle());
			}
			return true;
		} else {
			return false;
		}
	}
	public boolean isPlaying() {
		return isPlaying;
	}
	public int getPlayCnt() {
		return mPlayCnt;
	}

	public void enableStream(int type, boolean isEnable) {
		if (mediaSource.getTaskIndex() >= 0) {
			ToolLog.i("======stream type[" + type + "] ; enable[" + type + "]");
			GlRenderNative.EnableStream(mediaSource.getTaskIndex(), type, isEnable);
		}
	}
}