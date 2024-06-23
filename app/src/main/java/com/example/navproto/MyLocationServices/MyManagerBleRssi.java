package com.example.navproto.MyLocationServices;

import static com.example.navproto.fingerprinting.fingerprinting.addTestFingerprints;
import static com.example.navproto.fingerprinting.fingerprinting.findFingerprint;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanResult;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.navproto.multilateration.Multilateration;
import com.example.navproto.fingerprinting.*;

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

        addTestFingerprints();
    }

    @SuppressLint("MissingPermission")
    public void startScanning(MyLocationListener myLocationListener){

        // For Testing
        if(false){
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
                .setScanMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setReportDelay(1500)
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

            @Override
            public void onBatchScanResults(List<ScanResult> resultsWithDup) {
                if(resultsWithDup == null || resultsWithDup.isEmpty()){
                    return;
                }

                // Remove duplicates
                Map<String,ScanResult> map = new HashMap<String,ScanResult>(resultsWithDup.size());
                for (ScanResult r : resultsWithDup) map.put(r.getDevice().getAddress(), r);
                List<ScanResult> results = new ArrayList<ScanResult>(map.values());

                // Check Fingerprints
                Log.d(TAG, "Fingerprint: ");
                for(ScanResult res : results){
                    Log.i(TAG, "" + res.getDevice().getAddress() + " " + res.getRssi());
                }

                Fingerprint fingerprint = findFingerprint(results);

                if(fingerprint != null){
                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(fingerprint.lat);
                    location.setLongitude(fingerprint.lng);
                    location.setAltitude(fingerprint.alt);

                    if (myLocationListener != null) {
                        myLocationListener.onLocationChanged(location);
                    }
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
       // beacons.put("88:C6:26:AC:F4:1F", new Beacon("88:C6:26:AC:F4:1F", 0, 0, 0, -48.5));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, 0));
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, 0));
        beacons.put("F8:B9:5A:C7:76:9B", new Beacon("F8:B9:5A:C7:76:9B", 0, 0, 0, 0));
        beacons.put("4B:6A:14:DB:91:86", new Beacon("4B:6A:14:DB:91:86", 0, 0, 0, 0));
        beacons.put("1C:AF:4A:20:D2:55", new Beacon("1C:AF:4A:20:D2:55", 0, 0, 0, 0));
    }
}
