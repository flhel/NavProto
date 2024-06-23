package com.example.navproto.fingerprinting;

import android.bluetooth.le.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fingerprinting {

    static List<Fingerprint> fingerprints = new ArrayList<>();

    public static Fingerprint findFingerprint(List<ScanResult> scanResults){

        List<Fingerprint> possibleMatches = new ArrayList<>();

        for(Fingerprint fingerprint: fingerprints){

            boolean possibleMatch = true;

            // strictly match all devices
            boolean strict = true;
            if(strict && fingerprint.beacons.size() != scanResults.size()) {
                possibleMatch = false;
                continue;
            }

            //List<Beacon> fBeacons = new ArrayList<>();

            for(ScanResult scanResult : scanResults){
                Beacon fBeacon = fingerprint.beacons.get(scanResult.getDevice().getAddress());
                if(fBeacon == null){
                    // Fingerprint does not contain this beacon, check next fingerprint
                    possibleMatch = false;
                    break;
                } else {
                    //fBeacons.add(fBeacon);
                    if(fBeacon.getMeasuredRssi() == scanResult.getRssi()
                            || fBeacon.getMeasuredRssi() > scanResult.getRssi() - 5
                            || fBeacon.getMeasuredRssi() < scanResult.getRssi() + 5) {
                        // Is within margin of error to match the fingerprint
                    } else {
                        // Does not match finger print, check next fingerprint
                        possibleMatch = false;
                        break;
                    }
                }
            }

            if(possibleMatch){
                possibleMatches.add(fingerprint);
            }
        }

        //TODO find most fitting Fingerprint
        if(possibleMatches.isEmpty()){
            return null;
        }
        return possibleMatches.get(0);
    }

    public static void addTestFingerprints(){
        HashMap<String, Beacon> beacons;

        //Fingerprint 1 z1
        beacons = new HashMap<>();
        //beacons.put("4B:6A:14:DB:91:86", new Beacon("4B:6A:14:DB:91:86", 0, 0, 0, -98));
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, -85));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -70));
        fingerprints.add(new Fingerprint(50.1, 7.1, 3, beacons));

        //Fingerprint 2 bk
        beacons = new HashMap<>();
        beacons.put("70:09:71:C7:F8:31", new Beacon("70:09:71:C7:F8:31", 0, 0, 0, -90));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -78));
        beacons.put("1C:AF:4A:20:D2:55", new Beacon("1C:AF:4A:20:D2:55", 0, 0, 0, -95));
        fingerprints.add(new Fingerprint(50.2, 7.1, 3, beacons));

        //Fingerprint 3 z2
        beacons = new HashMap<>();
        //beacons.put("70:09:71:C7:F8:31", new Beacon("F8:B9:5A:C7:76:9B", 0, 0, 0, -100));
        beacons.put("1C:AF:4A:20:D2:55", new Beacon("1C:AF:4A:20:D2:55", 0, 0, 0, -90));
        beacons.put("CC:B1:1A:E1:17:D5", new Beacon("CC:B1:1A:E1:17:D5", 0, 0, 0, -80));
        fingerprints.add(new Fingerprint(50.3, 7.1, 3, beacons));

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
