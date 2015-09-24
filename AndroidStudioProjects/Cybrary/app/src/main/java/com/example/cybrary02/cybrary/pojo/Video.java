package com.example.cybrary02.cybrary.pojo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cybrary02.cybrary.VideoUrlListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cybrary02 on 9/21/15.
 */
public class Video {
    public Video(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String name;
    public String url;
    public String vimeoMetadataUrl = null;
    public JSONObject vimeoMetadata = null;
    public String videoUrl = null;

    /*
     * Returns an id identifying this video among others, using the URL hashcode.
     * This id can be used to check if the video is already available in the cache
     */
    public int getId() {
        return url.replace("https://www.cybary.it", "").hashCode();
    }

    /**
     * Get the video mp4 URL nd notify the specified listener.
     * @return
     */
    public void getMp4Url(Context context, VideoUrlListener listener) {

        // URLAalready loaded
        if(videoUrl != null) {
            listener.onUrlLoaded(this);
            return;
        }

        retrieveVimeoUrl(context, listener);
    }

    private void downloadVideoMetadata(final Context context, final VideoUrlListener listener) {
        if(vimeoMetadataUrl == null) {
            throw new RuntimeException("Use retrieveVimeoUrl before calling downloadVideoMetadata");
        }

        // Download the list of courses from the website
        // Creating a new Volley HTTP GET request
        String reqUrl = vimeoMetadataUrl;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.contains(".mp4")) {
                    Toast.makeText(context, "Can't access private Vimeo URL :(", Toast.LENGTH_LONG).show();
                    Log.e("VIMEO", response);
                    return;
                }

                //We're looking for something which looks like this:
                // <script>(function(e,a){var t=JSONJSON</script>
                // We'll use a regexp to match this in the raw HTML:
                Pattern p = Pattern.compile("\\<script\\>\\(function\\(e,a\\)\\{var t=(.+)\\<\\/script\\>", Pattern.MULTILINE);
                Matcher m = p.matcher(response);
                while(m.find()) {
                    try {
                        vimeoMetadata = new JSONObject(m.group(1));
                        videoUrl = vimeoMetadata.getJSONObject("request").getJSONObject("files").getJSONObject("h264").getJSONObject("sd").getString("url");
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                        Log.e("VIMEO", "Unable to parse JSON for " + m.group(1));
                        return;
                    }
                    Log.i("VIMEO", "Playing video from " + videoUrl);

                    listener.onUrlLoaded(Video.this);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Some server error, or no network connectivity
                error.printStackTrace();
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("referer", url);
                params.put("user-agent", "Mozilla Firefox");

                return params;
            }
        };;

        // Send the request
        queue.add(messagesRequest);
    }

    private void retrieveVimeoUrl(final Context context, final VideoUrlListener listener) {
        // Download the Cybary video page, and retrieve the vimeo URL
        // Creating a new Volley HTTP GET request
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //We're looking for something which looks like this:
                // <iframe src="https://player.vimeo.com/video/116096483" width="500" height="281" frameborder="0"></iframe>
                // We'll use a regexp to match this in the raw HTML:
                Pattern p = Pattern.compile("player\\.vimeo\\.com/video/([0-9]+)\" width");
                Matcher m = p.matcher(response);
                while(m.find()) {
                    vimeoMetadataUrl = "https://player.vimeo.com/video/" + m.group(1);
                }
                //ANext steps: download vimeo metadata
                downloadVideoMetadata(context, listener);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Some server error, or no network connectivity
                error.printStackTrace();
            }
        });

        // Send the request
        queue.add(messagesRequest);
    }
}
