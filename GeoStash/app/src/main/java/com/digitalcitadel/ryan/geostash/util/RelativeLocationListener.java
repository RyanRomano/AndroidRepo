package com.digitalcitadel.ryan.geostash.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public abstract class RelativeLocationListener implements LocationListener
{
    private Location mDestination;

    public RelativeLocationListener(Location destination)
    {
        mDestination = destination;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        double degreesFromNorth = location.bearingTo(mDestination);
        double distance = LocationUtil.getDistanceFromLocationInMeters(location, mDestination);
        onRelativeDistanceChanged(distance, degreesFromNorth);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
        // Do Nothing
    }

    @Override
    public void onProviderEnabled(String s)
    {
        // Do Nothing
    }

    @Override
    public void onProviderDisabled(String s)
    {
        // Do Nothing
    }

    public abstract void onRelativeDistanceChanged(double distanceAway, double degreesFromNorth);
}
