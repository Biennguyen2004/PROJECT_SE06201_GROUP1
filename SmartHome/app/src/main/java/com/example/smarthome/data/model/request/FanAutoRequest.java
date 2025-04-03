package com.example.smarthome.data.model.request;

public class FanAutoRequest {
    private boolean status; // true: bật auto, false: tắt auto

    public FanAutoRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}