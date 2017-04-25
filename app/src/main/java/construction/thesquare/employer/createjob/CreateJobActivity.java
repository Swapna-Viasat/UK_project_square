package construction.thesquare.employer.createjob;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.fragment.SelectDetailsFragment;
import construction.thesquare.employer.createjob.fragment.SelectExperienceFragment;
import construction.thesquare.employer.createjob.fragment.SelectExperienceTypeFragment;
import construction.thesquare.employer.createjob.fragment.SelectLocationFragment;
import construction.thesquare.employer.createjob.fragment.SelectQualificationsFragment;
import construction.thesquare.employer.createjob.fragment.SelectRoleFragment;
import construction.thesquare.employer.createjob.fragment.SelectSkillsFragment;
import construction.thesquare.employer.createjob.fragment.SelectTradeFragment;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.TextTools;

/**
 * Created by gherg on 12/6/2016.
 */

public class CreateJobActivity extends AppCompatActivity {

    public static final String TAG = "CreateJobActivity";

    @BindView(R.id.toolbar_create_job) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);
        ButterKnife.bind(this);

        Fragment fragment = SelectRoleFragment.newInstance(null, false);

        if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.KEY_UNFINISHED, false)) {
            setToolbar(false);
            CreateRequest request = GsonConfig.buildDefault()
                    .fromJson(getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                            .getString(Constants.KEY_REQUEST, ""), CreateRequest.class);
            switch (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE).getInt(Constants.KEY_STEP, 0)) {
                case Constants.KEY_STEP_ROLE:
                    fragment = SelectRoleFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_TRADE:
                    fragment = SelectTradeFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_EXPERIENCE:
                    fragment = SelectExperienceFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_QUALIFICATIONS:
                    fragment = SelectQualificationsFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_SKILLS:
                    fragment = SelectSkillsFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_EXPERIENCE_TYPE:
                    fragment = SelectExperienceTypeFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_LOCATION:
                    fragment = SelectLocationFragment.newInstance(request, false);
                    break;
                case Constants.KEY_STEP_DETAILS:
                    fragment = SelectDetailsFragment.newInstance(request, false);
                    break;
            }
        } else {
            setToolbar(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.create_job_content, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "create job activity resumed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    TextTools.log(TAG, "finishing ");
                    finish();
                } else {
                    TextTools.log(TAG, "popping ");
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar(boolean back) {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(getMenuIcon());
            ab.setDisplayHomeAsUpEnabled(back);
            ab.setElevation(0);
            ab.setTitle("");
        }
    }
    private Drawable getMenuIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.redSquareColor));
        return drawable;
    }

    @Override
    public void onBackPressed() {
        if (getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .getBoolean(Constants.KEY_UNFINISHED, false)) {
            //
        } else {
            super.onBackPressed();
        }
    }
}