package com.ennova.outscreen.bean;

import java.util.List;

/**
 * @作者 zhouchao
 * @日期 2019/4/3
 * @描述
 */
public class WeatherBean {

    /**
     * time : 2019-04-03 09:00:20
     * cityInfo : {"city":"天津市","cityId":"101030100","parent":"天津","updateTime":"08:23"}
     * date : 20190403
     * message : Success !
     * status : 200
     * data : {"shidu":"44%","pm25":29,"pm10":75,"quality":"良","wendu":"10","ganmao":"极少数敏感人群应减少户外活动","yesterday":{"date":"02","sunrise":"05:57","high":"高温 17.0℃","low":"低温 7.0℃","sunset":"18:35","aqi":55,"ymd":"2019-04-02","week":"星期二","fx":"东北风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},"forecast":[{"date":"03","sunrise":"05:56","high":"高温 21.0℃","low":"低温 11.0℃","sunset":"18:36","aqi":63,"ymd":"2019-04-03","week":"星期三","fx":"西南风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"04","sunrise":"05:54","high":"高温 27.0℃","low":"低温 10.0℃","sunset":"18:37","aqi":73,"ymd":"2019-04-04","week":"星期四","fx":"西南风","fl":"4-5级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"05","sunrise":"05:53","high":"高温 20.0℃","low":"低温 10.0℃","sunset":"18:38","aqi":50,"ymd":"2019-04-05","week":"星期五","fx":"东南风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"06","sunrise":"05:51","high":"高温 18.0℃","low":"低温 8.0℃","sunset":"18:39","aqi":80,"ymd":"2019-04-06","week":"星期六","fx":"东北风","fl":"4-5级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"07","sunrise":"05:50","high":"高温 16.0℃","low":"低温 5.0℃","sunset":"18:40","aqi":71,"ymd":"2019-04-07","week":"星期日","fx":"东风","fl":"4-5级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"08","sunrise":"05:48","high":"高温 17.0℃","low":"低温 8.0℃","sunset":"18:40","aqi":25,"ymd":"2019-04-08","week":"星期一","fx":"东风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"09","sunrise":"05:47","high":"高温 16.0℃","low":"低温 7.0℃","sunset":"18:41","ymd":"2019-04-09","week":"星期二","fx":"西风","fl":"<3级","type":"阴","notice":"不要被阴云遮挡住好心情"},{"date":"10","sunrise":"05:45","high":"高温 17.0℃","low":"低温 7.0℃","sunset":"18:42","ymd":"2019-04-10","week":"星期三","fx":"南风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"11","sunrise":"05:43","high":"高温 18.0℃","low":"低温 9.0℃","sunset":"18:43","ymd":"2019-04-11","week":"星期四","fx":"南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"12","sunrise":"05:42","high":"高温 20.0℃","low":"低温 10.0℃","sunset":"18:44","ymd":"2019-04-12","week":"星期五","fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"13","sunrise":"05:40","high":"高温 24.0℃","low":"低温 14.0℃","sunset":"18:45","ymd":"2019-04-13","week":"星期六","fx":"西南风","fl":"<3级","type":"小雨","notice":"雨虽小，注意保暖别感冒"},{"date":"14","sunrise":"05:39","high":"高温 27.0℃","low":"低温 16.0℃","sunset":"18:46","ymd":"2019-04-14","week":"星期日","fx":"东风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"15","sunrise":"05:37","high":"高温 27.0℃","low":"低温 17.0℃","sunset":"18:47","ymd":"2019-04-15","week":"星期一","fx":"东南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"16","sunrise":"05:36","high":"高温 21.0℃","low":"低温 11.0℃","sunset":"18:48","ymd":"2019-04-16","week":"星期二","fx":"东北风","fl":"5-6级","type":"阴","notice":"不要被阴云遮挡住好心情"},{"date":"17","sunrise":"05:35","high":"高温 16.0℃","low":"低温 10.0℃","sunset":"18:49","ymd":"2019-04-17","week":"星期三","fx":"北风","fl":"3-4级","type":"小雨","notice":"雨虽小，注意保暖别感冒"}]}
     */

    private String time;
    private CityInfoBean cityInfo;
    private String date;
    private String message;
    private int status;
    private DataBean data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CityInfoBean getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(CityInfoBean cityInfo) {
        this.cityInfo = cityInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class CityInfoBean {
        /**
         * city : 天津市
         * cityId : 101030100
         * parent : 天津
         * updateTime : 08:23
         */

        private String city;
        private String cityId;
        private String parent;
        private String updateTime;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class DataBean {
        /**
         * shidu : 44%
         * pm25 : 29
         * pm10 : 75
         * quality : 良
         * wendu : 10
         * ganmao : 极少数敏感人群应减少户外活动
         * yesterday : {"date":"02","sunrise":"05:57","high":"高温 17.0℃","low":"低温 7.0℃","sunset":"18:35","aqi":55,"ymd":"2019-04-02","week":"星期二","fx":"东北风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"}
         * forecast : [{"date":"03","sunrise":"05:56","high":"高温 21.0℃","low":"低温 11.0℃","sunset":"18:36","aqi":63,"ymd":"2019-04-03","week":"星期三","fx":"西南风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"04","sunrise":"05:54","high":"高温 27.0℃","low":"低温 10.0℃","sunset":"18:37","aqi":73,"ymd":"2019-04-04","week":"星期四","fx":"西南风","fl":"4-5级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"05","sunrise":"05:53","high":"高温 20.0℃","low":"低温 10.0℃","sunset":"18:38","aqi":50,"ymd":"2019-04-05","week":"星期五","fx":"东南风","fl":"3-4级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"06","sunrise":"05:51","high":"高温 18.0℃","low":"低温 8.0℃","sunset":"18:39","aqi":80,"ymd":"2019-04-06","week":"星期六","fx":"东北风","fl":"4-5级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"07","sunrise":"05:50","high":"高温 16.0℃","low":"低温 5.0℃","sunset":"18:40","aqi":71,"ymd":"2019-04-07","week":"星期日","fx":"东风","fl":"4-5级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"08","sunrise":"05:48","high":"高温 17.0℃","low":"低温 8.0℃","sunset":"18:40","aqi":25,"ymd":"2019-04-08","week":"星期一","fx":"东风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"09","sunrise":"05:47","high":"高温 16.0℃","low":"低温 7.0℃","sunset":"18:41","ymd":"2019-04-09","week":"星期二","fx":"西风","fl":"<3级","type":"阴","notice":"不要被阴云遮挡住好心情"},{"date":"10","sunrise":"05:45","high":"高温 17.0℃","low":"低温 7.0℃","sunset":"18:42","ymd":"2019-04-10","week":"星期三","fx":"南风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"11","sunrise":"05:43","high":"高温 18.0℃","low":"低温 9.0℃","sunset":"18:43","ymd":"2019-04-11","week":"星期四","fx":"南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"12","sunrise":"05:42","high":"高温 20.0℃","low":"低温 10.0℃","sunset":"18:44","ymd":"2019-04-12","week":"星期五","fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"13","sunrise":"05:40","high":"高温 24.0℃","low":"低温 14.0℃","sunset":"18:45","ymd":"2019-04-13","week":"星期六","fx":"西南风","fl":"<3级","type":"小雨","notice":"雨虽小，注意保暖别感冒"},{"date":"14","sunrise":"05:39","high":"高温 27.0℃","low":"低温 16.0℃","sunset":"18:46","ymd":"2019-04-14","week":"星期日","fx":"东风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"15","sunrise":"05:37","high":"高温 27.0℃","low":"低温 17.0℃","sunset":"18:47","ymd":"2019-04-15","week":"星期一","fx":"东南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"16","sunrise":"05:36","high":"高温 21.0℃","low":"低温 11.0℃","sunset":"18:48","ymd":"2019-04-16","week":"星期二","fx":"东北风","fl":"5-6级","type":"阴","notice":"不要被阴云遮挡住好心情"},{"date":"17","sunrise":"05:35","high":"高温 16.0℃","low":"低温 10.0℃","sunset":"18:49","ymd":"2019-04-17","week":"星期三","fx":"北风","fl":"3-4级","type":"小雨","notice":"雨虽小，注意保暖别感冒"}]
         */

        private String shidu;
        private int pm25;
        private int pm10;
        private String quality;
        private String wendu;
        private String ganmao;
        private YesterdayBean yesterday;
        private List<ForecastBean> forecast;

        public String getShidu() {
            return shidu;
        }

        public void setShidu(String shidu) {
            this.shidu = shidu;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }

        public int getPm10() {
            return pm10;
        }

        public void setPm10(int pm10) {
            this.pm10 = pm10;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public String getGanmao() {
            return ganmao;
        }

        public void setGanmao(String ganmao) {
            this.ganmao = ganmao;
        }

        public YesterdayBean getYesterday() {
            return yesterday;
        }

        public void setYesterday(YesterdayBean yesterday) {
            this.yesterday = yesterday;
        }

        public List<ForecastBean> getForecast() {
            return forecast;
        }

        public void setForecast(List<ForecastBean> forecast) {
            this.forecast = forecast;
        }

        public static class YesterdayBean {
            /**
             * date : 02
             * sunrise : 05:57
             * high : 高温 17.0℃
             * low : 低温 7.0℃
             * sunset : 18:35
             * aqi : 55
             * ymd : 2019-04-02
             * week : 星期二
             * fx : 东北风
             * fl : 3-4级
             * type : 晴
             * notice : 愿你拥有比阳光明媚的心情
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private int aqi;
            private String ymd;
            private String week;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getSunrise() {
                return sunrise;
            }

            public void setSunrise(String sunrise) {
                this.sunrise = sunrise;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getSunset() {
                return sunset;
            }

            public void setSunset(String sunset) {
                this.sunset = sunset;
            }

            public int getAqi() {
                return aqi;
            }

            public void setAqi(int aqi) {
                this.aqi = aqi;
            }

            public String getYmd() {
                return ymd;
            }

            public void setYmd(String ymd) {
                this.ymd = ymd;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }
        }

        public static class ForecastBean {
            /**
             * date : 03
             * sunrise : 05:56
             * high : 高温 21.0℃
             * low : 低温 11.0℃
             * sunset : 18:36
             * aqi : 63
             * ymd : 2019-04-03
             * week : 星期三
             * fx : 西南风
             * fl : 3-4级
             * type : 晴
             * notice : 愿你拥有比阳光明媚的心情
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private int aqi;
            private String ymd;
            private String week;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getSunrise() {
                return sunrise;
            }

            public void setSunrise(String sunrise) {
                this.sunrise = sunrise;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getSunset() {
                return sunset;
            }

            public void setSunset(String sunset) {
                this.sunset = sunset;
            }

            public int getAqi() {
                return aqi;
            }

            public void setAqi(int aqi) {
                this.aqi = aqi;
            }

            public String getYmd() {
                return ymd;
            }

            public void setYmd(String ymd) {
                this.ymd = ymd;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }
        }
    }
}
