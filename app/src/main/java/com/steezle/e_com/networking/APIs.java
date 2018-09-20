package com.steezle.e_com.networking;

import android.view.KeyEvent;

import com.steezle.e_com.model.CountryModel;

/**
 * Created by dr on 26/08/17.
 * On Ocean-Resto-Bar
 */

public interface APIs {

    String BASE_URL ="http://steezle.ca/wp-json/rest/";
//    String BASE_URL ="http://mytestwork.com/steezle/wp-json/rest/";
    String REGISTER_API=BASE_URL+"register";
    String LOGIN_API = BASE_URL+"login";
    String LOGOUT_API=BASE_URL+"logout";
    String Forgot_API= BASE_URL+"forgot_password";
    String search=BASE_URL+"search";
    String productlist=BASE_URL+"productlist";
    String productdetail=BASE_URL+"product-detail";
    String categories=BASE_URL+"get_categories";
    String categories_sub=BASE_URL+"get_subcategories";
    String remove_fvt=BASE_URL+"remove-favlist";
    String add_fvt=BASE_URL+"add-favlist";
    String variation_check=BASE_URL+"variation-check";
    String get_fvt=BASE_URL+"get-favlist";
    String orderlist=BASE_URL+"order-history";
    String orderlisthistory=BASE_URL+"order-detail";
    String add_cart=BASE_URL+"add-cart";
    String get_cart=BASE_URL+"get-cart";
    String remove_cart=BASE_URL+"remove-cart";
    String editprofile=BASE_URL+"editprofile";
    String setaddress=BASE_URL+"set-address";
    String getaddress=BASE_URL+"get-address";
    String countries=BASE_URL+"get-countries";
    String state=BASE_URL+"get-state";
    String checkout=BASE_URL+"checkout";
    String payment=BASE_URL+"payment";
    String sociallogin=BASE_URL+"social-login";
    String applayfilter=BASE_URL+"apply-filters";
    String filter=BASE_URL+"product-filters";
    String Ordercancel=BASE_URL+"cancel-order";
    String applaycoupon=BASE_URL+"apply-coupon";
    String getcard=BASE_URL +"get-saved-card";
    String savecard=BASE_URL+"save-card";
    String deletecard=BASE_URL +"delete-saved-card";
    String suffle=BASE_URL +"shuffle";


}
