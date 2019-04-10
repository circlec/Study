package com.ennova.outscreen.bean;

/**
 * @作者 zhouchao
 * @日期 2019/4/10
 * @描述
 */
public class ShopDetail {

    private int code;
    private ShopDetailBean data;
    private String errorMsg;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ShopDetailBean getData() {
        return data;
    }

    public void setData(ShopDetailBean data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class ShopDetailBean {
        /**
         * id : 0
         * latitude : 0
         * longitude : 0
         * mobile : string
         * remark : string
         * shopImage : string
         * shopName : string
         */

        private int id;
        private double latitude;
        private double longitude;
        private String mobile;
        private String remark;
        private String shopImage;
        private String shopName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getShopImage() {
            return shopImage;
        }

        public void setShopImage(String shopImage) {
            this.shopImage = shopImage;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }
}
