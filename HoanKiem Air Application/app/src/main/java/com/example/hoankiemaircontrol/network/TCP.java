package com.example.hoankiemaircontrol.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

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



    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static PrintWriter pt;

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
            socket = new Socket(ip, 3000);
            pt = new PrintWriter(socket.getOutputStream());
            pt.write("Hello Server" + "\n\r\n");
            pt.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(IMessageListener l) {
        ReceiveMessage.getInstance().subscribe(l);
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


    private static class ReceiveMessage extends AsyncTask<Void, Void, String> {
        private String mess;
        private IMessageListener listener;

        private static ReceiveMessage _instance;

        public static ReceiveMessage getInstance() {
            if (_instance==null) {
                _instance = new ReceiveMessage();
                _instance.execute();
            }
            return _instance;
        }

        public void subscribe(IMessageListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {

            do {
                try {
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    mess = bufferedReader.readLine();
                    //TODO: query the message
                    listener.messageReceived(mess);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }while(true);

        }
    }



}


