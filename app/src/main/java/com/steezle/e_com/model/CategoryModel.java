package com.steezle.e_com.model;

/**
 * Created by juli on 28/9/17.
 */

public class CategoryModel {

    private String name, id, image, bag_count;

    public CategoryModel(String name, String id, String image) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.bag_count = bag_count;
    }

    public String getBag_count() {
        return bag_count;
    }

    public void setBag_count(String bag_count) {
        this.bag_count = bag_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
