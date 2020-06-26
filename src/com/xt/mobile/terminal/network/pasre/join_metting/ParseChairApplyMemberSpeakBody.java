package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/6/6.
 */
public class ParseChairApplyMemberSpeakBody {

    /**
     * params : {"buttons":[{"isClose":false,"text":"同意入会","command":{"params":{"isAgree":"true","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}},{"isClose":true,"text":"拒绝入会","command":{"params":{"isAgree":"false","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}}],"text":"收到彭永顺1的入会邀请","title":"入会申请消息"}
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
         * buttons : [{"isClose":false,"text":"同意入会","command":{"params":{"isAgree":"true","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}},{"isClose":true,"text":"拒绝入会","command":{"params":{"isAgree":"false","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}}]
         * text : 收到彭永顺1的入会邀请
         * title : 入会申请消息
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
             * isClose : false
             * text : 同意入会
             * command : {"params":{"isAgree":"true","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"},"funName":"publishAnswerApplyJoinFromConference"}
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
                 * params : {"isAgree":"true","sceneID":"a8ed4ca6-7e81-4895-b6a4-ca8138004375","applyUserID":"pengyongshun1"}
                 * funName : publishAnswerApplyJoinFromConference
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
                     * isAgree : true
                     * sceneID : a8ed4ca6-7e81-4895-b6a4-ca8138004375
                     * applyUserID : pengyongshun1
                     */

                    private String isAgree;
                    private String sceneID;
                    private String applyUserID;

                    public String getIsAgree() {
                        return isAgree;
                    }

                    public void setIsAgree(String isAgree) {
                        this.isAgree = isAgree;
                    }

                    public String getSceneID() {
                        return sceneID;
                    }

                    public void setSceneID(String sceneID) {
                        this.sceneID = sceneID;
                    }

                    public String getApplyUserID() {
                        return applyUserID;
                    }

                    public void setApplyUserID(String applyUserID) {
                        this.applyUserID = applyUserID;
                    }
                }
            }
        }
    }
}
