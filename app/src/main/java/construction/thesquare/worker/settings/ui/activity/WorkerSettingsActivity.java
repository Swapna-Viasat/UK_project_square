package construction.thesquare.worker.settings.ui.activity;

import android.os.Bundle;

import construction.thesquare.R;
import construction.thesquare.shared.settings.SettingsActivity;
import construction.thesquare.worker.settings.ui.fragments.WorkerSettingsFragment;

/**
 * Created by maizaga on 2/1/17.
 *
 */

public class WorkerSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, WorkerSettingsFragment.newInstance())
                .commit();
    }
}
