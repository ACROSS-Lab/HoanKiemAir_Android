package com.example.hoankiemaircontrol.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.network.TCP;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ReconnectActivity extends AppCompatActivity {

    private TextView TextViewAddress;
    private CircularProgressButton mConnectButton;
    TCP TCP_connect;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);
        ip = getIntent().getStringExtra("ip");
        TextViewAddress = findViewById(R.id.edit_text_ip_address_reconnect);
        TextViewAddress.setText(ip);
        mConnectButton = findViewById(R.id.button_reconnect);
        mConnectButton.setOnClickListener(this::onClick);

        setUpIpAddress();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void setUpIpAddress(){
        TextViewAddress.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        mConnectButton.performClick();
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

    }

    private void onClick(View v) {
        Thread thread =  new Thread(() -> {
            TCP_connect = new TCP(ReconnectActivity.this);
            TCP_connect.setIP(ip);
            TCP_connect.createConnection();

            Intent i = new Intent(ReconnectActivity.this, MainActivity.class);
            i.putExtra("ip", ip);
            startActivity(i);

        });

        mConnectButton.startAnimation(() -> null);
        thread.start();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

    }

}