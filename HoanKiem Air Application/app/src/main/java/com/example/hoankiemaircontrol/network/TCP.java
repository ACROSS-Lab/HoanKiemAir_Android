package com.example.hoankiemaircontrol.network;

import android.content.Context;

import android.os.AsyncTask;

import com.example.hoankiemaircontrol.network.support.IMessageListener;
import com.example.hoankiemaircontrol.network.support.Message;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

public class TCP {
    private String ip;
    static Socket socket;
    private Context Present_context;
    private static TCP TCP_instance;


    private static ReceiveMessage _instance;


    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static PrintWriter pt;

    public TCP(Context context) {
        Present_context = context;
    }

    public static TCP getInstance(Context context) {
        if (TCP_instance == null) {
            TCP_instance = new TCP(context);
        }
        return TCP_instance;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public static void set_instance(ReceiveMessage _instance) {
        TCP._instance = _instance;
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


    public int SendMessageTask(String mess, int data) {
           if(socket.isConnected()){
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
           return data;
    }



    private static class ReceiveMessage extends AsyncTask<Void, Void, Void>  {
        private String mess;
        private IMessageListener listener;

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
        protected Void doInBackground(Void... voids){
            do {
                try {
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    mess = bufferedReader.readLine();
                    listener.messageReceived(mess);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }while(socket.isConnected());

            return null;
        }
}



}


