package com.example.hoankiemaircontrol.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hoankiemaircontrol.R;
import com.example.hoankiemaircontrol.network.TCP;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class Reconnect_Activity extends AppCompatActivity {
    private EditText mEditTextIpAddress;
    private CircularProgressButton mConnectButton;
    TCP _TCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);

        mEditTextIpAddress = findViewById(R.id.edit_text_ip_address_reconnect);
        mEditTextIpAddress.setOnKeyListener((v, keyCode, event) -> {
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

        mConnectButton = findViewById(R.id.button_reconnect);
        mConnectButton.setOnClickListener(this::ReconnectClicked);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.findItem(R.id.reset_params).setVisible(false);
//        return true;
//    }


    public void ReconnectClicked(View view) {
        String serverIp = mEditTextIpAddress.getText().toString();
        Thread thread =  new Thread(){
            @Override
            public void run() {
                _TCP = new TCP(Reconnect_Activity.this);
                _TCP.setIP(serverIp);
                _TCP.createConnection();
                Intent i = new Intent(Reconnect_Activity.this, MainActivity.class);
                startActivity(i);
            }

        };

        if (serverIp.length() == 0) {
            Toast.makeText(Reconnect_Activity.this,
                    getResources().getString(R.string.toast_blank_ip),
                    Toast.LENGTH_SHORT).show();
        } else {
//            mConnectButton.startAnimation(() -> null);
            thread.start();
        }
    }
}