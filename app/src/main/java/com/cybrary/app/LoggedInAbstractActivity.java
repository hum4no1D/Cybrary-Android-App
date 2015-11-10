package com.cybrary.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by cybrary02 on 9/28/15.
 */
public abstract class LoggedInAbstractActivity extends AppCompatActivity {
    Tracker mTracker;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loggedin, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CybraryApplication application = (CybraryApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }


    @Override
    protected void onResume() {
        Log.e("Analytics", "Class name:" + getClass().getSimpleName());
        mTracker.setScreenName(getClass().getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            logOut();
            return true;
        } else if (id == R.id.action_support) {
            Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/members/cybrarysupport/#gform_4"));
            startActivity(supportIntent);
            return true;
        } else if (id == R.id.action_forum) {
            Intent forumIntent = new Intent(this, WebviewActivity.class);
            forumIntent.putExtra("url", "https://www.cybrary.it/forums/#forums-list-0");
            startActivity(forumIntent);
            return true;
        } else if (id == R.id.action_rate) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.cybrary.app");
            Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(gotoMarket);
        } else if (id == R.id.action_jobs) {
            Intent jobsIntent = new Intent(this, WebviewActivity.class);
            jobsIntent.putExtra("url", "https://www.cybrary.it/cyber-security-jobs/");
            startActivity(jobsIntent);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            String login = getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("login", "");
            Intent profileIntent = new Intent(this, WebviewActivity.class);
            profileIntent.putExtra("url", "https://www.cybrary.it/members/" + login + "/");
            startActivity(profileIntent);
            return true;
        } else if (id == R.id.action_cybytes) {
            Intent cybytesIntent = new Intent(this, WebviewActivity.class);
            cybytesIntent.putExtra("url", "https://www.cybrary.it/cybytes/");
            startActivity(cybytesIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        // Log the user out
        getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().remove("login").commit();
        ((CybraryApplication) getApplication()).getCookieStore(this).removeAll();
        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }
}
