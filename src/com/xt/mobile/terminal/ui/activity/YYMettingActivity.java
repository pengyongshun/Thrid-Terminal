package com.xt.mobile.terminal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.bean.MettingListBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.fragment.FragmentMetting;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.DateUtil;
import com.xt.mobile.terminal.util.RandomUtils;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.dailog.VDialog;
import com.xt.mobile.terminal.view.wheelview.WiDateTimePickerDialog;

public class YYMettingActivity extends BaseActivity implements WiDateTimePickerDialog.DateTimePickerListener {

    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private ClearEditText mClearEditText;
    private LinearLayout mKssjLl;
    private TextView mKssjTv;
    private LinearLayout mJssjLl;
    private TextView mJssjTv;
    private WiDateTimePickerDialog wiDateTimePickerDialog;
    private String ksYMR="";
    private String jsYMR="";

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    VDialog.getDialogInstance(YYMettingActivity.this).hideLoadingDialog();
                    if (msg.obj instanceof MettingListBean){
                        MettingListBean bean = (MettingListBean) msg.obj;
                        Intent intent=new Intent(YYMettingActivity.this, ActivityMain.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("mettingListBean",bean);
                        intent.putExtra("data",bundle);
                        setResult(1001,intent);
                        finish();
                        ToastUtil.showShort(YYMettingActivity.this,"保存成功");

                    }

                    break;
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yymetting);
        initView();
    }

    private void initView() {

        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mClearEditText = (ClearEditText) findViewById(R.id.activity_yymetting_cet);
        mKssjLl = (LinearLayout) findViewById(R.id.activity_yymetting_kssj_ll);
        mKssjTv = (TextView) findViewById(R.id.activity_yymetting_kssj_tv);
        mJssjLl = (LinearLayout) findViewById(R.id.activity_yymetting_jssj_ll);
        mJssjTv = (TextView) findViewById(R.id.activity_yymetting_jssj_tv);

        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mLeftIv.setOnClickListener(this);
        mTitleTv.setText("预约会议");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText(R.string.save);
        mRightTv.setOnClickListener(this);

        mKssjLl.setOnClickListener(this);
        mJssjLl.setOnClickListener(this);

        mClearEditText.setHint("请输入会议主题");

        //初始化时间选择器
        wiDateTimePickerDialog = DailogUitl.initPickTimeDialog(YYMettingActivity.this,this,1);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_iv) {
            //取消、返回
            finish();
        }else if (v.getId() == R.id.right_tv) {
            //保存
            saveData();
        }else if (v.getId() == R.id.activity_yymetting_kssj_ll) {
            //开始时间
            if (wiDateTimePickerDialog !=null){
                wiDateTimePickerDialog.showDialog(1,System.currentTimeMillis());
            }
        }else if (v.getId() == R.id.activity_yymetting_jssj_ll) {
            //结束时间
            if (wiDateTimePickerDialog !=null){
                wiDateTimePickerDialog.showDialog(2,System.currentTimeMillis());
            }
        }

    }

    /**
     * 保存数据
     */
    private void saveData() {
        String mettingZt = mClearEditText.getText().toString();
        if (mettingZt !=null && mettingZt.length()>0){
            String kssj = mKssjTv.getText().toString();
            if (kssj !=null && kssj.length()>0){
                String jssj = mJssjTv.getText().toString();
                if (jssj !=null && jssj.length()>0){
                    int i = DateUtil.compareTime(kssj, jssj);
                    if (i==2){
                        //开始时间小于结束时间
                        final MettingListBean bean=new MettingListBean();
                        bean.setJssj(jssj);
                        bean.setKssj(kssj);
                        bean.setMettingID(RandomUtils.getRandomNumbers(9));
                        bean.setMettingTitle(mettingZt);
                        VDialog.getDialogInstance(YYMettingActivity.this).showLoadingDialog("正在加载中...",true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000*5);

                                    Message message=Message.obtain();
                                    message.what=1;
                                    message.obj=bean;
                                    handler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else {
                        //开始时间大于或者等于结束时间
                        ToastUtil.showShort(YYMettingActivity.this,"开始时间不能大于或者等于结束时间");
                    }

                }else {
                    ToastUtil.showShort(YYMettingActivity.this,"请设置开始时间");
                }
            }else {
                ToastUtil.showShort(YYMettingActivity.this,"请设置开始时间");
            }
        }else {
            ToastUtil.showShort(YYMettingActivity.this,"请填写会议主题");
        }
    }



    @Override
    public void onDateTimePickerFinished(int requestCode, int year, int month, int day, int hour, int min) {

        switch (requestCode){
            case 1:
                //开始时间
                ksYMR=year+"-"+coverData(month)+"-"+coverData(day);
                String ksHF=coverData(hour)+":"+coverData(min);
                mKssjTv.setText(ksYMR+"\t"+ksHF);
                break;
            case 2:
                //结束时间
                jsYMR=year+"-"+coverData(month)+"-"+coverData(day);
                String jsHF=coverData(hour)+":"+coverData(min);
                mJssjTv.setText(jsYMR+"\t"+jsHF);
                break;
        }
    }

    /**
     * 对年月日进行补领
     * @param result
     * @return
     */
    private String coverData(int result) {
        String data="";
        if (result<=9){
            data="0"+result;
        }else {
            data=""+result;
        }
        return data;
    }
}
