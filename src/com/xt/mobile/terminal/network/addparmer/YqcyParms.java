package com.xt.mobile.terminal.network.addparmer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/18.
 */
public class YqcyParms implements Serializable{


    /**
     * code : 1756
     * userID : lixiaojia
     * funName : publishAddMembersFromConference
     * params : {"sceneID":"562b641b-b86a-45f0-93cb-3a23a6a82b04","members":[{"index":0,"userID":"taoyong","userName":"陶勇","resourceType":"User"}],"operatorToken":"-M7bwKA6ipLA9i5Nhhb1"}
     */

    private int code;
    private String userID;
    private String funName;
    private ParamsBean params;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * sceneID : 562b641b-b86a-45f0-93cb-3a23a6a82b04
         * members : [{"index":0,"userID":"taoyong","userName":"陶勇","resourceType":"User"}]
         * operatorToken : -M7bwKA6ipLA9i5Nhhb1
         */

        private String sceneID;
        private String operatorToken;
        private List<MembersBean> members;

        public String getSceneID() {
            return sceneID;
        }

        public void setSceneID(String sceneID) {
            this.sceneID = sceneID;
        }

        public String getOperatorToken() {
            return operatorToken;
        }

        public void setOperatorToken(String operatorToken) {
            this.operatorToken = operatorToken;
        }

        public List<MembersBean> getMembers() {
            return members;
        }

        public void setMembers(List<MembersBean> members) {
            this.members = members;
        }

        public static class MembersBean {
            /**
             * index : 0
             * userID : taoyong
             * userName : 陶勇
             * resourceType : User
             */

            private int index;
            private String userID;
            private String userName;
            private String resourceType;

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getUserID() {
                return userID;
            }

            public void setUserID(String userID) {
                this.userID = userID;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getResourceType() {
                return resourceType;
            }

            public void setResourceType(String resourceType) {
                this.resourceType = resourceType;
            }
        }
    }
}
