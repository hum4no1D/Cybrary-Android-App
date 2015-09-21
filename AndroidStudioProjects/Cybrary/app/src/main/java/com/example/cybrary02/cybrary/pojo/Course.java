package com.example.cybrary02.cybrary.pojo;

import java.io.Serializable;

/**
 * Created by cybrary02 on 9/19/15.
 */
public class Course implements Serializable {
    public Course(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String name;
    public String url;
}
