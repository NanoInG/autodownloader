package ua.ck.android.autodownloader.autodownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button startButton;
    WebView mainWebView;
    EditText timeOfWaitEditText;
    EditText countOfLoopEditText;
    final Object locker = new Object();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init ui elements
        startButton = findViewById(R.id.start_button);
        mainWebView = findViewById(R.id.main_web_wiew);
        timeOfWaitEditText = findViewById(R.id.wait_time_edittext);
        countOfLoopEditText = findViewById(R.id.count_of_loop);

        //settings for webview
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mainWebView.getSettings().setLoadsImagesAutomatically(false);
        mainWebView.setInitialScale(300);
        mainWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(final WebView view, String url) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(22000);
                            final String getLink = "document.getElementsByClassName(\'btn btn-danger \')[0].click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{" + getLink + "}");
                                }
                            });

                            Thread.sleep(3500);
                            final String uncheck = "document.getElementById(\'check\').click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{" + uncheck + "}");
                                }
                            });

                            Thread.sleep(4000);
                            final String download = "document.getElementById(\'download\').click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{ " + download + "}");
                                }
                            });
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{ " + download + "}");
                                }
                            });
                            Thread.sleep(1000);
                            synchronized (locker){
                                locker.notify();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
                super.onPageFinished(view, url);
            }
        });
        startButton.setOnClickListener(this);

    }


    //on main button click
    @Override
    public void onClick(View view) {
        if(timeOfWaitEditText.getText().toString().equals("")){
            timeOfWaitEditText.setText("1000");
        }
        if(countOfLoopEditText.getText().toString().equals("")){
            countOfLoopEditText.setText("2");
        }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runOnUiThread(disableUiRunnable);
                        int countOfLoop = Integer.parseInt(countOfLoopEditText.getText().toString());
                        Long sleepedTime = Long.parseLong(timeOfWaitEditText.getText().toString());
                        for(int i=0;i<countOfLoop;i++) {
                            PhoneManager.setMobileDataState(MainActivity.this, false);
                            Thread.sleep(5000);
                            PhoneManager.setMobileDataState(MainActivity.this, true);
                            Thread.sleep(6000);
                            runOnUiThread(checkIP);
                            Thread.sleep(10000);
                            runOnUiThread(loadPageRunnable);
                            synchronized (locker){
                                locker.wait();
                            }
                            Thread.sleep(sleepedTime);
                        }
                        runOnUiThread(enableUiRunnable);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
    }


    private Runnable enableUiRunnable = new Runnable() {
        @Override
        public void run() {
            startButton.setEnabled(true);
            countOfLoopEditText.setEnabled(true);
            timeOfWaitEditText.setEnabled(true);
        }
    };
    private Runnable disableUiRunnable = new Runnable() {
        @Override
        public void run() {
            startButton.setEnabled(false);
            countOfLoopEditText.setEnabled(false);
            timeOfWaitEditText.setEnabled(false);
        }
    };
    private Runnable loadPageRunnable = new Runnable() {
        @Override
        public void run() {
            mainWebView.loadUrl("http://skachay.org/_jLEc");
        }
    };
    private Runnable checkIP = new Runnable() {
        @Override
        public void run() {
            mainWebView.loadUrl("https://api.ipify.org/?format=txt");
        }
    };

}
