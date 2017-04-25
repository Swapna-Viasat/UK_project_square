package construction.thesquare.employer.myjobs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import construction.thesquare.R;
import construction.thesquare.employer.myjobs.fragment.JobsListFragment;
import construction.thesquare.shared.models.Job;

/**
 * Created by juanmaggi on 15/6/16.
 */
public class JobsPagerAdapter extends FragmentPagerAdapter {

    private static final int COUNT = 3;
    private Context context;

    public JobsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0 :
                return context.getString(R.string.employer_jobs_old);
            case 1 :
                return context.getString(R.string.employer_jobs_live);
            case 2 :
                return context.getString(R.string.employer_jobs_drafts);
        }
        return null;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return JobsListFragment.newInstance(Job.TAB_OLD);
            case 1:
                return JobsListFragment.newInstance(Job.TAB_LIVE);
            case 2:
                return JobsListFragment.newInstance(Job.TAB_DRAFT);
            default:
                return null;
        }
    }
}
