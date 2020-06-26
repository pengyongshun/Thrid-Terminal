package com.xt.mobile.terminal.bean;

/**
 * Created by 彭永顺 on 2020/5/26.
 */
public class MediaSettingBean {
    private String content;
    private boolean isSelet;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelet() {
        return isSelet;
    }

    public void setSelet(boolean selet) {
        isSelet = selet;
    }

    @Override
    public String toString() {
        return "MediaSettingBean{" +
                "content='" + content + '\'' +
                ", isSelet=" + isSelet +
                '}';
    }
}
