package com.steezle.e_com.model;

import java.util.List;

/**
 * Created by juli on 22/12/17.
 */

public class OrderhistoryModel {

    String price, name, product_thumbnail_url, brand, attribute_pa_size,  quantity;
    List<String> vaiations_list;

    public OrderhistoryModel(List<String> vaiations_list, String price,
                             String name,
                             String product_thumbnail_url,
                             String brand,
                             String quantity,
                             String attribute_pa_size
                             ) {
        this.vaiations_list=vaiations_list;
        this.price = price;
        this.name = name;
        this.product_thumbnail_url = product_thumbnail_url;
        this.brand = brand;
        this.attribute_pa_size = attribute_pa_size;
        this.quantity = quantity;
    }

    public List<String> getVaiations_list() {
        return vaiations_list;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_thumbnail_url() {
        return product_thumbnail_url;
    }

    public void setProduct_thumbnail_url(String product_thumbnail_url) {
        this.product_thumbnail_url = product_thumbnail_url;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAttribute_pa_size() {
        return attribute_pa_size;
    }

    public void setAttribute_pa_size(String attribute_pa_size) {
        this.attribute_pa_size = attribute_pa_size;
    }
}
