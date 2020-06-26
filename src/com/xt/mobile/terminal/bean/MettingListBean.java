package com.xt.mobile.terminal.bean;

import java.io.Serializable;

/**
 * Created by 彭永顺 on 2020/5/7.
 */
public class MettingListBean implements Serializable{
    private String mettingTitle;
    private String mettingID;
    private String kssj;
    private String jssj;
    private boolean isActived;

    public boolean isActived() {
        return isActived;
    }

    public void setActived(boolean actived) {
        isActived = actived;
    }

    public String getMettingID() {
        return mettingID;
    }

    public void setMettingID(String mettingID) {
        this.mettingID = mettingID;
    }

    public String getMettingTitle() {
        return mettingTitle;
    }

    public void setMettingTitle(String mettingTitle) {
        this.mettingTitle = mettingTitle;
    }

    public String getKssj() {
        return kssj;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    @Override
    public String toString() {
        return "MettingListBean{" +
                "mettingTitle='" + mettingTitle + '\'' +
                ", mettingID='" + mettingID + '\'' +
                ", kssj='" + kssj + '\'' +
                ", jssj='" + jssj + '\'' +
                ", isActived=" + isActived +
                '}';
    }
}
