package com.xt.mobile.terminal.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xt.mobile.terminal.R;
import com.xt.mobile.terminal.adapter.MessageSessionListAdapter;
import com.xt.mobile.terminal.bean.MessageSessionBean;
import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.domain.SipInfo;
import com.xt.mobile.terminal.view.ClearEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FragmentMessage extends Fragment implements OnClickListener {

	private View rootView;
	private String mUserId;
	private TextView mLeftTv;
	private ImageButton mLeftIv;
	private TextView mTitleTv;
	private TextView mRightTv;
	private ImageButton mRightIv;
	private MessageSessionListAdapter mMsgSessionAdapter;
	private List<MessageSessionBean> mMessageSessionList = new ArrayList<MessageSessionBean>();
	private ListView mListView;
	private TextView mEmptyTv;
	private ClearEditText mCetMessagerQuery;

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,
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
		initData();
		initView();
	}

	private void initView() {

		rootView = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_message, null);
		mLeftTv = (TextView) rootView.findViewById(R.id.left_tv);
		mLeftIv = (ImageButton) rootView.findViewById(R.id.left_iv);
		mTitleTv = (TextView) rootView.findViewById(R.id.title_tv);
		mRightTv = (TextView) rootView.findViewById(R.id.right_tv);
		mRightIv = (ImageButton) rootView.findViewById(R.id.right_iv);
		mListView = (ListView) rootView.findViewById(R.id.fragment_message_lv);
		mEmptyTv = (TextView) rootView.findViewById(R.id.fragment_messge_empty);
		mCetMessagerQuery = rootView.findViewById(R.id.cet_message_query);
		mCetMessagerQuery.addOnAfterTextChanged(new ClearEditText.OnAfterTextChanged() {
			@Override
			public void afterTextChanged(String text) {
				mEmptyTv.setText(text);
			}
		});

		mLeftTv.setVisibility(View.GONE);
		mLeftIv.setVisibility(View.GONE);
		mRightTv.setVisibility(View.GONE);
		mRightIv.setVisibility(View.GONE);
		mTitleTv.setText("消息");

		for(int s = 0 ; s < 20; s++){
			MessageSessionBean sessionBean = new MessageSessionBean();
			sessionBean.setUsers("AAA "+s);
			sessionBean.setSessionID("sss "+s);
			sessionBean.setLastMsg("msg " + s);
			sessionBean.setLastTime("time " + s);
			List<SipInfo> sipInfoList = new ArrayList<>();
			for(int i = 0 ; i < (s % 6) + 1; i ++){
				SipInfo info = new SipInfo();
				info.setUserid("UID"+ i + s);
				info.setUsername("UName"+ i + s);
				sipInfoList.add(info);
			}
			sessionBean.setSipInfoList(sipInfoList);
			mMessageSessionList.add(sessionBean);
		}
		mMsgSessionAdapter = new MessageSessionListAdapter(getContext(),mMessageSessionList);
		mMsgSessionAdapter.setOnDataEmptyCallBack(new MessageSessionListAdapter.OnDataEmptyCallBack() {
			@Override
			public void dataEmpty() {
				mEmptyTv.setVisibility(mMessageSessionList.size() > 0 ? View.GONE : View.VISIBLE);
				mListView.setVisibility(mMessageSessionList.size() > 0 ? View.VISIBLE : View.GONE);
			}
		});
		mEmptyTv.setVisibility(mMessageSessionList.size() > 0 ? View.GONE : View.VISIBLE);
		mListView.setVisibility(mMessageSessionList.size() > 0 ? View.VISIBLE : View.GONE);
		mListView.setAdapter(mMsgSessionAdapter);
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == 1 ) mMsgSessionAdapter.notifyDataSetChanged();
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void initData() {
		SharedPreferences sp = getActivity().getSharedPreferences(ConstantsValues.DEFAULT_SP, Context.MODE_PRIVATE);
		mUserId = sp.getString(ConstantsValues.USERID, null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public void update() {

	}
}