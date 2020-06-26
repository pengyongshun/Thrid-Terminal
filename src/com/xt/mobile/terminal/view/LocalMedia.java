package com.xt.mobile.terminal.view;

import java.io.File;
import java.io.FileOutputStream;

import com.xt.mediarecorder.StringUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LocalMedia implements Callback, PreviewCallback {
	
	private Camera.Parameters mParameters = null;
	@SuppressLint("InlinedApi")
	private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private int width = 1280;
	private int height = 720;
	private Activity activity;
	private MediaRecorder mediaRecorder;
	private String name = "";

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public LocalMedia(Activity activity, SurfaceView surfaceView) {
		this.activity = activity;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setFixedSize(width, height);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

	}

	public Camera getCamera() {
		return camera;
	}

	public int getCameraId() {
		return mCameraId;
	}

	public void startPreview() {
		if (camera != null) {
			camera.startPreview();
		}
	}

	public void stopPreview() {
		if (camera != null) {
			camera.stopPreview();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {

			if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
				camera = Camera.open();
			} else {
				// camera = Camera.open(mCameraId);
				camera = Camera.open();
			}

			if (camera != null) {

				camera.setPreviewDisplay(surfaceHolder);
				camera.setDisplayOrientation(getDispalyOritation(activity, mCameraId));
				mParameters = camera.getParameters();
				mParameters.setPreviewSize(1280, 720);
				mParameters.setPictureSize(640, 480);
				mParameters.setPreviewFormat(ImageFormat.YV12);
				camera.setParameters(mParameters);
				camera.setPreviewCallback(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

	}

	public void takePicture(String name) {
		this.name = name;
		camera.takePicture(mshutter, null, MJpeg);
	}

	public Camera.PictureCallback MJpeg = new Camera.PictureCallback() {
		@SuppressLint("InlinedApi")
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				Matrix matrix = new Matrix();
				matrix.postRotate(getDispalyOritation(activity, mCameraId));
				mParameters.setPreviewFormat(ImageFormat.YV12);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
				String fileName = StringUtils.getFileName(StringUtils.TYPE_FILE_PHOTO, name);
				String jpgFile = fileName.replace("bmp", "jpg");
				File file = new File(jpgFile);
				FileOutputStream fos = new FileOutputStream(file);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				if (camera != null) {
					camera.startPreview();
				}
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public Camera.ShutterCallback mshutter = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {

		}
	};

	public void startRecord(String name) {
		mediaRecorder = new MediaRecorder();
		if (camera != null) {
			
			String fileName = StringUtils.getFileName(StringUtils.TYPE_FILE_VIDEO, name);
			
			mediaRecorder.setCamera(camera);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mediaRecorder.setVideoSize(width, height);// 设置分辨率 防止录制的视频出现花屏

			mediaRecorder.setOrientationHint(getDispalyOritation(activity, mCameraId));			
			mediaRecorder.setOutputFile(fileName);

			try {
				camera.setDisplayOrientation(getDispalyOritation(activity, mCameraId));
				camera.unlock();
				mediaRecorder.prepare();
				mediaRecorder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void stopRecord() {
		if (mediaRecorder != null) {
			try {
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private int getDispalyOritation(Activity activity, int cameraId) {
		int degrees = activity.getWindowManager().getDefaultDisplay().getRotation();
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			degrees = getDisplayFrontRotation(degrees);
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			degrees = getDisplayBackRotation(degrees);
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	private int getDisplayBackRotation(int degrees) {
		switch (degrees) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 190;
		default:
			break;
		}
		return 0;
	}

	private int getDisplayFrontRotation(int degrees) {
		switch (degrees) {
		case Surface.ROTATION_0:
			return 180;
		case Surface.ROTATION_90:
			return 270;
		case Surface.ROTATION_180:
			return 0;
		case Surface.ROTATION_270:
			return 90;
		default:
			break;
		}
		return 0;
	}
}