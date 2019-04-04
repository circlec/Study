package com.ennova.outscreen.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ennova.outscreen.R;
import com.ennova.outscreen.bean.Weather;
import com.ennova.outscreen.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeatherFragment extends Fragment {

    @BindView(R.id.tv_today_weak)
    TextView tvTodayWeak;
    @BindView(R.id.tv_today_date)
    TextView tvTodayDate;
    @BindView(R.id.iv_today_weather)
    ImageView ivTodayWeather;
    @BindView(R.id.tv_today_temperature)
    TextView tvTodayTemperature;
    @BindView(R.id.tv_today_weather)
    TextView tvTodayWeather;
    @BindView(R.id.tv_today_wind)
    TextView tvTodayWind;
    Unbinder unbinder;

    public WeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void setWeather(Weather.ResultBean.FutureBean weathere) {
        tvTodayTemperature.setText(weathere.getTemperature());
        tvTodayWeather.setText(weathere.getWeather());
        tvTodayWind.setText(weathere.getDirect());
        tvTodayDate.setText(DateUtils.getMDFromStringDate(weathere.getDate()));
        tvTodayWeak.setText(DateUtils.getWeekFromStringDate(weathere.getDate()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
