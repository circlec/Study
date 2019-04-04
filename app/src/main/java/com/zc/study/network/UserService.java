package com.zc.study.network;


import com.zc.study.network.testbean.TestCountResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @作者 zhouchao
 * @日期 2019/3/22
 * @描述
 */
public interface UserService {

    @GET("api/demo/GetDemo")
    Observable<TestCountResponse> getTestCount();

}
