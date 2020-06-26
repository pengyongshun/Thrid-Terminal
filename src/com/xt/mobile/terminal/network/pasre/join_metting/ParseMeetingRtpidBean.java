package com.xt.mobile.terminal.network.pasre.join_metting;

/**
 * Created by andy on 2020/5/31.
 */
public class ParseMeetingRtpidBean {

    /**
     * screenIndex : 0
     * resourceID : xiangdong
     * resourceType : User
     * videoRTPId : 2924
     * audioRTPId :
     */

    private String screenIndex;
    private String resourceID;
    private String resourceType;
    private String videoRTPId;
    private String audioRTPId;

    public String getScreenIndex() {
        return screenIndex;
    }

    public void setScreenIndex(String screenIndex) {
        this.screenIndex = screenIndex;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getVideoRTPId() {
        return videoRTPId;
    }

    public void setVideoRTPId(String videoRTPId) {
        this.videoRTPId = videoRTPId;
    }

    public String getAudioRTPId() {
        return audioRTPId;
    }

    public void setAudioRTPId(String audioRTPId) {
        this.audioRTPId = audioRTPId;
    }
}
