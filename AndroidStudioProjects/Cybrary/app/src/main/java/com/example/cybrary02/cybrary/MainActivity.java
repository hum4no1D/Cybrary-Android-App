package com.example.cybrary02.cybrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    EditText user,pass;
    TextView tv;
    ProgressDialog dialog = null;

    public void Log_in(View e){
        user = (EditText)findViewById(R.id.Login);
        pass = (EditText)findViewById(R.id.Senha);
        tv = (TextView)findViewById(R.id.Situacao);
        dialog = ProgressDialog.show(MainActivity.this, "", "Validating user...", true);

        String reqUrl = "https://www.cybrary.it/wp-login.php";
        final String log = user.getText().toString().trim();
        final String pwd = pass.getText().toString().trim();

        if(log.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(MainActivity.this, "You need to fill in both fields!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }

        //ACreating a new Volley HTTP POST request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.POST, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Server replied successfully (200)
                //Now we want to check if the login was successful too
                //So we have to ensure we had a valid username AND a valid password
                if (!response.contains("Invalid username") && !response.contains("The password you entered for the username") && response.contains(log + "&gt; on Cybrary")) {
                    Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    tv.setText("Login Successfully");

                    //ANow, start ListActivity
                    startActivity(new Intent(MainActivity.this, ListActivity.class));
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(MainActivity.this, "Login failure :(", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    //ACan't find TVAchat on your window anymore
                    Log.i("MainActivity", "Login failure, server replied: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Some server error, or no network connectivity
                error.printStackTrace();

                //ADepending on your wordpress configuration you may need to move this code higher, near the "login failure" instead
                //Ai don't know if wordpress replies with custom http status code
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Login Error.");
                        builder.setMessage("User not Found.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        dialog.dismiss();

                    }
                });
            }
        }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("log", log);
                params.put("pwd", pwd);
                return params;
            };
        };


        // Send the request
        queue.add(messagesRequest);
    }


    public void RegUri(View c){
        Intent RegisterBtn = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/register/"));

        startActivity(RegisterBtn);
    }


    public void EsqASenha (View d){
        Intent Recuperar = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/wp-login.php?action=lostpassword"));
        startActivity(Recuperar);
    }





}
