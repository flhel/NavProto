package com.example.navproto;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.rtt.RangingResult;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Trilateration {
    //Theoretical Position of the Wifi Access Points
    private final LatLng ap1Location = new LatLng(0.1111, 0.1111);
    private final LatLng ap2Location = new LatLng(0.2222, 0.2222);
    private final LatLng ap3Location = new LatLng(0.3333, 0.3333);

    public Location findPosition(List<RangingResult> results){
        if(results.size() < 3){
            Log.d(TAG,"Not enough Reference Points for positioning!");
            return null;
        } else {
            //Calculation between 3 Points has to be enough, probably wont even get 3 APs to test the Algorithm

            Log.d(TAG,"Finally doing the magic!!!");

            LatLng position = calculatePosition(ap1Location, ap2Location, ap3Location,
                    results.get(0).getDistanceMm(),
                    results.get(1).getDistanceMm(),
                    results.get(2).getDistanceMm());

            // Make a Location Object out of the result
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(position.latitude);
            location.setLongitude(position.longitude);
            return location;
        }
    }

    //Calculation of an unknown Position by 3 know Points and the corresponding Distances in LatLng
    public static LatLng calculatePosition(LatLng p1, LatLng p2, LatLng p3, double distance1, double distance2, double distance3) {
        double angle1 = angleBetween(p2, p1, p3);
        double angle2 = angleBetween(p1, p2, p3);
        double angle3 = angleBetween(p2, p3, p1);

        double ratio1 = distance1 / Math.sin(angle1);
        double ratio2 = distance2 / Math.sin(angle2);
        double ratio3 = distance3 / Math.sin(angle3);

        double totalRatio = ratio1 + ratio2 + ratio3;

        double x = (ratio1 * p1.longitude + ratio2 * p2.longitude + ratio3 * p3.longitude) / totalRatio;
        double y = (ratio1 * p1.latitude + ratio2 * p2.latitude + ratio3 * p3.latitude) / totalRatio;

        return new LatLng(y, x);
    }

    //Angle between two Vectors
    public static double angleBetween(LatLng p1, LatLng p2, LatLng p3) {
        double[] vector1 = {p1.longitude - p2.longitude, p1.latitude - p2.latitude};
        double[] vector2 = {p3.longitude - p2.longitude, p3.latitude - p2.latitude};

        double dotProduct = vector1[0] * vector2[0] + vector1[1] * vector2[1];
        double magnitude1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]);
        double magnitude2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1]);

        return Math.acos(dotProduct / (magnitude1 * magnitude2));
    }
}
