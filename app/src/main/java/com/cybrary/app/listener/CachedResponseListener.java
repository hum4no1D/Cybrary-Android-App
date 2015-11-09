package com.cybrary.app.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public abstract class CachedResponseListener implements Response.Listener<String>, Response.ErrorListener {
    private static final String TAG = "CachedResponseListener";
    private final String url;
    protected Boolean usedCacheFallback = false;
    private Context context;
    private SharedPreferences sp;

    public CachedResponseListener(Context context, String url) {
        super();
        this.context = context;
        this.url = url;
        sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    @Override
    public void onResponse(String response) {
        //  Save on cache
        if (!usedCacheFallback) {
            Log.d(TAG, "Storing url in cache: " + url);
            sp.edit().putString(url, response).apply();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //  Loading failed. Do we have a copy in cache?
        if (sp.contains(url)) {
            Log.i(TAG, "Loading resource from cache, HTTP failed for " + url);
            usedCacheFallback = true;
            onResponse(sp.getString(url, ""));
        }

        error.printStackTrace();
    }
}
