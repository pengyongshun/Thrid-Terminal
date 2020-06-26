package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * 解析进入会议首页，获取会议列表
 * Created by 彭永顺 on 2020/5/21.
 */
public class ParseMeetingList {

    /**
     * responseCode : 1
     * responseDesc : success
     * data : {"list":[{"sceneName":"临时会议","chairmanName":"王克","sceneID":"b40a7591-04f5-4a68-89d5-e50ebab41744","chairmanID":"wangke","beginTime":"2020-05-29 14:09:13"}]}
     */

    private String responseCode;
    private String responseDesc;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * sceneName : 临时会议
             * chairmanName : 王克
             * sceneID : b40a7591-04f5-4a68-89d5-e50ebab41744
             * chairmanID : wangke
             * beginTime : 2020-05-29 14:09:13
             */

            private String sceneName;
            private String chairmanName;
            private String sceneID;
            private String chairmanID;
            private String beginTime;

            public String getSceneName() {
                return sceneName;
            }

            public void setSceneName(String sceneName) {
                this.sceneName = sceneName;
            }

            public String getChairmanName() {
                return chairmanName;
            }

            public void setChairmanName(String chairmanName) {
                this.chairmanName = chairmanName;
            }

            public String getSceneID() {
                return sceneID;
            }

            public void setSceneID(String sceneID) {
                this.sceneID = sceneID;
            }

            public String getChairmanID() {
                return chairmanID;
            }

            public void setChairmanID(String chairmanID) {
                this.chairmanID = chairmanID;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }
        }
    }
}
