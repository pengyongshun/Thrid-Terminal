package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by andy on 2020/5/31.
 */
public class ParseMeetingRtpidInfo {


    /**
     * body : {"params":{"data":"[{\"screenIndex\":0,\"resourceID\":\"xiangdong\",\"resourceType\":\"User\",\"videoRTPId\":\"2924\",\"audioRTPId\":\"\"}]","groupID":"4ec4f898-c962-4ea8-b2b7-89a3280ec007"},"userID":"xiangdong","funName":"informGroupStartMedia"}
     * extend : {"time":"09:54:44:689","userName":"","userID":"xiangdong"}
     * uri : 1756
     * version : 1
     */

    private String body;
    private ExtendBean extend;
    private int uri;
    private int version;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ExtendBean getExtend() {
        return extend;
    }

    public void setExtend(ExtendBean extend) {
        this.extend = extend;
    }

    public int getUri() {
        return uri;
    }

    public void setUri(int uri) {
        this.uri = uri;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static class ExtendBean {
        /**
         * time : 09:54:44:689
         * userName :
         * userID : xiangdong
         */

        private String time;
        private String userName;
        private String userID;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }
    }
}
