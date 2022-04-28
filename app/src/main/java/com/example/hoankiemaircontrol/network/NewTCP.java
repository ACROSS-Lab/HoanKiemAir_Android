package com.example.hoankiemaircontrol.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NewTCP implements Runnable{

    private String msg;
    Socket socket;
    String ip;

    DataOutputStream dt;

    public void setIP(String ip){
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(this.ip,60003);
            dt = new DataOutputStream(socket.getOutputStream());
            dt.writeUTF(msg);
            dt.flush();
            dt.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMess(String msg){
        this.msg = msg;
        run();
    }
}
