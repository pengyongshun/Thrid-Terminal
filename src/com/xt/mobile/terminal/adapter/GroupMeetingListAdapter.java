package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.GroupMeetingListBean;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/7.
 */
public class GroupMeetingListAdapter extends BaseAdapter {
    private List<GroupMeetingListBean> goodList=new ArrayList<GroupMeetingListBean>();
    private LayoutInflater mLayoutflater;
    private Context context;
    private OnClickCallBack onClickListener;

    public GroupMeetingListAdapter(Context context, List<GroupMeetingListBean> goodList) {
        this.goodList = goodList;
        this.context=context;
        mLayoutflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodList==null?0:goodList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder mViewHoder=null;
        if (convertView == null) {
            convertView=mLayoutflater.inflate(R.layout.item_group_metting_lv,null);
            //初始化控件
            mViewHoder=new ViewHoder(convertView);

        }else {
            mViewHoder= (ViewHoder) convertView.getTag();
        }
        final GroupMeetingListBean bean = goodList.get(position);
        mViewHoder.GroupTitleTv.setText("分组名称："+bean.getGroupTitle());
        mViewHoder.descriptionTv.setText("分组描述："+bean.getGroupContent());
        List<JoinMettingPeopleBean> groupPeople = bean.getGroupPeople();
        StringBuffer buffer=new StringBuffer();
        if (groupPeople !=null && groupPeople.size()>0){
            for (int i = 0; i < groupPeople.size(); i++) {
                String name = groupPeople.get(i).getName();

                if (groupPeople.size()-1==i){
                    //最后
                    buffer.append(name);
                }else {
                    buffer.append(name).append("、");
                }

            }
            mViewHoder.usersTv.setText("分组成员："+buffer.toString());
        }else {
            mViewHoder.usersTv.setText("分组成员："+bean.getGroupPeople());
        }
        mViewHoder.deletGroupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.deletClick(bean);
            }
        });
        mViewHoder.moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.moreClick(bean);
            }
        });
        return convertView;
    }
    class ViewHoder{
        public TextView GroupTitleTv;
        public TextView descriptionTv;
        public LinearLayout deletGroupLl;
        public ImageView moreIv;
        public TextView usersTv;

        public ViewHoder(View view){
            //设置标签
            view.setTag(this);
            this.GroupTitleTv = (TextView) view.findViewById(R.id.item_group_metting_title_tv);
            this.descriptionTv= (TextView) view.findViewById(R.id.item_group_metting_description);
            this.deletGroupLl= (LinearLayout) view.findViewById(R.id.item_group_metting_delet_ll);
            this.moreIv= (ImageView) view.findViewById(R.id.item_group_metting_more_iv);
            this.usersTv= (TextView) view.findViewById(R.id.item_group_metting_users);
        }


    }


    public void setOnClickListener(OnClickCallBack onClickListener){
        this.onClickListener=onClickListener;
    }

    public interface OnClickCallBack{
        void moreClick(GroupMeetingListBean bean);
        void deletClick(GroupMeetingListBean bean);
    }
}
