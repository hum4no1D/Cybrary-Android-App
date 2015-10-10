package com.cybrary.app.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybrary.app.CourseActivity;
import com.cybrary.app.R;
import com.cybrary.app.VideoUrlListener;
import com.cybrary.app.pojo.Video;

import java.util.ArrayList;


public class VideoAdapter extends ArrayAdapter<Video> implements VideoUrlListener {
    public final static String PREFERENCE_TAG = "videoStore";

    private ProgressDialog dialog;
    private CourseActivity parent;

    public VideoAdapter(CourseActivity context, ArrayList<Video> videos) {
        super(context, 0, videos);
        this.parent = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Video video = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
        }

        TextView messageName = (TextView) convertView.findViewById(R.id.video_name);
        messageName.setText(Html.fromHtml(video.name));

        final ImageView downloadButton = (ImageView) convertView.findViewById(R.id.downloadButton);
        final ImageView deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The user wants to download the video
                dialog = ProgressDialog.show(getContext(), "Downloading video",
                        "Downloading. Please wait...", true);
                dialog.show();
                VideoAdapter.this.parent.pauseVideo();
                video.getMp4Url(getContext(), VideoAdapter.this);
                downloadButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.removeLocalCopy();
                downloadButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            }
        });

        if(video.isLocallyAvailable()) {
            downloadButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
        else {
            downloadButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public void onUrlLoaded(final Video video) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.i("ADAPTER", "Downloading video " + video.videoUrl);

                video.downloadForOfflineAccess(parent, dialog);

                Log.i("ADAPTER", "Downloaded video " + video.videoUrl);

                SharedPreferences downloadedVideos = getContext().getSharedPreferences(PREFERENCE_TAG, Context.MODE_PRIVATE);
                String currentVideos = downloadedVideos.getString("videos", "");
                currentVideos += "|" + video.getPotentialFileName();
                downloadedVideos.edit().putString("videos", currentVideos).apply();

                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        thread.start();
    }
}