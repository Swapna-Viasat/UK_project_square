package construction.thesquare.employer.subscription.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import construction.thesquare.R;
import construction.thesquare.employer.subscription.fragment.CardsFragment;
import construction.thesquare.employer.subscription.fragment.SubscriptionFragment;

/**
 * Created by gherg on 12/29/2016.
 */

public class SubscriptionTabAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private static int NUM_ITEMS = 2;

    public SubscriptionTabAdapter(Context context, FragmentManager manager) {
        super(manager);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SubscriptionFragment.newInstance();
            case 1:
                return CardsFragment.newInstance();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.employer_payments_plan);
            case 1:
                return mContext.getResources().getString(R.string.employer_payments_card);
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
