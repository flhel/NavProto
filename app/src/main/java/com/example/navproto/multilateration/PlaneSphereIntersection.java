package com.example.navproto.multilateration;

public class PlaneSphereIntersection {

    public static Circle planeSphereIntersection(Plane plane, Sphere sphere) {

        Vector3D planeNormal = plane.normal;
        double d = plane.equationD;

        System.out.println("Plane Normal " + plane.normal.toString());
        System.out.println("Plane X " + plane.equationX);
        System.out.println("Plane Y " + plane.equationY);
        System.out.println("Plane Z " + plane.equationZ);
        System.out.println("Plane D " + plane.equationD);
        System.out.println("sphere.center " + sphere.center.toString());
        System.out.println("sphere.radius " + sphere.radius);

        Point3D sphereCenter= sphere.center;
        double sphereRadius = sphere.radius;

        // Calculate the distance between the sphere center and the plane
        double distance = Math.abs(planeNormal.x * sphereCenter.x + planeNormal.y * sphereCenter.y + planeNormal.z * sphereCenter.z + d)
                / Math.sqrt(planeNormal.x * planeNormal.x + planeNormal.y * planeNormal.y + planeNormal.z * planeNormal.z);

        System.out.println("Distance Plane to Sphere: " + distance);
        // If the distance is greater than the sphere radius, there is no intersection
        if (distance > sphereRadius) {
            return null;
        }

        // Calculate the center of the intersection circle

        Point3D center = sphereCenter.add(planeNormal.multiply(distance));

        // Single Point intersection
        if (distance == sphereRadius) {
            return new Circle(center, 0, planeNormal);
        }

        // Circular intersection
        double radius = Math.sqrt(sphereRadius * sphereRadius - distance * distance);
        return new Circle(center, radius, planeNormal);
    }

    public static void testPlaneSphere() {
        //Point3D planeNormal = new Point3D(3, 2, 5);
        //Point3D planeCenter = new Point3D(1, 3, 2);


        Vector3D planeNormal = new Vector3D(0, 0, 1);
        Point3D planeCenter = new Point3D(0, 0, 5);
        Plane plane = new Plane(planeCenter, planeNormal);

        Point3D sphereCenter = new Point3D(0, 0, 5);
        double sphereRadius = 5.0;
        Sphere sphere = new Sphere(sphereCenter, sphereRadius);

        // Find the intersection circle
        Circle intersectionCircle = planeSphereIntersection(plane, sphere);

        if (intersectionCircle != null) {
            System.out.println("Intersection Circle Center: (" +
                    intersectionCircle.center.x + ", " +
                    intersectionCircle.center.y + ", " +
                    intersectionCircle.center.z + ")");
            System.out.println("Intersection Circle Radius: " + intersectionCircle.radius);
            System.out.println("Intersection Circle Normal: (" +
                    intersectionCircle.normal.x + ", " +
                    intersectionCircle.normal.y + ", " +
                    intersectionCircle.normal.z + ")");
        } else {
            System.out.println("The plane and sphere do not intersect.");
        }
    }
}
