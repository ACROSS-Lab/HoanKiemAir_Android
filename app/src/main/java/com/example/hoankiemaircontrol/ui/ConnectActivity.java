package com.example.hoankiemaircontrol.ui;

import android.content.Intent;
import android.os.Bundle;
////import android.util.Log;
////import android.view.KeyEvent;
//import android.os.Handler;
//import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
//import android.view.View;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hoankiemaircontrol.R;
//import com.example.hoankiemaircontrol.network.MQTTConnector;
import com.example.hoankiemaircontrol.network.NewTCP;
import com.example.hoankiemaircontrol.network.TCPConnector;

//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttToken;





import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ConnectActivity extends BaseActivity {
    private EditText mEditTextIpAddress;
    private CircularProgressButton mConnectButton;
    TCPConnector mt;
    NewTCP newTCP;


//    public Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            //Print messages from the server
//            System.out.println("read:"+msg.obj.toString());
//        };
//    };

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
                newTCP = new NewTCP();
                newTCP.setIP(serverIp);
                newTCP.run();
                newTCP.sendMess("Hello Server !!!");
                Intent i = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(i);
            }
        };

        if (serverIp.length() == 0) {
            Toast.makeText(ConnectActivity.this,
                    getResources().getString(R.string.toast_blank_ip),
                    Toast.LENGTH_SHORT).show();
        } else {
            //TODO: change this one in step 2
            mConnectButton.startAnimation(() -> null);
            thread.start();
        }
    }
}
