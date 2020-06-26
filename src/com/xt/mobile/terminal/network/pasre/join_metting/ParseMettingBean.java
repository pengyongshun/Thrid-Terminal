package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/18.
 */
public class ParseMettingBean {


    /**
     * params : {"sceneType":"0","sceneName":"aa","members":[{"role":"chairman","index":"0","userID":"wangke","status":"onlineInMeeting"}],"sceneID":"15847a3a-b5a0-46b3-8ea2-5f2f892804e2","operatorID":"wangke"}
     * userID : wangke
     * funName : informRefreshActivedSceneDetail
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
         * sceneType : 0
         * sceneName : aa
         * members : [{"role":"chairman","index":"0","userID":"wangke","status":"onlineInMeeting"}]
         * sceneID : 15847a3a-b5a0-46b3-8ea2-5f2f892804e2
         * operatorID : wangke
         */

        private String sceneType;
        private String sceneName;
        private String sceneID;
        private String operatorID;
        private List<MembersBean> members;

        public String getSceneType() {
            return sceneType;
        }

        public void setSceneType(String sceneType) {
            this.sceneType = sceneType;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getSceneID() {
            return sceneID;
        }

        public void setSceneID(String sceneID) {
            this.sceneID = sceneID;
        }

        public String getOperatorID() {
            return operatorID;
        }

        public void setOperatorID(String operatorID) {
            this.operatorID = operatorID;
        }

        public List<MembersBean> getMembers() {
            return members;
        }

        public void setMembers(List<MembersBean> members) {
            this.members = members;
        }

        public static class MembersBean {
            /**
             * role : chairman
             * index : 0
             * userID : wangke
             * status : onlineInMeeting
             */

            private String role;
            private String index;
            private String userID;
            private String status;

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getUserID() {
                return userID;
            }

            public void setUserID(String userID) {
                this.userID = userID;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
