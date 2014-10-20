package com.vcelicky.smog;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

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
                Toast.makeText(context, "Wifi je zapnuta", Toast.LENGTH_SHORT).show();

                Notification n  = new Notification.Builder(context)
                        .setContentTitle("Hura, wifi").setContentText("Mehehehe, notifikacia")
                        .setSmallIcon(R.drawable.ic_launcher).getNotification();

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);

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
