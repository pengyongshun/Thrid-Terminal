package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.domain.SipInfo;

import java.util.ArrayList;

public class ContanctsAdapter extends BaseAdapter {

	private boolean tag;

	private ArrayList<SipInfo> sipInfos;
	private Context context;

	public ContanctsAdapter(Context context, ArrayList<SipInfo> sipInfos, boolean tag) {
		this.context = context;
		this.sipInfos = sipInfos;
		this.tag=tag;
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
		ViewHoder mViewHoder;
		Resources resources = context.getResources();
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_metting_contancts_ask_lv, null);
			//初始化控件
			mViewHoder=new ViewHoder(convertView);
		} else {
			mViewHoder = (ViewHoder) convertView.getTag();
		}

		//填充数据
		final SipInfo info = sipInfos.get(position);
		mViewHoder.tv_name.setText(info.getUsername());
		if (tag){
			//编辑
			mViewHoder.choiceCb.setVisibility(View.VISIBLE);
			boolean choice = info.isSelect();
			if (choice){
				//选中
                mViewHoder.choiceCb.setImageResource(R.drawable.icon_chexbox_pr);
			}else {
                mViewHoder.choiceCb.setImageResource(R.drawable.icon_chexbox_nor);
			}

		}else {
			//查看
			mViewHoder.choiceCb.setVisibility(View.GONE);
		}

		if (info.getType() == SipInfo.TYPE_USE_DIRECTORY
				|| info.getType() == SipInfo.TYPE_DEV_DIRECTORY) {
			//还没有到达指定的人，只是一个目录
			mViewHoder.tv_name.setTextColor(resources.getColor(R.color.contact_inline));
			mViewHoder.tv_status.setVisibility(View.GONE);
			mViewHoder.nextLL.setVisibility(View.VISIBLE);
			mViewHoder.iv_profile.setImageResource(R.drawable.icon_bumen);
			mViewHoder.iv_profile.setEnabled(true);
            mViewHoder.choiceCb.setVisibility(View.GONE);

		} else {
			//到达指定的人
            mViewHoder.choiceCb.setVisibility(View.VISIBLE);
			mViewHoder.tv_status.setVisibility(View.VISIBLE);
			mViewHoder.nextLL.setVisibility(View.GONE);
			if (info.getStatus() == 0) {
				//离线
				mViewHoder.tv_name.setTextColor(resources.getColor(R.color.common_text_color));
				mViewHoder.tv_status.setText("[离线]");
				mViewHoder.tv_status.setTextColor(resources.getColor(R.color.common_text_color));
				mViewHoder.iv_profile.setEnabled(false);

				if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
					mViewHoder.iv_profile.setImageResource(R.drawable.icon_people_offline);
				} else if (info.getType() == SipInfo.TYPE_DEV_RESOURCE) {
                    mViewHoder.iv_profile.setImageResource(R.drawable.icon_divce_offline);
				}
			} else {
				//在线
				mViewHoder.tv_name.setTextColor(resources.getColor(R.color.contact_inline));
				mViewHoder.tv_status.setText("[在线]");
				mViewHoder.tv_status.setTextColor(resources.getColor(R.color.contact_inline));
				mViewHoder.iv_profile.setEnabled(true);

				if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
					mViewHoder.iv_profile.setImageResource(R.drawable.icon_people_online);
				} else if (info.getType() == SipInfo.TYPE_DEV_RESOURCE) {
                    mViewHoder.iv_profile.setImageResource(R.drawable.icon_divce_online);
				}
			}
		}

		return convertView;
	}


	class ViewHoder{
		public TextView tv_name,tv_status;
		public ImageView choiceCb;
		private ImageView iv_profile;
		private LinearLayout nextLL;

		public ViewHoder(View view){
			//设置标签
			view.setTag(this);
			this.nextLL = (LinearLayout) view.findViewById(R.id.item_join_metting_people_list_next_ll);
			this.iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
			this.tv_name = (TextView) view.findViewById(R.id.item_join_metting_people_list_name_tv);
			this.tv_status = (TextView) view.findViewById(R.id.tv_status);
			this.choiceCb = (ImageView) view.findViewById(R.id.item_join_metting_people_list_selet_cb);
		}

	}







}