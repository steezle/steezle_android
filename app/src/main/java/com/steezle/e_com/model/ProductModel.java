package com.steezle.e_com.model;

/**
 * Created by juli on 12/12/17.
 */

public class ProductModel {

    String id, title, price, main_image, brand_name, brand_id;

    public ProductModel(String id, String title, String price, String main_image, String brand_name, String brand_id) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.main_image = main_image;
        this.brand_name = brand_name;
        this.brand_id = brand_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMain_image() {
        return main_image;
    }

    public void setMain_image(String main_image) {
        this.main_image = main_image;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }
}
