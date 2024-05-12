package com.example.navproto.multilateration;

import static com.example.navproto.multilateration.CircleIntersection.circleIntersection;
import static com.example.navproto.multilateration.PlaneSphereIntersection.planeSphereIntersection;
import static com.example.navproto.multilateration.PointOnSphereCheck.pointOnSphereCheck;
import static com.example.navproto.multilateration.SphereIntersection.calculateSphereIntersection;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class Multilateration {

    private static final String TAG = "Multilateration";

    // For Testing: latitude, longitude, altitude
    Point3D[] accessPointLocations = {
            new Point3D(50.928393, 6.928548, 3), // eg
            new Point3D(50.927977, 6.928806, 3), // eg
            new Point3D(50.928282, 6.929071, 3), // eg
            new Point3D(50.927977, 6.928806, 6)  // 1og
    };

    // For Testing: Distances from the unknown point to the known points in millimeters
    double[] distances = {
            15000, // Distance to Ap1 15 m
            15000, // Distance to Ap1 15 m
            15000, // Distance to Ap1 15 m
            5000  // Distance to Ap1 5 m
    };

    Point3D[] accessPointLocations2 = {
            new Point3D(0, 0, 0), // eg
            new Point3D(2, 0, 0), // eg
            new Point3D(1, 1, 0), // eg
            new Point3D(1, 0, 1)  // 1og
    };


    double[] distances2 = {1, 1, 1, 1};

    ArrayList<Sphere[]> allCombinations = new ArrayList<>();

    public Point3D multilateration() {

        Sphere[] spheres; //Param

        //For Testing
        if(true){
            spheres = new Sphere[4];
            for (int i = 0; i < spheres.length; i++) {
                spheres[i] = new Sphere(accessPointLocations2[i], distances2[i]);
            }
            Point3D location = calculateLocation(spheres);
            return null;
        }

        permute(spheres, 0);
        for (Sphere[] spheres1 : allCombinations) {
            Point3D location = calculateLocation(spheres1);
            if(location != null){
                return location;
            }
        }
        return null;
    }

    // Get every possible combination of Spheres recursively
    public Point3D permute(Sphere[] spheres, int startIndex) {
        if (startIndex == spheres.length - 1) {
            System.out.println(Arrays.toString(spheres));
            allCombinations.add(spheres);
        } else {
            for (int i = startIndex; i < spheres.length; i++) {
                swap(spheres, startIndex, i);
                permute(spheres, startIndex + 1);
                swap(spheres, startIndex, i);
            }
        }
    }

    // Swaps two Spheres in the given Array
    public static void swap(Sphere[] spheres, int i, int j) {
        Sphere tempSphere = spheres[i];
        spheres[i] = spheres[j];
        spheres[j] = tempSphere;
    }

    public Point3D calculateLocation(Sphere[] spheres) {

        //spheres[1] has no intersecting circle with spheres[0]
        Circle circle1 = calculateSphereIntersection(spheres[0],spheres[2]);
        if(circle1 == null){
            Log.d(TAG, "Spheres don't intersect in a circle" );
            return null;
        }

        Log.d(TAG, "Circle XYZ: " + circle1.center.toString());
        Log.d(TAG, "Circle Radius: " + circle1.radius);
        Log.d(TAG, "Circle Normal: " + circle1.normal.toString());

        Plane plane = new Plane(circle1.center, circle1.normal);
        Circle circle2 = planeSphereIntersection(plane, spheres[3]);

        if(circle2 == null){
            Log.d(TAG, "Plane and Sphere don't intersect" );
            return null;
        }

        Log.d(TAG, "Circle2 XYZ: " + circle2.center.toString());
        Log.d(TAG, "Circle2 Radius: " + circle2.radius);
        Log.d(TAG, "Circle2 Normal: " + circle2.normal.toString());

        ArrayList<Point3D> intersectionPoints = circleIntersection(circle1, circle2);

        if(intersectionPoints == null){
            Log.d(TAG, "Circles don't intersect" );
            return null;
        }

        for (Point3D point : intersectionPoints) {
            if(pointOnSphereCheck(point, spheres[1])) {
                Log.d(TAG, "Location: " + point.toString());
                return point;
            } else {
                Log.d(TAG, "Not my Location: " + point.toString());
            }
        }

        return null;
    }
}