package com.example.navproto.multilateration;

import static com.example.navproto.multilateration.CircleIntersection.circleIntersection;
import static com.example.navproto.multilateration.PlaneSphereIntersection.planeSphereIntersection;
import static com.example.navproto.multilateration.PointOnSphereCheck.pointOnSphereCheck;
import static com.example.navproto.multilateration.SphereIntersection.calculateSphereIntersection;

import android.util.Log;

import java.util.ArrayList;

public class Multilateration {

    private static final String TAG = "Multilateration";

    public void test() {
    /*
        // latitude, longitude, altitude
        Point3D[] accessPointLocations = {
                new Point3D(50.928393, 6.928548, 3), // eg
                new Point3D(50.927977, 6.928806, 3), // eg
                new Point3D(50.928282, 6.929071, 3), // eg
                new Point3D(50.927977, 6.928806, 6)  // 1og
        };

        // Distances from the unknown point to the known points in millimeters
        double[] distances = {
                15000, // Distance to Ap1 15 m
                15000, // Distance to Ap1 15 m
                15000, // Distance to Ap1 15 m
                5000  // Distance to Ap1 5 m
        };
    */


        Point3D[] accessPointLocations = {
                new Point3D(0, 0, 0), // eg
                new Point3D(2, 0, 0), // eg
                new Point3D(1, 1, 0), // eg
                new Point3D(1, 0, 1)  // 1og
        };


        double[] distances = {1, 1, 1, 1};


        Sphere[] spheres = new Sphere[4];
        for (int i = 0; i < spheres.length; i++) {
            spheres[i] = new Sphere(accessPointLocations[i], distances[i]);
        }


        //spheres[1] has nu intersecting circle with spheres[0]
        Circle circle1 = calculateSphereIntersection(spheres[0],spheres[2]);
        if(circle1 == null){
            Log.d(TAG, "Spheres don't intersect in a circle" );
            return;
        }

        Log.d(TAG, "Circle XYZ: " + circle1.center.toString());
        Log.d(TAG, "Circle Radius: " + circle1.radius);
        Log.d(TAG, "Circle Normal: " + circle1.normal.toString());

        //circle1.normal = new Vector3D(0.5,0.5,0);

        //fghjklö

        Plane plane = new Plane(circle1.center, circle1.normal);
        Circle circle2 = planeSphereIntersection(plane, spheres[3]);

        Log.d(TAG, "Circle2 XYZ: " + circle2.center.toString());
        Log.d(TAG, "Circle2 Radius: " + circle2.radius);
        Log.d(TAG, "Circle2 Normal: " + circle2.normal.toString());

        ArrayList<Point3D> intersectionPoints = circleIntersection(circle1, circle2);

        if(intersectionPoints == null){
            Log.d(TAG, "Circles don't intersect" );
            return;
        }

        for (Point3D point : intersectionPoints) {
            if(pointOnSphereCheck(point, spheres[1])) {
                Log.d(TAG, "Location: " + point.toString());
            } else {
                Log.d(TAG, "Nope: " + point.toString());
            }
        }

        /*
        Sphere sphere = new Sphere(new Point3D(0, 0, 0), 5);
        Point3D point = new Point3D(3, 4, 0);
        pointOnSphereCheck(point, sphere);

        test2Spheres();
        testSpheresCircle();
         */

    }

}