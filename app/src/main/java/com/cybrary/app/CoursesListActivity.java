package com.cybrary.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybrary.app.adapter.CourseAdapter;
import com.cybrary.app.listener.CachedResponseListener;
import com.cybrary.app.pojo.Course;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CoursesListActivity extends LoggedInAbstractActivity {
    private StickyListHeadersListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cybrary.app.R.layout.activity_courses_list);

        initializeListView();

        CookieManager cookieManager = new CookieManager(((CybraryApplication) getApplication()).getCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        downloadCourses();
    }

    public void downloadCourses() {
        // Download the list of courses from the website
        // Creating a new Volley HTTP POST request
        String reqUrl = "https://www.cybrary.it/courses";
        RequestQueue queue = Volley.newRequestQueue(this);

        CachedResponseListener responseListener = new CachedResponseListener(this, reqUrl) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if(!response.contains(getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("login", "UNKNOWNUSER"))) {
                    //We've been logged out!
                    logOut();
                    return;
                }

                //Server replied successfully (200)
                //Now we want to list the available courses
                ArrayList<Course> courses = new ArrayList<>();

                //  Retrieve all categories content
                Pattern blocksPattern = Pattern.compile("<h6.+?<\\/div\\>", Pattern.DOTALL);

                // Titles are formatted like this:
                // <h6 class="bmfont"><a title="Systems Administration" href="http://www.cybrary.it/systems-administration/">Systems Administration</a></h6>
                Pattern titlePattern = Pattern.compile("h6.+a title.+\\>([^\\>]+)\\<\\/a\\>");

                // Courses are formatted like this:
                // <li><a href="http://www.cybrary.it/course/ccna/">CCNA</a></li>
                // We'll use a regexp to match them all in the raw HTML:
                Pattern coursePattern = Pattern.compile("cybrary\\.it/course/(.+)\">([^\\>]+)\\<\\/a\\>\\<\\/li\\>");

                Matcher blockMatcher = blocksPattern.matcher(response);
                while(blockMatcher.find()) {
                    //  For each category block
                    //  Extract category title
                    String block = blockMatcher.group(0);

                    String currentCategory = "Unknown category";

                    Matcher titleMatcher = titlePattern.matcher(block);
                    while(titleMatcher.find()) {
                        currentCategory = titleMatcher.group(1);
                    }

                    //  And extract all courses
                    Matcher courseMatcher = coursePattern.matcher(block);
                    while(courseMatcher.find()) {
                        String url = "https://www.cybrary.it/course/" + courseMatcher.group(1);
                        String name = courseMatcher.group(2);
                        Course course = new Course(name, url, currentCategory);
                        courses.add(course);
                    }
                }

                //  Order courses by category first, then by name
                Collections.sort(courses, new Comparator<Course>() {
                    @Override
                    public int compare(Course lhs, Course rhs) {
                        int categoryOrdering = lhs.category.compareTo(rhs.category);
                        if(categoryOrdering != 0) {
                            return categoryOrdering;
                        }

                        //  Same categories for both courses, order them by name
                        return lhs.name.compareTo(rhs.name);
                    }
                });

                listView.setAdapter(new CourseAdapter(CoursesListActivity.this, courses));
            }
        };

        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, responseListener, responseListener);

        // Send the request
        queue.add(messagesRequest);
    }

    public void initializeListView() {
        listView = (StickyListHeadersListView) findViewById(com.cybrary.app.R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = (Course) parent.getItemAtPosition(position);
                Intent intent = new Intent(CoursesListActivity.this, CourseActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });
    }
}
