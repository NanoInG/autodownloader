package ua.ck.android.autodownloader.autodownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MAIN_URL = "http://skachay.org/_jLEc";
    Button startButton;
    WebView mainWebView;
    EditText timeOfWaitEditText;
    EditText countOfLoopEditText;
    TextView numberOfLoop;
    CheckBox plusTimeCheckbox;
    WifiManager wifiManager;
    final Object locker = new Object();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //init ui elements
        startButton = findViewById(R.id.start_button);
        mainWebView = findViewById(R.id.main_web_wiew);
        timeOfWaitEditText = findViewById(R.id.wait_time_edittext);
        countOfLoopEditText = findViewById(R.id.count_of_loop);
        numberOfLoop = findViewById(R.id.textview_number_of_loop);
        plusTimeCheckbox = findViewById(R.id.plus_time_checkbox);

        //settings for webview
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mainWebView.getSettings().setLoadsImagesAutomatically(false);
        mainWebView.setInitialScale(85);
        mainWebView.setWebViewClient(new MainWebViewClient());
        startButton.setOnClickListener(this);

    }

    //on main button click
    @Override
    public void onClick(View view) {
        changeButtonText("Початок роботи...");
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
                        Log.d("Test threads", "Thread 1 started");
                        changeUiEnabled(false);
                        int countOfLoop = Integer.parseInt(countOfLoopEditText.getText().toString());
                        Long sleepedTime = Long.parseLong(timeOfWaitEditText.getText().toString());

                        for(int i=0;i<countOfLoop;i++) {
                            changeNumberOfLoop(i+1);
                            changeButtonText("Початок " + (i+1) + " повторення...");

                            changeButtonText("Увімкнення Wi-Fi (+ 6 секунд)");
                            wifiManager.setWifiEnabled(true);
                            Thread.sleep(6000);

                            changeButtonText("Вимкнення Wi-Fi (+ 6 секунд)");
                            wifiManager.setWifiEnabled(false);
                            Thread.sleep(6000);

                            changeButtonText("Початок завантаження сторінки...");
                            loadPage(MAIN_URL);

                            synchronized (locker){
                                locker.wait();
                            }

                            changeButtonText("Очікування " + (sleepedTime/1000) + " секунд між повтореннями...");
                            Thread.sleep(sleepedTime);
                            Log.d("Test threads", "Thread 1 stopped");
                        }
                        changeUiEnabled(true);
                        changeButtonText("Start");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
    }


    private void changeUiEnabled(final boolean enabled){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startButton.setEnabled(enabled);
                countOfLoopEditText.setEnabled(enabled);
                timeOfWaitEditText.setEnabled(enabled);
                plusTimeCheckbox.setEnabled(enabled);
            }
        };
        runOnUiThread(runnable);
    }

    private void loadPage(final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mainWebView.loadUrl(url);
            }
        };
        runOnUiThread(runnable);
    }

    private void changeButtonText(final String text){
        Runnable changeButtonTextRunnable = new Runnable() {
            @Override
            public void run() {
                startButton.setText(text);
            }
        };
        runOnUiThread(changeButtonTextRunnable);
    }

    private void changeNumberOfLoop(final int number){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                numberOfLoop.setText("Номер повторення: " + number);
            }
        };
        runOnUiThread(runnable);
    }

    class MainWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(final WebView view, String url) {
            if(url.equals(MAIN_URL)) {
                changeButtonText("Сторінку завантажено...");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Test threads", "Thread 2 started");
                            synchronized (plusTimeCheckbox) {
                                if (plusTimeCheckbox.isChecked()){
                                    changeButtonText("Додаткові 20 секунд...");
                                    Thread.sleep(20000);
                                }
                            }
                            changeButtonText("Очікування 22 секунди до натискання кнопки");
                            Thread.sleep(22000);
                            final String getLink = "document.getElementsByClassName(\'btn btn-danger \')[0].click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{" + getLink + "}");
                                }
                            });

                            changeButtonText("Очікування 3.5 секунд до зняття checkbox");
                            Thread.sleep(3500);
                            final String uncheck = "document.getElementById(\'check\').click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{" + uncheck + "}");
                                }
                            });

                            changeButtonText("Очікування 4 секунди до завантаження");
                            Thread.sleep(4000);
                            final String download = "document.getElementById(\'download\').click()";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{ " + download + "}");
                                }
                            });

                            changeButtonText("Очікування 1 секунда до завантаження");
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl("javascript:{ " + download + "}");
                                }
                            });

                            changeButtonText("Очікування 1 секунда...");
                            Thread.sleep(1000);
                            synchronized (locker) {
                                locker.notify();
                            }
                            Log.d("Test threads", "Thread 2 stopped");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            super.onPageFinished(view, url);
        }
    }
}
