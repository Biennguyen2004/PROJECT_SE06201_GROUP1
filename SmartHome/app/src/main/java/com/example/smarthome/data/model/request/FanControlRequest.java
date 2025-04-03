package com.example.smarthome.data.model.request;

public class FanControlRequest {
    private boolean status; // true: bật quạt, false: tắt quạt

    public FanControlRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
