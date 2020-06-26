package com.xt.mediarecorder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.log.PLog;
import com.xtmedia.encode.SendMediaData;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MediaRecorderBase implements Callback, PreviewCallback, IMediaRecorder {
	// 默认旋转角度(编码端的预览窗口的角度，取决于摄像头id和硬件)
	public final static int ORITATION_270 = 270;
	public final static int ORITATION_180 = 180;
	public final static int ORITATION_90 = 90;
	public final static int ORITATION_0 = 0;
	public static final int MEDIA_ERROR_UNKNOWN = 1;
	public static final int MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY = 101;
	public static final int MEDIA_ERROR_CAMERA_PREVIEW = 102;
	public static final int MEDIA_ERROR_CAMERA_AUTO_FOCUS = 103;

	public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0;
	public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1;
	public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2;
	public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3;
	public static final int MAX_FRAME_RATE = 25;
	public static final int MIN_FRAME_RATE = 15;

	protected Camera camera;
	protected Camera.Parameters mParameters = null;
	protected List<Size> mSupportedPreviewSizes;
	protected SurfaceHolder mSurfaceHolder;

	protected int mFrameRate = MIN_FRAME_RATE;
	protected int mBitrate = 500000;
	protected int mCameraId = CameraInfo.CAMERA_FACING_BACK;
	protected int mVideoBitrate = 2048;

	protected boolean mPrepared, mStartPreview, mSurfaceCreated;

	// protected AudioRecorder mAudioRecorder;//修改代码，音频单独处理不在编码基类中操作
	// private String filename = "/mnt/sdcard/out.pcm";

	protected byte[] processedData = new byte[1024];
	public boolean isRecord = false;
	protected int mSampleRate = 8000;
	protected int channels = 1;
	protected Context context;

	private Object mLock = new Object();
	protected byte[] pps;
	protected byte[] sps;
	protected long m_startStamp;
	RandomAccessFile raf = null;
	byte[] h264Buff = null;

	// 此处的宽和高是默认值，实际编码会根据传进来的宽和高编码，宽和高不是任意指定，其拒绝与系统和硬件(获取设备支持的编码size可以通过Camera。Parameters的getSupportedPreviewSizes得到)
	int width = 1920;
	int height = 1080;
	// android的摄像头默认的采集方向为左倒置90度(后置，前置为右倒置90)，编码的时候我们需要将其顺时针旋转90度(后置，前置为逆时针旋转90),旋转之后导致款和高对调，此处的width2和height2记录导致后的宽和高
	int width2 = 1920;
	int height2 = 1080;
	byte[] data1 = new byte[width * height * 3 / 2];
	int p1, p2, wh;
	String Url;
	int videortpport, videortcpport, audioport;
	private int imageFormat;



	private boolean isSupported(List<String> list, String key) {
		return list != null && list.contains(key);
	}

	public boolean autoFocus(AutoFocusCallback cb) {
		if (camera != null) {
			try {
				camera.cancelAutoFocus();

				if (mParameters != null) {
					String mode = getAutoFocusMode();
					if (StringUtils.isNotEmpty(mode)) {
						mParameters.setFocusMode(mode);
						camera.setParameters(mParameters);
					}
				}
				camera.autoFocus(cb);
				return true;
			} catch (Exception e) {
				if (e != null)
					Log.e("TEST", "autoFocus", e);
			}
		}
		return false;
	}

	private String getAutoFocusMode() {
		if (mParameters != null) {
			List<String> focusModes = mParameters.getSupportedFocusModes();
			if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL
					.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
				return "continuous-picture";
			} else if (isSupported(focusModes, "continuous-video")) {
				return "continuous-video";
			} else if (isSupported(focusModes, "auto")) {
				return "auto";
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean manualFocus(AutoFocusCallback cb, List<Area> focusAreas) {
		if (camera != null && focusAreas != null && mParameters != null) {
			try {
				camera.cancelAutoFocus();
				if (mParameters.getMaxNumFocusAreas() > 0) {
					mParameters.setFocusAreas(focusAreas);
				}
				if (mParameters.getMaxNumMeteringAreas() > 0) {
					mParameters.setMeteringAreas(focusAreas);
				}
				mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
				camera.setParameters(mParameters);
				camera.autoFocus(cb);
				return true;
			} catch (Exception e) {
				if (e != null)
					Log.e("TEST", "autoFocus", e);
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	protected void prepareCameraParaments() {
		if (mParameters == null)
			return;

		List<Integer> rates = mParameters.getSupportedPreviewFrameRates();
		if (rates != null) {
			if (rates.contains(MAX_FRAME_RATE)) {
				mFrameRate = MAX_FRAME_RATE;
			} else {
				Collections.sort(rates);
				for (int i = rates.size() - 1; i >= 0; i--) {
					if (rates.get(i) <= MAX_FRAME_RATE) {
						mFrameRate = rates.get(i);
						break;
					}
				}
			}
		}

		mParameters.setPreviewFrameRate(mFrameRate);
		mParameters.setPreviewSize(width, height);// 3:2
		mParameters.setPreviewFormat(ImageFormat.NV21);
		String mode = getAutoFocusMode();
		if (StringUtils.isNotEmpty(mode)) {
			mParameters.setFocusMode(mode);
		}
		if (isSupported(mParameters.getSupportedWhiteBalance(), "auto")) {
			mParameters.setWhiteBalance("auto");
		}

		if ("true".equals(mParameters.get("video-stabilization-supported"))) {
			mParameters.set("video-stabilization", "true");
		}
	}

	public void setCameraParameters(int cameraId, int frameRate, int bit) {
		mCameraId = cameraId;
		mFrameRate = frameRate;
		mBitrate = bit;
	}

	public void startPreview() {
		mPrepared = true;
		if (mStartPreview || mSurfaceHolder == null || !mPrepared) {
			return;
		} else {
			mStartPreview = true;
		}
		try {
			if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
				camera = Camera.open();
			} else {
				if (Camera.getNumberOfCameras() == 1) {
					camera = Camera.open(0);
				} else {
					camera = Camera.open(mCameraId);
				}
			}
			try
			{
				camera.setPreviewDisplay(mSurfaceHolder);
				// 设置旋转角度
				if (ConstantsValues.DeviceModel.contains("SmartPhone")
						||ConstantsValues.DeviceModel.contains("KT8001-2F")) {
					camera.setDisplayOrientation(ORITATION_0);
					//Log.i("===aaa===", "=============setDisplayOrientation===ORITATION_0===");
				} else if (ConstantsValues.DeviceBrand.contains("Hyve")
						||ConstantsValues.DeviceModel.contains("WAS-AL00")) {
					camera.setDisplayOrientation(ORITATION_90);
					//Log.i("===aaa===", "=============setDisplayOrientation===ORITATION_90===");
				} else if (ConstantsValues.DeviceModel.contains("VAA_8848PL")) {
					camera.setDisplayOrientation(ORITATION_180);
					//Log.i("===aaa===", "=============setDisplayOrientation===ORITATION_180===");
				} else {
					camera.setDisplayOrientation(ORITATION_270);
					//Log.i("===aaa===", "=============setDisplayOrientation===ORITATION_270===");
				}
				// 获取并记录下设备支持H264的编码输入格式(必须将转成对应的格式设备才可以编码为H264)
				imageFormat = getImageFormat(getCodecInfo());
			} catch (IOException e) {
				Log.e("TEST", "setPreviewDisplay fail " + e.getMessage());
			}
			mParameters = camera.getParameters();
			mSupportedPreviewSizes = mParameters.getSupportedPreviewSizes();

			for (int i = 0; i < mSupportedPreviewSizes.size(); i++) {
				Size tmpSize = mSupportedPreviewSizes.get(i);
				Log.i("===aaa===", "===" + i + "=== size:" + tmpSize.width
						+ "-" + tmpSize.height);
			}

			prepareCameraParaments();
			camera.setParameters(mParameters);
			setPreviewCallback();
			camera.startPreview();
		} catch (Exception e) {
			Log.e("TEST", "startPreview fail :" + e.getMessage());
		}
	}

	private MediaCodecInfo getCodecInfo() {
		int nbCodecs = MediaCodecList.getCodecCount();
		for (int i = 0; i < nbCodecs; i++) {
			MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
			if (!mci.isEncoder()) {
				continue;
			}

			String[] types = mci.getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				if (types[j].equalsIgnoreCase("video/avc")) {
					return mci;
				}
			}
		}
		return null;
	}

	private int getImageFormat(MediaCodecInfo info) {
		int matchedColorFormat = 0;
		CodecCapabilities cc = info.getCapabilitiesForType("video/avc");
		for (int i = 0; i < cc.colorFormats.length; i++) {
			int cf = cc.colorFormats[i];
			// choose YUV for h.264, prefer the bigger one.
			// corresponding to the color space transform in onPreviewFrame
			if ((cf >= CodecCapabilities.COLOR_FormatYUV420Planar && cf <= CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {
				if (cf > matchedColorFormat) {
					matchedColorFormat = cf;
				}
			}
		}
		return matchedColorFormat;
	}

	protected void setPreviewCallback() {
		Size size = mParameters.getPreviewSize();
		if (size != null) {
			PixelFormat pf = new PixelFormat();
			PixelFormat.getPixelFormatInfo(mParameters.getPreviewFormat(), pf);
			int buffSize = size.width * size.height * pf.bitsPerPixel / 8;
			try {
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.setPreviewCallbackWithBuffer(this);
			} catch (OutOfMemoryError e) {
				Log.e("TEST", "startPreview...setPreviewCallback...", e);
			}
			Log.e("TEST", "startPreview...setPreviewCallbackWithBuffer...width:" + size.width
					+ " height:" + size.height);
		} else {
			camera.setPreviewCallback(this);
		}
	}

	public void stopPreview() {
		if (camera != null) {
			try {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				camera.release();
			} catch (Exception e) {
				Log.e("TEST", "stopPreview...");
			}
			camera = null;
		}
		mStartPreview = false;
	}

	public void release() {
		stopPreview();
		// if (mAudioRecorder != null)
		// {
		// mAudioRecorder.stop();
		// mAudioRecorder = null;
		// // NativiJniUtils.close();
		// try
		// {
		// Thread.sleep(500);
		// } catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
		finalize();
	}

	/**
	 * init SurfaceView's width and height
	 */
	public void initSurfaceView(SurfaceView mSurfaceView) {
		if (mSurfaceView != null) {
			setSurfaceHolder(mSurfaceView.getHolder());
			mSurfaceHolder = mSurfaceView.getHolder();
		}
	}

	@SuppressWarnings("deprecation")
	public void setSurfaceHolder(SurfaceHolder sh) {
		if (sh != null) {
			sh.addCallback(this);
			sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	public void setVideoQuality(int width, int height) {
		this.width = width;
		this.height = height;

		if (ConstantsValues.DeviceModel.contains("SmartPhone")
			|| ConstantsValues.DeviceModel.contains("VAA_8848PL")
			|| ConstantsValues.DeviceModel.contains("KT8001-2F")) {
			this.width2 = width;
			this.height2 = height;
			PLog.d("===aaa===", "============setVideoQuality===222===");
		}  else {
			this.width2 = height;
			this.height2 = width;
			PLog.d("===aaa===", "============setVideoQuality===111===");
		}
		h264Buff = new byte[width * height * 3];
		wh = width * height;
	}

	public void setDestination(String url, int videoPort, int audioPort) {
	}

	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public int CompressBuffer(byte[] yuvBuff, int length, byte[] h264Buff) {
		return 0;
	}

	boolean isGetFlag = false;

	// static File outFile = new File("/sdcard/123" + ".yuv");
	// static FileOutputStream outStream = null;

	// 数据抛出回调
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (isRecord) {
			int tmpResult = 0;
			if (imageFormat == CodecCapabilities.COLOR_FormatYUV420Planar) {
				// 绿皮手机(yuv420p的输入格式，不管前置还是后置摄像头都是顺时针旋转90)
				//Log.i("===aaa===", "=====setImageRotate===YUVRotate90===");
				YUVRotate90(data1, data, width, height);				
				tmpResult = CompressBuffer(data1, data1.length, h264Buff);
			} else {
				if (ConstantsValues.DeviceModel.contains("SmartPhone")
						|| ConstantsValues.DeviceModel.contains("KT8001-2F")) {

					//Log.i("===aaa===", "=====setImageRotate===0===");
					tmpResult = CompressBuffer(data, data.length, h264Buff);

				} else if ( ConstantsValues.DeviceModel.contains("V100")) {

					//Log.i("===aaa===", "=====setImageRotate===YUV_Rotate90===");
					YUV_Rotate90(data1, data, width, height);
					tmpResult = CompressBuffer(data1, data1.length, h264Buff);

				} else if (ConstantsValues.DeviceModel.contains("VAA_8848PL")) {

					//Log.i("===aaa===", "=====setImageRotate===rotateYUV420Degree180===");
					rotateYUV420Degree180(data, data1, width, height);
					tmpResult = CompressBuffer(data1, data1.length, h264Buff);

				} else {
					//Log.i("===aaa===", "=====setImageRotate===other===DeviceModel===");
					if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
						// 后置摄像头顺时针旋转90
						YUVRotate90(data1, data, width, height);
					} else {
						// 前置摄像头逆时针旋转90
						YUV_Rotate90(data1, data, width, height);
					}
					tmpResult = CompressBuffer(data1, data1.length, h264Buff);
				}
			}
			final int result = tmpResult;
			if (this instanceof MediaRecorderHard && result > 0) {
				if (!isGetFlag) {
					((MediaRecorderHard) this).getMediaInfo();
					isGetFlag = true;
				}
			}
			try {
				final int stamp = (int) ((System.currentTimeMillis() - m_startStamp) * 90);
				//Log.e("System.out", " " + SystemClock.elapsedRealtime() + " " + stamp);
				if (result > 0) {
					//创建基本线程池
					ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3,5,1, TimeUnit.SECONDS,
							new LinkedBlockingQueue<Runnable>(50));
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {
								synchronized (mLock) {
									PLog.d("onPreviewFrame",
											"529 sps="+sps.length+" pps="+pps.length+" h264Buff="+h264Buff.length+" result="+result);
									SendMediaData.sendVideoData(sps, pps, h264Buff, result, stamp);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					threadPoolExecutor.execute(runnable);

				}

//				final int stamp = (int) ((System.currentTimeMillis() - m_startStamp) * 90);
//				//Log.e("System.out", " " + SystemClock.elapsedRealtime() + " " + stamp);
//				if (result > 0) {
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							synchronized (mLock) {
//								PLog.d("onPreviewFrame",
//										"529 sps="+sps.length+" pps="+pps.length+" h264Buff="+h264Buff.length+" result="+result);
//								try {
//									SendMediaData.sendVideoData(sps, pps, h264Buff, result, stamp);
//								}catch (Exception e){
//									e.getMessage();
//								}
//
//							}
//						}
//					}).start();
//				}
			} catch (Exception ex) {
				PLog.e("TEST", ex.toString());
			}
		}
		camera.addCallbackBuffer(data);
	}



	public Bitmap convertBmp(Bitmap bmp) {
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1);
		Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
		return convertBmp;
	}   
	
	void xt_YUV_Rotate90(byte[] des, byte[] src, int srcWidth, int srcHeight) {
		// int wh = srcWidth * srcHeight;
		int k = 0;
		for (int i = 0; i < srcWidth; i++) {
			for (int j = 0; j < srcHeight; j++) {
				des[k] = src[srcWidth * j + i];
				k++;
			}
		}
		for (int i = 0; i < srcWidth; i += 2) {
			for (int j = 0; j < srcHeight / 2; j++) {
				des[k] = src[wh + srcWidth * j + i];
				des[k + 1] = src[wh + srcWidth * j + i + 1];
				k += 2;
			}
		}
	}

	// 逆时针旋转90度
	void YUV_Rotate90(byte[] des, byte[] src, int srcWidth, int srcHeight) {
		// int wh = srcWidth * srcHeight;
		int k = 0;
		for (int i = 0; i < srcWidth; i++) {
			// int pos = srcWidth - 1;
			for (int j = 0; j < srcHeight; j++) {
				des[k] = src[wh - 1 - i - srcWidth * j];
				k++;
			}
		}
		for (int i = 0; i < srcWidth; i += 2) {
			for (int j = 0; j < srcHeight / 2; j++) {
				des[k] = src[wh + (wh / 2 - 2 - i - srcWidth * j)];
				des[k + 1] = src[wh + (wh / 2 - 2 - i - srcWidth * j) + 1];
				k += 2;
			}
		}
	}

	// 顺时针旋转90度
	void YUVRotate90(byte[] des, byte[] src, int srcWidth, int srcHeight) {
		// int wh = srcWidth * srcHeight;
		int k = 0;
		for (int i = 0; i < srcWidth; i++) {
			int pos = 0;
			for (int j = 0; j < srcHeight; j++) {
				// des[k] = src[srcWidth*(srcHeight -1-j)+i];
				des[k] = src[wh - srcWidth - pos + i];
				pos += srcWidth;
				k++;
			}
		}
		for (int i = 0; i < srcWidth; i += 2) {
			int pos = 0;
			for (int j = 0; j < srcHeight / 2; j++) {
				int tmp = wh + wh / 2 - srcWidth - pos + i;

				// des[k] = src[wh +srcWidth *(srcHeight /2 -1 -j)+i];
				des[k] = src[tmp];
				// des[k+1]=src[wh+srcWidth *(srcHeight /2 -1 -j)+i +1];
				des[k + 1] = src[tmp + 1];
				pos += srcWidth;
				k += 2;
			}
		}
	}
	
	private void rotateYUV420Degree180(byte[] src, byte[] des, int width, int height){

    	int i =0;
    	int count =0;
    	 
		for(i = width * height -1; i >=0; i--){
			des[count]= src[i];
			count++;
		}

		int start = width * height *3/2-1;
		int wh = width* height;
		for(i = start; i >= wh; i -= 2){
			des[count++]= src[i -1];
			des[count++]= src[i];			
		}		
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.mSurfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.mSurfaceHolder = holder;
		this.mSurfaceCreated = true;
		// startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		mSurfaceCreated = false;
	}

	private Runnable pR = new Runnable() {
		@Override
		public void run() {
			startPreview();
		}

	};

	private Runnable tR = new Runnable() {

		@Override
		public void run() {
		}
	};

	private Runnable sR = new Runnable() {

		@Override
		public void run() {
		}
	};

	@Override
	public void startRecord() {
		isRecord = true;
		// if (mAudioRecorder == null)
		// {
		// mAudioRecorder = new AudioRecorder(this);
		// mAudioRecorder.init();
		// mAudioRecorder.start();
		// }
		new Thread(pR).start();
	}

	@Override
	public void stopRecord() {
		isRecord = false;
		// if (mAudioRecorder != null)
		// {
		// mAudioRecorder.stop();
		// mAudioRecorder = null;
		// }
		new Thread(sR).start();
	}

	@Override
	public void onAudioError(int what, String message) {
	}

	@Override
	public void receiveAudioData(byte[] sampleBuffer, int len) {
	}

	@Override
	public String getAudioConfig() {
		return "";
	}

	public void startTalk() {
		isRecord = true;
		new Thread(tR).start();
	}

	public void stopTalk() {
		isRecord = false;
		new Thread(sR).start();
	}

	@Override
	public void initSurfaceView(int Left, int Top, int Width, int Height, SurfaceView mSurfaceView) {
	}

	@Override
	public void startRecordSip() {
		isRecord = true;
		// if (mAudioRecorder == null)
		// {
		// mAudioRecorder = new AudioRecorder(this);
		// mAudioRecorder.init();
		// mAudioRecorder.start();
		// }
		new Thread(pR).start();
	}

	@Override
	public void setStartStamp(long startStamp) {
		m_startStamp = startStamp;
	}

	public String getDeviceCamera() {

		StringBuffer cameraBuf = new StringBuffer();

		int tmpCnt = Camera.getNumberOfCameras();
		if (tmpCnt > 0) {
			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < tmpCnt; i++) {
				Camera.getCameraInfo(i, info);
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					cameraBuf.append("front");
				} else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					cameraBuf.append("back");
				}
			}
		}
		return cameraBuf.toString();
	}

}