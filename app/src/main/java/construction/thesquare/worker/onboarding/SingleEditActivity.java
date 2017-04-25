package construction.thesquare.worker.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.worker.onboarding.fragment.EditPassportImageFragment;
import construction.thesquare.worker.onboarding.fragment.SelectAvailabilityFragment;
import construction.thesquare.worker.onboarding.fragment.SelectCompaniesFragment;
import construction.thesquare.worker.onboarding.fragment.SelectExperienceFragment;
import construction.thesquare.worker.onboarding.fragment.SelectExperienceTypeFragment;
import construction.thesquare.worker.onboarding.fragment.SelectLocationFragment;
import construction.thesquare.worker.onboarding.fragment.SelectQualificationsFragment;
import construction.thesquare.worker.onboarding.fragment.SelectRequirementsFragment;
import construction.thesquare.worker.onboarding.fragment.SelectRoleFragment;
import construction.thesquare.worker.onboarding.fragment.SelectSkillsFragment;
import construction.thesquare.worker.onboarding.fragment.SelectTradeFragment;
import construction.thesquare.worker.onboarding.fragment.SelectWorkerInfoFragment;

/**
 * Created by maizaga on 27/12/16.
 */

public class SingleEditActivity extends AppCompatActivity {
    public static final String TAG = "SingleEditActivity";

    public static final String BUNDLE_PAGE = "page";
    public static final String BUNDLE_WORKER = "worker";

    private Worker currentWorker;
    private int page;

    @BindView(R.id.toolbar_onboarding)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        setToolbar();
        handleIntent();
        loadPage(page);
    }

    private void handleIntent() {
        currentWorker = (Worker) getIntent().getSerializableExtra(BUNDLE_WORKER);
        page = getIntent().getIntExtra(BUNDLE_PAGE, -1);
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

    private void loadPage(int page) {
        if (page == -1) finish();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (page) {
            case Constants.KEY_ONBOARDING_DETAILS:
                fragmentTransaction.replace(R.id.onboarding_content,
                        SelectWorkerInfoFragment.newInstance(true, currentWorker));
                break;
            case Constants.KEY_ONBOARDING_LOCATION:
                fragmentTransaction.replace(R.id.onboarding_content,
                        SelectLocationFragment.newInstance(true, currentWorker));
                break;
            case Constants.KEY_ONBOARDING_ROLE:
                fragmentTransaction.replace(R.id.onboarding_content,
                        SelectRoleFragment.newInstance(true, currentWorker));
                break;
            case Constants.KEY_ONBOARDING_TRADES:
                fragmentTransaction.replace(R.id.onboarding_content, SelectTradeFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_EXPERIENCE:
                fragmentTransaction.replace(R.id.onboarding_content, SelectExperienceFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_QUALIFICATIONS:
                fragmentTransaction.replace(R.id.onboarding_content, SelectQualificationsFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_SKILLS:
                fragmentTransaction.replace(R.id.onboarding_content, SelectSkillsFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_SPECIFIC_EXPERIENCE:
                fragmentTransaction.replace(R.id.onboarding_content, SelectExperienceTypeFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_COMPANIES:
                fragmentTransaction.replace(R.id.onboarding_content, SelectCompaniesFragment.newInstance(true));
                break;
            case Constants.KEY_ONBOARDING_AVAILABILITY:
                fragmentTransaction.replace(R.id.onboarding_content, SelectAvailabilityFragment.newInstance(true));
                break;
            case Constants.KEY_STEP_REQUIREMENTS:
                fragmentTransaction.replace(R.id.onboarding_content, SelectRequirementsFragment.newInstance(true));
                break;
            case Constants.KEY_STEP_PASSPORT:
                fragmentTransaction.replace(R.id.onboarding_content, new EditPassportImageFragment());
                break;
            default:
                return;
        }
        fragmentTransaction.commit();
    }

    public static Intent startIntent(Activity caller, Worker worker, int page) {
        Intent intent = new Intent(caller, SingleEditActivity.class);
        intent.putExtra(BUNDLE_WORKER, worker);
        intent.putExtra(BUNDLE_PAGE, page);

        return intent;
    }
}
