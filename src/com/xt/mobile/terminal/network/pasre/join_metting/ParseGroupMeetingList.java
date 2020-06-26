package com.xt.mobile.terminal.network.pasre.join_metting;

import java.util.List;

/**
 * Created by 彭永顺 on 2020/5/22.
 */
public class ParseGroupMeetingList {

    /**
     * responseCode : 1
     * responseDesc :
     * data : {"totalCount":1,"list":[{"groupName":"嘻嘻嘻嘻","groupType":"Meeting","groupID":"82794694-47c9-38c6-92bb-0b2602192ecf","creatorID":"wangke","creatorName":"王克","description":"一下子在意我"}]}
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
         * totalCount : 1
         * list : [{"groupName":"嘻嘻嘻嘻","groupType":"Meeting","groupID":"82794694-47c9-38c6-92bb-0b2602192ecf","creatorID":"wangke","creatorName":"王克","description":"一下子在意我"}]
         */

        private int totalCount;
        private List<ListBean> list;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * groupName : 嘻嘻嘻嘻
             * groupType : Meeting
             * groupID : 82794694-47c9-38c6-92bb-0b2602192ecf
             * creatorID : wangke
             * creatorName : 王克
             * description : 一下子在意我
             */

            private String groupName;
            private String groupType;
            private String groupID;
            private String creatorID;
            private String creatorName;
            private String description;

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public String getGroupType() {
                return groupType;
            }

            public void setGroupType(String groupType) {
                this.groupType = groupType;
            }

            public String getGroupID() {
                return groupID;
            }

            public void setGroupID(String groupID) {
                this.groupID = groupID;
            }

            public String getCreatorID() {
                return creatorID;
            }

            public void setCreatorID(String creatorID) {
                this.creatorID = creatorID;
            }

            public String getCreatorName() {
                return creatorName;
            }

            public void setCreatorName(String creatorName) {
                this.creatorName = creatorName;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
