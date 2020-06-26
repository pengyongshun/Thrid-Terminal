package com.xt.mediarecorder;

import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;

import com.xtmedia.encode.SendMediaData;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MediaRecorderHard extends MediaRecorderBase {
	private MediaCodec mediaCodec;

	private static final String VCODEC = "video/avc";
	private int vcolor;
	private MediaCodecInfo vmci;

	// private MediaExtractor extractor;
	private byte[] yuv420 = null;
	byte[] m_info = null;

	public void setVideoQuality(int width, int height) {
		super.setVideoQuality(width, height);
		yuv420 = new byte[width * height * 3 / 2];
	}

	@Override
	public void startRecord() {
		// choose the right vencoder, perfer qcom then google.
		vcolor = chooseVideoEncoder();
		// vencoder yuv to 264 es stream.
		// requires sdk level 16+, Android 4.1, 4.1.1, the JELLY_BEAN
		try {
			mediaCodec = MediaCodec.createByCodecName(vmci.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// setup the vencoder.
		// @see
		// https://developer.android.com/reference/android/media/MediaCodec.html
		MediaFormat vformat = MediaFormat.createVideoFormat(VCODEC, width2, height2);
		vformat.setInteger(MediaFormat.KEY_COLOR_FORMAT, vcolor);
		vformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
		vformat.setInteger(MediaFormat.KEY_BIT_RATE, 300000);
		vformat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
		vformat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		// the following error can be ignored:
		// 1. the storeMetaDataInBuffers error:
		// [OMX.qcom.video.encoder.avc] storeMetaDataInBuffers (output) failed
		// w/ err -2147483648
		// @see http://bigflake.com/mediacodec/#q12
		mediaCodec.configure(vformat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mediaCodec.start();
		super.startRecord();
	}

	@Override
	public void startRecordSip() {
		// choose the right vencoder, perfer qcom then google.
		vcolor = chooseVideoEncoder();
		// vencoder yuv to 264 es stream.
		// requires sdk level 16+, Android 4.1, 4.1.1, the JELLY_BEAN
		// vcolor = 21;
		try {
			mediaCodec = MediaCodec.createByCodecName(vmci.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// vebi = new MediaCodec.BufferInfo();
		// setup the vencoder.
		// @see
		// https://developer.android.com/reference/android/media/MediaCodec.html
		MediaFormat vformat = MediaFormat.createVideoFormat(VCODEC, width2, height2);
		vformat.setInteger(MediaFormat.KEY_COLOR_FORMAT, vcolor);
		vformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
		vformat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate);
		vformat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
		vformat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
		vformat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		mediaCodec.configure(vformat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mediaCodec.start();
		super.startRecordSip();
	}

	// choose the video encoder by name.
	private MediaCodecInfo chooseVideoEncoder(String name, MediaCodecInfo def) {
		int nbCodecs = MediaCodecList.getCodecCount();
		for (int i = 0; i < nbCodecs; i++) {
			MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
			if (!mci.isEncoder()) {
				continue;
			}
			String[] types = mci.getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				if (types[j].equalsIgnoreCase(VCODEC)) {
					// Log.i(TAG, String.format("vencoder %s types: %s",
					// mci.getName(), types[j]));
					if (name == null) {
						return mci;
					}
					if (mci.getName().contains(name)) {
						return mci;
					}
				}
			}
		}
		return def;
	}

	// private static MediaCodecInfo selectCodec(String mimeType)
	// {
	// int numCodecs = MediaCodecList.getCodecCount();
	// for (int i = 0; i < numCodecs; i++)
	// {
	// MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
	// if (!codecInfo.isEncoder())
	// {
	// continue;
	// }
	// String[] types = codecInfo.getSupportedTypes();
	// for (int j = 0; j < types.length; j++)
	// {
	// if (types[j].equalsIgnoreCase(mimeType))
	// {
	// return codecInfo;
	// }
	// }
	// }
	// return null;
	// }

	private int chooseVideoEncoder() {
		// choose the encoder "video/avc":
		// 1. select one when type matched.
		// 2. perfer google avc.
		// 3. perfer qcom avc.
		vmci = chooseVideoEncoder(null, null);
		// vmci = chooseVideoEncoder("google", vmci);
		// vmci = chooseVideoEncoder("qcom", vmci);
		int matchedColorFormat = 0;
		MediaCodecInfo.CodecCapabilities cc = vmci.getCapabilitiesForType(VCODEC);
		for (int i = 0; i < cc.colorFormats.length; i++) {
			int cf = cc.colorFormats[i];
//			Log.i(TAG, String.format("vencoder %s supports color fomart 0x%x(%d)", vmci.getName(),
//					cf, cf));

			// choose YUV for h.264, prefer the bigger one.
			// corresponding to the color space transform in onPreviewFrame
			if ((cf >= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar && cf <= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {
				if (cf > matchedColorFormat) {
					matchedColorFormat = cf;
				}
			}
		}

		for (int i = 0; i < cc.profileLevels.length; i++) {
			MediaCodecInfo.CodecProfileLevel pl = cc.profileLevels[i];
//			Log.i(TAG, String.format("vencoder %s support profile %d, level %d", vmci.getName(),
//					pl.profile, pl.level));
		}

//		Log.i(TAG, String.format("vencoder %s choose color format 0x%x(%d)", vmci.getName(),
//				matchedColorFormat, matchedColorFormat));
		return matchedColorFormat;
	}

	@Override
	public void stopRecord() {
		try {
			// mediaCodec.signalEndOfInputStream();
			mediaCodec.stop();
			mediaCodec.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.stopRecord();
	}

	long mCount = 0;

	public int CompressBuffer(byte[] yuvBuff, int length, byte[] h264Buff) {

		int pos = 0;

		// TODO 旋转代码中前两种格式宽高用的height2和width2(其实width和height)
		if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
			YV12toYUV420Planar(yuvBuff, yuv420, height2, width2);
		} else if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
			YV12toYUV420PackedSemiPlanar(yuvBuff, yuv420, height2, width2);
		} else if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
			YV12toYUV420PackedSemiPlanar(yuvBuff, yuv420, width2, height2);
			// swapYV12toI420()
		} else {
			System.arraycopy(yuvBuff, 0, yuv420, 0, yuvBuff.length);
		}

		try {
			ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
			ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();

			int inputBufferIndex = mediaCodec.dequeueInputBuffer(100);

			if (inputBufferIndex >= 0) {
				ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
				inputBuffer.clear();
				inputBuffer.put(yuv420);
				mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length, mCount * 1000
						/ mFrameRate, 0);
				mCount++;
			}
			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

			int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);

			while (outputBufferIndex >= 0) {
				ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
				byte[] outData = new byte[bufferInfo.size];
				outputBuffer.get(outData);

				if (m_info != null) {
					System.arraycopy(outData, 0, h264Buff, pos, outData.length);
					pos += outData.length;
				} else {
					ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
					if (spsPpsBuffer.getInt() == 0x00000001) {
						m_info = new byte[outData.length];
						System.arraycopy(outData, 0, m_info, 0, outData.length);
					} else {
						return -1;
					}
				}
				mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
				outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return pos;
	}

	public void getMediaInfo() {
		if (m_info != null) {
			int offset = 4;
			for (int i = 4; i < m_info.length; i++) {
				if ((m_info[i] == 0x00) && (m_info[i + 1] == 0x00) && (m_info[i + 2] == 0x00)
						&& (m_info[i + 3] == 0x01))
					break;
				offset++;
			}
			sps = new byte[offset];
			pps = new byte[m_info.length - offset];
			System.arraycopy(m_info, 0, sps, 0, offset);
			System.arraycopy(m_info, offset, pps, 0, m_info.length - offset);
		}
	}

	// the color transform, @see
	// http://stackoverflow.com/questions/15739684/mediacodec-and-camera-color-space-incorrect
	private static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output,
			final int width, final int height) {
		/*
		 * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12 We convert by putting
		 * the corresponding U and V bytes together (interleaved).
		 */
		final int frameSize = width * height;
		final int qFrameSize = frameSize / 4;

		System.arraycopy(input, 0, output, 0, frameSize); // Y

		for (int i = 0; i < qFrameSize; i++) {
			output[frameSize + i * 2] = input[frameSize + i * 2 + 1]; // Cb (U)
			output[frameSize + i * 2 + 1] = input[frameSize + i * 2]; // Cr (V)
		}
		return output;
	}

	private static byte[] YV12toYUV420Planar(byte[] input, byte[] output, int width, int height) {
		/*
		 * COLOR_FormatYUV420Planar is I420 which is like YV12, but with U and V
		 * reversed. So we just have to reverse U and V.
		 */
		final int frameSize = width * height;
		final int qFrameSize = frameSize / 4;

		System.arraycopy(input, 0, output, 0, frameSize); // Y

		for (int i = 0; i < qFrameSize; i++) {
			output[frameSize + i] = input[frameSize + i * 2 + 1]; // Cb (U)
			output[frameSize + qFrameSize + i] = input[frameSize + i * 2]; // Cr
																			// (V)
		}

		return output;
	}

	@Override
	public void receiveAudioData(byte[] sampleBuffer, int len) {

//		ToolLog.i("收到音频数据抛出的回调len:" + len + "---sampleBuffer:" + sampleBuffer
//				+ "---sampleBuffer.len:" + sampleBuffer.length);
		if (len > 0 && isRecord)// 这里增加了一个isRecord的判断
		{
			// NativiJniUtils.AacWrite(sampleBuffer, len);
			// RCAuidoEncodeNative.PushRecordData(sampleBuffer, len);
			SendMediaData.sendAudioData(sampleBuffer, len);
		}
	}
}