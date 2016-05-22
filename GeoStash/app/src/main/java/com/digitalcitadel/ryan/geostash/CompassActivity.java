package com.digitalcitadel.ryan.geostash;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalcitadel.ryan.geostash.R;
import com.digitalcitadel.ryan.geostash.util.CompassEventListener;
import com.digitalcitadel.ryan.geostash.util.LocationUtil;
import com.digitalcitadel.ryan.geostash.util.RelativeLocationListener;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class CompassActivity extends AppCompatActivity
{
    private static final String EXTRA_LATITUDE = "extra_latitude";
    private static final String EXTRA_LONGITUDE = "extra_longitude";

    public static void start(Activity callingActivity, double latitude, double longitude)
    {
        Intent intent = new Intent(callingActivity, CompassActivity.class);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        callingActivity.startActivity(intent);
    }

    // ===================

    // Smooth
    private float mLastAngle;
    private boolean mAnimationLock = false;

    private ImageView mDirectionArrow;
    private TextView mDistanceText;

    private RelativeLocationListener mRelativeLocationListener;
    private Location mDestination;

    // Location
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;

    // Compass
    private CompassEventListener mCompassEventListener;
    private float mLastRotationAngle;
    private double mDestinationDegreesFromNorth;

    private static final int LOCATION_REFRESH_TIME = 100;
    private static final int LOCATION_MIN_DISTANCE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        // Finding Views
        mDirectionArrow = (ImageView) findViewById(R.id.direction_arrow);
        mDistanceText = (TextView) findViewById(R.id.distance_text);

        // Initialize Location Manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialize Sensor Manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Set up compass listener
        mCompassEventListener = new CompassEventListener()
        {
            @Override
            public void onCompassChanged(float angle)
            {
                mLastRotationAngle = -angle;
                updateCompassRotation();


            }
        };
        mSensorManager.registerListener(
                mCompassEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL
        );
        mSensorManager.registerListener(
                mCompassEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        // Get Destination
        double destinationLat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        double destinationLong = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        mDestination = new Location("");
        mDestination.setLatitude(destinationLat);
        mDestination.setLongitude(destinationLong);

        // Set Location Listener
        mRelativeLocationListener = new RelativeLocationListener(mDestination)
        {
            @Override
            public void onRelativeDistanceChanged(double distanceAway, double degreesFromNorth)
            {
                handleRelativeDistanceChanged(distanceAway, degreesFromNorth);
            }
        };

        // Initialize Nammu for permission access
        Nammu.init(getApplicationContext());

        // Ask for permissions
        askForLocationPermissions();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(
                mCompassEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        );
        mSensorManager.unregisterListener(
                mCompassEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        );
    }

    private void handleRelativeDistanceChanged(double distanceAway, double degreesFromNorth)
    {
        mDistanceText.setText("You are " + (int) distanceAway + " meters away!");
        mDestinationDegreesFromNorth = degreesFromNorth;
        updateCompassRotation();
    }

    private void updateCompassRotation()
    {
        // Breaking out if views aren't drawn yet
        if(mDirectionArrow.getWidth() == 0 || mDirectionArrow.getHeight() == 0) return;

        // Only allowing one animation at once
        if(mAnimationLock) return;
        mAnimationLock = true;

        final float angle = (float) (mLastRotationAngle + mDestinationDegreesFromNorth);

        // Get from + to degrees
        float fromDegrees = mLastAngle;
        if(fromDegrees < 0) {
            fromDegrees += 360;
        }
        float toDegrees = angle;
        if(toDegrees < 0) {
            toDegrees += 360;
        }
        if (Math.abs(fromDegrees - toDegrees) > 180)
        {
            if(toDegrees > fromDegrees)
            {
                toDegrees -= 360;
            }
            else
            {
                fromDegrees -= 360;
            }
        }

        RotateAnimation rAnim = new RotateAnimation(fromDegrees, toDegrees, mDirectionArrow.getWidth() / 2, mDirectionArrow.getHeight() / 2);
        rAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        rAnim.setDuration(400);
        rAnim.setFillAfter(true);
        rAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mLastAngle = angle;
                mAnimationLock = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
                // Do Nothing
            }
        });
        mDirectionArrow.startAnimation(rAnim);
    }

    private void enableRequestLocationUpdates()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // Update location immediately
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager
                    .GPS_PROVIDER);
            double degreesFromNorth = lastKnownLocation.bearingTo(mDestination);
            double distance = LocationUtil.getDistanceFromLocationInMeters(lastKnownLocation,
                    mDestination);
            handleRelativeDistanceChanged(distance, degreesFromNorth);

            // Start Location Updates
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME,
                    LOCATION_MIN_DISTANCE,
                    mRelativeLocationListener
            );
        }
    }

    // =================================
    // ========== Permissions ==========
    // =================================

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults)
    {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Asks for the ACCESS_FINE_LOCATION permissions if they haven't
     * been granted to us already.
     */
    private void askForLocationPermissions()
    {
        // Ask if we don't have access to the permissions yet
        if(! Nammu.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            Nammu.askForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                    mFineLocationPermissionCallback);
        }
        // If we have the permission already, enable location updates
        else
        {
            enableRequestLocationUpdates();
        }
    }

    private final PermissionCallback mFineLocationPermissionCallback = new PermissionCallback()
    {
        @Override
        public void permissionGranted()
        {
            // We just got the permission, enable location updates
            enableRequestLocationUpdates();
        }

        @Override
        public void permissionRefused()
        {
            Toast.makeText(CompassActivity.this, "This app requires location permissions.", Toast
                    .LENGTH_LONG).show();
        }
    };
}
