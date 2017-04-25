package construction.thesquare.worker.myaccount.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.worker.myaccount.ui.fragment.SubmitTimesheetFragment;

/**
 * Created by maizaga on 11/11/16.
 *
 */

public class SubmitTimesheetActivity extends AppCompatActivity {

    private static final String TIMESHEET = "timesheet";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Timesheet timesheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        ButterKnife.bind(this);

        setToolbar();
        handleIntent();

        if (savedInstanceState == null) {
            SubmitTimesheetFragment viewProfileFragment = SubmitTimesheetFragment.newInstance(timesheet);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, viewProfileFragment);
            fragmentTransaction.commit();
        }
    }

    private void handleIntent() {
        timesheet = getIntent().getParcelableExtra(TIMESHEET);
    }

    public static Intent startIntent(Context caller, Timesheet timesheet) {
        Intent i = new Intent(caller, SubmitTimesheetActivity.class);
        i.putExtra(TIMESHEET, timesheet);

        return i;
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.submit_timesheet);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
