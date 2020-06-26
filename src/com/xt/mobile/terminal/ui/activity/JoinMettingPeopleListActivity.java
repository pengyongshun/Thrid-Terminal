package com.xt.mobile.terminal.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.JoinMettingPeopleAdapter;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeark;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeaskBody;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.ActivityTools;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.PopupWindowUitl;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 已参与会议的成员列表
 */
public class JoinMettingPeopleListActivity extends BaseActivity {

    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ListView mListView;
    private Button mAskPeopleBtn,mdeletPeopleBtn;

    private TextView mEmptyTv;

    private List<JoinMettingPeopleBean> list=new ArrayList<>();
    private List<JoinMettingPeopleBean> choiceBeen = new ArrayList<>();
    private JoinMettingPeopleAdapter mAdapter;

    private boolean isEdit=false;//是否选择编辑
    private PopupWindowUitl popupWindowUitl;
    private TextView mPopTitleTv;
    private TextView mPopZDFYTv;
    private TextView mPopDeletTv;
    private TextView mPopCloseTv;
    private JoinMettingPeopleBean currBean;
    private CustomTextDialog textDialog;
    private int tag = -1;
    private TextView mLeftTv;
    private JoinMettingBean joinMettingBean;
    private JoinMettingPeopleBean zxBean;
    private boolean isMember;
    private String activtyTag="";
    private boolean isThridApp=false;
    private CustomTextDialog isApplySpeakDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_metting_people_list);
        activtyTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
        if (activtyTag !=null && activtyTag.length()>0){
            //第三方来的
            isThridApp=true;
        }else {
            //非第三方来的
            isThridApp=false;
        }
        isMember = getIntent().getBooleanExtra("isMember",true);
        String result = getIntent().getStringExtra("json");
        if (result !=null && result.length()>0){
            joinMettingBean = FastJsonTools.json2BeanObject(result, JoinMettingBean.class);

        }
        initView();
        initData();

    }

    private void initData() {
        if (list.size()>0){
            list.clear();
        }
        if (joinMettingBean !=null){
            String sceneID = joinMettingBean.getSceneID();
            List<JoinMettingBean.MembersBean> members = joinMettingBean.getMembers();
            if (members !=null && members.size()>0){
                for (int i = 0; i < members.size(); i++) {
                    JoinMettingPeopleBean bean=new JoinMettingPeopleBean();
                    //暂时用拼音
                    bean.setName(members.get(i).getUserName());
                    bean.setUseId(members.get(i).getUserID());
                    String status = members.get(i).getStatus();
                    if (status !=null && status.length()>0){
                        if (status.equals("onlineInMeeting")){
                            //在线
                            bean.setOnline(true);
                        }else {
                            //不在线
                            bean.setOnline(false);
                        }
                    }else {
                        //不在线
                        bean.setOnline(false);
                    }
                    //默认都为选中
                    bean.setChoice(false);
                    bean.setMeetingID(sceneID);
                    bean.setRole(members.get(i).getRole());

                    list.add(bean);

                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mListView = (ListView) findViewById(R.id.activity_join_metting_poeple_list_lv);
        mAskPeopleBtn = (Button) findViewById(R.id.activity_join_metting_poeple_list_btn);
        mdeletPeopleBtn = (Button) findViewById(R.id.activity_join_metting_poeple_list_yc_btn);
        mEmptyTv = (TextView) findViewById(R.id.activity_join_metting_poeple_empty_tv);

        swthichTop(tag);

        //初始化对话框
        initDaliog();

        addListListener(isEdit);

        if (isThridApp){
            //第三方
            mRightTv.setVisibility(View.GONE);
            mAskPeopleBtn.setVisibility(View.VISIBLE);
            mListView.setEnabled(false);
        }else {
            //非第三方
            //成员只能看，不能邀请，不能删除
            //主席能邀请，能删除
            if (isMember){
                //成员
                mRightTv.setVisibility(View.GONE);
                mAskPeopleBtn.setVisibility(View.GONE);
                mListView.setEnabled(false);
            }else {
                //主席
                mRightTv.setVisibility(View.VISIBLE);
                mAskPeopleBtn.setVisibility(View.VISIBLE);
                mListView.setEnabled(true);
            }
        }




    }

    /**
     * 切换头部试图和底部试图
     * @param tag  -1为默认
     */
    private void swthichTop(int tag) {
        if (tag==-1){
            //默认进来的时候
            mLeftIv.setVisibility(View.VISIBLE);
            mLeftIv.setBackgroundResource(R.drawable.login_params_back);
            mLeftIv.setOnClickListener(this);

            mLeftTv.setVisibility(View.GONE);
            mLeftTv.setText("取消");
            mLeftTv.setOnClickListener(this);

            mTitleTv.setText("参会成员");

            mRightTv.setVisibility(View.VISIBLE);
            mRightTv.setText("选择");
            mRightTv.setOnClickListener(this);

            mAskPeopleBtn.setVisibility(View.VISIBLE);
            mdeletPeopleBtn.setVisibility(View.GONE);
            mAskPeopleBtn.setOnClickListener(this);
            mdeletPeopleBtn.setOnClickListener(this);
        }else {
            //处理编辑状态
            mLeftIv.setVisibility(View.GONE);
            mLeftIv.setBackgroundResource(R.drawable.login_params_back);
            mLeftIv.setOnClickListener(this);

            mLeftTv.setVisibility(View.VISIBLE);
            mLeftTv.setText("取消");
            mLeftTv.setOnClickListener(this);

            mTitleTv.setText("参会成员");

            mRightTv.setVisibility(View.VISIBLE);
            if (tag==0){
                mRightTv.setText("全选");
            }else if (tag==1){
                mRightTv.setText("全不选");
            }
            mRightTv.setOnClickListener(this);

            mAskPeopleBtn.setVisibility(View.GONE);
            mdeletPeopleBtn.setVisibility(View.VISIBLE);
            mAskPeopleBtn.setOnClickListener(this);
            mdeletPeopleBtn.setOnClickListener(this);
        }
    }

    private void addListListener(final boolean isEdit) {
        mAdapter=new JoinMettingPeopleAdapter(JoinMettingPeopleListActivity.this,
                list,isEdit,false,true);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyTv);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (list.size()>0){
                        if (!isEdit && tag==-1){
                            //显示弹框
                            currBean = list.get(position);
                            String role = currBean.getRole();
                            if (role.equals("chairman")){
                                ToastUtil.showShort(JoinMettingPeopleListActivity.this,
                                        "不能点自己");
                            }else {
                                if (popupWindowUitl !=null && !(popupWindowUitl.isShowing())){
                                    //填充数据
                                    mPopTitleTv.setText("对"+" ["+currBean.getName()+"] 进行操作");
                                    popupWindowUitl.showWindow(mAskPeopleBtn, Gravity.BOTTOM,0,0);
                                }
                            }

                        }else {
                            //显示选择框
                            secetItem(position,isEdit);
                        }

                    }
                }
            });

    }

    /**
     * 选择单个
     *
     * @param position
     */
    private void secetItem(int position, boolean flag) {
        if (list.size() > 0 ) {
            JoinMettingPeopleBean bean = list.get(position);
            if (bean.getRole().equals("chairman")){
                //主席
                bean.setChoice(false);
                ToastUtil.showShort(JoinMettingPeopleListActivity.this,"不能操作自己");
            }else {
                //成员
                boolean choice = bean.isChoice();
                if (choice) {
                    bean.setChoice(false);
                } else {
                    bean.setChoice(true);
                }
                list.remove(position);
                list.add(position, bean);
            }


            //将选择的添加到容器中
            if (choiceBeen.size() > 0) {
                choiceBeen.clear();
            }
            for (int i = 0; i < list.size(); i++) {
                JoinMettingPeopleBean bean1 = list.get(i);
                boolean choice1 = bean1.isChoice();
                if (choice1) {
                    //已选择的
                    choiceBeen.add(bean1);
                }
            }
            if (choiceBeen.size() == 1) {
                //没有一个被选中的
                tag = 0;
                mRightTv.setText("全选");

            } else if (choiceBeen.size() == list.size()-1) {
                //全部被选中的
                tag = 1;
                mRightTv.setText("全不选");
            }
            mdeletPeopleBtn.setText("移除 ("+choiceBeen.size()+")");
            mAdapter=new JoinMettingPeopleAdapter(JoinMettingPeopleListActivity.this,
                    list,flag,false,true);
            mListView.setAdapter(mAdapter);
            mListView.setEmptyView(mEmptyTv);
        }
    }

    /**
     * 全选或者全部取消
     */
    private void secetAll(boolean flag) {

        List<JoinMettingPeopleBean> beanList = new ArrayList<JoinMettingPeopleBean>();
        if (list.size() > 0) {

            if (tag == 0) {
                //未选，需要全部选中
                for (int i = 0; i < list.size(); i++) {
                    JoinMettingPeopleBean bean = list.get(i);
                    if (bean.getRole().equals("chairman")){
                        bean.setChoice(false);
                        zxBean = list.get(i);
                    }else {
                        bean.setChoice(false);
                        beanList.add(bean);
                    }

                }
                if (choiceBeen.size()>0){
                    choiceBeen.clear();
                }
                mRightTv.setText("全选");
            } else if (tag == 1) {
                //已选中，需要全部取消
                for (int i = 0; i < list.size(); i++) {
                    JoinMettingPeopleBean bean = list.get(i);
                    if (bean.getRole().equals("chairman")){
                        //主席不能选中
                        bean.setChoice(false);
                        zxBean = list.get(i);
                    }else {
                        bean.setChoice(true);
                        beanList.add(bean);
                    }

                }
                if (choiceBeen.size()>0){
                    choiceBeen.clear();
                }
                choiceBeen.addAll(beanList);
                mRightTv.setText("全不选");
            }

            list.clear();
            list.add(zxBean);
            list.addAll(beanList);
            mdeletPeopleBtn.setText("移除 ("+choiceBeen.size()+")");
            mAdapter=new JoinMettingPeopleAdapter(JoinMettingPeopleListActivity.this,
                    list,flag,false,true);
            mListView.setAdapter(mAdapter);
            mListView.setEmptyView(mEmptyTv);
        }
    }

    private void initDaliog() {
        //初始化弹出框
        popupWindowUitl = DailogUitl.initJoinMettingEditPopwindow(JoinMettingPeopleListActivity.this
                , new PopupWindowUitl.PopupWindowCall() {
                    @Override
                    public void initView(View view) {
                        //初始化控件
                        mPopTitleTv = (TextView) view.findViewById(R.id.pop_join_metting_edit_title_tv);
                        mPopZDFYTv = (TextView) view.findViewById(R.id.pop_join_metting_edit_zdfy_tv);
                        mPopDeletTv = (TextView) view.findViewById(R.id.pop_join_metting_delet_metting_tv);
                        mPopCloseTv = (TextView) view.findViewById(R.id.pop_join_metting_close_tv);

                    }

                    @Override
                    public void initEvent() {
                        //主席指定发言
                        mPopZDFYTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (popupWindowUitl !=null &&(popupWindowUitl.isShowing())){
                                    popupWindowUitl.dissWindow();
                                }
                                if (currBean !=null){
                                    //网络请求
                                    //指定成员发言
                                    String role = currBean.getRole();
                                    if (!role.equals("chairman")){
                                        showLoadingDaliog("正在指定发言中...");
                                        zdfyrWss();
                                        String result=FastJsonTools.bean2Json(currBean);
                                        Message message=Message.obtain();
                                        message.what=2;
                                        message.obj=result;
                                        mHandler.sendMessageDelayed(message,1000);
                                    }else {
                                        ToastUtil.showShort(JoinMettingPeopleListActivity.this,
                                                "不能操作自己");
                                    }

                                }


                            }
                        });
                        //移除会议 主席删除成员
                        mPopDeletTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //网络请求
                                //删除成员
                                String role = currBean.getRole();
                                if (!role.equals("chairman")){
                                    showLoadingDaliog("正在移除中...");
                                    deletMembers(false);

                                    //移除会议  主席删除成员
                                    if (currBean !=null){
                                        ToastUtil.showShort(JoinMettingPeopleListActivity.this,"移除会议-->curBean="+currBean.getName());
                                        if (list.size()>0 && list.contains(currBean)){
                                            //模拟
                                            list.remove(currBean);
                                            mAdapter.notifyDataSetChanged();
                                            currBean=null;
                                            choiceBeen.clear();

                                            String result="";
                                            if (list.size()>0){
                                                //数据格式转换
                                                swtichData(list);

                                            }else {
                                                List<JoinMettingBean.MembersBean> data=new ArrayList<>();
                                                joinMettingBean.setMembers(data);
                                            }
                                            result=FastJsonTools.bean2Json(joinMettingBean);
                                            Message message=Message.obtain();
                                            message.what=1;
                                            message.obj=result;
                                            mHandler.sendMessageDelayed(message,1000);


                                        }
                                    }
                                }else {
                                    ToastUtil.showShort(JoinMettingPeopleListActivity.this,
                                            "不能操作自己");
                                }


                                if (popupWindowUitl !=null &&(popupWindowUitl.isShowing())){
                                    popupWindowUitl.dissWindow();
                                }

                            }
                        });
                        //取消
                        mPopCloseTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //关闭弹出框
                                if (popupWindowUitl !=null &&(popupWindowUitl.isShowing())){
                                    popupWindowUitl.dissWindow();
                                }

                            }
                        });
                    }
                });


        //可编辑状态显示对话框
        textDialog = DailogUitl.initTextDialog(JoinMettingPeopleListActivity.this,
                "移除会议", "确认将选中成员移除会议吗？","移除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认删除会议  主席删除成员
                if (choiceBeen !=null && choiceBeen.size()>0){
                    if (list.size()>0 && list.size()>=choiceBeen.size()){
                        //网络请求
                        //删除成员
                        showLoadingDaliog("正在移除中...");
                        deletMembers(true);

                        //模拟
                        list.removeAll(choiceBeen);
                        mAdapter.notifyDataSetChanged();
                        choiceBeen.clear();
                        mdeletPeopleBtn.setText("移除 ("+choiceBeen.size()+")");
                        if (list.size()==0){
                            //此时为空，需要邀请
                            tag=-1;
                            swthichTop(tag);
                        }

                        //确认删除后回到会议界面

                        if (list.size()>0){
                            //数据格式转换
                            swtichData(list);

                        }else {
                            List<JoinMettingBean.MembersBean> data=new ArrayList<>();
                            joinMettingBean.setMembers(data);
                        }
                        String result="";
                        result=FastJsonTools.bean2Json(joinMettingBean);
                        Message message=Message.obtain();
                        message.what=1;
                        message.obj=result;
                        mHandler.sendMessageDelayed(message,1000);

                    }
                }
            }
        });


}

    /**
     * 数据格式转换
     * @param list
     */
    private void swtichData(List<JoinMettingPeopleBean> list) {
        if (joinMettingBean !=null){
            List<JoinMettingBean.MembersBean> membersBeen=new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                JoinMettingBean.MembersBean membersBean = new JoinMettingBean.MembersBean();
                membersBean.setRole(list.get(i).getRole());
                boolean online = list.get(i).isOnline();
                if (online){
                    //在线
                    membersBean.setStatus("onlineInMeeting");
                }else {
                    //不在线
                    membersBean.setStatus("outlineInMeeting");
                }

                membersBean.setIndex(i+"");
                membersBean.setUserID(list.get(i).getUseId());

                membersBeen.add(membersBean);

            }

            joinMettingBean.setMembers(membersBeen);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.left_iv) {
            //返回
            finish();
        }else if (v.getId() == R.id.left_tv) {
            //取消、
            //回到默认状态
            isEdit=false;
            tag=-1;
            choiceBeen.clear();
            swthichTop(tag);
            addListListener(isEdit);
        }else if (v.getId() == R.id.right_tv) {
            //选择
            isEdit=true;
            String string = mRightTv.getText().toString();
            if (string.equals("全选")){
                tag =1;
            }else if (string.equals("全不选")){
                tag =0;
            }else {
                tag =0;
            }
            addListListener(isEdit);
            swthichTop(0);
            secetAll(isEdit);
        }else if (v.getId() == R.id.activity_join_metting_poeple_list_btn) {
            //邀请  则进入到通讯录中进行选则
            Intent intent=new Intent(JoinMettingPeopleListActivity.this,BaseContanctsActivity.class);
            intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_YQCY_LIST);
            String json = FastJsonTools.bean2Json(joinMettingBean);
            intent.putExtra("JoinMettingBean",json);
            intent.putExtra("isThridApp",isThridApp);
            startActivityForResult(intent,1000);
        }else if (v.getId() == R.id.activity_join_metting_poeple_list_yc_btn) {
            //将选中的移除
            if (textDialog !=null &&!(textDialog.isShowing())){
                textDialog.show();
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1001:
                if (requestCode==1000){
                    //从通讯录界面返回的
                    String result = data.getStringExtra("JoinMettingBean");
                    Intent intent=new Intent(JoinMettingPeopleListActivity.this,
                            VedioMettingActivity.class);
                    intent.putExtra("JoinMettingBean",result);
                    setResult(1002,intent);
                    finish();
                }
                break;

        }
    }


    ////////////////////////////////////////////网络请求////////////////////////

    /**
     * 指定发言人
     */
    private void zdfyrWss(){
        if (currBean !=null){
            WebSocketCommand.getInstance().onWssSetSpeaker(currBean.getMeetingID(),
                    currBean.getUseId());
        }
    }


    /**
     * 主席删除成员  可以单个，可以多个
     * @param isMultiple ture--多选
     */
    private void deletMembers(boolean isMultiple){
        if (isMultiple){
            //多选
            if (choiceBeen !=null && choiceBeen.size()>0){
                String meetingID = choiceBeen.get(0).getMeetingID();
                List<String> userIds=new ArrayList<>();
                for (int i = 0; i < choiceBeen.size(); i++) {
                 userIds.add(choiceBeen.get(i).getUseId());
                }
                WebSocketCommand.getInstance().onWssRemoveMembers(meetingID,userIds);
            }

        }else {
            //单选
            if (currBean !=null){
                String meetingID = currBean.getMeetingID();
                List<String> userIds=new ArrayList<>();
                userIds.add(currBean.getUseId());
                WebSocketCommand.getInstance().onWssRemoveMembers(meetingID,userIds);
            }

        }

    }


    /**
     * 主席同意成员发言（需要验证）
     * memberID  这个是主席接收到成员申请发言指令后服务返回给主席的数据里面有
     * @param mettingID
     */
    private void zxAgreeSpeak(String mettingID, String memberID){
        WebSocketCommand.getInstance().onWssAcceptSpeaker(mettingID,memberID);
    }


    /**
     * 主席拒绝成员发言（需要验证）
     * memberID  这个是主席接收到成员申请发言指令后服务返回给主席的数据里面有
     * @param mettingID
     */
    private void zxRefuseSpeak(String mettingID, String memberID){
        WebSocketCommand.getInstance().onWssRefuseSpeaker(mettingID,memberID);
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
     * wss请求返回的结果
     * @param msg
     */
    protected void onReceiveWssMessage(String msg) {
        hideLoadingDialog();
        if (msg.isEmpty()) {
            return;
        }else if (msg.indexOf(WssContant.WSS_STOP_METTING) >= 0) {


        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {


        }else if (msg.indexOf("publishAcceptSpeakerFromConference")>0 ||
                msg.indexOf(WssContant.WSS_MEETING_APPLY_SPEAK_SHOW)>0){
            if (isThridApp){
                //第三方
            }else {
                if (!isMember) {
                    //主席
                    //主席监听成员发送申请的指令
                    if (msg != null && msg.length() > 0) {
                        ParseMeetingApplySpeark parseMeetingApplySpeark = FastJsonTools.json2BeanObject(msg,
                                ParseMeetingApplySpeark.class);
                        String body = parseMeetingApplySpeark.getBody();
                        if (body != null && body.length() > 0) {
                            ParseMeetingApplySpeaskBody speaskBody = FastJsonTools.json2BeanObject(body,
                                    ParseMeetingApplySpeaskBody.class);
                            ParseMeetingApplySpeaskBody.ParamsBeanX params = speaskBody.getParams();
                            String contentText = params.getText();
                            String title = params.getTitle();
                            String memberID = "";
                            String sceneID = "";
                            List<ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean> buttons = params.getButtons();
                            if (buttons != null && buttons.size() > 0) {
                                for (int i = 0; i < buttons.size(); i++) {
                                    ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean.CommandBean command = buttons.get(i).getCommand();
                                    if (command != null) {
                                        ParseMeetingApplySpeaskBody.ParamsBeanX.ButtonsBean.CommandBean.ParamsBean params1 = command.getParams();
                                        if (params1 != null) {
                                            memberID = params1.getMemberID();
                                            sceneID = params1.getSceneID();
                                            if (memberID != null && memberID.length() > 0 &&
                                                    sceneID != null && sceneID.length() > 0) {
                                                //主席进行允许成员发言和拒绝成员发言
                                                initIsApplySpaekDialog(title, contentText, sceneID, memberID);
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                } else {
                    //成员
                }
               }
            }else if (msg.indexOf(WssContant.WSS_MEETING_ZX_OUT_MEETING)>=0){
            //成员收到主席退会的信息
            //成员离开会议
            if (isMember){
                Intent intent=new Intent(JoinMettingPeopleListActivity.this,
                        VedioMettingActivity.class);
                setResult(1010,intent);
                finish();
            }


        }
    }


    private void initIsApplySpaekDialog(String title , String text,
                                        final String meetingId, final String menmberId) {
        //主席是否允许成员发言
        isApplySpeakDialog = DailogUitl.initTextDialog(JoinMettingPeopleListActivity.this, title, text,"拒绝", "同意", new DialogInterface.OnClickListener() {
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
                showLoadingDaliog("同意发言中...");
                Message message=Message.obtain();
                message.what=3;
                mHandler.sendMessageDelayed(message,1000);
            }
        });

        if (isApplySpeakDialog !=null && !(isApplySpeakDialog.isShowing())){
            isApplySpeakDialog.show();
        }

    }


    /**
     * 接收跳转的地方
     */
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //删除
                    hideLoadingDialog();
                    if (msg.obj instanceof String){
                        String result = (String) msg.obj;
                        Intent intent=new Intent(JoinMettingPeopleListActivity.this,
                                VedioMettingActivity.class);
                        intent.putExtra("JoinMettingBean",result);
                        setResult(1001,intent);

                    }

                    break;
                case 2:
                    //指定发言
                    hideLoadingDialog();
                    if (msg.obj instanceof String){
                        String result = (String) msg.obj;
                        Intent intent=new Intent(JoinMettingPeopleListActivity.this,
                                VedioMettingActivity.class);
                        intent.putExtra("JoinMettingPeopleBean",result);
                        setResult(1000,intent);
                        finish();

                    }
                    break;
                case 3:
                    //主席同意成员发言
                    hideLoadingDialog();
                    Intent intent=new Intent(JoinMettingPeopleListActivity.this,
                            VedioMettingActivity.class);
                    setResult(1005,intent);
                    finish();
                    break;
            }

        }
    };
}
