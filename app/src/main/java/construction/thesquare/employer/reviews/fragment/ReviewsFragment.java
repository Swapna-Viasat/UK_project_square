package construction.thesquare.employer.reviews.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.reviews.adapter.ReviewsPagerAdapter;

/**
 * Created by Evgheni on 11/11/2016.
 */
public class ReviewsFragment extends Fragment {

    public static final String TAG = "ReviewsFragment";
    public static ReviewsFragment fragment;

    @BindView(R.id.worker_reviews_tablayout) TabLayout tabLayout;
    @BindView(R.id.worker_reviews_pager) ViewPager viewPager;

    public static ReviewsFragment newInstance() {
        if (fragment == null) {
            fragment = new ReviewsFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager.setAdapter(new ReviewsPagerAdapter(getContext(),
                getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reviews");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }
}