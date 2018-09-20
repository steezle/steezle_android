package com.steezle.e_com.model;

/**
 * Created by juli on 31/1/18.
 */

public class SaveCardModel {

    String token, card_string;

    public SaveCardModel(String token, String card_string) {
        this.token = token;
        this.card_string = card_string;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCard_string() {
        return card_string;
    }

    public void setCard_string(String card_string) {
        this.card_string = card_string;
    }
}
