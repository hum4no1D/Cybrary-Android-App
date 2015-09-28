package com.example.cybrary02.cybrary.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cybrary02.cybrary.R;
import com.example.cybrary02.cybrary.VideoUrlListener;
import com.example.cybrary02.cybrary.pojo.Video;

import java.util.ArrayList;


public class VideoAdapter extends ArrayAdapter<Video> implements VideoUrlListener {
    private ProgressDialog dialog;

    public VideoAdapter(Context context, ArrayList<Video> videos) {
        super(context, 0, videos);
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
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The user wants to download the video
                dialog = ProgressDialog.show(getContext(), "Downloading video",
                        "Downloading. Please wait...", true);
                dialog.show();
                video.getMp4Url(getContext(), VideoAdapter.this);
            }
        });
        if(video.isLocallyAvailable()) {
            downloadButton.setVisibility(View.INVISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }


    @Override
    public void onUrlLoaded(final Video video) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.e("ADAPTER", "Downloading video " + video.videoUrl);

                video.downloadForOfflineAccess();

                Log.e("ADAPTER", "Downloaded video " + video.videoUrl);
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        thread.start();
    }
}