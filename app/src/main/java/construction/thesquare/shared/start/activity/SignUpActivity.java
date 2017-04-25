package construction.thesquare.shared.start.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.start.ActionBarViewHolder;
import construction.thesquare.shared.start.SignUpViewPager;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;

/**
 * Created by juanmaggi on 10/5/16.
 */
public abstract class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.viewPagerWizard)
    protected SignUpViewPager viewPager;

    protected ActionBarViewHolder actionBarViewHolder;
    protected int currentPage;
    protected boolean isLogin;
    protected boolean onlyCurrentPage;
    private static final String TAG = "SignUpActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        currentPage = bundle.getInt("currentPage");

        isLogin = bundle.getBoolean("isLogin");
        onlyCurrentPage = bundle.getBoolean("onlyCurrentPage", false);

        if (currentPage == 2) {
            setCustomActionBar(getCountSteps());
        } else setCustomActionBar(0);
        if ( (currentPage == 0) || (currentPage == 1)) {
            actionBarViewHolder.enableBackButton(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    public void setCustomActionBar(int indicatorsCount) {
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_sign_up, null, false);
        actionBarViewHolder = new ActionBarViewHolder(actionBarView, indicatorsCount);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(actionBarView, layoutParams);
            Toolbar parent = (Toolbar) actionBarView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {

            if ((SharedPreferencesManager.getInstance(this).getToken() != null) && (viewPager.getCurrentItem() == 2)) {
                finish();
            } else {
                prevStep();
            }
        }
    }

    //<y>
    public void nextStep() {
        if (viewPager.getCurrentItem() < (viewPager.getAdapter().getCount() - 1)) {
            TextTools.log(TAG, "Continue to Next STEP");
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else {
            //Toast.makeText(this, "This is the last STEP", Toast.LENGTH_LONG);
            TextTools.log(TAG, "Continue to MAIN Screen");
            try {
                continueToApp();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    public void prevStep() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle header native back arrow
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return false;
    }

    public SignUpViewPager getViewPager() {
        return viewPager;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public void phoneValidated() {
        nextStep();
    }

    public abstract void continueToApp();

    public abstract int getCountSteps();
}
