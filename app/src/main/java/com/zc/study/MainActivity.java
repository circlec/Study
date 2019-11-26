package com.zc.study;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.encdata.client.MessageHandler;
import com.encdata.client.SocketClient;
import com.zc.study.network.HttpMethods;
import com.zc.study.network.testbean.TestCountResponse;
import com.zc.study.pager.PagersActivity;
import com.zc.study.service.JobHandlerService;
import com.zc.study.tdswitch.TDSwitchActivity;
import com.zc.study.view.recents.RecentsAdapter;
import com.zc.study.view.recents.RecentsList;

import java.util.concurrent.TimeUnit;

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
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            startActivity(intent);
        });
        getTestCount();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doService();
        }
//        recentsView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                testSocket();
            }
        }).start();

    }
    SocketClient client;
    private void testSocket() {
        Log.i("Test", "testSocket");
        // 参数：主机IP、端口号、自定义的消息处理类
         client = new SocketClient("10.4.140.222",8090,new TestMessageHandler());

        String msg = "你好，服务器！";
        boolean rst = client.sendMessage(msg);

        if(rst){
            System.out.println("发送成功。");
            Log.i("Test", "testSocket: 发送成功。");
        }

    }
    public class TestMessageHandler implements MessageHandler {
        @Override
        public void onMessage(String message) {
            System.out.println(message);
            Log.i("Test", "message:"+message);
            client.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void doService() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, JobHandlerService.class));  //指定哪个JobService执行操作
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(10)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(15));  //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
        builder.setRequiresCharging(false); // 未充电状态
        jobScheduler.schedule(builder.build());
    }

    private void recentsView() {
        RecentsList recents = findViewById(R.id.recents);
        recents.setAdapter(new RecentsAdapter() {
            @Override
            public String getTitle(int position) {
                return "Item " + position;
            }

            @Override
            public View getView(int position) {
                ImageView iv = new ImageView(MainActivity.this);
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
