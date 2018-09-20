package com.steezle.e_com.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by juli on 3/10/17.
 */

public class GetcartModel implements Serializable {

    String  product_id,product_title,brand,qty,price,regular_price,sale_price,product_image,variation_id,cart_total,
            total_cart_item;
    List<String> vaiations_list;

    public GetcartModel(List<String> vaiations_list, String product_id, String product_title, String brand, String qty,
                        String price, String product_image, String variation_id) {
        this.vaiations_list=vaiations_list;
        this.product_id = product_id;
        this.product_title = product_title;
        this.brand = brand;
        this.qty = qty;
        this.price = price;
        this.regular_price = regular_price;
        this.sale_price = sale_price;
        this.product_image = product_image;
        this.variation_id = variation_id;
        this.cart_total = cart_total;
        this.total_cart_item = total_cart_item;
    }

    public List<String> getVaiations_list() {
        return vaiations_list;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(String variation_id) {
        this.variation_id = variation_id;
    }

    public String getCart_total() {
        return cart_total;
    }

    public void setCart_total(String cart_total) {
        this.cart_total = cart_total;
    }

    public String getTotal_cart_item() {
        return total_cart_item;
    }

    public void setTotal_cart_item(String total_cart_item) {
        this.total_cart_item = total_cart_item;
    }
}
