package construction.thesquare.worker.myjobs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import construction.thesquare.R;
import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.myjobs.fragment.JobsListFragment;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsPagerAdapter extends FragmentPagerAdapter {

    private static final int JOBS_COUNT = 4;
    private Context context;
    private List<JobsListFragment> fragments;

    public JobsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        initAdapter();
    }

    private void initAdapter() {
        fragments = new ArrayList<>();
        fragments.add(JobsListFragment.newInstance(Job.TYPE_BOOKED));
        fragments.add(JobsListFragment.newInstance(Job.TYPE_OFFER));
        fragments.add(JobsListFragment.newInstance(Job.TYPE_LIKED));
        fragments.add(JobsListFragment.newInstance(Job.TYPE_OLD));
       // fragments.add(JobsListFragment.newInstance(Job.TYPE_COMPLETED));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.worker_jobs_booked);
            case 1:
                return context.getResources().getString(R.string.worker_jobs_offers_applications);
            case 2:
                return context.getResources().getString(R.string.worker_jobs_liked);
            case 3:
                return context.getResources().getString(R.string.employer_jobs_old);
        }
        return null;
    }

    @Override
    public Fragment getItem(int index) {
        if (index >= 0 && index < JOBS_COUNT) return fragments.get(index);
        return null;
    }

    @Override
    public int getCount() {
        return JOBS_COUNT;
    }
}