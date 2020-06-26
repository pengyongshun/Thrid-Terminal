package com.xt.mobile.terminal.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.domain.SipInfo;

public class ScrollDirectoryAdapter extends BaseAdapter {

	public class DirHolder {
		public int position;
		private TextView tv_name;
	}
	
	private ArrayList<SipInfo> sipInfos;
	private Context context;

	public ScrollDirectoryAdapter(Context context, ArrayList<SipInfo> sipInfos) {
		this.context = context;
		this.sipInfos = sipInfos;
	}

	public ArrayList<SipInfo> getSipInfos() {
		return sipInfos;
	}

	public void setSipInfos(ArrayList<SipInfo> sipInfos) {
		this.sipInfos = sipInfos;
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
		DirHolder holder;
		if (view == null) {
			view = View.inflate(context, R.layout.lv_scroll_directory_item, null);
			holder = new DirHolder();
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			view.setTag(holder);
		} else {
			holder = (DirHolder) view.getTag();
		}
		
		final SipInfo info = sipInfos.get(position);
		if (position == sipInfos.size() - 1) {
			holder.tv_name.setTextColor(Color.BLACK);
			holder.tv_name.setText(info.getUsername());
		} else {
			holder.tv_name.setTextColor(Color.GRAY);
			holder.tv_name.setText(info.getUsername() + " >");
		}

		return view;
	}

}