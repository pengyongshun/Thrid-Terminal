package com.xt.mobile.terminal.thridpart.bean;

/**
 * Created by 彭永顺 on 2020/6/14.
 */
public class CreateMeetingBean {
    private String meetingID;//会议id
    private String memberID;//成员的userId
    private String meetingType;//会议类型
    private String chairmanID;//客户的主席userID

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

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getChairmanID() {
        return chairmanID;
    }

    public void setChairmanID(String chairmanID) {
        this.chairmanID = chairmanID;
    }

    @Override
    public String toString() {
        return "CreateMeetingBean{" +
                "meetingID='" + meetingID + '\'' +
                ", memberID='" + memberID + '\'' +
                ", meetingType='" + meetingType + '\'' +
                ", chairmanID='" + chairmanID + '\'' +
                '}';
    }
}