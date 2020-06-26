package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by 彭永顺 on 2020/6/4.
 */
public class ParseMeetingApplySpeark {

    /**
     * body : {"params":{"buttons":[{"isClose":true,"text":"同意发言申请","command":{"params":{"sceneID":"ddd523e0-ca7e-408c-b6e6-a46bf1029e83","memberID":"pengyongshun1"},"funName":"publishAcceptSpeakerFromConference"}},{"isClose":true,"text":"拒绝发言申请","command":{"params":{}}}],"text":"收到彭永顺1发言申请","title":"申请发言消息"},"userID":"pengyongshun2","funName":"informShowMessage"}
     * extend : {"time":"07:19:37:255","userName":"","userID":"pengyongshun2"}
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
         * time : 07:19:37:255
         * userName :
         * userID : pengyongshun2
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
