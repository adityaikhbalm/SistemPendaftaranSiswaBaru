package id.sch.yppui.smp.yppuiapp;

import android.app.Application;

/**
 * Created by ipin on 4/28/2018.
 */

public class MyApplication extends Application {

    // Gloabal

    public static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;

    }

    public static void activityPaused() {
        activityVisible = false;

    }

}
