package com.xt.mobile.terminal.thridpart.bean;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/16.
 */
public class InvitePeopleBean {
    private String meetingID;//会议id
    private String meetingType;//会议类型
    private List<ChoicePeople> peopleBeen;//邀请的成员信息

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public List<ChoicePeople> getPeopleBeen() {
        return peopleBeen;
    }

    public void setPeopleBeen(List<ChoicePeople> peopleBeen) {
        this.peopleBeen = peopleBeen;
    }

    @Override
    public String toString() {
        return "InvitePeopleBean{" +
                "meetingID='" + meetingID + '\'' +
                ", meetingType='" + meetingType + '\'' +
                ", peopleBeen=" + peopleBeen +
                '}';
    }

    public static class ChoicePeople{
        private String userId;
        private String userName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String useId) {
            this.userId = useId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public String toString() {
            return "ChoicePeople{" +
                    "useId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }
}
