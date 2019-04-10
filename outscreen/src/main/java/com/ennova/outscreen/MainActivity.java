package com.ennova.outscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennova.outscreen.activity.MapActivity;
import com.ennova.outscreen.bean.Videos;
import com.ennova.outscreen.bean.Weather;
import com.ennova.outscreen.network.ApiService;
import com.ennova.outscreen.network.HttpMethods;
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
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    public static final String ACTION_PAGER_CHANGE = "ACTION.PAGER_CHANGE";

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
    @BindView(R.id.vp_video)
    ViewPager viewPager;

    private ArrayList<Fragment> fragment_list = new ArrayList<>();
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
        showTitleTime();
        initWeather();
        updateTime();
        initVideo();
    }

    private void initVideo() {
        HttpMethods.getInstance().getVideos(new Subscriber<Videos>(){

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast("network error");
            }

            @Override
            public void onNext(Videos videos) {
                if(videos!=null && videos.getCode()==0&&videos.getData()!=null&&videos.getData().getContent()!=null&&videos.getData().getContent().size()>0){
                    for(int i=0;i<videos.getData().getContent().size();i++){
                        if(!TextUtils.isEmpty(videos.getData().getContent().get(i).getContentPath())){
                            Fragment fragment = VideoFragment.newInstance(videos.getData().getContent().get(i).getContentPath());
                            fragment_list.add(fragment);
                        }
                    }
                }
                adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragment_list);
                viewPager.setAdapter(adapter);
                dotsLayout = findViewById(R.id.mydots);
                dotsLayout.setDot(0, fragment_list.size());
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int index) {
                        dotsLayout.setDot(index, fragment_list.size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
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
                        toast("network error");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        showTitleTime();
                    }
                });
    }

    private void initWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://apis.juhe.cn/")
                .build();
        retrofit.create(ApiService.class).getWeather("鹰潭", "f971c2219357f31d9a47e97332cec63f")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("network error");
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


    private void showTitleTime() {
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

    @OnClick({R.id.iv_map, R.id.iv_product, R.id.iv_food})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_map:
                Intent intent_map = new Intent(this, MapActivity.class);
                startActivity(intent_map);
                break;
            case R.id.iv_product:
                Intent intent_product = new Intent(this, MapActivity.class);
                startActivity(intent_product);
                break;
            case R.id.iv_food:
                Intent intent_food = new Intent(this, MapActivity.class);
                startActivity(intent_food);
                break;
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
