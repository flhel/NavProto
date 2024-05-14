package com.example.navproto.multilateration;

import android.icu.math.BigDecimal;

import java.math.RoundingMode;
import java.util.ArrayList;

public class GeometricCalculations3D {

    // Function to find intersection circle between two spheres
    public static Circle3D sphereIntersection(Sphere sphere1, Sphere sphere2) {
        Point3D center1 = sphere1.center;
        double radius1 = sphere1.radius;

        Point3D center2 = sphere2.center;
        double radius2 = sphere2.radius;

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

    // Function to find intersection Points between two 3D Circles in the same Plane
    public static ArrayList<Point3D> circleIntersection(Circle3D circle1, Circle3D circle2) {

        // Calculate the line of intersection between the planes of the circles
        Vector3D lineDirection = circle1.normal.cross(circle2.normal);

        // The circles aren't on the same Plane
        if (lineDirection.magnitude() != 0) {
            return null;
        }

        Vector3D center1 = circle1.center.toVector3D();
        Vector3D center2 = circle2.center.toVector3D();
        double radius1 = circle1.radius;
        double radius2 = circle2.radius;
        double distance = center1.distance(center2);

        // The circles don't intersect
        if (distance > (radius1 + radius2)) {
            return null;
        }

        // Calculate intersection parameters
        double a = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(distance, 2)) / (2 * distance);
        double h = Math.sqrt(Math.pow(radius1, 2) - Math.pow(a, 2));

        // Calculate intersection Circle Center
        double cx = center1.x + a * (center2.x - center1.x) / distance;
        double cy = center1.y + a * (center2.y - center1.y) / distance;
        double cz = center1.z + a * (center2.z - center1.z) / distance;
        Vector3D center = new Vector3D(cx, cy, cz);

        // Define a tangent Vector in the Plane
        Vector3D t = center2.subtract(center1).cross(circle1.normal).normalize();

        // Calculate intersection Points
        Vector3D intersectionPoint1 = center.subtract(t.multiply(h));
        Vector3D intersectionPoint2 = center.add(t.multiply(h));

        // Create output List
        ArrayList<Point3D> intersectionPoints = new ArrayList<>();
        intersectionPoints.add(intersectionPoint1);
        intersectionPoints.add(intersectionPoint2);
        return intersectionPoints;
    }

    // Function to find intersection Circle between a Sphere and a Plane
    public static Circle3D planeSphereIntersection(Plane plane, Sphere sphere) {

        Vector3D planeNormal = plane.normal;
        double d = plane.equationD;

        Point3D sphereCenter= sphere.center;
        double sphereRadius = sphere.radius;

        // Calculate the distance between the sphere center and the plane
        double distance = Math.abs(planeNormal.x * sphereCenter.x + planeNormal.y * sphereCenter.y + planeNormal.z * sphereCenter.z + d)
                / Math.sqrt(planeNormal.x * planeNormal.x + planeNormal.y * planeNormal.y + planeNormal.z * planeNormal.z);

        // If the distance is greater than the sphere radius, there is no intersection
        if (distance > sphereRadius) {
            return null;
        }

        // Calculate the center of the intersection circle

        Point3D center = sphereCenter.add(planeNormal.multiply(distance));

        // Single Point intersection
        if (distance == sphereRadius) {
            return new Circle3D(center, 0, planeNormal);
        }

        // Circular intersection
        double radius = Math.sqrt(sphereRadius * sphereRadius - distance * distance);
        return new Circle3D(center, radius, planeNormal);
    }

    // Checks if a Point lays on a Sphere
    public static boolean pointOnSphereCheck(Point3D point, Sphere sphere, double precision) {

        if(Double.isNaN(point.x) || Double.isNaN(point.y) || Double.isNaN(point.z)){
            return false;
        }

        double distance = point.distance(sphere.center);

        if (distance >= (sphere.radius - precision) && distance <= (sphere.radius + precision) ) {
            return true;
        } else {
            return false;
        }
    }
}
