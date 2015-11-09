package com.cybrary.app.pojo;

import java.io.Serializable;

/**
 * Created by cybrary02 on 9/19/15.
 */
public class Course implements Serializable {
    public String name;
    public String url;
    public String category;
    public Course(String name, String url, String category) {
        this.name = name;
        this.url = url;
        this.category = category;
    }
}
