package com.example.hoankiemaircontrol.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.databinding.ActivityMapsBinding;
import com.example.hoankiemaircontrol.network.TCP;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button draw_polygon;
    Button clear;
    Polygon polygon, polygon1;

    private final List<LatLng> latLngList = new ArrayList<>();
    private final List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }


        draw_polygon = findViewById(R.id.draw_polygon);
        draw_polygon.setOnClickListener(this::drawPolygon);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this::drawClear);

    }

    private void drawPolygon(View view) {
        if(polygon != null){
            polygon.remove();
        }
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).clickable(true)
                                        .strokeColor(Color.BLUE).strokeWidth(3);
        polygon = mMap.addPolygon(polygonOptions);

        TCP.getInstance(MapsActivity.this).SendMessageTask("Block_of_polygon", latLngList);
    }

    private void drawClear(View view){
        if(polygon != null) polygon.remove();
        for(Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();

        TCP.getInstance(MapsActivity.this). SendMessageTask("Block_of_polygon", latLngList);
    }

    public void setBounder(){
        double top = 21.04287722989717;
        double right = 105.87771067295719;
        double bottom = 21.02153486576196;
        double left = 105.83025731798917;

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(bottom, left),
                new LatLng(top, right)
        );

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1150, 1150, 0));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.map_style));

        polygon1 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(21.020657, 105.841596),
                        new LatLng(21.028989, 105.841660),
                        new LatLng(21.029189, 105.842561),
                        new LatLng(21.028788, 105.843012),
                        new LatLng(21.031923, 105.844353),
                        new LatLng(21.032364, 105.843409),
                        new LatLng(21.035087, 105.843945),
                        new LatLng(21.034862, 105.845008),
                        new LatLng(21.039913, 105.846125),
                        new LatLng(21.040834, 105.850320),
                        new LatLng(21.040284, 105.850792),
                        new LatLng(21.042867, 105.857498),
                        new LatLng(21.020684, 105.868289),
                        new LatLng(21.018430, 105.861101),
                        new LatLng(21.019071, 105.858746),
                        new LatLng(21.018370, 105.855077),
                        new LatLng(21.017960, 105.853918))
                .strokeColor(Color.RED).strokeWidth(3));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        setBounder();

        mMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            Marker marker = mMap.addMarker(markerOptions);

            latLngList.add(latLng);
            markerList.add(marker);
        });

    }

}