package com.xt.mobile.terminal.bean;

/**
 * Created by 彭永顺 on 2020/6/19.
 */
public class QueryBindDeviceBean {

    /**
     * responseCode : 1
     * responseDesc : success
     * data : {"callItem":"MANUAL","encoderItemName":"1592316113820TerminalEncoder","encoderItem":"88b1b09f-bb36-4c60-a5be-c48290cbe15b","userID":"1592316113918","meetingItem":"MANUAL"}
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
         * callItem : MANUAL
         * encoderItemName : 1592316113820TerminalEncoder
         * encoderItem : 88b1b09f-bb36-4c60-a5be-c48290cbe15b
         * userID : 1592316113918
         * meetingItem : MANUAL
         */

        private String callItem;
        private String encoderItemName;
        private String encoderItem;
        private String userID;
        private String meetingItem;

        public String getCallItem() {
            return callItem;
        }

        public void setCallItem(String callItem) {
            this.callItem = callItem;
        }

        public String getEncoderItemName() {
            return encoderItemName;
        }

        public void setEncoderItemName(String encoderItemName) {
            this.encoderItemName = encoderItemName;
        }

        public String getEncoderItem() {
            return encoderItem;
        }

        public void setEncoderItem(String encoderItem) {
            this.encoderItem = encoderItem;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getMeetingItem() {
            return meetingItem;
        }

        public void setMeetingItem(String meetingItem) {
            this.meetingItem = meetingItem;
        }
    }
}
