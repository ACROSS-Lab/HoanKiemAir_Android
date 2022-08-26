package com.example.hoankiemaircontrol.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.network.TCP;
import com.example.hoankiemaircontrol.network.support.IMessageListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends BaseActivity implements IMessageListener, OnMapReadyCallback {

    private final static int DISPLAY_MODE_TRAFFIC = 0;
    private final static int DISPLAY_MODE_POLLUTION = 1;

    private DiscreteSeekBar mSeekBarNumCars;
    private DiscreteSeekBar mSeekBarNumMotorbikes;
    private SegmentedGroup mRadioGroupDisplayMode;
    private SegmentedGroup mRadioGroupMapSenarios;
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;


    private static String[] mess2;
    static ArrayList<Entry> lineEntries = new ArrayList<>();
    private static String ip;

    private GoogleMap mMap;
    Button draw_polygon;
    Button clear;
    Polygon polygon, polygon1;

    private final List<LatLng> latLngList = new ArrayList<>();
    private final List<Marker> markerList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(MainActivity.this);
        }



        ip = getIntent().getStringExtra("ip");
        // Catch UI
//        mRadioGroupMapSenarios = findViewById(R.id.radio_group_map_senarios);
        mSeekBarNumCars = findViewById(R.id.seekBar_for_cars);
        mSeekBarNumMotorbikes = findViewById(R.id.seekBar_for_motobikes);
        mRadioGroupDisplayMode = findViewById(R.id.radio_group_display_mode);
        draw_polygon = findViewById(R.id.draw_polygon);
        clear = findViewById(R.id.clear);
        draw_polygon.setOnClickListener(this::drawPolygon);
        clear.setOnClickListener(this::drawClear);

        // Handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, e) -> {
                    TCP.set_instance(null);
                    Intent intent = new Intent (getApplicationContext(), ReconnectActivity.class);
                    intent.putExtra("ip", ip);
                    startActivity(intent);
                });

        ChangeNumberOfCar();
        ChangeNumberOfMotor();
        TCP.getInstance(MainActivity.this).subscribe(this);

    }


    // Function for handle message that app receive

    public void ChangeNumberOfCar(){
        mSeekBarNumCars.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                 TCP.getInstance(MainActivity.this).SendMessageTask("n_cars", value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    public void ChangeNumberOfMotor(){
        mSeekBarNumMotorbikes.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                TCP.getInstance(MainActivity.this).SendMessageTask("n_motorbikes", value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void drawPolygon(View view) {
        if(polygon != null){
            polygon.remove();
        }
        PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).clickable(true)
                .strokeColor(Color.BLUE).strokeWidth(3);
        polygon = mMap.addPolygon(polygonOptions);

        TCP.getInstance(MainActivity.this).SendMessageTask("Block_of_polygon", latLngList);
    }

    private void drawClear(View view){
        if(polygon != null) polygon.remove();
        for(Marker marker : markerList) marker.remove();
        latLngList.clear();
        markerList.clear();

        TCP.getInstance(MainActivity.this). SendMessageTask("Block_of_polygon", latLngList);
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

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2500, 2500, 0));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.map_style));

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


    // Button change status of display mode
    public void onDisplayModeRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.radio_button_traffic:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("display_mode", DISPLAY_MODE_TRAFFIC);

                break;
            case R.id.radio_button_pollution:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("display_mode", DISPLAY_MODE_POLLUTION);
                break;
        }
    }


    // Button change status of Daytime mode
    public void onDaytimeClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_button_off:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("day_time_traffic", 0);

                break;
            case R.id.radio_button_on:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("day_time_traffic", 1);
                break;
        }
    }


    @Override
    public void messageReceived(String mess) {
        if (mess != null) {
            mess2 = mess.replace("[", "").replace("]", "").split(",");
        }else throw new NullPointerException();
        lineChart = findViewById(R.id.LineChart);
        getEntries();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineDataSet = new LineDataSet(lineEntries, "The rate change of pollution");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);
    }


    private void getEntries() {
        lineEntries.clear();
        for(int i=0; i<mess2.length; i++){
            lineEntries.add(new Entry(i, Float.parseFloat(mess2[i])));
        }
    }

    // Reset parameters
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_params:
                Log.d("MainActivity", "reset pressed!");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.reset_params_prompt);
                // Add the buttons
                builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                    mSeekBarNumCars.setProgress(0);
                    mSeekBarNumMotorbikes.setProgress(0);
//                    mRadioGroupRoadScenario.check(R.id.radio_button_scenario_0);
                    mRadioGroupDisplayMode.check(R.id.radio_button_traffic);
//                    onRoadScenarioRadioButtonClicked(findViewById(R.id.radio_button_scenario_0));
                    onDisplayModeRadioButtonClicked(findViewById(R.id.radio_button_traffic));
                });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.language_setting:
                showLanguageOptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        TCP.getInstance(MainActivity.this).endTask();
        TCP.set_instance(null);
    }

    @Override
    public void onBackPressed() {
        finish();
    }



}
