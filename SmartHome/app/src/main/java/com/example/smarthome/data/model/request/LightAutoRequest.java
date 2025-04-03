package com.example.smarthome.data.model.request;

public class LightAutoRequest {
    private boolean status;

    public LightAutoRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
