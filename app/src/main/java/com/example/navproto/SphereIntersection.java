package com.example.navproto;

import java.util.ArrayList;

public class SphereIntersection {

    // Function to find intersection circle between two spheres
    public static Circle intersectionCircle(Point3D center1, double radius1, Point3D center2, double radius2) {
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

            // Calculate intersection circle normal
            Vector3D normal = new Vector3D(center1, center2).normalize();

            // Return the intersection circle
            return new Circle(center, radius, normal);
        }
    }

    // Class representing a point in 3D space
    static class Point3D {
        double x, y, z;

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distance(Point3D p) {
            return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2) + Math.pow(z - p.z, 2));
        }
    }

    // Class representing a vector in 3D space
    static class Vector3D {
        double x, y, z;

        public Vector3D(Point3D p1, Point3D p2) {
            this.x = p2.x - p1.x;
            this.y = p2.y - p1.y;
            this.z = p2.z - p1.z;
        }

        public Vector3D(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double magnitude() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public Vector3D normalize() {
            double mag = magnitude();
            return new Vector3D(x / mag, y / mag, z / mag);
        }
    }

    // Class representing a circle in 3D space
    static class Circle {
        Point3D center;
        double radius;
        Vector3D normal;

        public Circle(Point3D center, double radius, Vector3D normal) {
            this.center = center;
            this.radius = radius;
            this.normal = normal;
        }
    }

    public void test2Spheres() {

        Point3D center1 = new Point3D(1, 1, 1);
        double radius1 = 2.0;
        Point3D center2 = new Point3D(-1, -1, -1);
        double radius2 = 2.0;

        Circle intersection = intersectionCircle(center1, radius1, center2, radius2);
        if (intersection != null) {
            System.out.println("Intersection Circle: Center=" + intersection.center.x
                    + " " + intersection.center.y
                    + " " + intersection.center.z
                    + ", Radius=" + intersection.radius
                    + ", Normal=" + intersection.normal.x
                    + " " + intersection.normal.y
                    + " " + intersection.normal.z
            );
        } else {
            System.out.println("No intersection between the spheres.");
        }
    }
}
