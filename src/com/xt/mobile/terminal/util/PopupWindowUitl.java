package com.xt.mobile.terminal.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.view.dailog.CommonPopupWindow;


/**
 * Created by 彭永顺 on 2018/8/16.
 */
public class PopupWindowUitl {
    public static final int TRANSLATE= R.style.animTranslate;

    private Activity context;
    private CommonPopupWindow window;
    private Window window1;
    private int style=TRANSLATE;
    private Drawable backGoup=new ColorDrawable(Color.TRANSPARENT);
    private PopupWindowCall popupWindowCall;


    public PopupWindowUitl(Activity context, PopupWindowCall popupWindowCall) {
        this.context = context;
        this.popupWindowCall = popupWindowCall;
    }

    /**
     * 创建PopupWindow
     * @param layout
     */
    public void creatPopupWindow(int layout) {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        window1 = context.getWindow();
        // create popup window
        window=new CommonPopupWindow(context, layout, ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight*0.7)) {
            @Override
            protected void initView() {
                View view=getContentView();
                popupWindowCall.initView(view);
            }

            /**
             * 事件处理
             */
            @Override
            protected void initEvent() {
               popupWindowCall.initEvent();
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance=getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp=window1.getAttributes();
                        lp.alpha=1.0f;
                        window1.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        window1.setAttributes(lp);
                    }
                });
            }
        };
        setSetting(window);
    }

    /**
     * PopupWindow设置
     * @param window
     */
    private void setSetting(CommonPopupWindow window){
        PopupWindow popupWindow = window.getPopupWindow();
//             // 创建PopupWindow对象，其中：
//             // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
//             // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
//             PopupWindow window=new PopupWindow(contentView, 100, 100, true);

             // 设置PopupWindow是否能响应外部点击事件
             popupWindow.setOutsideTouchable(true);
             // 设置PopupWindow是否能响应点击事件
             popupWindow.setTouchable(true);
             // 显示PopupWindow，其中：
             // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
             // popupWindow.showAsDropDown(anchor, xoff, yoff);
             // 或者也可以调用此方法显示PopupWindow，其中：
             // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
             // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
            // window.showAtLocation(parent, gravity, x, y);
    }


    /**
     * 关闭PopupWindow
     */
    public void dissWindow(){
        if (window!=null &&
                (window.getPopupWindow().isShowing())
                &&(window.getPopupWindow()!=null)){
            window.getPopupWindow().dismiss();
        }

    }

    /**
     * 关闭PopupWindow
     */
    public void showWindow(View view ,int gravity, int x, int y ){
        if (window!=null &&
                (!(window.getPopupWindow().isShowing()))
                &&(window.getPopupWindow()!=null)){
            // 设置PopupWindow的背景
            window.getPopupWindow().setBackgroundDrawable(backGoup);
            window.getPopupWindow().setAnimationStyle(style);
            //定位
            window.showAtLocation(view,gravity,x,y);
            WindowManager.LayoutParams lp=window1.getAttributes();
            lp.alpha=0.3f;
            window1.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window1.setAttributes(lp);

        }

    }

    /**
     * 设置PopupWindow背景
     * @param background
     */
    public void setBackgroundDrawable(Drawable background){
        if (window!=null &&(window.getPopupWindow()!=null)){
           this.backGoup=background;
        }
    }

    /**
     * 设置PopupWindow动画风格
     * @param style
     */
    public void setAnimationStyle(int style){
        if (window!=null &&(window.getPopupWindow()!=null)){
            this.style=style;
        }

    }

    /**
     * PopupWindow是否展示
     * @return
     */
    public boolean isShowing(){
        if (window!=null &&(window.getPopupWindow()!=null)){
            PopupWindow popupWindow = window.getPopupWindow();
            boolean showing = popupWindow.isShowing();
            return showing;
        }else {
            return false;
        }
    }


    /**
     * 获取PopupWindow对象
     * @return
     */
    public PopupWindow getPopupWindow(){
        if (window!=null ){
            PopupWindow popupWindow = window.getPopupWindow();
            return popupWindow;
        }else {
            return null;
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context,float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    /**
     * 设置window消失监听事件
     * @param listener
     */
    public void setDissWindowListener(final DissPopupWindowListener listener){
        getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                listener.onDiss();
            }
        });
    }


    public interface PopupWindowCall{
        void initView(View view);
        void initEvent();
    }

    public interface DissPopupWindowListener{
        void onDiss();

    }

    public CommonPopupWindow getWindow(){
        if (window !=null){
            return window;
        }else {
            return null;
        }
    }




}
