package com.example.hoankiemaircontrol.network;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCP {

    String ip;
    static Socket socket;
    private Context sContext;
    private static TCP TCP;
    private String mess;

    private static DataOutputStream dt;
    private static DataInputStream it;
    private  static PrintWriter pt;
    private  static BufferedReader bt;

    public TCP(Context context) {
        sContext = context;
    }

    public static TCP getInstance(Context context) {
        if (TCP == null) {
            TCP = new TCP(context);
        }
        return TCP;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void createConnection() {
        try {
            socket = new Socket(this.ip, 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void SendMessageTask(String mess, Object data){

                try {
                    pt = new PrintWriter(socket.getOutputStream());
                    var mess1 = new Message(mess,data);
                    var str_mess = mess1.toString() + "\n\r\n";
                    pt.write(str_mess);
                    pt.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    public String Receive(){
        try {
            it = new DataInputStream(socket.getInputStream());
            mess = it.readLine().toString()+ "\n";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mess;
    }

}


