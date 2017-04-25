package construction.thesquare.shared.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;

/**
 * Created by swapna on 3/8/2017.
 */

public class SettingsPrivacyFragment extends Fragment {
    public static final String TAG = "SettingsPrivacyFragment";

    @BindView(R.id.webview)
    WebView webview;

    public static SettingsPrivacyFragment newInstance() {
        return new SettingsPrivacyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_terms_conditions, container, false);
        ButterKnife.bind(this, view);
        WebSettings settings= webview.getSettings();
        settings.setDefaultFontSize(12);
        webview.loadUrl("file:///android_asset/privacy.html");
        return view;
    }
}
