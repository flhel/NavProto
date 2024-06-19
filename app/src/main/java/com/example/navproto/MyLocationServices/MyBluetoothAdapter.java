package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.example.navproto.multilateration.Multilateration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBluetoothAdapter {
    private static final String TAG = "MyBluetoothAdapter";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback callback ;
    ScanSettings settings;
    private List<ScanResult> btScanResults = new ArrayList<>();

    private Map<String, Beacon> beacons;


    public MyBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Please activate Bluetooth
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        putMyBeacons();
    }

    @SuppressLint("MissingPermission")
    public void startScanning(MyLocationListener myLocationListener){

        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        List<Beacon> list = new ArrayList<Beacon>(beacons.values());

        for(Beacon beacon : list){
            filters.add(new ScanFilter.Builder().setDeviceAddress(beacon.address).build());
        }


        settings = new ScanSettings.Builder()
                //.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        callback  = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                //Check if its one of our btBeacons
                Beacon myBeacon = beacons.get(result.getDevice().getAddress());
                if(myBeacon != null) {
                    for(int i = 0; i < btScanResults.size(); i++){
                        // Check for duplicate
                        if(btScanResults.get(i).getDevice().getAddress().equals(result.getDevice().getAddress())){
                            btScanResults.remove(i);
                            // After this add a fresh ScanResult for a newer measurement
                        }
                    }
                    // Add ScanResult
                    btScanResults.add(result);

                    Log.d(TAG, "TxPowerLevel: " + myBeacon.measuredRssi + " RSSI" + result.getRssi());
                    double dist = Math.pow(10, (myBeacon.measuredRssi - result.getRssi()) / 20.0);
                    Log.d(TAG, "Distanz: " + dist);
                }

                // Give bundled List to the Multilateration-Algorithm
                if(btScanResults.size() >= 4) {
                    if (myLocationListener != null) {
                        myLocationListener.onLocationChanged(new Multilateration().findPositionBLE(btScanResults));
                    }
                    btScanResults = new ArrayList<>();
                }
            }
        };



        Log.i(TAG, "Scanning for Bluetooth Beacons");
        bluetoothLeScanner.startScan(filters, settings, callback);
    }

    @SuppressLint("MissingPermission")
    public void stopScanning(){
        bluetoothLeScanner.stopScan(callback);
    }

    // Add Hardcoded Beacon Locations to the Map
    private void putMyBeacons() {
        //TODO put real values here
        beacons = new HashMap<>();
        beacons.put("88:C6:26:AC:F4:1F", new Beacon("88:C6:26:AC:F4:1F", 50.928393, 6.928548, 3, -48.5));
        beacons.put("24:15:10:30:1B:FF", new Beacon("24:15:10:30:1B:FF", 50.927977, 6.928806, 3, -55));
        beacons.put("04:57:91:08:D5:2E", new Beacon("04:57:91:08:D5:2E", 50.928282, 6.929071, 3, -55));
        beacons.put("A4:08:01:CF:17:1E", new Beacon("A4:08:01:CF:17:1E", 50.928419, 6.928843, 3, -55));
    }

    //Bluetooth Beacon Class
    public class Beacon {
        private String address;
        private double lat;
        private double lng;
        private double alt;
        private double measuredRssi;

        public Beacon(String address, double lat, double lng, double alt, double measuredRssi) {
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.alt = alt;
            this.measuredRssi = measuredRssi;
        }
    }
}
