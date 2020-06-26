package com.xt.mediarecorder;

import android.view.SurfaceView;

public interface IMediaRecorder
{
	public void startRecord();

	public void stopRecord();

	public void startRecordSip();

	public void setDestination(String url, int videoPort, int audioPort);

	public void initSurfaceView(int Left, int Top, int Width, int Height,
								SurfaceView mSurfaceView);

	public void setVideoQuality(int width, int height);

	public String getAudioConfig();

	public void setCameraParameters(int cameraId, int frameRate, int bit);

	public void onAudioError(int what, String message);

	public void receiveAudioData(byte[] sampleBuffer, int len);
	
	public void setStartStamp(long startStamp);
}