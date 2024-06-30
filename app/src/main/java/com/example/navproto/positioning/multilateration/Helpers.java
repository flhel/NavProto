package com.example.navproto.positioning.multilateration;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Helpers {
    public static List<Point3D> testApLocations = new ArrayList<Point3D>(
            Arrays.asList(
                    new Point3D(3, 1, 3),
                    new Point3D(2, 1, 1),
                    new Point3D(3, 2, 1),
                    new Point3D(5, 1, 1)
            )
    );

    public static double[] testDistances = new double[]{2, 1, 1, 2}; // in Meters

    static void LogInsufficientAPs(int count,String TAG){
        if(count < 4){
            Log.d(TAG,"Not enough Reference Points for positioning!");
            Log.d(TAG,"Fallback to Hardcoded Points for Testing!");
        }
    }

    static void swap(Sphere[] spheres, int a, int b) {
        Sphere tmp = spheres[a];
        spheres[a] = spheres[b];
        spheres[b] = tmp;
    }

    // Make a Location Object out of the Result
    public static Location toLocation(Point3D point) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(point.x);
        location.setLongitude(point.y);
        /*
         The altitude of this location in meters above the WGS84 reference ellipsoid
         */
        location.setAltitude(point.z);
        return location;
    }
}
