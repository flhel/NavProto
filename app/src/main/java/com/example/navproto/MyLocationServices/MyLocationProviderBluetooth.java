package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class MyLocationProviderBluetooth implements IMyLocationProvider {
    private static final String TAG = "MyLocationProviderBluetooth";
    Context context;
    LocationManager locationManager;
    MyBluetoothAdapter myBluetoothAdapter;
    MyLocationProviderBluetooth myLocationProvider;

    private Location myLocation;
    private IMyLocationConsumer locationConsumer;
    MyLocationListener mylocationListener;

    public MyLocationProviderBluetooth(Context c){
        context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myBluetoothAdapter = new MyBluetoothAdapter();
        myLocationProvider = this;
    }

    @SuppressLint("MissingPermission")
    private boolean listenForBluetoothLocation() {
        try{
            // Define a listener that responds to bt location updates
            mylocationListener = new MyLocationListener() {
                public void onLocationChanged(@NonNull Location location) {
                    Log.d(TAG,"Bluetooth: Latitude, Longitude = " + location.getLatitude() + ", " + location.getLongitude());
                    myLocation = location;
                    locationConsumer.onLocationChanged(myLocation, myLocationProvider);
                }
            };

            myBluetoothAdapter.startScanning(mylocationListener);

            return true;

        }catch(Exception e){
            Log.e(TAG, "Location Exception: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
        locationConsumer = myLocationConsumer;
        return listenForBluetoothLocation();
    }

    @Override
    public void stopLocationProvider() {
        locationConsumer = null;
        if (myBluetoothAdapter != null) {
            myBluetoothAdapter.stopScanning();
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
