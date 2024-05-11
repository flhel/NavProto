package com.example.navproto;

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

        double[][] spheresTest = {
                {0, 0, 0, 1},
                {1, 0, 1, 1},
                {3, 0, 0, 2},
                {1, 1, 0, 1}
        };

        SphereIntersection s = new SphereIntersection();
        ArrayList<Point3D> intersectionPoints = s.calculateIntersectionPoints(spheresTest);
        ArrayList<Point3D> intersectionPointsRounded = new ArrayList<Point3D>();

        // Find the common intersection Point of all Spheres
        for (Point3D point : intersectionPoints) {
            // Rounding the double values to 10 decimal places
            final int scale = 10;
            BigDecimal x = BigDecimal.valueOf(point.getX()).setScale(scale, BigDecimal.ROUND_HALF_UP);
            BigDecimal y = BigDecimal.valueOf(point.getY()).setScale(scale, BigDecimal.ROUND_HALF_UP);
            BigDecimal z = BigDecimal.valueOf(point.getZ()).setScale(scale, BigDecimal.ROUND_HALF_UP);

            Log.d(TAG, "Point: " + x + " " + y + " " + z);

            Point3D roundedPoint = new Point3D(x.doubleValue(), y.doubleValue(), z.doubleValue());
            intersectionPointsRounded.add(roundedPoint);
        }

        Point3D location = findMostCommonIntersectionPoint(intersectionPointsRounded);
        Log.d(TAG, "My Location: " + location.x + " " + location.y + " " + location.z);

    }

    public static Point3D findMostCommonIntersectionPoint(ArrayList<Point3D> intersectionPoints) {
        int maxOccurrences = -1;
        Point3D mostCommonPoint = null;

        for(Point3D point : intersectionPoints){
            int occurrences = 0;
            for(Point3D point2 : intersectionPoints){
                if(point.equals(point2)){
                    occurrences++;
                }
            }
            if(occurrences > maxOccurrences){
                maxOccurrences = occurrences;
                mostCommonPoint = point;
            }
        }

        return mostCommonPoint;
    }
}