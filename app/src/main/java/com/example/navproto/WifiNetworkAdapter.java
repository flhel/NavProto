package com.example.navproto;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WifiNetworkAdapter {
    private final Context context;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private String ssid = "";
    private String bssid = "";

    public WifiNetworkAdapter(final Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            ssid = ssid.replace("\"","");

            bssid = wifiInfo.getBSSID();
            bssid = ssid.replace("\"","");
        }
    }

    @SuppressLint("MissingPermission")
    // Get all currently available networks
    public List<ScanResult> getWifiNetworks() {
        List<ScanResult> wifiNetworks = wifiManager.getScanResults();
        if (wifiNetworks == null) {
            return null;
        }
        final List<String> duplicates = new ArrayList<>(wifiNetworks.size());
        final List<ScanResult> filteredResults = new ArrayList<>(wifiNetworks.size());
        for (ScanResult result : wifiNetworks) {

            if (!duplicates.contains(result.SSID)) {
                duplicates.add(result.SSID);
                filteredResults.add(result);
            }
        }
        wifiNetworks = filteredResults;
        return wifiNetworks;
    }

    @SuppressLint("MissingPermission")
    // Get all the Access Points available in the currently connected wifi network
    public List<ScanResult> getNetworkAccessPoints(){
        if(ssid == null){
            //Not connected to a Network
            return null;
        }
        List<ScanResult> wifiAccessPoints = new ArrayList<ScanResult>();
        List<ScanResult> wifiScanResults = wifiManager.getScanResults();
        if (wifiScanResults == null) {
            return null;
        }

        for (ScanResult result : wifiScanResults) {
            if (ssid.equals(result.SSID)) {
                wifiAccessPoints.add(result);
            }
        }
        return wifiAccessPoints;
    }
}