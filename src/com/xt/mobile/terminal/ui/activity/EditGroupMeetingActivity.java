package com.xt.mobile.terminal.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.JoinMettingPeopleAdapter;
import com.xt.mobile.terminal.bean.GroupMeetingListBean;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.network.addparmer.GroupmettingAdd;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.addparmer.GroupMeetingEdit;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseGroupMeetingDetail;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.ToolKeyBoard;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditGroupMeetingActivity extends BaseActivity implements RequestUitl.HttpResultCall {

	private ImageButton left_iv;
	private TextView title_tv;
	private TextView right_tv;
	private String activityTag;
	private boolean isAdd=false;
	private GroupMeetingListBean editGroupMeetingBean;

	private EditText mMeetingNameEt;
	private CheckBox mPasswordMeetingCb;
	private EditText mPasswordMeetingEt;
	private RadioGroup mMeetingTypeRgp;
	private RadioButton mTypeRgpNomRb;
	private RadioButton mTypeRgpPjRb;
	private EditText mContentEt;
	private TextView mAddPeopleTv;
	private ListView mPeopleLv;
	private TextView mPeopleEmptyTv;
	private List<JoinMettingPeopleBean> peopleBeen=new ArrayList<>();
	private JoinMettingPeopleAdapter mAdapter;
	private CustomTextDialog textDialog;
	private JoinMettingPeopleBean curBean;
	private boolean isLook=false;
	private RequestUitl instans;
	private UserMessge userMessge;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_group_metting);
		instans = RequestUitl.getInstans(this,this);
		userMessge = UserMessge.getInstans(this);
		activityTag = getIntent().getStringExtra(Constants.ACTIVTY_TAG);
		if (activityTag.equals(Constants.ACTIVTY_GROUP_POP_EDIT)){
			//编辑
			//需要填充数据
			isAdd=false;
			isLook=false;
			editGroupMeetingBean = (GroupMeetingListBean) getIntent().getSerializableExtra("GroupMeetingListBean");
		}else if (activityTag.equals(Constants.ACTIVTY_GROUP_LIST_ADD)){
			//新增
			isAdd=true;
			isLook=false;
			JoinMettingPeopleBean bean=new JoinMettingPeopleBean();
			bean.setMeetingID("");
			bean.setName(userMessge.getUserName());
			bean.setUseId(userMessge.getUserID());
			bean.setRole("chairman");
			bean.setChoice(true);
			bean.setOnline(true);
			if (peopleBeen.size()>0){
				peopleBeen.clear();
			}
			peopleBeen.add(bean);
		}else if (activityTag.equals(Constants.ACTIVTY_GROUP_DETIAL_LOOK)){
			//查看
			isLook=true;
			editGroupMeetingBean = (GroupMeetingListBean) getIntent().getSerializableExtra("GroupMeetingListBean");

		}
		initView();
		initData();
	}



	private void initTop() {
		left_iv = (ImageButton) findViewById(R.id.left_iv);
		left_iv.setVisibility(View.VISIBLE);
		left_iv.setBackgroundResource(R.drawable.login_params_back);
		left_iv.setOnClickListener(this);
		title_tv = (TextView) findViewById(R.id.title_tv);
		left_iv.setVisibility(View.VISIBLE);

		right_tv = (TextView) findViewById(R.id.right_tv);
		right_tv.setText(R.string.save);
		right_tv.setOnClickListener(this);

	}

	private void initView() {
		initTop();
		iniContentView();
		iniDaliog();
		
	}

	private void iniDaliog() {
		//显示对话框
		textDialog = DailogUitl.initTextDialog(EditGroupMeetingActivity.this, "删除成员", "确认删除当前成员吗？","删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//确认删除成员
				if (curBean !=null){
					if (peopleBeen !=null && peopleBeen.size()>0){
						if (peopleBeen.contains(curBean)){
							peopleBeen.remove(curBean);
							mAdapter.notifyDataSetChanged();
						}
					}
				}

			}
		});
	}

	private void iniContentView() {
		mMeetingNameEt = (EditText) findViewById(R.id.activity_group_detail_edit_meeting_name_et);
		mPasswordMeetingCb = (CheckBox) findViewById(R.id.activity_group_detail_edit_password_meeting_cb);
		mPasswordMeetingEt = (EditText) findViewById(R.id.activity_group_detail_edit_password_meeting_et);
		mMeetingTypeRgp = (RadioGroup) findViewById(R.id.activity_group_detail_edit_meeting_type_rgp);
		mTypeRgpNomRb = (RadioButton) findViewById(R.id.activity_group_detail_edit_type_rgp_nom_rb);
		mTypeRgpPjRb = (RadioButton) findViewById(R.id.activity_group_detail_edit_type_rgp_pj_rb);
		mContentEt = (EditText) findViewById(R.id.activity_group_detail_edit_content_et);
		mAddPeopleTv = (TextView) findViewById(R.id.activity_group_detail_edit_add_people_tv);
		mPeopleLv = (ListView) findViewById(R.id.activity_group_detail_edit_people_lv);
		mPeopleEmptyTv = (TextView) findViewById(R.id.activity_group_detail_edit_people_empty_tv);

		mAddPeopleTv.setOnClickListener(this);


        if (isLook){
			mAdapter=new JoinMettingPeopleAdapter(this,peopleBeen,false,false,false);
		}else {
			mAdapter=new JoinMettingPeopleAdapter(this,peopleBeen,false,true,false);
		}
		mPeopleLv.setAdapter(mAdapter);
		mPeopleLv.setEmptyView(mPeopleEmptyTv);
		mAdapter.setDeletListener(new JoinMettingPeopleAdapter.DeletPeopleCallBack() {
			@Override
			public void deletPeople(int postion) {

				if (textDialog !=null &&!(textDialog.isShowing())){
					textDialog.show();
				}
				//删除成员
				if (peopleBeen.size()>0){
					curBean = peopleBeen.get(postion);
				}
			}
		});



		mPasswordMeetingCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean checked = mPasswordMeetingCb.isChecked();
				if (checked){
					mPasswordMeetingEt.setEnabled(true);
				}else {
					mPasswordMeetingEt.setEnabled(false);
					mPasswordMeetingEt.setText("");
				}
			}
		});


		if (isLook){
			//不可编辑
			mContentEt.setEnabled(false);
			mMeetingNameEt.setEnabled(false);
			mTypeRgpPjRb.setEnabled(false);
			mTypeRgpNomRb.setEnabled(false);
			mMeetingTypeRgp.setEnabled(false);
			mPasswordMeetingEt.setEnabled(false);
			mAddPeopleTv.setVisibility(View.GONE);

			right_tv.setVisibility(View.GONE);
			title_tv.setText(R.string.metting_group_meeting_xq_title_look);
		}else {
			right_tv.setVisibility(View.VISIBLE);
			if (isAdd){
				title_tv.setText(R.string.metting_group_meeting_xq_title_add);
			}else {
				title_tv.setText(R.string.metting_group_meeting_xq_title_edit);
			}
		}



	}

	private void initData() {
		if (isLook){
			//查看
			ToolKeyBoard.onHideKeyBoard(mContentEt);
			//addContentData();
			//请求网络
			sendDetialGroupMeeting();
		}else {
			if (isAdd){
				//默认情况
				//常规
				mTypeRgpNomRb.setChecked(true);
				mTypeRgpPjRb.setChecked(false);
				//不需要密码
				mPasswordMeetingCb.setChecked(false);
				mPasswordMeetingEt.setEnabled(false);
				editGroupMeetingBean=new GroupMeetingListBean();
			}else {
				//编辑
//				// 获取之前保存的值
//				addContentData();
                //请求网络
				sendDetialGroupMeeting();
			}
		}

	}

	/**
	 * 添加成员以外的数据
	 */
	private void addContentData() {
		//成员列表
		if (peopleBeen.size()>0){
			peopleBeen.clear();
		}
        if (editGroupMeetingBean !=null){
			List<JoinMettingPeopleBean> groupPeople = editGroupMeetingBean.getGroupPeople();
			if (groupPeople !=null && groupPeople.size()>0){
				peopleBeen.addAll(groupPeople);
				mAdapter.notifyDataSetChanged();
			}

			mMeetingNameEt.setText(editGroupMeetingBean.getGroupTitle());
			mContentEt.setText(editGroupMeetingBean.getGroupContent());
			boolean meetingType = editGroupMeetingBean.isMeetingType();
			if (meetingType){
				//常规
				mTypeRgpNomRb.setChecked(true);
				mTypeRgpPjRb.setChecked(false);
			}else {
				mTypeRgpNomRb.setChecked(false);
				mTypeRgpPjRb.setChecked(true);
			}

			boolean password = editGroupMeetingBean.isNeedPassword();
			if (password){
				//需要密码
				mPasswordMeetingEt.setEnabled(true);
				mPasswordMeetingEt.setText(editGroupMeetingBean.getPassword());
				mPasswordMeetingCb.setChecked(true);
			}else {
				//不需要密码
				mPasswordMeetingEt.setEnabled(false);
				mPasswordMeetingCb.setChecked(false);

			}

		}

	}


	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.left_iv) {
			finish();
		}else if (id == R.id.right_tv){
			//保存
			saveData();
		}else if (id == R.id.activity_group_detail_edit_add_people_tv){
			//添加成员
			GroupMeetingListBean groupMeetingListBean=new GroupMeetingListBean();
			String meetingName = mMeetingNameEt.getText().toString();
			boolean checked = mPasswordMeetingCb.isChecked();
			String password = mPasswordMeetingEt.getText().toString();
			boolean checked1 = mTypeRgpNomRb.isChecked();
			String content = mContentEt.getText().toString();
			groupMeetingListBean.setGroupPeople(peopleBeen);
			groupMeetingListBean.setPassword(password);
			groupMeetingListBean.setNeedPassword(checked);
			groupMeetingListBean.setMeetingType(checked1);
			groupMeetingListBean.setGroupContent(content);
			groupMeetingListBean.setGroupTitle(meetingName);
			groupMeetingListBean.setGroupPeople(peopleBeen);
			Intent intent=new Intent(EditGroupMeetingActivity.this,BaseContanctsActivity.class);
			intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_GROUP_DETIAL_ADD);
			intent.putExtra("data",FastJsonTools.bean2Json(groupMeetingListBean));
			startActivityForResult(intent,1001);

		}

	}

	/**
	 * 保存数据
	 */
	private void saveData() {
		String meetingName = mMeetingNameEt.getText().toString();
		boolean checked = mPasswordMeetingCb.isChecked();
		String password = mPasswordMeetingEt.getText().toString();
		boolean checked1 = mTypeRgpNomRb.isChecked();
		String content = mContentEt.getText().toString();
		editGroupMeetingBean.setGroupPeople(peopleBeen);
		editGroupMeetingBean.setPassword(password);
		editGroupMeetingBean.setNeedPassword(checked);
		editGroupMeetingBean.setMeetingType(checked1);
		editGroupMeetingBean.setGroupContent(content);
		editGroupMeetingBean.setGroupTitle(meetingName);
		if (isAdd){
			//新增
			editGroupMeetingBean.setGroupID("");
			//editGroupMeetingBean.setGroupID(MettingUilt.creatMettingID());
		}

		if (meetingName !=null && meetingName.length()>0){
			if (content.length()>100){
				ToastUtil.showShort(EditGroupMeetingActivity.this,"请将输入的内容限制在100字以下");
			}else {
				if (content.length()>0){
					//请求网络，添加分组会议
					if (isAdd){
						//新增
						GroupmettingAdd bean=new GroupmettingAdd();
						bean.setDescription(editGroupMeetingBean.getGroupContent());
						bean.setGroupName(editGroupMeetingBean.getGroupTitle());
						bean.setIsDefaultScheme("false");
						boolean meetingType = editGroupMeetingBean.isMeetingType();
						if (meetingType){
							bean.setIsMediaProcessing("false");
						}else {
							bean.setIsMediaProcessing("true");

						}
						List<GroupmettingAdd.MeetingDevice> meetingDevices=new ArrayList<>();
						bean.setMeetingDevices(meetingDevices);
						bean.setNeedPassword(editGroupMeetingBean.isNeedPassword()+"");
						bean.setPassword(editGroupMeetingBean.getPassword());
						bean.setSchemeID("");
						List<JoinMettingPeopleBean> groupPeople = editGroupMeetingBean.getGroupPeople();
						List<GroupmettingAdd.MeetingUser> meetingUsers=new ArrayList<>();
						if (groupPeople !=null && groupPeople.size()>1){
							for (int i = 0; i < groupPeople.size(); i++) {
								GroupmettingAdd.MeetingUser meetingUser = new GroupmettingAdd.MeetingUser();
								meetingUser.setIndex(i+1);
								meetingUser.setIsSpectator("false");
								meetingUser.setUserID(groupPeople.get(i).getUseId());
								meetingUsers.add(meetingUser);
							}
							bean.setMeetingUsers(meetingUsers);
							//请求网络
							onAddGroupMeeting(bean);
						}else {
							ToastUtil.showShort(EditGroupMeetingActivity.this,"请添加成员");
						}

					}else {
						//编辑
						GroupMeetingEdit bean=new GroupMeetingEdit();
						bean.setDescription(editGroupMeetingBean.getGroupContent());
						bean.setGroupName(editGroupMeetingBean.getGroupTitle());
						bean.setDefaultScheme(false);
						boolean meetingType = editGroupMeetingBean.isMeetingType();
						if (meetingType){
							bean.setMediaProcessing(false);
						}else {
							bean.setMediaProcessing(true);

						}
						List<GroupMeetingEdit.MeetingDevice> meetingDevices=new ArrayList<>();
						bean.setMeetingDevices(meetingDevices);
						bean.setNeedPassword(editGroupMeetingBean.isNeedPassword());
						bean.setPassword(editGroupMeetingBean.getPassword());
						bean.setSchemeID("");
						bean.setGroupID(editGroupMeetingBean.getGroupID());
						List<JoinMettingPeopleBean> groupPeople = editGroupMeetingBean.getGroupPeople();
						List<GroupMeetingEdit.MeetingUser> meetingUsers=new ArrayList<>();
						if (groupPeople !=null && groupPeople.size()>1){
							for (int i = 0; i < groupPeople.size(); i++) {
								GroupMeetingEdit.MeetingUser meetingUser = new GroupMeetingEdit.MeetingUser();
								meetingUser.setIndex(i);
								meetingUser.setIsSpectator("false");
								meetingUser.setUserID(groupPeople.get(i).getUseId());
								meetingUsers.add(meetingUser);
							}
							bean.setMeetingUsers(meetingUsers);
							//请求网络
							onEditGroupMeeting(bean);
						}else {
							ToastUtil.showShort(EditGroupMeetingActivity.this,"请添加成员");
						}

					}
				}else {
					ToastUtil.showShort(EditGroupMeetingActivity.this,"会议简介不能为空");
				}


			}


		}else {
			ToastUtil.showShort(EditGroupMeetingActivity.this,"会议名称不能为空");
		}




	}


	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1001:
                if (resultCode==1001){
					String json = data.getStringExtra("data");
					if (json !=null && json.length()>0){
						GroupMeetingListBean bean = FastJsonTools.json2BeanObject(json,
								GroupMeetingListBean.class);
						if (bean !=null){
							List<JoinMettingPeopleBean> groupPeople = bean.getGroupPeople();
							if (groupPeople !=null && groupPeople.size()>0){
								if (peopleBeen !=null && peopleBeen.size()>0){
									peopleBeen.clear();
								}
								peopleBeen.addAll(groupPeople);
								mAdapter.notifyDataSetChanged();
							}

						}
					}
				}
                break;
        }
    }



	///////////////////////////////网络请求/////////////////////////
	/**
	 * 添加分组会议
	 */
	public void onAddGroupMeeting(GroupmettingAdd bean) {
		String userTokenkey = UserMessge.getInstans(EditGroupMeetingActivity.this).
				getUserTokenkey();
		String obj = FastJsonTools.bean2Json(bean);
		Map<String,String> map=new HashMap<>();
		map.put("tokenKey",userTokenkey);
		map.put("obj",obj);
		List<NameValuePair> params = MoudleParams.getAddGroupMeetingParams(this,map );
		// 请求
		showLoadingDaliog("正在添加中...");
		if (instans !=null){
			instans.sendRequest(params,false,Constants.HTTP_Post_AddMeetingGroup);
		}

	}

	/**
	 * 编辑分组会议
	 */
	public void onEditGroupMeeting(GroupMeetingEdit bean) {
		String userTokenkey = UserMessge.getInstans(EditGroupMeetingActivity.this).
				getUserTokenkey();
		String obj = FastJsonTools.bean2Json(bean);
		Map<String,String> map=new HashMap<>();
		map.put("tokenKey",userTokenkey);
		map.put("obj",obj);
		List<NameValuePair> params = MoudleParams.getEditGroupMeetingParams(this,map );
		// 请求
		showLoadingDaliog("正在保存中...");
		if (instans !=null){
			instans.sendRequest(params,false,Constants.HTTP_Post_EditMeetingGroup);
		}

	}

	/**
	 * 分组会议详情
	 */
	private void sendDetialGroupMeeting() {

		if (editGroupMeetingBean !=null){
			String userTokenkey = UserMessge.getInstans(EditGroupMeetingActivity.this).
					getUserTokenkey();
			String groupID = editGroupMeetingBean.getGroupID();
			Map<String,String> map=new HashMap<>();
			map.put("tokenKey",userTokenkey);
			map.put("groupID",groupID);
			List<NameValuePair> params = MoudleParams.getDetialGroupMeetingParams(EditGroupMeetingActivity.this,map);
			// 请求
			showLoadingDaliog("正在查询中...");
			if (instans !=null){
				instans.sendRequest(params,true,Constants.HTTP_Get_MeetingGroupDetail);
			}
		}
	}


	@Override
	public void success(int tag, String result) {
		hideLoadingDialog();
		switch (tag){
			case Constants.HTTP_Post_AddMeetingGroup:
				//添加分组会议
				ToastUtil.showShort(EditGroupMeetingActivity.this,"添加成功");
				//目前模拟
				Intent intent=new Intent(EditGroupMeetingActivity.this,GroupMeetingActivity.class);
				setResult(1001,intent);
				finish();

				break;
			case Constants.HTTP_Get_MeetingGroupDetail:
				//分组详情
				if (result!=null){
					ParseGroupMeetingDetail beanObject = FastJsonTools.json2BeanObject(result,
							ParseGroupMeetingDetail.class);
					if (beanObject !=null){
						ParseGroupMeetingDetail.DataBean data = beanObject.getData();
						editGroupMeetingBean.setGroupID(data.getGroupID());
						editGroupMeetingBean.setMeetingType(true);
						editGroupMeetingBean.setNeedPassword(false);
						editGroupMeetingBean.setGroupTitle(data.getGroupName());
						List<JoinMettingPeopleBean> dataList =new ArrayList<>();
						List<ParseGroupMeetingDetail.DataBean.MeetingUsersBean> meetingUsers = data.getMeetingUsers();
						if (meetingUsers !=null && meetingUsers.size()>0){
							for (int i = 0; i < meetingUsers.size(); i++) {
								JoinMettingPeopleBean a =new JoinMettingPeopleBean();
								a.setMeetingID(data.getGroupID());
								a.setUseId(meetingUsers.get(i).getUserID());
								a.setName(meetingUsers.get(i).getUserName());
								a.setChoice(true);
								a.setOnline(true);
								dataList.add(a);
							}
						}

						editGroupMeetingBean.setGroupPeople(dataList);
						editGroupMeetingBean.setPassword("");

						//刷新界面
						addContentData();
					}
				}
				//ToastUtil.showShort(EditGroupMeetingActivity.this,result);
				break;
			case Constants.HTTP_Post_EditMeetingGroup:
				//编辑分组会议
				Log.i("android", "---------edit-----success---------->: result"+result);
				Intent intent1=new Intent(EditGroupMeetingActivity.this,GroupMeetingActivity.class);
				setResult(1001,intent1);
				finish();
				break;
		}
	}

	@Override
	public void faile(int tag, String error) {
		hideLoadingDialog();
		switch (tag){
			case Constants.HTTP_Post_AddMeetingGroup:
				//添加分组会议
				//{"responseCode":"100","responseDesc":"内部异常","data":"错误详细信息：Request method 'GET' not supported"}
				ToastUtil.showShort(EditGroupMeetingActivity.this,error);
				break;
			case Constants.HTTP_Get_MeetingGroupDetail:
				//分组详情
				ToastUtil.showShort(EditGroupMeetingActivity.this,error);
				break;
			case Constants.HTTP_Post_EditMeetingGroup:
				//编辑分组会议
				Log.i("android", "---------edit-----faile---------->: result"+error);
				ToastUtil.showShort(EditGroupMeetingActivity.this,error);
				break;
		}
	}
}