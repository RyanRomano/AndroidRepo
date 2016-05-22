package com.digitalcitadel.ryan.geostash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ryan on 2/20/16.
 */
public class MainActivity extends Activity {
    ImageView logo;
    LocationManager locationManager;
    DatabaseHelper myDb;
    double latitude;
    double longitude;
    String description;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myDb = new DatabaseHelper(getApplicationContext());

        final Button findAStash = (Button) findViewById(R.id.find_a_stash_button);
        final Button leaveAStash = (Button) findViewById(R.id.leave_a_stash_button);
        logo = (ImageView) findViewById(R.id.logo);
        findAStash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindStash.class);
                startActivity(intent);
            }
        });
        leaveAStash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG).show();

                }
                else
                {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    myDb.insertData(description, latitude + "", longitude + "");
                    Toast.makeText(getApplicationContext(), "Successfully Added", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
