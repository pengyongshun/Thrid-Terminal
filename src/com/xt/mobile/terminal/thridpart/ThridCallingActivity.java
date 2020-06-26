package com.xt.mobile.terminal.thridpart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.thridpart.bean.MemberRefuseBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.activity.VedioMettingActivity;
import com.xt.mobile.terminal.util.CountdownUtil;
import com.xt.mobile.terminal.util.comm.UserMessge;

public class ThridCallingActivity extends BaseActivity implements CountdownUtil.OnCountdownListener {

    private ImageView mRefuseIv;
    private ImageView mReceiveIv;
    private UserMessge userMessge;
    private CountdownUtil countdownUtil;
    private String meetingType="";
    private boolean isOperation=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_thrid_calling);
        userMessge = UserMessge.getInstans(this);
        meetingType = userMessge.getThirdPartyGroupID();
        mRefuseIv = (ImageView) findViewById(R.id.activity_thrid_calling_refuse_iv);
        mReceiveIv = (ImageView) findViewById(R.id.activity_thrid_calling_receive_iv);
        mReceiveIv.setOnClickListener(this);
        mRefuseIv.setOnClickListener(this);

        //初始化计时器
        //首次进来的时候
        //初始化计时器
        int parseInt =60;
        String callOutTime = userMessge.getCallOutTime();
        if (callOutTime !=null && callOutTime.length()>0){
            parseInt = Integer.parseInt(callOutTime);
        }else {
            parseInt =60;
        }
        countdownUtil = CountdownUtil.newInstance();
        countdownUtil.setTotalTime(parseInt * 1000);
        countdownUtil.setIntervalTime(1 * 1000);
        countdownUtil.callback(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id==R.id.activity_thrid_calling_refuse_iv){
            //拒绝
            //向客户发送广播
            isOperation=true;
            MemberRefuseBean refuseBean=new MemberRefuseBean();
            refuseBean.setMeetingID(userMessge.getThirdPartyMeetingId());
            refuseBean.setMemberID(userMessge.getThirdPartyUserName());
            refuseBean.setMeetingType(userMessge.getThirdPartyGroupID());
            refuseBean.setChairmanID(userMessge.getThirdPartyChairmanID());
            KXBroadstcast.sendBroadcastMemberRefuse(ThridCallingActivity.this,refuseBean);
            finish();
        }else if (id==R.id.activity_thrid_calling_receive_iv){
            //接听  进入到
            isOperation=true;
            Intent intent=new Intent(ThridCallingActivity.this,VedioMettingActivity.class);
            intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_THRID_APP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRemain(long millisUntilFinished) {
        //倒计时进行时
        int count = (int) (millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {
        //倒计时已经结束
        //群聊
        if (!isOperation){
            showLoadingDaliog("正在退出...");
            handler.sendEmptyMessageDelayed(1,1000*2);
            PLog.d("ThridCallingActivity","------成员超时------->成员离开拒绝会议");
        }

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoadingDialog();
            finish();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if (countdownUtil != null && countdownUtil.isRunning()) {
            countdownUtil.stop();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countdownUtil != null && !(countdownUtil.isRunning())) {
            countdownUtil.start();
        }

    }

    /**
     * 主席手动离开 成员在此界面，成员关闭这个界面
     * @param meetingId
     */
    @Override
    protected void leaveRefuse(String meetingId) {
        super.leaveRefuse(meetingId);
        showLoadingDaliog("正在退出...");
        PLog.d("ThridCallingActivity","------成员未超时------->成员受到主席离开而离开拒绝界面");
        handler.sendEmptyMessageDelayed(1,1000*2);
    }
}
