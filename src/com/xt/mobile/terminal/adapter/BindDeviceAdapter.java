package com.xt.mobile.terminal.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.domain.SipInfo;

public class BindDeviceAdapter extends BaseAdapter {

	public class Holder {
		ImageView iv_profile;
		TextView tv_name;
		CheckBox cb_select;
	}

	private ArrayList<SipInfo> sipInfos;
	private Context context;
	private int mPosition = -1;

	public BindDeviceAdapter(Context context, ArrayList<SipInfo> sipInfos) {
		this.context = context;
		this.sipInfos = sipInfos;
	}

	public ArrayList<SipInfo> getSipInfos() {
		return sipInfos;
	}

	public void setSipInfos(ArrayList<SipInfo> sipInfos) {
		this.sipInfos = sipInfos;
	}

	public void setSelect(int position) {
		this.mPosition = position;
	}

	@Override
	public int getCount() {
		return sipInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return sipInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		Holder holder;
		Resources resources = context.getResources();
		if (view == null) {
			view = View.inflate(context, R.layout.lv_bind_device_item, null);
			holder = new Holder();
			holder.iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			holder.cb_select = (CheckBox) view.findViewById(R.id.cb_select);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		final SipInfo info = sipInfos.get(position);
		holder.tv_name.setText(info.getUsername());
		holder.tv_name.setTextColor(resources.getColor(R.color.common_text_color));
		holder.iv_profile.setImageResource(R.drawable.contact_device_offline);
		holder.iv_profile.setEnabled(false);

		if (position == mPosition) {
			holder.cb_select.setBackgroundResource(R.drawable.setting_msg_press);
			holder.cb_select.setVisibility(View.VISIBLE);
		} else {
			holder.cb_select.setBackgroundResource(0);
			holder.cb_select.setVisibility(View.GONE);
		}

		return view;
	}

}