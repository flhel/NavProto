package com.example.navproto;

import java.util.ArrayList;

public class SphereIntersection {

    //Test Code (Common Point is x=1.0 y=0.0 Z=0.0)
    double[][] spheresTest = {
            {0, 0, 0, 1},
            {1, 0, 1, 1},
            {3, 0, 0, 2},
            {1, 1, 0, 1}
    };

    public void test() {
        ArrayList<Point3D> intersectionPoints = calculateIntersectionPoints(spheresTest);
        System.out.println("Intersection Points:");
        for (Point3D point : intersectionPoints) {
            System.out.println("(" + point.getX() + ", " + point.getY() + ", " + point.getZ() + ")");
        }
    }

    public ArrayList<Point3D> calculateIntersectionPoints(double[][] spheres) {

        ArrayList<Point3D> intersectionPoints = new ArrayList<Point3D>();

        // Iterate over pairs of spheres and find their intersections
        for (int i = 0; i < spheres.length - 1; i++) {
            for (int j = i + 1; j < spheres.length; j++) {
                // Get the centers and radii of the two spheres
                double[] sphere1 = spheres[i];
                double[] sphere2 = spheres[j];

                // Calculate the distance between the centers of the two spheres
                double d = Math.sqrt(Math.pow(sphere2[0] - sphere1[0], 2) +
                        Math.pow(sphere2[1] - sphere1[1], 2) +
                        Math.pow(sphere2[2] - sphere1[2], 2));

                // Calculate the distance from sphere1 to the plane of intersection
                double a = (Math.pow(sphere1[3], 2) - Math.pow(sphere2[3], 2) + Math.pow(d, 2)) / (2 * d);

                // Calculate the coordinates of the points of intersection
                double x1 = sphere1[0] + a * (sphere2[0] - sphere1[0]) / d;
                double y1 = sphere1[1] + a * (sphere2[1] - sphere1[1]) / d;
                double z1 = sphere1[2] + a * (sphere2[2] - sphere1[2]) / d;

                // Calculate the height of the intersection points from sphere1
                double h = Math.sqrt(Math.pow(sphere1[3], 2) - Math.pow(a, 2));

                // Calculate the coordinates of the intersection points
                double intersectionX1 = x1 + h * (sphere2[1] - sphere1[1]) / d;
                double intersectionY1 = y1 - h * (sphere2[0] - sphere1[0]) / d;
                double intersectionZ1 = z1;

                double intersectionX2 = x1 - h * (sphere2[1] - sphere1[1]) / d;
                double intersectionY2 = y1 + h * (sphere2[0] - sphere1[0]) / d;
                double intersectionZ2 = z1;

                // Add intersection points to the list
                intersectionPoints.add(new Point3D(intersectionX1, intersectionY1, intersectionZ1));
                intersectionPoints.add(new Point3D(intersectionX2, intersectionY2, intersectionZ2));
            }
        }
        return intersectionPoints;
    }
}
