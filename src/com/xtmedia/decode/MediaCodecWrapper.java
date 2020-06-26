/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xtmedia.decode;

import android.media.*;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.xt.mobile.terminal.log.PLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Simplifies the MediaCodec interface by wrapping around the buffer processing
 * operations.
 */
public class MediaCodecWrapper {
	// Handler to use for {@code OutputSampleListener} and {code
	// OutputFormatChangedListener}
	// callbacks
	private Handler mHandler;

	// Callback when media output format changes.
	public interface OutputFormatChangedListener {
		void outputFormatChanged(MediaCodecWrapper sender, MediaFormat newFormat);
	}

	private OutputFormatChangedListener mOutputFormatChangedListener = null;

	/**
	 * Callback for decodes frames. Observers can register a listener for
	 * optional stream of decoded data
	 */
	public interface OutputSampleListener {
		void outputSample(MediaCodecWrapper sender, MediaCodec.BufferInfo info, ByteBuffer buffer);
	}

	/**
	 * The {@link MediaCodec} that is managed by this class.
	 */
	MediaCodec mDecoder;

	// References to the internal buffers managed by the codec. The codec
	// refers to these buffers by index, never by reference so it's up to us
	// to keep track of which buffer is which.
	ByteBuffer[] mInputBuffers;
	ByteBuffer[] mOutputBuffers;

	// Indices of the input buffers that are currently available for writing.
	// We'll
	// consume these in the order they were dequeued from the codec.
	Queue<Integer> mAvailableInputBuffers;

	// Indices of the output buffers that currently hold valid data, in the
	// order
	// they were produced by the codec.
	Queue<Integer> mAvailableOutputBuffers;

	// Information about each output buffer, by index. Each entry in this array
	// is valid if and only if its index is currently contained in
	// mAvailableOutputBuffers.
	MediaCodec.BufferInfo[] mOutputBufferInfo;

	// An (optional) stream that will receive decoded data.
	OutputSampleListener mOutputSampleListener;

	boolean bfirst;

	boolean b_stop;

	public MediaCodecWrapper(MediaCodec Codec) {
		mDecoder = Codec;
		mDecoder.start();
		mInputBuffers = Codec.getInputBuffers();
		mOutputBuffers = Codec.getOutputBuffers();
		mOutputBufferInfo = new MediaCodec.BufferInfo[mOutputBuffers.length];
		mAvailableInputBuffers = new ArrayDeque<Integer>(mOutputBuffers.length);
		mAvailableOutputBuffers = new ArrayDeque<Integer>(mInputBuffers.length);
		bfirst = true;
		b_stop = false;
	}

	/**
	 * Releases resources and ends the encoding/decoding session.
	 */
	public void stopAndRelease() {
		b_stop = true;
		mDecoder.stop();
		mDecoder.release();
		mDecoder = null;
		mHandler = null;
		bfirst = true;
	}

	/**
	 * Getter for the registered {@link OutputFormatChangedListener}
	 */
	public OutputFormatChangedListener getOutputFormatChangedListener() {
		return mOutputFormatChangedListener;
	}

	/**
	 * 
	 * @param outputFormatChangedListener
	 *            the listener for callback.
	 * @param handler
	 *            message handler for posting the callback.
	 */
	public void setOutputFormatChangedListener(
			final OutputFormatChangedListener outputFormatChangedListener, Handler handler) {
		mOutputFormatChangedListener = outputFormatChangedListener;

		// Making sure we don't block ourselves due to a bad implementation of
		// the callback by
		// using a handler provided by client.
		Looper looper;
		mHandler = handler;
		if (outputFormatChangedListener != null && mHandler == null) {
			looper = Looper.myLooper();
			if (looper != null) {
				mHandler = new Handler();
			} else {
				throw new IllegalArgumentException("Looper doesn't exist in the calling thread");
			}
		}
	}

	/**
	 * Constructs the {@link MediaCodecWrapper} wrapper object around the video
	 * codec. The codec is created using the encapsulated information in the
	 * {@link MediaFormat} object.
	 * 
	 * @param trackFormat
	 *            The format of the media object to be decoded.
	 * @param surface
	 *            Surface to render the decoded frames.
	 * @return
	 */
	public static MediaCodecWrapper fromVideoFormat(final MediaFormat trackFormat, Surface surface)
			throws IOException {
		MediaCodecWrapper result = null;
		MediaCodec Codec = null;
		final String mimeType = trackFormat.getString(MediaFormat.KEY_MIME);
		// Check to see if this is actually a video mime type. If it is, then
		// create
		// a codec that can decode this mime type.
		if (mimeType.contains("video/")) {
			Codec = MediaCodec.createDecoderByType(mimeType);
			Codec.configure(trackFormat, surface, null, 0);
		}
		// If codec creation was successful, then create a wrapper object around
		// the
		// newly created codec.
		if (Codec != null) {
			result = new MediaCodecWrapper(Codec);
		}
		return result;
	}

	/**
	 * Write a media sample to the decoder.
	 * 
	 * A "sample" here refers to a single atomic access unit in the media
	 * stream. The definition of "access unit" is dependent on the type of
	 * encoding used, but it typically refers to a single frame of video or a
	 * few seconds of audio. {@link android.media.MediaExtractor} extracts data
	 * from a stream one sample at a time.
	 * 
	 * @param input
	 *            A ByteBuffer containing the input data for one sample. The
	 *            buffer must be set up for reading, with its position set to
	 *            the beginning of the sample data and its limit set to the end
	 *            of the sample data.
	 * 
	 * @param presentationTimeUs
	 *            The time, relative to the beginning of the media stream, at
	 *            which this buffer should be rendered.
	 * 
	 * @param flags
	 *            Flags to pass to the decoder. See
	 *            {@link MediaCodec#queueInputBuffer(int, int, int, long, int)}
	 * 
	 * @throws MediaCodec.CryptoException
	 */
	static MediaCodec.CryptoInfo cryptoInfo = new MediaCodec.CryptoInfo();

	/**
	 * Write a media sample to the decoder.
	 * 
	 * A "sample" here refers to a single atomic access unit in the media
	 * stream. The definition of "access unit" is dependent on the type of
	 * encoding used, but it typically refers to a single frame of video or a
	 * few seconds of audio. {@link android.media.MediaExtractor} extracts data
	 * from a stream one sample at a time.
	 * 
	 * @param extractor
	 *            Instance of {@link android.media.MediaExtractor} wrapping the
	 *            media.
	 * 
	 * @param presentationTimeUs
	 *            The time, relative to the beginning of the media stream, at
	 *            which this buffer should be rendered.
	 * 
	 * @param flags
	 *            Flags to pass to the decoder. See
	 *            {@link MediaCodec#queueInputBuffer(int, int, int, long, int)}
	 * 
	 * @throws MediaCodec.CryptoException
	 */
	public int writeSample(byte[] Getbuffer, int size, boolean isSecure, long pts, int flags) {
		int result = 0;
		if (b_stop)
			return 0;
		/*
		 * if (!mAvailableInputBuffers.isEmpty()) {
		 * 
		 * bfirst= false; int index = mAvailableInputBuffers.remove();
		 * 
		 * ByteBuffer inputBuffer = mInputBuffers[index]; inputBuffer.clear();
		 * inputBuffer.put(Getbuffer); //mInputBuffers[index].wrap(Getbuffer,0,
		 * Getbuffer.length); // Submit the buffer to the codec for decoding.
		 * The presentationTimeUs // indicates the position (play time) for the
		 * current sample.
		 */
		if (mDecoder != null) {
			try {
				if (mDecoder != null) {
					int inputBufferIndex = mDecoder.dequeueInputBuffer(1000 * 1000);
					if (inputBufferIndex >= 0) {
						ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
						inputBuffer.clear();
						inputBuffer.put(Getbuffer);
						mDecoder.queueInputBuffer(inputBufferIndex, 0, size, pts, flags);
						result = 1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * mDecoder.queueInputBuffer(index, 0, size, pts, flags); result = 1; }
		 * if(bfirst) { result =1; }
		 */
		return result;
	}

	/**
	 * Performs a peek() operation in the queue to extract media info for the
	 * buffer ready to be released i.e. the head element of the queue.
	 * 
	 * @param out_bufferInfo
	 *            An output var to hold the buffer info.
	 * 
	 * @return True, if the peek was successful.
	 */
	public boolean peekSample(MediaCodec.BufferInfo out_bufferInfo) {
		// dequeue available buffers and synchronize our data structures with
		// the codec.
		if (b_stop)
			return true;
		update();
		boolean result = false;
		if (!mAvailableOutputBuffers.isEmpty()) {
			int index = mAvailableOutputBuffers.peek();
			MediaCodec.BufferInfo info = mOutputBufferInfo[index];
			// metadata of the sample
			out_bufferInfo.set(info.offset, info.size, info.presentationTimeUs, info.flags);
			result = true;
		}
		return result;
	}

	/**
	 * Processes, releases and optionally renders the output buffer available at
	 * the head of the queue. All observers are notified with a callback. See
	 * {@link OutputSampleListener#outputSample(MediaCodecWrapper, android.media.MediaCodec.BufferInfo, java.nio.ByteBuffer)}
	 * 
	 * @param render
	 *            True, if the buffer is to be rendered on the {@link Surface}
	 *            configured
	 * 
	 */
	public void popSample(boolean render) {
		// dequeue available buffers and synchronize our data structures with
		// the codec.
		// update();
		if (b_stop)
			return;
		while (!mAvailableOutputBuffers.isEmpty()) {
			int index = mAvailableOutputBuffers.remove();
			if (b_stop)
				break;
			if (index >= 0) {
				try {
					if (render && mOutputSampleListener != null) {
						ByteBuffer buffer = mOutputBuffers[index];
						MediaCodec.BufferInfo info = mOutputBufferInfo[index];
						mOutputSampleListener.outputSample(this, info, buffer);

					}
					if (mDecoder != null) {
						// releases the buffer back to the codec
						mDecoder.releaseOutputBuffer(index, render);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Synchronize this object's state with the internal state of the wrapped
	 * MediaCodec.
	 */
	private void update() {
		int index;
		// Get valid input buffers from the codec to fill later in the same
		// order they were
		// made available by the codec.
		while ((index = mDecoder.dequeueInputBuffer(2000)) != MediaCodec.INFO_TRY_AGAIN_LATER
				&& b_stop == false) {
			mAvailableInputBuffers.add(index);
		}
		// Likewise with output buffers. If the output buffers have changed,
		// start using the
		// new set of output buffers. If the output format has changed, notify
		// listeners.
		MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
		while (b_stop == false
				&& (index = mDecoder.dequeueOutputBuffer(info, 0)) != MediaCodec.INFO_TRY_AGAIN_LATER) {
			switch (index) {
			case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
				mOutputBuffers = mDecoder.getOutputBuffers();
				mOutputBufferInfo = new MediaCodec.BufferInfo[mOutputBuffers.length];
				mAvailableOutputBuffers.clear();
				break;
			case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
				if (mOutputFormatChangedListener != null) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mOutputFormatChangedListener.outputFormatChanged(
									MediaCodecWrapper.this, mDecoder.getOutputFormat());

						}
					});
				}
				break;
			default:
				// Making sure the index is valid before adding to output
				// buffers. We've already
				// handled INFO_TRY_AGAIN_LATER, INFO_OUTPUT_FORMAT_CHANGED &
				// INFO_OUTPUT_BUFFERS_CHANGED i.e all the other possible return
				// codes but
				// asserting index value anyways for future-proofing the code.
				if (index >= 0) {
					mOutputBufferInfo[index] = info;
					// Log.e("decoder", "output "+info.presentationTimeUs);
					mAvailableOutputBuffers.add(index);
				} else {
					throw new IllegalStateException("Unknown status from dequeueOutputBuffer");
				}
				break;
			}

		}

	}

	/*
	 * private class WriteException extends Throwable { private
	 * WriteException(final String detailMessage) { super(detailMessage); } }
	 */
	// static Surface m_surfce;
	int render = 0;

	/**
	 * Callback for decodes frames. Observers can register a listener for
	 * optional stream of decoded data
	 */

	// public static void SetSurface(Surface surface)
	// {
	// m_surfce = surface;
	// }
	public static MediaCodecWrapper CreateMediaCodec(String codecType, Surface surface, int width,
			int height, byte[] pro, byte[] pps) {
		PLog.d("MediaCodecWrapper","------------------>width="+width+"height="+height+"pro="+pro.length+"\n" +
				"pps="+pps.length+"codecType="+codecType);
		return new MediaCodecWrapper(codecType, surface, width, height, pro, pps);
	}

	public MediaCodecWrapper(String codecType, Surface surface, int width, int height, byte[] pro,
			byte[] pps) {
		// Check to see if this is actually a video mime type. If it is, then
		// create
		// a codec that can decode this mime type.
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(codecType, width, height);
		mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(pro));
		if (pps != null)
			mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
		// mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, width *
		// height);
		// mediaFormat.setInteger("durationUs", 63446722);
		if (codecType.contains("video/")) {
			try {
				mDecoder = MediaCodec.createDecoderByType(codecType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mDecoder != null) {
			// Surface asurface = Surface(surface);
			mDecoder.configure(mediaFormat, surface, null, 0);
			mDecoder.start();
			mInputBuffers = mDecoder.getInputBuffers();
			mOutputBuffers = mDecoder.getOutputBuffers();
			mOutputBufferInfo = new MediaCodec.BufferInfo[mOutputBuffers.length];
			mAvailableInputBuffers = new ArrayDeque<Integer>(mOutputBuffers.length);
			mAvailableOutputBuffers = new ArrayDeque<Integer>(mInputBuffers.length);
		}
		return;
	}

	public int CloseCodec() {
		stopAndRelease();
		return 0;
	}

	public long PutOneFrame(byte[] inputbuffer, int size, int Flags, long pts) {
		// Log.e("decoder", "input "+pts);
		int result = writeSample(inputbuffer, size, false, pts, Flags);
		// Examine the sample at the head of the queue to see if its ready to be
		// rendered and is not zero sized End-of-Stream record.
		// MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
		// peekSample(out_bufferInfo);
		//
		//
		//
		// if (out_bufferInfo.size < 0) {
		// // stopAndRelease();
		// } else/* if (out_bufferInfo.presentationTimeUs / 1000 < totalTime)*/
		// {
		// // Pop the sample off the queue and send it to {@link Surface}
		// popSample(true);
		// }
		return result;
	}

	int renderindex = -1;

	public long GetFramePts() {
		// Examine the sample at the head of the queue to see if its ready to be
		// rendered and is not zero sized End-of-Stream record.
		try {
			if (mDecoder != null) {
				MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
				int index = mDecoder.dequeueOutputBuffer(out_bufferInfo, 1000 * 1000);
				if (index >= 0) {
					renderindex = index;
					return out_bufferInfo.presentationTimeUs;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return -1;
	}

	public void RenderFrame(boolean brender) {
		/* if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) */
		// Pop the sample off the queue and send it to {@link Surface}
		try {
			if (renderindex >= 0 && mDecoder != null) {
				mDecoder.releaseOutputBuffer(renderindex, brender);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// popSample(brender);
	}
}