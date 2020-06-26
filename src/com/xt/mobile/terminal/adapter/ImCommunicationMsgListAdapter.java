package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.MessageBean;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;

import java.util.ArrayList;
import java.util.List;

public class ImCommunicationMsgListAdapter extends BaseAdapter {
    private List<MessageBean> mMessageList = new ArrayList<MessageBean>();
    private LayoutInflater mLayoutInflater;
    private Context context;

    public ImCommunicationMsgListAdapter(Context context, List<MessageBean> messageList) {
        this.mMessageList = messageList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_im_messages, null);
            mViewHolder = new ViewHolder(convertView);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        //填充数据
        final MessageBean message = mMessageList.get(position);
        mViewHolder.setMessage(message);
        return convertView;
    }

    class ViewHolder {
        public TextView tvTime;
        public ImageView ivSender;
        public TextView tvSender;
        public TextView tvMessage;
        public ImageView ivSelf;

        public ViewHolder(View view) {
            //设置标签
            view.setTag(this);
            this.tvTime = view.findViewById(R.id.item_my_metting_title_tv);
            this.ivSender = view.findViewById(R.id.iv_im_sender_face);
            this.tvSender = view.findViewById(R.id.item_my_metting_ID_tv);
            this.tvMessage = view.findViewById(R.id.item_my_metting_ks_sj_tv);
            this.ivSelf = view.findViewById(R.id.iv_im_self_face);
        }

        public void setMessage(MessageBean message) {
            this.tvTime.setText(message.getSendTime());
            boolean isSelf = message.getSenderID().equals(WebSocketCommand.getInstance().getmUserId());
            this.tvSender.setText(isSelf ? "我" : message.getSenderName());
            this.tvMessage.setText(message.getContent());
            int msgColorResId = isSelf ? R.color.white : R.color.black;
            this.tvMessage.setTextColor(context.getResources().getColor(msgColorResId));
            int msgBackgroundResId = isSelf ? R.drawable.im_msg_bg_self : R.drawable.im_msg_bg_sender;
            this.tvMessage.setBackgroundResource(msgBackgroundResId);
            RelativeLayout.LayoutParams senderParams = (RelativeLayout.LayoutParams) this.tvSender.getLayoutParams();
            senderParams.removeRule(isSelf ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF);

            RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams) this.tvMessage.getLayoutParams();
            msgParams.removeRule(isSelf ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF);

            if (isSelf) {
                senderParams.addRule(RelativeLayout.LEFT_OF, this.ivSelf.getId());
                msgParams.addRule(RelativeLayout.LEFT_OF, this.ivSelf.getId());
            } else {
                senderParams.addRule(RelativeLayout.RIGHT_OF, this.ivSender.getId());
                msgParams.addRule(RelativeLayout.RIGHT_OF, this.ivSender.getId());
            }
            this.tvSender.setLayoutParams(senderParams);
            this.tvMessage.setLayoutParams(msgParams);

            this.ivSender.setVisibility(isSelf ? View.GONE : View.VISIBLE);
            this.ivSelf.setVisibility(isSelf ? View.VISIBLE : View.GONE);
        }
    }
}
