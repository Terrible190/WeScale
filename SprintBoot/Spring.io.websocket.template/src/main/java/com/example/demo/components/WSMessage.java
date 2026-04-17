package com.example.demo.components;

public class WSMessage {
    private String type;
    private Object data;

    public WSMessage() {}

    public WSMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() { return type; }
    public Object getData() { return data; }

    public void setType(String type) { this.type = type; }
    public void setData(Object data) { this.data = data; }
}