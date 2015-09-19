package com.example.cybrary02.cybrary;
//
import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
public class MainActivityBackup extends Activity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    EditText user,pass;
//    TextView tv;
//    ProgressDialog dialog = null;
//
//
//    public void Log_in(View e){
//        user = (EditText)findViewById(R.id.Login);
//        pass = (EditText)findViewById(R.id.Senha);
//        tv = (TextView)findViewById(R.id.Situacao);
//        dialog = ProgressDialog.show(MainActivity.this, "", "Validating user...", true);
//
////        try {
////            WebClient client = new WebClient();
//        final String reqBody = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
//                "<methodCall>" +
//                //"  <methodName>demo.sayHello</methodName>" +
//                "  <methodName>wp.getPosts</methodName>" +
//                "  <params>" +
//                "   <param></param>" +
//                "  </params>" +
//                "</methodCall>";
////            WebRequest req = new WebRequest(new URL("https://www.cybrary.it/xmlrpc.php"), HttpMethod.POST);
////            req.setRequestBody(reqBody);
////            String response = client.getPage(req).getWebResponse().getContentAsString();
////            System.out.println(response);
////        }catch(Exception ex){
////            ex.printStackTrace();
////        }
//
//
//        String reqUrl = "https://www.cybrary.it/xmlrpc.php?username="+user.getText().toString().trim()+"&password="+pass.getText().toString().trim();
//
//        //ACreating a new Volley HTTP POST request
//        RequestQueue queue = Volley.newRequestQueue(this);
//        StringRequest messagesRequest = new StringRequest(Request.Method.POST, reqUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //AServer replied successfully (200)
//                System.out.println(response);
//                if(!response.contains("login_error")){
//                    Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    tv.setText("Login Successfuly");
//                    dialog.dismiss();
//
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Login failure :(", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    Log.i("MainActivity", "Login failure, server replied: " + response);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Some server error, or no network connectivity
//                error.printStackTrace();
//
//                //ADepending on your wordpress configuration you may need to move this code higher, near the "login failure" instead
//                //Ai don't know if wordpress replies with custom http status code
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("Login Error.");
//                        builder.setMessage("User not Found.")
//                                .setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();
//                        dialog.dismiss();
//
//                    }
//                });
//            }
//        }) {
//            //            string username
////            string password 
//            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", user.getText().toString().trim());
//                params.put("password", pass.getText().toString().trim());
//                return params;
//            };
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return reqBody.getBytes();
//            }
//        };
//
//
//        // Send the request
//        queue.add(messagesRequest);
//    }
//
//
//    public void RegUri(View c){
//        Intent RegisterBtn = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/register/"));
//
//        startActivity(RegisterBtn);
//    }
//
//
//    public void EsqASenha (View d){
//        Intent Recuperar = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybrary.it/wp-login.php?action=lostpassword"));
//        startActivity(Recuperar);
//    }
//
//
//
//
//


}