package com.example.navproto;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;

// Test class to get the Ping Time to a Router from an Android App (Application Layer)
public class NetworkPingTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = "NetworkPingTask";
    private long time;
    private String apIp;

    @Override
    protected Boolean doInBackground(String... params) {
        String ipAddress = params[0];
        apIp = params[0];
        int timeout = 3000;

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            long startTime = System.currentTimeMillis();
            boolean out = inetAddress.isReachable(timeout);
            time = System.currentTimeMillis() - startTime;
            return out;
        } catch (IOException e) {
            Log.e(TAG, "Error while pinging: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.d(TAG, "Host " + apIp + " is reachable. Ping: " + time);
        } else {
            Log.d(TAG, "Host is not reachable.");
        }
    }
}