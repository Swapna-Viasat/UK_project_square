package construction.thesquare.shared.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;

/**
 * Created by Evgheni on 11/17/2016.
 */

public class SettingsDocsFragment extends Fragment {

    public static SettingsDocsFragment newInstance() {
        SettingsDocsFragment settingsDocsFragment = new SettingsDocsFragment();
        return settingsDocsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_settings_docs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.employer_settings_terms));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.tc, R.id.pp})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tc:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsTermsConditionsFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
            case R.id.pp:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsPrivacyFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
        }
    }
}
