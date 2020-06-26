package com.xt.mobile.terminal.util;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;


/**
 * 类名        ：ActivityTools
 *
 * 描述        ：Activity中处理工具
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class ActivityTools {

    // 运行的最小版本号 4.0
    public static final int DEFAULT_API_VERSION = 14;

    public static int request_code_9 = 9;
    public static int request_code_8 = 8;
    public static int request_code_7 = 7;
    public static int request_code_6 = 6;
    public static int request_code_5 = 5;
    public static int request_code_4 = 4;
    public static int request_code_3 = 3;
    public static int request_code_2 = 2;
    public static int request_code_1 = 1;


    /*
     * 消失view调用动画 R.anim.push_left_out
     */
    public static void startAnimation(final Context context, final View view, final int resourcesId) {
        if (context == null || view == null) {
            return;
        }
        view.startAnimation(AnimationUtils.loadAnimation(context, resourcesId));
        view.setVisibility(View.GONE);

    }

    // add 按钮的旋转动画
    private static RotateAnimation mAddOpenAnim = new RotateAnimation(0, +45,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
    private static RotateAnimation mAddCloseAnim = new RotateAnimation(+45, 0,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);

    /**
     * @param isOpen
     * @param view
     * @param animTime 动画持续时间
     */
    public static void loadAddAnimationBase(boolean isOpen, View view, int animTime) {
        int time = animTime;
        if (view != null) {
            if (isOpen) {
                mAddOpenAnim.setDuration(time);
                mAddOpenAnim.setInterpolator(new AccelerateInterpolator());
                mAddOpenAnim.setFillAfter(true);

                mAddOpenAnim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // mAddOpenAnim = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                });
                view.startAnimation(mAddOpenAnim);
            } else {
                mAddCloseAnim.setDuration(time);
                mAddCloseAnim.setInterpolator(new AccelerateInterpolator());
                mAddCloseAnim.setFillAfter(true);

                mAddCloseAnim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // mAddCloseAnim = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                });

                view.startAnimation(mAddCloseAnim);
            }
        }

    }

    public static void loadAddAnimation(boolean isOpen, View view) {
        loadAddAnimationBase(isOpen, view, 200);
    }



    /**
     * 控件的隐藏与显示
     * 
     * @param v
     * @param b true 为隐藏否则为显示
     */
    public static void setViewState(View v, boolean b) {
        if (b) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 公用控件隐藏
     *
     * @param jh
     * @param position
     */
    public static void setHideShow(RelativeLayout[] jh, int position) {
        for (int i = 0; i < jh.length; i++) {
            if (position == i) {
                jh[i].setVisibility(View.VISIBLE);
            } else {
                jh[i].setVisibility(View.GONE);
            }
        }
    }



    /**
     * 设置删除线
     * 
     * @param context
     * @param value
     * @return
     */
    public static void setTextLinear(Context context, TextView textView, String value, int color) {
        SpannableString ss = new SpannableString(value);
        ss.setSpan(new StrikethroughSpan(), 0, value.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setTextColor(color);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

////////////////////////////////界面跳转///////////////////////////////

    /***
     * 外部跳转方法（带值）
     * 
     * @param
     * @param appPackage 当前appid(包路径)字串
     * @param appActivity 目标 activity 字串
     */
    public static void externalJump(Context context, String appPackage,
            String appActivity, String data, String key) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(appPackage, appActivity));
            intent.putExtra("data", data);
            intent.putExtra("key", key);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 外部跳转方法(不带值)
     *
     * @param
     * @param appPackage 当前appid(包路径)字串
     * @param appActivity 目标 activity 字串
     */
    public static void externalJump(Context context, String appPackage,
                                    String appActivity) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(appPackage, appActivity));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @描述：内部之间的跳转 无值跳转<br>
     * @param context 当前activity
     * @param obj 目标activity 传class进来
     * @param finish 是否关闭
     */
    public static void startActivity(Context context, Class<?> obj, boolean finish) {
        try {
            Intent intent = new Intent();
            intent.setClass(context, obj);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(
                    R.anim.push_left_in,
                    R.anim.push_left_out);
            if (finish) {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @描述：内部之间的跳转 带Bundle传值<br>
     * @param context 当前activity
     * @param obj 目标activity字符串
     * @param bundle 数据
     */
    public static void startActivity(Context context, Class<?> obj, Bundle bundle, boolean finish) {
        try {
            Intent intent = new Intent();
            intent.setClass(context, obj);
            intent.putExtra("bundle",bundle);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(
                    R.anim.push_left_in,
                    R.anim.push_left_out);
            if (finish) {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @描述：内部之间的跳转，没有返回值 <br>
     * @param context 当前activity
     * @param obj 目标activity字符串
     * @param bundle 传递时附带的参数
     * @param finish 是否关闭
     */
    public static void startActivity(Context context, Class<?> obj, Bundle bundle,
            boolean finish, int enterAnim, int exitAnim) {
        try {
            Intent intent = new Intent(context, obj);
            intent.putExtra("bundle",bundle);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(enterAnim, exitAnim);
            if (finish) {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @描述：内部之间的跳转，没有返回值 <br>
     * @param context 当前activity
     * @param obj 目标activity字符串
     * @param finish 是否关闭
     */
    public static void startActivity(Context context, Class<?> obj,
                                     boolean finish, int enterAnim, int exitAnim) {
        try {
            Intent intent = new Intent(context, obj);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(enterAnim, exitAnim);
            if (finish) {
                ((Activity) context).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//////////////////////////////////handler传值////////////////////////////
    /**
     * 发送消息
     * 
     * @param handler
     * @param flag
     */
    public static void sendMessage(Handler handler, int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    /**
     * 发送消息 延迟
     * 
     * @param handler
     * @param flag
     * @param time
     */
    public static void sendMessage(Handler handler, int flag, long time) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessageDelayed(msg, time);
    }

    /**
     * 发送消息 可以带参数
     * 
     * @param handler
     * @param flag
     * @param obj
     */
    public static void sendMessage(Handler handler, int flag, Object obj) {
        Message msg = new Message();
        msg.what = flag;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    /**
     * 发送消息数据传值用bundle
     * 
     * @param handler
     * @param flag
     * @param bundle
     */
    public static void sendMessage(Handler handler, int flag, Bundle bundle) {
        Message msg = new Message();
        msg.what = flag;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
//////////////////////////////功能///////////////////////////////

    /*
     * 模拟home键
     */
    public static void simulationHome(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /*
     * 从luach启动
     */
    public static void homeStart(Context context, String packageName) {
        if (context == null) {
            return;
        }
        Intent intent = null;
        if (!TextUtils.isEmpty(packageName)) {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        } else {
            intent = context.getPackageManager().getLaunchIntentForPackage("com.sinosun.tchats");
        }
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    ///////////////////////////////////广播和service///////////////////////
    /**
     * 消息来后通知广播 key什么类型的广播
     *
     * @param context
     */
    public static void sendMessageBroadCast(Context context, String key) {
        Intent i = new Intent(key);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /*
     * 发送原始广播
     */
    public static void sendBroadCast(Context context, String key) {
        Intent i = new Intent(key);
        context.sendBroadcast(i);
    }

    /**
     * 消息来后通知广播 key什么类型的广播
     *
     * @param context 带参数的
     */
    public static void sendMessageBroadCast(Context context,
                                            String broadCastName, String key) {
        Intent i = new Intent(broadCastName);
        i.putExtra("key", key);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /**
     * 消息来后通知广播 key什么类型的广播
     *
     * @param context 带参数的
     */
    public static void sendMessageBroadCast2(Context context,
                                             String broadCastName, String key, String value) {
        Intent i = new Intent(broadCastName);
        i.putExtra("key", key);
        i.putExtra("value", value);
        context.sendBroadcast(i);
    }

    /**
     * 消息来后通知广播 key什么类型的广播
     *
     * @param context 带参数的
     */
    public static void sendMessageBroadCast1(Context context,
                                             String broadCastName, String key, String value, String isShow) {
        Intent i = new Intent(broadCastName);
        i.putExtra("key", key);
        i.putExtra("value", value);
        i.putExtra("isbackShow", isShow);
        context.sendBroadcast(i);
    }

    /***
     * 重启服务
     *
     * @param context "com.cjsc.PlatformService"
     */
    public static void onReStartService(Context context, String key) {
        try {
            Intent intent = new Intent(key);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 停止服务
     *
     * @param context "com.cjsc.PlatformService"
     */
    public static void onStopService(Context context, String key) {
        try {
            Intent intent = new Intent(key);
            context.stopService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
