package com.xt.mobile.terminal.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.network.addparmer.JoinMemberBean;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeark;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeaskBody;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidInfo;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidBody;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.bean.MettingListBean;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.JsonParseUilt;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingPeopleList;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingPeopleListBean;
import com.xt.mobile.terminal.network.pasre.join_metting.UserIdSwtichBean;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.service.VideoService;
import com.xt.mobile.terminal.sip.Session;
import com.xt.mobile.terminal.sip.SessionManager;
import com.xt.mobile.terminal.sip.SipManager;
import com.xt.mobile.terminal.thridpart.KXBroadstcast;
import com.xt.mobile.terminal.thridpart.ThridJoinListActivity;
import com.xt.mobile.terminal.thridpart.XTContants;
import com.xt.mobile.terminal.thridpart.bean.CreateMeetingBean;
import com.xt.mobile.terminal.thridpart.bean.InvitePeopleBean;
import com.xt.mobile.terminal.thridpart.bean.MemberLeaveBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.CountdownUtil;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.MediaServiceUitl;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.TrustAllCerts;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.XTMediaPlay;
import com.xt.mobile.terminal.view.XTMediaSource;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xtmedia.encode.SendMediaData;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class VedioMettingActivity extends BaseActivity implements RequestUitl.HttpResultCall, CountdownUtil.OnCountdownListener {

    private SurfaceView mZstSfv;
    private SurfaceView mFstSfv;
    private XTMediaPlay mXTremoteMediaPlay;
    private XTMediaPlay mXTremoteMedia2;
    private ArrayList<ParseMeetingRtpidBean> mMeetingRtpidList = new ArrayList<ParseMeetingRtpidBean>();
    
    private LinearLayout mMenuLl;
    private ImageView mCloseAvdioIv;
    private ImageView mMenuOpenVedioIv;
    private ImageView mMenuScreenIv;
    private LinearLayout mMenuScreenLl;
    private ImageView mMenuPeopleIv;
    private ImageView mMenuCloseAvdioIv;
    private ImageView mMenuAskPeopleIv;
    private ImageView mSwthCammerTv;
    private TextView mIDTv;
    private Chronometer mSjTv;
    private TextView mCloseTv;
    private Button mShfyBtn;

    private LinearLayout mMenuCySqfyLl;
    private ImageView mMenuCySqfyIv;
    private TextView mMenuCySqfyTv;

    private int onTochcount = 1;
    private int audioCount=1;
    private int vedioCount=1;
    private int screenCount=1;
    private int peopleCount=1;
    private int askPeopleCount=1;
    private int cammerCount=1;
    private int cySqfyCount=1;

    private String activtyTag="";
    private CustomTextDialog shfyDialog;
    private CustomTextDialog customScreenDialog;
    private ImageView mScreenCenterIv;
    private LinearLayout mScreenCenterLl;
    private TextView mMenuScreenTv;
    private JoinMettingBean curMettingBean;//当前的会议
    private LinearLayout mMenuAskPeopleLl;
    private boolean isStartSpeak =false;//默认没有指定发言
    private boolean isMember=true;//默认是成员
    private JoinMettingPeopleBean curMemberBean;//当前成员的信息
    private CustomTextDialog cyLeaveMeetingDialog;
    private int requestType=-1;//服务请求类型
    private UserMessge userMessge;
    private boolean isCreatMeeting=true;
    private MediaServiceUitl mediaServiceUitl;
    private CustomTextDialog customTextDialog;
    private CustomTextDialog isApplySpeakDialog;
    private LinearLayout mMenuPeopleLl;
    private String mSessionId;
    private RequestUitl instans;
    private String urlGet ="";
    private List<JoinMettingPeopleBean> joinMettingPeopleBeanList=new ArrayList<>();
    private CountdownUtil countdownUtil;
    private String meetingType;
    private boolean isOver=false;
    private String choiceJson="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vedio_metting);
        PLog.d("VedioMettingActivity","--------启动aar包------开始转跳会议界面---->结束时间：time="+ TimeUitls.getLongSysnTime());
        mediaServiceUitl = MediaServiceUitl.getInstans(this);
        userMessge = UserMessge.getInstans(this);
        meetingType = userMessge.getThirdPartyGroupID();
        instans = RequestUitl.getInstans(this, this);
        activtyTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
        userMessge.setCountOver(false);
        isOver=userMessge.isCountOver();
        initView();
        iniData();

        //初始化计时器
        int parseInt =60;
                String callOutTime = userMessge.getCallOutTime();
        if (callOutTime !=null && callOutTime.length()>0){
             parseInt = Integer.parseInt(callOutTime);
        }else {
            parseInt =60;
        }
        //首次进来的时候
        userMessge.setTimeCount(parseInt);
        countdownUtil = CountdownUtil.newInstance();
        countdownUtil.setTotalTime(parseInt * 1000);
        countdownUtil.setIntervalTime(1 * 1000);
        countdownUtil.callback(this);
    }

    private void iniData() {
        //默认是主席  且底部导航栏默认不显示
        ActivityTools.setViewState(mMenuLl, true);
        //从其他界面来的   收回发言试图不可见
        ActivityTools.setViewState(mShfyBtn, true);
        //隐藏俯视图
        ActivityTools.setViewState(mFstSfv, true);
        if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
            //快速会议
            //主席
            isMember=false;
            curMettingBean= (JoinMettingBean) getIntent().
                    getSerializableExtra("JoinMettingBean");
            String sceneID = curMettingBean.getSceneName();
            mIDTv.setText(sceneID);

            //创建会议
            createMetting(curMettingBean.getSceneName(),"",0);

        }else if (activtyTag.equals(Constants.ACTIVTY_GROUP_POP_JOIN)){
            //从分组会议来的
            String result = getIntent().
                    getStringExtra("JoinMettingBean");
            curMettingBean=FastJsonTools.json2BeanObject(result,JoinMettingBean.class);
            isMember = getIntent().getBooleanExtra("isMember", true);

            String sceneID = curMettingBean.getSceneName();
            mIDTv.setText(sceneID);
            //开始分组会议
            //进入会议
            WebSocketCommand.getInstance().
                    onWssStartGroupMeeting(curMettingBean.getSceneID());
        }else if (activtyTag.equals(Constants.ACTIVTY_JIONHY)){
            //从进入会议来的
            //加入会议
            //成员
            isMember=true;
            MettingListBean curBean= (MettingListBean) getIntent().
                    getSerializableExtra("MettingListBean");
            String sceneID = curBean.getMettingTitle();
            mIDTv.setText(sceneID);

            curMettingBean=new JoinMettingBean();
            curMettingBean.setSceneID(curBean.getMettingID());
            curMettingBean.setSceneName(curBean.getMettingTitle());
            //发起加入会议
            joinMetting(curMettingBean.getSceneName(),6);
        }else if (activtyTag.equals(Constants.ACTIVTY_CY_JOIN)){
            //成员收到主席邀请，成员进来的
            //加入会议
            isMember=true;

            curMettingBean= (JoinMettingBean) getIntent().
                    getSerializableExtra("JoinMettingBean");
            String sceneID = curMettingBean.getSceneName();
            mIDTv.setText(sceneID);

            //发起加入会议
            joinMetting(curMettingBean.getSceneID(),6);
        }else if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            PLog.d("VedioMettingActivity","----------onCreate------->进入到会议");
            //隐藏悬浮框
            Intent intent = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
            sendBroadcast(intent);
            //从第三方来的
            //创建端口
            //主席
            isMember=false;
            //创建端口
            mediaServiceUitl.createSendPort();
            isCreatMeeting = userMessge.isThirdPartyCreatMeeting();
            String meetingId = userMessge.getThirdPartyMeetingId();
            curMettingBean= new JoinMettingBean();
            curMettingBean.setSceneID(meetingId);
            curMettingBean.setSceneName("临时会议");
            String sceneID = curMettingBean.getSceneName();
            mIDTv.setText(sceneID);

            try{
                Thread.sleep(1000*2);
            }catch (Exception e){
                e.getStackTrace();
            }
            if (isCreatMeeting){
                //分单聊和群聊
                String groupID = userMessge.getThirdPartyGroupID();
                if (groupID.equals(XTContants.MEETING_GROUP)){
                    //群聊
                     choiceJson = getIntent().getStringExtra("json");
                    createMetting(curMettingBean.getSceneName(),"",0);
                }else if (groupID.equals(XTContants.MEETING_SINGLE)){
                    //单聊
                    createMetting(curMettingBean.getSceneName(),"",0);

                }
                //创建会议
                PLog.d("VedioMettingActivity","----------onCreate------->创建者 创建会议");

            }else {
                //加入会议
                String sceneID1 = curMettingBean.getSceneID();
                PLog.d("VedioMettingActivity","----------onCreate------->sceneID："+sceneID1);
                if (sceneID1 !=null && sceneID1.length()>0){
                    PLog.d("VedioMettingActivity","----------onCreate------->成员 加入会议");
                    joinMetting(curMettingBean.getSceneID(),6);
                }else {
                    ToastUtil.showShort(VedioMettingActivity.this,"会议id不能为空");
                    showLoadingDaliog("正在退出...");
                    mHandler.sendEmptyMessageDelayed(1002,1000);
                }

            }

        }


        //初始化计时器
        int hour = (int) ((SystemClock.elapsedRealtime() - mSjTv.getBase()) / 1000 / 60);
        mSjTv.setFormat("0"+String.valueOf(hour)+":%s");
        startTime();


        //默认屏幕共享中间的提示不显示，只有点击共享才会出现
        mScreenCenterLl.setVisibility(View.GONE);

        //默认是主席 ，显示主席有的导航  没有申请发言
        //成员 没有 屏幕共享、邀请好友
        //发言人 有主席显示的东西，但是除了邀请好友没有
        if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            //默认是主席 ，显示主席有的导航  没有申请发言
            mMenuCySqfyLl.setVisibility(View.GONE);
            mMenuScreenLl.setVisibility(View.GONE);
            mMenuAskPeopleLl.setVisibility(View.GONE);
            String groupID = userMessge.getThirdPartyGroupID();
            if (groupID.equals(XTContants.MEETING_SINGLE)){
                //单聊
                mMenuPeopleLl.setVisibility(View.GONE);
            }else if (groupID.equals(XTContants.MEETING_GROUP)){
                //群聊
                mMenuPeopleLl.setVisibility(View.VISIBLE);
            }
//            if (isCreatMeeting){
//                mMenuPeopleLl.setVisibility(View.VISIBLE);
//            }else {
//                mMenuPeopleLl.setVisibility(View.GONE);
//            }
        }else {
            if (isMember){
                //成员
                mMenuCySqfyLl.setVisibility(View.VISIBLE);
                mMenuScreenLl.setVisibility(View.GONE);
                mMenuAskPeopleLl.setVisibility(View.GONE);
            }else {
                //主席
                mMenuCySqfyLl.setVisibility(View.GONE);
                mMenuScreenLl.setVisibility(View.VISIBLE);
                mMenuAskPeopleLl.setVisibility(View.VISIBLE);
            }
        }
        //初始化视频配置
        initVedio();
    }

    private void initVedio() {
        //创建视频保存目录

    	mXTremoteMediaPlay = new XTMediaPlay();
		mXTremoteMediaPlay.InitSurfaceView(mZstSfv);

        mXTremoteMedia2 = new XTMediaPlay();
        mXTremoteMedia2.InitSurfaceView(mFstSfv);
    }

    private void initView() {
        mZstSfv = (SurfaceView) findViewById(R.id.activity_vedio_metting_zst_sfv);
        mFstSfv = (SurfaceView) findViewById(R.id.activity_vedio_metting_fst_sfv);
        mMenuLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_menu_ll);
        mMenuCloseAvdioIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_close_avdio_iv);
        mMenuOpenVedioIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_open_vedio_iv);
        mMenuScreenIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_screen_iv);
        mMenuScreenTv = (TextView) findViewById(R.id.activity_vedio_metting_menu_screen_tv);
        mMenuScreenLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_menu_screen_ll);

        mMenuCySqfyIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_cy_sqfy_iv);
        mMenuCySqfyTv = (TextView) findViewById(R.id.activity_vedio_metting_menu_cy_sqfy_tv);
        mMenuCySqfyLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_menu_cy_sqfy_ll);

        mScreenCenterIv = (ImageView) findViewById(R.id.activity_vedio_metting_screen_share_center_iv);
        mScreenCenterLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_screen_share_center_ll);

        mMenuPeopleIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_people_iv);
        mMenuPeopleLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_menu_people_ll);

        mMenuAskPeopleLl = (LinearLayout) findViewById(R.id.activity_vedio_metting_menu_ask_people_ll);
        mMenuAskPeopleIv = (ImageView) findViewById(R.id.activity_vedio_metting_menu_ask_people_iv);
        mSwthCammerTv = (ImageView) findViewById(R.id.activity_vedio_metting_swth_cammer_tv);
        mIDTv = (TextView) findViewById(R.id.activity_vedio_metting_ID_tv);
        mSjTv = (Chronometer) findViewById(R.id.activity_vedio_metting_sj_tv);
        mCloseTv = (TextView) findViewById(R.id.activity_vedio_metting_close_tv);
        mShfyBtn = (Button) findViewById(R.id.activity_vedio_metting_shfy_btn);
        mMenuCloseAvdioIv.setOnClickListener(this);
        mMenuOpenVedioIv.setOnClickListener(this);
        mMenuScreenIv.setOnClickListener(this);
        mMenuPeopleIv.setOnClickListener(this);
        mMenuAskPeopleIv.setOnClickListener(this);
        mMenuCySqfyIv.setOnClickListener(this);
        mSwthCammerTv.setOnClickListener(this);
        mCloseTv.setOnClickListener(this);
        mShfyBtn.setOnClickListener(this);



        //初始化对话框
        //主席停止会议
        customTextDialog = DailogUitl.initTextDialog(VedioMettingActivity.this, "停止会议", "请确认是否离开会议？","离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //离开会议,返回到会议首页
                if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                    //第三方
                    if (curMettingBean !=null){
                        stopMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出聊天室...");
                    mHandler.sendEmptyMessageDelayed(1001,1000);
                }else {
                    //非第三方
                    if (curMettingBean !=null){
                        stopMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在停止会议...");
                    mHandler.sendEmptyMessageDelayed(1000,1000);
                }



            }
        });

        //初始化对话框
        //成员离开会议
        cyLeaveMeetingDialog = DailogUitl.initTextDialog(VedioMettingActivity.this, "离开会议", "请确认是否离开会议？","离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //离开会议,返回到会议首页
                if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                    //第三方
                    if (curMettingBean !=null){
                        leaveMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出聊天室...");
                    mHandler.sendEmptyMessageDelayed(1001,1000);
                }else {
                    //非第三方
                    if (curMettingBean !=null){
                        leaveMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出会议...");
                    mHandler.sendEmptyMessageDelayed(1000,1000);
                }

            }
        });

        //屏幕共享对话框
        customScreenDialog = DailogUitl.initTextDialog(VedioMettingActivity.this, "注意", "您屏幕上的所有内容将被会议其他成员可见","确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //中间屏幕的按钮可见,开始共享屏幕
                mScreenCenterLl.setVisibility(View.VISIBLE);
                mFstSfv.setVisibility(View.GONE);
            }
        });


    }


    private void initIsApplySpaekDialog(String title , String text,
                                        final String meetingId, final String menmberId) {
        //主席是否允许成员发言
        isApplySpeakDialog = DailogUitl.initTextDialog(VedioMettingActivity.this, title, text,"拒绝", "同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //拒绝成员发言
                zxRefuseSpeak(meetingId,menmberId);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //允许成员发言
                zxAgreeSpeak(meetingId,menmberId);
                //收回发言人控件可见
                ActivityTools.setViewState(mShfyBtn, false);

            }
        });

        if (isApplySpeakDialog !=null && !(isApplySpeakDialog.isShowing())){
            isApplySpeakDialog.show();
        }

    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoadingDialog();
            switch (msg.what){
                case 1000:
                    //非第三方
                    Intent intent=new Intent(VedioMettingActivity.this, ActivityMain.class);
                    setResult(1001,intent);
                    finish();
                    break;
                case 1001:
                    //第三方
                    if (isCreatMeeting){
                        //创建者  退出
                        //向客户发送主席离开会议的通知
                        KXBroadstcast.sendBroadcastChariManStopMeeting(VedioMettingActivity.this);
                        PLog.d("VedioMettingActivity","----------mHandler------->创建者手动退出会议");
                        hideLoadingDialog();
                        //删除会议
                        deletMeetingRequest(curMettingBean);
                        finish();
                    }else {
                        hideLoadingDialog();
                        PLog.d("VedioMettingActivity","----------mHandler------->成员退出会议");
                        //删除会议
                        deletMeetingRequest(curMettingBean);
                        //向客户发送成员退出会议的广播
                        sendMemberLeaveMeetingBroadcast();
                        hideLoadingDialog();
                        finish();
                    }


                    break;
                case 1002:
                    //第三方
                    //成员会议id为空，返回
                    PLog.d("VedioMettingActivity","----------mHandler------->成员会议id为空，退出会议");
                    hideLoadingDialog();
                    finish();
                    break;
                case 1003:
                    //主席超时离开
                    //向客户发送主席因成员在规定时间内未进入退出
                    PLog.d("VedioMettingActivity","----------mHandler------->创建者超时自动退出会议");
                    KXBroadstcast.sendBroadcastChariManOutTimeStopMeeting(VedioMettingActivity.this);
                    hideLoadingDialog();
                    //删除会议
                    deletMeetingRequest(curMettingBean);
                    finish();
                    break;
            }


        }
    };



    /**
     * 发言人对话框
     * @param name
     */
    private void initfyrDaloig(String name) {
        shfyDialog = DailogUitl.initTextDialog(VedioMettingActivity.this, "停止发言", "收回当前发言人 ["+name+"] 的发言权","确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //主席收回成员发言
                isStartSpeak =false;
                //收回成员发言
                //网络请求
                if (curMettingBean !=null){
                    backSpeak(curMettingBean.getSceneID());
                }
                ToastUtil.showShort(VedioMettingActivity.this,"收回成员发言");
                //收回发言不可见
                ActivityTools.setViewState(mShfyBtn, true);
                //finish();
            }
        });

        if (shfyDialog !=null && !(shfyDialog.isShowing())){
            shfyDialog.show();
        }
    }

    private void stopTime() {
      //停止计时
      mSjTv.stop();
    }

    private void startTime() {
        if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            if (isCreatMeeting){
                //这里启动定时需要在创建会议成功才可以计时
            }else {
                //设置开始计时时间
                mSjTv.setBase(SystemClock.elapsedRealtime() );
                //启动计时器
                mSjTv.start();
            }
        }else {
            if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
                //这里启动定时需要在创建会议成功才可以计时
            }else {
                //设置开始计时时间
                mSjTv.setBase(SystemClock.elapsedRealtime() );
                //启动计时器
                mSjTv.start();
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_vedio_metting_menu_close_avdio_iv) {
            // 闭麦
            swthView(audioCount,null,0);
        }else if (v.getId() == R.id.activity_vedio_metting_menu_open_vedio_iv) {
            // 开启视频
            swthView(vedioCount,null,1);
        }else if (v.getId() == R.id.activity_vedio_metting_menu_screen_iv) {
            // 屏幕共享
            swthView(screenCount,null,2);
        }else if (v.getId() == R.id.activity_vedio_metting_menu_people_iv) {
            //收回悬浮框
            Intent dailogIntnet = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
            sendBroadcast(dailogIntnet);
            if (isStartSpeak){
                //从指定发言来的
                ToastUtil.showShort(VedioMettingActivity.this,"请先结束当前的发言才能操作");
            }else {
                //从其他界面来的
                if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                    //第三方
                    Intent intent=new Intent(VedioMettingActivity.this,ThridJoinListActivity.class);
                    String json="";
                    if (joinMettingPeopleBeanList.size()>0){
                        Gson gson=new Gson();
                        json=gson.toJson(joinMettingPeopleBeanList);
                    }else {
                        json="";
                    }
                    PLog.d("VedioMettingActivity","-------contacts---------->json ="
                            +json+"\nmeetingId="+curMettingBean.getSceneID()+"\nisCreatMeeting="+isCreatMeeting);
                    intent.putExtra("json",json);
                    intent.putExtra("meetingId",curMettingBean.getSceneID());
                    intent.putExtra("isCreatMeeting",isCreatMeeting);
                    if (isCreatMeeting){
                        //创建者
                        startActivityForResult(intent,1020);
                    }else {
                        //成员
                        startActivityForResult(intent,1021);
                    }


                }else {
                    //非第三方
                    if (isMember){
                        // 成员
                        Intent intent=new Intent(VedioMettingActivity.this,JoinMettingPeopleListActivity.class);
                        intent.putExtra("isMember",isMember);
                        //addMembersData();
                        String json = FastJsonTools.bean2Json(curMettingBean);
                        intent.putExtra("json",json);
                        intent.putExtra("activityTag","");
                        startActivityForResult(intent,1001);
                    }else {
                        //主席
                        Intent intent=new Intent(VedioMettingActivity.this,JoinMettingPeopleListActivity.class);
                        intent.putExtra("isMember",isMember);
                        String json = FastJsonTools.bean2Json(curMettingBean);
                        intent.putExtra("json",json);
                        intent.putExtra("activityTag","");
                        startActivityForResult(intent,1000);
                    }
                }

            }

            //swthView(peopleCount,null,3);
        }else if (v.getId() == R.id.activity_vedio_metting_menu_ask_people_iv) {
            //主席  这个只有主席有
            // 邀请好友
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //从第三方进来的
                //收回悬浮框
                Intent dailogIntnet = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
                sendBroadcast(dailogIntnet);

                Intent intent=new Intent(VedioMettingActivity.this,BaseContanctsActivity.class);
                intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_THRID_APP);
                intent.putExtra("JoinMettingBean",curMettingBean);
                startActivityForResult(intent,1003);
            }else {
                //主席  这个只有主席有
                // 邀请好友
                if (!isMember){
                    //主席
                    //收回悬浮框
                    Intent dailogIntnet = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
                    sendBroadcast(dailogIntnet);

                    Intent intent=new Intent(VedioMettingActivity.this,BaseContanctsActivity.class);
                    intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_KSHY);
                    intent.putExtra("JoinMettingBean",curMettingBean);
                    startActivityForResult(intent,1003);
                }
            }

        }else if (v.getId() == R.id.activity_vedio_metting_swth_cammer_tv) {
            // 切换摄像头
            swthView(cammerCount,null,5);
        } else if (v.getId() == R.id.activity_vedio_metting_close_tv) {
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //第三方
                // 离开会议
                if (isCreatMeeting){
                    if (customTextDialog !=null && !(customTextDialog.isShowing())){
                        customTextDialog.show();
                    }
                }else {
                    if (cyLeaveMeetingDialog !=null && !(cyLeaveMeetingDialog.isShowing())){
                        cyLeaveMeetingDialog.show();
                    }
                }

            }else {
                //非第三方
                if (isMember){
                    //成员
                    // 离开会议
                    if (cyLeaveMeetingDialog !=null && !(cyLeaveMeetingDialog.isShowing())){
                        cyLeaveMeetingDialog.show();
                    }

                }else {
                    //主席
                    // 离开会议
                    if (customTextDialog !=null && !(customTextDialog.isShowing())){
                        customTextDialog.show();
                    }
                }
            }

        } else if (v.getId() == R.id.activity_vedio_metting_shfy_btn) {
            //主席这边  收回发言
            if (curMemberBean !=null){
                initfyrDaloig(curMemberBean.getName());
            }
        } else if (v.getId() == R.id.activity_vedio_metting_screen_share_center_iv) {
            //点击中间屏幕的停止共享按钮
            mMenuScreenIv.setImageDrawable(getResources().
                    getDrawable(R.drawable.icon_screen_nor));
            //中间屏幕的停止共享按钮 不可见
            mScreenCenterLl.setVisibility(View.GONE);
            mFstSfv.setVisibility(View.VISIBLE);
            screenCount++;
        } else if (v.getId() == R.id.activity_vedio_metting_menu_cy_sqfy_iv) {
            //成员这边  申请发言
            if (cySqfyCount==1){
                //申请发言
                //网络请求
                //成员申请发言
                if (curMettingBean !=null){
                    cyApplySpeak(curMettingBean.getSceneID(),1);
                }
            }else if (cySqfyCount==2){
                //取消发言
                cySqfyCount=1;
                mMenuCySqfyIv.setImageDrawable(getResources().
                        getDrawable(R.drawable.icon_cy_sqfy));
                mMenuCySqfyTv.setText("申请发言");

                //成员取消发言
                if (curMettingBean !=null){
                    cyCancelSpeak(curMettingBean.getSceneID(),2);
                }
            }
           // swthView(cySqfyCount,null,6);
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                swthView(onTochcount,mMenuLl,0);
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                break;
            case MotionEvent.ACTION_UP:
                //抬起
                break;

        }
        return false;
    }

    /**
     * 切换视图
     *
     * @param count
     */
    private void swthView(int count, View view, int type) {
        if (view != null) {
            //涉及到试图的显示与否
            if (count % 2 == 0) {
                //影藏
                ActivityTools.setViewState(view, true);

            } else {
                //显示
                ActivityTools.setViewState(view, false);

            }
            count++;
            onTochcount=count;
        } else {
            //不涉及到试图的显示与否
            if (count % 2 == 0){
                //放开
                switch (type){
                    case 0:
                        //打开声音
                        mMenuCloseAvdioIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_audio_open));
                        count++;
                        audioCount=count;
                        SendMediaData.isOpenAudio=true;
                        break;
                    case 1:
                        //打开视频
                        mMenuOpenVedioIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_vedio_open));
                        count++;
                        vedioCount=count;
                        SendMediaData.isOpenVideo=true;
                        break;
                    case 2:
                        //关闭屏幕共享
                        mMenuScreenIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_screen_nor));
                        count++;
                        screenCount=count;
                        //中间屏幕的停止共享按钮 不可见
                        mScreenCenterLl.setVisibility(View.GONE);
                        mFstSfv.setVisibility(View.VISIBLE);

                        ToastUtil.showShort(this, "关闭屏幕共享");
                        break;
//                    case 3:
//                        //关闭成员
////                        mMenuPeopleIv.setImageDrawable(getResources().
////                                getDrawable(R.drawable.icon_people_nor));
//                        count++;
//                        peopleCount=count;
//
//                        ToastUtil.showShort(this, "关闭成员");
//                        break;
//                    case 4:
//                        //关闭邀请成员
//                        mMenuAskPeopleIv.setImageDrawable(getResources().
//                                getDrawable(R.drawable.icon_ask_people_nor));
//                        count++;
//                        askPeopleCount=count;
//
//                        ToastUtil.showShort(this, "关闭邀请成员");
//                        break;
                    case 5:
                        //前置摄像头
                        count++;
                        cammerCount=count;
                        //切换前置摄像头
                        Intent intent2 = new Intent(VideoService.ACTION_SWTICH_FRONT_CAMERA);
                        sendBroadcast(intent2);

                        break;
                    case 6:
                        //成员 停止发言
                        mMenuCySqfyIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_cy_jsfy));
                        mMenuCySqfyTv.setText("申请发言");
                        count++;
                        cySqfyCount=count;

                        //成员取消发言
                        if (curMettingBean !=null){
                            cyCancelSpeak(curMettingBean.getSceneID(),2);
                        }
                        break;

                }
            }else {
                //点击
                switch (type){
                    case 0:
                        mMenuCloseAvdioIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_audio_close));

                        count++;
                        audioCount=count;
                        SendMediaData.isOpenAudio=false;
                        break;
                    case 1:
                        mMenuOpenVedioIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_vedio_close));
                        count++;
                        vedioCount=count;
                        SendMediaData.isOpenVideo=false;
                        break;
                    case 2:
                        mMenuScreenIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_screen_pr));
                        count++;
                        screenCount=count;

                        //中间屏幕的按钮可见  弹出框
                        if (customScreenDialog !=null && !(customScreenDialog.isShowing())){
                            customScreenDialog.show();
                        }

                        ToastUtil.showShort(this, "打开屏幕共享");
                        break;
//                    case 3:
////                        mMenuPeopleIv.setImageDrawable(getResources().
////                                getDrawable(R.drawable.icon_people_pr));
//                        count++;
//                        peopleCount=count;
//                        ActivityTools.startActivity(VedioMettingActivity.this,
//                                JoinMettingPeopleListActivity.class,false);
//                        ToastUtil.showShort(this, "开启成员");
//                        break;
//                    case 4:
//                        mMenuAskPeopleIv.setImageDrawable(getResources().
//                                getDrawable(R.drawable.icon_ask_people_pr));
//                        count++;
//                        askPeopleCount=count;
//                        ToastUtil.showShort(this, "开启邀请成员");
//                        break;
                    case 5:
                        count++;
                        cammerCount=count;
                        //切换后置摄像头
                        Intent intent2 = new Intent(VideoService.ACTION_SWTICH_REAR_CAMERA);
                        sendBroadcast(intent2);
                        break;

                    case 6:
                        //成员 申请发言
                        mMenuCySqfyIv.setImageDrawable(getResources().
                                getDrawable(R.drawable.icon_cy_sqfy));
                        mMenuCySqfyTv.setText("结束发言");
                        count++;
                        cySqfyCount=count;

                        //网络请求
                        //成员申请发言
                        if (curMettingBean !=null){
                            cyApplySpeak(curMettingBean.getSceneID(),1);
                        }
                        break;

                }
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            //停止计时
            stopTime();
            //销毁端口
            if (mediaServiceUitl !=null){
                mediaServiceUitl.destorySendPort();
            }
        }else {
            //停止计时
            stopTime();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                    //第三方
                    // 离开会议
                    if (isCreatMeeting){
                        if (customTextDialog !=null && !(customTextDialog.isShowing())){
                            customTextDialog.show();
                        }
                    }else {
                        if (cyLeaveMeetingDialog !=null && !(cyLeaveMeetingDialog.isShowing())){
                            cyLeaveMeetingDialog.show();
                        }
                    }
                }else {
                    //非第三方
                    //推出会议
                    if (isMember){
                        //成员
                        if (cyLeaveMeetingDialog !=null && !(cyLeaveMeetingDialog.isShowing())){
                            cyLeaveMeetingDialog.show();
                        }
                    }else {
                        //主席
                        if (customTextDialog !=null && !(customTextDialog.isShowing())){
                            customTextDialog.show();
                        }
                    }

                }


                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    
    @Override
	protected void onReceivePlay(String sessionId) {

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {
			SessionManager.receivePlay(null, true, true, sessionId);
			ToolLog.i("===VedioMettingActivity::onReceivePlay (收到呼叫)");
		} else {
			ToolLog.i("===VedioMettingActivity::onReceivePlay (sessionId match failed) sessionId:"
					+sessionId + "; getSessionId:"+session.getSessionId());
		}
	}

	@Override
	protected void onPlaySuccess(String sessionId) {

        PLog.d("===VedioMettingActivity::onPlaySuccess ================111=========");
		Session session = SessionManager.getSessionBySessionId(sessionId);
        PLog.d("===VedioMettingActivity::onPlaySuccess ================222=========session:" +session);
		if (session != null) {
            ToolLog.i("===VedioMettingActivity::onPlaySuccess ================333=========");
            session.setFinish(true);

            if (mMeetingRtpidList.size() > 0) {

                PLog.d("===VedioMettingActivity::onPlaySuccess ================666=========index:"+ (mMeetingRtpidList.size() - 1));
                ParseMeetingRtpidBean databean = mMeetingRtpidList.get(mMeetingRtpidList.size() - 1);
                String videoRtpid = databean.getVideoRTPId();
                String audioRtpid = databean.getAudioRTPId();
                int tmpVideoRtpid = (videoRtpid == null || videoRtpid.length() <= 0) ? -1 : Integer.valueOf(videoRtpid);
                int tmpAudioRtpid = (audioRtpid == null || audioRtpid.length() <= 0) ? -1 : Integer.valueOf(audioRtpid);

                if (tmpAudioRtpid == session.getAudioRtpid() || tmpVideoRtpid == session.getVideoRtpid()) {

                    PLog.d("===VedioMettingActivity::onPlaySuccess ================777=========");
                    XTMediaSource source = SipManager.media_map.get(session.getSessionId());
                    if (source != null) {
                        PLog.d("===VedioMettingActivity::onPlaySuccess ================888=========");
                        if (databean.getScreenIndex().equals("0")) {
                            mXTremoteMediaPlay.setMediaSource(source);
                            mXTremoteMediaPlay.Play();
                            if (mXTremoteMedia2.isPlaying()) {
                                mXTremoteMedia2.stop();
                            }
                            mZstSfv.setVisibility(View.VISIBLE);
                            mFstSfv.setVisibility(View.GONE);
                            session.setInMapPosition(0);
                        } else {
                            mXTremoteMedia2.setMediaSource(source);
                            mXTremoteMedia2.Play();
                            if (mXTremoteMediaPlay.isPlaying()) {
                                mXTremoteMediaPlay.stop();
                            }
                            mZstSfv.setVisibility(View.GONE);
                            mFstSfv.setVisibility(View.VISIBLE);
                            session.setInMapPosition(1);
                        }
                        mSessionId = session.getSessionId();
                        PLog.d("===VedioMettingActivity::onPlaySuccess (呼叫成功)");
                    }
                }
            }
		} else {
            PLog.d("===VedioMettingActivity::onReceivePlay (sessionId match failed) sessionId:"
					+sessionId + "; getSessionId:"+session.getSessionId());
		}
	}

	@Override
	protected void onPlayFail(String sessionId) {
		// TODO 是否需要发送上层业务通知？

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {

            PLog.d("===VedioMettingActivity::onPlayFail (呼叫失败)");
            stopRemotePlay(sessionId, session.getInMapPosition());
			SessionManager.clearSingleSession(sessionId);
			
		} else {
            PLog.d("===VedioMettingActivity::onPlayFail (sessionId match failed) sessionId:"
					+sessionId + "; getSessionId:"+session.getSessionId());
		}
	}

	@Override
	protected void onReceiveBye(String sessionId) {

		Session session = SessionManager.getSessionBySessionId(sessionId);
		if (session != null) {

            stopRemotePlay(sessionId, session.getInMapPosition());
			SessionManager.clearSingleSession(sessionId);

            PLog.d("===VedioMettingActivity::onReceiveBye (呼叫bye)");
		} else {
            PLog.d("===ActivityPlaying::onReceiveBye (sessionId match failed) sessionId:"
					+sessionId + "; getSessionId:"+session.getSessionId());
		}
	}

	private void stopRemotePlay(String sessionId, int inMapPosition) {

		if (SipManager.media_map != null && SipManager.media_map.size() > 0) {
			XTMediaSource source = SipManager.media_map.remove(sessionId);
			if (source != null) {
                if (inMapPosition == 0) {
                    mXTremoteMediaPlay.stop();
                } else if (inMapPosition == 1) {
                    mXTremoteMedia2.stop();
                }
				if (!isBackground) {
					source.releaseClientPorts();
					source.clear();
				}
			}
		}
        PLog.d("===VedioMettingActivity::stopRemotePlay (结束)");
	}

    @Override
    protected void onResume() {
        super.onResume();

        if (mMeetingRtpidList.size() == 1) {

//            //方法一：
//            mXTremoteMediaPlay.onlyPlayNotCreateTransmit();

          try{
              //方法二：
              XTMediaSource source = SipManager.media_map.get(mSessionId);
              if (!source.isValidHandle()) {
                  source.initClientParams(Session.PLAY);
              }
              mXTremoteMediaPlay.Play();
          }catch (Exception e){
              e.getMessage();
          }
        } else if (mMeetingRtpidList.size() == 2) {
            mXTremoteMedia2.onlyPlayNotCreateTransmit();
        }
        isOver = userMessge.isCountOver();
        if (!isOver){
            if (isCreatMeeting) {
                if (countdownUtil != null && !(countdownUtil.isRunning())) {
                    int timeCount = userMessge.getTimeCount();
                    countdownUtil.setTotalTime(timeCount*1000);
                    countdownUtil.setIntervalTime(1*1000);
                    countdownUtil.start();
                }
            }
        }

    }
    /**
     * 在界面被完全挡住的时候停止解码
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mMeetingRtpidList.size() == 1) {

            //方法一：
            //mXTremoteMediaPlay.onlyStopPlaySaveTransmit();

            try{
                //方法二：
                mXTremoteMediaPlay.stop();
            }catch (Exception e){
                e.getMessage();
            }
        } else if (mMeetingRtpidList.size() == 2) {
            //mXTremoteMedia2.onlyStopPlaySaveTransmit();
        }

        //主席、单聊才有倒计时，监听对方是否进来了
        isOver = userMessge.isCountOver();
        if (!isOver){
            if (isCreatMeeting) {
                if (countdownUtil != null && (countdownUtil.isRunning())) {
                    countdownUtil.stop();
                }
            }
        }
    }


    //////////////////////////////////////网络请求/////////////////////////////////////

    /**
     * 主席创建会议
     * @param mettingTitle
     * @param type  0
     */
    private void createMetting(String mettingTitle,String members1, int type) {
        PLog.d("VedioMettingActivity","----------createMetting------->主席正在创建会议");
        requestType=type;
        String sceneName=mettingTitle;
        String description="";
        boolean isMediaProcessing=true;//拼接会议
        String operatorName=UserMessge.getInstans(this).getUserName();
        boolean needPassword=false;
        String password="" ;
        String members=members1;
        showLoadingDaliog("正在创建会议中...");
        WebSocketCommand.getInstance().onWssCreateMeeting(sceneName,description,
                isMediaProcessing,operatorName,needPassword,password,members);
    }

    /**
     * 主席停止会议
     * @param mettingID
     */
    private void stopMetting(String mettingID){
        PLog.d("VedioMettingActivity","----------stopMetting------->创建者正在停止会议");

        WebSocketCommand.getInstance().onWssStopMeeting(mettingID);
		
		Intent intent = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
        sendBroadcast(intent);
    }

    /**
     * 成员离开会议
     * @param mettingID
     */
    private void leaveMetting(String mettingID){
        PLog.d("VedioMettingActivity","----------leaveMetting------->成员正在离开会议");
        WebSocketCommand.getInstance().onWssMemberLeaveMeeting(mettingID);
		
		Intent intent = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
        sendBroadcast(intent);
    }

    /**
     * 成员加入会议
     * @param mettingID
     * @param type  6
     */
    private void joinMetting(String mettingID,int type){
        requestType=type;
        PLog.d("VedioMettingActivity","----------joinMetting------->成员正在加入会议");
        WebSocketCommand.getInstance().onWssMemberJoinMeeting(mettingID);
    }


    /**
     * 主席收回成员发言
     * @param mettingID
     */
    private void backSpeak(String mettingID){
        WebSocketCommand.getInstance().onWssCancelSpeaker(mettingID);
    }


    /**
     * 成员申请发言
     * @param mettingID
     * @param type  1
     */
    private void cyApplySpeak(String mettingID, int type){
        requestType=type;
        WebSocketCommand.getInstance().onWssMemberApplySpeak(mettingID);
    }


    /**
     * 成员取消发言
     * @param mettingID
     * @param type  2
     */
    private void cyCancelSpeak(String mettingID, int type){
        requestType=type;
        WebSocketCommand.getInstance().onWssMemberCancelSpeak(mettingID);
    }


    /**
     * 主席同意成员发言
     * memberID  这个是主席接收到成员申请发言指令后服务返回给主席的数据里面有
     * @param mettingID
     */
    private void zxAgreeSpeak(String mettingID, String memberID){
        WebSocketCommand.getInstance().onWssAcceptSpeaker(mettingID,memberID);
    }


    /**
     * 主席拒绝成员发言
     * memberID  这个是主席接收到成员申请发言指令后服务返回给主席的数据里面有
     * @param mettingID
     */
    private void zxRefuseSpeak(String mettingID, String memberID){
        WebSocketCommand.getInstance().onWssRefuseSpeaker(mettingID,memberID);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * 格式化
     * @param obj
     */
    private JoinMettingBean swtichData(ParseMeetingPeopleListBean obj) {
        JoinMettingBean joinMettingBean=null;
        ParseMeetingPeopleListBean.ParamsBean params = obj.getParams();
        if (params !=null){
            joinMettingBean=new JoinMettingBean();
            String sceneID = params.getSceneID();
            String sceneName = params.getSceneName();

            joinMettingBean.setSceneID(sceneID);
            joinMettingBean.setSceneName(sceneName);
            joinMettingBean.setUserID("");
            joinMettingBean.setUserName("");
            List<JoinMettingBean.MembersBean> membersBeen=new ArrayList<>();
            List<ParseMeetingPeopleListBean.ParamsBean.MembersBean> members = params.getMembers();
            if (members !=null && members.size()>0){
                for (int i = 0; i < members.size(); i++) {
                    JoinMettingBean.MembersBean bean=new JoinMettingBean.MembersBean();
                    bean.setRole(members.get(i).getRole());
                    bean.setUserID(members.get(i).getUserID());
                    bean.setUserName(members.get(i).getUserName());
                    bean.setIndex(members.get(i).getIndex());
                    bean.setStatus(members.get(i).getStatus());
                    membersBeen.add(bean);
                }

            }
            joinMettingBean.setMembers(membersBeen);
        }
        return joinMettingBean;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1000:
                if (requestCode==1000){
                    //主席
                    //从参会成员列表中来的
                    //主席点击成员后，点击指定成员发言后返回的   界面要显示收回发言
                    ActivityTools.setViewState(mShfyBtn, false);
                    isStartSpeak =true;
                    //显示悬浮框
                    Intent intent = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                    sendBroadcast(intent);

                    String json = data.getStringExtra("JoinMettingPeopleBean");
                    curMemberBean = FastJsonTools.json2BeanObject(json, JoinMettingPeopleBean.class);
                }else if (requestCode==1001){
                    //成员
                    //从参会成员列表中来的
                    //成员只是查看
                    //显示悬浮框
                    Intent intent = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                    sendBroadcast(intent);
//                    String json = data.getStringExtra("JoinMettingPeopleBean");
//                    curMemberBean = FastJsonTools.json2BeanObject(json, JoinMettingPeopleBean.class);
                }
                break;
            case 1001:
                //主席  成员没有这个，成员不能点击操作
                //从参会成员列表中来的
                //主席点击成员后，点击删除成员后返回的
//                String json = data.getStringExtra("JoinMettingBean");
//                if (json !=null && json.length()>0){
//                    curMettingBean = FastJsonTools.json2BeanObject(json, JoinMettingBean.class);
//                }
//
//                String sceneName = curMettingBean.getSceneName();
//                ToastUtil.showShort(VedioMettingActivity.this,sceneName);
                //显示悬浮框
                Intent intent1 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent1);
                break;
            case 1002:
                //主席点击参会成员列表，然后邀请成员后返回的
//                String json = data.getStringExtra("JoinMettingBean");
//                if (json !=null && json.length()>0){
//                    curMettingBean = FastJsonTools.json2BeanObject(json, JoinMettingBean.class);
//                }
                //显示悬浮框
                Intent intent3 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent3);
                break;
            case 1003:
                //主席  成员没有这个，成员不能点击操作
                //点击邀请成员 成员邀请成功返回的
//                String json1 = data.getStringExtra("JoinMettingBean");
//                if (json1 !=null && json1.length()>0){
//                    curMettingBean = FastJsonTools.json2BeanObject(json1, JoinMettingBean.class);
//                }
//
//                String sceneName1 = curMettingBean.getSceneName();
//                ToastUtil.showShort(VedioMettingActivity.this,sceneName1);

                //显示悬浮框
                Intent intent2 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent2);
                break;
            case 1004:
                //主席  成员没有这个，成员不能点击操作
                //点击邀请成员 成员邀请成功返回的
                //这个是从第三方来的
//                String json1 = data.getStringExtra("JoinMettingBean");
//                if (json1 !=null && json1.length()>0){
//                    curMettingBean = FastJsonTools.json2BeanObject(json1, JoinMettingBean.class);
//                }
//
//                String sceneName1 = curMettingBean.getSceneName();
//                ToastUtil.showShort(VedioMettingActivity.this,sceneName1);

                //显示悬浮框
                Intent intent4 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent4);
                break;

            case 1005:
                //主席同意成员发言  从参会列表来的
                //显示悬浮框
                Intent intent5 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent5);
                break;

            case 1006:
                //主席同意成员发言  从通讯录列表来的
                //显示悬浮框
                Intent intent6 = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent6);
                break;

            case 1010:
                //成员从参会列表界面来的
                //成员退出会议
                if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                    if (!isCreatMeeting){
                        if (curMettingBean !=null){
                            leaveMetting(curMettingBean.getSceneID());
                        }
                        showLoadingDaliog("正在退出聊天室...");

                        mHandler.sendEmptyMessageDelayed(1001,1000);
                    }

                }else {
                    if (isMember){
                        if (curMettingBean !=null){
                            leaveMetting(curMettingBean.getSceneID());
                        }
                        showLoadingDaliog("正在退出会议...");
                        mHandler.sendEmptyMessageDelayed(1000,1000);
                    }

                }

                break;
            case 1011:
                //参会列表来的
                if (requestCode==1020){
                    //创建者 当成员都退出时，主席停止会议
                    if (curMettingBean !=null){
                        stopMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出聊天室...");
                    mHandler.sendEmptyMessageDelayed(1001,1000);
                }else if (requestCode==1021){
                    //成员  收到主席退会后，自动退出会议
                    if (curMettingBean !=null){
                        leaveMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出聊天室...");

                    mHandler.sendEmptyMessageDelayed(1001,1000);
                }
                break;
            case 1012:
                //主席在规定时间内未收到成员加入会议的超时
                //创建者
                if (curMettingBean !=null){
                    stopMetting(curMettingBean.getSceneID());
                }
                showLoadingDaliog("正在退出聊天室...");
                mHandler.sendEmptyMessageDelayed(1003,1000);
                break;
        }

        if (data==null){
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //从其他界面直接按返回键返回的
                //隐藏悬浮框
                Intent intent = new Intent(VideoService.ACTION_HIDE_CAPTURE_WINDOW);
                sendBroadcast(intent);
            }else {
                //从其他界面直接按返回键返回的
                //显示悬浮框
                Intent intent = new Intent(VideoService.ACTION_SHOW_CAPTURE_WINDOW);
                sendBroadcast(intent);
            }

        }
    }



    /**
     * 主席需要监听成员申请发言的指令（主席可以接收成员发言、可以拒绝成员发言，
     * 主席收到指令是，处理完毕不需马上回到会议界面）
     * 、监听成员退出会议后有重新进入会议，主席默认让其进入。
     *
     * 成员需要监听主席发来的指定发言的指令（来判断是否为发言人，
     * 屏幕共享是否显示，成员自动进入发言，且成员自动进入会议界面，成员导航中应显示取消发言，可以申请取消发言）、
     * 监听主席发送过来的（成员自己申请发言）是否被通过发言（通过发言，为发言人，且进入会议界面，
     * 没有通过发言就是普通成员，不需要进入会议界面，没有通过发言有对话框提示，通过发言没有对话框提示）
     *
     * 需要在三个界面重写结果消息的方法，
     * 在参会人员列表和邀请成员列表（包含通讯录界面）（成员需要跳转到会议界面的，用onActivityResult
     * 来进行activity的传值，都要将值传到会议界面，传到会议界面是，注意会议界面的接收消息的地方，防止重复，
     * 可以用标识判断是否为同一个数据，传值需要传服务器返回的字符串和自己经过处理），成员在非会议界面自动进入会议界面的
     * 都属于发言人
     *
     * wss请求返回的结果
     * @param msg
     */
    protected void onReceiveWssMessage(String msg) {
        hideLoadingDialog();
        if (msg.isEmpty()) {
            return;
        }else if (msg.indexOf(WssContant.WSS_STOP_METTING) >= 0) {


        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {
            //快速会议来的
            if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
                //创建会议
                //主席，自己入会
                if (msg.indexOf("sceneID") >= 0){
                    if (requestType==0){
                        //代表是创建会议后直接加入会议
                        //设置开始计时时间
                        mSjTv.setBase(SystemClock.elapsedRealtime() );
                        //停止计时
                        mSjTv.stop();
                        //启动计时器
                        mSjTv.start();

                        curMettingBean = JsonParseUilt.getMettingListBeen(msg);
                        PLog.i("VedioMettingActivity: ------------创建会议----"+msg);
                    }

                }
            }else if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //从第三发来的
                if (msg.indexOf("sceneID") >= 0){
                    if (requestType==0){
                        //创建会议
                        //主席，自己入会
                        requestType=-1;
                        //代表是创建会议后直接加入会议
                        //设置开始计时时间
                        mSjTv.setBase(SystemClock.elapsedRealtime() );
                        //停止计时
                        mSjTv.stop();
                        //启动计时器
                        mSjTv.start();
                        curMettingBean = JsonParseUilt.getMettingListBeen(msg);
                        String groupID = userMessge.getThirdPartyGroupID();
                        if (groupID.equals(XTContants.MEETING_SINGLE)){
                            //单聊
                            if (isCreatMeeting){
                                //向客户那边发送广播
                                sendKXBroadcast();
                            }
                        }else if (groupID.equals(XTContants.MEETING_GROUP)){
                            //群聊  需要发送广播,告知邀请的人
                            if (isCreatMeeting){
                                if (choiceJson !=null && choiceJson.length()>0){
                                    List<JoinMettingPeopleBean> list = FastJsonTools.
                                            jsonToList(choiceJson, JoinMettingPeopleBean.class);
                                    String meetingId = curMettingBean.getSceneID();
                                    if (meetingId != null && meetingId.length() > 0) {
                                        InvitePeopleBean invitePeopleBean = new InvitePeopleBean();
                                        invitePeopleBean.setMeetingType(userMessge.getThirdPartyGroupID());
                                        invitePeopleBean.setMeetingID(meetingId);
                                        List<InvitePeopleBean.ChoicePeople> choicePeopleList = new ArrayList<>();
                                        for (int i = 0; i < list.size(); i++) {
                                            InvitePeopleBean.ChoicePeople choicePeople = new InvitePeopleBean.ChoicePeople();
                                            choicePeople.setUserId(list.get(i).getUseId());
                                            choicePeople.setUserName(list.get(i).getName());
                                            choicePeopleList.add(choicePeople);
                                        }
                                        invitePeopleBean.setPeopleBeen(choicePeopleList);
                                        KXBroadstcast.sendBroadcastPeoples(VedioMettingActivity.this, invitePeopleBean);
                                    } else {
                                        ToastUtil.showShort(VedioMettingActivity.this, "会议id不能为空");
                                    }

                                }

                            }


                        }
                        PLog.i("VedioMettingActivity: ------------创建会议----"+msg);
                    }else if (requestType==6){
                        requestType=-1;
                        //成员 加入会议
                        PLog.i("VedioMettingActivity: ------------加入会议----"+msg);
                    }

                    //保存会议
                    saveMeetingRequest(curMettingBean);

                }

            }
            //获取参会成员列表
            if (msg !=null && msg.length()>0){
                ParseMeetingPeopleList beanObject = FastJsonTools.json2BeanObject(msg, ParseMeetingPeopleList.class);
                String body = beanObject.getBody();
                if (body !=null && body.length()>0){
                    ParseMeetingPeopleListBean obj = FastJsonTools.
                            json2BeanObject(body, ParseMeetingPeopleListBean.class);
                    if (obj !=null){
                        if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                            String groupID = userMessge.getThirdPartyGroupID();
                            if (groupID.equals(XTContants.MEETING_SINGLE)){
                                //单聊
                                //将userID切换成客户的
                                curMettingBean = swtichData(obj);
                                List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
                                //保存已经在会人员的信息
                                int num =0;
                                if (members.size()>0){
                                    num = members.size();
                                }else {
                                    num =0;
                                }
                                userMessge.setonLinePeople(num);
                            }else if (groupID.equals(XTContants.MEETING_GROUP)){
                                //群聊
                                //将userID切换成客户的
                                curMettingBean = swtichData(obj);
                                swtichKXUserIDRequest(curMettingBean);

                                List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
                                //保存已经在会人员的信息
                                int num =0;
                                if (members.size()>0){
                                    num = members.size();
                                }else {
                                    num =0;
                                }
                                userMessge.setonLinePeople(num);
                            }

                        }else {
                            curMettingBean = swtichData(obj);
                        }

                    }

                }
                PLog.i("VedioMettingActivity: ------------获取会议成员列表----"+msg);
            }


        }else if (msg.indexOf(WssContant.WSS_GROUP_START_COMMOND) >= 0
                || msg.indexOf(WssContant.WSS_GROUP_REFRESH_COMMOND) >= 0) {
//            if (requestType==1){
//                requestType=-1;
//                //成员点击申请发言后
//                //成员收到主席的结果
//                if (msg.indexOf(WssContant.WSS_GROUP_REFRESH_COMMOND) >= 0){
//                   //收到主席允许发言
//                    cySqfyCount=2;
//
//                    mMenuCySqfyIv.setImageDrawable(getResources().
//                            getDrawable(R.drawable.icon_cy_jsfy));
//                    mMenuCySqfyTv.setText("结束发言");
//                }
//            }

            if (msg !=null && msg.length()>0){
                PLog.d("VedioMettingActivity","---------informGroupStartMedia/informRefreshGroupMedia------->msg="+msg);

                mMeetingRtpidList.clear();

                ParseMeetingRtpidInfo rtpidInfos = FastJsonTools.json2BeanObject(msg, ParseMeetingRtpidInfo.class);
                if (rtpidInfos !=null){
                    String rtpidBody = rtpidInfos.getBody();
                    if (rtpidBody != null && rtpidBody.length() > 0) {
                        ParseMeetingRtpidBody bodyInfo = FastJsonTools.json2BeanObject(rtpidBody, ParseMeetingRtpidBody.class);
                        if (bodyInfo != null) {
                            String beanData = bodyInfo.getParams().getData();
                            if (beanData != null && beanData.length() > 0) {
                                try {
                                    JSONArray objArray = new JSONArray(beanData);
                                    for (int i=0; i<objArray.length(); i++) {

                                        JSONObject obj = objArray.getJSONObject(i);
                                        ParseMeetingRtpidBean bean = new ParseMeetingRtpidBean();
                                        bean.setScreenIndex(obj.getString("screenIndex"));
                                        bean.setResourceID(obj.getString("resourceID"));
                                        bean.setResourceType(obj.getString("resourceType"));
                                        bean.setVideoRTPId(obj.getString("videoRTPId"));
                                        bean.setAudioRTPId(obj.getString("audioRTPId"));

                                        mMeetingRtpidList.add(bean);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                onPlayByRtpid();
            }

        }else if (msg.indexOf("publishAcceptSpeakerFromConference")>0 ||
                msg.indexOf(WssContant.WSS_MEETING_APPLY_SPEAK_SHOW)>0){
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //第三方
            }else {
                if (!isMember){
                    //主席
                    //主席监听成员发送申请的指令
                    if (msg !=null && msg.length()>0){
                        ParseMeetingApplySpeark parseMeetingApplySpeark = FastJsonTools.json2BeanObject(msg,
                                ParseMeetingApplySpeark.class);
                        String body = parseMeetingApplySpeark.getBody();
                        if (body !=null && body.length()>0){
                            ParseMeetingApplySpeaskBody speaskBody = FastJsonTools.json2BeanObject(body,
                                    ParseMeetingApplySpeaskBody.class);
                            ParseMeetingApplySpeaskBody.ParamsBeanX params = speaskBody.getParams();
                            String contentText = params.getText();
                            String title = params.getTitle();
                            String memberID ="";
                            String sceneID ="";
                            List<ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean> buttons = params.getButtons();
                            if (buttons !=null && buttons.size()>0){
                                for (int i = 0; i < buttons.size(); i++) {
                                    ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean.CommandBean command = buttons.get(i).getCommand();
                                    if (command !=null){
                                        ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean.CommandBean.ParamsBean params1 = command.getParams();
                                        if (params1 !=null){
                                            memberID = params1.getMemberID();
                                            sceneID = params1.getSceneID();
                                            if (memberID !=null && memberID.length()>0 &&
                                                    sceneID !=null && sceneID.length()>0 ){
                                                //主席进行允许成员发言和拒绝成员发言
                                                curMettingBean.setSceneID(sceneID);
                                                curMemberBean=new JoinMettingPeopleBean();
                                                curMemberBean.setName(memberID);
                                                curMemberBean.setUseId(memberID);
                                                curMemberBean.setMeetingID(sceneID);
                                                initIsApplySpaekDialog(title,contentText,sceneID,memberID);
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }else {
                    //成员
                }
            }

        }else if (msg.indexOf(WssContant.WSS_MEETING_ZX_OUT_MEETING)>=0){
            //成员收到主席退会的信息
            //成员离开会议
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                if (!isCreatMeeting){
                    if (curMettingBean !=null){
                        PLog.d("VedioMettingActivity","----------成员收到主席退会------->成员正在自动退出会议");
                        leaveMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出聊天室...");

                    mHandler.sendEmptyMessageDelayed(1001,1000);
                }

            }else {
                if (isMember){
                    if (curMettingBean !=null){
                        leaveMetting(curMettingBean.getSceneID());
                    }
                    showLoadingDaliog("正在退出会议...");
                    mHandler.sendEmptyMessageDelayed(1000,1000);
                }

            }

        }
    }

    /**
     * 将兴图的userID转换为空信的userID
     *
     * @param curMettingBean
     */
    private void swtichKXUserIDRequest(JoinMettingBean curMettingBean) {
        if (curMettingBean != null) {
            String sceneID = curMettingBean.getSceneID();
            List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
            if (members != null && members.size() > 0) {
                List<String> userIDs = new ArrayList<>();
                for (int i = 0; i < members.size(); i++) {
                    userIDs.add(members.get(i).getUserID());
                }

                //网络请求，获取空信的userID
                if (userIDs != null && userIDs.size() > 0) {
                    //参数
                    StringBuffer buffer=new StringBuffer();
                    for (int i = 0; i < userIDs.size(); i++) {
                        if (i==userIDs.size()-1){
                            buffer.append(userIDs.get(i));
                        }else {
                            buffer.append(userIDs.get(i)).append(",");
                        }
                    }

                    String json = buffer.toString();
                    PLog.d("VedioMettingActivity","-----swtichKXUserIDRequest--->json ="+json);
                    List<NameValuePair> params = MoudleParams.getKXUserIdParams(this, json);
                    String url=params.get(0).getValue();
                    //有参数，需要看下url里面最后一个是否带有=符号
                    params.remove(0);
                    //对参数进行格式化
                    String p = URLEncodedUtils.format(params, HTTP.UTF_8);
                    urlGet = url+"?" + p;
                    PLog.d("VedioMettingActivity","----------在线人员未转换的XTUserID------------->p="+p);
                    //请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //初始化OkHttpClient对象时进行信任证书的操作
                                OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
                                mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
                                mBuilder.hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier());
                                Request request = new Request.Builder()
                                        .url(urlGet)
                                        .build();
                                OkHttpClient client = mBuilder.build();
                                Response response = null;
                                response = client.newCall(request).execute();
                                String result=response.body().string();
                                Message message=Message.obtain();
                                message.obj=result;
                                message.what=1;
                                mGetHandler.sendMessage(message);
                            } catch (IOException e) {
                                String error = e.getMessage();
                                Message message=Message.obtain();
                                message.obj=error;
                                message.what=2;
                                mGetHandler.sendMessage(message);
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
    }



    /**
     * 保存会议
     */
    private void saveMeetingRequest(JoinMettingBean curMettingBean){
        if (curMettingBean !=null){
            String sceneID = curMettingBean.getSceneID();
            //网络请求
            if (sceneID != null && sceneID.length() > 0) {
                //参数
                Map<String,String> map=new HashMap<>();
                map.put("conferenceId",sceneID);
                map.put("userId",userMessge.getUserID());
                if (isCreatMeeting){
                    map.put("roleType","chairman");
                }else {
                    map.put("roleType","member");
                }

                List<NameValuePair> params = MoudleParams.saveMeetingParams(this, map);
                //请求
                if (instans != null) {
                    instans.sendRequest(params, false, Constants.HTTP_POST_SAVE_MEETING);
                }
            }

        }
    }


    /**
     * 删除会议
     */
    private void deletMeetingRequest(JoinMettingBean curMettingBean){
        if (curMettingBean !=null){
            String userID = userMessge.getUserID();
            //网络请求
            if (userID != null && userID.length() > 0) {
                //参数
                Map<String,String> map=new HashMap<>();
                map.put("userId",userID);
                List<NameValuePair> params = MoudleParams.deletMeetingParams(this, map);
                //请求
                if (instans != null) {
                    instans.sendRequest(params, true, Constants.HTTP_GET_DELET_MEETING);
                }
            }

        }
    }



    /**
     * 向客户那边发送广播
     */
    private void sendKXBroadcast() {
        if (userMessge !=null && curMettingBean !=null){
            CreateMeetingBean bean=new CreateMeetingBean();
            bean.setMeetingID(curMettingBean.getSceneID());
            bean.setMemberID(userMessge.getThirdPartyUserName());
            bean.setChairmanID(userMessge.getThirdPartyUserName());
            bean.setMeetingType(userMessge.getThirdPartyGroupID());
            KXBroadstcast.sendBroadcastCreateMeeting(VedioMettingActivity.this,bean);

        }
    }


    private void sendMemberLeaveMeetingBroadcast(){

        MemberLeaveBean leaveBean=new MemberLeaveBean();
        leaveBean.setMeetingID(curMettingBean.getSceneID());
        leaveBean.setMemberID(userMessge.getThirdPartyUserName());
        leaveBean.setMeetingType(userMessge.getThirdPartyGroupID());
        leaveBean.setChairmanID(userMessge.getThirdPartyChairmanID());
        KXBroadstcast.sendBroadcastMemberLeave(VedioMettingActivity.this,leaveBean);
    }

    private void onPlayByRtpid() {

        PLog.d("===VedioMettingActivity::WSS ==========mMeetingRtpidList.size:" + mMeetingRtpidList.size());

        if (mMeetingRtpidList.size() > 0) {
            ParseMeetingRtpidBean databean = mMeetingRtpidList.get(mMeetingRtpidList.size() - 1);
                String videoRtpid = databean.getVideoRTPId();
                String audioRtpid = databean.getAudioRTPId();
                int tmpVideoRtpid = (videoRtpid == null || videoRtpid.length() <= 0) ? -1 : Integer.valueOf(videoRtpid);
                int tmpAudioRtpid = (audioRtpid == null || audioRtpid.length() <= 0) ? -1 : Integer.valueOf(audioRtpid);

                Session session = SessionManager.getSessionByRtpid(tmpVideoRtpid, tmpAudioRtpid);


                if (session != null && session.isFinish()) {

                    XTMediaSource source = SipManager.media_map.get(session.getSessionId());
                    if (source != null) {
                        if (databean.getScreenIndex().equals("0")) {
                            mXTremoteMediaPlay.setMediaSource(source);
                        if (mXTremoteMediaPlay.getPlayCnt() == 0) {
                            mXTremoteMediaPlay.Play();
                        } else {
                            mXTremoteMediaPlay.onlyPlayNotCreateTransmit();
                        }
                            if (mXTremoteMedia2.isPlaying()) {
                            mXTremoteMedia2.onlyStopPlaySaveTransmit();
                            }
                            mZstSfv.setVisibility(View.VISIBLE);
                            mFstSfv.setVisibility(View.GONE);
                            session.setInMapPosition(0);
                        } else {
                            mXTremoteMedia2.setMediaSource(source);
                        if (mXTremoteMedia2.getPlayCnt() == 0) {
                            mXTremoteMedia2.Play();
                        } else {
                            mXTremoteMedia2.onlyPlayNotCreateTransmit();
                        }
                            if (mXTremoteMediaPlay.isPlaying()) {
                            mXTremoteMediaPlay.onlyStopPlaySaveTransmit();
                            }
                            mZstSfv.setVisibility(View.GONE);
                            mFstSfv.setVisibility(View.VISIBLE);
                            session.setInMapPosition(1);
                        }
                        mSessionId = session.getSessionId();
                        ToolLog.i("===VedioMettingActivity::WSS (呼叫成功)");
                    }
                }
            }
        }


    /**
     * 主席停止会议
     * @param meetingId
     */
    @Override
    protected void stopChairManMeeting(String meetingId) {
        if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            //第三方
            if (curMettingBean !=null){
                List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
                if (members !=null && members.size()>0){
                    if (members.size()==1){
                        if (meetingId !=null && meetingId.length()>0){
                            stopMetting(meetingId);
                            showLoadingDaliog("正在退出聊天室...");
                            mHandler.sendEmptyMessageDelayed(1001,1000);
                        }else {
                            ToastUtil.showShort(VedioMettingActivity.this,"会议id为空，无法停止会议");
                        }
                    }
                }

            }

        }
    }


    private Handler mGetHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                String result = (String) msg.obj;
                PLog.d("VedioMettingActivity","-----mGetHandler--->result ="+result);
                //兴图的userID转换为空信userId
                if (result != null && result.length() > 0) {
                    try {
                        //  进行数据解析
                        PLog.d(result);
                        UserIdSwtichBean userIdSwtichBean = FastJsonTools.json2BeanObject(result, UserIdSwtichBean.class);
                        if (userIdSwtichBean !=null){
                            List<UserIdSwtichBean.PeopleBean> data = userIdSwtichBean.getData();
                            if (data !=null && data.size()>0){
                                PLog.d("VedioMettingActivity","----------在线已转换的kxUserID------------->data.size="+data.size()+"data="+data.toString());
                                String partPeoples = userMessge.getThridPartPeoples();
                                String chairmanID = userMessge.getThirdPartyChairmanID();
                                if (partPeoples !=null &&
                                        partPeoples.length()>0){
                                    //客户的人员信息
                                    List<InvitePeopleBean.ChoicePeople> peopleList = FastJsonTools.
                                            jsonToList(partPeoples, InvitePeopleBean.ChoicePeople.class);
                                    //将客户的通讯录的所有信息转换为 JoinMettingPeopleBean
                                    List<JoinMettingPeopleBean> peopleBeen=new ArrayList<>();
                                    for (int i = 0; i < peopleList.size(); i++) {
                                        JoinMettingPeopleBean bean=new JoinMettingPeopleBean();
                                        bean.setChoice(false);
                                        bean.setUseId(peopleList.get(i).getUserId());
                                        bean.setOnline(true);
                                        bean.setName(peopleList.get(i).getUserName());
                                        bean.setMeetingID(curMettingBean.getSceneID());
                                        if (peopleList.get(i).getUserId().equals(chairmanID)){
                                            //主席
                                            bean.setRole("chairman");
                                        }else {
                                            //成员
                                            bean.setRole("member");
                                        }

                                        peopleBeen.add(bean);

                                    }
                                    for (int i = 0; i < peopleBeen.size(); i++) {
                                        //替换成空信的
                                        JoinMettingPeopleBean kxBean = peopleBeen.get(i);
                                        String kxUserID = kxBean.getUseId();
                                        for (int j = 0; j < data.size(); j++) {
                                            //替换成空信的
                                            UserIdSwtichBean.PeopleBean peopleBean = data.get(j);
                                            String kxuserid = peopleBean.getKxuserid();
                                            if (kxuserid.equals(kxUserID)){
                                                //相同，替换
                                                kxBean.setChoice(true);
                                            }else {
                                                //不同
                                                kxBean.setChoice(false);
                                            }

                                        }
                                    }

                                    //添加数据
                                    if (joinMettingPeopleBeanList.size() > 0) {
                                        joinMettingPeopleBeanList.clear();
                                    }
                                    PLog.d("VedioMettingActivity","----------在线已转换的kxUserID------------->joinMettingPeopleBeanList.size="+joinMettingPeopleBeanList.size()+"joinMettingPeopleBeanList="+joinMettingPeopleBeanList.toString());
                                    joinMettingPeopleBeanList.addAll(peopleBeen);

                                    if (isCreatMeeting) {
                                        //向客户发送在会人数的广播
                                        List<InvitePeopleBean.ChoicePeople> choicePeoples=new ArrayList<>();
                                        if (joinMettingPeopleBeanList != null && joinMettingPeopleBeanList.size() > 0) {
                                            for (int i = 0; i < joinMettingPeopleBeanList.size(); i++) {
                                                InvitePeopleBean.ChoicePeople choicePeople = new InvitePeopleBean.ChoicePeople();
                                                choicePeople.setUserId(joinMettingPeopleBeanList.get(i).getUseId());
                                                choicePeople.setUserName(joinMettingPeopleBeanList.get(i).getName());
                                                choicePeoples.add(choicePeople);
                                            }
                                            Gson gson=new Gson();
                                            String json = gson.toJson(choicePeoples);
                                            KXBroadstcast.sendBroadcastPeopleNum(VedioMettingActivity.this, json);
                                        } else {
                                            KXBroadstcast.sendBroadcastPeopleNum(VedioMettingActivity.this, "");
                                        }
                                    }
                                }
                            }else {
                                ToastUtil.showShort(VedioMettingActivity.this,"获取kxUserIds为空");
                            }

                        }else {
                            ToastUtil.showShort(VedioMettingActivity.this,"获取kxUserIds为空");
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }else if (msg.what==2){
                //兴图的userID转换为空信userId
                List<JoinMettingBean.MembersBean> list=new ArrayList<>();
                curMettingBean.setMembers(list);
                ToastUtil.showShort(VedioMettingActivity.this,"转换失败");
                PLog.d("VedioMettingActivity", "----------faile------->userID转换失败");
            }
        }
    };

    @Override
    public void success(int tag, String result) {
        switch (tag){
            case Constants.HTTP_POST_SAVE_MEETING:
                //创建者保存会议
                if (result != null && result.length() > 0) {
                    try {
                        // TODO 进行数据解析
                        PLog.d(result);
                        PLog.d("VedioMettingActivity", "----------success------->保存会议成功");
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
                break;
            case Constants.HTTP_GET_DELET_MEETING:
                //删除会议
                if (result != null && result.length() > 0) {
                    try {
                        // TODO 进行数据解析
                        PLog.d(result);

                        PLog.d("VedioMettingActivity", "----------success------->删除会议成功");
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    public void faile(int tag, String error) {
        switch (tag){
            case Constants.HTTP_POST_SAVE_MEETING:
                //保存会议
                ToastUtil.showShort(VedioMettingActivity.this,"保存会议失败");
                PLog.d("VedioMettingActivity", "----------faile------->保存会议失败");
                break;
            case Constants.HTTP_GET_DELET_MEETING:
                //删除会议
                ToastUtil.showShort(VedioMettingActivity.this,"删除会议失败");
                PLog.d("VedioMettingActivity", "----------faile------->删除会议失败");
                finish();
                break;
        }
    }

    @Override
    public void onRemain(long millisUntilFinished) {
       int timeCount= (int) (millisUntilFinished/1000);
        userMessge.setTimeCount(timeCount);
        PLog.d("VedioMettingActivity","-------onRemain-------->"+timeCount);
    }

    @Override
    public void onFinish() {
        //倒计时已经结束
        isOver=true;
        userMessge.setCountOver(isOver);
        int parseInt =60;
        String callOutTime = userMessge.getCallOutTime();
        if (callOutTime !=null && callOutTime.length()>0){
            parseInt = Integer.parseInt(callOutTime);
        }else {
            parseInt =60;
        }
        userMessge.setTimeCount(parseInt);
        if (isCreatMeeting){
            int onLinePeople = userMessge.getOnLinePeople();
            //监听在会人员数量
            if (onLinePeople<=1){
                //说明对方几个人没有进来，主席退出
                if (curMettingBean !=null){
                    stopMetting(curMettingBean.getSceneID());
                }
                showLoadingDaliog("正在退出聊天室...");
                mHandler.sendEmptyMessageDelayed(1003,1000);
            }

        }

    }

}

