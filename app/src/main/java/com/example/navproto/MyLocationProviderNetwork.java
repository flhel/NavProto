package com.example.navproto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class MyLocationProviderNetwork implements IMyLocationProvider {

    Context context;
    LocationManager locationManager;
    WifiNetworkAdapter wifiNetworkAdapter;
    MyLocationProviderNetwork myLocationProvider;

    private static final String TAG = MyLocationProviderNetwork.class.getSimpleName();

    private Location myLocation;
    private IMyLocationConsumer locationConsumer;
    LocationListener locationListenerNetwork;

    public MyLocationProviderNetwork(Context c){
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        myLocationProvider = this;
    }

    public boolean updateLocation(){

        boolean networkEnabled=false;

        //check wifi
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Log.d(TAG,"Wifi connected");
        }

        try{
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!networkEnabled){
            Log.d(TAG,"Network: Missing");
        }

        if(listenForNetworkLocation()){
            return true;
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private boolean listenForNetworkLocation() {
        try{
            // Define a listener that responds to wifi location updates
            locationListenerNetwork = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"NW: Latitude, Longitude =  " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
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
                locationManager.removeUpdates(locationListenerNetwork);
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
