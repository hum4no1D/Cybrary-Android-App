package com.cybrary.app;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by cybrary02 on 9/19/15.
 */
public class CybraryApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "QyyYsh6nmuLi1QRAE11CRS7oCaLvFRemWjVTI9P6", "7wWGt3OuOMzmcLHV1jQu7RgcbHvXdYVovQON6Ehl");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
    private PersistentCookieStore persistentCookieStore = null;
    private Tracker mTracker;

    public PersistentCookieStore getCookieStore(Context context) {
        if (persistentCookieStore == null) {
            persistentCookieStore = new PersistentCookieStore(context);
        }
        return persistentCookieStore;
    }


    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
