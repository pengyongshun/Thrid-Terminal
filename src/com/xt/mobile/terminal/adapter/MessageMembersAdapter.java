package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.domain.SipInfo;

import java.util.ArrayList;
import java.util.List;

public class MessageMembersAdapter extends BaseAdapter {
    private List<SipInfo> mMemberList = new ArrayList<SipInfo>();
    private LayoutInflater mLayoutInflater;
    private Context context;

    public MessageMembersAdapter(Context context, List<SipInfo> messageList) {
        this.mMemberList = messageList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMemberList == null ? 0 : mMemberList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_message_member, null);
            mViewHolder = new ViewHolder(convertView);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        //填充数据
        final SipInfo member = mMemberList.get(position);
        mViewHolder.setMessage(member);
        return convertView;
    }

    class ViewHolder {
        public ImageView ivSender;
        public TextView tvUser;

        public ViewHolder(View view) {
            view.setTag(this);
            this.ivSender = view.findViewById(R.id.iv_message_member_face);
            this.tvUser = view.findViewById(R.id.tv_message_member_user);
        }

        public void setMessage(SipInfo member) {
            this.tvUser.setText(member.getUsername());
        }
    }
}
