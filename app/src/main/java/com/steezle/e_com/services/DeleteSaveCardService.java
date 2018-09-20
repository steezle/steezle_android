package com.steezle.e_com.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.steezle.e_com.R;
import com.steezle.e_com.utils.Constant;
import com.steezle.e_com.utils.ProjectUtility;

import java.util.HashMap;
import java.util.Map;

public class DeleteSaveCardService extends APIService {

    private static final String TAG = DeleteSaveCardService.class.getSimpleName();

    private static final String USER_DETAIL_URL = Constant.BASE_URL + Constant.DeleteSaveCard.DELETE_SAVECARD_URL;

    public static void deleteSaveCard(
            final Context context,
            final String user_id,
            final String token,
            final ProgressBar dialog,
            final Success<String> successListener) {


        final ProgressDialog progressDialog =new ProgressDialog(context, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        // Initialize a new StringRequest
        StringRequest postRequest = new StringRequest(Request.Method.POST, USER_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.i(TAG, "Response==>" + response);
                            successListener.onSuccess(new String(response));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        handleError(context, error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.DeleteSaveCard.USER_ID, user_id);
                params.put(Constant.DeleteSaveCard.TOKEN, token);

                ProjectUtility.queryString("deleteSaveCard", USER_DETAIL_URL, params);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        APIController.getInstance(context).addRequest(postRequest, TAG);
    }
}
