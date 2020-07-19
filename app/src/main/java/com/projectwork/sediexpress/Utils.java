package com.projectwork.sediexpress;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.location.LocationManagerCompat;

import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static final String PENDING = "pending";
    public static final String COMPLETE = "complete";
    public static final String APPROVED = "approved";
    public static final String PAID = "paid";
    private static final String SMS_CLIENT_IDD = "wzyaaoyd";
    private static final String SMS_SECRETT = "hizayhei";
    private static final String KEY = "a6e0a18bb84a73cdce75";
    private static final String SENDER_ID = "sediMobile";
    public static String ADMIN = "admin";

//
//    public static void hideProgressDialog(Context context) {
//        ProgressDialog mProgressDialog = new ProgressDialog(context);
//            mProgressDialog.dismiss();
//    }
//
//    public static void showProgressDialog(Context context, String title, String msg) {
//    ProgressDialog.show(context, title, msg, true, true);
//    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    static class SMS_SEND extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            try {
                String recipient = args[0];
                String msg = args[1];


                String strUrl = "http://bulk.mnotify.net/smsapi?key=" + KEY + "&to=" + recipient + "&msg=" + msg + "&sender_id=" + SENDER_ID;
                Log.d("TAG", "doInBackground: " + strUrl);
                URL url = new URL(strUrl);
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                System.out.println(uc.getResponseMessage());
                uc.disconnect();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            return null;
        }
    }

}
