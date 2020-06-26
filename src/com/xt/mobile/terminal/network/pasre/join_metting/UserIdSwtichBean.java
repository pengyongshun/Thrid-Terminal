package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/16.
 */
public class UserIdSwtichBean {
    private String responseCode;
    private String responseDesc;
    private List<PeopleBean>data;
    public static class PeopleBean{
        private String kxuserid;
        private String userid;

        public String getKxuserid() {
            return kxuserid;
        }

        public void setKxuserid(String kxuserid) {
            this.kxuserid = kxuserid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        @Override
        public String toString() {
            return "PeopleBean{" +
                    "kxuserid='" + kxuserid + '\'' +
                    ", userid='" + userid + '\'' +
                    '}';
        }
    }

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

    public List<PeopleBean> getData() {
        return data;
    }

    public void setData(List<PeopleBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserIdSwtichBean{" +
                "responseCode='" + responseCode + '\'' +
                ", responseDesc='" + responseDesc + '\'' +
                ", data=" + data +
                '}';
    }
}
