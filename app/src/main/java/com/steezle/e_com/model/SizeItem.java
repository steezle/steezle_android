package com.steezle.e_com.model;

/**
 * Created by Hiren Patel on 1/25/2018.
 */

public class SizeItem {

    private String variation_id;
    private String variation_display_price;
    private String variation_reguler_price;
    private String qty;
    private String is_in_stock;
    private String availability_stock;
    private String color_code;
    private String pa_color;
    private String title_id, title_name, description_id, description;


    public String getVariation_id() {
        return variation_id;
    }

    public void setVariation_id(String variation_id) {
        this.variation_id = variation_id;
    }

    public String getVariation_display_price() {
        return variation_display_price;
    }

    public void setVariation_display_price(String variation_display_price) {
        this.variation_display_price = variation_display_price;
    }

    public String getVariation_reguler_price() {
        return variation_reguler_price;
    }

    public void setVariation_reguler_price(String variation_reguler_price) {
        this.variation_reguler_price = variation_reguler_price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public String getAvailability_stock() {
        return availability_stock;
    }

    public void setAvailability_stock(String availability_stock) {
        this.availability_stock = availability_stock;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getPa_color() {
        return pa_color;
    }

    public void setPa_color(String pa_color) {
        this.pa_color = pa_color;
    }

    public String getTitle_id() {
        return title_id;
    }

    public void setTitle_id(String title_id) {
        this.title_id = title_id;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }

    public String getDescription_id() {
        return description_id;
    }

    public void setDescription_id(String description_id) {
        this.description_id = description_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
