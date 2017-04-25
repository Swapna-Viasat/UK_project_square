/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.main.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.main.activity.MainActivity;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.ShareUtils;
import construction.thesquare.worker.help.HelpFragment;
import construction.thesquare.worker.jobmatches.fragment.JobMatchesFragment;
import construction.thesquare.worker.myaccount.ui.fragment.AccountFragment;
import construction.thesquare.worker.myjobs.fragment.JobsFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainWorkerActivity extends AppCompatActivity {

    @BindView(R.id.drawer_worker_layout)
    DrawerLayout drawerWorkerLayout;
    @BindView(R.id.nav_worker_view)
    NavigationView navigationView;
    @BindView(R.id.toolbarWorker)
    Toolbar toolbar;

    private SwitchCompat availableNowSwitch;
    private Worker currentWorker;
    private Call<ResponseBody> sendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_worker);
        ButterKnife.bind(this);

        setToolbar();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
            availableNowSwitch = (SwitchCompat) navigationView.getHeaderView(0).findViewById(R.id.worker_menu_switch);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, JobMatchesFragment.newInstance()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMe();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sendToken != null) sendToken.cancel();
    }

    private void setToolbar() {
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
            ab.setHomeAsUpIndicator(getMenuIcon());
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.menu_worker_my_jobs_matches_home);
            ab.setElevation(24);
        }
    }

    private void setToolbarTitle(String title) {
        ActionBar ab = getSupportActionBar();

        if (ab != null) ab.setTitle(title);
    }

    private Drawable getMenuIcon() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_black_24dp, null);
        if (drawable != null)
        DrawableCompat.setTint(drawable, ResourcesCompat.getColor(getResources(), R.color.redSquareColor, null));

        return drawable;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        selectItem(menuItem);
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerWorkerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerWorkerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerWorkerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_worker_my_jobs_matches_home:
                fragment = JobMatchesFragment.newInstance();
                setToolbarTitle(getString(R.string.menu_worker_my_jobs_matches_home));
                break;
            case R.id.nav_worker_my_jobs:
                fragment = JobsFragment.newInstance();
                setToolbarTitle(getString(R.string.menu_worker_my_jobs));
                break;
            case R.id.nav_worker_my_account:
                fragment = AccountFragment.newInstance();
                setToolbarTitle(getString(R.string.my_account_worker_title));
                break;
            case R.id.nav_worker_help:
                fragment = HelpFragment.newInstance();
                setToolbarTitle(getString(R.string.my_account_worker_help_title));
                break;
            case R.id.nav_worker_share:
                ShareUtils.workerLink(this);
                break;
            case R.id.nav_worker_logout:
                fragment = null;
                SharedPreferencesManager.getInstance(this).deleteToken();
                SharedPreferencesManager.getInstance(this).deleteSessionInfoWorker();
                SharedPreferencesManager.getInstance(this).deleteIsInComingSoon();

                getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, MODE_PRIVATE).edit().clear().apply();

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
            drawerWorkerLayout.closeDrawers();
            setTitle(menuItem.getTitle());
        }

    }

    private void populateAvailabilitySwitch() {
        if (currentWorker != null) {
            setAvailability(currentWorker.now);
        }
    }

    public void setAvailability(boolean now) {
        if (availableNowSwitch != null) {
            availableNowSwitch.setOnCheckedChangeListener(null);
            availableNowSwitch.setChecked(now);
            availableNowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    patchWorker();
                }
            });
        }
    }

    private void fetchMe() {
        final Dialog dialog = DialogBuilder.showCustomDialog(this);
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            if (null != response.body()) {
                                if (null != response.body().getResponse()) {
                                    currentWorker = response.body().getResponse();
                                    populateAvailabilitySwitch();
                                    if (null != currentWorker.email) {
                                        sendFirebaseTokenToBackend(currentWorker.email);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                        //HandleErrors.parseFailureError(MainWorkerActivity.this, dialog, t);
                    }
                });
    }

    private void sendFirebaseTokenToBackend(String email) {
//        final Dialog dialog = DialogBuilder.showCustomDialog(this);
        HashMap<String, Object> body = new HashMap<>();
        String fbToken = FirebaseInstanceId.getInstance().getToken();
        body.put("firebase_token", fbToken);
        body.put("email", email);
        sendToken = HttpRestServiceConsumer.getBaseApiClient()
                .sendWorkerToken(body);
        sendToken.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                        if (response.isSuccessful()) {
                            //
//                            DialogBuilder.cancelDialog(dialog);
//                            Toast.makeText(MainWorkerActivity
//                                    .this, "Registered with Firebase!", Toast.LENGTH_LONG).show();
                        } else {
//                            HandleErrors.parseError(MainWorkerActivity.this, dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        HandleErrors.parseFailureError(MainWorkerActivity.this, dialog, t);
                    }
                });
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(this);

        HashMap<String, Object> request = new HashMap<>();
        request.put("available_now", availableNowSwitch.isChecked());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(currentWorker.id, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            currentWorker = response.body().getResponse();
                            populateAvailabilitySwitch();
                        } else {
                            HandleErrors.parseError(MainWorkerActivity.this, dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(MainWorkerActivity.this, dialog, t);
                    }
                });
    }
}
