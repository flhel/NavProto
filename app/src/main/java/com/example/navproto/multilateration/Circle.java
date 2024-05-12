package com.example.navproto.multilateration;

public class Circle extends Plane{
    double radius;

    public Circle(Point3D center, double radius, Vector3D normal) {
        super(center, normal);
        this.radius = radius;
    }

}