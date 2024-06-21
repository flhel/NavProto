package com.example.navproto.MyLocationServices;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.example.navproto.multilateration.Multilateration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyManagerBleRssi {
    private static final String TAG = "MyManagerBleRssi";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback callback ;
    ScanSettings settings;
    private List<ScanResult> btScanResults = new ArrayList<>();

    private Map<String, Beacon> beacons;

    public MyManagerBleRssi() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Please activate Bluetooth
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        putMyBeacons();
    }

    @SuppressLint("MissingPermission")
    public void startScanning(MyLocationListener myLocationListener){

        // For Testing
        if(true){
            myLocationListener.onLocationChanged(
                    new Multilateration().findPositionBLE(null, beacons, 1));
        }

        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        List<Beacon> beaconList = new ArrayList<Beacon>(beacons.values());

        for(Beacon beacon : beaconList){
            filters.add(new ScanFilter.Builder().setDeviceAddress(beacon.getAddress()).build());
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

                    /*
                    Log.d(TAG, "Measured Power Level: " + myBeacon.measuredRssi + " RSSI" + result.getRssi());
                    double dist = Math.pow(10, (myBeacon.measuredRssi - result.getRssi()) / 20.0);
                    Log.d(TAG, "Distanz: " + dist);
                    */
                }

                // Give bundled List to the Multilateration-Algorithm
                if(btScanResults.size() >= 4) {
                    if (myLocationListener != null) {
                        myLocationListener.onLocationChanged(
                                new Multilateration().findPositionBLE(btScanResults, beacons, 1));
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
        if(bluetoothLeScanner != null){
            bluetoothLeScanner.stopScan(callback);
        }
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
        public double lat;
        public double lng;
        public double alt;
        private double measuredRssi;

        public Beacon(String address, double lat, double lng, double alt, double measuredRssi) {
            this.address = address;
            this.lat = lat;
            this.lng = lng;
            this.alt = alt;
            this.measuredRssi = measuredRssi;
        }

        public double getMeasuredRssi() {
            return measuredRssi;
        }

        public String getAddress() {
            return address;
        }
    }
}
