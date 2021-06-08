package com.kokava.apps.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.snackbar.Snackbar;
import com.kokava.apps.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private String url = "https://02e82f3d422d.ngrok.io/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getPdf();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void getPdf() {
        try {
            binding.swipeRefresh.setRefreshing(false);
            WebView webView = binding.webView;
            webView.setWebViewClient(new MyWebViewClient());
            webView.setWebViewClient(new MyWebViewClient());
            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(url);

        } catch (Exception e) {
            retryRequest();
        }
    }

    /**
     * retry loading pdf
     */
    private void retryRequest() {
        try {
            View contentView = this.findViewById(android.R.id.content);

            Snackbar
                    .make(contentView, "Something went wrong.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", view -> {
                        getPdf();
                    })
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "retryRequest: ", e);
        }
    }

    public class MyWebViewClient extends WebViewClient {

        ProgressDialog pd;

        private MyWebViewClient() {
            // empty constructor
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //super.onPageStarted(view, url, favicon);
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e(TAG, "onPageStarted: url: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to continue?");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            } catch (Exception e) {
                Log.e(TAG, "onReceivedSslError: ", e);
            }
        }

        private static final String TAG = "MyWebViewClient";

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                error) {
            super.onReceivedError(view, request, error);
            retryRequest();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}