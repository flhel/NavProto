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
/*
        // Calculate the distance between the centers of the circles
        double distanceBetweenCenters = Math.sqrt(Math.pow(circle2.center.x - circle1.center.x, 2) +
                Math.pow(circle2.center.y - circle1.center.y, 2) +
                Math.pow(circle2.center.z - circle1.center.z, 2));

        // Calculate the direction vector from the center of circle1 to the center of circle2
        double directionX = (circle2.center.x - circle1.center.x) / distanceBetweenCenters;
        double directionY = (circle2.center.y - circle1.center.y) / distanceBetweenCenters;
        double directionZ = (circle2.center.z - circle1.center.z) / distanceBetweenCenters;

        // Calculate the midpoint between the centers of the circles
        double midpointX = circle1.center.x + (distanceBetweenCenters / 2) * directionX;
        double midpointY = circle1.center.y + (distanceBetweenCenters / 2) * directionY;
        double midpointZ = circle1.center.z + (distanceBetweenCenters / 2) * directionZ;

        // Calculate the distance from the midpoint to the intersection points
        double distanceFromMidpointToIntersection = Math.sqrt(Math.pow(circle1.radius, 2) -
                Math.pow(distanceBetweenCenters / 2, 2));

        // Calculate the intersection points
        double intersectionPoint1X = midpointX + distanceFromMidpointToIntersection * directionY;
        double intersectionPoint1Y = midpointY - distanceFromMidpointToIntersection * directionX;
        double intersectionPoint1Z = midpointZ;
        double intersectionPoint2X = midpointX - distanceFromMidpointToIntersection * directionY;
        double intersectionPoint2Y = midpointY + distanceFromMidpointToIntersection * directionX;
        double intersectionPoint2Z = midpointZ;

        // Add the intersection points to the list
        intersectionPoints.add(new Point3D(intersectionPoint1X, intersectionPoint1Y, intersectionPoint1Z));
        intersectionPoints.add(new Point3D(intersectionPoint2X, intersectionPoint2Y, intersectionPoint2Z));
*/

        Vector3D center1 = circle1.center.toVector3D();
        Vector3D center2 = circle2.center.toVector3D();
        double radius1 = circle1.radius;
        double radius2 = circle2.radius;
        double distance = center1.distance(center2);

        //
        double a = (Math.pow(radius1, 2) - Math.pow(radius2, 2) + Math.pow(distance, 2)) / (2 * distance);
        double h = Math.sqrt(Math.pow(radius1, 2) - Math.pow(a, 2));

        // Calculate intersection circle center
        double cx = center1.x + a * (center2.x - center1.x) / distance;
        double cy = center1.y + a * (center2.y - center1.y) / distance;
        double cz = center1.z + a * (center2.z - center1.z) / distance;
        Vector3D center = new Vector3D(cx, cy, cz);

        // Define a tangent vector in the plane
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