package com.steezle.e_com.networking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.steezle.e_com.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonValidation {

    public static int getHeight(Activity context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        return height;
    }

    public static int getWidth(Activity context) {
        Display mDisplay = context.getWindowManager().getDefaultDisplay();
        final int width = mDisplay.getWidth();
        return width;
    }

    public static boolean CheckInternetConnection(Context activity) {

        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            Log.i("wifi", "connection available");

            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            Log.i("mobile", "connection available");

            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            Log.i("active network", "connection available");

            return true;
        } else {
            return false;
        }
    }

    private static Dialog dialog1;


    public static void hideDailog() {
        if (dialog1 != null)
            dialog1.dismiss();
    }

    public static boolean isValidMobile(EditText phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone.getText().toString())) {
            if ((phone.length() < 10 || phone.length() > 10)) {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }


    public static boolean isEdittextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() <= 0;
    }

    public static boolean isEmailIdInvalid(EditText email) {
        Pattern pattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(email.getText());
        return !(matcher.matches());
    }


    public static boolean isValidPasssword(EditText password) {
        Pattern pattern1 = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$");
        Matcher matcher1 = pattern1.matcher(password.getText().toString());
        return !(matcher1.matches());
    }


    public static boolean isAValidZipCode(String zip) {
        return Pattern.matches("[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]", zip);

//        return Pattern.matches("/(^\\d{5}$)|(^\\d{5}-\\d{4}$)/.test(\"90210\")", zip);
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";
    }

    public static void closeKeybroad(Activity v) {

        View view = v.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) v.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static void showKeyBoard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }



    public static String getString(Object object) {
        if (object instanceof TextView) {
            return ((TextView) object).getText().toString().trim();
        } else if (object instanceof EditText) {
            return ((EditText) object).getText().toString().trim();
        } else {
            return "";
        }

    }

    public static boolean isNetworkAvailable(final Context context) {
        boolean isNetAvailable = false;
        if (context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (mConnectivityManager != null) {
                boolean mobileNetwork = false;
                boolean wifiNetwork = false;

                boolean mobileNetworkConnecetd = false;
                boolean wifiNetworkConnecetd = false;

                final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mobileInfo != null) {
                    mobileNetwork = mobileInfo.isAvailable();
                }

                if (wifiInfo != null) {
                    wifiNetwork = wifiInfo.isAvailable();
                }

                if (wifiNetwork || mobileNetwork) {
                    if (mobileInfo != null)
                        mobileNetworkConnecetd = mobileInfo.isConnectedOrConnecting();
                    wifiNetworkConnecetd = wifiInfo.isConnectedOrConnecting();
                }

                isNetAvailable = (mobileNetworkConnecetd || wifiNetworkConnecetd);
            }
            if (!isNetAvailable) {
                Log.v("TAG", "context : " + context.toString());
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            new DialogUtility();

                        }
                    });

                }
            }
        }

        return isNetAvailable;
    }
}
