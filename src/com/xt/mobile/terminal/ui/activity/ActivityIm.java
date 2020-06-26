package com.xt.mobile.terminal.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.ImCommunicationMsgListAdapter;
import com.xt.mobile.terminal.bean.MessageBean;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.ToolLog;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.dailog.MyDialog_Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ActivityIm extends BaseActivity {

    public static final String PARAM_SIPINFO = "ResSipInfo";
    public static final String PARAM_SESSIONID = "SessionID";
    private boolean isMsgNotEmpty = false;
    private List<SipInfo> memberList = new ArrayList<SipInfo>();
    private ImageView left_iv;
    private TextView title_tv;
    private ImageView ivImGroup;
    private ImageButton ibImAddMember;
    private ListView lvImContent;
    private EditText etMsg;
    private Button btnImSend;
    private String mSessionID;
    private List<MessageBean> mMessageList = new ArrayList<MessageBean>();
    private ImCommunicationMsgListAdapter mImCommMsgAdapter;
    private int unReadedTotalCount = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_im);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);//设置状态栏背景颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Bundle bundle = getIntent().getBundleExtra("bundle");
        mSessionID = bundle.getString(PARAM_SESSIONID);
        String sipInfoString = bundle.getString(PARAM_SIPINFO);
        if (sipInfoString == null) {
            ToastUtil.showLong(ActivityIm.this, "信息错误");
            finish();
        } else {
            try {
                List<SipInfo> list = JSON.parseArray(sipInfoString, SipInfo.class);
                for (int i = 0; i < list.size(); i++) {
                    memberList.add(list.get(i));
                }
                mUserName = memberList.get(0).getUsername();
                mUserId = memberList.get(0).getUserid();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showLong(ActivityIm.this, "信息错误");
                finish();
            }
        }
        initView();
        initData();
    }

    private void initTop() {
        left_iv = findViewById(R.id.left_iv);
        left_iv.setVisibility(View.VISIBLE);
        left_iv.setOnClickListener(this);
        title_tv = findViewById(R.id.title_tv);
        left_iv.setVisibility(View.VISIBLE);
        ivImGroup = findViewById(R.id.iv_im_group);
        ivImGroup.setOnClickListener(this);
        ibImAddMember = findViewById(R.id.ib_im_add_member);
        ibImAddMember.setVisibility(View.VISIBLE);
        ibImAddMember.setOnClickListener(this);
        setTitleAndGroup();
    }

    private void initView() {
        initTop();
        initBody();
    }

    private void initBody() {
        lvImContent = findViewById(R.id.lv_im_content);
        etMsg = findViewById(R.id.et_im_input);
        btnImSend = findViewById(R.id.btn_im_button_send);
        btnImSend.setOnClickListener(this);
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isMsgNotEmpty = s.toString().trim().length() > 0;
                if(isMsgNotEmpty){
                    btnImSend.setBackgroundResource(R.drawable.im_input_text_bg);
                    btnImSend.setText("发送");
                }else {
                    btnImSend.setText("");
                    btnImSend.setBackgroundResource(R.drawable.im_bottom_add);
                }
            }
        });
        lvImContent = findViewById(R.id.lv_im_content);
        mImCommMsgAdapter = new ImCommunicationMsgListAdapter(this, mMessageList);
        lvImContent.setAdapter(mImCommMsgAdapter);
    }

    private void initData() {
    }
    protected void onReceiveWssMessage(String msg) {
        ToolLog.i("===onReceiveWssMessage LG " + msg);
        super.onReceiveWssMessage(msg);
        if (msg.indexOf(WssContant.WSS_RECEVIE_COMMUNICATION_MESSAGE) >= 0) {
            onRecevieCommunicationMessage(msg);
        } else if (msg.indexOf(WssContant.WSS_USER_COMM_SESSION_PREVIEW) >= 0) {
            onUserCommSessionPreview(msg);
        }
    }

    private void onRecevieCommunicationMessage(String msg) {
        try {
            JSONObject obj = new JSONObject(msg);
            String userId = obj.getString("userID");
            JSONObject paramsObj = obj.getJSONObject("params");
            String sessionID = paramsObj.getString("sessionID");
            if (mSessionID == null || mSessionID.isEmpty()) {
                mSessionID = sessionID;
            } else if (!mSessionID.equals(sessionID)) {
                return;
            }
            unReadedTotalCount = paramsObj.getInt("unReadedTotalCount");
            JSONArray objUserArray = paramsObj.getJSONArray("users");
            if (objUserArray != null && objUserArray.length() > 0) {
                memberList.clear();
                for (int mi = 0; mi < objUserArray.length(); mi++) {
                    JSONObject userObj = objUserArray.getJSONObject(mi);
                    SipInfo userSipInfo = new SipInfo();
                    userSipInfo.setUserid(userObj.getString("userID"));
                    userSipInfo.setUsername(userObj.getString("userName"));
                    memberList.add(userSipInfo);
                    setTitleAndGroup();
                }
            }
            JSONArray objMsgArray = paramsObj.getJSONArray("messages");
            if (objMsgArray != null && objMsgArray.length() > 0) {
                for (int mi = 0; mi < objMsgArray.length(); mi++) {
                    JSONObject msgObj = objMsgArray.getJSONObject(mi);
                    MessageBean messageBean = new MessageBean();
                    messageBean.setMessageID(msgObj.getString("messageID"));
                    messageBean.setSenderID(msgObj.getString("senderID"));
                    messageBean.setSenderName(msgObj.getString("senderName"));
                    messageBean.setContent(msgObj.getString("content"));
                    messageBean.setSendTime(msgObj.getString("sendTime"));
                    messageBean.setReaded(msgObj.getBoolean("isReaded"));
                    if (mMessageList.contains(messageBean)) {
                        for (MessageBean msgBean : mMessageList) {
                            if (msgBean.equals(messageBean)) {
                                msgBean.setReaded(messageBean.isReaded());
                                break;
                            } else continue;
                        }
                    } else mMessageList.add(messageBean);
                    mImCommMsgAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setTitleAndGroup() {
        StringBuilder sbMembers = new StringBuilder();
        if (memberList.size() == 1) {
            sbMembers.append(memberList.get(0).getUsername());
            title_tv.setText(sbMembers.toString());
        } else if (memberList.size() == 2) {
            sbMembers.append(memberList.get(0).getUsername()).append(",");
            sbMembers.append(memberList.get(1).getUsername());
            title_tv.setText(sbMembers.toString());
        } else if (memberList.size() >= 3) {
            sbMembers.append(memberList.get(0).getUsername()).append(",");
            sbMembers.append(memberList.get(1).getUsername());
            if (sbMembers.length() < 16) {
                sbMembers.append(" (").append(memberList.size()).append("人)");
                title_tv.setText(sbMembers.toString());
            } else {
                String title = sbMembers.substring(0, 16) + " (" + memberList.size() + "人)";
                title_tv.setText(title);
            }
        }
        ivImGroup.setVisibility(memberList.size() > 1 ? View.VISIBLE : View.GONE);
    }
    private void onUserCommSessionPreview(String msg) {
        ToolLog.i("===onUserCommSessionPreview LG " + msg);
    }

    @Override
    public void onClick(View v) {
        if (XTUtils.fastClick()) {
            return;
        }
        int id = v.getId();
		
		if (id == R.id.left_iv) {
              finish();
         } else if (id == R.id.iv_im_group) {
                goToImGroupMembersList();

		} else if (id == R.id.ib_im_add_member) {
                addImGroupMembers();
		} else if (id == R.id.btn_im_button_send) {
                callVideoOrAudio();
		}
 
    }

    private void goToImGroupMembersList() {
        Bundle bundle = new Bundle();
        bundle.putString(ActivityMessageMembers.PARAM_MM, JSON.toJSONString(memberList));
        ActivityTools.startActivity(this, ActivityMessageMembers.class, bundle, false);
    }
    private void addImGroupMembers() {
        ToastUtil.showLong(ActivityIm.this, "添加群聊成员");
    }

    private void callVideoOrAudio() {
        if(isMsgNotEmpty){
            String content = etMsg.getText().toString().trim();
            if (mSessionID == null || mSessionID.isEmpty()) {
                List<String> receiverList = new ArrayList<String>();
                for (SipInfo sipInfo : memberList) receiverList.add(sipInfo.getUserid());
                WebSocketCommand.getInstance().onWssSendCommunicationMessage(receiverList, content);
            } else {
                WebSocketCommand.getInstance().onWssSendCommunicationMessageFromSession(mSessionID, content);
            }
            etMsg.setText("");
            return;
        }
        if (memberList.size() > 1) { //两人以上消息通话
            ToastUtil.showLong(ActivityIm.this, "两人以上消息通话时,不支持视频通讯或语音通讯");
        } else {
            MyDialog_Call dialog = new MyDialog_Call(ActivityIm.this, new MyDialog_Call.VideoCallCallBack() {
                @Override
                public void setResult(String result) {
                    if (result != null && result.equals("VideoCall")) {
                        Intent callIntent = new Intent(ActivityIm.this, ActivityCalling.class);
                        callIntent.putExtra("ReceiverId", memberList.get(0).getUserid());
                        callIntent.putExtra("ReceiverName", memberList.get(0).getUsername());
                        callIntent.putExtra("MediaType", "VideoCall");
                        callIntent.putExtra("IsReceive", false);
                        startActivity(callIntent);
                    } else if (result != null && result.equals("AudioRing")) {
                        Toast.makeText(ActivityIm.this, "敬请期待", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
    }
}