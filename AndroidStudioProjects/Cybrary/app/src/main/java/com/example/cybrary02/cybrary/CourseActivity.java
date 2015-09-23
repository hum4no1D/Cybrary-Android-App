package com.example.cybrary02.cybrary;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.cybrary02.cybrary.adapter.VideoAdapter;
import com.example.cybrary02.cybrary.pojo.Course;
import com.example.cybrary02.cybrary.pojo.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseActivity extends AppCompatActivity {
    private ListView listView;
    private Course course;
    private VideoView vidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Revert this when Internet is back, and restore the manifest to add the LAUNCHERAintent to the LoginActivity
        course = (Course) getIntent().getSerializableExtra("course");
        // course = new Course("Linux+", "https://www.cybrary.it/course/comptia-linux-plus/");

        setTitle(course.name);

        // Add a button to the title to get back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeListView();

        CookieManager cookieManager = new CookieManager(((CybraryApplication) getApplication()).getCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        downloadVideos(course);

        vidView = (VideoView) findViewById(R.id.myVideo);
   }

    public void initializeListView() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getItemAtPosition(position);
                playVideo(video);
            }
        });
    }

    public void playVideo(final Video video) {
        // Download the list of courses from the website
        // Creating a new Volley HTTP GET request
        String reqUrl = video.url;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //We're looking for something which looks like this:
                // <iframe src="https://player.vimeo.com/video/116096483" width="500" height="281" frameborder="0"></iframe>
                // We'll use a regexp to match this in the raw HTML:
                Pattern p = Pattern.compile("player\\.vimeo\\.com/video/([0-9]+)\" width");
                Matcher m = p.matcher(response);
                while(m.find()) {
                    video.vimeoMetadataUrl = "https://player.vimeo.com/video/" + m.group(1);
                }
                downloadVideoMetadata(video);

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

    public void downloadVideoMetadata(final Video video) {
        if(video.vimeoMetadataUrl == null) {
            throw new RuntimeException("Use playVideo before calling downloadVideoMetadata");
        }
        // Download the list of courses from the website
        // Creating a new Volley HTTP GET request
        String reqUrl = video.vimeoMetadataUrl;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.contains(".mp4")) {
                    Toast.makeText(CourseActivity.this, "Can't access private Vimeo URL :(", Toast.LENGTH_LONG).show();
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
                        video.vimeoMetadata = new JSONObject(m.group(1));
                        video.videoUrl = video.vimeoMetadata.getJSONObject("request").getJSONObject("files").getJSONObject("h264").getJSONObject("sd").getString("url");
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                        Log.e("VIMEO", "Unable to parse JSON for " + m.group(1));
                        return;
                    }
                    Log.i("VIMEO", "Playing video from " + video.videoUrl);

                    Uri vidUri = Uri.parse(video.videoUrl);
                    vidView.setVideoURI(vidUri);
                    vidView.start();
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
                params.put("referer", course.url);
                params.put("user-agent", "Mozilla Firefox");

                Log.e("WTF", "REFERER IS " + course.url);
                return params;
            }
        };;

        // Send the request
        queue.add(messagesRequest);
    }

    public void downloadVideos(Course course) {
        // Download the list of courses from the website
        // Creating a new Volley HTTP GET request
        String reqUrl = course.url;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Server replied successfully (200)
                //Now we want to list the videos
                ArrayList<Video> videos = new ArrayList<>();

                // Videos are formatted like this:
                // <a href="https://www.cybrary.it/video/the-bios/" class="title">BIOS &#8211; Basic Input Output System</a>
                // We'll use a regexp to match them all in the raw HTML:
                Pattern p = Pattern.compile("cybrary\\.it/video/(.+)\" class=\"title\">([^\\>]+)\\<\\/a\\>");
                Matcher m = p.matcher(response);
                while(m.find()) {
                    String url = "https://www.cybrary.it/video/" + m.group(1);
                    String name = m.group(2);
                    Video video = new Video(name, url);
                    videos.add(video);
                }

                listView.setAdapter(new VideoAdapter(CourseActivity.this, videos));

                // Play the first video automatically
                if(videos.size() > 0) {
                    playVideo(videos.get(0));
                }
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
