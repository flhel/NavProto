package com.example.navproto;

import static com.example.navproto.PointOnSphereCheck.pointOnSphereCheck;

import android.icu.math.BigDecimal;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Multilateration {

    private static final String TAG = "Multilateration";

    public void test() {

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


        SphereIntersection s = new SphereIntersection();
        s.test2Spheres();

        SphereCircleIntersection s2 = new SphereCircleIntersection();
        s2.testSpheresCircle();

        pointOnSphereCheck();

    }

}