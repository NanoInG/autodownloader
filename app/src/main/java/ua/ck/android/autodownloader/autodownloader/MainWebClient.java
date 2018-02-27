package ua.ck.android.autodownloader.autodownloader;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by bogda on 27.02.2018.
 */

public class MainWebClient extends WebViewClient {

    @Override
    public void onPageFinished(final WebView view, final String url) {
        synchronized (view) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(22000);
                        String getLink = "document.getElementsByClassName(\'btn btn-danger \')[0].click()";
                        view.loadUrl("javascript:{" + getLink + "}");
                        Thread.sleep(3500);
                        String uncheck = "document.getElementById(\'check\').click()";
                        view.loadUrl("javascript:{" + uncheck + "}");
                        Thread.sleep(4000);
                        String download = "document.getElementById(\'download\').click()";
                        view.loadUrl("javascript:{ " + download + "}");
                        Thread.sleep(1000);
                        view.loadUrl("javascript:{ " + download + "}");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.run();

        }
        super.onPageFinished(view, url);
    }
}
