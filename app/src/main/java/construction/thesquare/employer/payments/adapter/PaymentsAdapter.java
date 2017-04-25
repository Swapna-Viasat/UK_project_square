package construction.thesquare.employer.payments.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import construction.thesquare.R;

/**
 * Created by gherg on 1/27/17.
 */

public class PaymentsAdapter extends PagerAdapter {

    public static final String TAG = "PaymentsAdapter";

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.basic;
                break;
            case 1:
                resId = R.id.standard;
                break;
            case 2:
                resId = R.id.premium;
                break;
        }
        return collection.findViewById(resId);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }
}