package com.xt.mobile.terminal.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.MessageMembersAdapter;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ActivityMessageMembers extends BaseActivity {

    public static final String PARAM_MM = "MessageMembers";
    private List<SipInfo> memberList = new ArrayList<SipInfo>();
    private ImageView left_iv;
    private TextView title_tv;
    private ListView lvMessageMember;
    private MessageMembersAdapter mMMAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_message_members);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);//设置状态栏背景颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String sipInfoString = bundle.getString(PARAM_MM);
        if (sipInfoString == null) {
            toastErrorAndFinish();
        } else {
            try {
                List<SipInfo> list = JSON.parseArray(sipInfoString, SipInfo.class);
                if(list != null && list.size() > 0) {
                    memberList.addAll(list);
                }else toastErrorAndFinish();
            } catch (Exception e) {
                e.printStackTrace();
                toastErrorAndFinish();
            }
        }
        initView();
    }

    private void toastErrorAndFinish() {
        ToastUtil.showLong(ActivityMessageMembers.this, "信息错误");
        finish();
    }

    private void initTop() {
        findViewById(R.id.inc_im_top_bar).setBackgroundResource(R.color.white);
        left_iv = findViewById(R.id.left_iv);
        left_iv.setVisibility(View.VISIBLE);
        left_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv = findViewById(R.id.title_tv);
        left_iv.setVisibility(View.VISIBLE);
        title_tv.setText("参会成员");
        findViewById(R.id.ib_im_add_member).setVisibility(View.INVISIBLE);
    }

    private void initView() {
        initTop();
        initBody();
    }

    private void initBody() {
        lvMessageMember = findViewById(R.id.lv_message_members);
        mMMAdapter = new MessageMembersAdapter(this,memberList);
        lvMessageMember.setAdapter(mMMAdapter);
    }

}