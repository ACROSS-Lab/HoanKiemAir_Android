package com.example.hoankiemaircontrol.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.network.TCP;
import com.example.hoankiemaircontrol.network.support.IMessageListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends BaseActivity implements IMessageListener{

    private static  int DISPLAY_MODE_TRAFFIC = 0;
    private static  int DISPLAY_MODE_POLLUTION = 1;
    private static  int NO_CLOSE_ROADS = 0;
    private static  int PEDESTRIAN_ZONE_ACTIVE = 1;
    private static  int EXTENSION_PLAN = 2;


    private DiscreteSeekBar mSeekBarNumCars;
    private DiscreteSeekBar mSeekBarNumMotorbikes;
    private SegmentedGroup mRadioGroupRoadScenario;
    private SegmentedGroup mRadioGroupDisplayMode;

    private int num_cars;
    private int num_motorbikes;
    private static String[] mess2;


    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    static ArrayList<Entry> lineEntries = new ArrayList<>();
    private static String ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = getIntent().getStringExtra("ip");

        // Catch UI
        mSeekBarNumCars = findViewById(R.id.seekBar_for_cars);
        mSeekBarNumMotorbikes = findViewById(R.id.seekBar_for_motobikes);
        mRadioGroupRoadScenario = findViewById(R.id.radio_group_road_scenario);
        mRadioGroupDisplayMode = findViewById(R.id.radio_group_display_mode);

        // Handle uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, e) -> {
                    TCP.set_instance(null);
                    Intent intent = new Intent (getApplicationContext(), ReconnectActivity.class);
                    intent.putExtra("ip", ip);
                    startActivity(intent);
                });


        //Save status when app stop
        if(savedInstanceState != null){
            num_cars = savedInstanceState.getInt("num_cars");
            num_motorbikes = savedInstanceState.getInt("num_motorbikes");
            DISPLAY_MODE_TRAFFIC = savedInstanceState.getInt("DISPLAY_MODE_TRAFFIC");
            DISPLAY_MODE_POLLUTION = savedInstanceState.getInt("DISPLAY_MODE_POLLUTION");
            NO_CLOSE_ROADS = savedInstanceState.getInt("NO_CLOSE_ROADS");
            PEDESTRIAN_ZONE_ACTIVE = savedInstanceState.getInt("PEDESTRIAN_ZONE_ACTIVE");
            EXTENSION_PLAN = savedInstanceState.getInt("EXTENSION_PLAN");
        }

        ChangeNumberOfCar();
        ChangeNumberOfMotor();
        TCP.getInstance(MainActivity.this).subscribe(this);

    }

    // Save status when app stop
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("num_cars", num_cars);
        savedInstanceState.putInt("num_motorbikes", num_motorbikes);
        savedInstanceState.putInt("DISPLAY_MODE_TRAFFIC", 0);
        savedInstanceState.putInt("DISPLAY_MODE_POLLUTION", 1);
        savedInstanceState.putInt("NO_CLOSE_ROADS", 0);
        savedInstanceState.putInt("PEDESTRIAN_ZONE_ACTIVE", 1);
        savedInstanceState.putInt("EXTENSION_PLAN", 2);
        super.onSaveInstanceState(savedInstanceState);
    }



    // Function for handle message that app receive
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

    // Button change status of road
    public void onRoadScenarioRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.radio_button_scenario_0:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", NO_CLOSE_ROADS);

                break;
            case R.id.radio_button_scenario_1:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", PEDESTRIAN_ZONE_ACTIVE);
                break;
            case R.id.radio_button_scenario_2:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", EXTENSION_PLAN);

                break;
        }
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
                    mRadioGroupRoadScenario.check(R.id.radio_button_scenario_0);
                    mRadioGroupDisplayMode.check(R.id.radio_button_traffic);
                    onRoadScenarioRadioButtonClicked(findViewById(R.id.radio_button_scenario_0));
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
