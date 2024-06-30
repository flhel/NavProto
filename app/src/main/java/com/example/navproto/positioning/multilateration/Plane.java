package com.example.navproto.positioning.multilateration;

public class Plane {
    Point3D center;
    Vector3D normal; //Vector

    double equationX;
    double equationY;
    double equationZ;
    double equationD;

    public Plane(Point3D center, Vector3D normal) {
        this.center = center;
        this.normal = normal;
        calcEquationXYZD();
    }

    private void calcEquationXYZD() {
        equationX = normal.x;
        equationY = normal.y;
        equationZ = normal.z;
        equationD = normal.x * (-1 * center.x) + normal.y * (-1 * center.y) + normal.z * (-1 * center.z);
    }
}
