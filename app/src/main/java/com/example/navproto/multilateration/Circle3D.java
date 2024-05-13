package com.example.navproto.multilateration;

public class Circle3D extends Plane{
    double radius;

    public Circle3D(Point3D center, double radius, Vector3D normal) {
        super(center, normal);
        this.radius = radius;
    }

}