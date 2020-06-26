package com.xt.mobile.terminal.network.sysim;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xt.mobile.terminal.log.PLog;
import com.xt.mobile.terminal.network.http.Constants;
import com.xt.mobile.terminal.util.HttpUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/15.
 */
public class RequestUitl {
    private static RequestUitl requestUitl;
    private Context context;
    private HttpAsyncTask mHttpAsyncTask;
    private int Tag=-1;
    String urlGet ="";
    private HttpResultCall httpResultCall;
    private Handler mHandler = new Handler();

    ////////////单例模式//////////////////////////////////////
    public static RequestUitl getInstans(Context context, HttpResultCall httpResultCall){
        requestUitl=new RequestUitl(context,httpResultCall);
           return requestUitl;
    }
    private RequestUitl(Context context , HttpResultCall httpResultCall){
        this.context=context;
        this.httpResultCall=httpResultCall;

    }


    /**
     * @param paramsList 参数
     * @param isGET  ture-get   false-post
     * @param requestTag  请求的唯一标识
     */
    public void sendRequest(List<NameValuePair> paramsList,boolean isGET
            ,int requestTag){
        if (paramsList !=null && paramsList.size()>0){
            String url = paramsList.get(0).getValue();
            String path ="";
            if (paramsList.size()==1){
                //没有参数
               path=url;
            }else {
                //有参数，需要看下url里面最后一个是否带有=符号
                paramsList.remove(0);
                //对参数进行格式化
                String params = URLEncodedUtils.format(paramsList, HTTP.UTF_8);
                if (url.contains("=")){
                    if (isGET) {
                        String last = url.substring(url.length() - 1, url.length());
                        if (last !=null && last.length()>0){
                            if (last.equals("=")){
                                //调用的地址
                                path = url + params;

                            }else {
                                //最后一个不是=符号
                                //调用的地址
                                path = url+"?" + params;
                            }
                        }

                        Log.i("===aaa===", "===get===path:" + path);
                        Tag=requestTag;
                        HttpCallback jsonCallback = new HttpCallback();
                        mHttpAsyncTask = new HttpAsyncTask(context, path, null, jsonCallback, isGET, true);
                        mHttpAsyncTask.execute("");
                    } else {

                        Log.i("===aaa===", "===post===path:" + path);
                        Log.i("===aaa===", "===post===params:" + params);
                        Tag=requestTag;
                        HttpCallback jsonCallback = new HttpCallback();
                        mHttpAsyncTask = new HttpAsyncTask(context, path, params, jsonCallback, isGET, true);
                        mHttpAsyncTask.execute("");
                    }
                }else {
                    //有参数，url里面没有带=符号
                    //调用的地址
                    if (isGET) {
                        path = url+"?" + params;
                        PLog.d("===get===path:" + path);
                        Log.i("===aaa===", "===get===path:" + path);
                        Tag=requestTag;
                        HttpCallback jsonCallback = new HttpCallback();
                        mHttpAsyncTask = new HttpAsyncTask(context, path, null, jsonCallback, isGET, true);
                        mHttpAsyncTask.execute("");
                    } else {
                        path=url;
                        PLog.d("===post===path:" + path);
                        PLog.d("===post===params:" + params);
                        Log.i("===aaa===", "===post===path:" + path);
                        Log.i("===aaa===", "===post===params:" + params);
                        Tag=requestTag;
                        HttpCallback jsonCallback = new HttpCallback();
                        mHttpAsyncTask = new HttpAsyncTask(context, path, params, jsonCallback, isGET, true);
                        mHttpAsyncTask.execute("");
                    }
                }

                  }

                }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                httpResultCall.faile(Tag, String.valueOf("请求超时"));
            }
        }, 10000);
        }

    public class HttpCallback implements HttpAsyncTask.HttpCallBack {

        @Override
        public void setResult(String result) {
            // TODO Auto-generated method stub
            //销毁异步任务
            if (mHttpAsyncTask != null && !mHttpAsyncTask.isCancelled()) {
                mHttpAsyncTask.cancel(true);
                mHttpAsyncTask = null;
            }

            mHandler.removeCallbacksAndMessages(null);
            if (result != null && result.length() > 0) {

                if (result.contains("success") || result.contains("\"responseCode\":\"1\"")) {
                    //成功
                    httpResultCall.success(Tag,result);
                } else if (result.equals("errorNet") || result.contentEquals("errorData")) {
                    //网络请求失败
                    httpResultCall.faile(Tag,String.valueOf("网络请求异常"));
                } else {
                    //返回结果失败
                    if (Tag> Constants.HTTP_GET_RE_NEWUSER_TOKEN){
                        try {
                            JSONObject obj = new JSONObject(result);
                            String desc = obj.getString("responseDesc");
                            httpResultCall.faile(Tag,desc);
                        } catch (JSONException e) {
                            httpResultCall.faile(Tag,e.getMessage());
                            e.printStackTrace();
                        }
                    }else {
                        httpResultCall.faile(Tag,result);
                    }
                }
            } else {
                //异常
                httpResultCall.faile(Tag,String.valueOf("返回结果异常"));
            }
        }
    }



    public interface HttpResultCall{
        void success(int tag , String result);
        void faile(int tag , String error);
    }


}
