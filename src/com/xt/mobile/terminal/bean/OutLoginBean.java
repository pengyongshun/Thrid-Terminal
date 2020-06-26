package com.xt.mobile.terminal.bean;

/**
 * Created by 彭永顺 on 2020/6/12.
 */
public class OutLoginBean {

    /**
     * code : 1756
     * userID : pengyongshun1
     * funName : leave
     * params : {"operatorToken":"-M9b6EM-vuLU-jfm8hYr"}
     */

    private int code;
    private String userID;
    private String funName;
    private ParamsBean params;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFunName() {
        return funName;
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * operatorToken : -M9b6EM-vuLU-jfm8hYr
         */

        private String operatorToken;

        public String getOperatorToken() {
            return operatorToken;
        }

        public void setOperatorToken(String operatorToken) {
            this.operatorToken = operatorToken;
        }
    }
}
