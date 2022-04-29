package com.example.hoankiemaircontrol.network;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NewTCP implements Runnable{

    private String msg;
    Socket socket;
    String ip;
    Object data = null;
    private Context sContext;
    private static NewTCP newTCP;

    DataOutputStream dt;

    public NewTCP(Context context) {
        sContext = context;
    }

    public static NewTCP getInstance(Context context) {
        if (newTCP == null) {
            newTCP = new NewTCP(context);
        }
        return newTCP;
    }

    public void setIP(String ip){
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(this.ip,60006);
            dt = new DataOutputStream(socket.getOutputStream());
            dt.flush();
            dt.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMess(String msg, Object data){
        this.msg = msg;
        this.data = data;
        run();
    }
}
