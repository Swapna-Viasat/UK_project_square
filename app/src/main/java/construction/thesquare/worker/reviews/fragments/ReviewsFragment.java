package construction.thesquare.worker.reviews.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.reviews.activity.ReviewRequestActivity;
import construction.thesquare.worker.reviews.adapter.ReviewsPagerAdapter;

/**
 * Created by Evgheni on 11/11/2016.
 */
public class ReviewsFragment extends Fragment {

    public static final String TAG = "ReviewsFragment";
    @BindView(R.id.worker_reviews_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.worker_reviews_pager)
    ViewPager viewPager;
    private JosefinSansTextView request;

    public static ReviewsFragment newInstance() {
        return new ReviewsFragment();
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
        request = (JosefinSansTextView)
                getActivity().findViewById(R.id.menu_request_label);
        if (null != request) {
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCreateRequest();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        request = (JosefinSansTextView)
                getActivity().findViewById(R.id.menu_request_label);
        if (null != request) {
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCreateRequest();
                }
            });
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reviews");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        if (null != request) request.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_worker_reviews, menu);
        Drawable drawable = menu.getItem(0).getIcon();
        drawable.mutate();
        drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.redSquareColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.worker_request:
                openCreateRequest();
                break;
        }
        return true;
    }

    private void openCreateRequest() {
        Intent intent = new Intent(getActivity(), ReviewRequestActivity.class);
        startActivity(intent);
    }
}