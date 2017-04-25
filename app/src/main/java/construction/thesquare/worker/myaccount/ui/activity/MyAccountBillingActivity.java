package construction.thesquare.worker.myaccount.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Invoice;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.worker.myaccount.ui.fragment.MyAccountFinancialInfoFragment;
import construction.thesquare.worker.myaccount.ui.fragment.MyAccountInvoicesFragment;
import construction.thesquare.worker.myaccount.ui.fragment.MyAccountTimesheetsFragment;


public class MyAccountBillingActivity extends AppCompatActivity {

    private static final String TIMESHEETS = "timesheets";
    private static final String INVOICES = "invoices";

    public static final int REQUEST_TIMESHEET_UNITS = 100;
    public static final String TIMESHEET_BUNDLE = "timesheetBundle";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<Timesheet> timesheets;
    private ArrayList<Invoice> invoices;

    private MyAccountTimesheetsFragment timesheetsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_billing);

        ButterKnife.bind(this);

        setToolbar();
        handleIntent();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) tabLayout.setupWithViewPager(mViewPager);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.title_activity_my_account_billing);
        }
    }

    private void handleIntent() {
        timesheets = getIntent().getParcelableArrayListExtra(TIMESHEETS);
        invoices = getIntent().getParcelableArrayListExtra(INVOICES);
    }

    public static Intent startIntent(Context caller, ArrayList<Timesheet> timesheets, ArrayList<Invoice> invoices) {
        Intent i = new Intent(caller, MyAccountBillingActivity.class);
        i.putParcelableArrayListExtra(TIMESHEETS, timesheets);
        i.putParcelableArrayListExtra(INVOICES, invoices);

        return i;
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    timesheetsFragment = MyAccountTimesheetsFragment.newInstance(timesheets);
                    return timesheetsFragment;
                case 1:
                    return MyAccountInvoicesFragment.newInstance(invoices);
                case 2:
                    return MyAccountFinancialInfoFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.timesheets_title);
                case 1:
                    return getString(R.string.invoices_title);
                case 2:
                    return getString(R.string.financial_info_title);
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_TIMESHEET_UNITS) {
            Timesheet timesheet = data.getParcelableExtra(TIMESHEET_BUNDLE);
            if (timesheet != null) {
                for (Timesheet tempTimesheet : timesheets) {
                    if (tempTimesheet.getId() == timesheet.getId()) {
                        timesheets.set(timesheets.indexOf(tempTimesheet), timesheet);
                    }
                }
                if (timesheetsFragment != null)
                    timesheetsFragment.setTimesheets(timesheets);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
