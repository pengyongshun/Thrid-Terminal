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
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.util.List;

/**
 * Created by 彭永顺 on 2019/5/19.
 */
public class JoinMettingPeopleAdapter extends BaseAdapter {
    private List<JoinMettingPeopleBean> data;
    private LayoutInflater mLayoutflater;
    private Context context;
    private boolean tag;
    private boolean isDelet;
    private boolean isShowRylx;
    private DeletPeopleCallBack deletPeopleCallBack;
    public JoinMettingPeopleAdapter(Context context,
                                    List<JoinMettingPeopleBean> data,
                                    boolean tag,
                                    boolean isDelet,
                                    boolean isShowRylx) {
        this.context=context;
        mLayoutflater = LayoutInflater.from(context);
        this.data=data;
        this.tag=tag;
        this.isDelet=isDelet;
        this.isShowRylx=isShowRylx;
    }
    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHoder mViewHoder;
        if (convertView == null) {
            convertView=mLayoutflater.inflate(R.layout.item_join_metting_people_list_lv,null);
            //初始化控件
            mViewHoder=new ViewHoder(convertView);

        }else {
            mViewHoder= (ViewHoder) convertView.getTag();
        }

        JoinMettingPeopleBean bean = data.get(position);
        if (tag){
            //编辑
            mViewHoder.choiceCb.setVisibility(View.VISIBLE);
            boolean choice = bean.isChoice();
            if (choice){
                //选中
                mViewHoder.choiceCb.setImageResource(R.drawable.icon_chexbox_pr);
            }else {
                mViewHoder.choiceCb.setImageResource(R.drawable.icon_chexbox_nor);
            }

        }else{
            //查看
            mViewHoder.choiceCb.setVisibility(View.GONE);
            if (isDelet){
                //显示删除按钮
                String userID = UserMessge.getInstans(context).getUserID();
                String userName = UserMessge.getInstans(context).getUserName();
                String name = bean.getName();
                String useId = bean.getUseId();
                if (userName.equals(name) && userID.equals(useId)){
                    //相同，代表是自己，不能够删除
                    mViewHoder.deletLl.setVisibility(View.GONE);
                }else {
                mViewHoder.deletLl.setVisibility(View.VISIBLE);
                }
                mViewHoder.deletLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletPeopleCallBack.deletPeople(position);
                    }
                });
            }else {
                //不显示删除按钮
                mViewHoder.deletLl.setVisibility(View.GONE);
            }
        }
        mViewHoder.nameTv.setText(bean.getName());
        if (isShowRylx){
            mViewHoder.rylxLl.setVisibility(View.VISIBLE);
            String role = bean.getRole();
            if (role.equals("chairman")){
                //主席
                mViewHoder.rylxTv.setText("群主");
            }else {
                //成员
                mViewHoder.rylxTv.setText("");
            }

        }else {
            mViewHoder.rylxLl.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHoder{
        public TextView nameTv;
        public ImageView choiceCb;
        public LinearLayout deletLl;
        public LinearLayout rylxLl;
        public TextView rylxTv;
        public ViewHoder(View view){
            //设置标签
            view.setTag(this);
            this.nameTv = (TextView) view.findViewById(R.id.item_join_metting_people_list_name_tv);
            this.choiceCb = (ImageView) view.findViewById(R.id.item_join_metting_people_list_selet_cb);
            this.deletLl = (LinearLayout) view.findViewById(R.id.item_join_metting_people_list_delet_ll);
            this.rylxLl = (LinearLayout) view.findViewById(R.id.item_join_metting_people_list_rylx_ll);
            this.rylxTv = (TextView) view.findViewById(R.id.item_join_metting_people_list_rylx_tv);
        }

    }

    public void setDeletListener(DeletPeopleCallBack deletPeopleCallBack){
        this.deletPeopleCallBack=deletPeopleCallBack;
    }

    public interface DeletPeopleCallBack{
        void deletPeople(int postion);
    }
}
