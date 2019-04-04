package com.ennova.outscreen.video;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import com.ennova.outscreen.MainActivity;

import cn.jzvd.JzvdStd;

/**
 * @作者 zhouchao
 * @日期 2019/4/1
 * @描述
 */
public class MyJzvdStd extends JzvdStd {
    public Context context;
    public MyJzvdStd(Context context) {
        super(context);
        this.context = context;
    }

    public MyJzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_PAGER_CHANGE);
        context.sendBroadcast(intent);//发送标准广播
    }
}
