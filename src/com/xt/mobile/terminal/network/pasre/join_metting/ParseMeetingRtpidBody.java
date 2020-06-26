package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by andy on 2020/5/31.
 */
public class ParseMeetingRtpidBody {


    /**
     * params : {"data":"[{\"screenIndex\":0,\"resourceID\":\"xiangdong\",\"resourceType\":\"User\",\"videoRTPId\":\"2924\",\"audioRTPId\":\"\"}]","groupID":"4ec4f898-c962-4ea8-b2b7-89a3280ec007"}
     * userID : xiangdong
     * funName : informGroupStartMedia
     */

    private ParamsBean params;
    private String userID;
    private String funName;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
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

    public static class ParamsBean {
        /**
         * data : [{"screenIndex":0,"resourceID":"xiangdong","resourceType":"User","videoRTPId":"2924","audioRTPId":""}]
         * groupID : 4ec4f898-c962-4ea8-b2b7-89a3280ec007
         */

        private String data;
        private String groupID;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }
    }
}
