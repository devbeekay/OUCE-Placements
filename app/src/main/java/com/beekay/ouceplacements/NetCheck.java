package com.beekay.ouceplacements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Krishna on 8/17/2015.
 */
public class NetCheck {
    public boolean isNetAvailable(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null && info.isConnectedOrConnecting();
    }
    static String user;

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        NetCheck.user = user;
    }

    static String pass;

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        NetCheck.pass = pass;
    }
}
