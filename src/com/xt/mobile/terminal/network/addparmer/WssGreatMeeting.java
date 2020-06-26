package com.xt.mobile.terminal.network.addparmer;

import java.util.List;

/**
 * wss请求  创建会议
 * Created by 彭永顺 on 2020/5/25.
 */
public class WssGreatMeeting {


    /**
     * code : 1756
     * userID : chenmingjun
     * funName : publishStartConference
     * params : {"sceneName":"临时会议","description":"临时会议","schemeID":"","isMediaProcessing":"false","operatorName":"陈明军","needPassword":"false","password":"1234","members":[{"index":0,"userID":"wangke","userName":"王克","resourceType":"User"}],"device":[],"spectator":[],"mediaType":"AV","operatorToken":"-M88MM_07zBOxeIprwlu"}
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
         * sceneName : 临时会议
         * description : 临时会议
         * schemeID :
         * isMediaProcessing : false
         * operatorName : 陈明军
         * needPassword : false
         * password : 1234
         * members : [{"index":0,"userID":"wangke","userName":"王克","resourceType":"User"}]
         * device : []
         * spectator : []
         * mediaType : AV
         * operatorToken : -M88MM_07zBOxeIprwlu
         */

        private String sceneName;
        private String description;
        private String schemeID;
        private String isMediaProcessing;
        private String operatorName;
        private String needPassword;
        private String password;
        private String mediaType;
        private String operatorToken;
        private List<MembersBean> members;
        private List<?> device;
        private List<?> spectator;

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSchemeID() {
            return schemeID;
        }

        public void setSchemeID(String schemeID) {
            this.schemeID = schemeID;
        }

        public String getIsMediaProcessing() {
            return isMediaProcessing;
        }

        public void setIsMediaProcessing(String isMediaProcessing) {
            this.isMediaProcessing = isMediaProcessing;
        }

        public String getOperatorName() {
            return operatorName;
        }

        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        public String getNeedPassword() {
            return needPassword;
        }

        public void setNeedPassword(String needPassword) {
            this.needPassword = needPassword;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
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

        public List<?> getDevice() {
            return device;
        }

        public void setDevice(List<?> device) {
            this.device = device;
        }

        public List<?> getSpectator() {
            return spectator;
        }

        public void setSpectator(List<?> spectator) {
            this.spectator = spectator;
        }

        public static class MembersBean {
            /**
             * index : 0
             * userID : wangke
             * userName : 王克
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

            @Override
            public String toString() {
                return "MembersBean{" +
                        "index=" + index +
                        ", userID='" + userID + '\'' +
                        ", userName='" + userName + '\'' +
                        ", resourceType='" + resourceType + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "sceneName='" + sceneName + '\'' +
                    ", description='" + description + '\'' +
                    ", schemeID='" + schemeID + '\'' +
                    ", isMediaProcessing='" + isMediaProcessing + '\'' +
                    ", operatorName='" + operatorName + '\'' +
                    ", needPassword='" + needPassword + '\'' +
                    ", password='" + password + '\'' +
                    ", mediaType='" + mediaType + '\'' +
                    ", operatorToken='" + operatorToken + '\'' +
                    ", members=" + members +
                    ", device=" + device +
                    ", spectator=" + spectator +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WssGreatMeeting{" +
                "code=" + code +
                ", userID='" + userID + '\'' +
                ", funName='" + funName + '\'' +
                ", params=" + params +
                '}';
    }
}
