package com.xt.mobile.terminal;

import android.app.Application;
import android.content.Context;


public class XTApplication extends Application {

	public static Context mAppContext;

	@Override
	public void onCreate() {
		super.onCreate();
		InitXTMobileLibrary.getInstans(this).init();
		mAppContext = this;
		//打印地址
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getmAppContext() {
		return mAppContext;
	}

}