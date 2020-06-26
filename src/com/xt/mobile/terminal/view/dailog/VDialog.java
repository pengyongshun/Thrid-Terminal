package com.xt.mobile.terminal.view.dailog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.XTApplication;
import com.xt.mobile.terminal.util.MyActivityManager;


/**
 * 类名        ：VDialog
 *
 * 描述        ：自定义弹出框
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class VDialog {
	private static VDialog instance = null;
	public static final int INSERTDATA = 101;
	public static final int UPDATEDATA = 103;
	public static final int CANCEL = 100;
	public static final int OK = 102;
	private MyPopupWindow pw;
	private LoadingDialog mLoadingDlg = null;
	private int deviceWidth = 0;
	private int deviceHeight = 0;
	private Context context;

	public static synchronized VDialog getDialogInstance(Context context) {
		if (null == instance) {
			instance = new VDialog(context);
		}
		return instance;
	}

	public VDialog(Context context) {
		this.context=context;
		getDeviceScreenSize();
	}

	/**
	 * 函数名 ：getDeviceScreenSize
	 *
	 * 功能描述 ：获取设备尺寸
	 *
	 */
	private void getDeviceScreenSize() {
		Display dis = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		dis.getMetrics(metrics);
		deviceWidth = metrics.widthPixels;
		deviceHeight = metrics.heightPixels;

	}

	/**
	 * 显示loding框
	 */
	public void showLoadingDialog(String content, boolean cancelable) {
		Activity currActy = MyActivityManager.getActivityManager().currentActivity();
		hideLoadingDialog();
		if (currActy != null && !currActy.isFinishing()) {
			if (mLoadingDlg ==null){
				mLoadingDlg = new LoadingDialog(currActy, R.style.showLoadingDialogStyle);
				mLoadingDlg.setDialogLayout(R.layout.loading_tip);
				// 如果上面传递下来是TRUE则不消失框
				mLoadingDlg.setDialogCanceable(cancelable);
				mLoadingDlg.setTipContent(content);
				mLoadingDlg.setCanceledOnTouchOutside(false);
				mLoadingDlg.show();
			}else {
				if (mLoadingDlg.isShowing()){
					mLoadingDlg.dismiss();
				}

				mLoadingDlg=null;

				mLoadingDlg = new LoadingDialog(currActy, R.style.showLoadingDialogStyle);
				mLoadingDlg.setDialogLayout(R.layout.loading_tip);
				// 如果上面传递下来是TRUE则不消失框
				mLoadingDlg.setDialogCanceable(cancelable);
				mLoadingDlg.setTipContent(content);
				mLoadingDlg.setCanceledOnTouchOutside(false);
				mLoadingDlg.show();
			}


		}
	}

//	/**
//	 * 函数名 ：showUpgradeTipDlg 功能描述：版本升级弹出框
//	 */
//	public MyPopupWindow showUpDateVersionDlg(final Activity context, View parent, final Handler handler,
//											  CheckVersionResult result, final boolean isForceUpdae) {
//		if (context != null && !context.isFinishing()) {
//			if (parent != null && parent.getWindowToken() != null) {
//				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				final View vPopupWindow = inflater.inflate(R.layout.showupgradetipdlg_layout, null, false);
//				pw = new MyPopupWindow(context, vPopupWindow, deviceWidth, deviceHeight, true);
//				pw.setFocusable(false);
//				pw.setOutsideTouchable(false);
//				pw.showAtLocation(parent, Gravity.CENTER, 0, 0);
//				pw.setBackgroundDrawable(new BitmapDrawable());
//
//
//				TextView updateVersion = (TextView) vPopupWindow.findViewById(R.id.tv_update_version);
//				updateVersion.setText("V" + result.getsVersion());
//				TextView updateDescribe = (TextView) vPopupWindow.findViewById(R.id.tv_update_version_describe);
//				updateDescribe.setText(result.getUpDescribe());
//				//设置可滚动
//				updateDescribe.setMovementMethod(ScrollingMovementMethod.getInstance());
//				// 稍后更新
//				final Button updateLater = (Button) vPopupWindow.findViewById(R.id.btn_update_version_later);
//				updateLater.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Message msgs = new Message();
//						msgs.what = VDialog.CANCEL;
//						handler.sendMessage(msgs);
//						closePw();
//					}
//				});
//
//
//				// 现在更新
//				Button updateNow = (Button) vPopupWindow.findViewById(R.id.btn_update_version_now);
//				updateNow.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Message msgs = new Message();
//						msgs.what = VDialog.OK;
//						handler.sendMessage(msgs);
//						closePw();
//					}
//				});
//
//				//关闭按钮
//				// 现在更新
//				ImageView updateClose = (ImageView) vPopupWindow.findViewById(R.id.img_version_update_close);
//				updateClose.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						if (!isForceUpdae) {
//							closePw();
//						}
//					}
//				});
//
//				vPopupWindow.setFocusable(true);// 设置view能够接听事件
//				vPopupWindow.setFocusableInTouchMode(true);
//				vPopupWindow.setOnKeyListener(new View.OnKeyListener() {
//					@Override
//					public boolean onKey(View view, int i, KeyEvent keyEvent) {
//						int a = 2;
//						return false;
//					}
//				});
//				pw.setTouchInterceptor(new View.OnTouchListener() {
//					@Override
//					public boolean onTouch(View view, MotionEvent motionEvent) {
//						if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
//							int a = 0 ;
//							return true;
//						}
//						return false;
//					}
//				});
//
//
//				pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
//					@Override
//					public void onDismiss() {
//						int a = 0 ;
//					}
//				});
//				//如果是强制升级  隐藏稍后升级
//				if (isForceUpdae) {
//					updateLater.setVisibility(View.GONE);
//				}
//			}
//		}
//
//		return pw;
//	}

	/*
	 * 关闭正在显示的弹出框
	 */
	public void closeLoadingDialog() {

		mLoadingDlg.dismiss();
		mLoadingDlg = null;

	}

	/**
	 * 隐藏loding框 返回true，表示有dlg显示， false 没有dlg在显示
	 */
	public boolean hideLoadingDialog() {
		if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
			mLoadingDlg.dismiss();
			mLoadingDlg = null;
			Log.i("VDialog", "hideLoadingDialog");
		}
		return true;

	}

	public abstract static interface OnDialogDismissListener {
		public void onDismiss();
	}



	/**
	 * @method: closePw @Description: 关闭和释放资源 @throws
	 */
	public void closePw() {
		if (pw != null) {
			Activity attach = pw.getAttachActivity();
			if (attach != null && !attach.isFinishing()) {
				pw.closePopupWindow();
				pw = null;
			}
		}
	}

	/**
	 * toast 提示
	 * @param text
	 */
	public void toast(final String text) {
	       Toast.makeText(XTApplication.getmAppContext(),
				   text, Toast.LENGTH_SHORT).show();

	    }

	/**
	 * 会议更多
	 */
	public void popMettingMoreDialog(final Activity context, View parent, final Handler handler) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive()) {
			inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
		//LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutInflater inflater = context.getLayoutInflater();
		final View vPopupWindow = inflater.inflate(R.layout.popup_list_metting_more, null, false);
		pw = new MyPopupWindow(context, vPopupWindow, deviceWidth, deviceHeight, true);


		vPopupWindow.setFocusableInTouchMode(true);

		pw.setFocusable(true); // 设置PopupWindow可触摸
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		pw.setAnimationStyle(R.style.animTranslate);

		pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        //获取自身的长宽高
		parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int popupHeight = parent.getMeasuredHeight();
		int popupWidth = parent.getMeasuredWidth();
		//获取需要在其上方显示的控件的位置信息
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		//在控件上方显示
		pw.showAtLocation(parent, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
		//pw.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				closePw();
			}
		});

		//绑定页面中控件
		TextView mPopAskPeopleTv = (TextView) vPopupWindow.findViewById(R.id.pop_metting_ask_people_tv);
		TextView mPopDeletMettingTv = (TextView) vPopupWindow.findViewById(R.id.pop_metting_delet_metting_tv);
		TextView mPopCloseTv = (TextView) vPopupWindow.findViewById(R.id.pop_metting_close_tv);


		mPopAskPeopleTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Message msgs = new Message();
				msgs.what = 0;
				handler.sendMessage(msgs);
				closePw();
			}
		});

		mPopDeletMettingTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Message msgs = new Message();
				msgs.what = 1;
				handler.sendMessage(msgs);
				closePw();
			}
		});
		mPopCloseTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				closePw();
			}
		});
		vPopupWindow.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					closePw();
					return true;
				}
				return false;
			}
		});

		vPopupWindow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				closePw();
			}
		});

	}



		public interface CallBackView {
			void callView(ImageView imageView, TextView textView);
		}

}
