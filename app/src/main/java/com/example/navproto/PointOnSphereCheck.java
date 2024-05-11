package com.example.navproto;

public class PointOnSphereCheck {
    public static boolean pointOnSphereCheck() {
        // Define the center of the sphere and the radius
        Point3D center = new Point3D(0, 0, 0); // Change coordinates as needed
        double radius = 5; // Change radius as needed

        // Define the point to check
        Point3D point = new Point3D(3, 4, 0); // Change coordinates as needed

        // Calculate the distance between the point and the center of the sphere
        double distance = point.distance(center);

        // Check if the distance equals the radius
        if (distance == radius) {
            System.out.println("The point lies on the surface of the sphere.");
            return true;
        } else {
            System.out.println("The point does not lie on the surface of the sphere.");
            return false;
        }
    }
}