package com.example.vitaly.authapp1.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecretAuth {

    public static String getSecret(Context context) {

        String secret = null;
        ConnectivityManager mngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netI = mngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (netI.isConnected()) {
            final WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo ci = wm.getConnectionInfo();
            if (ci != null && !TextUtils.isEmpty(ci.getSSID())) {
                secret = ci.getSSID().replaceAll("\"", "");
            }
        }
        return secret;
    }

    public static String processSecret(String secret) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(secret.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexStr = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexStr.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexStr.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
