package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.util.Log;

import com.example.navproto.multilateration.Multilateration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MyWifiRttManager {
    private static final String TAG = "MyWifiRttManager";
    Context context;
    WifiRttManager wifiRttManager;
    Executor mainExecutor;

    @SuppressLint("ServiceCast")
    public MyWifiRttManager(Context context) {
        this.context = context;
        wifiRttManager = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        mainExecutor = context.getMainExecutor();
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(List<ScanResult> scanResults, MyLocationListener myLocationListener){

        //Check if everything is there
        if(wifiRttManager == null){
            // Test Code for Multilateration if Hardware is missing the Wifi RTT Capabilities
            boolean forTesting = false;
            if(forTesting){
                myLocationListener.onLocationChanged(
                        new Multilateration().findPositionRTT(new ArrayList<RangingResult>()));
            }
            return;
        }
        if(scanResults == null){
            return;
        }

        // Build the RangingRequest
        RangingRequest request = new RangingRequest.Builder().addAccessPoints(scanResults).build();

        // Build the Callback
        final RangingResultCallback callback = new RangingResultCallback() {
            @Override
            public void onRangingFailure(int code) {
                Log.d(TAG,"WiFi-Ranging failed: " + code);
            }
            @Override
            public void onRangingResults(List<RangingResult> results) {
                Log.d(TAG,"WiFi-Ranging success: " + results);
                if (myLocationListener != null) {
                    myLocationListener.onLocationChanged(new Multilateration().findPositionRTT(results));
                }
            }
        };

        // Start requesting Ranges
        Log.d(TAG,"startRanging...");
        wifiRttManager.startRanging(request, mainExecutor, callback);
    }

    public void removeUpdates( MyLocationListener myLocationListener){
        //TODO
        wifiRttManager = null;
    }
}