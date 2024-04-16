package com.example.navproto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiNetworkAdapter {
    private final Context context;
    private WifiManager wifiManager;
    private List<ScanResult> wifiNetworks;

    WifiNetworkAdapter(final Context context) {
        this.context = context;
        wifiNetworks = null;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public List<ScanResult> getWifiNetworks() {
        return wifiNetworks;
    }

    @SuppressLint("MissingPermission")
    void setWifiNetworks() {
        List<ScanResult> wifiNetworks = wifiManager.getScanResults();
        if (wifiNetworks == null) {
            return;
        }
        final List<String> duplicates = new ArrayList<>(wifiNetworks.size());
        final List<ScanResult> filteredResults = new ArrayList<>(wifiNetworks.size());
        for (ScanResult result : wifiNetworks) {
            if (!duplicates.contains(result.SSID)) {
                duplicates.add(result.SSID);
                filteredResults.add(result);
            }
        }
        this.wifiNetworks = filteredResults;
    }
}