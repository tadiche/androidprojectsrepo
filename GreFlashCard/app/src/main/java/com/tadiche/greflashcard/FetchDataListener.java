package com.tadiche.greflashcard;

import org.json.JSONArray;
import org.json.JSONObject;

public interface FetchDataListener {
    void onFetchComplete(JSONObject data);
    void onFetchComplete_JsonArray(JSONArray data);
    void onFetchFailure(String msg);
    void onFetchStart();
}
