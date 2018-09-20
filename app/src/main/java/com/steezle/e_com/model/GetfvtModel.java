package com.steezle.e_com.model;

import java.io.Serializable;

/**
 * Created by juli on 3/10/17.
 */

public class GetfvtModel implements Serializable {

    String product_id, product_image, product_name, product_type, product_price, product_sale_price, product_regular_price, product_stock_status, fav_list_added_date, brand;


    public GetfvtModel(String product_id, String product_image, String product_name, String brand, String product_type, String product_price) {
        this.product_id = product_id;
        this.product_image = product_image;
        this.product_name = product_name;
        this.product_type = product_type;
        this.product_price = product_price;
        this.product_sale_price = product_sale_price;
        this.product_regular_price = product_regular_price;
        this.product_stock_status = product_stock_status;
        this.fav_list_added_date = fav_list_added_date;
        this.brand = brand;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_sale_price() {
        return product_sale_price;
    }

    public void setProduct_sale_price(String product_sale_price) {
        this.product_sale_price = product_sale_price;
    }

    public String getProduct_regular_price() {
        return product_regular_price;
    }

    public void setProduct_regular_price(String product_regular_price) {
        this.product_regular_price = product_regular_price;
    }

    public String getProduct_stock_status() {
        return product_stock_status;
    }

    public void setProduct_stock_status(String product_stock_status) {
        this.product_stock_status = product_stock_status;
    }

    public String getFav_list_added_date() {
        return fav_list_added_date;
    }

    public void setFav_list_added_date(String fav_list_added_date) {
        this.fav_list_added_date = fav_list_added_date;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
