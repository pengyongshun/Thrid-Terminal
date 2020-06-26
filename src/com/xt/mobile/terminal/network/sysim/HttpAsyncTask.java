package com.xt.mobile.terminal.network.sysim;

import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.util.ToolNetwork;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpAsyncTask extends AsyncTask<String, Void, String> {

	private Context mContext = null;
	private String url = null;
	private String data = null;
	private HttpCallBack callback = null;
	private Boolean mIsGet = true;
	private boolean mIsCrypto = true;
	private Boolean mNetConnect = true;

	public HttpAsyncTask(Context mContext, String url, String data, HttpCallBack jsonCallback,
			Boolean isGet, boolean isCrypto) {
		this.mContext = mContext;
		this.url = url;
		this.data = data;
		this.mIsCrypto = isCrypto;
		this.callback = jsonCallback;
		this.mIsGet = isGet;
	}

	@Override
	protected String doInBackground(String... params) {
		String srcStr = "error";
		if (mNetConnect) {
			try {
				if (mIsGet) {
					if (mIsCrypto) {
						srcStr = HttpMethod.doHttpsGet(url);
					} else {
						srcStr = HttpMethod.doHttpGet(url);
					}
				} else {
					if (mIsCrypto) {
						srcStr = HttpMethod.doHttpsPost(url, data);
					} else {
						srcStr = HttpMethod.doHttpPost(url, data);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		PLog.d("HttpAsyncTask","doInBackground----------netResult----------->srcStr ="+srcStr);

		return srcStr;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (!result.contentEquals("error")) {
			try {
				PLog.d("HttpAsyncTask","onPostExecute----------netResult----------->result ="+result);
				callback.setResult(result);
			} catch (Exception e) {
				e.printStackTrace();
				callback.setResult("errorData");
				Toast.makeText(mContext, "连接会议服务失败！", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, "连接会议服务失败", Toast.LENGTH_SHORT).show();
			callback.setResult("errorNet");
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mNetConnect = true;
		if (mContext != null && !ToolNetwork.isNetworkConnected(mContext)) {
			mNetConnect = false;
			return;
		}
	}

	public interface HttpCallBack {
		public void setResult(String result);
	}

}
