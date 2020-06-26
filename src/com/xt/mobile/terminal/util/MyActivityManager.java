

package com.xt.mobile.terminal.util;

import android.app.Activity;

import java.util.Stack;

/**
 * 类名        ：MyActivityManager
 *
 * 描述        ：activity管理器
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class MyActivityManager {

    private Stack<Activity> activityStack;
    private static MyActivityManager instance;

    private MyActivityManager() {

    }

    public static MyActivityManager getActivityManager() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    // 退出栈顶
    public  void removeActivityFromStack(Activity activity) {
        try {
            if (activity != null && activityStack != null && activityStack.contains(activity)) {
              //  activity.finish();
                activityStack.remove(activity);
                activity = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void popActivity(Activity activity){
        if(activity != null){
            if(activityStack != null && activityStack.contains(activity)) {
                activityStack.remove(activity);
            }
            activity.finish();
            activity = null ;
        }
    }
    
    
    public void popCurretentActivity(){
    	 Activity activity = currentActivity();
         if (activity == null) {
              return ;
         }
         popActivity(activity);
    }
    

    // 获得当前栈顶
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null && !activityStack.empty()) {
            activity = (Activity) activityStack.lastElement();
        }
        return activity;
    }

    // 当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    
    public boolean isMainActivityContains(){
    	boolean ret = false ;
    	if (activityStack != null && !activityStack.empty()) {
			int size = activityStack.size();
			for (int i = size - 1; i >= 0; i--) {
				Activity ac = activityStack.elementAt(i);
				if (ac != null && ac.getClass().getSimpleName().equals("MainActivity")) {
					ret = true;
					break;
				}
			}

		}
    	return ret ;
    }
    
    /*
     * 判断当前activity是否在栈中
     */
    public boolean isActivityContains(String className){
    	boolean ret = false ;
    	if (activityStack != null && !activityStack.empty()) {
			int size = activityStack.size();
			for (int i = size - 1; i >= 0; i--) {
				Activity ac = activityStack.elementAt(i);
				if (ac != null && ac.getClass().getSimpleName().equals(className)) {
					ret = true;
					break;
				}
			}
		}
    	return ret ;
    }
    

    // 退出所有Activity
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }
}
