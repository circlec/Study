package com.ennova.outscreen;

import android.app.Application;
import android.content.Context;
import com.danikula.videocache.HttpProxyCacheServer;
/**
 * @作者 zhouchao
 * @日期 2019/4/3
 * @描述
 */
public class MyApplication extends Application {


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

}
