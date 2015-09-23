package com.example.cybrary02.cybrary.pojo;

import org.json.JSONObject;

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
}
