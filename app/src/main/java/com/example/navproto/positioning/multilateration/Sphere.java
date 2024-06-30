package com.example.navproto.positioning.multilateration;

public class Sphere {
    Point3D center;
    double radius;

    public Sphere(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }
}