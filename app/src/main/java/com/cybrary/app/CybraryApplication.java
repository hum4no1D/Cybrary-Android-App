package com.cybrary.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by cybrary02 on 9/19/15.
 */
public class CybraryApplication extends Application {
    private PersistentCookieStore persistentCookieStore = null;

    public PersistentCookieStore getCookieStore(Context context) {
        if(persistentCookieStore == null) {
            persistentCookieStore = new PersistentCookieStore(context);
        }
        return persistentCookieStore;
    }
}
