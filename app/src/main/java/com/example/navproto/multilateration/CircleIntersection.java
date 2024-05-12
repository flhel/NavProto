package com.example.navproto.multilateration;

import java.util.ArrayList;

public class CircleIntersection {

    public static ArrayList<Point3D> circleIntersection(Circle circle1, Circle circle2) {

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
}