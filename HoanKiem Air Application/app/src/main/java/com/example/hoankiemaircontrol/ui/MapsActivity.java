package com.example.hoankiemaircontrol.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    Boolean Is_MAP_Moveable = false;
    GoogleMap mMap;
    List<LatLng> val;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // to detect map is movable



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapsActivity.this);
        }

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());

    }

    private void Draw_Map() {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(Color.RED);
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(Color.CYAN);
        Polygon polygon = mMap.addPolygon(rectOptions);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void Draw_in_Map(){
        FrameLayout fram_map = findViewById(R.id.fram_map);
        Button btn_draw_State = findViewById(R.id.btn_draw_State);
        btn_draw_State.setOnClickListener(v -> mMap.getUiSettings().setScrollGesturesEnabled(false));

        fram_map.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();

            int x_co = Math.round(x);
            int y_co = Math.round(y);

            Projection projection = mMap.getProjection();
            Point x_y_points = new Point(x_co, y_co);

            LatLng latLng = projection.fromScreenLocation(x_y_points);
            double latitude = latLng.latitude;

            double longitude = latLng.longitude;

            int eventaction = event.getAction();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    // finger touches the screen
                    val.add(new LatLng(latitude, longitude));

                case MotionEvent.ACTION_MOVE:
                    // finger moves on the screen
                    val.add(new LatLng(latitude, longitude));

                case MotionEvent.ACTION_UP:
                    // finger leaves the screen
                    Draw_Map();
                    break;
            }

            return Is_MAP_Moveable;

        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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

        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
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

        Draw_in_Map();
    }
}