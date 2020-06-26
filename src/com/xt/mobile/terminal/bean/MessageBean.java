package com.xt.mobile.terminal.bean;

import java.io.Serializable;

/**
 * 即时消息的消息实体
 */
public class MessageBean implements Serializable{

    private String messageID;
    private String senderID;
    private String senderName;
    private String content;
    private String sendTime;
    private boolean isReaded;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageBean that = (MessageBean) o;

        return messageID.equals(that.messageID);

    }

    @Override
    public int hashCode() {
        return messageID.hashCode();
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "messageID='" + messageID + '\'' +
                ", senderID='" + senderID + '\'' +
                ", senderName='" + senderName + '\'' +
                ", content='" + content + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", isReaded=" + isReaded +
                '}';
    }
}
