package com.example.smarthome.data.model.request;

public class DoorAutoRequest {
    private boolean status; // true: bật auto, false: tắt auto

    public DoorAutoRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}