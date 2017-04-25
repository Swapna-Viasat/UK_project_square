package construction.thesquare.shared.start.fragment;

/**
 * Created by juanmaggi on 12/5/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TutorialPagerAdapterGeneric extends FragmentPagerAdapter {

    public static int pos = 0;
    private List<Fragment> myFragments;
    private Context context;

    public TutorialPagerAdapterGeneric(Context c, FragmentManager fragmentManager, List<Fragment> myFrags) {
        super(fragmentManager);
        myFragments = myFrags;
        this.context = c;
    }

    @Override
    public Fragment getItem(int position) {
        return myFragments.get(position);
    }

    @Override
    public int getCount() {
        return myFragments.size();
    }


    public static int getPos() {
        return pos;
    }

    public void add(Class<Fragment> c, String title, Bundle b) {
        myFragments.add(Fragment.instantiate(context,c.getName(),b));
    }

    public static void setPos(int pos) {
        TutorialPagerAdapterGeneric.pos = pos;
    }
}
