package com.example.hoankiemaircontrol.network.support;

public class Message {
    private final String message;
    private final Object data;

    public Message(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return   "{" + message + ";" + data + "}";
    }
}
