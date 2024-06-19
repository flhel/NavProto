package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.navproto.WifiNetworkAdapter;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class MyLocationProviderWifiRTT implements IMyLocationProvider {

    Context context;
    LocationManager locationManager;
    MyWifiRttManager myWifiRttManager;
    WifiNetworkAdapter wifiNetworkAdapter;
    MyLocationProviderWifiRTT myLocationProvider;

    private static final String TAG = MyLocationProviderWifiRTT.class.getSimpleName();

    private Location myLocation;
    private IMyLocationConsumer locationConsumer;
    MyLocationListener mylocationListener;

    public MyLocationProviderWifiRTT(Context c){
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myWifiRttManager = new MyWifiRttManager(context);
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        myLocationProvider = this;
    }

    public boolean updateLocation(){

        //check wifi
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected()) {
            Log.i(TAG,"Wifi: Connected");
        } else {
            Log.e(TAG,"Wifi: Not connected");
            //return false;
        }

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            Log.i(TAG,"Wifi RTT: Available");
        } else {
            Log.e(TAG,"Wifi RTT: Unavailable on Device");
            //return false;
        }

        if(listenForWifiRttLocation()){
            return true;
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private boolean listenForWifiRttLocation() {
        try{
            // Define a listener that responds to rtt location updates
            mylocationListener = new MyLocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"WifiRTT: Latitude, Longitude = " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }
            };

            myWifiRttManager.requestLocationUpdates(wifiNetworkAdapter.getNetworkAccessPoints(), mylocationListener);
            return true;

        }catch(Exception e){
            Log.e(TAG, "Location Exception: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
        locationConsumer = myLocationConsumer;
        return updateLocation();
    }

    @Override
    public void stopLocationProvider() {
        locationConsumer = null;
        if (myWifiRttManager != null) {
            try {
                myWifiRttManager.removeUpdates(mylocationListener);
            } catch (Throwable ex) {
                Log.w(IMapView.LOGTAG, "Unable to deattach location listener", ex);
            }
        }
    }

    @Override
    public Location getLastKnownLocation() {
        return myLocation;
    }

    @Override
    public void destroy() {
        stopLocationProvider();
        myLocation = null;
        locationManager = null;
        locationConsumer = null;
    }
}
