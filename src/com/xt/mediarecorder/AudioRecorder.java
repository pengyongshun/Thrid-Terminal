package com.xt.mediarecorder;

import java.util.UUID;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.audiofx.AudioEffect;
import android.util.Log;

public class AudioRecorder
{
	private AudioRecord mAudioRecord = null;
	// 2015-06-18 rate=44100
	private int mSampleRate = 8000;
	private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	private int mChannel = 1;
	private IMediaRecorder mMediaRecorder;
	private int mMinBufferSize;
	private int mSampleBufferSize;
	private static final int BUFFERS_PER_SECOND = 100;
	public static final UUID EFFECT_TYPE_NULL = UUID
			.fromString("ec7178ec-e5e1-4432-a3f4-4657e6795210");
	AudioEffect m_AudioEffect;

	private Thread aworker;

	private static final String TAG = "AudioRecorder";

	public AudioRecorder(IMediaRecorder mediaRecorder)
	{
		this.mMediaRecorder = mediaRecorder;
	}

	public void setSampleRate(int sampleRate)
	{
		this.mSampleRate = sampleRate;
	}

	public void init()
	{
		if (mSampleRate != 8000 && mSampleRate != 16000 && mSampleRate != 22050
				&& mSampleRate != 44100)
		{
			mMediaRecorder
					.onAudioError(
							MediaRecorderBase.AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT,
							"sampleRate not support.");
			return;
		}
		mMinBufferSize = AudioRecord.getMinBufferSize(mSampleRate,
				channelConfig, AudioFormat.ENCODING_PCM_16BIT);
		mSampleBufferSize =  mChannel * AudioFormat.ENCODING_PCM_16BIT  * (mSampleRate / BUFFERS_PER_SECOND);
		mMinBufferSize = Math.max(2 * mMinBufferSize, mSampleBufferSize);
		if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize)
		{
			mMediaRecorder
					.onAudioError(
							MediaRecorderBase.AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT,
							"parameters are not supported by the hardware.");
			return;
		}
		if (mAudioRecord != null)
		{
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
		}
		mAudioRecord = new AudioRecord(
				android.media.MediaRecorder.AudioSource.VOICE_COMMUNICATION,
				mSampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT,
				mMinBufferSize);

		if (null == mAudioRecord)
		{
			mMediaRecorder.onAudioError(
					MediaRecorderBase.AUDIO_RECORD_ERROR_CREATE_FAILED,
					"new AudioRecord failed.");
			return;
		}
	}

	public void start()
	{
		try
		{
			mAudioRecord.startRecording();
			// start audio worker thread.
			aworker = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					fetchAudioFromDevice();
				}
			});
			aworker.start();

		}
		catch (IllegalStateException e)
		{
			mMediaRecorder.onAudioError(
					MediaRecorderBase.AUDIO_RECORD_ERROR_UNKNOWN,
					"startRecording failed.");
			return;
		}
	}

	public void fetchAudioFromDevice()
	{
		byte[] sampleBuffer = new byte[mSampleBufferSize];
		short[] shortBuf = new short[mSampleBufferSize];
//		long readTime = System.currentTimeMillis();
//		long stamp = 0;//System.currentTimeMillis();;
//		long stampEx = 0;
		try
		{
			while (!Thread.currentThread().isInterrupted())
			{
				//Log.e(TAG, "mAudioRecord read start " +  mSampleBufferSize + " " + (startTime - endTime));
				//int result = mAudioRecord.read(sampleBuffer,0, mSampleBufferSize);
				int result = mAudioRecord.read(shortBuf,0, mSampleBufferSize);
//				if(stamp == 0)
//				{
//					stamp = System.currentTimeMillis();
//				}
//				readTime = System.currentTimeMillis();
				if (result > 0)
				{
//					stampEx += result;
//					Log.e(TAG, "mAudioRecord read  stampEx " + stampEx + " result " + result
//							+ " stamp[" + (readTime - stamp) + "] clock[" + (1000 * stampEx / 2 / 8000) + "]");
					
					G711Code.G711aEncoder(shortBuf, sampleBuffer, result);
					mMediaRecorder.receiveAudioData(sampleBuffer, result);
				}
				//Log.e(TAG, "mAudioRecord read end " + ( endTime - readTime));
			}
		}
		catch (Exception e)
		{
			String message = "";
			if (e != null)
				message = e.getMessage();
			mMediaRecorder.onAudioError(
					MediaRecorderBase.AUDIO_RECORD_ERROR_UNKNOWN, message);
		}
	}

	public void stop()
	{
		if (aworker != null)
		{
			Log.e(TAG, "stop audio worker thread");
			aworker.interrupt();
			try
			{
				aworker.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			aworker = null;
		}
		mAudioRecord.stop();
		mAudioRecord.release();
		mAudioRecord = null;
	}
}