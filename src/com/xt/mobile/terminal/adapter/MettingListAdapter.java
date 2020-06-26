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
import com.xt.mobile.terminal.bean.MettingListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/7.
 */
public class MettingListAdapter extends BaseAdapter {
    private List<MettingListBean> goodList=new ArrayList<MettingListBean>();
    private LayoutInflater mLayoutflater;
    private Context context;
    private OnClickCallBack onClickListener;

    private boolean isShowMore;

    public MettingListAdapter(Context context,
                              List<MettingListBean> goodList,
                              boolean isShowMore) {
        this.goodList = goodList;
        this.context=context;
        this.isShowMore=isShowMore;
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
            convertView=mLayoutflater.inflate(R.layout.item_my_metting_lv,null);
            //初始化控件
            mViewHoder=new ViewHoder(convertView);

        }else {
            mViewHoder= (ViewHoder) convertView.getTag();
        }
        //填充数据
        final MettingListBean bean = goodList.get(position);
        mViewHoder.mettingTitle.setText("会议主题："+bean.getMettingTitle());
        mViewHoder.ksTime.setText("开始时间："+bean.getKssj());
       // mViewHoder.jsTime.setText("结束时间："+bean.getJssj());
        mViewHoder.mettingID.setText("会议ID："+bean.getMettingID());
        mViewHoder.jrMetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.joinMettingClick(bean);
            }
        });
        if (isShowMore){
            mViewHoder.more.setVisibility(View.VISIBLE);
        }else {
            mViewHoder.more.setVisibility(View.GONE);
        }
        mViewHoder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.moreClick(bean);
            }
        });
        return convertView;
    }
    class ViewHoder{
        public TextView mettingTitle;
        public TextView mettingID;
        public LinearLayout jrMetting;
        public ImageView more;
        public TextView ksTime;
        public TextView jsTime;

        public ViewHoder(View view){
            //设置标签
            view.setTag(this);
            this.mettingTitle = (TextView) view.findViewById(R.id.item_my_metting_title_tv);
            this.mettingID= (TextView) view.findViewById(R.id.item_my_metting_ID_tv);
            this.jrMetting= (LinearLayout) view.findViewById(R.id.item_my_metting_jr_ll);
            this.more= (ImageView) view.findViewById(R.id.item_my_metting_more_iv);
            this.ksTime= (TextView) view.findViewById(R.id.item_my_metting_ks_sj_tv);
            this.jsTime= (TextView) view.findViewById(R.id.item_my_metting_js_sj_tv);
        }


    }

    public void setOnClickListener(OnClickCallBack onClickListener){
        this.onClickListener=onClickListener;
    }

    public interface OnClickCallBack{
        void moreClick(MettingListBean bean);
        void joinMettingClick(MettingListBean bean);
    }
}
