package com.zc.study.service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * @作者 zhouchao
 * @日期 2019/6/6
 * @描述
 */
@SuppressLint("NewApi")
public class JobHandlerService extends JobService {
    private JobScheduler mJobScheduler;

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(startId++,
                    new ComponentName(getPackageName(), JobHandlerService.class.getName()));

            builder.setPeriodic((1000 ));//设置间隔时间

            builder.setRequiresCharging(true);// 设置是否充电的条件,默认false

            builder.setRequiresDeviceIdle(true);// 设置手机是否空闲的条件,默认false

            builder.setPersisted(true);//设备重启之后你的任务是否还要继续执行

            if (mJobScheduler.schedule(builder.build()) <= 0) {
                Log.e("Test", "JobHandlerService 工作失败");
            } else {
                Log.e("Test", "JobHandlerService 工作成功");
            }
        }
        return START_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("Test", "onStartJob: ");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("Test", "onStopJob: ");
        return false;
    }


}

