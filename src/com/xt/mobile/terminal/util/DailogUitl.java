package com.xt.mobile.terminal.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.dailog.CustomDialog;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xt.mobile.terminal.view.wheelview.WiDateTimePickerDialog;

/**
 * Created by 彭永顺 on 2020/5/7.
 */
public class DailogUitl {
    /**
     * 有输入框的对话框
     */
    public static CustomDialog initDialog(Context context, String title,
                                          final DialogFace listenerSure)
    {
        final CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("");
        builder.setPositiveButton("取消", null);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClearEditText clearEditText = builder.getClearEditText();
                String result = clearEditText.getText().toString();
                listenerSure.onClick(dialog,which,clearEditText);
            }
        });
        CustomDialog dialog = builder.createDialog();
//        WindowManager m = getWindowManager();
//        //为获取屏幕宽、高
//        Display d = m.getDefaultDisplay();
//        //获取对话框当前的参数值
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
//        p.height = (int) (d.getHeight() * 0.25);   //高度设置为屏幕的0.3
//        p.width = (int) (d.getWidth() * 0.75);    //宽度设置为屏幕的0.5
//        dialog.getWindow().setAttributes(p);     //设置生效
        return dialog;
    }


    /**
     * 没有输入框的对话框
     */
    public static CustomTextDialog initTextDialog(Context context, String title,String messge,
                                                  String btnText,
                                                  DialogInterface.OnClickListener onClickListener)
    {
        CustomTextDialog.Builder builder = new CustomTextDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(messge);
        builder.setPositiveButton("取消", null);
        builder.setNegativeButton(btnText, onClickListener);
        CustomTextDialog dialog = builder.create();
//        WindowManager m = getWindowManager();
//        //为获取屏幕宽、高
//        Display d = m.getDefaultDisplay();
//        //获取对话框当前的参数值
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
//        p.height = (int) (d.getHeight() * 0.25);   //高度设置为屏幕的0.3
//        p.width = (int) (d.getWidth() * 0.75);    //宽度设置为屏幕的0.5
//        dialog.getWindow().setAttributes(p);     //设置生效
        return dialog;
    }


    /**
     * 没有输入框的对话框
     */
    public static CustomTextDialog initTextDialog(Context context, String title,String messge,
                                                  String cancleText,String btnText, DialogInterface.OnClickListener cancleListener,
                                                  DialogInterface.OnClickListener sureListener)
    {
        CustomTextDialog.Builder builder = new CustomTextDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(messge);
        builder.setPositiveButton(cancleText, cancleListener);
        builder.setNegativeButton(btnText, sureListener);
        CustomTextDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
//        WindowManager m = getWindowManager();
//        //为获取屏幕宽、高
//        Display d = m.getDefaultDisplay();
//        //获取对话框当前的参数值
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
//        p.height = (int) (d.getHeight() * 0.25);   //高度设置为屏幕的0.3
//        p.width = (int) (d.getWidth() * 0.75);    //宽度设置为屏幕的0.5
//        dialog.getWindow().setAttributes(p);     //设置生效
        return dialog;
    }

    /**
     * 时间选择对话框
     * type=1---->年月日时分
     * type=2---->年月日
     */
    public static WiDateTimePickerDialog initPickTimeDialog(Context context,WiDateTimePickerDialog.
            DateTimePickerListener listener , int type){
       WiDateTimePickerDialog wiDateTimePickerDialog = new WiDateTimePickerDialog(context);
       wiDateTimePickerDialog.setmShowType(type);
       wiDateTimePickerDialog.setDateTimePickerListener(listener);
      return wiDateTimePickerDialog;
    }

    /**
     * 分组会议更多的弹出框
     */
    public static PopupWindowUitl initMoreGroupPopwindow(Activity activity,
                                                    PopupWindowUitl.PopupWindowCall popupWindowCall){
        PopupWindowUitl popupWindowUitl=new PopupWindowUitl(activity,popupWindowCall);
        popupWindowUitl.creatPopupWindow(R.layout.popup_list_group_metting_more);//创建
        return popupWindowUitl;
    }

    /**
     * 会议中更多的弹出框
     * type=1---->年月日时分
     * type=2---->年月日
     */
    public static PopupWindowUitl initMorePopwindow(Activity activity,
                                                    PopupWindowUitl.PopupWindowCall popupWindowCall){
        PopupWindowUitl popupWindowUitl=new PopupWindowUitl(activity,popupWindowCall);
        popupWindowUitl.creatPopupWindow(R.layout.popup_list_metting_more);//创建
        return popupWindowUitl;
    }

    /**
     * 会议成员列表
     * type=1---->年月日时分
     * type=2---->年月日
     */
    public static PopupWindowUitl initJoinMettingEditPopwindow(Activity activity,
                                                    PopupWindowUitl.PopupWindowCall popupWindowCall){
        PopupWindowUitl popupWindowUitl=new PopupWindowUitl(activity,popupWindowCall);
        popupWindowUitl.creatPopupWindow(R.layout.popup_list_join_metting_edit);//创建
        return popupWindowUitl;
    }


    public  interface DialogFace{

        void onClick(DialogInterface dialog, int which ,ClearEditText clearEditText );
    }

    public  interface TextDialogFace{

        void sure(DialogInterface dialog, int which );
        void cancle(DialogInterface dialog, int which );
    }
}
