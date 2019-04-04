package com.ennova.outscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennova.outscreen.bean.Weather;
import com.ennova.outscreen.network.ApiService;
import com.ennova.outscreen.utils.DateUtils;
import com.ennova.outscreen.video.MyFragmentPagerAdapter;
import com.ennova.outscreen.video.VideoFragment;
import com.ennova.outscreen.view.DotsLayout;
import com.ennova.outscreen.view.WeatherFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_PAGER_CHANGE = "ACTION.pager_change";

    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_today_weather)
    ImageView ivTodayWeather;
    @BindView(R.id.tv_today_temperature)
    TextView tvTodayTemperature;
    @BindView(R.id.tv_today_weather)
    TextView tvTodayWeather;
    @BindView(R.id.tv_today_wind)
    TextView tvTodayWind;
    WeatherFragment fg_first;
    WeatherFragment fg_second;
    WeatherFragment fg_third;
    WeatherFragment fg_fourth;
    Subscription subscribe_auto;

    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private Fragment fragment1, fragment2, fragment3;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private MyBrocastReceiver recevier;
    private DotsLayout dotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBorcastReceiver();
        initFragment();
        initTitleTime();
        initWeather();
        updateTime();
        initVideo();
    }

    private void initVideo() {
        viewPager = findViewById(R.id.vp_video);
        fragment1 = VideoFragment.newInstance("http://travel.enn.cn/group1/M00/00/0A/CiaAUlyAxreASu7hABnIV4_GV2I891.mp4");
        fragment2 = VideoFragment.newInstance("http://travel.enn.cn/group1/M00/00/0A/CiaAUlyAxbuAFwcBABOztqZJKFM900.mp4");
        fragment3 = VideoFragment.newInstance("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4");
        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        fragment_list.add(fragment3);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragment_list);
        viewPager.setAdapter(adapter);
        dotsLayout = findViewById(R.id.mydots);
        dotsLayout.setDot(0,fragment_list.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int index) {
                dotsLayout.setDot(index,fragment_list.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initBorcastReceiver() {
        recevier = new MyBrocastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAGER_CHANGE);
        registerReceiver(recevier, intentFilter);
    }

    private void updateTime() {
        subscribe_auto = Observable.interval(1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        initTitleTime();
                    }
                });
    }

    private void initWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://apis.juhe.cn/")
                .build();
        retrofit.create(ApiService.class).getWeather("北京", "f971c2219357f31d9a47e97332cec63f")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        if (weather != null && weather.getError_code() == 0) {
                            fillWeather(weather.getResult().getFuture());
                        }
                    }
                });
    }

    private void fillWeather(List<Weather.ResultBean.FutureBean> future) {
        tvTodayTemperature.setText(future.get(0).getTemperature());
        tvTodayWeather.setText(future.get(0).getWeather());
        tvTodayWind.setText(future.get(0).getDirect());
        fg_first.setWeather(future.get(1));
        fg_second.setWeather(future.get(2));
        fg_third.setWeather(future.get(3));
        fg_fourth.setWeather(future.get(4));
    }


    private void initTitleTime() {
        String time = DateUtils.getHHMM(System.currentTimeMillis()) + getAmPm();
        tvTime.setText(time);
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fg_first = (WeatherFragment) fragmentManager.findFragmentById(R.id.fg_first);
        fg_second = (WeatherFragment) fragmentManager.findFragmentById(R.id.fg_second);
        fg_third = (WeatherFragment) fragmentManager.findFragmentById(R.id.fg_third);
        fg_fourth = (WeatherFragment) fragmentManager.findFragmentById(R.id.fg_fourth);
    }

    private String getAmPm() {
        Calendar calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == 0) {
            return "上午";
        } else {
            return "下午";
        }
    }


    public class MyBrocastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (viewPager.getCurrentItem() != fragment_list.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                viewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe_auto != null && subscribe_auto.isUnsubscribed()) {
            subscribe_auto.unsubscribe();
        }
        unregisterReceiver(recevier);
    }

}
