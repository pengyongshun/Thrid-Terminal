package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by 彭永顺 on 2020/6/6.
 */
public class ParseChairApplyMemberSpeak {


    /**
     * body : {"params":{"buttons":[{"isClose":false,"text":"同意入会","command":{"params":{"isAgree":"true","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}},{"isClose":true,"text":"拒绝入会","command":{"params":{"isAgree":"false","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}}],"text":"收到彭永顺1的入会邀请","title":"入会申请消息"},"userID":"pengyongshun2","funName":"informShowMessage"}
     * extend : {"time":"13:49:33:065","userName":"","userID":"pengyongshun2"}
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
         * time : 13:49:33:065
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
