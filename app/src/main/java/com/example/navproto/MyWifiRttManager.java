package com.example.navproto;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.util.Log;

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

    public void checkEnabled(){
        IntentFilter filter =
                new IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED);
        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (wifiRttManager.isAvailable()) {
                    Log.d(TAG,"Wifi RTT: Active");
                } else {
                    Log.d(TAG,"Wifi RTT: Disabled");
                }
            }
        };
        context.registerReceiver(myReceiver, filter);
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(List<ScanResult> scanResults, MyLocationListener myLocationListener){

        //Check if everything is there
        if(wifiRttManager == null){
            //Test Code if Hardware is missing the Capabilities
            boolean forTesting = true;
            if(forTesting){
                myLocationListener.onLocationChanged(new Trilateration().findPosition(new ArrayList<RangingResult>()));
            }
            return;
        }
        if(scanResults == null){
            return;
        }

        RangingRequest request = new RangingRequest.Builder().addAccessPoints(scanResults).build();

        final RangingResultCallback callback = new RangingResultCallback() {
            @Override
            public void onRangingFailure(int code) {
                Log.d(TAG,"WiFi-Ranging failed: " + code);
            }
            @Override
            public void onRangingResults(List<RangingResult> results) {
                Log.d(TAG,"WiFi-Ranging success: " + results);
                if (myLocationListener != null) {
                    myLocationListener.onLocationChanged(new Trilateration().findPosition(results));
                }
            }
        };

        Log.d(TAG,"startRanging...");
        wifiRttManager.startRanging(request, mainExecutor, callback);
    }
}