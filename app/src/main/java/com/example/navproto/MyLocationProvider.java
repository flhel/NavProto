package com.example.navproto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class MyLocationProvider implements IMyLocationProvider {

    Context context;
    LocationManager locationManager;
    MyWifiRttManager myWifiRttManager;
    WifiNetworkAdapter wifiNetworkAdapter;
    MyLocationProvider myLocationProvider;

    private static final String TAG = MyLocationProvider.class.getSimpleName();

    private Location myLocation;
    private IMyLocationConsumer locationConsumer;

    //For Activation of different localisation Methods
    private boolean listenNetworkEnabled;
    private boolean listenGpsEnabled;
    private boolean listenWifiRttEnabled;

    public MyLocationProvider(Context c){
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myWifiRttManager = new MyWifiRttManager(context);
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        myLocationProvider = this;

        //For Activation of different localisation Methods
        listenNetworkEnabled = false;
        listenGpsEnabled = false;
        listenWifiRttEnabled = true;
    }

    /** Check if we can get our location */
    public boolean updateLocation(){

        boolean gps_enabled=false;
        boolean network_enabled=false;

        //check wifi
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Log.d(TAG,"Wifi connected");
        }

        //check gps and wifi availability
        try{
            gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}

        try{
            network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled){
            Log.d(TAG,"GPS: Missing");
        }
        if(!network_enabled){
            Log.d(TAG,"Network: Missing");
        }
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            Log.d(TAG,"Wifi RTT: Missing");
        }

        //TODO
        myWifiRttManager.checkEnabled();

        if(listenForNetworkLocation() || listenForGpsLocation() || listenForWifiRttLocation()){
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private boolean listenForNetworkLocation() {
        if(!listenNetworkEnabled){
            return false;
        }
        try{
            // Define a listener that responds to wifi location updates
            LocationListener locationListenerNetwork = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"NW: Latitude, Longitude =  " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {Log.d(TAG,"location found 1");}

                public void onProviderEnabled(String provider) {Log.d(TAG,"location found 2");}

                public void onProviderDisabled(String provider) {Log.d(TAG,"location found 3");}
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            return true;

        }catch(Exception e){
            Log.e(TAG, "Location Exception: " + e.getMessage());
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private boolean listenForGpsLocation() {
        if(!listenGpsEnabled){
            return false;
        }
        try{
            // Define a listener that responds to gps location updates
           LocationListener locationListenerGPS = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"GPS: Latitude, Longitude = " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {Log.d(TAG,"location found 1");}

                public void onProviderEnabled(String provider) {Log.d(TAG,"location found 2");}

                public void onProviderDisabled(String provider) {Log.d(TAG,"location found 3");}
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            return true;

        }catch(Exception e){
            Log.e(TAG, "Location Exception: " + e.getMessage());
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private boolean listenForWifiRttLocation() {
        if(!listenWifiRttEnabled){
            return false;
        }
        try{
            // Define a listener that responds to gps location updates
            LocationListener locationListenerWifiRtt = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"WifiRTT: Latitude, Longitude = " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {Log.d(TAG,"location found 1");}

                public void onProviderEnabled(String provider) {Log.d(TAG,"location found 2");}

                public void onProviderDisabled(String provider) {Log.d(TAG,"location found 3");}
            };

            wifiNetworkAdapter.setWifiNetworks();
            myWifiRttManager.requestLocationUpdates(wifiNetworkAdapter.getWifiNetworks());
            //locationManager.requestLocationUpdates(locationProviderWifiRtt, 0, 0, locationListenerWifiRtt);
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
        if (locationManager != null) {
            try {
                locationManager.removeUpdates((LocationListener) this);
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
