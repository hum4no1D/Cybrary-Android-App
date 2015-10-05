package com.cybrary.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends Activity {

    SharedPreferences credentials;
    EditText user,pass;
    TextView tv;
    ProgressDialog dialog = null;
    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        cookieManager = new CookieManager(((CybraryApplication) getApplication()).getCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        credentials = getSharedPreferences("credentials", Context.MODE_PRIVATE);

        //  Auto-login has been disabled since Cybrary removes cookies after browser is closed
        //  This behavior can easily be restored once using the API (we'll get a long-lived token)
        if(credentials.contains("login")) {
            Toast.makeText(this, "Automatically logged in as " + credentials.getString("login", ""), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, CoursesListActivity.class));
            finish();
        }

        user = (EditText)findViewById(R.id.Login);
        Typeface batman = Typeface.createFromAsset(getAssets(), "batman.ttf");
        user.setTypeface(batman);

    }

    public void Log_in(View e){
        user = (EditText)findViewById(R.id.Login);
        pass = (EditText)findViewById(R.id.Senha);
        tv = (TextView)findViewById(R.id.Situacao);
        dialog = ProgressDialog.show(LoginActivity.this, "", "Validating user...", true);

        String reqUrl = "https://www.cybrary.it/wp-login.php";
        final String log = user.getText().toString().trim();
        final String pwd = pass.getText().toString().trim();

        if(log.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(LoginActivity.this, "You need to fill in both fields!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }

        //Creating a new Volley HTTP POST request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest messagesRequest = new StringRequest(Request.Method.POST, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Server replied successfully (200)
                //Now check if the login was successful too
                //So ensure user had a valid username AND a valid password
                if (!response.contains("Invalid username") && !response.contains("The password you entered for the username") && response.contains("@" + log + "<br")) {
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    tv.setText("Login Successfully");
                    getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().putString("login", log).commit();

                    //Now, start CoursesListActivity
                    startActivity(new Intent(LoginActivity.this, CoursesListActivity.class));
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login failure :(", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    Log.i("LoginActivity", "Login failure, server replied: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Some server error, or no network connectivity
                error.printStackTrace();

                //Depending on wordpress configuration you may need to move this code higher, near the "login failure" instead
                //i don't know if wordpress replies with custom http status code
                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
