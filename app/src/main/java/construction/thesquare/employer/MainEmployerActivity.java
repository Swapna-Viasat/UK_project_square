package construction.thesquare.employer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.account.AccountFragment;
import construction.thesquare.employer.createjob.CreateJobActivity;
import construction.thesquare.employer.help.EmployerHelpFragment;
import construction.thesquare.employer.mygraftrs.fragment.MyGraftrsEmployerFragment;
import construction.thesquare.employer.myjobs.fragment.JobsFragment;
import construction.thesquare.employer.payments.fragment.PricePlanFragment;
import construction.thesquare.employer.payments.fragment.SubscriptionFragment;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.main.activity.MainActivity;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ShareUtils;
import construction.thesquare.shared.utils.TextTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by juanmaggi on 10/6/16.
 *
 * Refactored by Evgheni Gherghelejiu throughout January and February 2017
 */
public class MainEmployerActivity extends AppCompatActivity {

    public static final String TAG = "MainEmployer";

    private int lastTab;
    private Call<ResponseObject<Employer>> fetchMe;
    private Call<ResponseBody> sendToken;

    private DrawerLayout drawerEmployerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employer);
        ButterKnife.bind(this); setToolbar();


        // some UI setup from previous dev
        drawerEmployerLayout = (DrawerLayout) findViewById(R.id.drawer_employer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_employer_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        fetchMe();

        // checking if the employer wasn't in the process of creating a job
        // when last left the app
        if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.KEY_UNFINISHED, false)) {
            ///
            TextTools.log(TAG, "resume create job flow");
            startActivity(new Intent(this, CreateJobActivity.class));
            ///
        } else if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.DRAFT_JOB_AWAIT_PLAN, false)) {
            ///
            TextTools.log(TAG, "we have a draft job not published because no plan was setup");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_employer_content,
                            SubscriptionFragment.newInstance(false, false))
                    //.addToBackStack("")
                    .commit();
            ///
        } else {
            int currentTab =
                    getSharedPreferences(Constants.EMPLOYER, MODE_PRIVATE)
                            .getInt(Constants.EMPLOYER_CURRENT_TAB, 0);

            switch (currentTab) {
                case 0:
                    selectItem(getString(R.string.menu_employer_my_jobs_home),
                            navigationView.getMenu().getItem(0));
                    break;
                case 1:
                    selectItem(getString(R.string.menu_employer_my_graftrs),
                            navigationView.getMenu().getItem(1));
                    break;
                case 2:
                    selectItem(getString(R.string.menu_employer_my_account),
                            navigationView.getMenu().getItem(2));
                    break;
                case 3:
                    selectItem(getString(R.string.employer_account_price_plan),
                            navigationView.getMenu().getItem(3));
                    break;
            }
        }
    }

    private void fetchMe() {
        //
        fetchMe = HttpRestServiceConsumer.getBaseApiClient().meEmployer();
        fetchMe.enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        //
                        if (response.isSuccessful()) {
                            if (null != response.body()) {
                                if (null != response.body().getResponse()) {
                                    if (null != response.body().getResponse().email) {
                                        sendFirebaseTokenToBackend(response.body().getResponse().email);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {

                    }
                });
    }

    private void sendFirebaseTokenToBackend(String email) {
        //
        HashMap<String, Object> body = new HashMap<>();
        String fbToken = FirebaseInstanceId.getInstance().getToken();
        body.put("firebase_token", fbToken);
        body.put("email", email);
        sendToken = HttpRestServiceConsumer.getBaseApiClient().sendEmployerToken(body);
        sendToken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                //
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "main employer activity resumed");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != fetchMe) {
            fetchMe.cancel();
        }
        if (null != sendToken) {
            sendToken.cancel();
        }
        getSharedPreferences(Constants.EMPLOYER, MODE_PRIVATE)
                .edit()
                .putInt(Constants.EMPLOYER_CURRENT_TAB, lastTab)
                .commit();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEmployer);
        setSupportActionBar(toolbar);
        // find the title text view
        TextView toolbarTitle;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                // set my custom font
                Typeface typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/JosefinSans-Italic.ttf");
                toolbarTitle.setTypeface(typeface);
                break;
            }
        }

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            final Drawable menu = ContextCompat
                    .getDrawable(this, R.drawable.ic_menu_black_24dp);
            menu.setColorFilter(ContextCompat
                    .getColor(this, R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
            final Drawable backArrow = ContextCompat
                    .getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            backArrow.setColorFilter(ContextCompat
                    .getColor(this, R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
            ab.setHomeAsUpIndicator(menu);
            ab.setDisplayHomeAsUpEnabled(true);

            getSupportFragmentManager()
                    .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                ab.setHomeAsUpIndicator(menu);
                            } else {
                                ab.setHomeAsUpIndicator(backArrow);
                            }
                        }
                    });
        }
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        String title = menuItem.getTitle().toString();
                        selectItem(title, menuItem);
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    drawerEmployerLayout.openDrawer(GravityCompat.START);
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(String title, MenuItem menuItem) {
        Fragment fragment = null;
        menuItem.setChecked(true);
        if (title.equals(getResources().getString(R.string.menu_employer_my_jobs_home))) {
            lastTab = 0;
            fragment = new JobsFragment();
        }
        if (title.equals(getResources().getString(R.string.menu_employer_my_graftrs))) {
            lastTab = 1;
            fragment = new MyGraftrsEmployerFragment();
        }
        if (title.equals(getResources().getString(R.string.menu_employer_my_account))) {
            lastTab = 2;
            fragment = new AccountFragment();
        }
        if (title.equals(getString(R.string.employer_account_price_plan))) {
            //
//            Intent intent = new Intent(this, PaymentsActivity.class);
//            startActivity(intent);
//            return;
            lastTab = 3;
            fragment = PricePlanFragment.newInstance();
        }
        if (title.equals(getString(R.string.menu_worker_share))) {
            ShareUtils.employerLink(this);
            return;
        }
        if (title.equals(getString(R.string.menu_employer_help))) {
            lastTab = 4;
            fragment = EmployerHelpFragment.newInstance();
        }
        if (title.equals(getResources().getString(R.string.menu_employer_log_out))) {
            lastTab = 0;
            fragment = null;
            SharedPreferencesManager.getInstance(this).deleteToken();
            SharedPreferencesManager.getInstance(this).deleteSessionInfoEmployer();
            SharedPreferencesManager.getInstance(this).deleteIsInComingSoon();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

//        if (title.equals(getResources().getString(R.string.menu_employer_edit_profile))) {
//            fragment = null;
//            startActivity(new Intent(this, OnboardingEmployerActivity.class));
//        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_employer_content, fragment)
                    .commit();

            drawerEmployerLayout.closeDrawers();
            setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            int i = 0;
            while (i < getSupportFragmentManager().getBackStackEntryCount()) {
               getSupportFragmentManager().popBackStack();
                i++;
            }
            setTitle(getResources().getString(R.string.menu_employer_my_jobs_home));
        } else {
            super.onBackPressed();
        }
    }
}