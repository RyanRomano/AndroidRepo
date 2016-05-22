package com.digitalcitadel.ryan.geostash;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalcitadel.ryan.geostash.R;

import java.util.ArrayList;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class FindStash extends AppCompatActivity
{
    private ListView stashListView;
    ArrayList<Stash> stashes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_stash);
        stashListView = (ListView) findViewById(R.id.listView);
        final DatabaseHelper myDb = new DatabaseHelper(getApplicationContext());
        stashes = myDb.getStashes();

        StashAdapter stashAdapter = new StashAdapter(this, stashes);
        stashListView.setAdapter(stashAdapter);


        stashListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CompassActivity.start(getParent(), 42.12345678, -74.90991111);
            }
        });
    }
}
