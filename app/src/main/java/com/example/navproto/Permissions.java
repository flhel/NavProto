package com.example.navproto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//Ask / Check for required runtime Permissions
public class Permissions {
    public static void askPermissions(Context context) {
        String[] permissions = {Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN};

        int permissionCheck0 = ContextCompat.checkSelfPermission(context, permissions[0]);
        int permissionCheck1 = ContextCompat.checkSelfPermission(context, permissions[1]);
        int permissionCheck2 = ContextCompat.checkSelfPermission(context, permissions[2]);

        if (permissionCheck0 != PackageManager.PERMISSION_GRANTED
                || permissionCheck1 != PackageManager.PERMISSION_GRANTED
                || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, 12);
        }
    }

    public static boolean hasPermissions(Context context) {
        if ( ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ) {
            return true;
        }
        return false;
    }
}
