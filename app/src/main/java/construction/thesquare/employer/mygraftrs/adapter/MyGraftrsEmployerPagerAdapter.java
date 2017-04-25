package construction.thesquare.employer.mygraftrs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import construction.thesquare.R;
import construction.thesquare.employer.mygraftrs.fragment.MyGraftrsFragment;

import static construction.thesquare.employer.mygraftrs.fragment.MyGraftrsFragment.PAGE_BOOKED;

/**
 * Created by juanmaggi on 21/6/16.
 */
public class MyGraftrsEmployerPagerAdapter extends FragmentPagerAdapter {

    private int MY_GRAFTRS_EMPLOYER_PAGES_COUNT = 1;
    private Context mContext;

    public MyGraftrsEmployerPagerAdapter(FragmentManager fragmentManager,
                                         Context context) {
        super(fragmentManager);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return MyGraftrsFragment.newInstance(PAGE_BOOKED);
//            case 1:
//                return MyGraftrsFragment.newInstance(PAGE_OFFERS);
//            case 1:
//                return MyGraftrsFragment.newInstance(MyGraftrsFragment.PAGE_LIKED);
//            case 1:
//                return MyGraftrsFragment.newInstance(MyGraftrsFragment.PAGE_PREVIOUS);
        }
        return null;
    }

    @Override
    public int getCount() {
        return MY_GRAFTRS_EMPLOYER_PAGES_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getResources().getString(R.string.employer_workers_previous);
//            case 1 :
//                return mContext.getResources().getString(R.string.employer_workers_applications);
//            case 1:
//                return mContext.getResources().getString(R.string.employer_workers_liked);
//            case 1 :
//                return mContext.getResources().getString(R.string.employer_workers_previous);
        }
        return null;
    }
}