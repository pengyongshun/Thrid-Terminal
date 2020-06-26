package com.xt.mobile.terminal.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xt.mobile.terminal.R;

import com.xt.mobile.terminal.adapter.GroupMeetingListAdapter;
import com.xt.mobile.terminal.bean.GroupMeetingListBean;
import com.xt.mobile.terminal.bean.JoinMettingPeopleBean;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.JsonParseUilt;
import com.xt.mobile.terminal.network.addparmer.GroupmettingAdd;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.network.http.MoudleParams;
import com.xt.mobile.terminal.network.http.NetUrl;
import com.xt.mobile.terminal.network.pasre.join_metting.JoinMettingBean;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseGroupMeetingDetail;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseGroupMeetingList;
import com.xt.mobile.terminal.network.pasre.join_metting.ParseMeetingList;
import com.xt.mobile.terminal.network.sysim.RequestUitl;
import com.xt.mobile.terminal.network.wss.WebSocketCommand;
import com.xt.mobile.terminal.network.wss.WssContant;
import com.xt.mobile.terminal.ui.BaseActivity;
import com.xt.mobile.terminal.util.DailogUitl;
import com.xt.mobile.terminal.util.FastJsonTools;
import com.xt.mobile.terminal.util.PopupWindowUitl;
import com.xt.mobile.terminal.util.ToastUtil;
import com.xt.mobile.terminal.util.XTUtils;
import com.xt.mobile.terminal.util.comm.UserMessge;
import com.xt.mobile.terminal.view.dailog.CustomTextDialog;

public class GroupMeetingActivity extends BaseActivity implements GroupMeetingListAdapter.OnClickCallBack, RequestUitl.HttpResultCall {

	private ImageButton left_iv;
	private TextView title_tv;
	private TextView right_tv;

	private ListView mListView;
	private TextView mEmptyTv;

	private List<GroupMeetingListBean> list=new ArrayList<GroupMeetingListBean>();
	private GroupMeetingListAdapter mAdapter;
	private PopupWindowUitl morePopupWindow;
	private TextView mPopEditTv;
	private TextView mPopDeletTv;
	private TextView mPopCloseTv;
	private GroupMeetingListBean curBean;
	private CustomTextDialog textDialog;
	private RequestUitl instans;
	private TextView mPopDetailTv;
	private JoinMettingBean joinMettingBean;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_group_metting);
		instans = RequestUitl.getInstans(this,this);
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
		title_tv.setText(R.string.fz_meeting);
		right_tv = (TextView) findViewById(R.id.right_tv);
		right_tv.setVisibility(View.VISIBLE);
		right_tv.setText(R.string.meeting_add_group);
		right_tv.setOnClickListener(this);
	}

	private void initView() {
		initTop();
		mListView = (ListView) findViewById(R.id.activity_group_meeting_lv);
		mEmptyTv = (TextView) findViewById(R.id.activity_group_meeting_empty_tv);

		mAdapter =new GroupMeetingListAdapter(this,list);
		mListView.setAdapter(mAdapter);
		mListView.setEmptyView(mEmptyTv);
		mAdapter.setOnClickListener(this);

		initDaliog();
	}

	private void initData() {
		// 获取之前保存的值
		onGetGroupMeetingList();

//		//模拟数据
//		if (list.size()>0){
//			list.clear();
//		}
//		for (int i = 0; i < 5; i++) {
//			GroupMeetingListBean bean=new GroupMeetingListBean();
//			bean.setGroupTitle("项目"+i);
//			bean.setGroupID("12245555"+i);
//			bean.setGroupContent("讨论项目设计方案");
//			if (i==3){
//				bean.setMeetingType(false);
//				bean.setNeedPassword(true);
//				bean.setPassword("137873"+i);
//
//			}else {
//				bean.setMeetingType(true);
//				bean.setNeedPassword(false);
//				bean.setPassword("");
//			}
//
//			List<JoinMettingPeopleBean> peopleBeen=new ArrayList<>();
//			for (int j = 0; j < 3; j++) {
//				JoinMettingPeopleBean peopleBean=new JoinMettingPeopleBean();
//				peopleBean.setName("张三"+j);
//				peopleBean.setChoice(false);
//				peopleBean.setMeetingID("123456789");
//				peopleBean.setUseId("zhangsan"+j);
//				peopleBean.setOnline(true);
//				peopleBeen.add(peopleBean);
//			}
//			bean.setGroupPeople(peopleBeen);
//			list.add(bean);
//		}
//		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (XTUtils.fastClick()) {
			return;
		}
		int id = v.getId();
		if (id == R.id.left_iv) {
			Intent intent1=new Intent(GroupMeetingActivity.this,ActivityMain.class);
			setResult(1002,intent1);
			finish();
		}else if (id == R.id.right_tv) {
			//跳转到新增界面
			Intent intent=new Intent(GroupMeetingActivity.this,EditGroupMeetingActivity.class);
			intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_GROUP_LIST_ADD);
			startActivityForResult(intent,1003);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
				Intent intent1=new Intent(GroupMeetingActivity.this,ActivityMain.class);
				setResult(1002,intent1);
				finish();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * c初始化对话框
	 */
	private void initDaliog() {
		//列表中更多
		morePopupWindow = DailogUitl.initMoreGroupPopwindow(this, new PopupWindowUitl.PopupWindowCall() {
			@Override
			public void initView(View view) {
				mPopEditTv = (TextView) view.findViewById(R.id.pop_metting_group_list_edit_tv);
				mPopDeletTv = (TextView) view.findViewById(R.id.pop_metting_group_list_delet_tv);
				mPopCloseTv = (TextView) view.findViewById(R.id.pop_metting_group_list_close_tv);
				mPopDetailTv = (TextView) view.findViewById(R.id.pop_metting_group_list_detail_tv);

			}

			@Override
			public void initEvent() {
				//编辑分组
				mPopEditTv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (curBean !=null){
							if (list !=null && list.size()>0){
								if (list.contains(curBean)){
									//跳转到编辑界面
									Intent intent=new Intent(GroupMeetingActivity.this,EditGroupMeetingActivity.class);
									intent.putExtra("GroupMeetingListBean",curBean);
									intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_GROUP_POP_EDIT);
									startActivityForResult(intent,1002);
								}
							}
						}

						//关闭弹出框
						if (morePopupWindow !=null &&(morePopupWindow.isShowing())){
							morePopupWindow.dissWindow();
						}

					}
				});
				//删除分组
				mPopDeletTv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (textDialog !=null &&!(textDialog.isShowing())){
							textDialog.show();
						}

						//关闭弹出框
						if (morePopupWindow !=null &&(morePopupWindow.isShowing())){
							morePopupWindow.dissWindow();
						}

					}
				});
				//会议详情
				mPopDetailTv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//关闭弹出框
						if (morePopupWindow !=null &&(morePopupWindow.isShowing())){
							morePopupWindow.dissWindow();
						}

						if (curBean !=null){
							if (list !=null && list.size()>0){
								if (list.contains(curBean)){
									Intent intent=new Intent(GroupMeetingActivity.this,EditGroupMeetingActivity.class);
									intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_GROUP_DETIAL_LOOK);
									intent.putExtra("GroupMeetingListBean",curBean);
									startActivity(intent);
								}
							}
						}

					}
				});
				//取消
				mPopCloseTv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
                        //关闭弹出框
						if (morePopupWindow !=null &&(morePopupWindow.isShowing())){
							morePopupWindow.dissWindow();
						}
					}
				});

			}
		});


		//显示对话框
		textDialog = DailogUitl.initTextDialog(GroupMeetingActivity.this, "删除会议", "确认删除当前会议吗？","删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//确认删除会议
				if (curBean !=null){
					//删除请求
					onDeletGroupMeeting(curBean.getGroupID());
//					if (list !=null && list.size()>0){
//						if (list.contains(curBean)){
//							list.remove(curBean);
//							mAdapter.notifyDataSetChanged();
//						}
//					}
				}

			}
		});
	}

	/**
	 * 分组会议列表
	 */
	public void onGetGroupMeetingList() {
		if (list.size()>0){
			list.clear();
		}
		mAdapter.notifyDataSetChanged();
		String userTokenkey = UserMessge.getInstans(GroupMeetingActivity.this).getUserTokenkey();
		Map<String,String> map=new HashMap<>();
		map.put("tokenKey",userTokenkey);
		List<NameValuePair> params = MoudleParams.getGroupMeetingListParams(this,map );
		// 请求
		showLoadingDaliog("请等待...");
		if (instans !=null){
			instans.sendRequest(params,true,Constants.HTTP_Get_MeetingGroupList);
		}

	}


	/**
	 * 删除分组会议
	 */
	public void onDeletGroupMeeting(String groupId) {
		String userTokenkey = UserMessge.getInstans(GroupMeetingActivity.this).getUserTokenkey();
		Map<String,String> map=new HashMap<>();
//		List<String> groupIds=new ArrayList<>();
//		groupIds.add(groupId);
//		Gson gson=new Gson();
//		String json = gson.toJson(groupIds);
		map.put("tokenKey",userTokenkey);
		map.put("groupID",groupId);
		List<NameValuePair> params = MoudleParams.getDeletGroupMeetingParams(this,map );
		// 请求
		showLoadingDaliog("正在删除中...");
		if (instans !=null){
			instans.sendRequest(params,false,Constants.HTTP_Post_DeleteMeetingGroup);
		}

	}


	/**
	 * 分组会议详情
	 */
	private void sendDetialGroupMeeting(String groupID ) {

			String userTokenkey = UserMessge.getInstans(GroupMeetingActivity.this).
					getUserTokenkey();
			Map<String,String> map=new HashMap<>();
			map.put("tokenKey",userTokenkey);
			map.put("groupID",groupID);
			List<NameValuePair> params = MoudleParams.getDetialGroupMeetingParams(GroupMeetingActivity.this,map);
			// 请求
			if (instans !=null){
				instans.sendRequest(params,true,Constants.HTTP_Get_MeetingGroupDetail);
			}
	}

	/**
	 * 填充数据
	 * @param bean
	 */
	private void setData(ParseGroupMeetingList bean) {
		if (bean !=null){
			List<GroupMeetingListBean> been=new ArrayList<>();
			ParseGroupMeetingList.DataBean data = bean.getData();
			if (data !=null){
				List<ParseGroupMeetingList.DataBean.ListBean> listBeen = data.getList();
				if (listBeen != null && listBeen.size()>0){
					for (int i = 0; i < listBeen.size(); i++) {
						GroupMeetingListBean bean1=new GroupMeetingListBean();
						bean1.setGroupTitle(listBeen.get(i).getGroupName());
						bean1.setGroupContent(listBeen.get(i).getDescription());
						bean1.setGroupID(listBeen.get(i).getGroupID());
						String groupType = listBeen.get(i).getGroupType();
						//常规、不拼接
						bean1.setMeetingType(true);
//						if (groupType.equals("true")){
//							//代表拼接
//							bean1.setMeetingType(false);
//
//						}else if (groupType.equals("false")){
//							//代表常规
//							bean1.setMeetingType(true);
//						}
						been.add(bean1);
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



	// ///////////////////////////////////////////网络请求接收结果的地方///////////////////////////////

	/**
	 * @param tag
	 * @param result  {"responseCode":"0","responseDesc":"","data":{}}
	 */
	@Override
	public void success(int tag, String result) {
		hideLoadingDialog();
		switch (tag) {
		  case Constants.HTTP_Get_MeetingGroupList:
			  // 获取分组列表

			  try {
				  PLog.d("---------query-----success---------->: result"+result);
				  Log.i("android", "---------query-----success---------->: result"+result);
				  ParseGroupMeetingList bean = FastJsonTools.json2BeanObject(result, ParseGroupMeetingList.class);
				  setData(bean);
			  } catch (Exception e) {
				     e.printStackTrace();
			      }
			break;
			case Constants.HTTP_Post_DeleteMeetingGroup:
				//删除分组会议 成功后，查询列表
				onGetGroupMeetingList();
				PLog.d("---------Delete-----success---------->: result"+result);
				Log.i("android", "---------Delete-----success---------->: result"+result);
				break;

			case Constants.HTTP_Get_MeetingGroupDetail:
				//分组详情
				if (result!=null){
					ParseGroupMeetingDetail beanObject = FastJsonTools.json2BeanObject(result,
							ParseGroupMeetingDetail.class);
					if (beanObject !=null){
						ParseGroupMeetingDetail.DataBean data = beanObject.getData();
						if (data !=null){
							joinMettingBean=new JoinMettingBean();
							joinMettingBean.setSceneName(data.getGroupName());
							joinMettingBean.setSceneID(data.getGroupID());
							joinMettingBean.setUserID(data.getCreatorID());
							List<JoinMettingBean.MembersBean> membersBeen = new ArrayList<>();
							List<ParseGroupMeetingDetail.DataBean.MeetingUsersBean> meetingUsers = data.getMeetingUsers();
							if (meetingUsers !=null && meetingUsers.size()>0){
								for (int i = 0; i < meetingUsers.size(); i++) {
									JoinMettingBean.MembersBean membersBean = new JoinMettingBean.MembersBean();
									ParseGroupMeetingDetail.DataBean.MeetingUsersBean meetingUsersBean = meetingUsers.get(i);
									if (data.getCreatorID().equals(meetingUsersBean.getUserID())){
										//代表的是主席
										membersBean.setRole("chairman");
									}else {
										//代表的是成员
										membersBean.setRole("memberman");
									}

									membersBean.setStatus("onlineInMeeting");
									membersBean.setUserID(meetingUsersBean.getUserID());
									membersBean.setIndex(i+"");
									membersBean.setUserName(meetingUsersBean.getUserName());
									membersBeen.add(membersBean);
								}
							}

							joinMettingBean.setMembers(membersBeen);

							//转换数据
							boolean isMember=true;
							String userID = UserMessge.getInstans(GroupMeetingActivity.this).getUserID();
							if (userID.equals(joinMettingBean.getUserID())){
								//代表当前用户是主席
								isMember=false;
							}else {
								isMember=true;
							}
							String json = FastJsonTools.bean2Json(joinMettingBean);
							Intent intent=new Intent(GroupMeetingActivity.this,VedioMettingActivity.class);
							intent.putExtra(Constants.ACTIVTY_TAG,Constants.ACTIVTY_GROUP_POP_JOIN);
							intent.putExtra("JoinMettingBean", json);
							intent.putExtra("isMember",isMember);
							startActivity(intent);


						}
					}
				}
				break;
		}
	}


	@Override
	public void faile(int tag, String error) {
		hideLoadingDialog();
		switch (tag) {
		// 获取分组列表
		case Constants.HTTP_Get_MeetingGroupList:
			PLog.d("---------query-----faile---------->: result"+error);

			break;
		case Constants.HTTP_Post_DeleteMeetingGroup:
			//删除分组会议
			PLog.d("---------Delete-----faile---------->: result"+error);
			Log.i("android", "---------faile---------->: result"+error);
			ToastUtil.showShort(GroupMeetingActivity.this,error);
			break;
		case Constants.HTTP_Get_MeetingGroupDetail:
			//分组详情
			PLog.d("---------Delete-----faile---------->: result"+error);
			Log.i("android", "---------faile---------->: result"+error);
			ToastUtil.showShort(GroupMeetingActivity.this,error);
			break;
		}
	}

	////////////////////////////列表监听/////////////////////////////

	@Override
	public void moreClick(GroupMeetingListBean bean) {
		curBean=bean;
		if (morePopupWindow !=null && !(morePopupWindow.isShowing())){
			morePopupWindow.showWindow(mListView, Gravity.BOTTOM,0,0);
		}
	}

	@Override
	public void deletClick(GroupMeetingListBean bean) {
		//进入会议
		curBean=bean;
		//先查询，获取会议对应的信息
		showLoadingDaliog("正在进入会议中...");
		sendDetialGroupMeeting(curBean.getGroupID());

	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==1001){
			switch (requestCode){
				case 1002:
					//编辑
					//查询
					onGetGroupMeetingList();
					break;
				case 1003:
					//新增
					//查询
					onGetGroupMeetingList();
					break;
			}
		}

	}



}