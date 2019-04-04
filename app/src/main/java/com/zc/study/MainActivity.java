package com.zc.study;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zc.study.network.HttpMethods;
import com.zc.study.network.testbean.TestCountResponse;
import com.zc.study.pager.PagersActivity;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        tv.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, TextLayoutActivity.class);
//            startActivity(intent);
//            Intent intent =  new Intent(Settings.ACTION_SETTINGS);
//            startActivity(intent);
            Intent intent = new Intent(MainActivity.this, PagersActivity.class);
            startActivity(intent);
        });
        getTestCount();
    }

    private void getTestCount() {
        HttpMethods.getInstance().getTestCount(new Subscriber<TestCountResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TestCountResponse testCountResponse) {

            }
        });
    }
}
