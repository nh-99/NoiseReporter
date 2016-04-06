package com.nohowdezign.noisereporter.network;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SendData {
    //private String url = "http://thefortress.xyz:7070/";
    private String url = "http://thefortress.xyz:81/";

    public void send(Context context, double lat, double lon, double decibelLevel) throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity entity;

        JSONObject jsonData = new JSONObject();
        jsonData.put("lat", lat);
        jsonData.put("long", lon);
        jsonData.put("decibelLevel", decibelLevel);
        Log.d("HTTP", String.valueOf(decibelLevel));

        entity = new StringEntity(jsonData.toString(), "UTF-8");
        String  contentType = "application/json";

        Log.d("HTTP", "Post...");
        client.post(context, url + "report/", entity, contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("HTTP", "Post success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
