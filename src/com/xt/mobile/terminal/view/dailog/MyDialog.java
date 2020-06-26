package com.xt.mobile.terminal.view.dailog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xt.mobile.terminal.R;

public class MyDialog extends Dialog implements OnClickListener
{

	private Button bt_positive;
	private Button bt_negative;
	private View ll_button;
	private TextView tv_msg;
	boolean showButton = false;
	String positiveText;
	String negativeText;
	String msg;
	private View mBtnLine;

	public MyDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener)
	{
		super(context, cancelable, cancelListener);
	}

	public MyDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public MyDialog(Context context)
	{
		this(context, R.style.MyDialog);
		setCancelable(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_dialog);
		initView();
	}

	private void initView()
	{
		ll_button = findViewById(R.id.ll_button);
		mBtnLine = findViewById(R.id.mBtnLine);
		tv_msg = (TextView) findViewById(R.id.tv_msg);
		bt_positive = (Button) findViewById(R.id.bt_positive);
		bt_negative = (Button) findViewById(R.id.bt_negative);

		if (msg != null)
		{
			tv_msg.setText(msg);
		}
		if (showButton)
		{
			int num = 0;
			ll_button.setVisibility(View.VISIBLE);
			if (positiveText != null)
			{
				bt_positive.setText(positiveText);
				bt_positive.setVisibility(View.VISIBLE);
				num++;
			}
			else
			{
				bt_positive.setVisibility(View.GONE);
			}
			if (negativeText != null)
			{
				bt_negative.setText(negativeText);
				bt_negative.setVisibility(View.VISIBLE);
				num++;
			}
			else
			{
				bt_negative.setVisibility(View.GONE);
			}
			if (num < 2)
			{
				mBtnLine.setVisibility(View.GONE);
			}
		}
		bt_positive.setOnClickListener(this);
		bt_negative.setOnClickListener(this);
	}

	public static final int POSITIVE_BUTTON = 1;
	public static final int NEGATIVE_BUTTON = 2;

	public interface ButtonClickListener
	{
		public void onClick(MyDialog dialog, int which);
	}

	private ButtonClickListener positiveClick;
	private ButtonClickListener negativeClick;

	public MyDialog setMessage(String text)
	{
		msg = text;
		return this;
	}

	public MyDialog setPositiveButton(String text, ButtonClickListener listener)
	{
		if (text != null)
		{
			showButton = true;
			positiveText = text;
		}
		positiveClick = listener;
		return this;
	}

	public MyDialog setNegativeButton(String text, ButtonClickListener listener)
	{
		if (text != null)
		{
			showButton = true;
			negativeText = text;
		}
		negativeClick = listener;
		return this;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bt_positive) {
			if (positiveClick != null)
			{
				positiveClick.onClick(MyDialog.this, POSITIVE_BUTTON);
			}
			dismiss();
		}else if (id == R.id.bt_positive){
			if (negativeClick != null)
			{
				negativeClick.onClick(MyDialog.this, NEGATIVE_BUTTON);
			}
			dismiss();
		}

	}
}