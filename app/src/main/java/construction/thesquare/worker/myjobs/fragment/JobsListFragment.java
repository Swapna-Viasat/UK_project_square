package construction.thesquare.worker.myjobs.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.worker.jobdetails.JobDetailActivity;
import construction.thesquare.worker.jobdetails.LikeJobConnector;
import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.myaccount.ui.activity.MyAccountViewProfileActivity;
import construction.thesquare.worker.myjobs.JobsContract;
import construction.thesquare.worker.myjobs.JobsPresenter;
import construction.thesquare.worker.myjobs.adapter.JobsAdapter;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsListFragment extends Fragment
        implements JobsContract.View,
        JobsAdapter.JobsActionListener, LikeJobConnector.Callback {

    public static final String TAG = "JobListFragment";
    private int jobType;
    private JobsPresenter presenter;
    private JobsAdapter jobsAdapter;
    private List<Job> jobs = new ArrayList<>();
    private LikeJobConnector likeJobConnector;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.no_matches)
    TextView noMatches;
    @BindView(R.id.profile_link)
    TextView profileLink;
    private Dialog dialog;

    public static JobsListFragment newInstance(int jobType) {
        JobsListFragment jobsListFragment = new JobsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", jobType);
        jobsListFragment.setArguments(bundle);
        return jobsListFragment;
    }

    @Override
    public void onViewDetails(Job job) {
        if (getActivity() == null || job == null) return;
        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.JOB_ARG, job.id);
        startActivity(intent);
    }

    @Override
    public void onLikeJob(Job job) {
        if (job == null) return;

        if (job.liked) likeJobConnector.unlikeJob(getContext(), job.id);
        else likeJobConnector.likeJob(getContext(), job.id);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobType = getArguments().getInt("type");
        presenter = new JobsPresenter(getActivity(), this);
        jobsAdapter = new JobsAdapter(jobs, getContext(), this);
        likeJobConnector = new LikeJobConnector(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_myjobs_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        jobsAdapter.registerAdapterDataObserver(observer);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(jobsAdapter);
    }

    @OnClick(R.id.profile_link)
    void openProfile() {
        getContext().startActivity(new Intent(getContext(), MyAccountViewProfileActivity.class));
    }

    public void onFragmentBecameVisible() {
        if (null != presenter) presenter.init(jobType);

        switch (jobType) {
            case Job.TYPE_BOOKED:
                if (noMatches != null) noMatches.setText("No jobs in here yet.\n" +
                        "Booked workers have better profiles. Remember to ");
                profileLink.setVisibility(View.VISIBLE);
                break;
            case Job.TYPE_OFFER:
                if (noMatches != null) noMatches.setText("No jobs in here yet.\n" +
                        "Get applying!");
                profileLink.setVisibility(View.GONE);
                break;
            case Job.TYPE_LIKED:
                if (noMatches != null) noMatches.setText("No jobs in here yet!\n" +
                        "We'll list all the jobs you like in here.");
                profileLink.setVisibility(View.GONE);
                break;
            case Job.TYPE_OLD:
                if (noMatches != null)
                    noMatches.setText("This is where all your old jobs are kept.");
                profileLink.setVisibility(View.GONE);
                break;
            default:
                profileLink.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentBecameVisible();
    }

    public void displayJobs(List<Job> data) {
        if (getActivity() == null || !isAdded()) return;

        if (!jobs.isEmpty()) jobs.clear();
        jobs.addAll(data);
        jobsAdapter.notifyDataSetChanged();
    }

    public void displayProgress(boolean show) {
        if (show) {
            dialog = DialogBuilder.showCustomDialog(getContext());
        } else {
            DialogBuilder.cancelDialog(dialog);
        }
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (jobs.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
            } else {
                noMatches.setVisibility(View.GONE);
                profileLink.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onConnectorSuccess() {
        if (null != presenter) presenter.init(jobType);
    }
}
