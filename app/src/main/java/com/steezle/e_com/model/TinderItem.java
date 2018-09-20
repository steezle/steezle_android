package com.steezle.e_com.model;

/**
 * Created by Hiren Patel on 1/8/2018.
 */

public class TinderItem {


    private String title, id;
    private String main_image;
    private String description;
    private String brandname, price;
    private String favlist_cnt;

    public TinderItem() {
        this.title = title;
        this.id = id;
        this.main_image = main_image;
        this.description = description;
        this.brandname = brandname;
        this.price = price;
        this.favlist_cnt = favlist_cnt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getFavlist_cnt() {
        return favlist_cnt;
    }

    public void setFavlist_cnt(String favlist_cnt) {
        this.favlist_cnt = favlist_cnt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMain_image() {
        return main_image;
    }

    public void setMain_image(String main_image) {
        this.main_image = main_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
