package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by 彭永顺 on 2020/5/18.
 */
public class PasreJoinMettingBean {


    /**
     * body : {"params":{"sceneType":"0","sceneName":"aa","members":[{"role":"chairman","index":"0","userID":"wangke","status":"onlineInMeeting"}],"sceneID":"15847a3a-b5a0-46b3-8ea2-5f2f892804e2","operatorID":"wangke"},"userID":"wangke","funName":"informRefreshActivedSceneDetail"}
     * extend : {"time":"11:09:47:337","userName":"","userID":"wangke"}
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
         * time : 11:09:47:337
         * userName :
         * userID : wangke
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
