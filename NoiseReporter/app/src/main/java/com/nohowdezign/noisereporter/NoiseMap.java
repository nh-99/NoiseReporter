package com.nohowdezign.noisereporter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nohowdezign.noisereporter.audio.SoundMeter;
import com.nohowdezign.noisereporter.network.SendData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NoiseMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SoundMeter soundMeter;
    private List<Double> decibelMeasures = new ArrayList<Double>();
    private List<Double> averageDecibelMeasures = new ArrayList<Double>();
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addDrawerItems();
        setupDrawer();

        soundMeter = new SoundMeter();
        soundMeter.start();
        storeDecibels();
        calculateAverages();
        sendData();
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Noise Reporter");
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Noise Reporter");
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    /**
     * Drops pins on maps when it's ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // TODO: Poll server for noisy locations & drop pins
        LatLng belfast = new LatLng(44.4229874, -69.0111177);
        mMap.addMarker(new MarkerOptions().position(belfast).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(belfast));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void storeDecibels() {
        // Thread to get the dB
        final Handler decibelHandler = new Handler();
        decibelHandler.post(new Runnable() {
            @Override
            public void run() {
                decibelMeasures.add(soundMeter.getDb());
                decibelHandler.postDelayed(this, 5000);
            }
        });
    }

    private void calculateAverages() {
        // Thread to average the dB every 30 seconds
        final Handler averageHandler = new Handler();
        averageHandler.post(new Runnable() {
            @Override
            public void run() {
                double average = 0;
                int count = 0;
                for(Double decibel : decibelMeasures) {
                    average += decibel;
                    count += 1;
                }
                average = (average / count);
                averageDecibelMeasures.add(average);
                decibelMeasures.removeAll(decibelMeasures); // Wipe out the old measures to conserve memory
                averageHandler.postDelayed(this, 30000);
            }
        });
    }

    private void sendData() {
        // Thread to send the data to the server
        final Handler dataHandler = new Handler();
        dataHandler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("Sending shit");
                SendData dataSender = new SendData();
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                try {
                    double latitude = 0;
                    double longitude = 0;
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    try {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(averageDecibelMeasures.size() > 0) {
                        for (Double decibel : averageDecibelMeasures) {
                            try {
                                dataSender.send(getApplicationContext(), latitude, longitude, decibel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        averageDecibelMeasures.clear(); // Clear up averages so we don't have duplicate data entries
                    }
                } catch(final SecurityException e) {
                    e.printStackTrace();
                }
                //dataHandler.postDelayed(this, 300000);
                dataHandler.postDelayed(this, 60000);
            }
        });
    }

    private void checkPermissions() {
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
        int MY_PERMISSIONS_REQUEST_INTERNET = 2;
        int MY_PERMISSIONS_REQUEST_LOCATION = 3;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                // Show why we need the damn permission
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                // Show why we need the damn permission
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show why we need the damn permission
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }
}
