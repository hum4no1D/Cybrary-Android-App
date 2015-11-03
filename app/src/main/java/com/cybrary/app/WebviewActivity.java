package com.cybrary.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.HttpCookie;
import java.util.List;

public class WebviewActivity extends LoggedInAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String url = getIntent().getStringExtra("url");

        List<HttpCookie> cookies = ((CybraryApplication) getApplication()).getCookieStore(this).getCookies();

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        for(HttpCookie cookie : cookies) {
            String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
            cookieManager.setCookie(cookie.getDomain(), cookieString);
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            cookieManager.flush();
        } else{
            CookieSyncManager.getInstance().sync();
        }

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.contains("wp-login.php")) {
                    //  User is trying to log in on the webview!
                    //  Redirect to our login page instead
                    getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().remove("login").apply();
                    Log.i("WebiewActivity", "Intercepting login back to app: " + url);
                    startActivity(new Intent(WebviewActivity.this, LoginActivity.class));
                    finish();
                }

                Log.i("WebiewActivity", "Navigating to " + url);
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String sTitle) {
                super.onReceivedTitle(view, sTitle);
                if (sTitle != null && sTitle.length() > 0) {
                    setTitle(sTitle);
                } else {
                    setTitle("Cybrary.it");
                }
            }
        });

        webView.loadUrl(url);
    }
}
