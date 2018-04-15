package fun.rokers.hackafake.hackafake_dashboard_viewer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity implements KeyEvent.Callback {

    private static String HOME_URL = "http://hackafake.it";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //going fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        webView = findViewById(R.id.webView);
        //enabling javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //WebViewClient for navigation
        webView.setWebViewClient(new WebViewClient());
        //going to the homepage
        webView.loadUrl(HOME_URL);

        Counter counter = new Counter(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("INFO", "KeyCode deteched: " + keyCode);
        if(keyCode == 4) {
            webView.loadUrl(HOME_URL);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}