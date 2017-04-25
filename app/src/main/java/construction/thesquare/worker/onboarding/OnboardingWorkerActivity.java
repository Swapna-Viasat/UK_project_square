package construction.thesquare.worker.onboarding;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.worker.onboarding.fragment.SelectAvailabilityFragment;
import construction.thesquare.worker.onboarding.fragment.SelectCompaniesFragment;
import construction.thesquare.worker.onboarding.fragment.SelectExperienceFragment;
import construction.thesquare.worker.onboarding.fragment.SelectExperienceTypeFragment;
import construction.thesquare.worker.onboarding.fragment.SelectLocationFragment;
import construction.thesquare.worker.onboarding.fragment.SelectQualificationsFragment;
import construction.thesquare.worker.onboarding.fragment.SelectRoleFragment;
import construction.thesquare.worker.onboarding.fragment.SelectSkillsFragment;
import construction.thesquare.worker.onboarding.fragment.SelectTradeFragment;
import construction.thesquare.worker.onboarding.fragment.SelectWorkerInfoFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/18/2016.
 */

public class OnboardingWorkerActivity extends AppCompatActivity {

    public static final String TAG = "OnboardingActivity";

    private Worker currentWorker;
    private boolean alreadyHaveAccount;

    @BindView(R.id.toolbar_onboarding)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        setToolbar();

        if (getIntent() != null) {
            alreadyHaveAccount = getIntent().getBooleanExtra(Constants.KEY_HAVE_ACCOUNT, false);
        }

        fetchMe();
    }

    private void proceed() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.onboarding_content,
                        getFragmentByNumber(getLastFragmentNumber()))
                .commit();
    }

    private void fetchMe() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker().enqueue(new Callback<ResponseObject<Worker>>() {
            @Override
            public void onResponse(Call<ResponseObject<Worker>> call,
                                   Response<ResponseObject<Worker>> response) {

                if (response.isSuccessful()) {
                    TextTools.log(TAG, "success");
                    try {
                        currentWorker = response.body().getResponse();
                        proceed();
                    } catch (Exception e) {
                        CrashLogHelper.logException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                TextTools.log(TAG, "failure ");
                TextTools.log(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        saveProgress();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    TextTools.log(TAG, "backstack empty");
                    finish();
                } else {
                    TextTools.log(TAG, "popping backstack");
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(getMenuIcon());
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setElevation(0);
            ab.setTitle("");
        }
    }

    private Drawable getMenuIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.redSquareColor));
        return drawable;
    }

    private void saveProgress() {
        TextTools.log(TAG, "Saving progress");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.onboarding_content);
        int fragmentNumber = getFragmentNumber(fragment);

        getOnboardingFlowPreferences()
                .edit()
                .putBoolean(Constants.KEY_WORKER_ONBOARDING_UNFINISHED, fragmentNumber < 10)
                .putInt(Constants.KEY_WORKER_ONBOARDING_STEP, fragmentNumber < 10 ? fragmentNumber : 0)
                .apply();
    }

    private int getFragmentNumber(Fragment fragment) {
        int fragmentNumber = 0;

        if (fragment instanceof SelectWorkerInfoFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_DETAILS;
        } else if (fragment instanceof SelectLocationFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_LOCATION;
        } else if (fragment instanceof SelectRoleFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_ROLE;
        } else if (fragment instanceof SelectTradeFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_TRADES;
        } else if (fragment instanceof SelectExperienceFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_EXPERIENCE;
        } else if (fragment instanceof SelectQualificationsFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_QUALIFICATIONS;
        } else if (fragment instanceof SelectSkillsFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_SKILLS;
        } else if (fragment instanceof SelectExperienceTypeFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_SPECIFIC_EXPERIENCE;
        } else if (fragment instanceof SelectCompaniesFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_COMPANIES;
        } else if (fragment instanceof SelectAvailabilityFragment) {
            fragmentNumber = Constants.KEY_ONBOARDING_AVAILABILITY;
        }

        return fragmentNumber;
    }

    private Fragment getFragmentByNumber(int number) {
        Fragment fragment;
        switch (number) {
            case Constants.KEY_ONBOARDING_DETAILS:
                fragment = SelectWorkerInfoFragment.newInstance(false, currentWorker);
                break;
            case Constants.KEY_ONBOARDING_LOCATION:
                fragment = SelectLocationFragment.newInstance(false, currentWorker);
                break;
            case Constants.KEY_ONBOARDING_ROLE:
                fragment = SelectRoleFragment.newInstance(false, currentWorker);
                break;
            case Constants.KEY_ONBOARDING_TRADES:
                fragment = SelectTradeFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_EXPERIENCE:
                fragment = SelectExperienceFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_QUALIFICATIONS:
                fragment = SelectQualificationsFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_SKILLS:
                fragment = SelectSkillsFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_SPECIFIC_EXPERIENCE:
                fragment = SelectExperienceTypeFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_COMPANIES:
                fragment = SelectCompaniesFragment.newInstance(false);
                break;
            case Constants.KEY_ONBOARDING_AVAILABILITY:
                fragment = SelectAvailabilityFragment.newInstance(false);
                break;

            default:
                fragment = SelectWorkerInfoFragment.newInstance(false, currentWorker);
                break;
        }
        return fragment;
    }

    private int getLastFragmentNumber() {
        return getOnboardingFlowPreferences().getInt(Constants.KEY_WORKER_ONBOARDING_STEP, alreadyHaveAccount ? 2 : 0);
    }

    private SharedPreferences getOnboardingFlowPreferences() {
        return getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, MODE_PRIVATE);
    }
}