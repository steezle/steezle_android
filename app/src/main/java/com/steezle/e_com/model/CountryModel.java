package com.steezle.e_com.model;

/**
 * Created by juli on 25/1/18.
 */

public class CountryModel {

    private String name;
    private String code;

    public CountryModel(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
