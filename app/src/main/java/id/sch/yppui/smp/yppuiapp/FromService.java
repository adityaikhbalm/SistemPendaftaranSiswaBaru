package id.sch.yppui.smp.yppuiapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by ipin on 6/26/2018.
 */

public class FromService extends Application {
    private static Context context;
    private static Activity activity;

    public void onCreate() {
        super.onCreate();
        FromService.context = getApplicationContext();
    }

    public synchronized static Context getAppContext() {
        return FromService.context;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        activity = currentActivity;
    }

    public static Activity currentActivity() {
        return activity;
    }
}
