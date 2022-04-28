package com.example.hoankiemaircontrol.network;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


//TODO: try to build this one first
public class TCPConnector  {


//    Handler mHandler=null;
      Socket socket = null;
      String ip = null;
      DataOutputStream out;
      BufferedReader in;
      String msg;
//    OutputStream OutputStream = null; // output stream
//    InputStream InputStream = null; // receive stream
//    //Get handler of another thread
//
//    public void setHandler( Handler handler){
//        mHandler = handler;
//    }

    //Set server IP
//    public void setIp(String ip){
//        this.ip = ip;
//    }



    public void run() {
        try {
            socket = new Socket(this.ip, 60002);
            out = new DataOutputStream(socket.getOutputStream());
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
//        out.write(msg);
//        String resp = null;
//        try {
//            resp = in.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return resp;

        this.msg = msg;
        run();

    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public boolean isConnected(){
        return socket.isConnected();
    }

        //Get output stream
//        try {
//            OutputStream = socket.getOutputStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try{
//            while (true) // read the data sent from the server
//            {
//                final byte [] buffer = new byte [1024]; // create receive buffer
//                InputStream = socket.getInputStream();
//                final int len = InputStream.read(buffer); // read the data and return the length of the data
//                if(len>0)
//                {
//                    Message msg = mHandler.obtainMessage();
//                    //Set what to send
//                    msg.obj = new String(buffer,0,len);
//                    mHandler.sendMessage(msg);
//                }
//
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

}

    //Write data to server
//    public boolean write(String text){
//        boolean ret = true;
//        try {
//            OutputStream.write(text.getBytes());
//        } catch (IOException e) {
//            ret = false;
//            e.printStackTrace();
//        }
//        return ret;
//    }
