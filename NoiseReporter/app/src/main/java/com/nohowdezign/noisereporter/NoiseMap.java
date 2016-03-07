package com.nohowdezign.noisereporter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nohowdezign.noisereporter.audio.SoundMeter;

public class NoiseMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SoundMeter soundMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        soundMeter = new SoundMeter();
        soundMeter.start();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(soundMeter.getDb());
                handler.postDelayed(this, 5000);
            }
        });
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

    private void checkPermissions() {
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Show why we need the damn permission
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }
}
