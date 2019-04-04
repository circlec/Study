package com.zc.study;

import android.content.Context;

/**
 * @作者 zhouchao
 * @日期 2019/3/29
 * @描述
 */
public class Utils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
