package com.ennova.outscreen.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者 zhouchao
 * @日期 2019/4/9
 * @描述
 */
public class ActivityManager {
    private static List<Activity> activityList = new ArrayList<>();

    public static void add(Activity activity) {
        activityList.add(activity);
    }

    public static void remove(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishAllActivities() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    public static void finishNActivities(int count) {
        if (count > 0) {
            for (int i = activityList.size() - 1; i >= 0 && count > 0; i--) {
                activityList.get(i).finish();
                count--;
            }
        }
    }

    public static void finishTo(Class<? extends Activity> clazz) {
        for (int i = activityList.size() - 1; i >= 0; i--) {
            if (clazz != activityList.get(i).getClass()) {
                activityList.get(i).finish();
            }

        }
    }

    public static Activity getCurrActivity() {
        if (activityList.size() == 0) {
            return null;
        }
        return activityList.get(activityList.size() - 1);
    }

    public static Activity getActivityByName(String activityName) {
        if (activityList.size() == 0) {
            return null;
        }
        for(Activity activity:activityList){
            if (activityName.equals(activity.getClass().getSimpleName())){
                return activity;
            }
        }
        return null;
    }
}