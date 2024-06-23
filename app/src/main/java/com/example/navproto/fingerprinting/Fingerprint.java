package com.example.navproto.fingerprinting;

import java.util.HashMap;
import java.util.List;

public class Fingerprint {
    public double lat;
    public double lng;
    public double alt;
    HashMap<String, Beacon> beacons;

    public Fingerprint(double lat, double lng, double alt, HashMap<String, Beacon> beacons){
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
        this.beacons = beacons;
    }
}
