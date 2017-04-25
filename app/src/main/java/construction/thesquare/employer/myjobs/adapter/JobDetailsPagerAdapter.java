package construction.thesquare.employer.myjobs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import construction.thesquare.R;
import construction.thesquare.employer.myjobs.fragment.WorkerListFragment;

/**
 * Created by gherg on 12/30/2016.
 */

public class JobDetailsPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = "JobDetailsPA";

    private static final int NUM = 4;
    private int jobId;
    private Context context;
    private int type;

    public JobDetailsPagerAdapter(Context context,
                                  FragmentManager fragmentManager,
                                  int id, int adapterType) {
        super(fragmentManager);
        this.type = adapterType;
        this.context = context;
        this.jobId = id;
    }

    @Override
    public int getCount() {
        return NUM;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.employer_jobs_booked);
            case 1:
                return context.getString(R.string.employer_jobs_offers);
            case 2:
                return context.getString(R.string.employer_jobs_matched);
            case 3:
                return context.getString(R.string.employer_jobs_declined);
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WorkerListFragment
                        .newInstance(WorkerListFragment.WORKERS_BOOKED,
                                        jobId, type);
            case 1:
                return WorkerListFragment
                        .newInstance(WorkerListFragment.WORKERS_OFFERS,
                                        jobId, type);
            case 2:
                return WorkerListFragment
                        .newInstance(WorkerListFragment.WORKERS_MATCHED,
                                        jobId, type);
            case 3:
                return WorkerListFragment
                        .newInstance(WorkerListFragment.WORKERS_DECLINED,
                                        jobId, type);
            default:
                return null;
        }
    }
}
