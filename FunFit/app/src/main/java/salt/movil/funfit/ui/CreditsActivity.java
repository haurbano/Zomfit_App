package salt.movil.funfit.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import salt.movil.funfit.R;

public class CreditsActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        webView = (WebView) findViewById(R.id.web_view_credits);
        webView.getSettings().setJavaScriptEnabled(true);
        //Load the index.html file for credits, is easier
        webView.loadUrl("file:///android_asset/index.html");
    }
}
