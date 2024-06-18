package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;


public class MyLocationProviderGPS implements IMyLocationProvider {

    Context context;
    LocationManager locationManager;
    MyLocationProviderGPS myLocationProvider;

    private static final String TAG = MyLocationProviderGPS.class.getSimpleName();

    private Location myLocation;
    private IMyLocationConsumer locationConsumer;
    LocationListener locationListenerGPS;

    public MyLocationProviderGPS(Context c){
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myLocationProvider = this;
    }

    public boolean updateLocation(){

        boolean gpsEnabled = false;

        //check gps and wifi availability
        try{
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}

        if(!gpsEnabled){
            Log.d(TAG,"GPS: Missing");
        }

        if(listenForGpsLocation()){
            return true;
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private boolean listenForGpsLocation() {
        try{
            // Define a listener that responds to gps location updates
            locationListenerGPS = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"GPS: Latitude, Longitude = " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
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
                locationManager.removeUpdates(locationListenerGPS);
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
