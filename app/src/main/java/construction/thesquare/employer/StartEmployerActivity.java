package construction.thesquare.employer;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.veriphone.VerifyPhoneActivity;
import construction.thesquare.shared.start.ActionBarViewHolder;
import construction.thesquare.shared.start.fragment.TutorialFragmentStep;
import construction.thesquare.shared.start.fragment.TutorialPagerAdapterGeneric;
import construction.thesquare.shared.utils.Constants;

/**
 * Refactored by Evgheni Gherghelejiu
 * on 01/12/2017
 */
public class StartEmployerActivity extends AppCompatActivity {

    @BindView(R.id.btnStartEmployer) Button btnStartEmployer;

    @BindView(R.id.viewPagerEmployerTutorial) ViewPager viewPager;
    @BindView(R.id.ivTutorialEmployer1) ImageView tutorialStep1;
    @BindView(R.id.ivTutorialEmployer2) ImageView tutorialStep2;
    @BindView(R.id.ivTutorialEmployer3) ImageView tutorialStep3;
    @BindView(R.id.ivTutorialEmployer4) ImageView tutorialStep4;

    protected ActionBarViewHolder actionBarViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_employer);
        ButterKnife.bind(this);

        viewPager.setAdapter(new TutorialPagerAdapterGeneric(this,
                getSupportFragmentManager(), getFragments()));
        viewPager.addOnPageChangeListener(new EmployerTutorialPageChangeListener());

        updateIndicators(0);

        setActionBar();

        Analytics.recordCurrentScreen(this, ConstantsAnalytics.SCREEN_TUTORIAL_EMPLOYER);
    }

    private List<Fragment> getFragments() {
        List<android.support.v4.app.Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(TutorialFragmentStep.newInstance(getResources().getString(R.string.tutorial_employer_one_title),
                getResources().getString(R.string.tutorial_employer_one_text),
                R.drawable.employer_tutorial_1));
        fragments.add(TutorialFragmentStep.newInstance(getResources().getString(R.string.tutorial_employer_two_title),
                getResources().getString(R.string.tutorial_employer_two_text),
                R.drawable.employer_tutorial_2));
        fragments.add(TutorialFragmentStep.newInstance(getResources().getString(R.string.tutorial_employer_three_title),
                getResources().getString(R.string.tutorial_employer_three_text),
                R.drawable.employer_tutorial_3));
        fragments.add(TutorialFragmentStep.newInstance(getResources().getString(R.string.tutorial_employer_four_title),
                getResources().getString(R.string.tutorial_employer_four_text),
                R.drawable.employer_tutorial_4));
        return fragments;
    }

    @OnClick(R.id.btnStartEmployer)
    public void begin() {
        Intent intent = new Intent(this, VerifyPhoneActivity.class);
        intent.putExtra(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_EMPLOYER);
        startActivity(intent);
    }

    public void updateIndicators(int position) {
        switch (position) {
            case 0:
                tutorialStep1.setImageResource(R.drawable.rounded_filled_circle);
                tutorialStep2.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep3.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep4.setImageResource(R.drawable.rounded_bordered_circle);
                break;
            case 1:
                tutorialStep1.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep2.setImageResource(R.drawable.rounded_filled_circle);
                tutorialStep3.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep4.setImageResource(R.drawable.rounded_bordered_circle);
                break;
            case 2:
                tutorialStep1.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep2.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep3.setImageResource(R.drawable.rounded_filled_circle);
                tutorialStep4.setImageResource(R.drawable.rounded_bordered_circle);
                break;
            case 3:
                tutorialStep1.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep2.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep3.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep4.setImageResource(R.drawable.rounded_filled_circle);
                break;
            case 4:
                tutorialStep1.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep2.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep3.setImageResource(R.drawable.rounded_bordered_circle);
                tutorialStep4.setImageResource(R.drawable.rounded_bordered_circle);
                break;
        }
    }
    public void setActionBar() {
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_sign_up, null, false);
        actionBarViewHolder = new ActionBarViewHolder(actionBarView, 0);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.whiteSquareColor)));
            actionBar.setCustomView(actionBarView, layoutParams);
        }
        actionBarViewHolder.enableBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public class EmployerTutorialPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            //
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            //
        }

        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
        }
    }
}
