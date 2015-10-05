package com.cybrary.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by cybrary02 on 9/28/15.
 */
public abstract class LoggedInAbstractActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loggedin, menu);
        return true;
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
        }
        else if(id == R.id.action_support) {
            Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/members/cybrarysupport/#gform_4"));
            startActivity(supportIntent);
            return true;
        }
        else if(id == R.id.action_forum) {
            Intent forumIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/forums/#forums-list-0"));
            startActivity(forumIntent);
            return true;
        }
        else if(id == R.id.action_rate) {

            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.cybrary.app");
            Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(gotoMarket);
        }else if(id == R.id.action_jobs) {

            Intent jobsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/cyber-security-jobs/"));
            startActivity(jobsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        // Log the user out
        getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().remove("login").commit();
        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
        finish();
    }
}
