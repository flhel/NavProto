package com.example.navproto.multilateration;

import static com.example.navproto.multilateration.GeometricCalculations3D.*;
import static com.example.navproto.multilateration.ConvertKBS.*;
import static com.example.navproto.multilateration.Helpers.*;

import android.bluetooth.le.ScanResult;
import android.location.Location;
import android.net.wifi.rtt.RangingResult;
import android.util.Log;

import com.example.navproto.MyLocationServices.MyManagerBleRssi;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Multilateration {

    private static final String TAG = "Multilateration";

    private final Point3D coordinatesCenter = new Point3D(50.928280, 6.929074, 0);

    private double precision;


    // Stores all possible combinations of the 4 Spheres
    ArrayList<Sphere[]> allCombinations;


    public Location findPositionBLE(List<ScanResult> scanResults, Map<String, MyManagerBleRssi.Beacon> beacons, double precision) {

        // For testing
        if(scanResults == null){
            Sphere[] spheres = makeSpheres(testApLocations, testDistances);
            return findPosition(spheres);
        }

        this.precision = precision;

        LogInsufficientAPs(scanResults.size(), TAG);

        double[] distances = computeDistances(scanResults, beacons);

        List<MyManagerBleRssi.Beacon> beaconList = new ArrayList<MyManagerBleRssi.Beacon>(beacons.values());

        List<Point3D> locations = new ArrayList<>();
        for(MyManagerBleRssi.Beacon beacon : beaconList){
            locations.add(convertToXYZ(
                    new Point3D(beacon.lat, beacon.lng, beacon.alt), coordinatesCenter));
        }

        Sphere[] spheres = makeSpheres(locations, distances);

        return findPosition(spheres);
    }

    // Computes the distance in Meters from the BLE Access Points
    private double[] computeDistances(List<ScanResult> scanResults, Map<String, MyManagerBleRssi.Beacon> beacons){
        double[] distances = new double[scanResults.size()];

        for(int i = 0; i < scanResults.size(); i++){
            ScanResult res = scanResults.get(i);
            MyManagerBleRssi.Beacon beacon = beacons.get(res.getDevice().getAddress());

            distances[i] = Math.pow(10, (beacon.getMeasuredRssi()- res.getRssi()) / 20.0) / 1000;
        }
        return distances;
    }

    public Location findPositionRTT(List<RangingResult> results, double precision) {

        // For testing
        if(results == null) {
            Sphere[] spheres = makeSpheres(testApLocations, testDistances);
            return findPosition(spheres);
        }

        this.precision = precision;

        List<RangingResult> myAps = new ArrayList<>();
        for(RangingResult res : results){
            if(res.is80211mcMeasurement()){
                myAps.add(res);
            }
        }

        LogInsufficientAPs(myAps.size(), TAG);

        List<Point3D> locations = new ArrayList<>();
        double[] distances = new double[myAps.size()];

        for(int i = 0; i < myAps.size(); i++){
            Location loc = myAps.get(i).getUnverifiedResponderLocation().toLocation();
            Point3D apLocation = new Point3D(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
            locations.add(convertToXYZ(apLocation, coordinatesCenter));

            distances[i] = myAps.get(i).getDistanceMm() / 1000.0;
        }

        Sphere[] spheres = makeSpheres(locations, distances);

        return findPosition(spheres);
    }

    private Sphere[] makeSpheres(List<Point3D> locations, double[] distances){
        if(locations.size() != distances.length){
            Log.e(TAG, "Error: Given AP data is invalid");
            return null;
        }

        Sphere[] spheres = new Sphere[locations.size()];
        for(int i = 0; i < locations.size(); i++){
            spheres[i] = new Sphere(locations.get(i), distances[i]);
            Log.i(TAG, "Sphere: c = " + spheres[i].center + "  r = " + spheres[i].radius);
        }
        return spheres;
    }

    private Location findPosition(Sphere[] spheres) {
        Point3D locationP3D = computeForAllCombinations(spheres);
        if(locationP3D == null){
            Log.e(TAG, "There is no common intersection!");
            return null;
        }
        Point3D locationP3DConverted = convertToLatLngAlt(locationP3D, coordinatesCenter);
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

    public Point3D calculateLocation(Sphere[] spheres) {

        Circle3D circle1 = sphereIntersection(spheres[0],spheres[1]);
        if(circle1 == null){
            //Log.e(TAG, "Spheres don't intersect in a circle" );
            return null;
        }

        Plane plane = new Plane(circle1.center, circle1.normal);
        Circle3D circle2 = planeSphereIntersection(plane, spheres[2]);

        if(circle2 == null){
            //Log.e(TAG, "Plane and Sphere don't intersect" );
            return null;
        }

        ArrayList<Point3D> intersectionPoints = circleIntersection(circle1, circle2);

        if(intersectionPoints == null){
            //Log.e(TAG, "Circles don't intersect" );
            return null;
        }

        //Log.d(TAG, "Check if Points are on 4th Sphere:" );
        for (Point3D point : intersectionPoints) {
            if(pointOnSphereCheck(point, spheres[3], precision)) {
                Log.i(TAG, "Relative Location: " + point.toString());
                return point;
            } else {
                //Log.d(TAG, "No: " + point.toString());
            }
        }

        return null;
    }
}