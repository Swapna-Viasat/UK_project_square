package construction.thesquare.worker.jobmatches.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.jobmatches.JobMatchesFilterListener;
import construction.thesquare.worker.jobmatches.MatchesContract;
import construction.thesquare.worker.jobmatches.MatchesPresenter;
import construction.thesquare.worker.jobmatches.adapter.JobMatchesAdapter;
import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.jobmatches.model.Ordering;
import construction.thesquare.worker.myaccount.ui.activity.MyAccountViewProfileActivity;

public class JobMatchesFragment extends Fragment
        implements JobMatchesAdapter.JobMatchesActionListener,
        MatchesContract.View, JobMatchesFilterListener {

    private static final String TAG = "JobMatchesFragment";

    @BindView(R.id.job_matches_recycler_view)
    RecyclerView recyclerView;
    private JobMatchesAdapter adapter;
    private List<Job> jobs = new ArrayList<>();
    private MatchesContract.UserActionListener mUserActionsListener;
    @BindView(R.id.worker_matches_hint)
    View hint;
    @BindView(R.id.job_matches_counter)
    JosefinSansTextView counter;
    @BindView(R.id.no_matches)
    View noMatches;
    private Dialog dialog;

    public static JobMatchesFragment newInstance() {
        JobMatchesFragment fragment = new JobMatchesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUserActionsListener = new MatchesPresenter(this);

        Analytics.recordCurrentScreen(getActivity(), ConstantsAnalytics.SCREEN_WORKER_MATCHES);
    }

    @Override
    public void displayMatches(List<Job> data) {
        if (getActivity() == null || !isAdded()) return;

        jobs = data;
        adapter.setData(jobs);
        updateEmptyViewVisibility();
        updateCounter();
    }

    @Override
    public void displayProgress(boolean show) {
        if (show) {
            dialog = DialogBuilder.showCustomDialog(getContext());
        } else {
            DialogBuilder.cancelDialog(dialog);
        }
    }

    @Override
    public void displayHint(boolean show) {
        if (getActivity() == null || !isAdded()) return;
        hint.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_worker_matches_list, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.worker_map:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, JobMatchesMapFragment.newInstance())
                        .commit();
                break;
            case R.id.worker_tune:
                JobMatchesFilterDialog.newInstance(this, mUserActionsListener.getOrdering()
                ).show(getFragmentManager(), "JobMatchesFilterDialog");
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_matches, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCounter();
        updateEmptyViewVisibility();
        mUserActionsListener.fetchMe(getContext());
        mUserActionsListener.fetchJobMatches();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new JobMatchesAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
    }

    public void onViewDetails(final Job job) {
        mUserActionsListener.onShowDetails(getActivity(), job);
    }

    @Override
    public void onLikeJob(Job job) {
        mUserActionsListener.onLikeJobClick(getActivity(), job);
    }

    @OnClick({R.id.worker_matches_exit_hint, R.id.editProfile})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.worker_matches_exit_hint:
                hint.setVisibility(View.GONE);
                break;
            case R.id.editProfile:
                getActivity().startActivity(new Intent(getContext(), MyAccountViewProfileActivity.class));
                break;
        }
    }

    private void updateEmptyViewVisibility() {
        if (getActivity() == null || !isAdded()) return;

        if (jobs == null || jobs.isEmpty()) noMatches.setVisibility(View.VISIBLE);
        else noMatches.setVisibility(View.GONE);
    }

    private void updateCounter() {
        if (getActivity() == null || !isAdded()) return;
        int count = (jobs == null || jobs.isEmpty()) ? 0 : jobs.size();

        counter.setText(String.format(getString(R.string.matching_jobs_profile),
                String.valueOf(count),
                getResources().getQuantityString(R.plurals.job_plural, count)));
    }

    @Override
    public void onFilterSet(Ordering ordering, int commuteTime) {
        mUserActionsListener.setMatchesFilters(ordering, commuteTime);
        mUserActionsListener.fetchJobMatches();
    }
}