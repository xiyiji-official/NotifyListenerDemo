package com.llw.notify;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpHelper {
    public static void post(String param) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://81.70.192.177:5000/received");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //设置请求方式 GET / POST 一定要大小
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(param.getBytes(StandardCharsets.UTF_8));
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code" + responseCode);
            }
            String result = String.valueOf(connection.getInputStream());
            Log.d("succuss", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}