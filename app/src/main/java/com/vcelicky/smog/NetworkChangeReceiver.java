package com.vcelicky.smog;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.support.v4.app.NotificationCompat;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.vcelicky.smog.activities.CameraActivity;
import com.vcelicky.smog.activities.AlreadyOnlineActivity;

/**
 * Created by jerry on 20. 10. 2014.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int networkType = intent.getExtras().getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
        boolean isWifi = networkType == ConnectivityManager.TYPE_WIFI;
        boolean isMobile = networkType == ConnectivityManager.TYPE_MOBILE;
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networkType);
        boolean isConnected = networkInfo.isConnected();

        if (isWifi) {
            if (isConnected) {
                //tu sa udeju zmeny po zapnuti WiFi, a v nasom pripade upload obrazka
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Uploadnúť fotky!")
                                .setContentText("Niektoré Vaše úlovky ešte neboli odoslané.")
                                .setAutoCancel(true); //po kliknuti na notifikaciu sa odstrani zo zoznamu notifikacii

                Intent notificationIntent = new Intent(context, AlreadyOnlineActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(CameraActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(notificationIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                //notificationManager.notify(0, n);
                notificationManager.notify(0, mBuilder.build());

                Log.i("APP_TAG", "Wi-Fi - CONNECTED");
            } else {
                Log.i("APP_TAG", "Wi-Fi - DISCONNECTED");
            }
        } else if (isMobile) {
            if (isConnected) {
                Log.i("APP_TAG", "Mobile - CONNECTED");
            } else {
                Log.i("APP_TAG", "Mobile - DISCONNECTED");
            }
        } else {
            if (isConnected) {
                Log.i("APP_TAG", networkInfo.getTypeName() + " - CONNECTED");
            } else {
                Log.i("APP_TAG", networkInfo.getTypeName() + " - DISCONNECTED");
            }
        }

    }

}
