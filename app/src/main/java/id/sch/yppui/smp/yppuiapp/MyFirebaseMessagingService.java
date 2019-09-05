package id.sch.yppui.smp.yppuiapp;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

/**
 * Created by ipin on 6/23/2018.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private SharedPreferences pref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        pref = getApplication().getSharedPreferences("Login", MODE_PRIVATE);

        if(pref.getBoolean(Constants.IS_LOGGED_IN, false) && !applicationInForeground()) {
            String notification_title = remoteMessage.getNotification().getTitle();
            String notification_message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String channelId = getString(R.string.default_notification_channel_id);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(notification_title)
                            .setContentText(notification_message)
                            .setSound(notification_sound)
                            .setAutoCancel(true);

            Intent resultIntent = new Intent(click_action);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            builder.setContentIntent(resultPendingIntent);

            int mNotificationId = (int) System.currentTimeMillis();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        notification_title,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(notification_sound, null);
                nm.createNotificationChannel(channel);
            }

            nm.notify(mNotificationId, builder.build());
        }
        else if(pref.getBoolean(Constants.IS_LOGGED_IN, false) && applicationInForeground()) {
            ((MainActivity) FromService.currentActivity()).cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "fragment");
        }
    }

    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(getPackageName())) {
            isActivityFound = true;
        }

        return isActivityFound;
    }
}
