package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by 彭永顺 on 2020/6/9.
 */
public class ParseInitMobileMrdiaBody {


    /**
     * params : {"DecoderSIPID":"00010000123","EncoderSIPID":"00010000013","serverIP":"172.16.100.61","serverPort":"5071","serverID":"10001","clientPassword":"741426"}
     * userID : pengyongshun
     * funName : informInitMediaTerminal
     */

    private ParamsBean params;
    private String userID;
    private String funName;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
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

    public static class ParamsBean {
        /**
         * DecoderSIPID : 00010000123
         * EncoderSIPID : 00010000013
         * serverIP : 172.16.100.61
         * serverPort : 5071
         * serverID : 10001
         * clientPassword : 741426
         */

        private String DecoderSIPID;
        private String EncoderSIPID;
        private String serverIP;
        private String serverPort;
        private String serverID;
        private String clientPassword;
        private String encoderClientPassword;

        public String getEncoderClientPassword() {
            return encoderClientPassword;
        }

        public void setEncoderClientPassword(String encoderClientPassword) {
            this.encoderClientPassword = encoderClientPassword;
        }

        public String getDecoderSIPID() {
            return DecoderSIPID;
        }

        public void setDecoderSIPID(String DecoderSIPID) {
            this.DecoderSIPID = DecoderSIPID;
        }

        public String getEncoderSIPID() {
            return EncoderSIPID;
        }

        public void setEncoderSIPID(String EncoderSIPID) {
            this.EncoderSIPID = EncoderSIPID;
        }

        public String getServerIP() {
            return serverIP;
        }

        public void setServerIP(String serverIP) {
            this.serverIP = serverIP;
        }

        public String getServerPort() {
            return serverPort;
        }

        public void setServerPort(String serverPort) {
            this.serverPort = serverPort;
        }

        public String getServerID() {
            return serverID;
        }

        public void setServerID(String serverID) {
            this.serverID = serverID;
        }

        public String getClientPassword() {
            return clientPassword;
        }

        public void setClientPassword(String clientPassword) {
            this.clientPassword = clientPassword;
        }
    }
}
