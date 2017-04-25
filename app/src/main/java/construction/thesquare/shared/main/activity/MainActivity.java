package construction.thesquare.shared.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.onboarding.OnboardingEmployerActivity;
import construction.thesquare.employer.signup.model.Employer;
import construction.thesquare.shared.analytics.AnalyticStorage;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.start.activity.StartActivity;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.main.ui.MainWorkerActivity;
import construction.thesquare.worker.onboarding.OnboardingWorkerActivity;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Refactored by Evgheni Gherghelejiu
 * on 01/06/2016
 */
public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getAutoInstance(this);
        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject,
                                       LinkProperties linkProperties,
                                       BranchError error) {
                //
                Log.d(TAG, "on init finished");
                //
                if (error == null) {
                    //
                    try {
                        AnalyticStorage.persistCampaignName(MainActivity.this,
                                String.valueOf((null != linkProperties)
                                        ? linkProperties.getCampaign() : ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //
                    Log.d(TAG, "no errors");
                } else {
                    //
                    Log.d(TAG, "errors");
                }
            }
        }, this.getIntent().getData(), this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(this, ConstantsAnalytics.SCREEN_LAUNCH);

        if (TextUtils.isEmpty(SharedPreferencesManager.getInstance(this).getToken())) {
            startActivity(new Intent(this, StartActivity.class));
        } else {
            onTokenExists();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void onTokenExists() {
        // wtf is this "getIsLogin()" ??
        if (SharedPreferencesManager.getInstance(this).getIsLogin()) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
        } else if (SharedPreferencesManager.getInstance(this)
                .loadSessionInfoWorker().getUserId() > 0) {
            // 2 cases follow
            if (TextUtils.isEmpty(SharedPreferencesManager.getInstance(this).getToken())) {
                startActivity(new Intent(this, OnboardingWorkerActivity.class));
            } else {
                getWorker(SharedPreferencesManager
                        .getInstance(this).loadSessionInfoWorker().getUserId());
            }
            //
        } else if (SharedPreferencesManager.getInstance(this)
                .loadSessionInfoEmployer().getUserId() > 0) {
            // 2 cases follow
            if (TextUtils.isEmpty(SharedPreferencesManager.getInstance(this).getToken())) {
                startActivity(new Intent(this, OnboardingEmployerActivity.class));
            } else {
                getEmployer(SharedPreferencesManager
                        .getInstance(this).loadSessionInfoEmployer().getUserId());
            }
        }
    }

    private void getWorker(int id) {
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerProfile(id)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        
                        if (response.isSuccessful()) {
                            if (!response.body().getResponse().onboardingSkipped &&
                                    !isWorkerOnboardingSkipped()) {
                                //
                                Intent intent = new Intent(MainActivity.this, MainWorkerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //
                            } else {
                                Intent intent = new Intent(MainActivity.this, OnboardingWorkerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else if (response.code() == 401) {
                            //
                            // so this shouldn't normally happen but because of the issue raised by
                            // Sian I'm doing this check anyway.
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteToken();
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteSessionInfoEmployer();
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteIsInComingSoon();
                            getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, MODE_PRIVATE).edit().clear().apply();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            //
                        } else {
                            HandleErrors.parseError(MainActivity.this, null, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(MainActivity.this, null, t);
                    }
                });
    }

    private void getEmployer(int id) {
        HttpRestServiceConsumer.getBaseApiClient()
                .getEmployerProfile(id)
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        //
                        if (response.isSuccessful()) {
                            if (response.body().getResponse().isOnboarding_skipped() ||
                                    response.body().getResponse().isOnboarding_done()) {
                                //
                                Intent intent = new Intent(MainActivity.this, MainEmployerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //
                            } else {
                                startActivity(new Intent(MainActivity.this, OnboardingEmployerActivity.class));
                            }
                        } else if (response.code() == 401) {
                            //
                            // so this shouldn't normally happen but because of the issue raised by
                            // Sian I'm doing this check anyway.
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteToken();
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteSessionInfoEmployer();
                            SharedPreferencesManager.getInstance(MainActivity.this).deleteIsInComingSoon();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            //
                        } else {
                            HandleErrors.parseError(MainActivity.this, null, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        HandleErrors.parseFailureError(MainActivity.this, null, t);
                    }
                });
    }

    private boolean isWorkerOnboardingSkipped() {
        return getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.KEY_WORKER_ONBOARDING_UNFINISHED, false);
    }
}