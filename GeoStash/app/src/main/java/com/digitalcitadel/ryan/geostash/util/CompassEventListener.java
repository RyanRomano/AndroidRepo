package com.digitalcitadel.ryan.geostash.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class CompassEventListener implements SensorEventListener
{
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private double mLastRotation;
    private static final int MIN_CHANGE = 1;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = sensorEvent.values;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            mGeomagnetic = sensorEvent.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                double azimut = (double) orientation[0];
                double rotation = Math.toDegrees(azimut);


                if(Math.abs(mLastRotation - rotation) > MIN_CHANGE)
                {
                    onCompassChanged((float) rotation);
                    mLastRotation = rotation;
                }
            }
        }
    }

    public abstract void onCompassChanged(float degrees);

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
        // Do Nothing
    }
}
