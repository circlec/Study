package com.ennova.outscreen.network;


import com.ennova.outscreen.bean.Points;
import com.ennova.outscreen.bean.Weather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @作者 zhouchao
 * @日期 2019/4/3
 * @描述
 */
public interface ApiService {

    @GET("simpleWeather/query")
    Observable<Weather> getWeather(@Query("city") String city, @Query("key") String key);

    @GET("h5/shop/map/index")
    Observable<Points> getPoints(@Query("shopType") String shopType);
}
