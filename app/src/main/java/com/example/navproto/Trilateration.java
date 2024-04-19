package com.example.navproto;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.rtt.RangingResult;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Trilateration {
    //Theoretical Position of the Wifi Access Points

    // 50.928162, 6.928819
    private LatLng ap1Location = new LatLng(50.928393, 6.928548);
    private LatLng ap2Location = new LatLng(50.927977, 6.928806);
    private LatLng ap3Location = new LatLng(50.928282, 6.929071);


    //Theoretical Distance to the Wifi Access Points
    private int ap1DistanceInMm = 15000;
    private int ap2DistanceInMm = 15000;
    private int ap3DistanceInMm = 15000;

    public Location findPosition(List<RangingResult> results){

        //Flag for Testing etc.
        boolean apPositionsHardcoded = false;
        if(results.isEmpty()){
            apPositionsHardcoded = true;
        }

        if(!apPositionsHardcoded){
            // Look for WifiRtt capable APs in Ranging Results
            List<RangingResult> myAps = new ArrayList<RangingResult>();
            for(RangingResult res : results){
                if(res.is80211mcMeasurement()){
                    myAps.add(res);
                }
            }

            if(myAps.size() < 3){
                Log.d(TAG,"Not enough Reference Points for positioning!");
                Log.d(TAG,"Fallback to Hardcoded Points for Testing!");
            } else {
                //Set the Locations and Distances for the Algorithm
                Location loc1 = myAps.get(0).getUnverifiedResponderLocation().toLocation();
                Location loc2 = myAps.get(1).getUnverifiedResponderLocation().toLocation();
                Location loc3 = myAps.get(2).getUnverifiedResponderLocation().toLocation();
                ap1Location = new LatLng(loc1.getLatitude(), loc1.getLongitude());
                ap2Location = new LatLng(loc2.getLatitude(), loc2.getLongitude());
                ap3Location = new LatLng(loc3.getLatitude(), loc3.getLongitude());
                ap1DistanceInMm = myAps.get(0).getDistanceMm();
                ap2DistanceInMm = myAps.get(1).getDistanceMm();
                ap3DistanceInMm = myAps.get(2).getDistanceMm();
            }
        }

        //Calculation between 3 Points has to be enough, probably wont even get 3 APs to test the Algorithm properly
        LatLng position = calculatePosition(ap1Location, ap2Location, ap3Location,
                ap1DistanceInMm, ap2DistanceInMm, ap3DistanceInMm);

        // Make a Location Object out of the Result
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(position.latitude);
        location.setLongitude(position.longitude);
        return location;
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
