package com.xt.mobile.terminal.thridpart.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/14.
 * 空信调用aar包获取的参数
 */
public class EntranceXT implements Serializable{
    private String userID;//当前的useId
    private String meetingID;//会议id（创建者为空，非创建者有数据）
    private boolean isCreatMeeting;//是否为创建者 ture-创建者  false-成员
    private List<BroadcastName> broadcastNames;//用于向客户传递数据用的广播
    private String xt_ip;//aar需要的ip地址
    private String xt_port;//aar需要的端口号
    private List<InvitePeopleBean.ChoicePeople> contacts;//通讯录的信息
    private String meetingType;//会议类型
    private String chairmanID;//客户的主席ID
    private String timeOut;//超时时间

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getXt_ip() {
        return xt_ip;
    }

    public void setXt_ip(String xt_ip) {
        this.xt_ip = xt_ip;
    }

    public String getChairmanID() {
        return chairmanID;
    }

    public void setChairmanID(String chairmanID) {
        this.chairmanID = chairmanID;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public static class BroadcastName{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "BroadcastName{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }


    public boolean isCreatMeeting() {
        return isCreatMeeting;
    }

    public void setCreatMeeting(boolean creatMeeting) {
        isCreatMeeting = creatMeeting;
    }

    public List<BroadcastName> getBroadcastNames() {
        return broadcastNames;
    }

    public void setBroadcastNames(List<BroadcastName> broadcastNames) {
        this.broadcastNames = broadcastNames;
    }

    public String getXt_port() {
        return xt_port;
    }

    public void setXt_port(String xt_port) {
        this.xt_port = xt_port;
    }

    public List<InvitePeopleBean.ChoicePeople> getContacts() {
        return contacts;
    }

    public void setContacts(List<InvitePeopleBean.ChoicePeople> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "EntranceXT{" +
                "userID='" + userID + '\'' +
                ", meetingID='" + meetingID + '\'' +
                ", isCreatMeeting=" + isCreatMeeting +
                ", broadcastNames=" + broadcastNames +
                ", xt_ip='" + xt_ip + '\'' +
                ", xt_port='" + xt_port + '\'' +
                ", contacts=" + contacts +
                ", meetingType='" + meetingType + '\'' +
                ", chairmanID='" + chairmanID + '\'' +
                ", timeOut='" + timeOut + '\'' +
                '}';
    }
}
