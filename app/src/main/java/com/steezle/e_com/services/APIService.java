package com.steezle.e_com.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import com.google.android.gms.cast.framework.SessionManager;
import com.steezle.e_com.networking.APIs;
import com.steezle.e_com.networking.AppSharedPreferences;
import com.steezle.e_com.networking.DialogUtility;
import com.steezle.e_com.view.ShoppigbagActivity;
import com.steezle.e_com.view.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by Hiren
 */
public abstract class APIService {

    public static final String BASE_URL = APIs.get_cart;/* Live url */

    private static final String TAG = APIService.class.getSimpleName();

    private static final String UN_AUTHORIZED_MESSAGE = "Your session is expired,Please login again";






    public interface Success<T> {
        public void onSuccess(T response);
    }

    protected static void handleError(final Context context, VolleyError error) {
            Log.d(TAG, "Error :: " + error);

            if (error instanceof NoConnectionError) {

                //Toast.makeText(context, "Please check internet connection !", Toast.LENGTH_LONG).show();
                DialogUtility.alertErrorMessage(context, "Please check internet connection !");

            } else if (error.networkResponse != null) {

                Log.d(TAG, "CODE =" + error.networkResponse.statusCode);
                Log.d(TAG, new String(error.networkResponse.data));

            String msg = "";
            try {
                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                if(jsonObject.has("error_description")) {
                    msg = jsonObject.getString("error_description");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            if(error.networkResponse.statusCode == 400) {

                DialogUtility.alertErrorMessage(context, getErrorMessage(error.networkResponse.data));

            } else if((error.networkResponse.statusCode == 401 || error instanceof AuthFailureError) && !msg.equals("user not found")) {

                //Not Authorized Error Display and Redirect to Login Screen
                DialogUtility.alertErrorMessage(context, UN_AUTHORIZED_MESSAGE);
                Log.d(TAG, "Session expired(Unauthorized error)");
                AppSharedPreferences.getSharePreference(context).logOut();

                APIController.getInstance(context).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });

            } else if((error.networkResponse.statusCode == 404)) {

            } else {
                //Read Error Response and Display Error Dialog
                //DialogUtility.alertErrorMessage(context, getErrorMessage(error.networkResponse.data));
            }

        } else {
            error.printStackTrace();
            Toast.makeText(context,error.toString(), Toast.LENGTH_LONG).show();
            DialogUtility.alertErrorMessage(context, error.toString());
        }
    }

    private static String getErrorMessage(byte[] responseData) {
        String message = null;
        String code = null;
        try {

            JSONObject jsonObject = new JSONObject(new String(responseData));
            JSONObject jsonObject1 = jsonObject;
            JSONArray errorArray = null;

            if(jsonObject != null && jsonObject.has("fieldErrors"))
                errorArray = jsonObject.getJSONArray("fieldErrors");

            if(errorArray != null && errorArray.length() > 0)
                jsonObject1 = errorArray.getJSONObject(0);

            if(jsonObject1 != null && jsonObject1.has("code"))
                code = jsonObject1.getString("code");

            if(jsonObject1 != null && jsonObject1.has("message"))
                message = jsonObject1.getString("message");

            if(jsonObject1 != null && jsonObject1.has("error_description"))
                message = jsonObject1.getString("error_description");

        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, "getErrorMessage" + e.getMessage());
            message = e.getMessage();//"Something is wrong,Please try again";
        }
        return message;
    }

}
