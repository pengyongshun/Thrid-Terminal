package com.xt.mobile.terminal.thridpart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.JoinMettingPeopleAdapter;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.thridpart.bean.InvitePeopleBean;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.activity.GroupMeetingActivity;
import com.xt.mobile.terminal.ui.activity.VedioMettingActivity;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.TimeUitls;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;

import java.util.ArrayList;
import java.util.List;

public class GroupContactActivity extends BaseActivity {
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ListView mListView;
    private TextView mEmptyTv;
    private TextView mLeftTv;

    private List<JoinMettingPeopleBean> list = new ArrayList<>();
    private List<JoinMettingPeopleBean> choiceBeen = new ArrayList<>();
    private JoinMettingPeopleAdapter mAdapter;
    private JoinMettingPeopleBean currBean;
    private UserMessge userMessge;
    private boolean isCreateMeeting;
    private String meetingId = "";
    private String activtyTag="";
    private Intent PeopleIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_thrid_join_list);
        PLog.d("GroupContactActivity","--------启动aar包------开始转跳会议界面---->结束时间：time="+ TimeUitls.getLongSysnTime());
        activtyTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
        userMessge=UserMessge.getInstans(GroupContactActivity.this);
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
        mTitleTv.setText("邀请人员");

        mLeftIv.setOnClickListener(this);
        mRightTv.setText("邀请");
        mRightTv.setOnClickListener(this);
        //第三方
        mRightTv.setVisibility(View.VISIBLE);
        mListView.setEnabled(true);
    }


    private void initData() {
        List<JoinMettingPeopleBean> been1 = new ArrayList<>();
        List<JoinMettingPeopleBean> been2 = new ArrayList<>();
        String peoples = userMessge.getThridPartPeoples();
        String userId = userMessge.getThirdPartyUserName();
        if (peoples !=null && peoples.length()>0){
            List<InvitePeopleBean.ChoicePeople> peopleList = FastJsonTools.
                    jsonToList(peoples, InvitePeopleBean.ChoicePeople.class);
            for (int i = 0; i < peopleList.size(); i++) {
                JoinMettingPeopleBean bean=new JoinMettingPeopleBean();
                InvitePeopleBean.ChoicePeople peopleBean = peopleList.get(i);
                if (userId.equals(peopleBean.getUserId())){
                    //主席
                    bean.setChoice(true);
                    bean.setRole("chairman");
                    bean.setName(peopleBean.getUserName());
                    bean.setUseId(peopleBean.getUserId());
                    bean.setMeetingID("");
                    bean.setOnline(true);
                    been1.add(bean);
                }else {
                    //成员
                    bean.setChoice(false);
                    bean.setRole("member");
                    bean.setName(peopleBean.getUserName());
                    bean.setUseId(peopleBean.getUserId());
                    bean.setMeetingID("");
                    bean.setOnline(true);
                    been2.add(bean);
                }



            }

        }
        list.add(been1.get(0));
        list.addAll(been2);
        mAdapter = new JoinMettingPeopleAdapter(GroupContactActivity.this,
                list, true, false, true);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyTv);
        addListListener();

    }


    private void addListListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (list.size() > 0) {
                    if (choiceBeen.size()<9){
                        //显示弹框
                        currBean = list.get(position);
                        String role = currBean.getRole();
                        if (role.equals("chairman")) {
                            //
                            currBean.setChoice(true);
                            ToastUtil.showShort(GroupContactActivity.this,
                                    "不能编辑群主");
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

                        list.remove(position);
                        list.add(position, currBean);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        ToastUtil.showShort(GroupContactActivity.this,"选择人数不能大于9人");
                    }



                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.left_iv) {
            //返回
            //清理缓存的数据
            UserMessge.getInstans(GroupContactActivity.this).clearData();
            WebSocketCommand.getInstance().onSendClearUserStatus();
            showLoadingDaliog("正在返回中...");
            handler.sendEmptyMessageAtTime(1,1000);

        } else if (id == R.id.right_tv) {
            //邀请
            if (choiceBeen.size() > 0 && choiceBeen.size()<9) {
                //进入到会议界面
                showLoadingDaliog("正在邀请中...");
                Gson g=new Gson();
                String json = g.toJson(choiceBeen);
                PeopleIntent = new Intent(GroupContactActivity.this, VedioMettingActivity.class);
                PeopleIntent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_THRID_APP);
                PeopleIntent.putExtra("json", json);
                handler.sendEmptyMessageDelayed(2,1000);

            } else {
                ToastUtil.showShort(GroupContactActivity.this, "邀请的人不能为空或者不能大于9人");
            }

        }
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==2){
                hideLoadingDialog();
                startActivity(PeopleIntent);
                finish();
                ToastUtil.showShort(GroupContactActivity.this,choiceBeen.size()+"");
            }else if (msg.what==1){
                hideLoadingDialog();
                finish();
            }

        }
    };
}
