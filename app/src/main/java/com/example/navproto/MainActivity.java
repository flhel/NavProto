package com.example.navproto;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.navproto.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

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
                wifiNetworkAdapter.setWifiNetworks();
                ArrayList<ScanResult> wifis = (ArrayList) wifiNetworkAdapter.getWifiNetworks();



                ArrayList<String> output = new ArrayList<String>();
                for (ScanResult res : wifis) {
                    String hasWifiRttStr= "No";
                    if(res.is80211mcResponder()){
                        hasWifiRttStr = "Yes";
                    }
                    Log.d(TAG, res.SSID + hasWifiRttStr);
                    output.add(res.SSID + " | Has Wifi RTT: " + hasWifiRttStr);
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, output);
                binding.wifiNetworkList.setAdapter(arrayAdapter);
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