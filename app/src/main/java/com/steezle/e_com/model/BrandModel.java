package com.steezle.e_com.model;

/**
 * Created by juli on 20/1/18.
 */

public class BrandModel {

    String id, brand;
    boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public BrandModel(String id, String brand, boolean status) {
        this.id = id;
        this.brand = brand;
        this.status=status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
