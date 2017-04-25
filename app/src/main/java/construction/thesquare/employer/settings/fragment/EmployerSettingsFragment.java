package construction.thesquare.employer.settings.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.settings.fragments.SettingsFragment;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class EmployerSettingsFragment extends SettingsFragment {

    public static EmployerSettingsFragment newInstance() {
        Bundle args = new Bundle();
        EmployerSettingsFragment fragment = new EmployerSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.my_security, R.id.notify})
    public void onAction(View view) {
        switch (view.getId()) {
            case R.id.my_security:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, EmployerSecurityFragment.newInstance())
                        .addToBackStack("security")
                        .commit();
                break;
            case R.id.notify:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, EmployerSettingsNotifyFragment.newInstance())
                        .addToBackStack("notifications")
                        .commit();
                break;
            default:
                //
                break;

        }
    }
}
