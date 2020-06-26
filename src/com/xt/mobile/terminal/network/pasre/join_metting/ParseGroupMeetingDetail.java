package com.xt.mobile.terminal.network.pasre.join_metting;
import java.util.List;

/**
 * 解析分组会议详情
 * Created by 彭永顺 on 2020/5/21.
 */
public class ParseGroupMeetingDetail {


    /**
     * responseCode : 1
     * responseDesc :
     * data : {"groupName":"嘻嘻嘻嘻","meetingUsers":[{"userName":"王克","userID":"wangke"}],"groupID":"82794694-47c9-38c6-92bb-0b2602192ecf","creatorID":"wangke"}
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
        /**
         * groupName : 嘻嘻嘻嘻
         * meetingUsers : [{"userName":"王克","userID":"wangke"}]
         * groupID : 82794694-47c9-38c6-92bb-0b2602192ecf
         * creatorID : wangke
         */

        private String groupName;
        private String groupID;
        private String creatorID;
        private List<MeetingUsersBean> meetingUsers;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public String getCreatorID() {
            return creatorID;
        }

        public void setCreatorID(String creatorID) {
            this.creatorID = creatorID;
        }

        public List<MeetingUsersBean> getMeetingUsers() {
            return meetingUsers;
        }

        public void setMeetingUsers(List<MeetingUsersBean> meetingUsers) {
            this.meetingUsers = meetingUsers;
        }

        public static class MeetingUsersBean {
            /**
             * userName : 王克
             * userID : wangke
             */

            private String userName;
            private String userID;

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
}
