package com.example.hoankiemaircontrol.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;


import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.network.IMessageListener;
import com.example.hoankiemaircontrol.network.TCP;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends BaseActivity implements IMessageListener{

    private static final int DISPLAY_MODE_TRAFFIC = 0;
    private static final int DISPLAY_MODE_POLLUTION = 1;



    private DiscreteSeekBar mSeekBarNumCars;
    private DiscreteSeekBar mSeekBarNumMotorbikes;
    private SegmentedGroup mRadioGroupRoadScenario;
    private SegmentedGroup mRadioGroupDisplayMode;

    private TextView statistics;

    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    static ArrayList<Entry> lineEntries = new ArrayList<>();

    private static String[] mess2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mSeekBarNumCars = findViewById(R.id.seekBar_for_cars);
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

        mSeekBarNumMotorbikes = findViewById(R.id.seekBar_for_motobikes);
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

        mRadioGroupRoadScenario = findViewById(R.id.radio_group_road_scenario);
        mRadioGroupDisplayMode = findViewById(R.id.radio_group_display_mode);

        TCP.getInstance(MainActivity.this).subscribe(this);



    }

    @Override
    public void messageReceived(String mess) {
        mess2= mess.replace("[", "").replace("]", "").split(",");
        lineChart = findViewById(R.id.LineChart);
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "The rate change of pollution");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);

    }

    public void getEntries(){
        lineEntries.clear();
        for(int i=0; i<mess2.length; i++){
            lineEntries.add(new Entry(i, Float.parseFloat(mess2[i])));
        }
    }


    public void onRoadScenarioRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked

        switch(v.getId()) {
            case R.id.radio_button_scenario_0:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", 0);
                break;
            case R.id.radio_button_scenario_1:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", 1);
                break;
            case R.id.radio_button_scenario_2:
                if (checked)
                    TCP.getInstance(MainActivity.this).SendMessageTask("road_scenario", 2);
                break;
        }
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_params:
                Log.d("MainActivity", "reset pressed!");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.reset_params_prompt);
                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSeekBarNumCars.setProgress(0);
                        mSeekBarNumMotorbikes.setProgress(0);
                        mRadioGroupRoadScenario.check(R.id.radio_button_scenario_0);
                        mRadioGroupDisplayMode.check(R.id.radio_button_traffic);
                        onRoadScenarioRadioButtonClicked(findViewById(R.id.radio_button_scenario_0));
                        onDisplayModeRadioButtonClicked(findViewById(R.id.radio_button_traffic));
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
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
    //TODO: This one need to change in last step

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TCP.getInstance(this).Disconnect();
    }

}
