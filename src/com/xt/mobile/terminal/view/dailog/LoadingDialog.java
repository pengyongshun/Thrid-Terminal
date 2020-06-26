
package com.xt.mobile.terminal.view.dailog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.xt.mobile.terminal.R;


/**
 * 类名        ：LoadingDialog
 *
 * 描述        ：loding菊花弹框
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class LoadingDialog extends AlertDialog {
    private String TAG ="LoadingDialog";
    private Activity mAttachActivity ; // 当前activity 依附的activity
    private int reid ;
    public LoadingDialog(Activity context) {
        super(context);
        init(context);
    }
	 public LoadingDialog(Activity context, int theme){
		 super(context, theme);
		  init(context);
	 }

    /**
     * @param context
     * @param theme 设置样式
     */
    public LoadingDialog(Activity context, int theme, int type){
        super(context, theme);
        Log.d(TAG, "LoadingDialog: ");
        init(context);
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: ");
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                {
                    if(checkIsAttachActivityIsActive()){
                        Log.d(TAG, "onKey: back");
                        mAttachActivity.onKeyDown(keyCode,event);
                        return false;
                    }
                }
                return false;
            }

        });
    }

    private void init(Activity ctx){
    	mAttachActivity= ctx;
    }
    
    // 检查dialog 依附的activity 是否是有效 存在
    public boolean checkIsAttachActivityIsActive() {
    	if(mAttachActivity != null && !mAttachActivity.isFinishing()) {
    		return true;
    	}else {
    		return false ;
    	}
    }

    /**
     * 需要调supper.dismiss();不然不会关闭弹出框
     */
    @Override
    public void dismiss(){
        Log.d(TAG, "dismiss: ");
     
        if (isShowing() && checkIsAttachActivityIsActive()) {
            super.dismiss();
        }

    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(reid);

        if (isSetTipContent) {

            TextView content = (TextView) this.findViewById(R.id.tipContent);
            if (content != null) {
                if (resid != 0) {
                    content.setText(resid);
                } else {
                    if (!TextUtils.isEmpty(tipContent)) {
                        content.setText(tipContent);
                    }
                }

            }
        }
    }

    public void setDialogLayout(int id) {
        reid = id;
    }

    private int resid;
    private boolean isSetTipContent = false;
    private String tipContent;

    public void setDialogCanceable(boolean cancel) {
        this.setCancelable(cancel);
    }

    public void setTipContent(int stringID) {
        isSetTipContent = true;
        resid = stringID;
    }
    public void setTipContent(String content) {
        isSetTipContent = true;
        tipContent = content;
    }

}
