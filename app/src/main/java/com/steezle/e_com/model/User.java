package com.steezle.e_com.model;

import org.json.JSONObject;

/**
 * Created by juli on 9/1/18.
 */

public class User {

    String user_id, user_email, first_name, last_name, gender, contact_number, profile_pic, total_steez, bag_count;


    public User(JSONObject jsonObject) {
        try {
            user_id = jsonObject.optString("user_id");
            user_email = jsonObject.optString("user_email");
            first_name = jsonObject.optString("first_name");
            last_name = jsonObject.optString("last_name");
            gender = jsonObject.optString("gender");
            contact_number = jsonObject.optString("contact_number");
            profile_pic = jsonObject.optString("profile_pic");
            ;
            total_steez = jsonObject.optString("total_steez");
            bag_count = jsonObject.optString("bag_count");
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTotal_steez() {
        return total_steez;
    }

    public void setTotal_steez(String total_steez) {
        this.total_steez = total_steez;
    }

    public String getBag_count() {
        return bag_count;
    }

    public void setBag_count(String bag_count) {
        this.bag_count = bag_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}