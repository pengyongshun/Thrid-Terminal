package com.xt.mobile.terminal.view.wheelview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.view.wheelview.adapter.NumericWheelAdapter;
import com.xt.mobile.terminal.view.wheelview.adapter.WheelViewAdapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * 选择日期、时间dialog
 */
public class WiDateTimePickerDialog {
    private final String tag = "WiDateTimePickerDialog";

    private void logd(String msg) {
        Log.d(tag, "[WiDateTimePickerDialog] -- " + msg);
    }
    private Context mContext;

	private Dialog mDialog = null;
	private WheelView mYearView;// ”年“ 滑动view
	private WheelView mMonthView; // "月" 滑动view
    private WheelView mDayView; // "日" 滑动view
    private WheelView mHourView; // "小时" 滑动view
    private WheelView mMinView; // "分钟" 滑动view

    private TextView mTvTime; // 显示选择的时间

    private ImageView mBtnOk;
    private ImageView mBtnCancel;
    
    private TextView tv_selectTime; // 日期和星期显示

	private RelativeLayout mRelMinSec;// 时分父类
	private View mCenterView;// 中间的view
	public static int typeOne = 1;// 显示年月日时分模式
	public static int typeTwo = 2;// 显示年月日模式
	private int mShowType = typeOne;// 默认年月日时分模式
	
    public int getmShowType() {
		return mShowType;
	}

	public void setmShowType(int mShowType) {
		this.mShowType = mShowType;
	}

	private int mYear = 0;

    // 是谁请求的时间控件
    private int mRequestCode = -1;

    public WiDateTimePickerDialog(Context context) {
        mContext = context;
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
    }

    /*
     * 显示time picker， 指定时间、日期 requestCode
     * 可以把需要显示时间日期的控件id传进来，在完成回调的地方requestCode会原值回调，用于区分是哪个控件需要的时间
     */
    public void showDialog(int requestCode, int year, int month, int day, int time, int min) {
        mRequestCode = requestCode;
        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.Dialog_NoTitle);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            final View view = getDataPick( year,month, day, time, min);
            mDialog.getWindow().setContentView(view);
        } else if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /*
     * 显示time picker， 指定时间、日期
     */
    public void showDialog(int requestCode) {
        showDialog(requestCode, System.currentTimeMillis());
    }

    /*
     * 显示time picker，指定时间戳
     */
    public void showDialog(int requestCode, long stamp) {
        Calendar c = Calendar.getInstance();
        if (stamp > 0) {
            c.setTimeInMillis(stamp);
        }
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        showDialog(requestCode, year, month, day, hour, minute);
    }

    /*
     * 生成日期picker view
     */
    private View getDataPick(int year, int month, int day, int hour, int min) {
		int curYear = year;
        int curMonth = month + 1;
        int curDate = day;
        int curHour = hour;
        int curMin = min;
        logd("[getDataPick] -- month:" + month + ", day:" + day + ", hour:" + hour + ", min:" + min);

        // 用于控制横向view的间距的， 经验值...
        int padding = (int) this.mContext.getResources().getDimension(R.dimen.p40);
        View view = LayoutInflater.from(mContext).inflate(R.layout.lyt_date_time_picker, null);
        mTvTime = (TextView) view.findViewById(R.id.tv_time);
        mTvTime.setVisibility(View.GONE); // 测试使用的， 方便查看， 发布时隐藏掉即可

		mRelMinSec = (RelativeLayout) view.findViewById(R.id.rel_min_sec);// 时分父类
		mCenterView = (View) view.findViewById(R.id.view_center);// 中间view
		
		// 显示年月日模式
		if (mShowType == typeTwo) {
			mRelMinSec.setVisibility(View.GONE);
			mCenterView.setVisibility(View.GONE);
		} else {
			mRelMinSec.setVisibility(View.VISIBLE);
			mCenterView.setVisibility(View.VISIBLE);
		}
        
        tv_selectTime = (TextView) view.findViewById(R.id.tv_selectTime);// 日期和星期显示
        
        // 年
        mYearView = (WheelView) view.findViewById(R.id.year);
        NumericWheelAdapter adapterYear = new NumericWheelAdapter(mContext, 1900, 2099, "%04d");
        mYearView.setViewAdapter(adapterYear);
        mYearView.setCyclic(true);
        mYearView.addScrollingListener(mScrollListener);
        adapterYear.setTextGravity(Gravity.RIGHT);
        adapterYear.setTextViewPadding(0, padding);
        adapterYear.setWheelView(mYearView);
        
        // 月
        mMonthView = (WheelView) view.findViewById(R.id.month);
        NumericWheelAdapter adapterMonth = new NumericWheelAdapter(mContext, 1, 12, "%02d");
        mMonthView.setViewAdapter(adapterMonth);
        mMonthView.setCyclic(true);
        mMonthView.addScrollingListener(mScrollListener);
        adapterMonth.setTextGravity(Gravity.RIGHT);
        adapterMonth.setTextViewPadding(0, padding);
        adapterMonth.setWheelView(mMonthView);

        // 日
        mDayView = (WheelView) view.findViewById(R.id.day);
        NumericWheelAdapter adapterDay = new NumericWheelAdapter(mContext, 1, getDay(curYear,
                curMonth), "%02d");
        adapterDay.setLabel("");
        mDayView.setViewAdapter(adapterDay);
        mDayView.setCyclic(true);
        mDayView.addScrollingListener(mScrollListener);
        adapterDay.setTextGravity(Gravity.LEFT);
        adapterDay.setTextViewPadding(padding, 0);
        adapterDay.setWheelView(mDayView);

        // 小时
        mHourView = (WheelView) view.findViewById(R.id.hour);
        NumericWheelAdapter adapterTime = new NumericWheelAdapter(mContext, 0, 23, "%02d");
        mHourView.setViewAdapter(adapterTime);
        mHourView.setCyclic(true);
        mHourView.addScrollingListener(mScrollListener);
        adapterTime.setTextGravity(Gravity.RIGHT);
        adapterTime.setTextViewPadding(0, padding);
        adapterTime.setWheelView(mHourView);

        // 分钟
        mMinView = (WheelView) view.findViewById(R.id.min);
        NumericWheelAdapter adapterMin = new NumericWheelAdapter(mContext, 0, 59, "%02d");
        mMinView.setViewAdapter(adapterMin);
        mMinView.setCyclic(true);
        mMinView.addScrollingListener(mScrollListener);
        adapterMin.setTextGravity(Gravity.LEFT);
        adapterMin.setTextViewPadding(padding, 0);
        adapterMin.setWheelView(mMinView);

        // 显示的item个数
        mYearView.setVisibleItems(7);
        mMonthView.setVisibleItems(7);
        mDayView.setVisibleItems(7);
        mHourView.setVisibleItems(7);
        mMinView.setVisibleItems(7);

        mYearView.setCurrentItem(curYear - 1900);
        mMonthView.setCurrentItem(curMonth - 1);
        mDayView.setCurrentItem(curDate - 1);
        mHourView.setCurrentItem(curHour);
        mMinView.setCurrentItem(curMin);

        mBtnOk = (ImageView) view.findViewById(R.id.btn_ok);
        mBtnCancel = (ImageView) view.findViewById(R.id.btn_cancel);
        mBtnOk.setOnClickListener(mClickListener);
        mBtnCancel.setOnClickListener(mClickListener);

        // 显示一次星期几
        getCurrentSelectTime();
        
        return view;
    }

    /*
     * test code
     */
    private String getTime() {
        String birthday = new StringBuilder()
                .append((mMonthView.getCurrentItem() + 1) < 10 ? "0"
                        + (mMonthView.getCurrentItem() + 1)
                        : (mMonthView.getCurrentItem() + 1))
                .append(" - ")
                .append(((mDayView.getCurrentItem() + 1) < 10) ? "0"
                        + (mDayView.getCurrentItem() + 1)
                        : (mDayView.getCurrentItem() + 1)).toString();

        String timeText = new StringBuilder()
                .append((mHourView.getCurrentItem() + 1) < 10 ? "0"
                        + (mHourView.getCurrentItem() + 1)
                        : (mHourView.getCurrentItem() + 1))
                .append(" : ")
                .append(((mMinView.getCurrentItem() + 1) < 10) ? "0"
                        + (mMinView.getCurrentItem() + 1)
                        : (mMinView.getCurrentItem() + 1)).toString();
        return birthday + " -- " + timeText;
    }

    /*
     * 点击 完成、取消的按钮事件处理
     */
    OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_ok) {
                onOk();
            }
            closeDialog();
        }
    };

    /*
     * 完成
     */
    private void onOk() {
        if (mPickerListener != null) {
        	int year =mYearView.getCurrentItem()+1900;
            int month = mMonthView.getCurrentItem() + 1;
            int day = mDayView.getCurrentItem() + 1;
            int hour = mHourView.getCurrentItem();
            int min = mMinView.getCurrentItem();
            mPickerListener.onDateTimePickerFinished(mRequestCode, year, month, day, hour, min);
        }
    }
    
    /*
     * 关闭弹框
     */
    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /*
     * 滑动监听
     */
    OnWheelScrollListener mScrollListener = new OnWheelScrollListener() {
    	
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            mTvTime.setText(getTime());
            getCurrentSelectTime();
			// 根据年月判断日期
			WheelViewAdapter adapter = (mDayView.getViewAdapter());
			if (adapter instanceof NumericWheelAdapter) { // 重新设定日期
				NumericWheelAdapter numAdapter = (NumericWheelAdapter) adapter;
				numAdapter.setMaxValue(getDay(mYearView.getCurrentItem()+1900,
						mMonthView.getCurrentItem() + 1));
				numAdapter.notifyDataChangedEvent();
			}
        }
    };
    
    /*
     * 滚动完成后显示当前选择的时间和星期
     */
	public void getCurrentSelectTime() {
		Calendar c = Calendar.getInstance();
		Format f = new SimpleDateFormat("yyyy/MM/dd E");

		int year = mYearView.getCurrentItem() + 1900;
		int month = mMonthView.getCurrentItem();
		int day = mDayView.getCurrentItem() + 1;

		c.set(year, month, day);
		if (tv_selectTime != null) {
			tv_selectTime.setText(f.format(c.getTime()));
		}
	}
    

    /**
     * 计算这年这月有多少天？ 闰年、大月、小月？...
     */
    private int getDay(int year, int month) {
        int day = 30;
        /*
         * 大月 31天、 小月 30天， 2月比较特殊： 闰年29天，否则28天。 这些是小学数学常识， 不知道的去找找小学书本，没办法解释了
         */

        // 是否闰年
        boolean flag = (year % 4) == 0 ? true : false;

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /*
     * 日期、时间选择完成事件回调
     */
    public interface DateTimePickerListener {
        // requestCode, 用于标示是谁请求的，方便多个时间控件的地方区分
        public void onDateTimePickerFinished(int requestCode, int year, int month, int day, int hour, int min);
    }

    private DateTimePickerListener mPickerListener;

    /**
     * 日期、时间选择完成事件回调
     */
    public void setDateTimePickerListener(DateTimePickerListener listener) {
        mPickerListener = listener;
    }
}
