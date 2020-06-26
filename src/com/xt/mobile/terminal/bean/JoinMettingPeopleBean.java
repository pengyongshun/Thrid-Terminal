package com.xt.mobile.terminal.bean;

import java.io.Serializable;

/**
 * Created by 彭永顺 on 2020/5/11.
 */
public class JoinMettingPeopleBean implements Serializable{
    private String name;//姓名(useName)
    private boolean isChoice=false;//选中状态
    private String useId;//成员id
    private String meetingID;//会议id
    private boolean isOnline;//是否在线
    private String role;//角色  chairman-主席 member-成员

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoice() {
        return isChoice;
    }

    public void setChoice(boolean choice) {
        isChoice = choice;
    }

    @Override
    public String toString() {
        return "JoinMettingPeopleBean{" +
                "name='" + name + '\'' +
                ", isChoice=" + isChoice +
                ", useId='" + useId + '\'' +
                ", meetingID='" + meetingID + '\'' +
                ", isOnline=" + isOnline +
                ", role='" + role + '\'' +
                '}';
    }
}
