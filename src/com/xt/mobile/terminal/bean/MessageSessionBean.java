package com.xt.mobile.terminal.bean;

import com.xt.mobile.terminal.domain.SipInfo;

import java.util.List;

/**
 * Created by gzhul on 2020/6/8.
 */
public class MessageSessionBean {
    private String sessionID;
    private String users;
    private String image;
    private String lastMsg;
    private String lastTime;
    private List<SipInfo> sipInfoList;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public List<SipInfo> getSipInfoList() {
        return sipInfoList;
    }

    public void setSipInfoList(List<SipInfo> sipInfoList) {
        this.sipInfoList = sipInfoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageSessionBean that = (MessageSessionBean) o;

        return sessionID.equals(that.sessionID);

    }

    @Override
    public int hashCode() {
        return sessionID.hashCode();
    }

    @Override
    public String toString() {
        return "MessageSessionBean{" +
                "sessionID='" + sessionID + '\'' +
                ", users='" + users + '\'' +
                ", image='" + image + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", sipInfoList=" + sipInfoList +
                '}';
    }
}
