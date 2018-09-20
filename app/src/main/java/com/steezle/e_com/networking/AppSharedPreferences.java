package com.steezle.e_com.networking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.google.gson.Gson;

import com.steezle.e_com.model.User;
import com.steezle.e_com.view.LoginActivity;


public class AppSharedPreferences {

    private static AppSharedPreferences appSharedPreferences_ref;
    private SharedPreferences appSharedPreferences;
    private String MyPREFERENCES = "com_solynta";
    private Editor prefsEditor;
    private Context context;


    public static AppSharedPreferences getSharePreference(Context context) {
        context = context;
        if (appSharedPreferences_ref == null) {
            appSharedPreferences_ref = new AppSharedPreferences();
            appSharedPreferences_ref.appSharedPreferences = context.getSharedPreferences(appSharedPreferences_ref.MyPREFERENCES, Context.MODE_PRIVATE);
            appSharedPreferences_ref.prefsEditor = appSharedPreferences_ref.appSharedPreferences.edit();
        }
        return appSharedPreferences_ref;
    }

    public void setIsLogin(boolean isLogin) {
        prefsEditor.putBoolean("isLogin", isLogin);
        prefsEditor.commit();
    }

    public void setLoginType(boolean isLogin) {
        prefsEditor.putBoolean("LoginType", isLogin);
        prefsEditor.commit();
    }
    public boolean isLoginType() {
        return appSharedPreferences.getBoolean("LoginType", false);
    }
    public boolean isLogin() {
        return appSharedPreferences.getBoolean("isLogin", false);
    }

    public void setIsFirstTime(boolean isFirstTime) {
        prefsEditor.putBoolean("isFirstTime", isFirstTime);
        prefsEditor.commit();
    }

    public boolean isFirstTime() {
        return appSharedPreferences.getBoolean("isFirstTime", true);
    }

    public void setShowVideo(boolean isShowVideo) {
        prefsEditor.putBoolean("showVideo", isShowVideo);
        prefsEditor.commit();
    }

    public boolean isShowVideo() {
        return appSharedPreferences.getBoolean("showVideo", false);
    }

    public void setUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
    }

    public void logOut() {
        String regID = getRegID();
        prefsEditor.clear();
        prefsEditor.commit();
        setRegID(regID);
        setShowVideo(true);
        setIsLogin(false);
        setLoginType(false);
        prefsEditor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void ClearLogout() {
        String regID = getRegID();
        prefsEditor.clear();
        prefsEditor.commit();
        setRegID(regID);
        setShowVideo(true);
        setIsLogin(false);
        setLoginType(false);
        prefsEditor.commit();
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = appSharedPreferences.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        return obj;
    }

    public void setCartCount(String cartCount) {
        prefsEditor.putString("cart", cartCount);
        prefsEditor.commit();
    }

    public String getCartCount() {
        return appSharedPreferences.getString("cart", "0");
    }

    public void setTotalSteez(String cartCount) {
        prefsEditor.putString("total_steez", cartCount);
        prefsEditor.commit();
    }

    public String getTotalSteez() {
        return appSharedPreferences.getString("total_steez", "0");
    }

    public void setRegID(String regid) {
        prefsEditor.putString("regid", regid);
        prefsEditor.commit();
    }

    public String getRegID() {
        return appSharedPreferences.getString("regid", "0");
    }

    public void setLang(String lang) {
        prefsEditor.putString("lang", lang);
        prefsEditor.commit();
    }

    public String getLang() {
        return appSharedPreferences.getString("lang", "en");
    }

    public void clearData() {
        setUser(null);
    }

    public void clearData(String str) {
        if (str.equalsIgnoreCase("cart")) {

        }
    }

    public String getCountryID() {
        return appSharedPreferences.getString("country", "5");
    }

    public void setCountryID(String lang) {
        prefsEditor.putString("country", lang);
        prefsEditor.commit();
    }

    public void setCountryName(String s) {
        prefsEditor.putString("countryname", s);
        prefsEditor.commit();
    }

    public String getCountryName() {
        return appSharedPreferences.getString("countryname", "Suadi Arabia");
    }

    public void setEventOwnerID(String s) {
        prefsEditor.putString("eventOwnerID", s);
        prefsEditor.commit();
    }

    public String getEventOwnerID() {
        return appSharedPreferences.getString("eventOwnerID", "");
    }

    public void setNotificationCount(String count) {
        prefsEditor.putString("notificationCount", count);
        prefsEditor.commit();
    }

    public String getNotificationCount() {
        return appSharedPreferences.getString("notificationCount", "");
    }

    public void setProfileUrl(String URL) {
        prefsEditor.putString("profileURL", URL);
        prefsEditor.commit();
    }

    public String getProfileUrl() {
        return appSharedPreferences.getString("profileURL", "");
    }

}
