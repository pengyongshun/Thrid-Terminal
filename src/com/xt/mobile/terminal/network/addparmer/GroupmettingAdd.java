package com.xt.mobile.terminal.network.addparmer;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/20.
 */
public class GroupmettingAdd {
    private String groupName;//组名
    private String description;//描述
    private String schemeID;//方案ID
    private String isDefaultScheme;//是否默认显示方案
    private String needPassword;//是否需要密码

    private String password;//密码
    private String isMediaProcessing;//是否拼接会议  ture-拼接
    private List<MeetingUser> meetingUsers;//用户信息
    private List<MeetingDevice> meetingDevices;//设备信息

    public static class MeetingUser{
        private int index;//索引
        private String userID;//成员useId
        private String isSpectator;//是否为旁观者  ture--旁观者

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

        public String getIsSpectator() {
            return isSpectator;
        }

        public void setIsSpectator(String isSpectator) {
            this.isSpectator = isSpectator;
        }

        @Override
        public String toString() {
            return "MeetingUser{" +
                    "index=" + index +
                    ", userID='" + userID + '\'' +
                    ", isSpectator=" + isSpectator +
                    '}';
        }
    }


    public static class MeetingDevice{
        private int index;//索引
        private String deviceID;//设备id

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getDeviceID() {
            return deviceID;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        @Override
        public String toString() {
            return "MeetingDevice{" +
                    "index=" + index +
                    ", deviceID='" + deviceID + '\'' +
                    '}';
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<MeetingUser> getMeetingUsers() {
        return meetingUsers;
    }

    public void setMeetingUsers(List<MeetingUser> meetingUsers) {
        this.meetingUsers = meetingUsers;
    }

    public List<MeetingDevice> getMeetingDevices() {
        return meetingDevices;
    }

    public void setMeetingDevices(List<MeetingDevice> meetingDevices) {
        this.meetingDevices = meetingDevices;
    }

    public String getIsDefaultScheme() {
        return isDefaultScheme;
    }

    public void setIsDefaultScheme(String isDefaultScheme) {
        this.isDefaultScheme = isDefaultScheme;
    }

    public String getNeedPassword() {
        return needPassword;
    }

    public void setNeedPassword(String needPassword) {
        this.needPassword = needPassword;
    }

    public String getIsMediaProcessing() {
        return isMediaProcessing;
    }

    public void setIsMediaProcessing(String isMediaProcessing) {
        this.isMediaProcessing = isMediaProcessing;
    }

    @Override
    public String toString() {
        return "GroupmettingAdd{" +
                "groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                ", schemeID='" + schemeID + '\'' +
                ", isDefaultScheme=" + isDefaultScheme +
                ", needPassword=" + needPassword +
                ", password='" + password + '\'' +
                ", isMediaProcessing=" + isMediaProcessing +
                ", meetingUsers=" + meetingUsers +
                ", meetingDevices=" + meetingDevices +
                '}';
    }
}
