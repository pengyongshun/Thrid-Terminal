package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.MessageSessionBean;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.ui.activity.ActivityIm;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.view.SlidingDeleteView;

import java.util.ArrayList;
import java.util.List;

public class MessageSessionListAdapter extends BaseAdapter {
    private List<MessageSessionBean> mMessageSessionList = new ArrayList<MessageSessionBean>();
    private LayoutInflater mLayoutInflater;
    private Context context;
    private OnDataEmptyCallBack onDataEmptyCallBack;

    public MessageSessionListAdapter(Context context, List<MessageSessionBean> messageList) {
        this.mMessageSessionList = messageList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMessageSessionList == null ? 0 : mMessageSessionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageSessionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_message_sessions, null);
            mViewHolder = new ViewHolder(convertView);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final MessageSessionBean msgSession = mMessageSessionList.get(position);
        mViewHolder.setMessage(msgSession);
        mViewHolder.setSessionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goIMBySession(msgSession);
            }
        });

        mViewHolder.setRemoveOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.deleteViewGone();
                mMessageSessionList.remove(position);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (mMessageSessionList.isEmpty() && onDataEmptyCallBack != null) {
                            onDataEmptyCallBack.dataEmpty();
                        }
                    }
                }, 400);
            }
        });
        return convertView;
    }

    private void goIMBySession(MessageSessionBean sessionBean) {
        Bundle bundle = new Bundle();
        bundle.putString(ActivityIm.PARAM_SESSIONID, sessionBean.getSessionID());
        bundle.putString(ActivityIm.PARAM_SIPINFO, JSON.toJSONString(sessionBean.getSipInfoList()));
        ActivityTools.startActivity(context, ActivityIm.class, bundle, false);
    }

    public void setOnDataEmptyCallBack(OnDataEmptyCallBack callBack) {
        this.onDataEmptyCallBack = callBack;
    }

    public interface OnDataEmptyCallBack {
        void dataEmpty();
    }

    class ViewHolder {
        public SlidingDeleteView sdvSessionContent;
        public RelativeLayout rlSessionContent;
        public TextView tvTime;
        public ImageView ivSender;
        public TextView tvSender;
        public TextView tvMessage;
        public TextView tvRemove;

        public ViewHolder(View view) {
            view.setTag(this);
            this.rlSessionContent = view.findViewById(R.id.rl_msg_session_content);
            this.rlSessionContent.getLayoutParams().width = view.getContext().getResources().getDisplayMetrics().widthPixels;
            this.sdvSessionContent = view.findViewById(R.id.sdv_msg_session_content);
            this.sdvSessionContent.setEnable(true);
            this.tvTime = view.findViewById(R.id.tv_msg_session_time);
            this.ivSender = view.findViewById(R.id.iv_msg_session_face);
            this.tvSender = view.findViewById(R.id.tv_msg_session_sender);
            this.tvMessage = view.findViewById(R.id.tv_msg_session_msg);
            this.tvRemove = view.findViewById(R.id.tv_msg_session_remove);
        }

        public void setMessage(MessageSessionBean session) {
            this.tvTime.setText(session.getLastTime());
            this.tvSender.setText(session.getUsers());
            this.tvMessage.setText(session.getLastMsg());
            if (this.sdvSessionContent.isSlidingVisible())
                this.sdvSessionContent.setDeleteViewGone();
        }

        public void deleteViewGone() {
            this.sdvSessionContent.setDeleteViewGone();
        }

        public void setSessionOnClick(View.OnClickListener listener) {
            this.rlSessionContent.setOnClickListener(listener);
        }

        public void setRemoveOnClick(View.OnClickListener listener) {
            this.tvRemove.setOnClickListener(listener);
        }
    }
}
