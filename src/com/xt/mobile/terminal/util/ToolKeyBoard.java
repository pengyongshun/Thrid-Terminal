package com.xt.mobile.terminal.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘弹出与隐藏方法
 * 
 * @author ty
 * @time 2015/5/12
 */
public class ToolKeyBoard {

	/***
	 * 隐藏键盘，并取消View焦点
	 * @param v
	 */
	public static void onHideKeyBoard(View v) {

		v.setFocusable(false);
        v.setFocusableInTouchMode(false);
		v.clearFocus();
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	/***
	 * 弹出键盘，并让View获取焦点
	 * @param v
	 */
	public static void onOpenKeyBoard(View v) {
		
		v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
	}
}
