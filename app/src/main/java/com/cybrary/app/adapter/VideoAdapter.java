package com.cybrary.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cybrary.app.CourseActivity;
import com.cybrary.app.R;
import com.cybrary.app.VideoUrlListener;
import com.cybrary.app.pojo.Video;

import java.util.ArrayList;


public class VideoAdapter extends ArrayAdapter<Video> implements VideoUrlListener {
    public final static String PREFERENCE_TAG = "videoStore";
    public static int MAX_CONCURRENT_DOWNLOADS = 10;

    private final CourseActivity parent;
    public static int count = 0;

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
        final ProgressBar downloadProgress = (ProgressBar) convertView.findViewById(R.id.downloadProgress);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count >= MAX_CONCURRENT_DOWNLOADS) {
                    Toast.makeText(v.getContext(), "Can't download more than 10 videos at a time", Toast.LENGTH_LONG).show();
                    return;
                }

                count++;
                //The user wants to download the video
                video.getMp4Url(VideoAdapter.this.parent, VideoAdapter.this);
                video.isDownloading = true;
                downloadButton.setVisibility(View.INVISIBLE);
                downloadProgress.setVisibility(View.VISIBLE);
                // deleteButton.setVisibility(View.VISIBLE);
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

        if ((video.isLocallyAvailable() || video.isLocallyAvailableAlternative()) && !video.isDownloading) {
            // Video can be played offline
            downloadButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.INVISIBLE);
        } else if (!video.isDownloading) {
            //  Video can't be played offline
            downloadButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            downloadProgress.setVisibility(View.INVISIBLE);


        } else {
            //  Video is currently downloading
            downloadProgress.setVisibility(View.VISIBLE);
            downloadProgress.setProgress(video.downloadProgress);
            deleteButton.setVisibility(View.INVISIBLE);
            downloadButton.setVisibility(View.INVISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public void onUrlLoaded(final Video video) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("ADAPTER", "Downloading video " + video.videoUrl);

                video.downloadForOfflineAccess(VideoAdapter.this, parent);
                count--;

                Log.i("ADAPTER", "Downloaded video " + video.videoUrl);

                SharedPreferences downloadedVideos = getContext().getSharedPreferences(PREFERENCE_TAG, Context.MODE_PRIVATE);
                String currentVideos = downloadedVideos.getString("videos", "");
                currentVideos += "|" + video.getPotentialFileName();
                downloadedVideos.edit().putString("videos", currentVideos).apply();

                video.isDownloading = false;
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        });

        thread.start();
        Log.i("ADAPTER", "downloading video number" + count);
    }
}