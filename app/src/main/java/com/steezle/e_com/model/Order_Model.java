package com.steezle.e_com.model;

import java.io.Serializable;

/**
 * Created by juli on 22/12/17.
 */

public class Order_Model implements Serializable {

    String id, order_number, created_at, updated_at, completed_at, status, currency, total, subtotal,
            total_line_items_quantity, total_tax, total_shipping, cart_tax, shipping_tax, total_discount,
            used_coupons, order_key, billing_address, shipping_address;

    public Order_Model(String id, String order_number, String created_at, String updated_at, String completed_at, String status, String currency, String total, String subtotal, String total_line_items_quantity, String total_tax, String total_shipping, String cart_tax, String shipping_tax, String total_discount, String used_coupons, String order_key, String billing_address, String shipping_address) {
        this.id = id;
        this.order_number = order_number;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.completed_at = completed_at;
        this.status = status;
        this.currency = currency;
        this.total = total;
        this.subtotal = subtotal;
        this.total_line_items_quantity = total_line_items_quantity;
        this.total_tax = total_tax;
        this.total_shipping = total_shipping;
        this.cart_tax = cart_tax;
        this.shipping_tax = shipping_tax;
        this.total_discount = total_discount;
        this.used_coupons = used_coupons;
        this.order_key = order_key;
        this.billing_address = billing_address;
        this.shipping_address = shipping_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal_line_items_quantity() {
        return total_line_items_quantity;
    }

    public void setTotal_line_items_quantity(String total_line_items_quantity) {
        this.total_line_items_quantity = total_line_items_quantity;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(String total_tax) {
        this.total_tax = total_tax;
    }

    public String getTotal_shipping() {
        return total_shipping;
    }

    public void setTotal_shipping(String total_shipping) {
        this.total_shipping = total_shipping;
    }

    public String getCart_tax() {
        return cart_tax;
    }

    public void setCart_tax(String cart_tax) {
        this.cart_tax = cart_tax;
    }

    public String getShipping_tax() {
        return shipping_tax;
    }

    public void setShipping_tax(String shipping_tax) {
        this.shipping_tax = shipping_tax;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getUsed_coupons() {
        return used_coupons;
    }

    public void setUsed_coupons(String used_coupons) {
        this.used_coupons = used_coupons;
    }

    public String getOrder_key() {
        return order_key;
    }

    public void setOrder_key(String order_key) {
        this.order_key = order_key;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }
}
