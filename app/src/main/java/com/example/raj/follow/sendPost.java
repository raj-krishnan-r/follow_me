package com.example.raj.follow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

class sendPost extends AsyncTask<JSONObject, Void, String> {

    Context c;
    int taskcode;
    URL url;

    public sendPost(Context a, int taske, URL nurl) {
        c = a;
        taskcode = taske;
        url = nurl;
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        try {
            //URL url = new URL(urrl);


            JSONObject postDataParams = params[0];

        /*
        postDataParams.put("name","Apple");
        postDataParams.put("id","123");
        **/

            Log.e("params", postDataParams.toString());


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            int rcode = conn.getResponseCode();
            if (rcode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();

            } else {
                return new String("false : " + rcode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());

        }


    }


    @Override
    protected void onPostExecute(String result) {


        if (taskcode == 1) {
                if (result.equals("0")) {
                    Toast.makeText(c, "Login credentials wrong !", Toast.LENGTH_LONG).show();

                } else {

                    Intent i = new Intent(c, locTransfer.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this is apparently getting ignored... (ps: i've tried i.setFlags as well)
                    i.putExtra("datum", result);
                    c.startActivity(i);
                }
            }


        if (taskcode == 2) {

            //Toast.makeText(c, result, Toast.LENGTH_SHORT).show();

        }


    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }



}




