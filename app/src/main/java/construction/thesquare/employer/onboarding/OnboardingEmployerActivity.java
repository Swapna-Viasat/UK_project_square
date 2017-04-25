package construction.thesquare.employer.onboarding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.onboarding.fragment.SelectEmployerInfoFragment;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/29/2016.
 */

public class OnboardingEmployerActivity extends AppCompatActivity {

    public static final String TAG = "OnboardingActivity";

    private Employer currentEmployer;

    @BindView(R.id.toolbar_onboarding)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this); setToolbar();

        fetchMe();
    }

    private void proceed() {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.onboarding_content, SelectEmployerInfoFragment
                            .newInstance(currentEmployer))
                    .commit();
        } catch (IllegalStateException e) {
            CrashLogHelper.logException(e);
        }
    }

//    private void fetchData() {
//        final Dialog dialog = DialogBuilder.showCustomDialog(this);
//
//        HttpRestServiceConsumer.getBaseApiClient()
//                .fetchData()
//                .enqueue(new Callback<DataResponse>() {
//                    @Override
//                    public void onResponse(Call<DataResponse> call,
//                                           Response<DataResponse> response) {
//
//                        DialogBuilder.cancelDialog(dialog);
//
//                        if (response.isSuccessful()) {
//                            proceed(response.body());
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<DataResponse> call, Throwable t) {
//                        HandleErrors.parseFailureError(getBaseContext(), dialog, t);
//                    }
//                });
//    }

    private void fetchMe() {
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
            @Override
            public void onResponse(Call<ResponseObject<Employer>> call,
                                   Response<ResponseObject<Employer>> response) {
                //

                if (response.isSuccessful()) {
                    TextTools.log(TAG, "success");
                    try {
                        //
                        currentEmployer = response.body().getResponse();

                    } catch (Exception e) {
                        CrashLogHelper.logException(e);
                    }

                    proceed();

                } else {
                    TextTools.log(TAG, "no success");

                    proceed();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
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
}