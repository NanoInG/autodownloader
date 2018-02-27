package ua.ck.android.autodownloader.autodownloader;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button startButton;
    WebView mainWebView;
    EditText timeOfWaitEditText;
    EditText countOfLoopEditText;

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
        mainWebView.setWebViewClient(new MainWebClient());
        startButton.setOnClickListener(this);

    }


    //on main button click
    @Override
    public void onClick(View view) {
        synchronized (mainWebView) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int countOfLoop = Integer.parseInt(countOfLoopEditText.getText().toString());
                        Long sleepedTime = Long.parseLong(timeOfWaitEditText.getText().toString());
                        for(int i=0;i<countOfLoop;i++) {
                            PhoneManager.setMobileDataState(MainActivity.this, false);
                            Thread.sleep(5000);
                            PhoneManager.setMobileDataState(MainActivity.this, true);
                            Thread.sleep(6000);
                            mainWebView.loadUrl("http://skachay.org/_jLEc");
                            Thread.sleep(sleepedTime + 22000 + 3500 + 4000 + 1000 + 5000 + 5000);
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.run();
        }
    }
}
