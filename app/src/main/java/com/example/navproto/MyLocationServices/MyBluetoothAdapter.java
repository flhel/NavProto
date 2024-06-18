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

        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                //.setReportDelay((long) 3000.0)
                .build();

        callback  = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                //Check if its one of our btBeacons
                if(beacons.get(result.getDevice().getAddress()) != null ) {
                    for(int i = 0; i < btScanResults.size(); i++){
                        // Check for duplicate
                        if(btScanResults.get(i).getDevice().getAddress().equals(result.getDevice().getAddress())){
                            btScanResults.remove(i);
                            // After this add a fresh ScanResult for a newer measurement
                        }
                    }
                    // Add ScanResult
                    btScanResults.add(result);
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
        beacons = new HashMap<>();
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 50.928393, 6.928548, 3));
        beacons.put("6E:E7:CF:7A:D6:22", new Beacon("6E:E7:CF:7A:D6:22", 50.927977, 6.928806, 3));
        beacons.put("6C:04:08:66:09:65", new Beacon("6C:04:08:66:09:65", 50.928282, 6.929071, 3));
        beacons.put("43:33:76:4F:56:BC", new Beacon("43:33:76:4F:56:BC", 50.928419, 6.928843, 6));
    }

    //Bluetooth Beacon Class
    public class Beacon {
        private String address;
        private double lat;
        private double lng;
        private double alt;

        public Beacon(String address, double lat, double lng, double alt) {
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.alt = alt;
        }
    }
}
