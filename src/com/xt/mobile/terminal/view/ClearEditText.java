package com.xt.mobile.terminal.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xt.mobile.terminal.R;


public class ClearEditText extends EditText implements OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable; 
    private OnAfterTextChanged mOnAfterTextChanged;
    private OnBeforeTextChanged mOnBeforeTextChanged;
    private OnTextChanged mOnTextChanged;
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) {
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public void addOnAfterTextChanged(OnAfterTextChanged onAfterTextChanged) {
        mOnAfterTextChanged = onAfterTextChanged;
    }
    
    public void addOnBeforeTextChanged(OnBeforeTextChanged onBeforeTextChanged) {
        mOnBeforeTextChanged = onBeforeTextChanged;
    }
    public void addOnTextChanged(OnTextChanged onTextChanged) {
        mOnTextChanged = onTextChanged;
    }
    private void init() {
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
            mClearDrawable = getResources().getDrawable(R.drawable.icon_delet);
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 
 

    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInputFromWindow(this.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
                } 
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 

    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
        setClearIconVisible(hasFocus ? getText().length() > 0 : false);
        } 
 
 

    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    } 
     
    

    @Override 
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        setClearIconVisible(s.length() > 0); 
        if (mOnTextChanged != null) mOnTextChanged.textChanged(s, start, count, after);
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mOnBeforeTextChanged != null) mOnBeforeTextChanged.beforeTextChanged(s, start, count, after);
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
        if (mOnAfterTextChanged != null) mOnAfterTextChanged.afterTextChanged(s.toString());
    } 
    
   

    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    

    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
    public interface OnAfterTextChanged {
        void afterTextChanged(String text);
    }
    public interface OnBeforeTextChanged {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
    }
    public interface OnTextChanged {
        void textChanged(CharSequence s, int start, int count, int after);
    }
}
