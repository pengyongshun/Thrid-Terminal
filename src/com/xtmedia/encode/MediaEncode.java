package com.xtmedia.encode;

import android.util.Log;
import android.view.SurfaceView;

import com.xt.mediarecorder.AudioRecorder;
import com.xt.mediarecorder.MediaRecorderBase;
import com.xt.mediarecorder.MediaRecorderHard;

public class MediaEncode {

	private static MediaRecorderBase mMediaRecorder = null;
	protected static AudioRecorder mAudioRecorder = null;

	public static boolean IsEncodingStatus() {
		return (mMediaRecorder != null);
	}

	// 开始硬编
	public static boolean startEncodeSipEx(SurfaceView surfaceView, int width,
			int height, int camerafacing, int framerate, int bitrate) {

		boolean isVideo = true;
		boolean isAudio = true;

		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorderHard();
			if (isAudio) {
				// startStamp = RCAuidoEncodeNative.StartRecord();
				// RCAuidoEncodeNative.EnableEchoProcess(isEnableEchoProcess);
				if (mAudioRecorder == null) {
					mAudioRecorder = new AudioRecorder(mMediaRecorder);
					mAudioRecorder.init();
					mAudioRecorder.start();
				}
			}
			if (isVideo) {
				String cameraType = mMediaRecorder.getDeviceCamera();
				if (camerafacing == 1) {
					if (!cameraType.contains("front")
							&& cameraType.contains("back")) {
						camerafacing = 0;
					}
				} else if (camerafacing == 0) {
					if (!cameraType.contains("back")
							&& cameraType.contains("front")) {
						camerafacing = 1;
					}
				}
				Log.i("===send===", "===startEncodeSipEx======width:"
						+ width + "; height:" + height + "; camerafacing:"
						+ camerafacing + "; framerate:" + framerate
						+ "; bitrate:" + bitrate);
				long startStamp = System.currentTimeMillis();
				mMediaRecorder.initSurfaceView(surfaceView);
				mMediaRecorder.setCameraParameters(camerafacing, framerate,
						bitrate);
				mMediaRecorder.setVideoQuality(width, height);
				mMediaRecorder.startPreview();
				mMediaRecorder.setStartStamp(startStamp);
				mMediaRecorder.startRecordSip();
			}
		}
		return true;
	}

	/**
	 * 
	 * @param stopVideoEncode
	 *            是否停止视频解码
	 * @param stopAudioEncode
	 *            是否停止音频解码
	 */
	public static void stopEncodeSipEx(boolean stopVideoEncode,
			boolean stopAudioEncode) {
		if (mMediaRecorder != null) {
			if (stopVideoEncode) {
				// 如果不仅停音频，那么停止视频编码(只有没有绑定编码器，本地采集图像停止才是这种情况)
				mMediaRecorder.stopPreview();
				mMediaRecorder.stopRecord();

			}
			if (stopAudioEncode) {
				if (mAudioRecorder != null) {
					mAudioRecorder.stop();
					mAudioRecorder = null;
				}
				// RCAuidoEncodeNative.SetSendParam(0);
				// RCAuidoEncodeNative.SetSendParam2(0);
				// RCAuidoEncodeNative.StopRecord();
			}
			mMediaRecorder = null;
		}
	}

}