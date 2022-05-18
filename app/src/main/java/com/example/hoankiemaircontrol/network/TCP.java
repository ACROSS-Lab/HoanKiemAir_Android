package com.example.hoankiemaircontrol.network;

import android.content.Context;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

public class TCP {


    String ip;
    static Socket socket;
    private Context sContext;
    private static TCP TCP;

    private static DataOutputStream dt;

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
                    dt = new DataOutputStream(socket.getOutputStream());
                    dt.writeUTF(new Message(mess,data).toString() + "\n\r\n");
                    dt.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    public void Stop(){
        try {
            dt.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


