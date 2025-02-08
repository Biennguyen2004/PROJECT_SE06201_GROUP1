package com.example.smarthome.data.model;

public class Product {
    private int resourceId;
    private String name;
    private String des;

    public Product(int resourceId, String name, String des) {
        this.resourceId = resourceId;
        this.name = name;
        this.des = des;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
