package com.steezle.e_com.networking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.steezle.e_com.R;


/**
 * Created by Hiren
 */
public class DialogUtility {

    private static final String TAG = DialogUtility.class.getSimpleName();

    private static final String SUCCESS_MESSAGE = "Records submitted successfully.";

    private static final String ERROR_MESSAGE = "Oops !! Please try again later";



    public static void alertSuccessMessage(Context context) {
        alertSuccessMessage(context, SUCCESS_MESSAGE);
    }

    public static void alertSuccessMessage(Context context, String successMessage) {
        successMessage = (successMessage != null && successMessage.trim().length() > 0) ? successMessage : SUCCESS_MESSAGE;
        alertDialog(context, ResponseType.SUCCESS, successMessage);
    }

    public static void alertErrorMessage(Context context) {
        alertErrorMessage(context, ERROR_MESSAGE);
    }

    public static AlertDialog alertErrorMessage(Context context, String errorMessage) {
        errorMessage = (errorMessage != null && errorMessage.trim().length() > 0) ? errorMessage : ERROR_MESSAGE;
        return  alertDialog(context, ResponseType.ERROR, errorMessage);
    }

    private static AlertDialog alertDialog(Context context, ResponseType responseType, String message) {

        if(context != null) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate( R.layout.view_dialog, null);
//            ImageView dialogIcon = (ImageView) view.findViewById(R.id.dialogIcon);
            TextView dialogMessage = (TextView) view.findViewById(R.id.dialogMessage);
            Button btn_alert_ok = (Button) view.findViewById(R.id.btn_alert_ok);
            dialogMessage.setText(message);
           if (ResponseType.SUCCESS.equals(responseType)) {
//                dialogIcon.setImageResource(R.mipmap.ic_rightmark);
                dialogMessage.setTextColor(Color.GREEN);
            } else {
//                dialogIcon.setImageResource(R.mipmap.ic_crossmark);
                dialogMessage.setTextColor(Color.RED);
            }
            alert.setView(view);
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
            //alert.setPositiveButton();

            btn_alert_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            return alertDialog;
        }
        return  null;
    }

    public static ProgressDialog processDialog(Context context, String message, boolean isCancelable) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(isCancelable);
        dialog.show();
        return dialog;
    }


    public static ProgressDialog processDialogWith(Context context, String message, boolean isCancelable) {
        final ProgressDialog dialog = new ProgressDialog(context);
        //dialog.setMax(1);
        dialog.setTitle("ProgressDialog bar example");
        dialog.setMessage(message);
        dialog.setCancelable(isCancelable);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();


        return dialog;
    }
    enum ResponseType {
        SUCCESS, ERROR
    }
}
