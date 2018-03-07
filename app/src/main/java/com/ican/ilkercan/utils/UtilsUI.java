package com.ican.ilkercan.utils;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ican.ilkercan.places.R;

/**
 * Created by ilkercan on 13/02/2018.
 */

public class UtilsUI {
    public static void ShowProgressDialog(ProgressDialog progressDialog, boolean show)
    {
        if(show) {
            progressDialog.show();
        }
        else
        {
            progressDialog.hide();
        }
    }
    public static void ShowMessage(AppCompatActivity activity, String message)
    {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog GetProgressDialog(AppCompatActivity activity)
    {
        ProgressDialog progressDialog = new ProgressDialog(activity, R.style.MyAlertDialogStyle);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return progressDialog;
    }

    public static String GetSharedPreferenceValue(AppCompatActivity activity, String keyName)
    {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return app_preferences.getString(keyName, null);
    }

    public static void SetSharedPreferenceValue(AppCompatActivity activity, String keyName, String value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(keyName,value);
        editor.apply();

        editor.commit();
    }

}
