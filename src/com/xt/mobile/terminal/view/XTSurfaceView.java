package com.xt.mobile.terminal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.xt.mobile.terminal.contants.ConstantsValues;

public class XTSurfaceView extends SurfaceView implements OnGestureListener,
		OnDoubleTapListener
{
	private static final String TAG = XTSurfaceView.class.getSimpleName();
	private boolean debug = ConstantsValues.DEBUG && true;
	private Context mContext;
	private GestureDetector mGestureDetector;

	public XTSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public XTSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public XTSurfaceView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		mContext = context;
		mGestureDetector = new GestureDetector(mContext, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean hah = mGestureDetector.onTouchEvent(event);
		log("" + hah, Log.ERROR);
		return false;
	}

	/**
	 * show log
	 * 
	 * @param tag
	 * @param msg
	 * @param level
	 */
	private void log(String msg, int level)
	{
		if (debug)
		{
			switch (level)
			{
			case Log.INFO:
				Log.i(TAG, msg);
				break;
			case Log.WARN:
				Log.w(TAG, msg);
				break;
			case Log.VERBOSE:
				Log.v(TAG, msg);
				break;
			case Log.ERROR:
				Log.e(TAG, msg);
				break;
			case Log.DEBUG:
				Log.d(TAG, msg);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
		log("onSingleTapConfirmed", Log.INFO);
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
		log("onDoubleTap", Log.INFO);
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e)
	{
		log("onDoubleTapEvent", Log.INFO);
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		log("onDown id " + this.getId(), Log.INFO);
		isDoubleTabClicked(mContext, this.getId());
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		log("onShowPress", Log.INFO);

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		log("onSingleTapUp", Log.INFO);
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		log("onScroll", Log.INFO);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
		log("onLongPress", Log.INFO);

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		log("onFling", Log.INFO);
		return false;
	}

	/**
	 * 告诉XTFrameLayout 哪个XTSurfaceView 被双击了
	 */
	private void isDoubleTabClicked(Context context, int id)
	{
		log("isDoubleTabClicked", Log.INFO);
	}
}