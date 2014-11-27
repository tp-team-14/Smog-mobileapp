package com.vcelicky.smog.abs;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.vcelicky.smog.R;

/**
 * Created by Jerry on 10. 10. 2014.
 * Activity that all the other activities inherit from.
 */
public class BaseActivity extends Activity implements LocationListener {
    private static final String TAG = "BaseActivity";

    protected LocationManager mLocationManager;
    protected Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param context Context of calling activity.
     * @return true if WiFi is enabled and connected, otherwise false
     */
    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

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

    /**
     * Checks current connectivity status of device.
     *
     * @param context Context of calling activity.
     * @return true if WiFi or Mobile internet connection is established, false if not
     */
    public boolean isWifiOrMobileConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        boolean isWiFiOrMobile = false;
        if(isConnected) {
            //pri mobilnu siet by activeNetwork.getType() malo vracat 0, pre WiFi 1
            isWiFiOrMobile = (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    || (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        }
        return isWiFiOrMobile;
    }

    /**
     * Requests location update. Initializes LocationManager and current location.
     * If current location is not yet available, last known location is used.
     */
    public void requestLocationUpdate() {
        Log.d(TAG, "requestLocationUpdate() called");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed, longitude = " + location.getLongitude());
        mCurrentLocation.set(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "Status changed");

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
