package com.example.navproto.multilateration;

import android.util.Log;

public class SphereIntersection {

    private static final String TAG = "SphereIntersection";

    // Function to find intersection circle between two spheres
    public static Circle3D calculateCircle(Point3D center1, double radius1, Point3D center2, double radius2) {
        // Calculate the distance between centers
        double distance = center1.distance(center2);

        // Check for no intersection, one inside the other, or one touching the other
        if (distance > radius1 + radius2 || distance < Math.abs(radius1 - radius2)) {
            // No intersection
            return null;
        } else if (distance == 0 && radius1 == radius2) {
            // Circles are the same
            return null;
        } else if (distance == radius1 + radius2 || distance == Math.abs(radius1 - radius2)) {
            // Circles touch externally or internally
            return null;
        } else {
            // Calculate intersection circle parameters
            double a = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(distance, 2)) / (2 * distance);
            double h = Math.sqrt(Math.pow(radius1, 2) - Math.pow(a, 2));

            // Calculate intersection circle center
            double cx = center1.x + a * (center2.x - center1.x) / distance;
            double cy = center1.y + a * (center2.y - center1.y) / distance;
            double cz = center1.z + a * (center2.z - center1.z) / distance;
            Point3D center = new Point3D(cx, cy, cz);

            // Calculate intersection circle radius
            double radius = h;

            // Calculate intersection circle normal vector
            double nx = (center2.x - center1.x) / distance;
            double ny = (center2.y - center1.y) / distance;
            double nz = (center2.z - center1.z) / distance;

            Vector3D normal = new Vector3D(nx, ny, nz);

            // Return the intersection circle
            return new Circle3D(center, radius, normal);
        }
    }

    public static Circle3D calculateSphereIntersection(Sphere sphere1, Sphere sphere2) {
        Circle3D intersection = calculateCircle(sphere1.center, sphere1.radius, sphere2.center, sphere2.radius);
        if (intersection != null) {
            return intersection;
        } else {
            return null;
        }
    }
}
