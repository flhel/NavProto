package com.example.navproto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

        if (mWifi.isConnected()) {
            Log.d(TAG,"Wifi connected");
        }

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            Log.d(TAG,"Wifi RTT: Aviable");
        } else {
            Log.d(TAG,"Wifi RTT: Missing");
        }

        //TODO
        myWifiRttManager.checkEnabled();

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

            wifiNetworkAdapter.setWifiNetworks();
            myWifiRttManager.requestLocationUpdates(wifiNetworkAdapter.getWifiNetworks(), mylocationListener);
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
