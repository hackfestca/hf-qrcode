package com.example.poc_hackfest_qr_reader.webapi;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.poc_hackfest_qr_reader.PassportScan;
import com.example.poc_hackfest_qr_reader.PassportVaccinal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebApiHelper {
    private static final String baseUrl = "http://127.0.0.1:8080/api/v1";
    private static final String routeRegister = baseUrl + "/register";
    private static final String routeLogin = baseUrl + "/login";
    private static final String routeScan = baseUrl + "/scan";
    private static final String routeScans = baseUrl + "/scans";
    private static final String routeTrack = baseUrl + "/track/person";

    private Context mContext;
    private String mToken;

    public WebApiHelper(Context context) {
        this.mContext = context;
    }

    private void doPostRequest(String url, JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + WebApiHelper.this.mToken);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jsonObjectRequest);
    }

    public void login(String username, String password) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("{" +
                    "\"name\": \"alice\"," +
                    "\"password\": \"alice\"" +
                    "}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, routeLogin, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("WebRequest", response.toString());
                    try {
                        WebApiHelper.this.mToken = response.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("WebRequest", error.toString());
                }
            });
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void scan(PassportVaccinal passportVaccinal) {
        try {
            JSONObject jsonObject = new JSONObject("{" +
                    "\"firstname\": \"" + passportVaccinal.getFirstName() + "\"," +
                    "\"lastname\": \"" + passportVaccinal.getFamilyName() + "\"," +
                    "\"gender\": \"" + passportVaccinal.getGender() + "\"," +
                    "\"birthday\": \"" + passportVaccinal.getBirthDate() + "\"," +
                    "\"batches\": [" +
                    "{" +
                    "\"number\": \"" + passportVaccinal.getLotNumber1() + "\"," +
                    "\"location\": \"" + passportVaccinal.getLocation1() + "\"," +
                    "\"date\": \"" + passportVaccinal.getVaccineDate1() + "\"" +
                    "}," +
                    "{" +
                    "\"number\": \"" + passportVaccinal.getLotNumber2() + "\"," +
                    "\"location\": \"" + passportVaccinal.getLocation2() + "\"," +
                    "\"date\": \"" + passportVaccinal.getVaccineDate2() + "\"" +
                    "}" +
                    "]," +
                    "\"latitude\": \"" + String.valueOf(passportVaccinal.getLatitude()) + "\"," +
                    "\"longitude\": \"" + String.valueOf(passportVaccinal.getLongitude()) + "\"" +
                    "}");
            doPostRequest(routeScan, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void scan(PassportVaccinal passportVaccinal, double longitude, double latitude) {
        try {
            JSONObject jsonObject = new JSONObject("{" +
                    "\"firstname\": \"" + passportVaccinal.getFirstName() + "\"," +
                    "\"lastname\": \"" + passportVaccinal.getFamilyName() + "\"," +
                    "\"gender\": \"" + passportVaccinal.getGender() + "\"," +
                    "\"birthday\": \"" + passportVaccinal.getBirthDate() + "\"," +
                    "\"batches\": [" +
                    "{" +
                    "\"number\": \"" + passportVaccinal.getLotNumber1() + "\"," +
                    "\"location\": \"" + passportVaccinal.getLocation1() + "\"," +
                    "\"date\": \"" + passportVaccinal.getVaccineDate1() + "\"" +
                    "}," +
                    "{" +
                    "\"number\": \"" + passportVaccinal.getLotNumber2() + "\"," +
                    "\"location\": \"" + passportVaccinal.getLocation2() + "\"," +
                    "\"date\": \"" + passportVaccinal.getVaccineDate2() + "\"" +
                    "}" +
                    "]," +
                    "\"latitude\": \"" + String.valueOf(latitude) + "\"," +
                    "\"longitude\": \"" + String.valueOf(longitude) + "\"" +
                    "}");
            doPostRequest(routeScan, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addScan(PassportScan passportScan) {
        this.scan(passportScan.getPassport(), passportScan.getLatitude(), passportScan.getLongitude());
    }

}
