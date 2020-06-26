package com.xt.mobile.terminal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.MediaSettingAdapter;
import com.xt.mobile.terminal.bean.MediaSettingBean;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体设置/分辨率/帧率/码率
 */
public class ResolutionRatioActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private ListView mListView;
    private String activtyTag="";
    private UserMessge userMessge;
    private List<MediaSettingBean> beanList=new ArrayList<>();
    private MediaSettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolution_ratio);
        userMessge = UserMessge.getInstans(ResolutionRatioActivity.this);
        activtyTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
        initView();
        initData();
    }

    private void initData() {
        String fbl = userMessge.getFbl();
        String zl = userMessge.getZl();
        String ml = userMessge.getMl();
        if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_FBL)) {
            //分辨率
            mTitleTv.setText(R.string.resolution_ratio);

            String[] fbls = getResources().
                    getStringArray(R.array.media_setting_fbl);
            List<MediaSettingBean> list = setData(fbls, fbl);
            if (beanList.size()>0){
                beanList.clear();
            }
            beanList.addAll(list);
            adapter.notifyDataSetChanged();

        } else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ZL)) {
            //帧率
            mTitleTv.setText(R.string.frame_ratio);
            String[] zls = getResources().
                    getStringArray(R.array.media_setting_zl);
            List<MediaSettingBean> list = setData(zls, zl);
            if (beanList.size()>0){
                beanList.clear();
            }
            beanList.addAll(list);
            adapter.notifyDataSetChanged();
        } else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ML)) {
            //码率
            mTitleTv.setText(R.string.code_ratio);
            String[] mls = getResources().
                    getStringArray(R.array.media_setting_ml);
            List<MediaSettingBean> list = setData(mls, ml);
            if (beanList.size()>0){
                beanList.clear();
            }
            beanList.addAll(list);
            adapter.notifyDataSetChanged();
        }


    }

    private void initView() {
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mRightIv = (ImageButton) findViewById(R.id.right_iv);
        mListView = (ListView) findViewById(R.id.activity_resolutionRatio_lv);

        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mRightTv.setVisibility(View.GONE);
        mRightIv.setVisibility(View.GONE);
        mLeftIv.setOnClickListener(this);

        adapter=new MediaSettingAdapter(ResolutionRatioActivity.this,beanList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);


    }

    /**
     * 填充数据
     * @param data
     * @param defult
     * @return
     */
    public List<MediaSettingBean> setData(String[] data,String defult) {
        List<MediaSettingBean> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            MediaSettingBean bean=new MediaSettingBean();
            bean.setContent(data[i]);
            if (defult !=null && defult.length()>0){
                if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_FBL)) {
                    if (userMessge.getFbl().equals(data[i])){
                        //相同代表选中
                        bean.setSelet(true);
                    }else {
                        //不相同代表未选中
                        bean.setSelet(false);
                    }
                }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ZL)){
                    if (userMessge.getZl().equals(data[i])){
                        //相同代表选中
                        bean.setSelet(true);
                    }else {
                        //不相同代表未选中
                        bean.setSelet(false);
                    }
                }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ML)){
                    if (userMessge.getMl().equals(data[i])){
                        //相同代表选中
                        bean.setSelet(true);
                    }else {
                        //不相同代表未选中
                        bean.setSelet(false);
                    }
                }

            }else {
                    //默认
                    if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_FBL)) {
                        //分辨率
                        if ("1920 x 1080".equals(data[i])){
                            userMessge.setFbl(data[i]);
                            bean.setSelet(true);
                        }else {
                            bean.setSelet(false);
                        }

                    }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ZL)){
                        //帧率
                        if ("30".equals(data[i])){
                            userMessge.setZl(data[i]);
                            bean.setSelet(true);
                        }else {
                            bean.setSelet(false);
                        }
                    }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ML)){
                        //码率
                        if ("512K".equals(data[i])){
                            userMessge.setMl(data[i]);
                            bean.setSelet(true);
                        }else {
                            bean.setSelet(false);
                        }
                    }

            }

            list.add(bean);

        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < beanList.size(); i++) {
            if (position==i){
                beanList.get(i).setSelet(true);
            }else {
                beanList.get(i).setSelet(false);
            }
        }
        //保存
        MediaSettingBean bean = beanList.get(position);
        if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_FBL)) {
            //分辨率
            userMessge.setFbl(bean.getContent());
        }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ZL)){
            //帧率
            userMessge.setZl(bean.getContent());
        }else if (activtyTag.equals(Constants.ACTIVTY_MEDIA_SETTING_ML)){
            //码率
            userMessge.setMl(bean.getContent());
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.left_iv) {
            // 返回
            finish();
        }
    }
}
