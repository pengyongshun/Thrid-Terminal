package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by 彭永顺 on 2020/6/10.
 */
public class ParseUserIdSwtich {

    /**
     * responseCode : 1
     * responseDesc : 成功
     * data : {"XTUserID":"bug@f7e8decd-cb4c-4000-b53b-450a43e204fc"}
     */

    private String responseCode;
    private String responseDesc;
    private String data;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
