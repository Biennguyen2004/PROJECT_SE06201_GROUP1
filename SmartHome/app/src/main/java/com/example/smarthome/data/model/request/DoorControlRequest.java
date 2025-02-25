package com.example.smarthome.data.model.request;

public class DoorControlRequest {
    private boolean status; // true: mở cửa, false: đóng cửa

    public DoorControlRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }
}
