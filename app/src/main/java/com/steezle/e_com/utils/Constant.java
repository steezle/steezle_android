package com.steezle.e_com.utils;

import android.os.Environment;

import com.google.firebase.appindexing.builders.StickerBuilder;

/**
 * Created by android on 7/3/18.
 */

public class Constant {
    public static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.EsyShare/";
    public static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String TEST_PHOTO = ".esy.jpg";
    public static final String PROFILE_PHOTO = ".Profile.jpg";

    public static final String STATUS = "status";
    public static final String TRUE = "S";
    public static final String FALSE = "false";
    public static final String MESSAGE = "message";
    public static final String DATA = "data";

    public static final String EVENT_IMAGE_PATH_URL = "http://www.esyshare.com/event3/assets/images/events/";
    public static final String USER_IMAGE_PATH_URL = "http://www.esyshare.com/event3/assets/images/users/";

    public static final String BASE_URL ="http://steezle.ca/wp-json/rest/";
//    public static final String BASE_URL ="http://mytestwork.com/steezle/wp-json/rest/";

    public static final String GETFVTMODEL ="GetfvtModel";
    public static final String GETCARTMODEL = "GetcartModel";

    public static class Login {

        public static final String LOGIN_URL = "login";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";

        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";

    }


    public static class SocialLogin {

        public static final String SOCIAL_LOGIN_URL = "social-login";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";

        public static final String EMAIL = "email";
        public static final String SOCIAL_DATA = "socialdata";
        public static final String SOCIAL_TYPE = "social_type";
        public static final String SOCIAL_ID = "socialid";

    }


    public static class Register {

        public static final String REGISTER_URL = "register";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String DEVICE_TOKEN = "deviceToken";
        public static final String DEVICE_TYPE = "deviceType";
        public static final String GENDER = "gender";
        public static final String CONTACT_NUMBER = "contactNumber";

    }
    public static class Categoty {

        public static final String CATEGORY_URL = "get_categories";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String NAME = "name";
        public static final String ID = "id";
        public static final String IMAGE = "image";
        public static final String BAG_COUNT = "bag_count";
        public static final String TOTAL_STEEZ = "total_steez";
    }
    public static class GetFavourite {

        public static final String GET_FAVOURITE_URL = "get-favlist";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String BAG_COUNT = "bag_count";

        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_IMAGE = "product_image";
        public static final String PRODUCT_NAME = "product_name";
        public static final String BRAND = "brand";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_PRICE = "product_price";

    }


    public static class RemoveFavourite {

        public static final String REMOVE_FAVOURITE_URL = "remove-favlist";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String BAG_COUNT = "bag_count";

        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_IMAGE = "product_image";
        public static final String PRODUCT_NAME = "product_name";
        public static final String BRAND = "brand";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_PRICE = "product_price";

    }


    public static class AddToCart {

        public static final String ADDTOCART_URL = "add-cart";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String VARIATION_ID = "cart_products";
        public static final String BAG_COUNT = "bag_count";

    }


    public static class RemoveCart {

        public static final String REMOVE_URL = "remove-cart";

        public static final String PRODUCT_ID = "product_id";
        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String VARIATION_ID = "cart_products";

    }


    public static class GetOrderList {

        public static final String ORDERLIST_URL = "order-history";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String ORDER_ID = "id";
        public static final String ORDER_NUMBER = "order_number";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
        public static final String COMPLETED_AT = "completed_at";
        public static final String ORDER_STATUS = "status";
        public static final String TOTAL = "total";
        public static final String SUBTOTAL = "subtotal";
        public static final String TOTAL_LINE_ITEM_QUANTITY = "total_line_items_quantity";
        public static final String TOTAL_TAX = "total_tax";
        public static final String TOTAL_SHIPPING = "total_shipping";
        public static final String TOTAL_DISCOUNT = "total_discount";
        public static final String USED_COUPONS = "used_coupons";
        public static final String ORDER_KEY = "order_key";
        public static final String BILLING_ADDRESS = "billing_address";
        public static final String SHIPPING_ADDRESS = "shipping_address";



    }


    public static class CancelOrder {

        public static final String CANCEL_ORDER_URL = "cancel-order";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String ORDER_ID = "order_id";
        public static final String ORDER_NUMBER = "order_number";
        public static final String CREATED_AT = "created_at";
        public static final String ORDER_STATUS = "status";
        public static final String TOTAL = "total";
        public static final String TOTAL_LINE_ITEM_QUANTITY = "total_line_items_quantity";

    }

    public static class ProductDetail {

        public static final String PRODUCT_DETAIL_URL = "product-detail";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String PRODUCT_ID = "product_id";

        public static final String IMAGES = "images";

        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String REGULAR_PRICE = "regular_price";
        public static final String SALE_PRICE = "sale_price";
        public static final String PRICE = "price";
        public static final String DESCRIPTION = "description";
        public static final String MAIN_IMAGE = "main_image";
        public static final String BRANDS = "brands";
        public static final String AUTHOR = "author";
        public static final String AUTHOR_IMAGE = "author_image";
        public static final String TOTAL_STEEZ = "total_steez";
        public static final String BAG_COUNT = "bag_count";

        //Category Array & Sub Category Array
        public static final String CAT_ID = "id";
        public static final String NAME = "name";
        public static final String PARENT = "parent";


        //Attributes Array
        public static final String ATTRIBUTES = "attributes";
        public static final String VARIATION = "variations";
        public static final String VARIATION_ATTRIBUTES = "variation_attributes";
        public static final String PA_SIZE = "pa_size";
        public static final String PA_COLOR = "pa_color";
        public static final String VARIATION_TITLE = "title";
        public static final String VARIATIONS_ATTRIBUTES = "variations_attributes";
        public static final String VARIATION_ID = "variation_id";
        public static final String VARIATION_DISPLAY_PRICE = "variation_display_price";
        public static final String VARIATION_REGULER_PRICE = "variation_reguler_price";
        public static final String QUANTITY = "qty";
        public static final String IS_IN_STOCK = "is_in_stock";
        public static final String AVAILABILITY_STOCK = "availability_stock";
        public static final String COLOR_CODE = "color_code";
        public static final String COLOR_RGB = "color_rgb";

    }


    public static class GetCartList {

        public static final String GET_CARTLIST_URL = "get-cart";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String BAG_COUNT = "bag_count";
        public static final String CART_SUBTOTAL = "cart_subtotal";
        public static final String CART = "cart";

        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_TITLE = "product_title";
        public static final String PRODUCT_IMAGE = "product_image";
        public static final String PRODUCT_NAME = "product_name";
        public static final String BRAND = "brand";
        public static final String QUANTITY = "qty";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_PRICE = "product_price";
        public static final String PRICE = "price";
        public static final String PA_SIZE = "attribute_pa_size";
        public static final String PA_COLOR = "attribute_pa_color";
    }


    public static class SetAddress {

        public static final String SET_ADDRESS_URL = "set-address";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String BILLING = "billing";
        public static final String SHIPPING = "shipping";
        public static final String NAME = "name";

        public static final String ADDRESS_1 = "address1";
        public static final String ADDRESS_2 = "address2";
        public static final String PHONE = "phone";
        public static final String COUNTRY = "country";
        public static final String STATE = "state";
        public static final String CITY = "city";
        public static final String POST_CODE = "postcode";
        public static final String EMAIL = "email";
    }

    public static class CountryList {

        public static final String GET_COUNTRY_URL = "get-countries";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String NAME = "name";
        public static final String CODE = "code";

    }


    public static class StateList {

        public static final String GET_STATE_URL = "get-state";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CODE = "code";
        public static final String COUNTRY_CODE = "country_code";

        public static final String NAME = "name";
        public static final String PRODUCT_PRICE = "product_price";
        public static final String PRICE = "price";
    }



    public static class SaveCard {

        public static final String SAVECARD_URL = "save-card";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String TOKEN = "token";
        public static final String CARD_STRING = "card_string";

    }


    public static class GetSaveCard {

        public static final String GET_SAVECARD_URL = "get-saved-card";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String TOKEN = "token";
        public static final String CARD_STRING = "card_string";

    }

    public static class DeleteSaveCard {

        public static final String DELETE_SAVECARD_URL = "delete-saved-card";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String TOKEN = "token";
        public static final String CARD_STRING = "card_string";

    }


    public static class EditProfile {

        public static final String EDIT_PROFILE_URL = "editprofile";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String GENDER = "gender";
        public static final String PROFILE_PIC = "profile_pic";

    }


    public static class ProductFilter {

        public static final String PRODUCT_FILTER_URL = "product-filters";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CATEGORY_ID = "category_id";
        public static final String BRAND_ID = "brand_id";
        public static final String PRICE_FILTERS = "price_filters";
        public static final String MIN_PRICE = "min_price";
        public static final String MAX_PRICE = "max_price";
        public static final String BRAND_FILTERS = "brand_filtters";
        public static final String BRAND = "brand";

    }


    public static class ApplyFilter {

        public static final String APPLY_FILTER_URL = "apply-filters";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String UPDATED_CART = "updated_cart";
        public static final String BRAND_ID = "brand_id";
        public static final String PRICE_FILTERS = "price_filters";
        public static final String MIN_PRICE = "min_price";
        public static final String MAX_PRICE = "max_price";
        public static final String BRAND_FILTERS = "brand_filtters";
        public static final String BRAND = "brand";

    }


    public static class CheckOut {

        public static final String CHECKOUT_URL = "checkout";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String PRODUCT_ID = "product_id";
        public static final String VARIATION_ID = "variation_id";
        public static final String QUANTITY = "qty";
        public static final String UPDATED_CART = "updated_cart";
        public static final String CART_TOTALS = "cart_totals";
        public static final String SAVED_CARD_DATA = "saved_card_data";
        public static final String TOTAL_STEEZ = "total_steez";
        public static final String BAG_COUNT = "bag_count";

    }


    public static class ForgotPassword {

        public static final String FORGOT_URL = "forgot_password";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String EMAIL = "email";

    }


    public static class LogOut {

        public static final String LOGOUT_URL = "logout";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

    }


    public static class MySteezSwipe {

        public static final String MYSTEEZ_SWIPE_URL = "get-favlist";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String BAG_COUNT = "bag_count";
        public static final String TOTAL_CART = "total_cart";
        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_IMAGE = "product_image";
        public static final String PRODUCT_NAME = "product_name";
        public static final String BRAND = "brand";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_PRICE = "product_price";


    }


    public static class OrderDetails {

        public static final String ORDER_DETAIL_URL = "order-detail";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String ORDER_ID = "order_id";

    }


    public static class SubCategories {

        public static final String GET_SUBCATEGORY_URL = "get_subcategories";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String ID = "id";
        public static final String PAGED = "paged";
        public static final String PRODUCT_NAME = "product_name";
        public static final String BRAND = "brand";
        public static final String PRODUCT_TYPE = "product_type";
        public static final String PRODUCT_PRICE = "product_price";

    }


    public static class ApplyCoupon {

        public static final String APPLY_COUPON_URL = "apply-coupon";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CODE = "code";
        public static final String DISCOUNT = "discount";

    }


    public static class RemoveCoupon {

        public static final String REMOVE_COUPON_URL = "remove-coupon";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CODE = "code";
        public static final String DISCOUNT = "discount";

    }


    public static class SearchList {

        public static final String SEARCH_URL = "search";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String SEARCH_TEXT = "s";

    }


    public static class MakePayment {

        public static final String PAYMENT_URL = "payment";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String OLD_TOKEN = "old_token";
        public static final String NEW_TOKEN = "new_token";
        public static final String SAVED = "saved";


    }

    public static class Suffle {

        public static final String SUFFLE_URL = "shuffle";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String BAG_COUNT = "bag_count";
        public static final String TOTAL_STEEZ = "total_steez";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String PRICE = "price";
        public static final String DESCRIPTION = "description";
        public static final String MAIN_IMAGE = "main_image";
        public static final String BRANDS = "brands";
        public static final String PAGED = "paged";
        public static final String PAGES = "pages";

    }


    public static class ProductList {

        public static final String PRODUCTLIST_URL = "productlist";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CATEGORY_ID = "category_id";
        public static final String BAG_COUNT = "bag_count";
        public static final String TOTAL_STEEZ = "total_steez";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String PRICE = "price";
        public static final String DESCRIPTION = "description";
        public static final String MAIN_IMAGE = "main_image";
        public static final String BRANDS = "brands";
        public static final String PAGED = "paged";
        public static final String PAGES = "pages";
    }


    public static class AddToFavourite {

        public static final String ADDTOFAVOURITE_URL = "add-favlist";

        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";

        public static final String CATEGORY_ID = "category_id";
        public static final String BAG_COUNT = "bag_count";
        public static final String TOTAL_STEEZ = "total_steez";
        public static final String PRODUCT_ID = "product_id";
        public static final String TITLE = "title";
        public static final String PRICE = "price";
        public static final String DESCRIPTION = "description";
        public static final String MAIN_IMAGE = "main_image";
        public static final String BRANDS = "brands";
        public static final String PAGED = "paged";
        public static final String PAGES = "pages";
    }

}
