package construction.thesquare.employer.mygraftrs.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.mygraftrs.adapter.MyGraftrsEmployerPagerAdapter;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;

public class MyGraftrsEmployerFragment extends Fragment {

    @BindView(R.id.tabLayoutFragmentMyGraftrs)
    TabLayout tabLayout;
    @BindView(R.id.viewPagerFragmentMyGraftrs)
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_WORKERS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_employer_my_graftrs, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new MyGraftrsEmployerPagerAdapter(getChildFragmentManager(), getContext()));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        tabLayout.setMinimumWidth(width);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("My Workers");
            TextView textView = (TextView) ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(1)).getChildAt(1);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_like_tab, 0);
            int density = (int) getResources().getDisplayMetrics().density;
            textView.setCompoundDrawablePadding(6 * density);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
}