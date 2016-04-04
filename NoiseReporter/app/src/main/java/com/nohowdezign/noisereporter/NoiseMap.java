package com.nohowdezign.noisereporter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.nohowdezign.noisereporter.audio.SoundMeter;
import com.nohowdezign.noisereporter.network.SendData;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoiseMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SoundMeter soundMeter;
    private List<Double> decibelMeasures = new ArrayList<Double>();
    private List<Double> averageDecibelMeasures = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "i click on FAB", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        soundMeter = new SoundMeter();
        soundMeter.start();
        storeDecibels();
        calculateAverages();
        sendData();
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
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
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
