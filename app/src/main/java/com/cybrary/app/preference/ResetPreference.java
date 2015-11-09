package com.cybrary.app.preference;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.cybrary.app.adapter.VideoAdapter;

import java.io.File;


public class ResetPreference extends DialogPreference {

    public ResetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (which == DialogInterface.BUTTON_POSITIVE) {
            SharedPreferences downloadedVideos = getContext().getSharedPreferences(VideoAdapter.PREFERENCE_TAG, Context.MODE_PRIVATE);
            String currentVideos = downloadedVideos.getString("videos", "");

            String[] videos = currentVideos.split("\\|");
            int counter = 0;
            for (String video : videos) {
                if (!video.isEmpty()) {
                    Log.i("ResetPreference", "Cleaning " + video);
                    File f = new File(video);
                    f.delete();
                    counter += 1;
                }
            }
            downloadedVideos.edit().putString("videos", "").apply();

            Toast.makeText(getContext(), "Removed " + counter + " videos", Toast.LENGTH_LONG).show();
        }

    }

}