package com.xt.mobile.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.MediaSettingBean;
import com.xt.mobile.terminal.bean.MettingListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/7.
 */
public class MediaSettingAdapter extends BaseAdapter {
    private List<MediaSettingBean> goodList=new ArrayList<MediaSettingBean>();
    private LayoutInflater mLayoutflater;
    private Context context;

    public MediaSettingAdapter(Context context, List<MediaSettingBean> goodList) {
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
            convertView=mLayoutflater.inflate(R.layout.item_media_setting_lv,null);
            //初始化控件
            mViewHoder=new ViewHoder(convertView);

        }else {
            mViewHoder= (ViewHoder) convertView.getTag();
        }
        //填充数据
        MediaSettingBean bean = goodList.get(position);
        mViewHoder.contentTv.setText(bean.getContent());
        boolean selet = bean.isSelet();
        if (selet){
            mViewHoder.isSeletCb.setImageDrawable(context.getResources().
                    getDrawable(R.drawable.icon_chexbox_pr));
        }else {
            mViewHoder.isSeletCb.setImageDrawable(context.getResources().
                    getDrawable(R.drawable.icon_chexbox_nor));
        }

        return convertView;
    }
    class ViewHoder{
        public TextView contentTv;
        public ImageView isSeletCb;

        public ViewHoder(View view){
            //设置标签
            view.setTag(this);
            this.contentTv = (TextView) view.findViewById(R.id.item_media_setting_content_tv);
            this.isSeletCb= (ImageView) view.findViewById(R.id.item_media_setting_selet_cb);

        }

    }

}
