package construction.thesquare.shared.settings.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;

/**
 * Created by Evgheni on 11/17/2016.
 */

public class SettingsAboutFragment extends Fragment {

    public static SettingsAboutFragment newInstance() {
        return new SettingsAboutFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_about, container, false);
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
        getActivity().setTitle(getString(R.string.employer_settings_about));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.about, R.id.contact})
    public void toggles(View view) {
        switch (view.getId()) {
            case R.id.about:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsAboutInfoFragment.newInstance())
                        .addToBackStack("")
                        .commit();
                break;
           /* case R.id.faq:
                Toast.makeText(getContext(), "faq", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.contact:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsContactFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
        }
    }
}
