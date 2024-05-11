package com.example.navproto;

import java.util.ArrayList;


class Sphere {
    Point3D center;
    double radius;

    public Sphere(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }
}

class Circle {
    Point3D center;
    double radius;
    Point3D normal;

    public Circle(Point3D center, double radius, Point3D normal) {
        this.center = center;
        this.radius = radius;
        this.normal = normal;
    }
}

public class SphereCircleIntersection {

    public static ArrayList<Point3D> findIntersectionPoints(Sphere sphere, Circle circle) {
        ArrayList<Point3D> intersectionPoints = new ArrayList<>();

        // Translate the circle and sphere so that the sphere is centered at the origin
        Point3D translatedCircleCenter = new Point3D(circle.center.x - sphere.center.x,
                circle.center.y - sphere.center.y,
                circle.center.z - sphere.center.z);

        // Compute the distance from the translated circle center to the plane of the circle
        double distanceToPlane = translatedCircleCenter.x * circle.normal.x +
                translatedCircleCenter.y * circle.normal.y +
                translatedCircleCenter.z * circle.normal.z;

        // If the distance is greater than the radius of the sphere, there are no intersection points
        if (Math.abs(distanceToPlane) > sphere.radius) {
            return intersectionPoints;
        }

        // Compute the projection of the translated circle center onto the plane of the circle
        Point3D projection = new Point3D(translatedCircleCenter.x - distanceToPlane * circle.normal.x,
                translatedCircleCenter.y - distanceToPlane * circle.normal.y,
                translatedCircleCenter.z - distanceToPlane * circle.normal.z);

        // Compute the distance from the projected point to the circle center
        double distanceToCenter = Math.sqrt(projection.x * projection.x +
                projection.y * projection.y +
                projection.z * projection.z);

        // If the distance to the center is greater than the radius of the circle, there are no intersection points
        if (distanceToCenter > circle.radius) {
            return intersectionPoints;
        }

        // Compute the distance from the projected point to the intersection points
        double distanceToIntersection = Math.sqrt(circle.radius * circle.radius - distanceToCenter * distanceToCenter);

        // Compute the intersection points
        Point3D intersectionPoint1 = new Point3D(projection.x + distanceToIntersection * circle.normal.x,
                projection.y + distanceToIntersection * circle.normal.y,
                projection.z + distanceToIntersection * circle.normal.z);

        Point3D intersectionPoint2 = new Point3D(projection.x - distanceToIntersection * circle.normal.x,
                projection.y - distanceToIntersection * circle.normal.y,
                projection.z - distanceToIntersection * circle.normal.z);

        // Translate the intersection points back to the original coordinate system
        intersectionPoint1.x += sphere.center.x;
        intersectionPoint1.y += sphere.center.y;
        intersectionPoint1.z += sphere.center.z;

        intersectionPoint2.x += sphere.center.x;
        intersectionPoint2.y += sphere.center.y;
        intersectionPoint2.z += sphere.center.z;

        intersectionPoints.add(intersectionPoint1);
        intersectionPoints.add(intersectionPoint2);

        return intersectionPoints;
    }

    public void testSpheresCircle() {
        Sphere sphere = new Sphere(new Point3D(1, 1, 1), 2);
        Circle circle = new Circle(new Point3D(0.5, 0.5, 0.5), 1, new Point3D(0, 0, 1));

        ArrayList<Point3D> intersectionPoints = findIntersectionPoints(sphere, circle);

        if (intersectionPoints.isEmpty()) {
            System.out.println("No intersection points found.");
        } else {
            System.out.println("Intersection points:");
            for (Point3D point : intersectionPoints) {
                System.out.println("(" + point.x + ", " + point.y + ", " + point.z + ")");
            }
        }
    }
}