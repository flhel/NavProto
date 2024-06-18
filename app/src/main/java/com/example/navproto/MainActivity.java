package com.example.navproto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.navproto.databinding.ActivityMainBinding;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private Context context;

    WifiNetworkAdapter wifiNetworkAdapter;

    GeoPoint myLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        //Ask for runtime Permissions
        Permissions.askPermissions(this);

        //Set default Location Method to GPS
        binding.rbGPS.toggle();

        //Move last known Location around
        myLastKnownLocation = null;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            myLastKnownLocation = bundle.getParcelable("last_known_location", GeoPoint.class);
        }

        // Initialize Access Point List
        setNetworkList();

        binding.scanWifis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Refresh Access Point List
                setNetworkList();
                // Test Ping (Not usable to measure distance due to execution time)
                //new NetworkPingTask().execute("192.168.178.1");
                //new NetworkPingTask().execute("192.168.178.25");

            }
        });

        binding.showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change Activity to OSM
                Intent intent = new Intent(MainActivity.this, OpenStreetMap.class);

                // Get the selected localisation method
                intent.putExtra("boolean_use_gps", binding.rbGPS.isChecked());
                intent.putExtra("boolean_use_network", binding.rbNetwork.isChecked());
                intent.putExtra("boolean_use_rtt", binding.rbRTT.isChecked());
                intent.putExtra("boolean_use_bluetooth", binding.rbBluetooth.isChecked());
                intent.putExtra("last_known_location", (Parcelable) myLastKnownLocation);

                startActivity(intent);
                finish();
            }
        });

    }
    private void setNetworkList() {
        wifiNetworkAdapter = new WifiNetworkAdapter(context);
        ArrayList<ScanResult> wifis = (ArrayList) wifiNetworkAdapter.getNetworkAccessPoints();
        if(wifis != null){
            ArrayAdapter arrayAdapter = getArrayAdapter(wifis);
            binding.wifiNetworkList.setAdapter(arrayAdapter);
        } else {
            binding.wifiNetworkList.setAdapter(null);
        }
    }

    @NonNull
    private ArrayAdapter getArrayAdapter(ArrayList<ScanResult> wifis) {
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
        return arrayAdapter;
    }
}