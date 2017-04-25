package construction.thesquare.shared.start.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;

/**
 * Created by gherg on 11/28/2016.
 */

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.webview) WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_terms_conditions);
        ButterKnife.bind(this);
        WebSettings settings= webview.getSettings();
        settings.setDefaultFontSize(12);
        webview.loadUrl("file:///android_asset/terms.html");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
