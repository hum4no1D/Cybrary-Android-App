package com.example.cybrary02.cybrary.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cybrary02.cybrary.R;
import com.example.cybrary02.cybrary.pojo.Video;

import java.util.ArrayList;


public class VideoAdapter extends ArrayAdapter<Video> {
    public VideoAdapter(Context context, ArrayList<Video> videos) {
        super(context, 0, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Video video = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
        }

        TextView messageName = (TextView) convertView.findViewById(R.id.video_name);
        messageName.setText(Html.fromHtml(video.name));

        // Return the completed view to render on screen
        return convertView;
    }
}