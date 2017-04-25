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

public class SettingsSocialFragment extends Fragment {

    public static SettingsSocialFragment newInstance() {
        return new SettingsSocialFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_social, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.employer_settings_social));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    @OnClick({R.id.sync_fb, R.id.sync_twtr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_fb:
                Toast.makeText(getContext(), "Sync fb", Toast.LENGTH_LONG).show();
                break;
            case R.id.sync_twtr:
                Toast.makeText(getContext(), "Sync twtr", Toast.LENGTH_LONG).show();
                break;
        }
    }
}