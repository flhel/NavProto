package com.example.navproto.positioning.multilateration;

import android.location.Location;
import android.location.LocationManager;

import com.example.navproto.positioning.multilateration.Point3D;

public class ConvertKBS {
    /* convert lat/lon/alt (lat in degrees North, lon in degrees East, alt in meters)
       to a (x,y,z) System in Meters where 0,0,0 is at "coordinatesCenter" (lat,lng,alt)
       this is done by calculation  the distance in m between two points given in lat lng alt(m)
    */
    public static Point3D convertToXYZ(Point3D locationP3D, Point3D pCoordCenter) {
        double dx = (71.5 * 1000)  * (locationP3D.x - pCoordCenter.x);
        double dy = (111.3 * 1000) * (locationP3D.y - pCoordCenter.y);
        double dz = locationP3D.z - pCoordCenter.z;

        return new Point3D(dx, dy, dz);
    }

    // Calculate the locations (lat,lng,alt) from the distance to the  "coordinatesCenter" in meters
    public static Point3D convertToLatLngAlt(Point3D locationP3D, Point3D pCoordCenter) {
        double dx = pCoordCenter.x + (locationP3D.x / (71.5 * 1000));
        double dy = pCoordCenter.y + (locationP3D.y / (111.3 * 1000)) ;
        double dz = pCoordCenter.z + locationP3D.z;

        return new Point3D(dx, dy, dz);
    }
}
