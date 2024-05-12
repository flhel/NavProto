package com.example.navproto.multilateration;

public class PointOnSphereCheck {
    public static boolean pointOnSphereCheck(Point3D point, Sphere sphere) {

        double distance = point.distance(sphere.center);

        if (distance == sphere.radius) {
            return true;
        } else {
            return false;
        }
    }
}