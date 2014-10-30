package com.vcelicky.smog.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jerry on 30. 10. 2014.
 */
public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    /**
     *
     * @param context Application context. Important to pass this argument in order to get reference
     *                to the context from outside of this static class.
     * @return TRUE if WiFi is enabled and connected, otherwise FALSE.
     */
    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        boolean isWiFi = false;
        if(isConnected) {
            //pri WiFi by activeNetwork.getType() malo vracat 1
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
//        Log.d(TAG, "isWifi = " + isWiFi);
        return isWiFi;
    }
}