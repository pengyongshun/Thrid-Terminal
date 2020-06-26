package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/4.
 */
public class ParseMeetingApplySpeaskBody {

    /**
     * params : {"buttons":[{"isClose":true,"text":"同意发言申请","command":{"params":{"sceneID":"ddd523e0-ca7e-408c-b6e6-a46bf1029e83","memberID":"pengyongshun1"},"funName":"publishAcceptSpeakerFromConference"}},{"isClose":true,"text":"拒绝发言申请","command":{"params":{}}}],"text":"收到彭永顺1发言申请","title":"申请发言消息"}
     * userID : pengyongshun2
     * funName : informShowMessage
     */

    private ParamsBeanX params;
    private String userID;
    private String funName;

    public ParamsBeanX getParams() {
        return params;
    }

    public void setParams(ParamsBeanX params) {
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

    public static class ParamsBeanX {
        /**
         * buttons : [{"isClose":true,"text":"同意发言申请","command":{"params":{"sceneID":"ddd523e0-ca7e-408c-b6e6-a46bf1029e83","memberID":"pengyongshun1"},"funName":"publishAcceptSpeakerFromConference"}},{"isClose":true,"text":"拒绝发言申请","command":{"params":{}}}]
         * text : 收到彭永顺1发言申请
         * title : 申请发言消息
         */

        private String text;
        private String title;
        private List<ButtonsBean> buttons;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<ButtonsBean> getButtons() {
            return buttons;
        }

        public void setButtons(List<ButtonsBean> buttons) {
            this.buttons = buttons;
        }

        public static class ButtonsBean {
            /**
             * isClose : true
             * text : 同意发言申请
             * command : {"params":{"sceneID":"ddd523e0-ca7e-408c-b6e6-a46bf1029e83","memberID":"pengyongshun1"},"funName":"publishAcceptSpeakerFromConference"}
             */

            private boolean isClose;
            private String text;
            private CommandBean command;

            public boolean isIsClose() {
                return isClose;
            }

            public void setIsClose(boolean isClose) {
                this.isClose = isClose;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public CommandBean getCommand() {
                return command;
            }

            public void setCommand(CommandBean command) {
                this.command = command;
            }

            public static class CommandBean {
                /**
                 * params : {"sceneID":"ddd523e0-ca7e-408c-b6e6-a46bf1029e83","memberID":"pengyongshun1"}
                 * funName : publishAcceptSpeakerFromConference
                 */

                private ParamsBean params;
                private String funName;

                public ParamsBean getParams() {
                    return params;
                }

                public void setParams(ParamsBean params) {
                    this.params = params;
                }

                public String getFunName() {
                    return funName;
                }

                public void setFunName(String funName) {
                    this.funName = funName;
                }

                public static class ParamsBean {
                    /**
                     * sceneID : ddd523e0-ca7e-408c-b6e6-a46bf1029e83
                     * memberID : pengyongshun1
                     */

                    private String sceneID;
                    private String memberID;

                    public String getSceneID() {
                        return sceneID;
                    }

                    public void setSceneID(String sceneID) {
                        this.sceneID = sceneID;
                    }

                    public String getMemberID() {
                        return memberID;
                    }

                    public void setMemberID(String memberID) {
                        this.memberID = memberID;
                    }
                }
            }
        }
    }
}
