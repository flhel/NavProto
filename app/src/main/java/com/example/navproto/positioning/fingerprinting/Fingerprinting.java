package com.example.navproto.positioning.fingerprinting;

import android.bluetooth.le.ScanResult;

import com.example.navproto.positioning.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fingerprinting {

    static List<Fingerprint> fingerprints = new ArrayList<>();

    public static Fingerprint findFingerprint(List<ScanResult> scanResults){

        double minDifference = Double.MAX_VALUE;
        Fingerprint match = null;

        //Check all Fingerprints matching the closest to the Scan
        for(Fingerprint fingerprint: fingerprints){

            double difference = 0;

            for(ScanResult scanResult : scanResults){
                Beacon fBeacon = fingerprint.beacons.get(scanResult.getDevice().getAddress());
                if(fBeacon == null){
                    //Scanned beacon is not in the fingerprint -> this has to be considered in measuring the difference
                    //Adding 10 was working just fine
                    difference = difference + 10;
                } else {
                    difference = difference + Math.abs(fBeacon.getMeasuredRssi() - scanResult.getRssi());
                }
            }

            if(difference < minDifference) {
                minDifference = difference;
                match = fingerprint;
            }

        }

        return match;
    }

    public static void addTestFingerprints(){
        HashMap<String, Beacon> beacons;

        //Fingerprint 1 z1
        beacons = new HashMap<>();
        //beacons.put("4B:6A:14:DB:91:86", new Beacon("4B:6A:14:DB:91:86", 0, 0, 0, -98));
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, -85));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -70));
        beacons.put("88:C6:26:AC:F4:1F", new Beacon("88:C6:26:AC:F4:1F", 0, 0, 0, -70));
        fingerprints.add(new Fingerprint(50.940000, 7.020001, 3, beacons));

        //Fingerprint 2 bk
        beacons = new HashMap<>();
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, -90));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -78));
        beacons.put("1C:AF:4A:20:D2:55", new Beacon("1C:AF:4A:20:D2:55", 0, 0, 0, -95));
        beacons.put("88:C6:26:AC:F4:1F", new Beacon("88:C6:26:AC:F4:1F", 0, 0, 0, -55));
        fingerprints.add(new Fingerprint(50.940001, 7.020002, 3, beacons));

        //Fingerprint 3 z2
        beacons = new HashMap<>();
        //beacons.put("70:09:71:C7:F8:31", new Beacon("F8:B9:5A:C7:76:9B", 0, 0, 0, -100));
        beacons.put("1C:AF:4A:20:D2:55", new Beacon("1C:AF:4A:20:D2:55", 0, 0, 0, -90));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -80));
        beacons.put("88:C6:26:AC:F4:1F", new Beacon("88:C6:26:AC:F4:1F", 0, 0, 0, -70));
        fingerprints.add(new Fingerprint(50.940002, 7.020003, 3, beacons));

        //Fingerprint 4 kü
        /*
        beacons = new HashMap<>();
        beacons.put("70:09:71:C7:F8:31", new Beacon("F8:B9:5A:C7:76:9B", 0, 0, 0, -85));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -65));
        fingerprints.add(new Fingerprint(50.940890, 7.020325, 3, beacons));
         */


        //Fingerprint x
        /*
        beacons = new HashMap<>();
        beacons.put("F8:B9:5A:C7:76:9B", new Beacon("F8:B9:5A:C7:76:9B", 0, 0, 0, -70));
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, -98));
        beacons.put("4B:6A:14:DB:91:86", new Beacon("4B:6A:14:DB:91:86", 0, 0, 0, -96));
        beacons.put("54:DF:1B:4F:E6:DF", new Beacon("54:DF:1B:4F:E6:DF", 0, 0, 0, -80));
        beacons.put("90:32:4B:B7:D7:8C", new Beacon("90:32:4B:B7:D7:8C", 0, 0, 0, -88));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -78));
        fingerprints.add(new Fingerprint(50.940851, 7.020336, 3, beacons));
        */
    }
}
