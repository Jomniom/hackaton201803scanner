package com.superaligator.hackathonscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;


public class MyProgressDialog {

    public static ProgressDialog show(Context ctx) {
        String message = "Poczekaj...";
        return show(ctx, message, null);
    }

    public static ProgressDialog show(Context ctx, DialogInterface.OnCancelListener cancelListener) {
        String message = "Poczekaj...";
        return show(ctx, message, cancelListener);
    }

    public static ProgressDialog show(Context ctx, String message, DialogInterface.OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        if (cancelListener != null) {
            dialog.setOnCancelListener(cancelListener);
            dialog.setCancelable(true);
        }
        dialog.show();
        return dialog;
    }

}
