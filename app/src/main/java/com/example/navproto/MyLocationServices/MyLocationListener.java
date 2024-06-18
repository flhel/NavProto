package com.example.navproto.MyLocationServices;

import android.location.Location;

import androidx.annotation.NonNull;

public interface MyLocationListener {
    public void onLocationChanged(@NonNull Location location);
}
