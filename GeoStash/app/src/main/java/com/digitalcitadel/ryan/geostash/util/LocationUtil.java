package com.digitalcitadel.ryan.geostash.util;

import android.location.Location;

public class LocationUtil
{
    public static double getDistanceFromLocationInMeters(Location current, Location destination)
    {
        double diffLatitude = destination.getLatitude() - current.getLatitude();
        double diffLongitude = destination.getLongitude() - current.getLongitude();

        double a2 = Math.pow(diffLatitude, 2);
        double b2 = Math.pow(diffLongitude, 2);
        double lengthInLatLong = Math.sqrt(a2 + b2);

        // Number of miles in a lat * number of meters in a mile
        return lengthInLatLong * (69 * 1609.34);
    }
}
