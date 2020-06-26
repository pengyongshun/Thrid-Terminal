package com.xt.mobile.terminal.network.pasre.join_metting;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/18.
 */
public class JoinMettingBean implements Serializable{
    private String userName;
    private String userID;
    private String sceneName;
    private String sceneID;
    private List<MembersBean> members;

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

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    public static class MembersBean implements Serializable{
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
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

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

        @Override
        public String toString() {
            return "MembersBean{" +
                    "role='" + role + '\'' +
                    ", index='" + index + '\'' +
                    ", userID='" + userID + '\'' +
                    ", status='" + status + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JoinMettingBean{" +
                "userName='" + userName + '\'' +
                ", userID='" + userID + '\'' +
                ", sceneName='" + sceneName + '\'' +
                ", sceneID='" + sceneID + '\'' +
                ", members=" + members +
                '}';
    }
}
