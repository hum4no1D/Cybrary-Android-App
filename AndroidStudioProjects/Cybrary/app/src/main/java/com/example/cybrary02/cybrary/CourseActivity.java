package com.example.cybrary02.cybrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cybrary02.cybrary.adapter.CourseAdapter;
import com.example.cybrary02.cybrary.pojo.Course;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseActivity extends AppCompatActivity {
    private ListView listView;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);

        course = (Course) getIntent().getSerializableExtra("course");

        setTitle(course.name);
        Toast.makeText(this, "DISPLAYINGACOURSE " + course.url, Toast.LENGTH_LONG).show();

        // Add a button to the title to get back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeListView();

        CookieManager cookieManager = new CookieManager(((CybraryApplication) getApplication()).getCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        downloadVideos();

        VideoView vidView =  (VideoView)findViewById(R.id.myVideo);

        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        vidView.start();
        //WebView viewvideo = (WebView)findViewById(R.id.webView1);
        //viewvideo.loadData("<iframe src=\"http://player.vimeo.com/video/" + 127350537 + "\" width=\"180px\" height=\"180px\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>", "text/html", "utf-8");
    }

    public void initializeListView() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = (Course) parent.getItemAtPosition(position);
                Intent intent = new Intent(CourseActivity.this, CourseActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });
    }

    public void downloadVideos() {
        // Download the list of courses from the website
        // Creating a new Volley HTTP POST request
        String reqUrl = course.url;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Server replied successfully (200)
                //Now we want to list the videos
                ArrayList<Course> courses = new ArrayList<>();

                // Videos are formatted like this:
                // <li><a href="http://www.cybrary.it/course/ccna/">CCNA</a></li>
                // We'll use a regexp to match them all in the raw HTML:
                Pattern p = Pattern.compile("cybrary\\.it/course/(.+)\">([^\\>]+)\\<\\/a\\>\\<\\/li\\>");
                Matcher m = p.matcher(response);
                while(m.find()) {
                    String url = "https://www.cybrary.it/course/" + m.group(1);
                    String name = m.group(2);
                    Course course = new Course(name, url);
                    courses.add(course);
                }

                listView.setAdapter(new CourseAdapter(CoursesListActivity.this, courses));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_videoplayer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
