package com.nohowdezign.noisereporter;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NoiseMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
}
