package com.example.hoankiemaircontrol.ui;

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


public class ConnectActivity extends BaseActivity {
    private EditText mEditTextIpAddress;
    private CircularProgressButton mConnectButton;
    TCP _TCP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mEditTextIpAddress = findViewById(R.id.edit_text_ip_address);
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
        
        mConnectButton = findViewById(R.id.button_connect);
        mConnectButton.setOnClickListener(this::onClick);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.reset_params).setVisible(false);
        return true;
    }

    private void onClick(View v) {
        String serverIp = mEditTextIpAddress.getText().toString();
        Thread thread = new Thread(){
            @Override
            public void run() {
                _TCP = new TCP(ConnectActivity.this);
                _TCP.setIP(serverIp);
                _TCP.createConnection();

                Intent i = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(i);
            }

        };

        if (serverIp.length() == 0) {
            Toast.makeText(ConnectActivity.this,
                    getResources().getString(R.string.toast_blank_ip),
                    Toast.LENGTH_SHORT).show();
        } else {
            mConnectButton.startAnimation(() -> null);
            thread.start();
        }
    }
}
