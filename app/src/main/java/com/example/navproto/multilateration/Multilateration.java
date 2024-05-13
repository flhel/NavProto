package com.example.navproto.multilateration;

import static com.example.navproto.multilateration.CircleIntersection.circleIntersection;
import static com.example.navproto.multilateration.PlaneSphereIntersection.planeSphereIntersection;
import static com.example.navproto.multilateration.PointOnSphereCheck.pointOnSphereCheck;
import static com.example.navproto.multilateration.SphereIntersection.calculateSphereIntersection;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.rtt.RangingResult;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class Multilateration {

    private static final String TAG = "Multilateration";

    // lat, lng, alt
    Point3D ap1Location = new Point3D(50.928393, 6.928548, 3); // eg
    Point3D ap2Location = new Point3D(50.927977, 6.928806, 3); // eg
    Point3D ap3Location = new Point3D(50.928282, 6.929071, 3); // eg
    Point3D ap4Location = new Point3D(50.927977, 6.928806, 6); // 1og

    // in Millimeters
    double ap1DistanceInMm = 15000;
    double ap2DistanceInMm = 15000;
    double ap3DistanceInMm = 15000;
    double ap4DistanceInMm = 5000;

    Point3D[] accessPointLocations2 = {
            new Point3D(3, 1, 3),
            new Point3D(2, 1, 1),
            new Point3D(3, 2, 1),
            new Point3D(5, 1, 1)
    };


    double[] distances2 = {2, 1, 1, 2};

    // Stores all possible combinations of the 4 Spheres
    ArrayList<Sphere[]> allCombinations;

    public Location multilateration(ArrayList<RangingResult> results) {

        List<RangingResult> myAps = new ArrayList<RangingResult>();
        for(RangingResult res : results){
            if(res.is80211mcMeasurement()){
                myAps.add(res);
            }
        }

        if(myAps.size() < 4){
            Log.d(TAG,"Not enough Reference Points for positioning!");
            Log.d(TAG,"Fallback to Hardcoded Points for Testing!");
        } else {
            //Set the Locations and Distances for the Algorithm
            Location loc1 = myAps.get(0).getUnverifiedResponderLocation().toLocation();
            Location loc2 = myAps.get(1).getUnverifiedResponderLocation().toLocation();
            Location loc3 = myAps.get(2).getUnverifiedResponderLocation().toLocation();
            Location loc4 = myAps.get(2).getUnverifiedResponderLocation().toLocation();
            ap1Location = new Point3D(loc1.getLatitude(), loc1.getLongitude(), loc1.getAltitude());
            ap2Location = new Point3D(loc2.getLatitude(), loc2.getLongitude(), loc2.getAltitude());
            ap3Location = new Point3D(loc3.getLatitude(), loc3.getLongitude(), loc3.getAltitude());
            ap4Location = new Point3D(loc4.getLatitude(), loc4.getLongitude(), loc4.getAltitude());
            ap1DistanceInMm = myAps.get(0).getDistanceMm();
            ap2DistanceInMm = myAps.get(1).getDistanceMm();
            ap3DistanceInMm = myAps.get(2).getDistanceMm();
            ap4DistanceInMm = myAps.get(3).getDistanceMm();
        }

        Point3D ap1Center = convertToXYZ(ap1Location.x, ap1Location.y, ap1Location.z);
        Point3D ap2Center = convertToXYZ(ap2Location.x, ap2Location.y, ap2Location.z);
        Point3D ap3Center = convertToXYZ(ap3Location.x, ap3Location.y, ap3Location.z);
        Point3D ap4Center = convertToXYZ(ap4Location.x, ap4Location.y, ap4Location.z);
        double ap1radius = myAps.get(0).getDistanceMm() / 1000;
        double ap2radius = myAps.get(1).getDistanceMm() / 1000;
        double ap3radius = myAps.get(2).getDistanceMm() / 1000;
        double ap4radius = myAps.get(3).getDistanceMm() / 1000;

        Sphere[] spheres = {
                new Sphere(ap1Center, ap1radius),
                new Sphere(ap2Center, ap2radius),
                new Sphere(ap3Center, ap3radius),
                new Sphere(ap4Center, ap4radius)
        };

        //For Testing
        if(true){
            spheres = new Sphere[4];
            for (int i = 0; i < spheres.length; i++) {
                spheres[i] = new Sphere(accessPointLocations2[i], distances2[i]);
            }
        }

        Point3D locationP3D = computeForAllCombinations(spheres);
        Point3D locationP3DConverted = convertToLatLngAlt(locationP3D);
        return toLocation(locationP3DConverted);
    }

    private Point3D convertToLatLngAlt(Point3D locationP3D) {
        //TODO
        return locationP3D;
    }

    public Point3D computeForAllCombinations(Sphere[] spheres) {
        allCombinations = new ArrayList<>();
        findAllCombinations(spheres.length - 1, spheres);
        // Find the Combination of Spheres that give the Location (not every combination will compute)
        for (Sphere[] spheres1 : allCombinations) {
            Point3D location = calculateLocation(spheres1);
            if(location != null){
                return location;
            }
        }
        return null;
    }

    // Get every possible combination of Spheres recursively
    public void findAllCombinations(int n, Sphere[] spheres) {
        if(n == 1) {
            // Make new Array
            Sphere[] tmp = new Sphere[4];
            tmp[0] = spheres[0];
            tmp[1] = spheres[1];
            tmp[2] = spheres[2];
            tmp[3] = spheres[3];
            // Add to List of combinations
            allCombinations.add(tmp);
        } else {
            for(int i = 0; i < n-1; i++) {
                findAllCombinations(n - 1, spheres);
                if(n % 2 == 0) {
                    swap(spheres, i, n-1);
                } else {
                    swap(spheres, 0, n-1);
                }
            }
            findAllCombinations(n - 1, spheres);
        }
    }

    private void swap(Sphere[] spheres, int a, int b) {
        Sphere tmp = spheres[a];
        spheres[a] = spheres[b];
        spheres[b] = tmp;
    }

    // convert lat/lon/alt (lat in degrees North, lon in degrees East, alt in meters) to earth centered fixed coordinates (x,y,z)
    private Point3D convertToXYZ(double lat, double lon, double alt) {
        double Re = 6378137;
        double Rp = 6356752.31424518;

        double latrad = lat/180.0*Math.PI;
        double lonrad = lon/180.0*Math.PI;

        double coslat = Math.cos(latrad);
        double sinlat = Math.sin(latrad);
        double coslon = Math.cos(lonrad);
        double sinlon = Math.sin(lonrad);

        double term1 = (Re*Re*coslat)/
                Math.sqrt(Re*Re*coslat*coslat + Rp*Rp*sinlat*sinlat);

        double term2 = alt*coslat + term1;

        double x=coslon*term2;
        double y=sinlon*term2;
        double z = alt*sinlat + (Rp*Rp*sinlat)/
                Math.sqrt(Re*Re*coslat*coslat + Rp*Rp*sinlat*sinlat);

        return new Point3D(x, y, z);
    }

    // Make a Location Object out of the Result
    public Location toLocation(Point3D point) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(point.x);
        location.setLongitude(point.y);
        /*
         The altitude of this location in meters above the WGS84 reference ellipsoid
         */
        location.setAltitude(point.z);
        return location;
    }

    public Point3D calculateLocation(Sphere[] spheres) {

        Circle circle1 = calculateSphereIntersection(spheres[0],spheres[1]);
        if(circle1 == null){
            Log.d(TAG, "Spheres don't intersect in a circle" );
            return null;
        }

        Plane plane = new Plane(circle1.center, circle1.normal);
        Circle circle2 = planeSphereIntersection(plane, spheres[2]);

        if(circle2 == null){
            Log.d(TAG, "Plane and Sphere don't intersect" );
            return null;
        }

        ArrayList<Point3D> intersectionPoints = circleIntersection(circle1, circle2);

        if(intersectionPoints == null){
            Log.d(TAG, "Circles don't intersect" );
            return null;
        }

        Log.d(TAG, "Check if Points are on 4th Sphere:" );
        for (Point3D point : intersectionPoints) {
            if(pointOnSphereCheck(point, spheres[3])) {
                Log.d(TAG, "My Location: " + point.toString());
                return point;
            } else {
                Log.d(TAG, "No: " + point.toString());
            }
        }

        return null;
    }
}