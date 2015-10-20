package com.cybrary.app;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybrary.app.adapter.VideoAdapter;
import com.cybrary.app.listener.CachedResponseListener;
import com.cybrary.app.pojo.Course;
import com.cybrary.app.pojo.Video;
import com.google.android.gms.analytics.HitBuilders;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseActivity extends LoggedInAbstractActivity implements VideoUrlListener {
    private ListView listView;
    private Course course;
    private int currentVideoIndex = -1;
    private VideoView vidView;

    private View next;
    private View prev;

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
        final MediaController mc = new MediaController(CourseActivity.this);
        vidView.setMediaController(mc);
        mc.setMediaPlayer(vidView);

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                                          /*
                                           * and set its position on screen
                                           */
                        mc.setAnchorView(vidView);
                        mc.show(500);
                    }
                });
            }
        });

        vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("Video", "Finished playback");
                moveToVideo(1);
            }
        });

        //  Switch layouts according to landscape / portrait
        onConfigurationChanged(getResources().getConfiguration());

        next = findViewById(R.id.next_video);
        prev = findViewById(R.id.prev_video);
        final View fullscreen = findViewById(R.id.fullscreen);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToVideo(1);
            }
        });
        
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToVideo(-1);
            }
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    CourseActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    CourseActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
   }

    public void initializeListView() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getItemAtPosition(position);
                currentVideoIndex = position;
                if (video.isLocallyAvailable()) {
                    Log.i("CourseActivity", "Video " + video.getId() + " is already available for offline use.");
                    // Video is already available, no need to download it again.
                    vidView.setVideoURI(Uri.parse(video.getPotentialFileName()));
                    vidView.start();
                } else {
                    Log.i("CourseActivity", "Now retrieving metadata for " + video.getId());
                    video.getMp4Url(CourseActivity.this, CourseActivity.this);
                }
            }
        });

        listView.setAdapter(new VideoAdapter(CourseActivity.this, new ArrayList<Video>()));
    }

    public boolean canPlayVideo(int delta) {
        return currentVideoIndex + delta >= 0 && currentVideoIndex + delta < listView.getAdapter().getCount();
    }

    public void moveToVideo(int delta) {
        Log.i("CourseActivity", "Trying to move to video " + currentVideoIndex + " delta " + delta);
        if(canPlayVideo(delta)) {
            currentVideoIndex += delta;
            Video next = (Video) listView.getAdapter().getItem(currentVideoIndex);
            next.getMp4Url(CourseActivity.this, CourseActivity.this);

            // Build and send an Event.
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("course")
                    .setAction("play-video")
                    .setLabel(next.name)
                    .build());
        }

        prev.setVisibility(canPlayVideo(-1) ? View.VISIBLE : View.INVISIBLE);
        next.setVisibility(canPlayVideo(1) ? View.VISIBLE : View.INVISIBLE);
    }

    public void onConfigurationChanged (Configuration newConfig) {
        View videoPlayer = findViewById(R.id.videoWrapper);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            videoPlayer.setLayoutParams(lp);
            getSupportActionBar().hide();
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
        else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200);
            lp.weight = 1.0f;
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            videoPlayer.setLayoutParams(lp);
            getSupportActionBar().show();
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
            int color = typedValue.data;
            getWindow().getDecorView().setBackgroundColor(color);
        }

        super.onConfigurationChanged(newConfig);
    }

    public void downloadVideos(Course course) {
        // Download the list of courses from the website
        // Creating a new Volley HTTP GET request
        String reqUrl = course.url;
        RequestQueue queue = Volley.newRequestQueue(this);
        final CachedResponseListener responseListener = new CachedResponseListener(this, reqUrl) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
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
                    moveToVideo(1);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CourseActivity.this, "Unable to load videos from server. Website may be down or you have bad internet connection, try again later", Toast.LENGTH_LONG).show();
                super.onErrorResponse(error);
            }
        };
        StringRequest messagesRequest = new StringRequest(Request.Method.GET, reqUrl, responseListener, responseListener);

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
