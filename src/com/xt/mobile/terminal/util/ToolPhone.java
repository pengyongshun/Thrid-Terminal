package com.xt.mobile.terminal.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


public class ToolPhone {

	private TelephonyManager telephonyManager = null;
	private Context context = null;
	private static ToolPhone instance = null;

	public static ToolPhone getInstance(Context context) {
		if (instance == null) {
			instance = new ToolPhone(context);
		}
		return instance;
	}

	public ToolPhone(Context context) {

		this.context = context;
		telephonyManager = (TelephonyManager) this.context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	/***
	 * 判断是否手机号
	 */
	public boolean isMobileNum(String mobiles) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/***
	 * 获取手机号
	 * @return String
	 */
	public String getPhoneNo() {
		String phoneNo = telephonyManager.getLine1Number();
		return phoneNo;
	}
	
	/***
	 * 获取IMSI
	 * @return String
	 */
	public String getIMSI() {
		String imsi = telephonyManager.getSubscriberId();
		return imsi;
	}
	
	/***
	 * 获取IMEI
	 * @return String
	 */
	public String getIMEI() {
		String imei = telephonyManager.getDeviceId();
		return imei;
	}
	
	/**
	 * 获取机型
	 */
	public String getDeviceModel() {

		return android.os.Build.MODEL;
	}
	
	/**
	 * 获取品牌
	 */
	public String getDeviceBrand() {

		return android.os.Build.BRAND;
	}

	/**
	 * 获取SDK版本
	 */
	public int getDeviceSDKVersion() {

		return android.os.Build.VERSION.SDK_INT;
	}
	
	/**
	 * 获取设备版本号
	 */
	public String getDeviceID() {

		return android.os.Build.ID;
	}
	
	/**
	 * 获取设备显示版本包
	 */
	public String getDeviceDisplay() {

		return android.os.Build.DISPLAY;
	}
	
	/**
	 * 获取设备产品名
	 */
	public String getDeviceProduct() {

		return android.os.Build.PRODUCT;
	}
	
	/**
	 * 获取操作系统
	 */
	public String getDeviceOS() {
		
		return "Android" + android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取屏幕宽高
	 */
	public int[] getScreenSize(){
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();	
		if (dm == null) {
			return null;
		}
		int[] size = {dm.widthPixels,dm.heightPixels};
		return size;
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
	 */
	public int getStatusBarHeight(){ 
        Class<?> c = null; 
        Object obj = null; 
        Field field = null;
        
        int x = 0, statusBarHeight = 0;
        
        try { 
            c = Class.forName("com.android.internal.R$dimen"); 
            obj = c.newInstance(); 
            field = c.getField("status_bar_height"); 
            x = Integer.parseInt(field.get(obj).toString()); 
            statusBarHeight = context.getResources().getDimensionPixelSize(x);  
        } catch (Exception e1) { 
            e1.printStackTrace(); 
        }  
        
        return statusBarHeight; 
    }
}
