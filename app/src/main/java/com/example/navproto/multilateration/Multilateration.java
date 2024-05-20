package com.example.navproto.multilateration;

import static com.example.navproto.multilateration.GeometricCalculations3D.*;

import static java.lang.Math.sqrt;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.rtt.RangingResult;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class Multilateration {

    private static final String TAG = "Multilateration";

    private final Point3D coordinatesCenter = new Point3D(50.928280, 6.929074, 0); // eg

    // lat, lng, alt
    private Point3D ap1Location = new Point3D(50.928393, 6.928548, 3); // eg
    private Point3D ap2Location = new Point3D(50.927977, 6.928806, 3); // eg
    private Point3D ap3Location = new Point3D(50.928282, 6.929071, 3); // eg
    private Point3D ap4Location = new Point3D(50.928419, 6.928843, 6); // 1og

    // in Millimeters
    private double ap1DistanceInM = 30.57;
    private double ap2DistanceInM = 30;
    private double ap3DistanceInM = 30;
    private double ap4DistanceInM = 5;

    //Test Code
    private final Point3D[] accessPointLocations2 = {
            new Point3D(3, 1, 3),
            new Point3D(2, 1, 1),
            new Point3D(3, 2, 1),
            new Point3D(5, 1, 1)
    };

    //Test Code
    private final double[] distances2 = {2, 1, 1, 2};

    // Stores all possible combinations of the 4 Spheres
    ArrayList<Sphere[]> allCombinations;

    public Location findPosition(ArrayList<RangingResult> results) {

        List<RangingResult> myAps = new ArrayList<>();
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
            ap1DistanceInM = myAps.get(0).getDistanceMm() / 1000.0;
            ap2DistanceInM = myAps.get(1).getDistanceMm() / 1000.0;
            ap3DistanceInM = myAps.get(2).getDistanceMm() / 1000.0;
            ap4DistanceInM = myAps.get(3).getDistanceMm() / 1000.0;
        }

        Point3D ap1Center = convertToXYZ(ap1Location, coordinatesCenter);
        Point3D ap2Center = convertToXYZ(ap2Location, coordinatesCenter);
        Point3D ap3Center = convertToXYZ(ap3Location, coordinatesCenter);
        Point3D ap4Center = convertToXYZ(ap4Location, coordinatesCenter);
        double ap1radius = ap1DistanceInM;
        double ap2radius = ap2DistanceInM;
        double ap3radius = ap3DistanceInM;
        double ap4radius = ap4DistanceInM;

        Sphere[] spheres = {
                new Sphere(ap1Center, ap1radius),
                new Sphere(ap2Center, ap2radius),
                new Sphere(ap3Center, ap3radius),
                new Sphere(ap4Center, ap4radius)
        };

        //For Testing the Multilateration Algorithm
        if(false){
            spheres = new Sphere[4];
            for (int i = 0; i < spheres.length; i++) {
                spheres[i] = new Sphere(accessPointLocations2[i], distances2[i]);
            }
        }

        Point3D locationP3D = computeForAllCombinations(spheres);
        Point3D locationP3DConverted = convertToLatLngAlt(locationP3D, coordinatesCenter);
        System.out.println("locationP3DConverted: " + locationP3DConverted);
        return toLocation(locationP3DConverted);
    }

    public Point3D computeForAllCombinations(Sphere[] spheres) {
        allCombinations = new ArrayList<>();
        findAllCombinations(spheres.length, spheres);
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

    /* convert lat/lon/alt (lat in degrees North, lon in degrees East, alt in meters)
       to a (x,y,z) System in Meters where 0,0,0 is at "coordinatesCenter" (lat,lng,alt)
       this is done by calculation  the distance in m between two points given in lat lng alt(m)
    */
    private Point3D convertToXYZ(Point3D locationP3D, Point3D pCoordCenter) {
        double dx = (71.5 * 1000)  * (locationP3D.x - pCoordCenter.x);
        double dy = (111.3 * 1000) * (locationP3D.y - pCoordCenter.y);
        double dz = locationP3D.z - pCoordCenter.z;

        return new Point3D(dx, dy, dz);
    }

    // Calculate the locations (lat,lng,alt) from the distance to the  "coordinatesCenter" in meters
    private Point3D convertToLatLngAlt(Point3D locationP3D, Point3D pCoordCenter) {
        double dx = pCoordCenter.x + (locationP3D.x / (71.5 * 1000));
        double dy = pCoordCenter.y + (locationP3D.y / (111.3 * 1000)) ;
        double dz = pCoordCenter.z + locationP3D.z;

        return new Point3D(dx, dy, dz);
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

        Circle3D circle1 = sphereIntersection(spheres[0],spheres[1]);
        if(circle1 == null){
            Log.d(TAG, "Spheres don't intersect in a circle" );
            return null;
        }

        Plane plane = new Plane(circle1.center, circle1.normal);
        Circle3D circle2 = planeSphereIntersection(plane, spheres[2]);

        if(circle2 == null){
            Log.d(TAG, "Plane and Sphere don't intersect" );
            return null;
        }

        ArrayList<Point3D> intersectionPoints = circleIntersection(circle1, circle2);

        if(intersectionPoints == null){
            Log.d(TAG, "Circles don't intersect" );
            return null;
        }

        //Log.d(TAG, "Check if Points are on 4th Sphere:" );
        for (Point3D point : intersectionPoints) {
            if(pointOnSphereCheck(point, spheres[3], 0.05)) {
                Log.d(TAG, "My Location: " + point.toString());
                return point;
            } else {
                //Log.d(TAG, "No: " + point.toString());
            }
        }

        return null;
    }
}