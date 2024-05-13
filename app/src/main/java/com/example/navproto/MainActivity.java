package com.example.navproto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.rtt.RangingResult;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.navproto.databinding.ActivityMainBinding;
import com.example.navproto.multilateration.Multilateration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private Context context;

    WifiNetworkAdapter wifiNetworkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        //Ask for runtime Permissions
        Permissions.askPermissions(this);

        wifiNetworkAdapter = new WifiNetworkAdapter(this);


        binding.scanWifis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Test Ping (Not usable to measure distance due to execution time)
                //new NetworkPingTask().execute("192.168.178.1");
                //new NetworkPingTask().execute("192.168.178.25");

                // 1. Get APs from connected Network
                ArrayList<ScanResult> wifis = (ArrayList) wifiNetworkAdapter.getNetworkAccessPoints();

                //Test
                new Multilateration().findPosition(new ArrayList<RangingResult>());

                // 2. Get Networks
                //wifiNetworkAdapter.setWifiNetworks();
                //ArrayList<ScanResult> wifis = (ArrayList) wifiNetworkAdapter.getWifiNetworks();

                if(wifis != null){
                    ArrayList<String> output = new ArrayList<String>();
                    output.add("Network: " + wifis.get(0).SSID);
                    output.add("Access Points: ");
                    for (ScanResult res : wifis) {

                        String hasWifiRttStr= "No";
                        if(res.is80211mcResponder()){
                            hasWifiRttStr = "Yes";
                        }
                        //Log.d(TAG, res.SSID + hasWifiRttStr);
                        output.add(res.BSSID + " | Has Wifi RTT: " + hasWifiRttStr);
                    }

                    ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, output);
                    binding.wifiNetworkList.setAdapter(arrayAdapter);
                }
            }
        });

        binding.showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OpenStreetMap.class);
                startActivity(intent);
                finish();
            }
        });
    }
}