package com.xt.mobile.terminal.adapter;

import java.util.ArrayList;

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
import com.xt.mobile.terminal.ui.fragment.FragmentContacts;

public class ResourceListAdapter extends BaseAdapter {

	public class Holder {
		ImageView iv_profile;
		TextView tv_name;
		TextView tv_status;
		LinearLayout ll_back;
	}

	private ArrayList<SipInfo> sipInfos;
	private Context context;


	public ResourceListAdapter(Context context, ArrayList<SipInfo> sipInfos) {
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
		Holder holder;
		Resources resources = context.getResources();
		if (view == null) {
			view = View.inflate(context, R.layout.lv_contacts_item, null);
			holder = new Holder();
			holder.iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
			holder.ll_back = (LinearLayout) view.findViewById(R.id.item_join_metting_people_list_next_ll);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		final SipInfo info = sipInfos.get(position);
		holder.tv_name.setText(info.getUsername());

		if (info.getType() == SipInfo.TYPE_USE_DIRECTORY
				|| info.getType() == SipInfo.TYPE_DEV_DIRECTORY) {
			holder.tv_name.setTextColor(resources.getColor(R.color.contact_inline));
			holder.tv_status.setVisibility(View.GONE);
			holder.ll_back.setVisibility(View.VISIBLE);

			if (FragmentContacts.MODE_CATCH == FragmentContacts.MODE_Person) {
				//人员
				holder.iv_profile.setImageResource(R.drawable.icon_bumen);
			} else if (FragmentContacts.MODE_CATCH == FragmentContacts.MODE_Device) {
				//设备
				holder.iv_profile.setImageResource(R.drawable.icon_divce_group);
			}

			holder.iv_profile.setEnabled(true);
		} else {
			holder.tv_status.setVisibility(View.VISIBLE);
			holder.ll_back.setVisibility(View.GONE);
			if (info.getStatus() == 0) {
				holder.tv_name.setTextColor(resources.getColor(R.color.common_text_color));
				holder.tv_status.setText("[离线]");
				holder.tv_status.setTextColor(resources.getColor(R.color.common_text_color));
				holder.iv_profile.setEnabled(false);

				if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
					holder.iv_profile.setImageResource(R.drawable.icon_people_offline);
				} else if (info.getType() == SipInfo.TYPE_DEV_RESOURCE) {
					holder.iv_profile.setImageResource(R.drawable.icon_divce_offline);
				}
			} else {
				holder.tv_name.setTextColor(resources.getColor(R.color.contact_inline));
				holder.tv_status.setText("[在线]");
				holder.tv_status.setTextColor(resources.getColor(R.color.contact_inline));
				holder.iv_profile.setEnabled(true);

				if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
					holder.iv_profile.setImageResource(R.drawable.icon_people_online);
				} else if (info.getType() == SipInfo.TYPE_DEV_RESOURCE) {
					holder.iv_profile.setImageResource(R.drawable.icon_divce_online);
				}
			}
		}

		return view;
	}

}