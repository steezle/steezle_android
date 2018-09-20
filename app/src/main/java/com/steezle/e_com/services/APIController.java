package com.steezle.e_com.services;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Hiren
 */
public class APIController {

    private static final String TAG = APIController.class.getSimpleName();

    private RequestQueue requestQueue;
    private static Context context;

    private static APIController mInstance;

    private APIController(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized APIController getInstance(Context context) {
        if(mInstance == null)
            mInstance = new APIController(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }

    public <T> void addRequest(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addRequest(Request<T> request) {
        addRequest(request, null);
    }

    public void cancelPendingRequests(Object tag) {
        if(requestQueue != null)
            getRequestQueue().cancelAll(tag);
    }
}
