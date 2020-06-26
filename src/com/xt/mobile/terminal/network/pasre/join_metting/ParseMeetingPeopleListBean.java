package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/30.
 */
public class ParseMeetingPeopleListBean {

    /**
     * params : {"sceneType":"0","sceneName":"临时会议","members":[{"role":"member","index":"0","userName":"彭永顺","userID":"pengyongshun","status":"onlineInMeeting"},{"role":"chairman","index":"0","userName":"王克","userID":"wangke","status":"onlineInMeeting"}],"sceneID":"b9189bdf-a476-410d-ad73-bef9a01e2256","description":"临时会议","operatorID":"pengyongshun"}
     * userID : pengyongshun
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
         * sceneName : 临时会议
         * members : [{"role":"member","index":"0","userName":"彭永顺","userID":"pengyongshun","status":"onlineInMeeting"},{"role":"chairman","index":"0","userName":"王克","userID":"wangke","status":"onlineInMeeting"}]
         * sceneID : b9189bdf-a476-410d-ad73-bef9a01e2256
         * description : 临时会议
         * operatorID : pengyongshun
         */

        private String sceneType;
        private String sceneName;
        private String sceneID;
        private String description;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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
             * role : member
             * index : 0
             * userName : 彭永顺
             * userID : pengyongshun
             * status : onlineInMeeting
             */

            private String role;
            private String index;
            private String userName;
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

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            @Override
            public String toString() {
                return "MembersBean{" +
                        "role='" + role + '\'' +
                        ", index='" + index + '\'' +
                        ", userName='" + userName + '\'' +
                        ", userID='" + userID + '\'' +
                        ", status='" + status + '\'' +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "ParseMeetingPeopleListBean{" +
                "params=" + params +
                ", userID='" + userID + '\'' +
                ", funName='" + funName + '\'' +
                '}';
    }
}
