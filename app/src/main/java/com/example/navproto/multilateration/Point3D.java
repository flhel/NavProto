package com.example.navproto.multilateration;

public class Point3D {
    double x, y, z;

    Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Point3D point3D) {
        if(this.x == point3D.x && this.y == point3D.y && this.z == point3D.z){
            return true;
        } else {
            return false;
        }
    }

    public double distance(Point3D point2) {
        double dx = this.x - point2.x;
        double dy = this.y - point2.y;
        double dz = this.z - point2.z;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    public Point3D add(Point3D point2) {
        double x = this.x + point2.x;
        double y = this.y + point2.y;
        double z = this.z + point2.z;
        return new Point3D(x, y, z);
    }

    public Point3D multiply(double d) {
        double x = this.x * d;
        double y = this.y * d;
        double z = this.z * d;
        return new Point3D(x, y, z);
    }

    public Vector3D toVector3D(){
        return new Vector3D(x,y,z);
    }
    @Override
    public String toString(){
        return "x (" + this.x + ") y (" + this.y + ") z (" + this.z + ")";
    }
}
