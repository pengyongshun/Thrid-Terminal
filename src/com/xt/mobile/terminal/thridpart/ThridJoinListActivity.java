package com.xt.mobile.terminal.thridpart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.JoinMettingPeopleAdapter;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.JsonParseUilt;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeark;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeaskBody;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingPeopleList;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingPeopleListBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidBody;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingRtpidInfo;
import com.xt.mobile.terminal.network.pasre.join_metting.UserIdSwtichBean;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.thridpart.bean.InvitePeopleBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.activity.VedioMettingActivity;
import com.xt.mobile.terminal.util.CountdownUtil;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.HttpUtils;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.TrustAllCerts;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.zhy.http.okhttp.OkHttpUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThridJoinListActivity extends BaseActivity implements CountdownUtil.OnCountdownListener {
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ListView mListView;
    private TextView mEmptyTv;
    private TextView mLeftTv;

    private List<JoinMettingPeopleBean> list = new ArrayList<>();
    private List<JoinMettingPeopleBean> choiceBeen = new ArrayList<>();
    private List<JoinMettingPeopleBean> onLineBeen = new ArrayList<>();
    private JoinMettingPeopleAdapter mAdapter;
    private JoinMettingPeopleBean currBean;
    private UserMessge userMessge;
    private JoinMettingBean curMettingBean;
    private List<JoinMettingPeopleBean> joinMettingPeopleBeanList = new ArrayList<>();
    private boolean isCreateMeeting;
    private String meetingId = "";
    private String urlGet = "";
    private CountdownUtil countdownUtil;
    private boolean isOver=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_thrid_join_list);
        userMessge = UserMessge.getInstans(this);

        //初始化计时器
        //首次进来的时候
        //初始化计时器
//        int timeCount = userMessge.getTimeCount();
//        countdownUtil = CountdownUtil.newInstance();
//        countdownUtil.setTotalTime(timeCount * 1000);
//        countdownUtil.setIntervalTime(1 * 1000);
//        countdownUtil.callback(this);
//        isOver=userMessge.isCountOver();
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
        isCreateMeeting = getIntent().getBooleanExtra("isCreatMeeting", true);
        String result = getIntent().getStringExtra("json");
        meetingId = getIntent().getStringExtra("meetingId");
        PLog.d("ThridJoinListActivity", "-------onCreate---------->result ="
                + result + "\nmeetingId=" + meetingId + "\nisCreatMeeting=" + isCreateMeeting);
        if (result != null && result.length() > 0) {
            List<JoinMettingPeopleBean> peopleBeen = FastJsonTools.jsonToList(result, JoinMettingPeopleBean.class);
            if (list.size() > 0) {
                list.clear();
            }
            List<JoinMettingPeopleBean> been = new ArrayList<>();
            for (int i = 0; i < peopleBeen.size(); i++) {
                String role = peopleBeen.get(i).getRole();
                if (role.equals("chairman")) {
                    been.add(peopleBeen.get(i));
                    peopleBeen.remove(i);
                }
            }
            list.add(been.get(0));
            list.addAll(peopleBeen);

            if (onLineBeen.size() > 0) {
                onLineBeen.clear();
            }
            if (choiceBeen.size() > 0) {
                choiceBeen.clear();
            }
            for (int i = 0; i < list.size(); i++) {
                JoinMettingPeopleBean bean = list.get(i);
                boolean choice = bean.isChoice();
                if (choice) {
                    onLineBeen.add(bean);
                }
            }

        }


        initView();
        initData();

    }


    private void initView() {
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mListView = (ListView) findViewById(R.id.activity_thrid_join_list_lv);
        mEmptyTv = (TextView) findViewById(R.id.activity_thrid_join_empty_tv);

        mLeftIv.setVisibility(View.VISIBLE);
        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mRightTv.setVisibility(View.GONE);
        mTitleTv.setText(R.string.meeting_group_users);

        mLeftIv.setOnClickListener(this);
        mRightTv.setText("邀请");
        mRightTv.setOnClickListener(this);
        //第三方
        mRightTv.setVisibility(View.VISIBLE);
        mListView.setEnabled(true);
//        if (!isCreateMeeting) {
//            //成员
//            mRightTv.setVisibility(View.GONE);
//            mListView.setEnabled(false);
//        } else {
//            //主席
//            mRightTv.setVisibility(View.VISIBLE);
//            mListView.setEnabled(true);
//        }

    }


    private void initData() {
        if (!isCreateMeeting) {
            mAdapter = new JoinMettingPeopleAdapter(ThridJoinListActivity.this,
                    list, false, false, true);
        } else {
            mAdapter = new JoinMettingPeopleAdapter(ThridJoinListActivity.this,
                    list, true, false, true);
        }
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyTv);
        addListListener();

    }


    private void addListListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int num= choiceBeen.size()+onLineBeen.size();
                if (num<=9){
                    if (list.size() > 0) {
                        //显示弹框
                        currBean = list.get(position);
                        String role = currBean.getRole();
                        if (role.equals("chairman")) {
                            //
                            currBean.setChoice(true);
                            ToastUtil.showShort(ThridJoinListActivity.this,
                                    "不能编辑群主");
                        } else {
                            boolean a = isXT(currBean);
                            if (a) {
                                //此人已在群聊中，不可编辑
                                currBean.setChoice(true);
                                ToastUtil.showShort(ThridJoinListActivity.this,
                                        "此人已在群聊中");
                            } else {
                                boolean choice = currBean.isChoice();
                                if (choice) {
                                    if (choiceBeen.size() > 0 && choiceBeen.contains(currBean)) {
                                        choiceBeen.remove(currBean);
                                    }
                                    currBean.setChoice(false);
                                } else {
                                    currBean.setChoice(true);
                                    if (!choiceBeen.contains(currBean)) {
                                        choiceBeen.add(currBean);
                                    }

                                }
                            }

                        }

                        list.remove(position);
                        list.add(position, currBean);
                        mAdapter.notifyDataSetChanged();


                    }
                }else {
                    ToastUtil.showShort(ThridJoinListActivity.this,"已选择了9个人");
                }

            }
        });

    }

    private boolean isXT(JoinMettingPeopleBean currBean) {
        if (onLineBeen.size() > 0) {
            for (int i = 0; i < onLineBeen.size(); i++) {
                String useId = onLineBeen.get(i).getUseId();
                if (useId.equals(currBean.getUseId())) {
                    //相同
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.left_iv) {
            //返回
            finish();
        } else if (id == R.id.right_tv) {
            //邀请
            if (choiceBeen.size() > 0) {
                int num= choiceBeen.size()+onLineBeen.size();
                if (num<=9){
                    if (meetingId != null && meetingId.length() > 0) {
                        InvitePeopleBean invitePeopleBean = new InvitePeopleBean();
                        invitePeopleBean.setMeetingType(userMessge.getThirdPartyGroupID());
                        invitePeopleBean.setMeetingID(meetingId);
                        List<InvitePeopleBean.ChoicePeople> choicePeopleList = new ArrayList<>();
                        for (int i = 0; i < choiceBeen.size(); i++) {
                            InvitePeopleBean.ChoicePeople choicePeople = new InvitePeopleBean.ChoicePeople();
                            choicePeople.setUserId(choiceBeen.get(i).getUseId());
                            choicePeople.setUserName(choiceBeen.get(i).getName());
                            choicePeopleList.add(choicePeople);
                        }
                        invitePeopleBean.setPeopleBeen(choicePeopleList);
                        KXBroadstcast.sendBroadcastPeoples(ThridJoinListActivity.this, invitePeopleBean);
                        finish();
                    } else {
                        ToastUtil.showShort(ThridJoinListActivity.this, "会议id不能为空");
                    }
                }else {
                    ToastUtil.showShort(ThridJoinListActivity.this,"请选择在9个人以内");
                }

            } else {
                ToastUtil.showShort(ThridJoinListActivity.this, "请选择邀请的人");
            }

        }
    }


    /**
     * 主席停止会议
     *
     * @param meetingId
     */
    @Override
    protected void stopChairManMeeting(String meetingId) {
        if (meetingId != null && meetingId.length() > 0) {
            if (onLineBeen != null && onLineBeen.size() > 0) {
                if (onLineBeen.size() == 1) {
                    Intent intent = new Intent(ThridJoinListActivity.this,
                            VedioMettingActivity.class);
                    setResult(1011, intent);
                    finish();
                }
            }

        } else {
            ToastUtil.showShort(ThridJoinListActivity.this, "会议id为空，无法停止会议");
        }


    }


    /**
     * wss请求返回的结果
     *
     * @param msg
     */
    protected void onReceiveWssMessage(String msg) {
        hideLoadingDialog();
        if (msg.isEmpty()) {
            return;
        } else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {
            //获取参会成员列表
            if (msg != null && msg.length() > 0) {
                ParseMeetingPeopleList beanObject = FastJsonTools.json2BeanObject(msg, ParseMeetingPeopleList.class);
                String body = beanObject.getBody();
                if (body != null && body.length() > 0) {
                    ParseMeetingPeopleListBean obj = FastJsonTools.
                            json2BeanObject(body, ParseMeetingPeopleListBean.class);
                    if (obj != null) {
                        curMettingBean = swtichData(obj);
                        //将userID切换成客户的
                        swtichKXUserIDRequest(curMettingBean);
                        //保存已经在会人员的信息
                        List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
                        int num = 0;
                        if (members.size() > 0) {
                            num = members.size();
                        } else {
                            num = 0;
                        }
                        userMessge.setonLinePeople(num);


                    }

                }
                PLog.i("VedioMettingActivity: ------------获取会议成员列表----" + msg);
            }


        } else if (msg.indexOf(WssContant.WSS_MEETING_ZX_OUT_MEETING) >= 0) {
            //成员收到主席退会的信息
            //成员离开会议
            if (!isCreateMeeting) {
                if (curMettingBean != null) {
                    PLog.d("ThridJoinListActivity", "----------成员收到主席退会------->成员正在自动退出会议");
                    Intent intent = new Intent(ThridJoinListActivity.this,
                            VedioMettingActivity.class);
                    setResult(1011, intent);
                    finish();
                }
            }


        }
    }


    /**
     * 格式化
     *
     * @param obj
     */
    private JoinMettingBean swtichData(ParseMeetingPeopleListBean obj) {
        JoinMettingBean joinMettingBean = null;
        ParseMeetingPeopleListBean.ParamsBean params = obj.getParams();
        if (params != null) {
            joinMettingBean = new JoinMettingBean();
            String sceneID = params.getSceneID();
            String sceneName = params.getSceneName();

            joinMettingBean.setSceneID(sceneID);
            joinMettingBean.setSceneName(sceneName);
            joinMettingBean.setUserID("");
            joinMettingBean.setUserName("");
            List<JoinMettingBean.MembersBean> membersBeen = new ArrayList<>();
            List<ParseMeetingPeopleListBean.ParamsBean.MembersBean> members = params.getMembers();
            if (members != null && members.size() > 0) {
                for (int i = 0; i < members.size(); i++) {
                    JoinMettingBean.MembersBean bean = new JoinMettingBean.MembersBean();
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
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < userIDs.size(); i++) {
                        if (i == userIDs.size() - 1) {
                            buffer.append(userIDs.get(i));
                        } else {
                            buffer.append(userIDs.get(i)).append(",");
                        }
                    }

                    String json = buffer.toString();
                    List<NameValuePair> params = MoudleParams.getKXUserIdParams(this, json);
                    String url = params.get(0).getValue();
                    //有参数，需要看下url里面最后一个是否带有=符号
                    params.remove(0);
                    //对参数进行格式化
                    String p = URLEncodedUtils.format(params, HTTP.UTF_8);
                    urlGet = url + "?" + p;
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
                                String result = response.body().string();
                                Message message = Message.obtain();
                                message.obj = result;
                                message.what = 1;
                                mGetHandler.sendMessage(message);
                            } catch (IOException e) {
                                String error = e.getMessage();
                                Message message = Message.obtain();
                                message.obj = error;
                                message.what = 2;
                                mGetHandler.sendMessage(message);
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
    }

    private Handler mGetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String result = (String) msg.obj;
                PLog.d("VedioMettingActivity", "-----mGetHandler--->result =" + result);
                //兴图的userID转换为空信userId
                if (result != null && result.length() > 0) {
                    try {
                        //  进行数据解析
                        PLog.d(result);
                        UserIdSwtichBean userIdSwtichBean = FastJsonTools.json2BeanObject(result, UserIdSwtichBean.class);
                        if (userIdSwtichBean != null) {
                            List<UserIdSwtichBean.PeopleBean> data = userIdSwtichBean.getData();
                            if (data != null && data.size() > 0) {
                                String partPeoples = userMessge.getThridPartPeoples();
                                String chairmanID = userMessge.getThirdPartyChairmanID();
                                if (partPeoples != null &&
                                        partPeoples.length() > 0) {
                                    //客户的人员信息
                                    List<InvitePeopleBean.ChoicePeople> peopleList = FastJsonTools.
                                            jsonToList(partPeoples, InvitePeopleBean.ChoicePeople.class);
                                    //将客户的通讯录的所有信息转换为 JoinMettingPeopleBean
                                    List<JoinMettingPeopleBean> peopleBeen = new ArrayList<>();
                                    for (int i = 0; i < peopleList.size(); i++) {
                                        JoinMettingPeopleBean bean = new JoinMettingPeopleBean();
                                        bean.setChoice(false);
                                        bean.setUseId(peopleList.get(i).getUserId());
                                        bean.setOnline(true);
                                        bean.setName(peopleList.get(i).getUserName());
                                        bean.setMeetingID(curMettingBean.getSceneID());
                                        if (peopleList.get(i).getUserId().equals(chairmanID)) {
                                            //主席
                                            bean.setRole("chairman");
                                        } else {
                                            //成员
                                            bean.setRole("member");
                                        }

                                        peopleBeen.add(bean);

                                    }

                                    List<JoinMettingPeopleBean> choicePeoples = new ArrayList<>();
                                    for (int i = 0; i < peopleBeen.size(); i++) {
                                        //替换成空信的
                                        JoinMettingPeopleBean kxBean = peopleBeen.get(i);
                                        String kxUserID = kxBean.getUseId();
                                        for (int j = 0; j < data.size(); j++) {
                                            //替换成空信的
                                            UserIdSwtichBean.PeopleBean peopleBean = data.get(j);
                                            String kxuserid = peopleBean.getKxuserid();
                                            if (kxuserid.equals(kxUserID)) {
                                                //相同，替换
                                                kxBean.setChoice(true);
                                                choicePeoples.add(kxBean);
                                            } else {
                                                //不同
                                                kxBean.setChoice(false);
                                            }

                                        }
                                    }

                                    if (onLineBeen.size() > 0) {
                                        onLineBeen.clear();
                                    }
                                    onLineBeen.addAll(choicePeoples);

                                    //添加数据
                                    if (joinMettingPeopleBeanList.size() > 0) {
                                        joinMettingPeopleBeanList.clear();
                                    }
                                    joinMettingPeopleBeanList.addAll(peopleBeen);

                                    if (isCreateMeeting) {
                                        //向客户发送在会人数的广播
                                        List<InvitePeopleBean.ChoicePeople> choicePeopleBeen=new ArrayList<>();
                                        if (joinMettingPeopleBeanList != null && joinMettingPeopleBeanList.size() > 0) {
                                            for (int i = 0; i < joinMettingPeopleBeanList.size(); i++) {
                                                InvitePeopleBean.ChoicePeople choicePeople = new InvitePeopleBean.ChoicePeople();
                                                choicePeople.setUserId(joinMettingPeopleBeanList.get(i).getUseId());
                                                choicePeople.setUserName(joinMettingPeopleBeanList.get(i).getName());
                                                choicePeopleBeen.add(choicePeople);
                                            }
                                            Gson gson=new Gson();
                                            String json = gson.toJson(choicePeopleBeen);
                                            KXBroadstcast.sendBroadcastPeopleNum(ThridJoinListActivity.this, json);
                                        } else {
                                            KXBroadstcast.sendBroadcastPeopleNum(ThridJoinListActivity.this, "");
                                        }
                                    }
                                    list.addAll(joinMettingPeopleBeanList);
                                    refuashUI();
                                }
                            } else {
                                ToastUtil.showShort(ThridJoinListActivity.this, "获取kxUserIds为空");
                            }

                        } else {
                            ToastUtil.showShort(ThridJoinListActivity.this, "获取kxUserIds为空");
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            } else if (msg.what == 2) {
                //兴图的userID转换为空信userId
                List<JoinMettingBean.MembersBean> list = new ArrayList<>();
                curMettingBean.setMembers(list);
                ToastUtil.showShort(ThridJoinListActivity.this, "转换失败");
                PLog.d("ThridJoinListActivity", "----------faile------->userID转换失败");
            }
        }
    };


    private void refuashUI() {
        if (list.size() > 0) {
            if (choiceBeen.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JoinMettingPeopleBean bean = list.get(i);
                    String useId = bean.getUseId();
                    for (int j = 0; j < choiceBeen.size(); j++) {
                        JoinMettingPeopleBean c = choiceBeen.get(j);
                        if (c.getUseId().equals(useId)) {
                            boolean choice = bean.isChoice();
                            if (!choice) {
                                bean.setChoice(true);
                            }
                        }

                    }
                }

                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
//        isOver=userMessge.isCountOver();
//        if (!isOver){
//            if (isCreateMeeting) {
//                if (countdownUtil != null && countdownUtil.isRunning()) {
//                    countdownUtil.stop();
//                }
//            }
//        }

        if (isCreateMeeting) {
            if (countdownUtil != null && countdownUtil.isRunning()) {
                countdownUtil.stop();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        isOver=userMessge.isCountOver();
//        if (!isOver){
//            if (isCreateMeeting) {
//                if (countdownUtil != null && !(countdownUtil.isRunning())) {
//                    int timeCount = userMessge.getTimeCount();
//                    countdownUtil.setTotalTime(timeCount*1000);
//                    countdownUtil.setIntervalTime(1*1000);
//                    countdownUtil.start();
//                }
//            }
//        }

        if (isCreateMeeting) {
            if (countdownUtil != null && !(countdownUtil.isRunning())) {
                countdownUtil.start();
            }
        }

    }



    @Override
    public void onRemain(long millisUntilFinished) {
        int timeCount= (int) (millisUntilFinished/1000);
        PLog.d("ThridJoinListActivity","-------onRemain-------->"+timeCount);
    }

    @Override
    public void onFinish() {
        //倒计时已经结束
        if (isCreateMeeting){
            int onLinePeople = userMessge.getOnLinePeople();
            //监听在会人员数量
            if (onLinePeople<=1){
                //主席结束
                //向客户发送主席因成员在规定时间内未进入退出
                Intent intent = new Intent(ThridJoinListActivity.this,
                        VedioMettingActivity.class);
                setResult(1012, intent);
                finish();
            }

        }
    }

//
//    @Override
//    public void onRemain(long millisUntilFinished) {
//        int timeCount= (int) (millisUntilFinished/1000);
//        userMessge.setCallOutTime(timeCount+"");
//        PLog.d("ThridJoinListActivity","-------onRemain-------->"+timeCount);
//    }
//
//    @Override
//    public void onFinish() {
//        //倒计时已经结束
//        isOver=true;
//        userMessge.setCountOver(isOver);
//        int parseInt =60;
//        String callOutTime = userMessge.getCallOutTime();
//        if (callOutTime !=null && callOutTime.length()>0){
//            parseInt = Integer.parseInt(callOutTime);
//        }else {
//            parseInt =60;
//        }
//        userMessge.setTimeCount(parseInt);
//        if (isCreateMeeting){
//            int onLinePeople = userMessge.getOnLinePeople();
//            //监听在会人员数量
//            if (onLinePeople<=1){
//                //主席结束
//                //向客户发送主席因成员在规定时间内未进入退出
//                KXBroadstcast.sendBroadcastChariManStopMeeting(ThridJoinListActivity.this);
//                Intent intent = new Intent(ThridJoinListActivity.this,
//                        VedioMettingActivity.class);
//                setResult(1011, intent);
//                finish();
//            }
//
//        }
//    }
}
