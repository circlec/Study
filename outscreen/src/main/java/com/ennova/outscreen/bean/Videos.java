package com.ennova.outscreen.bean;

import java.util.List;

/**
 * @作者 zhouchao
 * @日期 2019/4/10
 * @描述
 */
public class Videos {

    private int code;
    private DataBean data;
    private String msg;
    private String errorMsg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static class DataBean {
        /**
         * content : [{"id":4,"channelId":5,"channelName":"005","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0D/CiaAUlygPwGAQvCfAAM4IpzBet8374.jpg","status":1,"createTime":1554005767,"lastUpdateTime":1554007819},{"id":12,"channelId":7,"channelName":"00009","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0F/CiaAUlysRI6AQI7cAAcHN49w7FA426.jpg","status":0,"createTime":1554793617,"lastUpdateTime":1554793617},{"id":11,"channelId":5,"channelName":"005","contentType":"视频","contentPath":"http://travel.enn.cn/group1/M00/00/0F/CiaAUlysN3aARh3RAA_ngJ4S8TQ093.mp4","status":0,"createTime":1554790277,"lastUpdateTime":1554790277},{"id":10,"channelId":1,"channelName":"001","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0F/CiaAUlysNz-AHqqrAAt2gMpZ1KI996.jpg","status":0,"createTime":1554790216,"lastUpdateTime":1554790216},{"id":9,"channelId":5,"channelName":"005","contentType":"视频","contentPath":"http://travel.enn.cn/group1/M00/00/0D/CiaAUlygt-mAeB3yAA_ngJ4S8TQ734.mp4","status":0,"createTime":1554036718,"lastUpdateTime":1554036718},{"id":3,"channelId":1,"channelName":"001","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0D/CiaAUlygPr-AIZ8SAAJccSUS6U8888.jpg","status":0,"createTime":1554005701,"lastUpdateTime":1554005701},{"id":2,"channelId":4,"channelName":"004","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0D/CiaAUlygPnCAW3DtAAt2gMpZ1KI286.jpg","status":0,"createTime":1554005647,"lastUpdateTime":1554005647},{"id":8,"channelId":5,"channelName":"005","contentType":"长图","contentPath":"http://travel.enn.cn/group1/M00/00/0D/CiaAUlygQI6AEs4dAAt2gMpZ1KI643.jpg","status":-1,"createTime":1554006161,"lastUpdateTime":1554006445},{"id":7,"channelId":4,"channelName":"004","contentType":"长图","contentPath":"","status":-1,"createTime":1554005956,"lastUpdateTime":1554006857},{"id":6,"channelId":4,"channelName":"004","contentType":"长图","contentPath":"","status":-1,"createTime":1554005925,"lastUpdateTime":1554007247}]
         * totalPages : 2
         * totalElements : 12
         * last : false
         * first : true
         * numberOfElements : 10
         * sort : [{"direction":"DESC","property":"status","ignoreCase":false,"nullHandling":"NATIVE","descending":true,"ascending":false},{"direction":"DESC","property":"createTime","ignoreCase":false,"nullHandling":"NATIVE","descending":true,"ascending":false}]
         * size : 10
         * number : 0
         */

        private int totalPages;
        private int totalElements;
        private boolean last;
        private boolean first;
        private int numberOfElements;
        private int size;
        private int number;
        private List<ContentBean> content;
        private List<SortBean> sort;

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public List<SortBean> getSort() {
            return sort;
        }

        public void setSort(List<SortBean> sort) {
            this.sort = sort;
        }

        public static class ContentBean {
            /**
             * id : 4
             * channelId : 5
             * channelName : 005
             * contentType : 长图
             * contentPath : http://travel.enn.cn/group1/M00/00/0D/CiaAUlygPwGAQvCfAAM4IpzBet8374.jpg
             * status : 1
             * createTime : 1554005767
             * lastUpdateTime : 1554007819
             */

            private int id;
            private int channelId;
            private String channelName;
            private String contentType;
            private String contentPath;
            private int status;
            private int createTime;
            private int lastUpdateTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getChannelId() {
                return channelId;
            }

            public void setChannelId(int channelId) {
                this.channelId = channelId;
            }

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }

            public String getContentType() {
                return contentType;
            }

            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            public String getContentPath() {
                return contentPath;
            }

            public void setContentPath(String contentPath) {
                this.contentPath = contentPath;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getCreateTime() {
                return createTime;
            }

            public void setCreateTime(int createTime) {
                this.createTime = createTime;
            }

            public int getLastUpdateTime() {
                return lastUpdateTime;
            }

            public void setLastUpdateTime(int lastUpdateTime) {
                this.lastUpdateTime = lastUpdateTime;
            }
        }

        public static class SortBean {
            /**
             * direction : DESC
             * property : status
             * ignoreCase : false
             * nullHandling : NATIVE
             * descending : true
             * ascending : false
             */

            private String direction;
            private String property;
            private boolean ignoreCase;
            private String nullHandling;
            private boolean descending;
            private boolean ascending;

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getProperty() {
                return property;
            }

            public void setProperty(String property) {
                this.property = property;
            }

            public boolean isIgnoreCase() {
                return ignoreCase;
            }

            public void setIgnoreCase(boolean ignoreCase) {
                this.ignoreCase = ignoreCase;
            }

            public String getNullHandling() {
                return nullHandling;
            }

            public void setNullHandling(String nullHandling) {
                this.nullHandling = nullHandling;
            }

            public boolean isDescending() {
                return descending;
            }

            public void setDescending(boolean descending) {
                this.descending = descending;
            }

            public boolean isAscending() {
                return ascending;
            }

            public void setAscending(boolean ascending) {
                this.ascending = ascending;
            }
        }
    }
}
