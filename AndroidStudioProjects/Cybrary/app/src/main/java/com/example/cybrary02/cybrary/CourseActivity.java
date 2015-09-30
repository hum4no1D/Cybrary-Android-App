package com.example.cybrary02.cybrary;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseActivity extends LoggedInAbstractActivity implements VideoUrlListener {
    private ListView listView;
    private Course course;
    private VideoView vidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);



        // restore the manifest to add the LAUNCHERAintent to the LoginActivity
        course = (Course) getIntent().getSerializableExtra("course");

        setTitle(course.name);

        // Add a button to the title to get back to the previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeListView();

        CookieManager cookieManager = new CookieManager(((CybraryApplication) getApplication()).getCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        downloadVideos(course);

        vidView = (VideoView) findViewById(R.id.myVideo);

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                          /*
                                           *  add media controller
                                           */
                        MediaController mc = new MediaController(CourseActivity.this);
                        ;
                        vidView.setMediaController(mc);
                                          /*
                                           * and set its position on screen
                                           */
                        mc.setAnchorView(vidView);
                    }
                });
            }
        });
   }

    public void initializeListView() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getItemAtPosition(position);
                if(video.isLocallyAvailable()) {
                    Log.i("CourseActivity", "Video " + video.getId() + " is already available for offline use.");
                    // Video is already available, no need to download it again.
                    vidView.setVideoURI(Uri.parse(video.getPotentialFileName()));
                    vidView.start();
                }
                else {
                    Log.i("CourseActivity", "Now retrieving metadata for " + video.getId());
                    video.getMp4Url(CourseActivity.this, CourseActivity.this);
                }
            }
        });
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
                //list the videos
                ArrayList<Video> videos = new ArrayList<>();

                // Videos are formatted like this:
                // <a href="https://www.cybrary.it/video/the-bios/" class="title">BIOS &#8211; Basic Input Output System</a>
                // use a regexp to match them all in the raw HTML:
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
                    videos.get(0).getMp4Url(CourseActivity.this, CourseActivity.this);
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
    public void onUrlLoaded(Video video) {
        Uri vidUri = Uri.parse(video.videoUrl);
        vidView.setVideoURI(vidUri);
        vidView.start();
    }
}
