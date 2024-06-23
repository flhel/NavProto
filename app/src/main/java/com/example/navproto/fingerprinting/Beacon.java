package com.example.navproto.fingerprinting;

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