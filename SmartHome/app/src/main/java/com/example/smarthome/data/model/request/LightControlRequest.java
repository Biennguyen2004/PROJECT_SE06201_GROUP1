package com.example.smarthome.data.model.request;

public class LightControlRequest {
    private boolean status;

    public LightControlRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}