package com.allattentionhere.songsearch.Helper;

import org.json.JSONObject;


public interface Datacallback {
     void onSuccess(JSONObject success, String uri);
     void onFailure(JSONObject failure, String uri);
}
