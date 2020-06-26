package com.xt.mobile.terminal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/19.
 */
public class GroupMeetingListBean implements Serializable{
    private String groupTitle;//会议名称
    private String groupID;//会议ID
    private String groupContent;//会议描述
    private boolean meetingType;//ture-常规   false-拼接
    private boolean needPassword;//ture-需要密码   false-不需要密码
    private String password;//在需要密码的时候才有
    private List<JoinMettingPeopleBean> groupPeople;

    public boolean isMeetingType() {
        return meetingType;
    }

    public void setMeetingType(boolean meetingType) {
        this.meetingType = meetingType;
    }

    public boolean isNeedPassword() {
        return needPassword;
    }

    public void setNeedPassword(boolean needPassword) {
        this.needPassword = needPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupContent() {
        return groupContent;
    }

    public void setGroupContent(String groupContent) {
        this.groupContent = groupContent;
    }

    public List<JoinMettingPeopleBean> getGroupPeople() {
        return groupPeople;
    }

    public void setGroupPeople(List<JoinMettingPeopleBean> groupPeople) {
        this.groupPeople = groupPeople;
    }

    @Override
    public String toString() {
        return "GroupMeetingListBean{" +
                "groupTitle='" + groupTitle + '\'' +
                ", groupID='" + groupID + '\'' +
                ", groupContent='" + groupContent + '\'' +
                ", meetingType=" + meetingType +
                ", needPassword=" + needPassword +
                ", password='" + password + '\'' +
                ", groupPeople=" + groupPeople +
                '}';
    }
}
