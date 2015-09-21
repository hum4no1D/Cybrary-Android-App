package com.example.cybrary02.cybrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class CoursesListActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.contains(getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("login", "UNKNOWNUSER"))) {
                    //We've been logged out!
                    getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().remove("login");
                    startActivity(new Intent(CoursesListActivity.this, LoginActivity.class));
                    Toast.makeText(CoursesListActivity.this, "Your session expired, please log in again", Toast.LENGTH_LONG).show();
                    finish();
                }

                //Server replied successfully (200)
                //Now we want to list the available courses
                ArrayList<Course> courses = new ArrayList<>();

                // Courses are formatted like this:
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

    public void initializeListView() {
        listView = (ListView) findViewById(R.id.listView);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
