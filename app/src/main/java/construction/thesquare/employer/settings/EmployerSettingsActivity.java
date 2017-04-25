package construction.thesquare.employer.settings;

import android.os.Bundle;

import construction.thesquare.R;
import construction.thesquare.employer.settings.fragment.EmployerSettingsFragment;
import construction.thesquare.shared.settings.SettingsActivity;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class EmployerSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, EmployerSettingsFragment.newInstance())
                .commit();
    }
}
