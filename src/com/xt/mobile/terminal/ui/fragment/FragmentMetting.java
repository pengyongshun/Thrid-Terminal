package com.xt.mobile.terminal.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.MettingListAdapter;
import com.xt.mobile.terminal.bean.MettingListBean;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.network.JsonParseUilt;
import com.xt.mobile.terminal.network.addparmer.WssGreatMeeting;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingList;
import com.xt.mobile.terminal.network.sysim.HttpAsyncTask;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.activity.ActivityMain;
import com.xt.mobile.terminal.ui.activity.BaseContanctsActivity;
import com.xt.mobile.terminal.ui.activity.GroupMeetingActivity;
import com.xt.mobile.terminal.ui.activity.VedioMettingActivity;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.PopupWindowUitl;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.dailog.CustomDialog;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;
import com.xt.mobile.terminal.view.dailog.VDialog;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 原来的 预约会议 变成 分组会议
 * 屏蔽掉加入会议
 */
public class FragmentMetting extends Fragment implements View.OnClickListener{

    private View rootView;
    private TextView title_tv;

    private LinearLayout mMettingAddLl;
    private LinearLayout mMettingKsLl;
    private LinearLayout mMettingYyLl;
    private ListView mListView;
    private TextView mEmptyTv;
    private List<MettingListBean> list=new ArrayList<MettingListBean>();
    private MettingListAdapter mAdapter;
    private CustomDialog joinDialog,ksDialog;
    private PopupWindowUitl popupWindowUitl;


    private TextView mPopAskPeopleTv;
    private TextView mPopDeletMettingTv;
    private TextView mPopCloseTv;
    private MettingListBean curBean;
    private CustomTextDialog textDialog;
    private RequestUitl instans;
    private HttpAsyncTask mHttpAsyncTask;
    private boolean isQuickMeeting=false;
    private CustomTextDialog cyJoinmeetingDialog;
    private JoinMettingBean joinMettingBean;
    private boolean isCYFirst=true;
    private int count=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_metting,
                null);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }


        initView();
        initData();
        return rootView;
    }


    /**
     * 初始化控件
     */
    private void initView() {
        initTop();
        initContent();
        //初始化对话框
        initDaliog();


    }

    /**
     * c初始化对话框
     */
    private void initDaliog() {
        //加入会议
        joinDialog = DailogUitl.initDialog(getActivity(), "加入会议", new DailogUitl.DialogFace() {
            @Override
            public void onClick(DialogInterface dialog, int which, ClearEditText clearEditText) {
                String result = clearEditText.getText().toString();
                ToastUtil.showShort(getActivity(),result);
                clearEditText.setText("");

                //转跳到视频会议
                Intent intent=new Intent(getActivity(),VedioMettingActivity.class);
                intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_ADDHY);
                intent.putExtra("MeetingTitle", result);
                startActivity(intent);

//                Bundle bundle=new Bundle();
//                bundle.putString(Constants.ACTIVTY_TAG, Constants.ACTIVTY_ADDHY);
//                ActivityTools.startActivity(getActivity(),
//                        VedioMettingActivity.class,bundle,false);
            }
        });

        //快速会议
        ksDialog = DailogUitl.initDialog(getActivity(), "会议主题", new DailogUitl.DialogFace() {
            @Override
            public void onClick(DialogInterface dialog, int which, ClearEditText clearEditText) {
                String result = clearEditText.getText().toString();
               // ToastUtil.showShort(getActivity(),result);
                clearEditText.setText("");
                if (result !=null && result.length()>0){
                   Intent intent=new Intent(getActivity(),VedioMettingActivity.class);
                   intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_KSHY);
                    JoinMettingBean bean=new JoinMettingBean();
                    bean.setSceneName(result);
                    bean.setUserName("");
                    bean.setUserID("");
                    List<JoinMettingBean.MembersBean> been=new ArrayList<>();
                    bean.setMembers(been);
                   intent.putExtra("JoinMettingBean",bean);
                   startActivityForResult(intent,1000);
                }else {
                    ToastUtil.showShort(getActivity(),"请先填写会议主题");
                }

            }
        });

        //主席邀请当前成员入会
        cyJoinmeetingDialog = DailogUitl.initTextDialog(getActivity(), "会议提醒", "当前收到一个会议邀请，请确认是否立即入会？","取消", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消
                //进行查询
                onGetMeetingList();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //成员确定入会
                if (joinMettingBean !=null){
                    Intent intent = new Intent(getActivity(), VedioMettingActivity.class);
                    intent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_CY_JOIN);
                    intent.putExtra("JoinMettingBean", joinMettingBean);
                    startActivityForResult(intent,1001);
                }

            }
        });




        //更多的弹出框
        popupWindowUitl = DailogUitl.initMorePopwindow(getActivity(), new PopupWindowUitl.PopupWindowCall() {
            @Override
            public void initView(View view) {
                //初始化控件
                mPopAskPeopleTv = (TextView) view.findViewById(R.id.pop_metting_ask_people_tv);
                mPopDeletMettingTv = (TextView) view.findViewById(R.id.pop_metting_delet_metting_tv);
                mPopCloseTv = (TextView) view.findViewById(R.id.pop_metting_close_tv);
            }

            @Override
            public void initEvent() {
                //邀请人员
                mPopAskPeopleTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindowUitl !=null &&(popupWindowUitl.isShowing())){
                            popupWindowUitl.dissWindow();
                        }
                        if (curBean !=null){
                            Intent intent=new Intent(getActivity(),BaseContanctsActivity.class);
                            intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_YQCY_HYSY);
                            startActivity(intent);
                            //ActivityTools.startActivity(getActivity(), BaseContanctsActivity.class,false);
                            //ToastUtil.showShort(getActivity(),"邀请人员-->curBean="+curBean.getMettingID());
                        }


                    }
                });
                //删除会议
                mPopDeletMettingTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindowUitl !=null &&(popupWindowUitl.isShowing())){
                            popupWindowUitl.dissWindow();
                        }
                        if (textDialog !=null &&!(textDialog.isShowing())){
                            textDialog.show();
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
        //显示对话框
        textDialog = DailogUitl.initTextDialog(getActivity(), "删除会议", "确认删除当前会议吗？","删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认删除会议
                if (curBean !=null){
                    ToastUtil.showShort(getActivity(),"删除会议-->curBean="+curBean.getMettingID());
                    if (list.size()>0 && list.contains(curBean)){
                        list.remove(curBean);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }


    private void initContent() {
        mMettingAddLl = (LinearLayout) rootView.findViewById(R.id.fragment_metting_add_ll);
        mMettingKsLl = (LinearLayout) rootView.findViewById(R.id.fragment_metting_ks_ll);
        mMettingYyLl = (LinearLayout) rootView.findViewById(R.id.fragment_metting_yy_ll);
        mListView = (ListView) rootView.findViewById(R.id.fragment_metting_list_lv);
        mEmptyTv = (TextView) rootView.findViewById(R.id.fragment_metting_empty_tv);

        mMettingAddLl.setOnClickListener(this);
        mMettingKsLl.setOnClickListener(this);
        mMettingYyLl.setOnClickListener(this);

        mAdapter =new MettingListAdapter(getActivity(),list,false);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyTv);

        mAdapter.setOnClickListener(new MettingListAdapter.OnClickCallBack() {
            @Override
            public void moreClick(MettingListBean bean) {
                //更多
                curBean=bean;
                if (popupWindowUitl !=null && !(popupWindowUitl.isShowing())){
                    popupWindowUitl.showWindow(ActivityMain.layoutMessge, Gravity.BOTTOM,0,0);
                }
            }

            @Override
            public void joinMettingClick(MettingListBean bean) {
               //进入会议
                //成员进入会议
                curBean=bean;
                Intent intent = new Intent(getActivity(), VedioMettingActivity.class);
                intent.putExtra(Constants.ACTIVTY_TAG, Constants.ACTIVTY_JIONHY);
                intent.putExtra("MettingListBean", curBean);
                startActivityForResult(intent,1001);

            }
        });

    }


    private void initTop() {
        title_tv = (TextView) rootView.findViewById(R.id.title_tv);
        title_tv.setText(R.string.metting);
    }

    /**
     * TODO 初始化数据
     */
    private void initData() {
        onGetMeetingList();

    }

    public void update() {

        Toast.makeText(getActivity(), "会议界面", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_metting_add_ll) {
            //已屏蔽掉
            //加入会议
            if (joinDialog !=null && !(joinDialog.isShowing())){
                joinDialog.show();
            }
            isQuickMeeting=false;
        }else if (v.getId() == R.id.fragment_metting_ks_ll) {
            //快速会议
            if (ksDialog !=null && !(ksDialog.isShowing())){
                ksDialog.show();
            }

            isQuickMeeting=true;
        }else if (v.getId() == R.id.fragment_metting_yy_ll) {
            //预约会议
//                Intent intent=new Intent(getActivity(),YYMettingActivity.class);
//                startActivityForResult(intent,1000);
            isQuickMeeting=false;
            //分组会议
            Intent intent=new Intent(getActivity(),GroupMeetingActivity.class);
            startActivityForResult(intent,10010);
        }
    }
    //预约会议会用到
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case 1000:
//                if (resultCode==1001){
//                    MettingListBean bean = (MettingListBean) data.
//                            getBundleExtra("data").
//                            getSerializable("mettingListBean");
//                    if (bean !=null){
//                        list.add(bean);
//                        mAdapter.notifyDataSetChanged();
//                        ToastUtil.showShort(getActivity(),"bean="+bean.getMettingID());
//                    }
//
//                }
//                break;
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1001:
                if (requestCode==1001){
                    //禁止对话框cyJoinmeetingDialog弹出
                    if (cyJoinmeetingDialog !=null && cyJoinmeetingDialog.isShowing()){
                        cyJoinmeetingDialog.dismiss();
                    }
                    //主席邀请成员 成员退出会议
                    isQuickMeeting=false;
                    if (list.size()>0){
                        list.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    //查询
                    onGetMeetingList();
                }else if (requestCode==1000){
                    //禁止对话框cyJoinmeetingDialog弹出
                    if (cyJoinmeetingDialog !=null && cyJoinmeetingDialog.isShowing()){
                        cyJoinmeetingDialog.dismiss();
                    }
                    //主席自己退出会议
                    isQuickMeeting=false;
                    if (list.size()>0){
                        list.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    //查询
                    onGetMeetingList();
                }
                break;
            case 1002:
                //从分组会议返回的
                if (requestCode==10010){
                    //禁止对话框cyJoinmeetingDialog弹出
                    if (cyJoinmeetingDialog !=null && cyJoinmeetingDialog.isShowing()){
                        cyJoinmeetingDialog.dismiss();
                    }
                    //刷新会议列表界面
                    if (list.size()>0){
                        list.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    onGetMeetingList();
                }
                break;

        }
    }


    ///////////////////////////////////////wss请求///////////////////////
//    private void joinMetting(String mettingTitle) {
//        String sceneName=mettingTitle;
//        String description="";
//        boolean isMediaProcessing=false;
//        String operatorName=UserMessge.getInstans(getActivity()).getUserName();
//        boolean needPassword=false;
//        String password="" ;
//        String members="";
//        WebSocketCommand.getInstance().onWssCreateMeeting(sceneName,description,
//                isMediaProcessing,operatorName,needPassword,password,members);
//    }


//    /**
//     * wss请求在主界面，接收通讯录结果的地方
//     * @param msg
//     */
//    public void requestResourceAnswer(String msg,int resquet) {
//        if (msg.isEmpty()) {
//            return;
//        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {
//                 switch (resquet){
//                     case 1000:
//                         joinMettingBean = JsonParseUilt.getMettingListBeen(msg);
//                         String json = FastJsonTools.bean2Json(joinMettingBean);
//                        SharedPreferences sp = getActivity().getSharedPreferences(ConstantsValues.DEFAULT_SP,
//                                getActivity().MODE_PRIVATE);
//                         String resultmsg1 = sp.getString("resultmsg", "");
//                         if (resultmsg1.equals(json)){
//                             //相同 过滤
//                         }else {
//                             //不相同，添加
//                             SharedPreferences.Editor edit = sp.edit();
//                             edit.putString("resultmsg",json);
//                             edit.commit();
//
//                             if (isQuickMeeting){
//                                 //主席，自己入会
//                                 if (msg.indexOf("sceneID") >= 0){
//                                     //代表是创建会议后直接加入会议
//                                     Log.i("", "onReceiveWssMessage: ---requestResourceAnswer-----msg----->"+msg);
//                                     joinMettingBean = JsonParseUilt.getMettingListBeen(msg);
//                                     if (joinMettingBean != null){
//                                         Intent intent=new Intent(getActivity(),VedioMettingActivity.class);
//                                         intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_KSHY);
//                                         intent.putExtra("JoinMettingBean",joinMettingBean);
//                                         startActivityForResult(intent,1000);
//                                     }else {
//                                         ToastUtil.showShort(getActivity(),"暂无数据");
//                                     }
//
//                                 }
//                             }else {
//                                 //成员
//                                 //主席邀请当前成员入会
//                                 if (msg.indexOf("sceneID") >= 0){
//                                     //代表是创建会议后直接加入会议
//                                     joinMettingBean = JsonParseUilt.getMettingListBeen(msg);
//                                     if (joinMettingBean != null){
//                                         if (cyJoinmeetingDialog !=null && !(cyJoinmeetingDialog.isShowing())){
//                                             cyJoinmeetingDialog.show();
//                                         }
//
//                                     }else {
//                                         ToastUtil.showShort(getActivity(),"暂无数据");
//                                     }
//
//                                 }
//
//                             }
//
//                         }
//
//
//
//                         break;
//                 }
//
//
//        }
//    }


    /**
     * wss请求在主界面，接收通讯录结果的地方
     * @param msg
     */
    public void requestResourceAnswer(String msg,int resquet) {
        if (msg.isEmpty()) {
            return;
        }else if (msg.indexOf(WssContant.WSS_JOIN_METTING) >= 0) {
            switch (resquet){
                case 1000:
                    joinMettingBean = JsonParseUilt.getMettingListBeen(msg);
                    String json = FastJsonTools.bean2Json(joinMettingBean);
                    SharedPreferences sp = getActivity().getSharedPreferences(ConstantsValues.DEFAULT_SP,
                            getActivity().MODE_PRIVATE);
                    String resultmsg1 = sp.getString("resultmsg", "");
                    if (resultmsg1.equals(json)){
                        //相同 过滤
                    }else {
                        //不相同，添加
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("resultmsg",json);
                        edit.commit();

                        if (isQuickMeeting){
                            //主席，自己入会
                        }else {
                            //成员
                            //主席邀请当前成员入会
                            if (msg.indexOf("sceneID") >= 0){
                                //代表是创建会议后直接加入会议
                                joinMettingBean = JsonParseUilt.getMettingListBeen(msg);
                                if (joinMettingBean != null){
                                    if (cyJoinmeetingDialog !=null && !(cyJoinmeetingDialog.isShowing())){
                                        cyJoinmeetingDialog.show();
                                    }

                                }else {
                                    ToastUtil.showShort(getActivity(),"暂无数据");
                                }

                            }

                        }

                    }



                    break;
            }


        }
    }

////////////////////////////////////////////////http请求/////////////////
    /**
     * 请求会议列表
     */
    public void onGetMeetingList() {
        if (list.size()>0){
            list.clear();
        }
        //参数
        String tokenkey = UserMessge.getInstans(getActivity()).getUserTokenkey();
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
//        paramsList.add(new BasicNameValuePair("tokenKey", tokenkey));
        paramsList.add(new BasicNameValuePair("operatorToken", tokenkey));
        //对参数进行格式化
        String params = URLEncodedUtils.format(paramsList, HTTP.UTF_8);
        //url
        String url= ConstantsValues.getHttpUrl(getActivity(), NetUrl.
                HTTP_GetMeetingList);
        String path = url+"?" + params;
        VDialog.getDialogInstance(getActivity()).showLoadingDialog("正在加载中...",true);
        MeetingListCallBack jsonCallback = new MeetingListCallBack();
        mHttpAsyncTask = new HttpAsyncTask(getActivity(), path, null, jsonCallback, true, true);
        mHttpAsyncTask.execute("");

    }
    private class MeetingListCallBack implements HttpAsyncTask.HttpCallBack{

        @Override
        public void setResult(String result) {
            //销毁异步任务
            if (mHttpAsyncTask != null && !mHttpAsyncTask.isCancelled()) {
                mHttpAsyncTask.cancel(true);
                mHttpAsyncTask = null;
            }
            VDialog.getDialogInstance(getActivity()).hideLoadingDialog();
            if (result.contains("success")&& result.contains("\"responseCode\":\"1\"")) {
                //成功
                //对结果进行解析
                ParseMeetingList parseMeetingList = FastJsonTools.json2BeanObject(result,
                        ParseMeetingList.class);
                setData(parseMeetingList);
            } else {
                //异常
               // ToastUtil.showShort(getActivity(),"服务器异常");
            }
        }
    }

    /**
     * 填充会议列表的数据
     * @param parseMeetingList
     * @return
     */
    private void setData(ParseMeetingList parseMeetingList) {
        List<MettingListBean> been=new ArrayList<>();
        if (parseMeetingList !=null){
            ParseMeetingList.DataBean data = parseMeetingList.getData();
            if (data !=null){
                List<ParseMeetingList.DataBean.ListBean> listBeen = data.getList();
                if (listBeen !=null && listBeen.size()>0){
                    for (int i = 0; i < listBeen.size(); i++) {
                        MettingListBean bean=new MettingListBean();
                        bean.setActived(false);
                        bean.setMettingID(listBeen.get(i).getSceneID());
                        bean.setMettingTitle(listBeen.get(i).getSceneName());
                        bean.setKssj(listBeen.get(i).getBeginTime());
                        been.add(bean);
                    }
                    if (list.size()>0){
                        list.clear();
                    }
                    list.addAll(been);
                    mAdapter.notifyDataSetChanged();

                }
            }
        }

    }


}
