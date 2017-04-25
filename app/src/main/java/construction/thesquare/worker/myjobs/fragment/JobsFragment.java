package construction.thesquare.worker.myjobs.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.worker.myjobs.adapter.JobsPagerAdapter;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsFragment extends Fragment {

    public static final String TAG = "JobsFragment";
    @BindView(R.id.worker_jobs_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.worker_jobs_pager)
    ViewPager viewPager;

    private JobsPagerAdapter jobsPagerAdapter;

    public static JobsFragment newInstance() {
        return new JobsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobsPagerAdapter = new JobsPagerAdapter(getActivity(), getChildFragmentManager());

        Analytics.recordCurrentScreen(getActivity(), ConstantsAnalytics.SCREEN_WORKER_JOBS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_jobs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(jobsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                notifyFragmentVisible(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void notifyFragmentVisible(int position) {
        if (jobsPagerAdapter != null && jobsPagerAdapter.getCount() > 0) {
            JobsListFragment fragment = ((JobsListFragment) jobsPagerAdapter.getItem(position));
            if (fragment != null) fragment.onFragmentBecameVisible();
        }
    }
}