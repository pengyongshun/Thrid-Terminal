package com.xt.mobile.terminal.network.pasre.join_metting;


/**
 * Created by 彭永顺 on 2020/5/29.
 */
public class ParseMeetingPeopleList {

    /**
     * body : {"params":{"sceneType":"0","sceneName":"临时会议","members":[{"role":"member","index":"0","userName":"彭永顺","userID":"pengyongshun","status":"onlineInMeeting"},{"role":"chairman","index":"0","userName":"王克","userID":"wangke","status":"onlineInMeeting"}],"sceneID":"9fde34ab-711f-4599-8c90-46afcc728406","description":"临时会议","operatorID":"pengyongshun"},"userID":"pengyongshun","funName":"informRefreshActivedSceneDetail"}
     * extend : {"time":"15:50:21:287","userName":"","userID":"pengyongshun"}
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
         * time : 15:50:21:287
         * userName :
         * userID : pengyongshun
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

        @Override
        public String toString() {
            return "ExtendBean{" +
                    "time='" + time + '\'' +
                    ", userName='" + userName + '\'' +
                    ", userID='" + userID + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "ParseMeetingPeopleList{" +
                "body=" + body +
                ", extend=" + extend +
                ", uri=" + uri +
                ", version=" + version +
                '}';
    }
}
