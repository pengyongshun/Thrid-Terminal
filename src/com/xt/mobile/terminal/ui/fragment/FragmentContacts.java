package com.xt.mobile.terminal.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.activity.ActivityPlaying;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.ResourceListAdapter;
import com.xt.mobile.terminal.adapter.ScrollDirectoryAdapter;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.ui.activity.ContactsDetialActivity;
import com.xt.mobile.terminal.util.CharacterParser;
import com.xt.mobile.terminal.util.ConfigureParse;
import com.xt.mobile.terminal.util.PinyinComparator;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.view.ClearEditText;
import com.xt.mobile.terminal.view.HSlidableListView;
import com.xt.mobile.terminal.view.HSlidableListView.OnFlingListener;
import com.xt.mobile.terminal.view.HorizontalListView;
import com.xt.mobile.terminal.view.dailog.VDialog;

public class FragmentContacts extends Fragment implements OnClickListener {

    public static final int MODE_Person = 0;
    public static final int MODE_Device = 1;
    public static int MODE_CATCH = MODE_Person;
    private View view_Person;
    private View view_Device;

    private HSlidableListView mResourceList;
    private HorizontalListView mDirectoryList;
    private ResourceListAdapter mResAdapter;
    private ScrollDirectoryAdapter mDirAdapter;
    private ArrayList<SipInfo> resourceInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> directoryInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> tmpInfos = new ArrayList<SipInfo>();
    private ArrayList<SipInfo> fitTmpInfos = new ArrayList<SipInfo>();
    private SipInfo dirSipInfo = null;
    private SipInfo resSipInfo = null;
    private SipInfo rootDir = new SipInfo("", "", 0, "root", "根目录", "", SipInfo.TYPE_USE_RESOURCE);
    private boolean mIsModifyDirectory = false;

    private View rootView;
    private TextView tv_Mode_Person;
    private TextView tv_Mode_Device;

    private String mUserId;
    private SharedPreferences mShared;
    private TextView mLeftTv;
    private ImageButton mLeftIv;
    private TextView mTitleTv;
    private TextView mRightTv;
    private ImageButton mRightIv;
    private ClearEditText mFilterEdit;
    private RelativeLayout mToobarRl;
    private TextView mQueryTv;
    private TextView mCloseTv;

    private ArrayList<SipInfo> filterDateList = new ArrayList<SipInfo>();
    private boolean isFitter = false;
    private ResourceListAdapter mFitterResourAdapter;
    private HSlidableListView mFitterResourceList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private boolean isDefultStart = true;
    private LinearLayout mMeunLl;
    private LinearLayout mSearchLl;

    /**
     * 更新数据
     */
    public void update() {

        if (mResAdapter != null) {
            mResAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initControl();
        updateMode(MODE_CATCH);
        iniFiter();
    }

    private void iniFiter() {
        //初始化 CharacterParser

        mFilterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s != null && s.length() > 0) {
                    chaneView(false);
                } else {
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
    }

    private void initUserInfo(String nodesId) {
        if (isFitter) {
            //模糊查询
            filterDateList.clear();
            filterDateList.addAll(ConfigureParse.getNodeUsers(nodesId));
        } else {
            //默认，不模糊查询
            resourceInfos.clear();
            resourceInfos.addAll(ConfigureParse.getNodeUsers(nodesId));
        }

    }

    private void initDeviceInfo(String nodesId) {
        if (isFitter) {
            filterDateList.clear();
            filterDateList.addAll(ConfigureParse.getNodeDevices(nodesId));
        } else {
            resourceInfos.clear();
            resourceInfos.addAll(ConfigureParse.getNodeDevices(nodesId));
        }

    }

    private void initControl() {
        isDefultStart = true;
        initUserInfo("");
        mResAdapter = new ResourceListAdapter(getActivity(), resourceInfos);

        rootView = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_contacts,
                null);
        initTop();

        mResourceList = (HSlidableListView) rootView.findViewById(R.id.lv_contacts);
        mResourceList.setVerticalScrollBarEnabled(false);
        mResourceList.setAdapter(mResAdapter);
        mResourceList.setOnItemClickListener(mOnItemClick);
        mResourceList.setOnFlingListener(mOnFling);
        mResourceList.setEmptyView(mQueryTv);

        tv_Mode_Person = (TextView) rootView.findViewById(R.id.tv_mode_person);
        tv_Mode_Device = (TextView) rootView.findViewById(R.id.tv_mode_device);

        view_Person = (View) rootView.findViewById(R.id.view_Person);
        view_Device = (View) rootView.findViewById(R.id.view_Device);
        view_Person.setBackgroundColor(getResources().getColor(R.color.topbar_background));
        view_Device.setBackgroundColor(getResources().getColor(R.color.white));
        rootView.findViewById(R.id.ll_mode_person).setOnClickListener(this);
        rootView.findViewById(R.id.ll_mode_device).setOnClickListener(this);

        mSearchLl = (LinearLayout) rootView.findViewById(R.id.activity_base_contancts_filter_serach_ll);
        mSearchLl.setOnClickListener(this);
        directoryInfos.clear();
        directoryInfos.add(rootDir);
        mDirAdapter = new ScrollDirectoryAdapter(getActivity(), directoryInfos);
        mDirectoryList = (HorizontalListView) rootView.findViewById(R.id.lv_directory);
        mDirectoryList.setAdapter(mDirAdapter);
        mDirectoryList.setOnItemClickListener(mDirectoryClick);

        mFitterResourAdapter = new ResourceListAdapter(getActivity(), filterDateList);
        mFitterResourceList.setAdapter(mFitterResourAdapter);
        mFitterResourceList.setOnItemClickListener(mOnItemClick);
        mFitterResourceList.setEmptyView(mQueryTv);


        mShared = getActivity().getSharedPreferences(ConstantsValues.DEFAULT_SP, 0);
        mUserId = mShared.getString(ConstantsValues.USERID, null);

        //默认布局
        chaneView(true);
    }

    private void initTop() {
        mLeftTv = (TextView) rootView.findViewById(R.id.left_tv);
        mLeftIv = (ImageButton) rootView.findViewById(R.id.left_iv);
        mTitleTv = (TextView) rootView.findViewById(R.id.title_tv);
        mRightTv = (TextView) rootView.findViewById(R.id.right_tv);
        mRightIv = (ImageButton) rootView.findViewById(R.id.right_iv);
        mMeunLl = (LinearLayout) rootView.findViewById(R.id.fragment_contacts_dh_ll);
        mFitterResourceList = (HSlidableListView) rootView.findViewById(R.id.activity_base_contancts_contacts_fitter_lv);
        mFilterEdit = (ClearEditText) rootView.findViewById(R.id.activity_base_contancts_filter_edit);
        mToobarRl = (RelativeLayout) rootView.findViewById(R.id.activity_base_contancts_toobar_rl);
        mQueryTv = (TextView) rootView.findViewById(R.id.activity_base_contancts_contacts_empty_tv);
        mCloseTv = (TextView) rootView.findViewById(R.id.activity_base_contancts_filter_close_tv);
        mCloseTv.setOnClickListener(this);

        mLeftTv.setVisibility(View.GONE);
        mLeftIv.setVisibility(View.GONE);
        mRightTv.setVisibility(View.GONE);
        mTitleTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("通讯录");


    }


    /**
     * 改变试图
     *
     * @param isDefult
     */
    private void chaneView(boolean isDefult) {
        if (isDefult) {
            //默认 标题可见
            isFitter = false;
            mToobarRl.setVisibility(View.VISIBLE);
            //取消 不出现
            mCloseTv.setVisibility(View.GONE);
            mDirectoryList.setVisibility(View.VISIBLE);
            mResourceList.setVisibility(View.VISIBLE);
            mFitterResourceList.setVisibility(View.GONE);
            if (filterDateList.size() > 0) {
                filterDateList.clear();
            }
            mQueryTv.setVisibility(View.GONE);
            mMeunLl.setVisibility(View.VISIBLE);

        } else {
            //标题不可见
            isFitter = true;
            mToobarRl.setVisibility(View.VISIBLE);
            //mToobarRl.setVisibility(View.GONE);
            //取消 出现
            mCloseTv.setVisibility(View.VISIBLE);
            mDirectoryList.setVisibility(View.GONE);
            mResourceList.setVisibility(View.GONE);
            mFitterResourceList.setVisibility(View.VISIBLE);
            mCloseTv.setVisibility(View.VISIBLE);
            mCloseTv.setText("取消");
            mMeunLl.setVisibility(View.GONE);
            mQueryTv.setVisibility(View.GONE);
        }


    }

    /**
     * WebSocket 发送请求
     *
     * @param info
     */
    private void onClickFunc(SipInfo info) {

        if (dirSipInfo == null || !dirSipInfo.getUserid().equals(info.getUserid())) {
            dirSipInfo = info;
            String dirID = info.getUserid();
            if (info.getType() == SipInfo.TYPE_USE_DIRECTORY && MODE_CATCH == MODE_Person) {
                WebSocketCommand.getInstance().onSendRequestResource(true, dirID);
            } else if (info.getType() == SipInfo.TYPE_DEV_DIRECTORY && MODE_CATCH == MODE_Device) {
                WebSocketCommand.getInstance().onSendRequestResource(false, dirID);
            }
        }
    }

    private OnItemClickListener mOnItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub

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
                if (info.getStatus() == 0) {
                    Toast.makeText(FragmentContacts.this.getActivity(), "该用户不在线",
                            Toast.LENGTH_SHORT).show();
                } else {
                    resSipInfo = info;
                    if (isFitter) {
                        filterDateList.clear();
                        chaneView(true);
                        Intent playIntent = new Intent(getActivity(), ContactsDetialActivity.class);
                        playIntent.putExtra("ResSipInfo", resSipInfo);
                        playIntent.putExtra("CompAndDepart", "");
                        getActivity().startActivityForResult(playIntent,1000);
                    } else {
                        //不是关键字查询到的
                        Intent playIntent = new Intent(getActivity(), ContactsDetialActivity.class);
                        playIntent.putExtra("ResSipInfo", resSipInfo);
                        StringBuilder sbCompAndDep = new StringBuilder();
                        if (directoryInfos != null && directoryInfos.size() >= 1)
                            for (int i = 1; i < directoryInfos.size(); i++)
                                sbCompAndDep.append(directoryInfos.get(i).getUsername()).append("/");
                        playIntent.putExtra("CompAndDepart", sbCompAndDep.substring(0, sbCompAndDep.length() - 1));
                        getActivity().startActivityForResult(playIntent,1000);
                    }

                }
            } else if (info.getType() == SipInfo.TYPE_DEV_RESOURCE) {
                if (info.getStatus() == 0) {
                    Toast.makeText(FragmentContacts.this.getActivity(), "该设备不在线",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chaneView(true);
                    Intent playIntent = new Intent(getActivity(), ActivityPlaying.class);
                    playIntent.putExtra("DeviceSipInfo", info);
                    startActivity(playIntent);
                }
            } else {
                //请求网络
                if (isFitter) {
                    mIsModifyDirectory = false;
                    onClickFunc(info);
                } else {
                    mIsModifyDirectory = true;
                    onClickFunc(info);
                }
            }

        }

    };

    private OnItemClickListener mDirectoryClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
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
                        onClickFunc(info);
                    } else {
                        if (MODE_CATCH == MODE_Person) {
                            initUserInfo("");
                        } else if (MODE_CATCH == MODE_Device) {
                            initDeviceInfo("");
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
                        onClickFunc(info);
                    } else {
                        if (MODE_CATCH == MODE_Person) {
                            initUserInfo("");
                        } else if (MODE_CATCH == MODE_Device) {
                            initDeviceInfo("");
                        }
                        mResAdapter.notifyDataSetChanged();
                    }
                }
            }

        }
    };

    private OnFlingListener mOnFling = new OnFlingListener() {

        @Override
        public void onRightFling() {
            // TODO Auto-generated method stub
            if (MODE_CATCH == MODE_Device) {
                updateMode(MODE_Person);
            } else {
                return;
            }
        }

        @Override
        public void onLeftFling() {
            // TODO Auto-generated method stub
            if (MODE_CATCH == MODE_Person) {
                updateMode(MODE_Device);
            } else {
                return;
            }
        }
    };

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_mode_person) {
			updateMode(MODE_Person);
		}else if (id == R.id.ll_mode_device) {
			updateMode(MODE_Device);
		}else if (id == R.id.activity_base_contancts_filter_close_tv) {
                //取消
                update();
                mFilterEdit.setText("");

		}else if (id == R.id.activity_base_contancts_filter_serach_ll) {
               String userKey = mFilterEdit.getText().toString();
                if (userKey != null && userKey.length() > 0) {
                    if (filterDateList.size() > 0) {
                        filterDateList.clear();
                    }
                    chaneView(false);
                    VDialog.getDialogInstance(getActivity()).showLoadingDialog("正在查找中...", true);
                    if (MODE_CATCH == MODE_Person) {
                        //人员
                        WebSocketCommand.getInstance().onWssQueryPeopleByKey(userKey);
                    } else if (MODE_CATCH == MODE_Device) {
                        //设备
                        WebSocketCommand.getInstance().onWssQueryDeviceByKey(userKey);
                    }

                }

		}
	}

    private void updateMode(int newMode) {
        switch (newMode) {
            case MODE_Person:
                initUserInfo("");
                tv_Mode_Person.setTextColor(getActivity().getResources().
                        getColor(R.color.topbar_background));
                tv_Mode_Device.setTextColor(getActivity().getResources().
                        getColor(R.color.common_text_black));
                view_Person.setBackgroundColor(getResources().getColor(R.color.topbar_background));
                view_Device.setBackgroundColor(getResources().getColor(R.color.white));
                rootDir.setType(SipInfo.TYPE_USE_DIRECTORY);

                mFilterEdit.setHint("请输入人员关键字...");
                break;
            case MODE_Device:
                initDeviceInfo("");
                tv_Mode_Device.setTextColor(getActivity().getResources().
                        getColor(R.color.topbar_background));
                tv_Mode_Person.setTextColor(getActivity().getResources().
                        getColor(R.color.common_text_black));
                view_Device.setBackgroundColor(getResources().getColor(R.color.topbar_background));
                view_Person.setBackgroundColor(getResources().getColor(R.color.white));
                rootDir.setType(SipInfo.TYPE_DEV_DIRECTORY);
                mFilterEdit.setHint("请输入设备关键字...");
                break;
        }
        MODE_CATCH = newMode;
        if (isFitter) {
            mFitterResourAdapter.notifyDataSetChanged();

            directoryInfos.clear();
            directoryInfos.add(rootDir);
            mDirAdapter.notifyDataSetChanged();
        } else {
            mResAdapter.notifyDataSetChanged();

            directoryInfos.clear();
            directoryInfos.add(rootDir);
            mDirAdapter.notifyDataSetChanged();
        }

    }

    /**
     * wss请求在主界面，接收通讯录结果的地方
     *
     * @param msg
     */
    public void requestResourceAnswer(String msg) {

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
                } else {
                    initDeviceInfo(dirSipInfo.getUserid());
                    parseNewResource(msg);
                    if (isFitter) {
                        mFitterResourAdapter.notifyDataSetChanged();
                        mFitterResourceList.setSelection(0);
                    } else {
                        mResAdapter.notifyDataSetChanged();
                        mDirAdapter.notifyDataSetChanged();
                        mDirectoryList.setSelection(0);
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
        VDialog.getDialogInstance(getActivity()).hideLoadingDialog();
        if (isFitter) {
            if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0) {
                fitTmpInfos.clear();
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
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
                }
            }
        } else {
            if (msg.indexOf(WssContant.WSS_INFORM_ADD_RESOURCE) >= 0) {
                tmpInfos.clear();
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
                for (int i = 0; i < tmpSipInfos.size(); i++) {
                    SipInfo info = tmpSipInfos.get(i);
                    if (info.getStatus() == 1) {
                        resourceInfos.add(info);
                    } else {
                        tmpInfos.add(info);
                    }
                }
                if (tmpInfos.size() > 0) {
                    resourceInfos.addAll(tmpInfos);
                }
            } else if (msg.indexOf(WssContant.WSS_INFORM_REFRESH_RESOURCE) >= 0) {
                ArrayList<SipInfo> tmpSipInfos = formatResource(msg);
                for (int i = 0; i < tmpSipInfos.size(); i++) {
                    SipInfo info1 = tmpSipInfos.get(i);
                    if (info1.getBelongsys().equals(directoryInfos.get(directoryInfos.size() - 1).getUserid())) {
                        boolean find = false;
                        for (int j = 0; j < resourceInfos.size(); j++) {
                            SipInfo info2 = resourceInfos.get(j);
                            if (info1 != null && info2 != null && info1.getUserid().equals(info2.getUserid())) {
                                find = true;
                                int status = info1.getStatus();
                                SipInfo info3 = resourceInfos.remove(j);
                                info3.setStatus(status);
                                if (status == 1) {
                                    int cnt = getOnLineNum(resourceInfos);
                                    resourceInfos.add(cnt, info3);
                                } else {
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
            // TODO Auto-generated catch block
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
        // TODO Auto-generated catch block
        return cnt;
    }


}