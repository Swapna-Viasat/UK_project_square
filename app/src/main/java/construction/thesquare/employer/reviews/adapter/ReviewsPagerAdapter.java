package construction.thesquare.employer.reviews.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import construction.thesquare.R;
import construction.thesquare.employer.reviews.fragment.ReviewsListFragment;
import construction.thesquare.shared.reviews.Review;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class ReviewsPagerAdapter extends FragmentPagerAdapter {

    private static final int COUNT = 3;
    private Context context;

    public ReviewsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.worker_reviews_received);
            case 1:
                return context.getResources().getString(R.string.worker_reviews_given);
            case 2:
                return context.getResources().getString(R.string.worker_reviews_pending);
        }
        return null;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return ReviewsListFragment.newInstance(Review.TAB_RECEIVED);
            case 1:
                return ReviewsListFragment.newInstance(Review.TAB_GIVEN);
            case 2:
                return ReviewsListFragment.newInstance(Review.TAB_PENDING);
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
