package com.zc.study;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zc.study.network.HttpMethods;
import com.zc.study.network.testbean.TestCountResponse;
import com.zc.study.pager.PagersActivity;
import com.zc.study.tdswitch.TDSwitchActivity;
import com.zc.study.view.recents.RecentsAdapter;
import com.zc.study.view.recents.RecentsList;

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
            Intent intent = new Intent(MainActivity.this, PagersActivity.class);
            startActivity(intent);
        });
        getTestCount();

//        recentsView();
    }

    private void recentsView() {
        RecentsList recents =  findViewById(R.id.recents);
        recents.setAdapter(new RecentsAdapter() {
            @Override
            public String getTitle(int position) {
                return "Item " + position;
            }

            @Override
            public View getView(int position) {
                ImageView iv = new ImageView( MainActivity.this);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageResource(R.drawable.image1);
                iv.setBackgroundColor(0xffffffff);
                return iv;
            }

            @Override
            public Drawable getIcon(int position) {
                return getResources().getDrawable(R.mipmap.ic_launcher);
            }

            @Override
            public int getHeaderColor(int position) {
                return 0xffffffff;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        recents.setOnItemClickListener((view, i) -> Toast.makeText(view.getContext(), "Card " + i + " clicked", Toast.LENGTH_SHORT).show());
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
