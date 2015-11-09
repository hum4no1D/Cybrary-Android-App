package com.cybrary.app.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.cybrary.app.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cybrary02 on 11/9/15.
 *
 */
public class CybraryRequest extends StringRequest {
    public CybraryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders(){
        String versionCode = BuildConfig.VERSION_NAME;
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", "Cybrary Android App v" + versionCode);
        return headers;
    }
}
