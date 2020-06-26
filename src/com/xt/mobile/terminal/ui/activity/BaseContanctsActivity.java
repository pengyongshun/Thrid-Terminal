package com.xt.mobile.terminal.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.ContanctsAdapter;
import com.xt.mobile.terminal.adapter.ScrollDirectoryAdapter;
import com.xt.mobile.terminal.bean.GroupMeetingListBean;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeark;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingApplySpeaskBody;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.CharacterParser;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.PinyinComparator;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.ConTanctsUitl;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.HSlidableListView;
import com.xt.mobile.terminal.view.HorizontalListView;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xt.mobile.terminal.view.dailog.MyDialog_Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BaseContanctsActivity extends BaseActivity {
    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private ClearEditText mFilterEdit;
    private TextView mNoticeTv;
    private Button mSureBtn;
    private RelativeLayout mToobarRl;

    private HSlidableListView mResourceList,mFitterResourceList;
    private HorizontalListView mDirectoryList;
    private ContanctsAdapter mResAdapter;
    private ScrollDirectoryAdapter mDirAdapter;
    private ArrayList<SipInfo> resourceInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> directoryInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> tmpInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> fitTmpInfos = new ArrayList<SipInfo>();
    private SipInfo dirSipInfo = null;
    private SipInfo resSipInfo = null;
    private SipInfo rootDir = new SipInfo("", "", 0, "root", "根目录", "", SipInfo.TYPE_USE_RESOURCE);
    private boolean mIsModifyDirectory = false;

    public static final int MODE_Person = 0;
    private static int MODE_CATCH = MODE_Person;
    private CharacterParser characterParser;
    private TextView mQueryTv;
    private PinyinComparator pinyinComparator;
    private TextView mCloseTv;
    private ArrayList<SipInfo> choicePeopleBeen = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> filterDateList = new ArrayList<SipInfo>();
    private boolean isFitter=false;
    private ContanctsAdapter mFitterResourAdapter;
    private int tag=-1;
    private boolean isDefultStart=true;
    private String activtyTag;
    private JoinMettingBean curMettingBean;
    private CustomTextDialog isApplySpeakDialog;
    private LinearLayout mSearchLl;
    private boolean isClose=false;
    private int resuest=0;
    private GroupMeetingListBean groupMeetingListBean;
    private UserMessge userMessge;
	private boolean isThridApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_contancts);
        activtyTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
        userMessge = UserMessge.getInstans(BaseContanctsActivity.this);
        if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
            //从快速会议来的
            curMettingBean = (JoinMettingBean) getIntent().
                    getSerializableExtra("JoinMettingBean");
        }else if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
            //从分组会议中详情，添加成员来的
            String data = getIntent().getStringExtra("data");
            groupMeetingListBean= FastJsonTools.json2BeanObject(data, GroupMeetingListBean.class);

        }else if (activtyTag.equals(Constants.ACTIVTY_YQCY_LIST)){
            //从参会成员列表界面来的
            String json = getIntent().getStringExtra("JoinMettingBean");
            isThridApp = getIntent().getBooleanExtra("isThridApp",false);
            curMettingBean= FastJsonTools.json2BeanObject(json,JoinMettingBean.class);
        }else if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            //从第三方来的
            curMettingBean = (JoinMettingBean) getIntent().
                    getSerializableExtra("JoinMettingBean");
        }
        initView();
        initdata();
    }

    private void initdata() {
        updateMode(MODE_CATCH);
    }

    public void update() {
        if (mResAdapter != null) {
            mResAdapter.notifyDataSetChanged();
        }
    }
    private void initUserInfo(String nodesId) {
        if (isFitter){
            //是模糊查询
            filterDateList.clear();
            filterDateList.addAll(ConfigureParse.getNodeUsers(nodesId));
                //刚开始进来的  默认都没有选中
            if (isDefultStart){
                if (filterDateList.size()>0){
                    for (int i = 0; i < filterDateList.size(); i++) {
                        filterDateList.get(i).setSelect(false);
                    }
                }
            }

        }else {
            //默认，不是模糊查询
            resourceInfos.clear();

            resourceInfos.addAll(ConfigureParse.getNodeUsers(nodesId));
                //刚开始进来的  默认都没有选中
            if (isDefultStart){
                if (resourceInfos.size()>0){
                    for (int i = 0; i < resourceInfos.size(); i++) {
                        resourceInfos.get(i).setSelect(false);
                    }
                }
            }
        }

    }



    private void initView() {
        isDefultStart=true;
        initUserInfo("");
        mLeftTv = (TextView) findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRightTv = (TextView) findViewById(R.id.right_tv);
        mRightIv = (ImageButton) findViewById(R.id.right_iv);
        mSearchLl = (LinearLayout) findViewById(R.id.activity_base_contancts_filter_serach_ll);
        mFilterEdit = (ClearEditText) findViewById(R.id.activity_base_contancts_filter_edit);
        mNoticeTv = (TextView) findViewById(R.id.activity_base_contancts__notice_tv);
        mDirectoryList = (HorizontalListView) findViewById(R.id.activity_base_contancts_directory_lv);
        mResourceList = (HSlidableListView) findViewById(R.id.activity_base_contancts_contacts_lv);
        mFitterResourceList = (HSlidableListView) findViewById(R.id.activity_base_contancts_contacts_fitter_lv);
        mSureBtn = (Button) findViewById(R.id.activity_base_contancts_sure_btn);
        mToobarRl = (RelativeLayout) findViewById(R.id.activity_base_contancts_toobar_rl);
        mQueryTv = (TextView) findViewById(R.id.activity_base_contancts_contacts_empty_tv);
        mCloseTv = (TextView) findViewById(R.id.activity_base_contancts_filter_close_tv);
        mCloseTv.setOnClickListener(this);
        mSearchLl.setOnClickListener(this);

        mLeftIv.setVisibility(View.VISIBLE);
        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
        mLeftIv.setOnClickListener(this);


        mTitleTv.setText("选择邀请人员");

        mRightTv.setOnClickListener(this);

        mSureBtn.setVisibility(View.VISIBLE);
        mSureBtn.setOnClickListener(this);

        chaneView(true);

        directoryInfos.clear();
        directoryInfos.add(rootDir);

        //初始化adrodter
        if (resourceInfos !=null && resourceInfos.size()>0){
            for (int i = 0; i < resourceInfos.size(); i++) {
                resourceInfos.get(i).setSelect(false);
            }
        }
        mResAdapter = new ContanctsAdapter(BaseContanctsActivity.this, resourceInfos,true);
        mResourceList.setVerticalScrollBarEnabled(false);
        mResourceList.setAdapter(mResAdapter);
        mResourceList.setOnItemClickListener(mOnItemClick);
        mResourceList.setEmptyView(mQueryTv);
        if (filterDateList !=null && filterDateList.size()>0){
            for (int i = 0; i < filterDateList.size(); i++) {
                filterDateList.get(i).setSelect(false);
            }
        }
        mFitterResourAdapter = new ContanctsAdapter(BaseContanctsActivity.this, filterDateList,true);
        mFitterResourceList.setAdapter(mFitterResourAdapter);
        mFitterResourceList.setOnItemClickListener(mOnItemClick);
        mFitterResourceList.setEmptyView(mQueryTv);

        mDirAdapter = new ScrollDirectoryAdapter(BaseContanctsActivity.this, directoryInfos);
        mDirectoryList.setAdapter(mDirAdapter);
        mDirectoryList.setOnItemClickListener(mDirectoryClick);
        mDirectoryList.setEmptyView(mQueryTv);
       // mResourceList.setOnFlingListener(mOnFling);

        //初始化 CharacterParser
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        mFilterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s !=null && s.length()>0){
                    chaneView(false);
                }else {
                    chaneView(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
            if (groupMeetingListBean !=null){
                List<JoinMettingPeopleBean> groupPeople = groupMeetingListBean.getGroupPeople();
                if (groupPeople !=null && groupPeople.size()>0){
                    //又被选中的
                    mSureBtn.setText("邀请 ("+groupPeople.size()+")");
                }else {
                    mSureBtn.setText("邀请 ("+"0"+")");
                }
            }
        }else {
            //从快速会议和参会列表进来的
            if (curMettingBean !=null){
                List<JoinMettingBean.MembersBean> groupPeople = curMettingBean.getMembers();
                if (groupPeople !=null && groupPeople.size()>0){
                    //又被选中的
                    mSureBtn.setText("邀请 ("+groupPeople.size()+")");
                }else {
                    mSureBtn.setText("邀请 ("+"0"+")");
                }
            }
        }

    }

    /**
     * 改变试图
     * @param isDefult
     */
    private void chaneView(boolean isDefult) {
        if (isDefult){
            //默认 标题可见
            isFitter=false;
            mToobarRl.setVisibility(View.VISIBLE);
            //取消 不出现
            mCloseTv.setVisibility(View.GONE);
            mDirectoryList.setVisibility(View.VISIBLE);
            mResourceList.setVisibility(View.VISIBLE);
            mFitterResourceList.setVisibility(View.GONE);
            if (filterDateList.size()>0){
                filterDateList.clear();
            }

            mQueryTv.setVisibility(View.GONE);

            tag =-1;
            mRightTv.setText("全选");
            mRightTv.setVisibility(View.VISIBLE);
        }else {
            //标题不可见
            isFitter=true;
            mToobarRl.setVisibility(View.VISIBLE);
            mRightTv.setVisibility(View.GONE);
            //取消 出现
            mCloseTv.setVisibility(View.VISIBLE);
            mDirectoryList.setVisibility(View.GONE);
            mResourceList.setVisibility(View.GONE);
            mFitterResourceList.setVisibility(View.VISIBLE);
            mQueryTv.setVisibility(View.GONE);
        }
   }

    @Override
    public void onClick(View v) {
       if (v.getId() == R.id.activity_base_contancts_filter_close_tv){
            //取消
            update();
            mFilterEdit.setText("");
        }else if (v.getId() == R.id.activity_base_contancts_sure_btn){
           //确定邀请人员
           sendInviteJoin();
        }else if (v.getId() == R.id.left_iv){
           //返回
           if (filterDateList.size()>0 ){
               filterDateList.clear();
           }
           if (resourceInfos.size()>0 ){
               resourceInfos.clear();
           }
           if (directoryInfos.size()>0 ){
               directoryInfos.clear();
           }
           finish();
        }else if (v.getId() == R.id.right_tv){
           //全选 或者 全不选
           String string = mRightTv.getText().toString();
           if (string.equals("全选")){
               tag =1;
           }else if (string.equals("全不选")){
               tag =0;
           }else {
               tag =-1;
           }
           secetAll();
        }else if (v.getId() == R.id.activity_base_contancts_filter_serach_ll){
                String userKey = mFilterEdit.getText().toString();
                if (userKey !=null && userKey.length()>0){
                    resuest=1;
                    if (filterDateList.size()>0){
                        filterDateList.clear();
                    }
                    showLoadingDaliog("正在查找中...");
                    WebSocketCommand.getInstance().onWssQueryPeopleByKey(userKey);
                }

        }

    }

    private void updateMode(int newMode) {

        switch (newMode) {
            case MODE_Person:
                initUserInfo("");
                rootDir.setType(SipInfo.TYPE_USE_DIRECTORY);
                break;
        }
        MODE_CATCH = newMode;
        mResAdapter.notifyDataSetChanged();

        directoryInfos.clear();
        directoryInfos.add(rootDir);
        mDirAdapter.notifyDataSetChanged();
    }

  

    @Override
    public void onResume() {
        super.onResume();
        update();
    }






    /**
     * 全选或者全部取消
     */
    private void secetAll() {
        ArrayList<SipInfo> beanList = new ArrayList<SipInfo>();
                        if (resourceInfos.size()>0){
            //获取在线人员的数据
            List<SipInfo> onlinePeoples = ConTanctsUitl.getOnlinePeoples(resourceInfos);
            List<SipInfo> outlinePeoples = ConTanctsUitl.getOutlinePeoples(resourceInfos);
            if (tag == 0) {
                //已选中，需要全部取消
                for (int i = 0; i < onlinePeoples.size(); i++) {
                    SipInfo bean = onlinePeoples.get(i);
                    String userid = bean.getUserid();
                    String username = bean.getUsername();
                    if (userid.equals(userMessge.getUserID()) &&
                            username.equals(userMessge.getUserName())){
                        bean.setSelect(true);
                    }else {
                        bean.setSelect(false);
                    }

                    beanList.add(bean);
                }
                List<SipInfo> myData=new ArrayList<>();
                //当前是取消,取消后获取剩余被选中的
                if (choicePeopleBeen.size()>0){
                    for (int i = 0; i < choicePeopleBeen.size(); i++) {
                        SipInfo sipInfo = choicePeopleBeen.get(i);
                        String userid = sipInfo.getUserid();
                        String username = sipInfo.getUsername();
                        if (userid.equals(userMessge.getUserID()) &&
                                username.equals(userMessge.getUserName())){
                            //自己不能移除
                            myData.add(sipInfo);
                        }else {
                            //别人可以移除

                        }
                    }
                }

                ArrayList<SipInfo> allCalceChoiceItems = ConTanctsUitl.
                        getAllCalceChoiceItems(beanList, choicePeopleBeen);
                if (myData.size()>0){
                    if (allCalceChoiceItems !=null && allCalceChoiceItems.size()>0){
                        for (int i = 0; i < allCalceChoiceItems.size(); i++) {
                            SipInfo sipInfo= allCalceChoiceItems.get(i);
                            String userid = sipInfo.getUserid();
                            String username = sipInfo.getUsername();
                            if (userid.equals(userMessge.getUserID()) &&
                                    username.equals(userMessge.getUserName())){
                                allCalceChoiceItems.remove(i);
                            }
                        }
                    }
                    allCalceChoiceItems.add(0,myData.get(0));
                }
                if (choicePeopleBeen.size()>0){
                    choicePeopleBeen.clear();
                }
                choicePeopleBeen.addAll(allCalceChoiceItems);

                mRightTv.setText("全选");
            } else if (tag == 1) {
                //未选，需要全部选中
                for (int i = 0; i < onlinePeoples.size(); i++) {
                    SipInfo bean = onlinePeoples.get(i);
                    String userid = bean.getUserid();
                    String username = bean.getUsername();
                    if (userid.equals(userMessge.getUserID()) &&
                            username.equals(userMessge.getUserName())){
                        bean.setSelect(true);
                    }else {
                        bean.setSelect(true);
                    }

                    beanList.add(bean);
                }
                //当前是全选了,全选后获取总共被选中的
                ArrayList<SipInfo> allCalceChoiceItems = ConTanctsUitl.getAllAddChoiceItems(beanList, choicePeopleBeen);
                if (choicePeopleBeen.size()>0){
                    choicePeopleBeen.clear();
                }
                choicePeopleBeen.addAll(allCalceChoiceItems);
                mRightTv.setText("全不选");
            }

            resourceInfos.clear();
            resourceInfos.addAll(beanList);
            resourceInfos.addAll(outlinePeoples);
            mSureBtn.setText("邀请 ("+choicePeopleBeen.size()+")");
            mResAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (filterDateList.size()>0 ){
                    filterDateList.clear();
                }

                if (resourceInfos.size()>0 ){
                    resourceInfos.clear();

                }
                if (directoryInfos.size()>0 ){
                    directoryInfos.clear();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (XTUtils.fastClick()) {
                return;
            }
            SipInfo info = null;
            if (isFitter) {
                //模糊查询
                info = filterDateList.get(position);
            } else {
                //默认，非模糊查询
                info = resourceInfos.get(position);
            }
            if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
                //成员
                if (info.getStatus() == 0) {
                    //用户不在线
                    Toast.makeText(BaseContanctsActivity.this, "该用户不在线",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //用户在线
                    resSipInfo = info;
                    boolean isMeeting=false;
                    boolean select = resSipInfo.isSelect();
                    if (select){
                            //已选中  需要取消选中
                            //自己不能够取消
                            String userID = userMessge.getUserID();
                            String userName = userMessge.getUserName();
                            if (userID.equals(info.getUserid())&&
                                    userName.equals(info.getUsername())){
                                //代表是自己
                                resSipInfo.setSelect(true);
                                ToastUtil.showShort(BaseContanctsActivity.this,"不能操作自己");
                            }else {
                                if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
                                resSipInfo.setSelect(false);
                                }else {
                                    //从快速会议来的
                                    //判断当前点击的人员是否已在参会中
                                    isMeeting=judgeIsMeeting(resSipInfo);
                                    if (isMeeting){
                                        resSipInfo.setSelect(true);
                                        ToastUtil.showShort(BaseContanctsActivity.this,"此人已在会议中");
                                    }else {
                                        resSipInfo.setSelect(false);
                                    }

                                }
                               if (!isMeeting){
                                   //删除
                                   if (choicePeopleBeen !=null && choicePeopleBeen.size()>0){
                                       List<SipInfo> sipInfos = ConTanctsUitl.delet(choicePeopleBeen, resSipInfo);
                                       if (choicePeopleBeen.size()>0){
                                           choicePeopleBeen.clear();
                                       }
                                       choicePeopleBeen.addAll(sipInfos);
                                   }
                               }
                            }

                    if (!isMeeting){
                        mRightTv.setText("全选");
                    }
                    }else {
                        //未选中  需要选中
                            //自己不能够取消
                            String userID = userMessge.getUserID();
                            String userName = userMessge.getUserName();
                            if (userID.equals(info.getUserid())&&
                                    userName.equals(info.getUsername())){
                                //代表是自己
                                resSipInfo.setSelect(true);
                                ToastUtil.showShort(BaseContanctsActivity.this,"不能操作自己");
                            }else {
                                if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
                                    //分组会议编辑来的
                                    resSipInfo.setSelect(true);
                                }else {
                                    //从快速会议来的
                                    //判断当前点击的人员是否已在参会中
                                    isMeeting=judgeIsMeeting(resSipInfo);
                                    if (isMeeting){
                                        resSipInfo.setSelect(true);
                                        ToastUtil.showShort(BaseContanctsActivity.this,"此人已在会议中");
                                    }else {
                                        resSipInfo.setSelect(true);
                                    }

                                }

                                if (!isMeeting){
                                    //添加
                                    if (choicePeopleBeen !=null && choicePeopleBeen.size()>0){
                                        List<SipInfo> sipInfos = ConTanctsUitl.add(choicePeopleBeen, resSipInfo);
                                        if (choicePeopleBeen.size()>0){
                                            choicePeopleBeen.clear();
                                        }
                                        choicePeopleBeen.addAll(sipInfos);
                                    }else {
                                        choicePeopleBeen.add(resSipInfo);
                                    }
                                }
                            }
                        if (!isFitter){
                            List<SipInfo> onlinePeoples = ConTanctsUitl.
                                    getOnlinePeoples(resourceInfos);
                            ArrayList list=new ArrayList();
                            list.addAll(onlinePeoples);
                            boolean currUiChoiceItemCount = ConTanctsUitl.
                                    getCurrUiChoiceItemCount(choicePeopleBeen, list);
                            if (currUiChoiceItemCount){
                                //当前页面全部选中了
                                mRightTv.setText("全不选");
                            }
                        }


                    }
                    //刷新界面
                    refulshUI(resSipInfo,position);
                    mSureBtn.setText("邀请(" + choicePeopleBeen.size() + ")");
                }
            } else {
                //目录
                //请求网络
                if (isFitter) {
                    mIsModifyDirectory = false;
                    sendContancts(info);
                } else {
                    mIsModifyDirectory = true;
                    sendContancts(info);
                }
            }
        }
    };

    /**
     * 判断此人是否在会议中
     * @param resSipInfo
     * @return
     */
    private boolean judgeIsMeeting(SipInfo resSipInfo) {
        if (resSipInfo !=null && curMettingBean !=null){
            List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
            if (members !=null && members.size()>0){
                for (int i = 0; i < members.size(); i++) {
                    JoinMettingBean.MembersBean bean = members.get(i);
                    if (bean.getUserName().equals(resSipInfo.getUsername())&&
                            bean.getUserName().equals(resSipInfo.getUsername())){
                        return true;
                    }
                }
            }else {
                return false;
            }
        }
        return false;
    }
    /**
     * 刷新选中与未选择，刷新界面
     * @param resSipInfo
     */
    private void refulshUI(SipInfo resSipInfo,int postion) {
        if (isFitter){
            //模糊查询
            if (filterDateList !=null && filterDateList.size()>0){
                filterDateList.remove(postion);
                filterDateList.add(postion,resSipInfo);
                mFitterResourAdapter.notifyDataSetChanged();
            }
        }else {
            if (resourceInfos !=null && resourceInfos.size()>0){
                resourceInfos.remove(postion);
                resourceInfos.add(postion,resSipInfo);
                mResAdapter.notifyDataSetChanged();
            }
        }
    }
    private AdapterView.OnItemClickListener mDirectoryClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isFitter) {
                if (XTUtils.fastClick()) {
                    return;
                }
                if (position < directoryInfos.size() - 1) {
                    for (int i = directoryInfos.size() - 1; i > position; i--) {
                        directoryInfos.remove(i);
                    }
                    mDirAdapter.notifyDataSetChanged();
                    if (directoryInfos.size() > 1) {
                        SipInfo info = directoryInfos.get(directoryInfos.size() - 1);
                        mIsModifyDirectory = false;
                        sendContancts(info);
                    } else {
                        if (MODE_CATCH == MODE_Person) {
                            initUserInfo("");
                        }
                        mFitterResourAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                if (XTUtils.fastClick()) {
                    return;
                }
                if (position < directoryInfos.size() - 1) {
                    for (int i = directoryInfos.size() - 1; i > position; i--) {
                        directoryInfos.remove(i);
                    }
                    mDirAdapter.notifyDataSetChanged();
                    if (directoryInfos.size() > 1) {
                        SipInfo info = directoryInfos.get(directoryInfos.size() - 1);
                        mIsModifyDirectory = false;
                        sendContancts(info);
                    } else {
                        if (MODE_CATCH == MODE_Person) {
                            initUserInfo("");
                            //全选按钮不可见
                            mRightTv.setVisibility(View.GONE);
                        }
                        mResAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    //////////////////////////////////////网络请求//////////////////////
    /**
     * 邀请成员加入
     *  String members="[{index:0,userID:\"chenmingjun\", userName:\"陈明军\", resourceType:\"User\"}]";
     */
    private void sendInviteJoin(){
        if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
            //从快速会议来的
            if (choicePeopleBeen.size()>0 && curMettingBean !=null){
                ArrayList<SipInfo> list = removeDuplicates(curMettingBean);
                if (list.size()>0){
                    showLoadingDaliog("正在加载中...");
                    String sceneID = curMettingBean.getSceneID();
                    WebSocketCommand.getInstance().onWssAddMembers(sceneID,list);
                }else {
                    ToastUtil.showShort(BaseContanctsActivity.this,"请选择除已在会中成员加入会议");
                }
            }else {
                ToastUtil.showShort(BaseContanctsActivity.this,"请选择成员加入会议");
            }
        }else if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
            //从分组会议中详情，添加成员来的
            //将数据填充到分组会议详情中
            if (choicePeopleBeen.size()>0){
                GroupMeetingListBean bean=new GroupMeetingListBean();
                bean.setGroupContent("");
                bean.setGroupID("");
                bean.setGroupTitle("");
                List<JoinMettingPeopleBean> list=new ArrayList<>();
                for (int i = 0; i < choicePeopleBeen.size(); i++) {
                    JoinMettingPeopleBean poeple=new JoinMettingPeopleBean();
                    poeple.setName(choicePeopleBeen.get(i).getUsername());
                    poeple.setUseId(choicePeopleBeen.get(i).getUserid());
                    poeple.setOnline(choicePeopleBeen.get(i).isShow());
                    poeple.setMeetingID("");
                    poeple.setChoice(false);
                    list.add(poeple);
                }
                bean.setGroupPeople(list);
                String json = FastJsonTools.bean2Json(bean);
                Intent intent=new Intent(BaseContanctsActivity.this,EditGroupMeetingActivity.class);
                intent.putExtra("data",json);
                setResult(1001,intent);
                clearData();
                finish();

            }else {
                ToastUtil.showShort(BaseContanctsActivity.this,"请选择成员加入会议");
            }

        }else if (activtyTag.equals(Constants.ACTIVTY_YQCY_LIST)){
            //从参会成员列表界面来的
            if (choicePeopleBeen.size()>0 && curMettingBean !=null){
                ArrayList<SipInfo> list = removeDuplicates(curMettingBean);
                if (list.size()>0){
                    showLoadingDaliog("正在加载中...");
                    String sceneID = curMettingBean.getSceneID();
                    WebSocketCommand.getInstance().onWssAddMembers(sceneID,list);
                }else {
                    ToastUtil.showShort(BaseContanctsActivity.this,"请选择除已在会中成员加入会议");
                }

            }else {
                ToastUtil.showShort(BaseContanctsActivity.this,"请选择成员加入会议");
            }

        }else if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
            //从第三方来的
			    ArrayList<SipInfo> list = removeDuplicates(curMettingBean);
                if (list.size()>0){
                    showLoadingDaliog("正在加载中...");
                    String sceneID = curMettingBean.getSceneID();
                    WebSocketCommand.getInstance().onWssAddMembers(sceneID,list);
                }else {
                    ToastUtil.showShort(BaseContanctsActivity.this,"请选择除已在会中成员加入会议");
                }
            }else {
                ToastUtil.showShort(BaseContanctsActivity.this,"请选择成员加入会议");
            }
        }



    /**
     * 去掉已经在会人员的
     * @param curMettingBean
     */
    private ArrayList<SipInfo> removeDuplicates(JoinMettingBean curMettingBean) {
        ArrayList<SipInfo> alldata=new ArrayList<>();
        if (choicePeopleBeen.size()>0){
            alldata.addAll(choicePeopleBeen);
            if (curMettingBean !=null){
                List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
                if (members !=null && members.size()>0){
                    //以前有选中的成员，从choicePeopleBeen中去掉以前选中的
                    for (int i = 0; i < members.size(); i++) {
                        JoinMettingBean.MembersBean bean = members.get(i);
                        String userid = bean.getUserID();
                        String username = bean.getUserName();
                        for (int j = 0; j < alldata.size(); j++) {
                            SipInfo sipInfo = alldata.get(j);
                            if (userid.equals(sipInfo.getUserid())&&
                                    username.equals(sipInfo.getUsername())){
                                alldata.remove(j);
                            }

                        }
                    }


                }else {
                    for (int i = 0; i < alldata.size(); i++) {
                        String userID = userMessge.getUserID();
                        String userName = userMessge.getUserName();
                        if (userID.equals(alldata.get(i).getUserid())&&
                                userName.equals(alldata.get(i).getUsername())){
                            //自己去掉
                            alldata.remove(i);

                        }
                    }

                    return alldata;

                }
            }else {
                for (int i = 0; i < alldata.size(); i++) {
                    String userID = userMessge.getUserID();
                    String userName = userMessge.getUserName();
                    if (userID.equals(alldata.get(i).getUserid())&&
                            userName.equals(alldata.get(i).getUsername())){
                        //自己去掉
                        alldata.remove(i);
                    }
                }
                return alldata;
            }
        }
        return alldata;

    }

    /**
     * 清除选中的数据
     */
    private void clearData() {
        choicePeopleBeen.clear();
        if (isFitter){
            if (filterDateList !=null && filterDateList.size()>0){
                for (int i = 0; i < filterDateList.size(); i++) {
                    filterDateList.get(i).setSelect(false);
                }
                mFitterResourAdapter.notifyDataSetChanged();
            }
        }else {
            if (resourceInfos !=null && resourceInfos.size()>0){
                for (int i = 0; i < resourceInfos.size(); i++) {
                    resourceInfos.get(i).setSelect(false);
                }
                mResAdapter.notifyDataSetChanged();
            }
        }

        mSureBtn.setText("邀请("+choicePeopleBeen.size()+")");
    }

    private void parseResource(String msg) {
        if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0
                || msg.indexOf(WssContant.WSS_INFORM_REFRESH_RESOURCE) >= 0) {
            Log.i("android", "requestResourceAnswer: msg=" + msg);
            if (dirSipInfo != null) {
                if (mIsModifyDirectory) {
                    directoryInfos.add(dirSipInfo);
                }
                if (MODE_CATCH == MODE_Person) {
                    initUserInfo(dirSipInfo.getUserid());
                    parseNewResource(msg);
                    if (isFitter) {
                        mFitterResourAdapter.notifyDataSetChanged();
                        mFitterResourceList.setSelection(0);
                    } else {
                        mResAdapter.notifyDataSetChanged();
                        mDirAdapter.notifyDataSetChanged();
                        mResourceList.setSelection(0);
                    }
                }
                dirSipInfo = null;
            } else {
                parseNewResource(msg);
                if (isFitter) {
                    mFitterResourAdapter.notifyDataSetChanged();
                    mFitterResourceList.setSelection(0);
                } else {
                    mResAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    private void parseNewResource(String msg) {
        if (isFitter) {
            hideLoadingDialog();
            if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0) {
                fitTmpInfos.clear();
                filterDateList.clear();
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
                if (tmpSipInfos !=null && tmpSipInfos.size()>0){
                    for (int i = 0; i < tmpSipInfos.size(); i++) {
                        SipInfo info = tmpSipInfos.get(i);
                        if (info.getStatus() == 1) {
                            filterDateList.add(info);
                        } else {
                            fitTmpInfos.add(info);
                        }
                    }
                    if (fitTmpInfos.size() > 0) {
                        filterDateList.addAll(fitTmpInfos);

                        List<SipInfo> outlinePeopleStauts = ConTanctsUitl.
                                getOutlinePeopleStauts(fitTmpInfos, filterDateList);
                        filterDateList.clear();
                        filterDateList.addAll(outlinePeopleStauts);
                        List<SipInfo> choicePeoplesDeletOutline = ConTanctsUitl.
                                getChoicePeoplesDeletOutline(choicePeopleBeen, fitTmpInfos);
                        if (choicePeoplesDeletOutline.size()>0){
                            if (choicePeopleBeen.size()>0){
                                choicePeopleBeen.clear();
                            }
                            choicePeopleBeen.addAll(choicePeoplesDeletOutline);
                        }
                        mRightTv.setVisibility(View.GONE);
                    }

                    ArrayList<SipInfo> list = ConTanctsUitl.
                            showCurrUiData(filterDateList, choicePeopleBeen);
                    filterDateList.clear();
                    filterDateList.addAll(list);
                    mFitterResourAdapter.notifyDataSetChanged();
                }

            }
        } else {
            if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0) {
                tmpInfos.clear();
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
                if (tmpSipInfos !=null && tmpSipInfos.size()>0){
                    //需要添加已经选中的
                    if (activtyTag.equals(Constants.ACTIVTY_GROUP_DETIAL_ADD)){
                        //分组会议来的
                        if (groupMeetingListBean !=null){
                            addChoiceData(groupMeetingListBean,tmpSipInfos);
                        }
                    }else {
                        //从快速会议和参会列表来的
                        if (curMettingBean !=null){
                            addChoiceDataMeeting(curMettingBean,tmpSipInfos);
                        }
                    }
                    for (int i = 0; i < tmpSipInfos.size(); i++) {
                        SipInfo info = tmpSipInfos.get(i);
                        if (info.getStatus() == 1) {
                            //在线
                            resourceInfos.add(info);
                        } else {
                            //不在线
                            tmpInfos.add(info);
                        }
                    }
                    if (tmpInfos.size() > 0) {
                        //有离线的数据
                        //判断下是否有选中的
                        boolean isAllChoice = ConTanctsUitl.
                                getCurrUiChoiceItemCount(choicePeopleBeen,
                                        resourceInfos);
                        if (isAllChoice){
                            mRightTv.setText("全不选");

                        }else {
                            mRightTv.setText("全选");
                        }
                        resourceInfos.addAll(tmpInfos);
                        List<SipInfo> outlinePeopleStauts = ConTanctsUitl.
                                getOutlinePeopleStauts(tmpInfos, resourceInfos);
                        resourceInfos.clear();
                        resourceInfos.addAll(outlinePeopleStauts);
                        List<SipInfo> choicePeoplesDeletOutline = ConTanctsUitl.
                                getChoicePeoplesDeletOutline(choicePeopleBeen, tmpInfos);
                        if (choicePeoplesDeletOutline.size()>0){
                            if (choicePeopleBeen.size()>0){
                                choicePeopleBeen.clear();
                            }
                            choicePeopleBeen.addAll(choicePeoplesDeletOutline);
                        }

                        mRightTv.setVisibility(View.VISIBLE);
                    }else {
                        //没有离线的数据
                        mRightTv.setVisibility(View.VISIBLE);
                        boolean isAllChoice = ConTanctsUitl.
                                getCurrUiChoiceItemCount(choicePeopleBeen,
                                        resourceInfos);
                        if (isAllChoice){
                            mRightTv.setText("全不选");

                        }else {
                            mRightTv.setText("全选");
                        }

                    }



                    ArrayList<SipInfo> list = ConTanctsUitl.
                            showCurrUiData(resourceInfos, choicePeopleBeen);
                    resourceInfos.clear();
                    resourceInfos.addAll(list);
                    mResAdapter.notifyDataSetChanged();
                }
            } else if (msg.indexOf(WssContant.WSS_INFORM_REFRESH_RESOURCE) >= 0) {
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
                if (tmpSipInfos !=null && tmpSipInfos.size()>0){
                    ArrayList<SipInfo> outLines=new ArrayList<>();
                    List<SipInfo> list1 = ConTanctsUitl.getOutlinePeoples(tmpSipInfos);
                    outLines.addAll(list1);
                    for (int i = 0; i < tmpSipInfos.size(); i++) {
                        SipInfo info1 = tmpSipInfos.get(i);
                        if (info1.getBelongsys().equals(directoryInfos.get(directoryInfos.size()-1).getUserid())) {
                            boolean find = false;
                            for (int j = 0; j < resourceInfos.size(); j++) {
                                SipInfo info2 = resourceInfos.get(j);
                                if (info1 != null && info2 != null && info1.getUserid().equals(info2.getUserid())) {
                                    find = true;
                                    int status = info1.getStatus();
                                    SipInfo info3 = resourceInfos.remove(j);
                                    info3.setStatus(status);
                                    if (status == 1) {
                                        //在线
                                        int cnt = getOnLineNum(resourceInfos);
                                        resourceInfos.add(cnt, info3);
                                    } else {
                                        //离线
                                        resourceInfos.add(info3);
                                    }
                                    break;
                                }
                            }
                            if (!find) {
                                int status = info1.getStatus();
                                if (status == 1) {
                                    int cnt = getOnLineNum(resourceInfos);
                                    resourceInfos.add(cnt, info1);
                                } else {
                                    resourceInfos.add(info1);
                                }
                            }
                        }
                    }
                    //去重
                    if (outLines.size()>0){
                        List<SipInfo> outlinePeopleStauts = ConTanctsUitl.
                                getOutlinePeopleStauts(outLines, resourceInfos);
                        resourceInfos.clear();
                        resourceInfos.addAll(outlinePeopleStauts);

                        List<SipInfo> choicePeoplesDeletOutline = ConTanctsUitl.
                                getChoicePeoplesDeletOutline(outLines, choicePeopleBeen);
                        if (choicePeopleBeen.size()>0){
                            choicePeopleBeen.clear();
                        }
                        choicePeopleBeen.addAll(choicePeoplesDeletOutline);

                        ArrayList<SipInfo> list = ConTanctsUitl.
                                showCurrUiData(resourceInfos, choicePeopleBeen);
                        resourceInfos.clear();
                        resourceInfos.addAll(list);
                        mResAdapter.notifyDataSetChanged();
                    }
                }

            }
        }
    }

    /**
     * 将从分组会议过来的选中的数据格式化
     * @param groupMeetingListBean
     */
    private void addChoiceData(GroupMeetingListBean groupMeetingListBean,
                               ArrayList<SipInfo> tmpSipInfos) {
        List<JoinMettingPeopleBean> groupPeople = groupMeetingListBean.getGroupPeople();
        if (groupPeople !=null && groupPeople.size()>0){
            for (int i = 0; i < groupPeople.size(); i++) {
                JoinMettingPeopleBean bean = groupPeople.get(i);
                for (int j = 0; j < tmpSipInfos.size(); j++) {
                    SipInfo sipInfo = tmpSipInfos.get(j);
                    int status = sipInfo.getStatus();
                    if (status==1){
                        //在线
                        String username = sipInfo.getUsername();
                        String userid = sipInfo.getUserid();
                        if (username.equals(bean.getName())&&
                                userid.equals(bean.getUseId())){
                            //相同，添加
                            if (choicePeopleBeen.size()>0){
                                Boolean eixit = ConTanctsUitl.isEixit(choicePeopleBeen, sipInfo);
                                if (eixit){
                                    //存在不添加
                                }else {
                                    //不存在添加
                                    choicePeopleBeen.add(sipInfo);
                                }
                            }else {
                                choicePeopleBeen.add(sipInfo);
                            }

                        }

                    }else {
                        //不在线
                    }
                }
            }


        }
    }


    /**
     * 将从快速会议过来的选中的数据格式化
     * @param joinMettingBean
     */
    private void addChoiceDataMeeting(JoinMettingBean joinMettingBean,
                               ArrayList<SipInfo> tmpSipInfos) {
        List<JoinMettingBean.MembersBean> groupPeople = joinMettingBean.getMembers();
        if (groupPeople !=null && groupPeople.size()>0){
            for (int i = 0; i < groupPeople.size(); i++) {
                JoinMettingBean.MembersBean bean = groupPeople.get(i);
                for (int j = 0; j < tmpSipInfos.size(); j++) {
                    SipInfo sipInfo = tmpSipInfos.get(j);
                    int status = sipInfo.getStatus();
                    if (status==1){
                        //在线
                        String username = sipInfo.getUsername();
                        String userid = sipInfo.getUserid();
                        if (username.equals(bean.getUserName())&&
                                userid.equals(bean.getUserID())){
                            //相同，添加
                            if (choicePeopleBeen.size()>0){
                                Boolean eixit = ConTanctsUitl.isEixit(choicePeopleBeen, sipInfo);
                                if (eixit){
                                    //存在不添加
                                }else {
                                    //不存在添加
                                    choicePeopleBeen.add(sipInfo);
                                }
                            }else {
                                choicePeopleBeen.add(sipInfo);
                            }

                        }

                    }else {
                        //不在线
                    }
                }
            }


        }
    }

    private ArrayList<SipInfo> formatResource(String msg) {
        ArrayList<SipInfo> tmpInfos = new ArrayList<SipInfo>();
        try {
            JSONObject obj = new JSONObject(msg);
            JSONObject exObj = obj.getJSONObject("extend");
            String userId = exObj.getString("userID");
            if (userId != null && userId.equals(mUserId)) {
                String bodyStr = obj.getString("body");
                JSONObject bodyObj = new JSONObject(bodyStr);
                JSONObject paramsObj = bodyObj.getJSONObject("params");
                JSONArray resourcesArray = paramsObj.getJSONArray("resources");
                for (int i = 0; i < resourcesArray.length(); i++) {
                    JSONObject tmpObj = resourcesArray.getJSONObject(i);
                    String resourceID = tmpObj.getString("resourceID");
                    String busStatus = tmpObj.getString("busStatus");
                    String resourceName = tmpObj.getString("resourceName");
                    String isOnline = tmpObj.getString("isOnline");
                    String parentId = tmpObj.getString("parentId");
                    String resourceType = tmpObj.getString("resourceType");
                    SipInfo info = new SipInfo("", "", 0, resourceID, resourceName, parentId,
                            SipInfo.TYPE_USE_RESOURCE);
                    info.setBusinesstatus(Integer.valueOf(busStatus));
                    if (resourceType != null && resourceType.equals("User")) {
                        info.setType(SipInfo.TYPE_USE_RESOURCE);
                    } else {
                        info.setType(SipInfo.TYPE_DEV_RESOURCE);
                    }
                    if (isOnline != null && isOnline.equals("true")) {
                        info.setStatus(1);
                    } else {
                        info.setStatus(0);
                    }
                    tmpInfos.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpInfos;
    }
    private int getOnLineNum(ArrayList<SipInfo> infos) {
        int cnt = 0;
        if (infos != null) {
            for (int i = 0; i < infos.size(); i++) {
                if (infos.get(i).getStatus() == 1) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
    ///////////////////////////////////////webservice///////////////
    /**
     * 请求通讯录，逐级请求  请求websivce
     * @param info
     */
    private void sendContancts(SipInfo info) {

        if (dirSipInfo == null || !dirSipInfo.getUserid().equals(info.getUserid())) {
            dirSipInfo = info;
            String dirID = info.getUserid();
            if (info.getType() == SipInfo.TYPE_USE_DIRECTORY && MODE_CATCH == MODE_Person) {
            WebSocketCommand.getInstance().onSendRequestResource(true, dirID);
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
     *
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

        super.onReceiveWssMessage(msg);
        if (msg.isEmpty()) {
            return;
        } else if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0
                || msg.indexOf(WssContant.WSS_INFORM_REFRESH_RESOURCE) >= 0) {
            //获取通讯录
            if (dirSipInfo != null) {
                if (MODE_CATCH == MODE_Person) {
                    parseResource(msg);
                    //刷新计数界面
                    mSureBtn.setText("邀请 ("+choicePeopleBeen.size()+")");

                }
            } else {
                parseResource(msg);
                //刷新计数界面
                mSureBtn.setText("邀请 ("+choicePeopleBeen.size()+")");
            }
        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0){
            if (activtyTag.equals(Constants.ACTIVTY_KSHY)){
                //主席
                //邀请成员
                hideLoadingDialog();
                Intent intent=new Intent(BaseContanctsActivity.this,
                        VedioMettingActivity.class);
                if (curMettingBean !=null && choicePeopleBeen !=null &&
                        choicePeopleBeen.size()>0){
                    //转换数据格式
                     swtichData(choicePeopleBeen);
                    String result= FastJsonTools.bean2Json(curMettingBean);
                    intent.putExtra("JoinMettingBean",result);
                    setResult(1003,intent);
                    finish();
                }

                Log.i("android", "onReceiveWssMessage: msg="+msg);
            }else if (activtyTag.equals(Constants.ACTIVTY_YQCY_LIST)){
                //从参会成员列表界面来的  这里包含第三方也包含非第三方的
                //邀请成员
                hideLoadingDialog();
                Intent intent=new Intent(BaseContanctsActivity.this,
                        JoinMettingPeopleListActivity.class);
                if (curMettingBean !=null && choicePeopleBeen !=null &&
                        choicePeopleBeen.size()>0){
                    //转换数据格式
                    swtichData(choicePeopleBeen);
                    String result= FastJsonTools.bean2Json(curMettingBean);
                    intent.putExtra("JoinMettingBean",result);
                    intent.putExtra("activityTag","");
                    setResult(1001,intent);
                    finish();
                }

                Log.i("android", "onReceiveWssMessage: msg="+msg);
            }else if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //从第三方来的
                //主席
                //邀请成员
                hideLoadingDialog();
                Intent intent=new Intent(BaseContanctsActivity.this,
                        VedioMettingActivity.class);
                if (curMettingBean !=null && choicePeopleBeen !=null &&
                        choicePeopleBeen.size()>0){
                    //转换数据格式
                    swtichData(choicePeopleBeen);
                    String result= FastJsonTools.bean2Json(curMettingBean);
                    intent.putExtra("JoinMettingBean",result);
                    setResult(1004,intent);
                    finish();
                }

                Log.i("android", "onReceiveWssMessage: msg="+msg);
            }

        }else if (msg.indexOf(WssContant.WSS_STOP_METTING) >= 0){

        }else if (msg.indexOf("publishAcceptSpeakerFromConference")>0 ||
                msg.indexOf(WssContant.WSS_MEETING_APPLY_SPEAK_SHOW)>0){
            if (activtyTag.equals(Constants.ACTIVTY_THRID_APP)){
                //第三方
            }else {
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
                                                initIsApplySpaekDialog(title,contentText,sceneID,memberID);
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }

            }


        }
    }


    private void initIsApplySpaekDialog(String title , String text,
                                        final String meetingId, final String menmberId) {
        //主席是否允许成员发言
        isApplySpeakDialog = DailogUitl.initTextDialog(BaseContanctsActivity.this, title, text,"拒绝", "同意", new DialogInterface.OnClickListener() {
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

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoadingDialog();
            Intent intent=new Intent(BaseContanctsActivity.this,
                    VedioMettingActivity.class);
            setResult(1006,intent);
            finish();
        }
    };

    /**
     * 数据转换
     * @param choicePeopleBeen
     */
    private void swtichData(ArrayList<SipInfo> choicePeopleBeen) {
        List<JoinMettingBean.MembersBean> membersBeen=new ArrayList<>();
        for (int i = 0; i < choicePeopleBeen.size(); i++) {
            JoinMettingBean.MembersBean membersBean = new JoinMettingBean.MembersBean();
            membersBean.setRole("memberman");
            membersBean.setUserID(choicePeopleBeen.get(i).getUserid());
            membersBean.setIndex(i+"");
            membersBean.setStatus("onlineInMeeting");
            membersBeen.add(membersBean);
        }
        List<JoinMettingBean.MembersBean> members = curMettingBean.getMembers();
        if (members !=null && members.size()>0){
            members.addAll(membersBeen);
            curMettingBean.setMembers(members);
        }else {
            curMettingBean.setMembers(membersBeen);
        }
    }


}
