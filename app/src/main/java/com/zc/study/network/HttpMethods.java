package com.zc.study.network;

import com.zc.study.network.testbean.TestCountResponse;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者 zhouchao
 * @日期 2019/3/27
 * @描述
 */
public class HttpMethods {

    private ApiServiceManager apiServiceManager;

    private HttpMethods() {
        apiServiceManager = ApiServiceManager.getInstance();
    }

    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void getTestCount(Subscriber<TestCountResponse> subscriber) {
        apiServiceManager.create(UserService.class).getTestCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
