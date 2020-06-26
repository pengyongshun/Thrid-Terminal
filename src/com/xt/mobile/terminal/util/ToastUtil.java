package com.xt.mobile.terminal.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @Author:pengyongshun
 * @Desc:
 * @Time:2018/8/29
 */
public class ToastUtil {
    private static final String TAG = "android";
    public static Exception exception=new Exception();
    // 获得调用者的方法名
    public static String _methodName = exception.getStackTrace()[1].getMethodName();
    // 获得调用者的类名
    public static String _className=exception.getStackTrace()[1].getClassName();
    // 获得当前的方法名
    public static String _thisMethodName = exception.getStackTrace()[0].getMethodName();
    private static Toast toast;
    /**
     * 长时间显示Toast
     * @param context  上下文
     * @param messge 要显示的内容
     */
    public static void showWithShortForLog(Context context,String messge){
        if (_className==null||_methodName==null){
            Log.e(TAG, "showWithShort: 获取类名和方法名失败");
        }
        String content=_className+"."+_methodName+"===>"+messge;
        show(context,content,Toast.LENGTH_LONG);
    }

    /**
     * 短时间显示Toast
     * @param context  上下文
     * @param messge  要显示的内容
     */
    public static void showWithLongForLog(Context context,String messge){
        if (_className==null||_methodName==null){
            Log.e(TAG, "showWithShort: 获取类名和方法名失败");
        }
        String content=_className+"."+_methodName+"===>"+messge;
        show(context,content,Toast.LENGTH_SHORT);
    }
    /**
     * 短时间显示Toast
     * @param context  上下文
     * @param messge 要显示的内容
     */
    public static void showShort(Context context,String messge){
        show(context,messge,Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示Toast
     * @param context  上下文
     * @param messge 要显示的内容
     */
    public static void showShort(Context context,int messge){
        show(context,context.getResources().getString(messge),Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     * @param context  上下文
     * @param messge  要显示的内容
     */
    public static void showLong(Context context,String messge){
        show(context,messge,Toast.LENGTH_LONG);
    }

    /**
     * 长时间显示Toast
     * @param context  上下文
     * @param messge  要显示的内容
     */
    public static void showLong(Context context,int messge){
        show(context,context.getResources().getString(messge),Toast.LENGTH_LONG);
    }


    /**
     * @param context
     * @param content
     */
    private static void show(Context context,
                                 String content,int duration) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    duration);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
