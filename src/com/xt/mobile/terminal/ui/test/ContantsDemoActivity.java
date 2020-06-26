package com.xt.mobile.terminal.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.ContanctsAdapter;
import com.xt.mobile.terminal.adapter.ScrollDirectoryAdapter;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.ui.activity.ActivityCalling;
import com.xt.mobile.terminal.util.CharacterParser;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.PinyinComparator;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.HSlidableListView;
import com.xt.mobile.terminal.view.HorizontalListView;
import com.xt.mobile.terminal.view.dailog.MyDialog_Call;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ContantsDemoActivity extends BaseActivity {
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
    private SipInfo dirSipInfo = null;
    private SipInfo resSipInfo = null;
    private SipInfo rootDir = new SipInfo("", "", 0, "root", "根目录", "", SipInfo.TYPE_USE_RESOURCE);

    public static final int MODE_Person = 0;
    private static int MODE_CATCH = MODE_Person;
    private CharacterParser characterParser;
    private TextView mQueryTv;
    private PinyinComparator pinyinComparator;
    private TextView mCloseTv;
    private ArrayList<SipInfo> choicePeopleBeen = new ArrayList<>();
    private ArrayList<SipInfo> choiceOneDirBeen = new ArrayList<>();
    private ArrayList<SipInfo> choiceTwoDirBeen = new ArrayList<>();
    private ArrayList<SipInfo> filterDateList = new ArrayList<SipInfo>();
    private boolean isFitter=false;
    private ContanctsAdapter mFitterResourAdapter;
    private int tag=-1;
    private int dirType=0;
    private boolean isDefultStart=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_contancts);
//        initView();
//        initdata();
    }


//    private void initdata() {
//        updateMode(MODE_CATCH);
//    }
//
//    private void initUserInfo(String nodesId,String type) {
//        if (isFitter){
//            //是模糊查询
//            filterDateList.clear();
//            ArrayList<SipInfo> nodeUsers = ConfigureParse.getNodeUsers(nodesId);
//            if (type.equals("0")){
//                //刚开始进来的  默认都没有选中
//                if (nodeUsers !=null && nodeUsers.size()>0){
//                    for (int i = 0; i < nodeUsers.size(); i++) {
//                        nodeUsers.get(i).setSelect(false);
//                    }
//                    filterDateList.addAll(nodeUsers);
//                }
//            }else {
//                filterDateList.addAll(nodeUsers);
//            }
//
//        }else {
//            //默认，不是模糊查询
//            resourceInfos.clear();
//
//            ArrayList<SipInfo> nodeUsers = ConfigureParse.getNodeUsers(nodesId);
//            if (type.equals("0")){
//                //刚开始进来的  默认都没有选中
//                if (nodeUsers !=null && nodeUsers.size()>0){
//                    for (int i = 0; i < nodeUsers.size(); i++) {
//                        nodeUsers.get(i).setSelect(false);
//                    }
//                    resourceInfos.addAll(nodeUsers);
//                }
//            }else {
//                resourceInfos.addAll(nodeUsers);
//            }
//        }
//
//    }
//
//    /**
//     * 更新数据
//     */
//    public void update() {
//
//        if (mResAdapter != null) {
//            mResAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private void initView() {
//        isDefultStart=true;
//        initUserInfo("","0");
//        mLeftTv = (TextView) findViewById(R.id.left_tv);
//        mLeftIv = (ImageButton) findViewById(R.id.left_iv);
//        mTitleTv = (TextView) findViewById(R.id.title_tv);
//        mRightTv = (TextView) findViewById(R.id.right_tv);
//        mRightIv = (ImageButton) findViewById(R.id.right_iv);
//        mFilterEdit = (ClearEditText) findViewById(R.id.activity_base_contancts_filter_edit);
//        mNoticeTv = (TextView) findViewById(R.id.activity_base_contancts__notice_tv);
//        mDirectoryList = (HorizontalListView) findViewById(R.id.activity_base_contancts_directory_lv);
//        mResourceList = (HSlidableListView) findViewById(R.id.activity_base_contancts_contacts_lv);
//        mFitterResourceList = (HSlidableListView) findViewById(R.id.activity_base_contancts_contacts_fitter_lv);
//        mSureBtn = (Button) findViewById(R.id.activity_base_contancts_sure_btn);
//        mToobarRl = (RelativeLayout) findViewById(R.id.activity_base_contancts_toobar_rl);
//        mQueryTv = (TextView) findViewById(R.id.activity_base_contancts_contacts_empty_tv);
//        mCloseTv = (TextView) findViewById(R.id.activity_base_contancts_filter_close_tv);
//        mCloseTv.setOnClickListener(this);
//
//        mLeftIv.setVisibility(View.VISIBLE);
//        mLeftIv.setBackgroundResource(R.drawable.login_params_back);
//        mLeftIv.setOnClickListener(this);
//
//
//        mTitleTv.setText("选择邀请人员");
//
//        mRightTv.setVisibility(View.VISIBLE);
//        mRightTv.setText("全选");
//        mRightTv.setOnClickListener(this);
//
//        mSureBtn.setVisibility(View.VISIBLE);
//        mSureBtn.setOnClickListener(this);
//
//        chaneView(true);
//
//        directoryInfos.clear();
//        directoryInfos.add(rootDir);
//
//        //初始化adrodter
//        if (resourceInfos !=null && resourceInfos.size()>0){
//            for (int i = 0; i < resourceInfos.size(); i++) {
//                resourceInfos.get(i).setSelect(false);
//            }
//        }
//        mResAdapter = new ContanctsAdapter(ContantsDemoActivity.this, resourceInfos,true);
//        mResourceList.setVerticalScrollBarEnabled(false);
//        mResourceList.setAdapter(mResAdapter);
//        mResAdapter.setListener(mOnItemClick);
//        //mResourceList.setOnItemClickListener(mOnItemClick);
//        if (filterDateList !=null && filterDateList.size()>0){
//            for (int i = 0; i < filterDateList.size(); i++) {
//                filterDateList.get(i).setSelect(false);
//            }
//        }
//        mFitterResourAdapter = new ContanctsAdapter(ContantsDemoActivity.this, filterDateList,true);
//        mFitterResourceList.setAdapter(mFitterResourAdapter);
//        mFitterResourAdapter.setListener(mOnItemClick);
//
//        mDirAdapter = new ScrollDirectoryAdapter(ContantsDemoActivity.this, directoryInfos);
//        mDirectoryList.setAdapter(mDirAdapter);
//        mDirectoryList.setOnItemClickListener(mDirectoryClick);
//        // mResourceList.setOnFlingListener(mOnFling);
//
//        //初始化 CharacterParser
//        characterParser = CharacterParser.getInstance();
//        pinyinComparator = new PinyinComparator();
//
//        mFilterEdit.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                chaneView(false);
//                filterData(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//    }
//
//    /**
//     * 改变试图
//     * @param isDefult
//     */
//    private void chaneView(boolean isDefult) {
//        if (isDefult){
//            //默认 标题可见
//            isFitter=false;
//            mToobarRl.setVisibility(View.VISIBLE);
//            //取消 不出现
//            mCloseTv.setVisibility(View.GONE);
//            mDirectoryList.setVisibility(View.VISIBLE);
//            mResourceList.setVisibility(View.VISIBLE);
//            mFitterResourceList.setVisibility(View.GONE);
//
//            mQueryTv.setVisibility(View.GONE);
//        }else {
//            //标题不可见
//            isFitter=true;
//            mToobarRl.setVisibility(View.GONE);
//            //取消 出现
//            mCloseTv.setVisibility(View.VISIBLE);
//            mDirectoryList.setVisibility(View.GONE);
//            mResourceList.setVisibility(View.GONE);
//            mFitterResourceList.setVisibility(View.VISIBLE);
//            mQueryTv.setVisibility(View.GONE);
//        }
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.activity_base_contancts_filter_close_tv:
//                //取消
//                chaneView(true);
//                //刷新
//                update();
//                break;
//            case R.id.activity_base_contancts_sure_btn:
//                //确认
//                ToastUtil.showShort(ContantsDemoActivity.this,"确认");
//                break;
//            case R.id.left_iv:
//                //返回
//                if (filterDateList.size()>0 ){
//                    filterDateList.clear();
//                }
//                if (resourceInfos.size()>0 ){
//                    resourceInfos.clear();
//                }
//                if (directoryInfos.size()>0 ){
//                    directoryInfos.clear();
//                }
//                finish();
//                break;
//            case R.id.right_tv:
//                //全选 或者 全不选
//                break;
//        }
//    }
//
//    private void updateMode(int newMode) {
//
//        switch (newMode) {
//            case MODE_Person:
//                initUserInfo("","0");
//                rootDir.setType(SipInfo.TYPE_USE_DIRECTORY);
//                break;
//        }
//        MODE_CATCH = newMode;
//        mResAdapter.notifyDataSetChanged();
//
//        directoryInfos.clear();
//        directoryInfos.add(rootDir);
//        mDirAdapter.notifyDataSetChanged();
//    }
//
////    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener() {
////
////        @Override
////        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////            // TODO Auto-generated method stub
////            if (XTUtils.fastClick()) {
////                return;
////            }
////            SipInfo info = resourceInfos.get(position);
////            if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
////                if (info.getStatus() == 0) {
////                    Toast.makeText(BaseContanctsActivity.this, "该用户不在线",
////                            Toast.LENGTH_SHORT).show();
////                } else {
////                    resSipInfo = info;
////                    MyDialog_Call dialog = new MyDialog_Call(BaseContanctsActivity.this, onCallBack);
////                    dialog.show();
////                }
////            } else {
////                sendContancts(info);
////                directoryInfos.add(info);
////            }
////        }
////    };
//
//    private ContanctsAdapter.ContanctsCall mOnItemClick = new ContanctsAdapter.ContanctsCall() {
//        @Override
//        public void onItemClick(int postion, SipInfo info) {
//            if (XTUtils.fastClick()) {
//                return;
//            }
//            if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
//                if (info.getStatus() == 0) {
//                    Toast.makeText(ContantsDemoActivity.this, "该用户不在线",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    resSipInfo = info;
//                    MyDialog_Call dialog = new MyDialog_Call(ContantsDemoActivity.this, onCallBack);
//                    dialog.show();
//                }
//            } else {
//                if (isFitter){
//                    //请求网络
//
//                    sendContancts(info);
//                }else {
//
//                    sendContancts(info);
//                    directoryInfos.add(info);
//                }
//
//            }
//        }
//
//        @Override
//        public void onCheckBoxClick(int postion, ArrayList<SipInfo> sipInfos) {
//            //选择框
//            if (isFitter){
//                secetItem(postion,filterDateList,isFitter);
//            }else {
//                secetItem(postion,resourceInfos,isFitter);
//            }
//
//        }
//    } ;
//
//    private MyDialog_Call.VideoCallCallBack onCallBack = new MyDialog_Call.VideoCallCallBack() {
//
//        @Override
//        public void setResult(String result) {
//            // TODO Auto-generated method stub
//            if (result != null && result.equals("VideoCall") && resSipInfo != null) {
//                Intent callIntent = new Intent(ContantsDemoActivity.this, ActivityCalling.class);
//                callIntent.putExtra("ReceiverId", resSipInfo.getUserid());
//                callIntent.putExtra("ReceiverName", resSipInfo.getUsername());
//                callIntent.putExtra("MediaType", "VideoCall");
//                callIntent.putExtra("IsReceive", false);
//                startActivity(callIntent);
//            } else if (result != null && result.equals("AudioRing") && resSipInfo != null) {
//                Toast.makeText(ContantsDemoActivity.this, "敬请期待", Toast.LENGTH_SHORT)
//                        .show();
//            }
//            resSipInfo = null;
//        }
//    };
//
//
//    private AdapterView.OnItemClickListener mDirectoryClick = new AdapterView.OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            // TODO Auto-generated method stub
//            if (XTUtils.fastClick()) {
//                return;
//            }
//            if (position < directoryInfos.size() - 1) {
//                for (int i = directoryInfos.size() - 1; i > position; i--) {
//                    directoryInfos.remove(i);
//                }
//                mDirAdapter.notifyDataSetChanged();
//
//                if (directoryInfos.size() > 1) {
//                    //这里是回到第二级目录
//                    dirType=2;
//                    ToastUtil.showShort(ContantsDemoActivity.this,"choiceOneDirBeen="+choiceOneDirBeen.size()+"\n" +
//                            "choiceTwoDirBeen="+choiceTwoDirBeen.size());
//                    SipInfo info = directoryInfos.get(directoryInfos.size() - 1);
//                    sendContancts(info);
//                } else {
//                    if (MODE_CATCH == MODE_Person) {
//                        //这里是回到第一级目录
//                        dirType=1;
//                        dirSipInfo=null;
//                        initUserInfo("","1");
//                    }
//                    mResAdapter.notifyDataSetChanged();
//                }
//            }
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        update();
//    }
//
//
//    ///////////////////////////////////////webservice///////////////
//    /**
//     * 请求通讯录，逐级请求  请求websivce
//     * @param info
//     */
//    private void sendContancts(SipInfo info) {
//
//        if (info.getType() == SipInfo.TYPE_USE_DIRECTORY && MODE_CATCH == MODE_Person) {
//            String dirID = info.getUserid();
//            WebSocketCommand.getInstance().onSendRequestResource(true, dirID);
//            dirSipInfo = info;
//        }
//    }
//
//    /**
//     * 请求网络返回的结果
//     * @param msg
//     */
//    protected void onReceiveWssMessage(String msg) {
//
//        super.onReceiveWssMessage(msg);
//
//        if (msg.indexOf(WssContant.WSS_MAIN_USERS_STATUS) >= 0) {
//            if (dirSipInfo != null) {
//                if (MODE_CATCH == MODE_Person) {
//                    parseNewResource(msg);
//                    //刷新计数界面
//                    mSureBtn.setText("邀请 ("+choicePeopleBeen.size()+")");
//
//                }
//
//            }
//        }
//    }
//
//    /**
//     * 根据选中得item刷新界面
//     */
//    private void showChoiceItems(ArrayList<SipInfo> result) {
//        if (isFitter){
//            switch (dirType){
//                case 1:
//                    //第一级目录
//                    ArrayList<SipInfo> choiceItems1 = getChoiceItems(result,
//                            choiceOneDirBeen);
//                    if (choiceItems1.size()>0){
//                        if (filterDateList.size()>0){
//                            filterDateList.clear();
//                        }
//                        filterDateList.addAll(choiceItems1);
//                    }
//
//                    break;
//                case 2:
//                    //第二级目录
//                    if (dirSipInfo !=null && dirSipInfo.getType() ==
//                            SipInfo.TYPE_USE_DIRECTORY){
//                        boolean select = dirSipInfo.isSelect();
//                        if (!select){
//                            //未选中
//                            boolean aBoolean = getCurrUiChoiceItemCount(choiceTwoDirBeen, result);
//                            if (aBoolean){
//                                ArrayList<SipInfo> changeChoiceItems =
//                                        getChangeChoiceItems(result, choiceTwoDirBeen);
//                                if (choiceTwoDirBeen.size()>0){
//                                    choiceTwoDirBeen.clear();
//                                }
//                                choiceTwoDirBeen.addAll(changeChoiceItems);
//                            }
//
//                        }
//                    }
//                    ArrayList<SipInfo> choiceItems2 = getChoiceItems(result,
//                            choiceTwoDirBeen);
//                    if (choiceItems2.size()>0){
//                        if (filterDateList.size()>0){
//                            filterDateList.clear();
//                        }
//                        filterDateList.addAll(choiceItems2);
//                    }
//                    break;
//                case 3:
//                    //第三级目录
//                    if (dirSipInfo !=null && dirSipInfo.getType() ==
//                            SipInfo.TYPE_USE_DIRECTORY){
//                        boolean select = dirSipInfo.isSelect();
//                        if (!select){
//                            //选中
//                            boolean aBoolean = getCurrUiChoiceItemCount(choicePeopleBeen, result);
//                            if (aBoolean){
//                                ArrayList<SipInfo> changeChoiceItems =
//                                        getChangeChoiceItems(result, choicePeopleBeen);
//                                if (choicePeopleBeen.size()>0){
//                                    choicePeopleBeen.clear();
//                                }
//                                choicePeopleBeen.addAll(changeChoiceItems);
//                            }
//                        }
//                    }
//                    ArrayList<SipInfo> choiceItems3 = getChoiceItems(result,
//                            choicePeopleBeen);
//                    if (choiceItems3.size()>0){
//                        if (filterDateList.size()>0){
//                            filterDateList.clear();
//                        }
//                        filterDateList.addAll(choiceItems3);
//                    }
//                    break;
//            }
//        }else {
//            switch (dirType){
//                case 1:
//                    //第一级目录
//                    ArrayList<SipInfo> choiceItems1 = getChoiceItems(result,
//                            choiceOneDirBeen);
//                    if (choiceItems1.size()>0){
//                        if (resourceInfos.size()>0){
//                            resourceInfos.clear();
//                        }
//                        resourceInfos.addAll(choiceItems1);
//                    }
//
//                    break;
//                case 2:
//                    //第二级目录
//                    if (dirSipInfo !=null && dirSipInfo.getType() ==
//                            SipInfo.TYPE_USE_DIRECTORY){
//                        boolean select = dirSipInfo.isSelect();
//                        if (!select){
//                            //选中
//                            boolean aBoolean = getCurrUiChoiceItemCount(choiceTwoDirBeen, result);
//                            if (aBoolean){
//                                ArrayList<SipInfo> changeChoiceItems =
//                                        getChangeChoiceItems(result, choiceTwoDirBeen);
//                                if (choiceTwoDirBeen.size()>0){
//                                    choiceTwoDirBeen.clear();
//                                }
//                                choiceTwoDirBeen.addAll(changeChoiceItems);
//                            }
//                        }
//                    }
//                    ArrayList<SipInfo> choiceItems2 = getChoiceItems(result,
//                            choiceTwoDirBeen);
//                    if (choiceItems2.size()>0){
//                        if (resourceInfos.size()>0){
//                            resourceInfos.clear();
//                        }
//                        resourceInfos.addAll(choiceItems2);
//                    }
//                    break;
//                case 3:
//                    //第三级目录
//                    if (dirSipInfo !=null && dirSipInfo.getType() ==
//                            SipInfo.TYPE_USE_DIRECTORY){
//                        boolean select = dirSipInfo.isSelect();
//                        if (!select){
//                            //选中
//                            boolean aBoolean = getCurrUiChoiceItemCount(choicePeopleBeen, result);
//                            if (aBoolean){
//                                ArrayList<SipInfo> changeChoiceItems =
//                                        getChangeChoiceItems(result, choicePeopleBeen);
//                                if (choicePeopleBeen.size()>0){
//                                    choicePeopleBeen.clear();
//                                }
//                                choicePeopleBeen.addAll(changeChoiceItems);
//                            }
//                        }
//                    }
//                    ArrayList<SipInfo> choiceItems3 = getChoiceItems(result,
//                            choicePeopleBeen);
//                    if (choiceItems3.size()>0){
//                        if (resourceInfos.size()>0){
//                            resourceInfos.clear();
//                        }
//                        resourceInfos.addAll(choiceItems3);
//                    }
//                    break;
//            }
//        }
//
//
//    }
//
//
//    /**
//     * 解析服务器返回的数据
//     * @param msg
//     */
//    private void parseNewResource(String msg) {
//        if (isFitter){
//            //是模糊查询的时候
//            if (isDefultStart){
//                initUserInfo(dirSipInfo.getUserid(),"0");
//                isDefultStart=false;
//            }else {
//                initUserInfo(dirSipInfo.getUserid(),"1");
//            }
//
//            ArrayList<SipInfo> result=parseJson(msg);
//            if (result !=null && result.size()>0){
//                if (filterDateList.size()>0){
//                    filterDateList.clear();
//                }
//                showChoiceItems(result);
//                if (mFitterResourAdapter !=null){
//                    mFitterResourAdapter.notifyDataSetChanged();
//                }
//
//
//
//            }else {
//                judgeDirAndLiIsSelet(null);
//                showChoiceItems(filterDateList);
//                mFitterResourAdapter.notifyDataSetChanged();
//            }
//
//        }else {
//            //默认，不是模糊查询的时候
//            if (isDefultStart){
//                initUserInfo(dirSipInfo.getUserid(),"0");
//                isDefultStart=false;
//            }else {
//                initUserInfo(dirSipInfo.getUserid(),"1");
//            }
//            ArrayList<SipInfo> result=parseJson(msg);
//            if (result !=null && result.size()>0){
//                if (resourceInfos.size()>0){
//                    resourceInfos.clear();
//                }
//                showChoiceItems(result);
//                mResAdapter.notifyDataSetChanged();
//                mDirAdapter.notifyDataSetChanged();
//            }else {
//                judgeDirAndLiIsSelet(null);
//                showChoiceItems(resourceInfos);
//                mResAdapter.notifyDataSetChanged();
//                mDirAdapter.notifyDataSetChanged();
//            }
//
//
//
//        }
//
//
//    }
//
//    /**
//     * 判断当前界面是否都选中
//     * @param choiceItems  之前选中得数据
//     * @return
//     */
//    private boolean getCurrUiChoiceItemCount(ArrayList<SipInfo> choiceItems
//            ,ArrayList<SipInfo> result) {
//        ArrayList<SipInfo> data=new ArrayList<>();
//        if (dirSipInfo !=null &&  dirSipInfo.getType() ==
//                SipInfo.TYPE_USE_DIRECTORY){
//            if (choiceItems !=null && choiceItems.size()>0){
//                String userid = dirSipInfo.getUserid();
//                for (int j = 0; j < choiceItems.size(); j++) {
//                    SipInfo info = choiceItems.get(j);
//                    if (userid.equals(info.getBelongsys())){
//                        //相同，代表同一组
//                        data.add(info);
//                        continue;
//                    }
//
//                }
//
//                if (data.size()>0){
//                    if (data.size()==result.size()){
//                        //数量相同
//                        return true;
//                    }else {
//                        return false;
//                    }
//
//                }else {
//                    //不是同一组
//                    return false;
//                }
//
//
//            }else {
//                //此目录下没有选中得，不需要改变result
//                return true;
//
//            }
//        }else {
//
//            return true;
//        }
//
//
//    }
//
//    /**
//     * 改变选中得item，且在界面显示
//     * @param result  现在取消得数据
//     * @param choiceItems  之前选中得数据
//     * @return
//     */
//    private ArrayList<SipInfo> getChangeChoiceItems(ArrayList<SipInfo> result,
//                                                    ArrayList<SipInfo> choiceItems) {
//        ArrayList<SipInfo> toalData=new ArrayList<>();
//        if (toalData.size()>0){
//            toalData.clear();
//        }
//
//        if (result !=null && result.size()>0){
//            if (choiceItems !=null && choiceItems.size()>0){
//                //此此界面取消得数据
//                for (int i = 0; i < result.size(); i++) {
//                    SipInfo sipInfo = result.get(i);
//                    for (int j = 0; j < choiceItems.size(); j++) {
//                        SipInfo info = choiceItems.get(j);
//                        if (sipInfo.getUsername().equals(info.getUsername())
//                                && sipInfo.getUserid().equals(info.getUserid())){
//                            //相同，从选中中删除
//                            choiceItems.remove(j);
//                            continue;
//                        }
//
//                    }
//                }
//
//                toalData.addAll(choiceItems);
//                return toalData;
//
//
//            }else {
//                //此目录下没有选中得，不需要改变result
//                return toalData;
//
//            }
//        }else {
//            if (toalData.size()>0){
//                toalData.clear();
//            }
//            toalData.addAll(choiceItems);
//            return toalData;
//        }
//
//
//    }
//
//    /**
//     * 获取选中得item，且在界面显示
//     * @param result
//     * @return
//     */
//    private ArrayList<SipInfo> getChoiceItems(ArrayList<SipInfo> result,
//                                              ArrayList<SipInfo> choiceItems) {
//        ArrayList<SipInfo> toalData=new ArrayList<>();
//        ArrayList<SipInfo> xtData=new ArrayList<>();
//        if (toalData.size()>0){
//            toalData.clear();
//        }
//        if (xtData.size()>0){
//            xtData.clear();
//        }
//        if (result !=null && result.size()>0){
//            if (choiceItems !=null && choiceItems.size()>0){
//                //此目录下有选中得，需要改变result
//                for (int i = 0; i < choiceItems.size(); i++) {
//                    SipInfo sipInfo = choiceItems.get(i);
//                    for (int j = 0; j < result.size(); j++) {
//                        SipInfo info = result.get(j);
//                        if (sipInfo.getUsername().equals(info.getUsername())
//                                && sipInfo.getUserid().equals(info.getUserid())){
//                            //相同，从result中移除，然后改变选中得状态，添加到临时容器
//                            info.setSelect(true);
//                            xtData.add(info);
//                            result.remove(j);
//                            continue;
//                        }
//
//                    }
//                }
//
//                //将选中得放在前面
//                if (xtData.size()>0){
//                    if (result.size()>0){
//                        //还有剩余得
//                        toalData.addAll(xtData);
//                        toalData.addAll(result);
//                    }else {
//                        //没有剩余得
//                        toalData.addAll(xtData);
//
//                    }
//                }else {
//                    //此界面都没有选中,不需要改变result
//                    toalData.addAll(result);
//                }
//
//            }else {
//                //此目录下没有选中得，不需要改变result
//                toalData.addAll(result);
//
//            }
//        }else {
//            if (toalData.size()>0){
//                toalData.clear();
//            }
//            if (xtData.size()>0){
//                xtData.clear();
//            }
//        }
//
//        return toalData;
//
//
//    }
//
//    /**
//     * 解析数据
//     * @param msg
//     * @return
//     */
//    private ArrayList<SipInfo> parseJson(String msg) {
//        ArrayList<SipInfo> onlineList=new ArrayList<>();
//        ArrayList<SipInfo> offlineList=new ArrayList<>();
//        ArrayList<SipInfo> toalList=new ArrayList<>();
//        if ((MODE_CATCH == MODE_Person && msg.
//                indexOf("MainUsersStatus") >= 0)) {
//            try {
//                JSONObject obj = new JSONObject(msg);
//                JSONObject exObj = obj.getJSONObject("extend");
//                String userId = exObj.getString("userID");
//                if (userId != null && userId.equals(mUserId)) {
//
//                    String bodyStr = obj.getString("body");
//                    JSONObject bodyObj = new JSONObject(bodyStr);
//
//                    JSONObject paramsObj = bodyObj.getJSONObject("params");
//                    JSONArray resourcesArray = paramsObj.getJSONArray("resources");
//                    for (int i = 0; i < resourcesArray.length(); i++) {
//
//                        JSONObject tmpObj = resourcesArray.getJSONObject(i);
//                        String resourceID = tmpObj.getString("resourceID");
//                        String busStatus = tmpObj.getString("busStatus");
//                        String resourceName = tmpObj.getString("resourceName");
//                        String isOnline = tmpObj.getString("isOnline");
//                        String parentId = tmpObj.getString("parentId");
//                        String resourceType = tmpObj.getString("resourceType");
//                        SipInfo info = new SipInfo("", "", 0, resourceID, resourceName, parentId,
//                                SipInfo.TYPE_USE_RESOURCE);
//
//                        info.setBusinesstatus(Integer.valueOf(busStatus));
//                        if (resourceType != null && resourceType.equals("User")) {
//                            info.setType(SipInfo.TYPE_USE_RESOURCE);
//                            if (isDefultStart){
//                                info.setSelect(false);
//                                isDefultStart=false;
//                            }
//                        } else {
//                            if (isDefultStart){
//                                info.setSelect(false);
//                                isDefultStart=false;
//                            }
//                            info.setType(SipInfo.TYPE_DEV_RESOURCE);
//                        }
//                        if (isOnline != null && isOnline.equals("true")) {
//                            judgeDirAndLiIsSelet(info);
//                            info.setStatus(1);
//                            onlineList.add(info);
//                        } else {
//                            judgeDirAndLiIsSelet(info);
//                            info.setStatus(0);
//                            offlineList.add(info);
//                        }
//                    }
//                }
//                if (onlineList.size() > 0) {
//
//                    toalList.addAll(onlineList);
//                }
//
//                if (offlineList.size() > 0) {
//
//                    toalList.addAll(offlineList);
//
//                }
//
//                return toalList;
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return toalList;
//    }
//
//    /**
//     * 判断目录与里面的是否选中   请求网络后展示用的
//     * @param info
//     */
//    private void judgeDirAndLiIsSelet(SipInfo info) {
//        if (dirSipInfo !=null){
//            if (dirSipInfo.getType() ==
//                    SipInfo.TYPE_USE_DIRECTORY){
//                //只是一个目录
//                //如果选中的是目录，则这个目录下面的数据也都被选中
//                boolean select = dirSipInfo.isSelect();
//                if (select){
//                    if (info !=null){
//                        //第三级目录 人员
//                        dirType=3;
//                        info.setSelect(true);
//                        getChoiceDiffernetBean(choicePeopleBeen,info,true,false);
//                    }else {
//                        //第二级目录
//                        dirType=2;
//                        if (isFitter){
//                            if (filterDateList.size()>0){
//                                for (int i = 0; i < filterDateList.size(); i++) {
//                                    filterDateList.get(i).setSelect(true);
//                                    getChoiceDiffernetBean(choiceTwoDirBeen,filterDateList.get(i),true,false);
//                                }
//                            }
//
//                        }else {
//                            if (resourceInfos.size()>0){
//                                for (int i = 0; i < resourceInfos.size(); i++) {
//                                    resourceInfos.get(i).setSelect(true);
//                                    getChoiceDiffernetBean(choiceTwoDirBeen,resourceInfos.get(i),true,false);
//                                }
//                            }
//
//                        }
//
//                    }
//
//                }else {
//                    //点击目录，回退时的监听
//                    if (info !=null){
//                        //第三级目录 人员
//                        dirType=3;
//                    }else {
//                        //第二级目录
//                        dirType=2;
//                    }
//                }
//            }
//        }else {
//            //第一级目录
//            dirType=1;
//        }
//
//
//
//    }
//
//
//    /**
//     * 选中中，消除相同的,对选中得进行收集
//     * @param sipInfo
//     */
//    private ArrayList<SipInfo> getChoiceDiffernetBean(ArrayList<SipInfo> data,
//                                                      SipInfo sipInfo,boolean isAdd,boolean isShow) {
//        if (isShow){
//            return data;
//        }else {
//            if (data.size()>0){
//                for (int i = 0; i < data.size(); i++) {
//                    if (data.get(i).getBelongsys().equals(sipInfo.getBelongsys())
//                            && data.get(i).getUsername().equals(sipInfo.getUsername())){
//                        //相同，移除
//                        data.remove(i);
//                    }
//                }
//                //添加
//                if (isAdd){
//                    data.add(sipInfo);
//                }
//
//            }else {
//                if (isAdd){
//                    //添加
//                    data.add(sipInfo);
//                }
//            }
//        }
//
//
//        return data;
//
//
//    }
//
//
//
//    ////////////////////////////////////////////模糊查询//////////////////////
//
//    /**
//     * 模糊查询数据
//     * @param filterStr
//     */
//    private void filterData(String filterStr) {
//        if (filterDateList.size()>0){
//            filterDateList.clear();
//        }
//        if (resourceInfos!=null &&resourceInfos.size()>0){
//            //如果输入框为空就显示全部
//            if (TextUtils.isEmpty(filterStr)) {
//                filterDateList.addAll(resourceInfos);
//                mQueryTv.setVisibility(View.GONE);
//            } else {
//                //如果不为空就查找
//                filterDateList.clear();
//                for (SipInfo sortModel : resourceInfos) {
//                    String name = sortModel.getUsername();
//                    //汉字是否包含或者拼音第一个字母是否相同
//                    if (name.indexOf(filterStr.toString()) != -1
//                            || characterParser.getSelling(name).startsWith(
//                            filterStr.toString())) {
//                        filterDateList.add(sortModel);
//                        Log.i("android", "filterData: result="+characterParser.getSelling(name));
//                    }
//                }
//
//            }
//            //搜索后有的
//            if (filterDateList!=null && filterDateList.size()>0){
//                //转化为拼音后排序
//                Collections.sort(filterDateList, pinyinComparator);
//                mFitterResourAdapter.notifyDataSetChanged();
//
//            }else {
//                //搜索后没有的
//                mQueryTv.setVisibility(View.VISIBLE);
//            }
//        }else {
//            Toast.makeText(ContantsDemoActivity.this, "数据源为空", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//
////    /**
////     * 初始化模糊查询配置
////     */
////    private void initFitterResourData() {
////
////        mFitterResourAdapter = new ContanctsAdapter(BaseContanctsActivity.this, filterDateList,true);
////        mFitterResourceList.setAdapter(mFitterResourAdapter);
////        mFitterResourceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                if (XTUtils.fastClick()) {
////                    return;
////                }
////                SipInfo info = filterDateList.get(position);
////                if (info.getType() == SipInfo.TYPE_USE_RESOURCE) {
////                    if (info.getStatus() == 0) {
////                        Toast.makeText(BaseContanctsActivity.this, "该用户不在线",
////                                Toast.LENGTH_SHORT).show();
////                    } else {
////                        resSipInfo = info;
////                        MyDialog_Call dialog = new MyDialog_Call(BaseContanctsActivity.this, onCallBack);
////                        dialog.show();
////                    }
////                } else {
////                    isFitter=true;
////                    sendContancts(info);
////                    //directoryInfos.add(info);
////                }
////            }
////        });
////    }
//
//
//    ///////////////////////////////////////选中////////////////////
//    /**
//     * 选择单个
//     *
//     * @param position
//     */
//    private void secetItem(int position,ArrayList<SipInfo> data, boolean a) {
//        ArrayList<SipInfo> list=new ArrayList<>();
//        ArrayList<SipInfo> choiceList=new ArrayList<>();
//        if (data.size() > 0 ) {
//            SipInfo bean = data.get(position);
//            boolean choice = bean.isSelect();
//            if (choice) {
//                //从选择中移除
//                if (dirSipInfo !=null){
//                    if (bean !=null){
//                        if (bean.getType() == SipInfo.TYPE_USE_DIRECTORY){
//                            //第二级目录
//                            bean.setSelect(false);
//                            getChoiceDiffernetBean(choiceTwoDirBeen, bean, false,false);
//
//                        }else {
//                            //第三级目录  人员
//                            bean.setSelect(false);
//                            getChoiceDiffernetBean(choicePeopleBeen, bean, false,false);
//
//                        }
//
//                    }
//                }else {
//                    //第一级目录
//                    bean.setSelect(false);
//                    getChoiceDiffernetBean(choiceOneDirBeen, bean, false,false);
//
//                }
//
//            } else {
//                //从未选择中添加
//                if (dirSipInfo !=null){
//                    if (bean !=null){
//                        if (bean.getType() == SipInfo.TYPE_USE_DIRECTORY){
//                            //第二级目录
//                            bean.setSelect(true);
//                            getChoiceDiffernetBean(choiceTwoDirBeen, bean, true,false);
//                        }else {
//
//                            //第三级目录  人员
//                            bean.setSelect(true);
//                            getChoiceDiffernetBean(choicePeopleBeen, bean, true,false);
//
//                        }
//                    }
//                }else {
//                    //第一级目录
//                    bean.setSelect(true);
//                    getChoiceDiffernetBean(choiceOneDirBeen, bean, true,false);
//
//                }
//
//            }
//
//            data.remove(position);
//            data.add(position, bean);
//            list.addAll(data);
//            //将选择的添加到容器中
//            for (int i = 0; i < data.size(); i++) {
//                SipInfo bean1 = data.get(i);
//                boolean choice1 = bean1.isSelect();
//                if (choice1) {
//                    //已选择的
//                    choiceList.add(bean1);
//                }
//            }
//            if (choiceList.size() == 0) {
//                //没有一个被选中的
//                tag = 0;
//                //mRightTv.setText("全选");
//
//            } else if (choiceList.size() == data.size()) {
//                //全部被选中的
//                tag = 1;
//                // mRightTv.setText("全不选");
//            }
//
//            mSureBtn.setText("邀请 ("+choicePeopleBeen.size()+")");
//
//            if (a){
//                //有模糊查询
//                //刷新界面
//                if (filterDateList.size()>0){
//                    filterDateList.clear();
//                }
//                filterDateList.addAll(list);
//                if (mFitterResourAdapter !=null){
//                    mFitterResourAdapter.notifyDataSetChanged();
//                }
//
//            }else {
//                //默认，没有模糊查询
//                //刷新界面
//                if (resourceInfos.size()>0){
//                    resourceInfos.clear();
//                }
//                resourceInfos.addAll(list);
//                if (mResAdapter !=null){
//                    mResAdapter.notifyDataSetChanged();
//                }
//            }
//
//
//        }
//
//    }
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode){
//            case KeyEvent.KEYCODE_BACK:
//                if (filterDateList.size()>0 ){
//                    filterDateList.clear();
//                }
//
//                if (resourceInfos.size()>0 ){
//                    resourceInfos.clear();
//
//                }
//                if (directoryInfos.size()>0 ){
//                    directoryInfos.clear();
//                }
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
