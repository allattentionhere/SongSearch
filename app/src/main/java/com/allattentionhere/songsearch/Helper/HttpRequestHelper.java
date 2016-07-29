package com.allattentionhere.songsearch.Helper;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;



public class HttpRequestHelper {

    public static String BASE_URL = "https://itunes.apple.com";

    public void MakeJsonGetRequest(final String relative_uri, ArrayMap<String, String> getData, final Datacallback db, final Context c) {

        if (relative_uri == null) return;
        else {
            final String complete_url;
            if (getData != null) {
                StringBuilder sb = new StringBuilder(100);
                sb.append(relative_uri).append("?").append("&");
                for (ArrayMap.Entry<String, String> entry : getData.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                complete_url = BASE_URL + sb.toString();
            } else {
                complete_url = BASE_URL + relative_uri + "?";
            }
            Log.i("volley", complete_url);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, complete_url, (JSONObject) null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("json get string", response.toString());
                            Log.i("volley", complete_url);
                            db.onSuccess(response, relative_uri);
                            // Check for info HTTP request
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    Log.i("volley", new String(error.networkResponse.data));
                                    db.onFailure(new JSONObject(new String(error.networkResponse.data)), relative_uri);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (error instanceof TimeoutError) {
                                try {
                                    Log.i("volley", "TimeOutError");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("fail", "TimeOutfail");
                                    db.onFailure(jsonObject, relative_uri);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                try {
                                    Log.i("volley", "failed");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("fail", "failed");
                                    db.onFailure(jsonObject, relative_uri);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            jsObjRequest.setShouldCache(false);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsObjRequest.setTag(db);
            MyApplication.Remotecalls.add(jsObjRequest);
        }
    }




}
