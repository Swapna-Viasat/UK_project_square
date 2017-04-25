package construction.thesquare.employer.myjobs.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.LikeWorkerConnector;
import construction.thesquare.employer.myjobs.activity.ViewWorkerProfileActivity;
import construction.thesquare.employer.myjobs.adapter.WorkersAdapter;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.response.JobWorkersResponse;
import construction.thesquare.shared.data.model.response.QuickInviteResponse;
import construction.thesquare.shared.models.Application;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/30/2016.
 */

public class WorkerListFragment extends Fragment
        implements WorkersAdapter.WorkersActionListener,
        LikeWorkerConnector.Callback {

    public static final String TAG = "WorkerListFragment";

    public static final int WORKERS_MATCHED = 65;
    public static final int WORKERS_OFFERS = 84;
    public static final int WORKERS_DECLINED = 85;
    public static final int WORKERS_BOOKED = 86;

    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.no_matches) View noMatches;
    private List<Worker> data = new ArrayList<>();
    private WorkersAdapter adapter;
    private LikeWorkerConnector likeWorkerConnector;
    private int adapterType;

    public static WorkerListFragment newInstance(int type,
                                                 int jobId,
                                                 int adapterType) {
        WorkerListFragment fragment = new WorkerListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("adapter_type", adapterType);
        bundle.putInt(Constants.KEY_JOB_ID, jobId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_workers_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterType = getArguments().getInt("adapter_type", 0);
        adapter = new WorkersAdapter(data, getContext(), this, adapterType);
        adapter.registerAdapterDataObserver(observer);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        likeWorkerConnector = new LikeWorkerConnector(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchWorkers(getArguments().getInt(Constants.KEY_JOB_ID));
    }

    private void fetchWorkers(int id) {
        int tab = getArguments().getInt("type", 0);

        if (tab == WORKERS_MATCHED) {
            fetchMatches(id);
        } else if (tab == WORKERS_BOOKED) {
            fetchBooked(id);
        } else if (tab == WORKERS_OFFERS) {
            fetchPending(id);
        } else if (tab == WORKERS_DECLINED) {
            fetchDeclined(id);
        }

    }

    @OnClick(R.id.no_matches)
    public void refresh() {
        fetchWorkers(getArguments().getInt(Constants.KEY_JOB_ID));
    }

    private void fetchDeclined(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobWorkers(id, Application.STATUS_DENIED)
                .enqueue(new Callback<JobWorkersResponse>() {
                    @Override
                    public void onResponse(Call<JobWorkersResponse> call,
                                           Response<JobWorkersResponse> response) {

                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, "success");

                        if (response.isSuccessful()) {


                            data.clear();
                            data.addAll(response.body().response);
                            adapter.notifyDataSetChanged();

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<JobWorkersResponse> call, Throwable t) {
                        TextTools.log(TAG, "fail");
                        //HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchMatches(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobWorkerMatches(id)
                .enqueue(new Callback<JobWorkersResponse>() {
                    @Override
                    public void onResponse(Call<JobWorkersResponse> call,
                                           Response<JobWorkersResponse> response) {

                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, "success");

                        if (response.isSuccessful()) {

                            data.clear();
                            data.addAll(response.body().response);
                            adapter.notifyDataSetChanged();

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<JobWorkersResponse> call, Throwable t) {
                        TextTools.log(TAG, "fail");
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchBooked(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobWorkers(id, Application.STATUS_APPROVED)
                .enqueue(new Callback<JobWorkersResponse>() {
                    @Override
                    public void onResponse(Call<JobWorkersResponse> call,
                                           Response<JobWorkersResponse> response) {

                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, "success");

                        if (response.isSuccessful()) {

                            data.clear();
                            data.addAll(response.body().response);
                            adapter.notifyDataSetChanged();

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<JobWorkersResponse> call, Throwable t) {
                        TextTools.log(TAG, "fail");
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchPending(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobWorkers(id, Application.STATUS_PENDING)
                .enqueue(new Callback<JobWorkersResponse>() {
                    @Override
                    public void onResponse(Call<JobWorkersResponse> call,
                                           Response<JobWorkersResponse> response) {

                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, "success");

                        if (response.isSuccessful()) {


                            data.clear();
                            data.addAll(response.body().response);
                            adapter.notifyDataSetChanged();

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<JobWorkersResponse> call, Throwable t) {
                        TextTools.log(TAG, "fail");
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    //TODO move strings into resources
    public void onInvite(final Worker worker) {
        String promptBook = String.format(getString(R.string.connect_prompt_book),
                (null != worker.firstName) ? worker.firstName : "...");
        String promptConnect = String.format(getString(R.string.connect_prompt_connect),
                (null != worker.firstName) ? worker.firstName : "...");
        new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                .setMessage((adapterType == Constants.ADAPTER_FOR_BOOK) ?
                                promptBook : promptConnect)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        inviteWorker(worker.id, worker.firstName,
                                getArguments().getInt(Constants.KEY_JOB_ID));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onViewWorkerProfile(Worker worker) {
        if (worker != null) {
            Intent viewWorkerProfileIntent = new Intent(getContext(),
                    ViewWorkerProfileActivity.class);
            viewWorkerProfileIntent.putExtra(Constants.KEY_WORKER_ID, worker.id);
            viewWorkerProfileIntent.putExtra(Constants.KEY_JOB_ID,
                    getArguments().getInt(Constants.KEY_JOB_ID));

            getActivity().startActivity(viewWorkerProfileIntent);
        }
    }

    @Override
    public void onLikeWorkerClick(Worker worker) {
        if (worker != null) {
            if (worker.liked) likeWorkerConnector.unlikeWorker(getContext(), worker.id);
            else likeWorkerConnector.likeWorker(getContext(), worker.id);
        }
    }

    @Override
    public void onBook(Worker worker) {
        //
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .acceptApplication(worker.applications.get(0).id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            fetchWorkers(getArguments().getInt(Constants.KEY_JOB_ID));
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void showWorkerInviteSent(String workerName) {
        if (getActivity() == null || !isAdded()) return;

        String confirmBook = String.format(getString(R.string.offer_job_confirm),
                workerName);
        String confirmConnect = String.format(getString(R.string.connect_confirm),
                workerName);
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_offer_confirm);
        ((TextView) dialog.findViewById(R.id.dialog_offer_job_confirm))
                .setText((adapterType == Constants.ADAPTER_FOR_BOOK)
                        ? confirmBook : confirmConnect);
        dialog.findViewById(R.id.offer_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void inviteWorker(int workerId, final String name, int jobId) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        final HashMap<String, Object> body = new HashMap<>();
        body.put("job_id", jobId);
        HttpRestServiceConsumer.getBaseApiClient()
                .quickInvite(body, workerId)
                .enqueue(new Callback<QuickInviteResponse>() {
                    @Override
                    public void onResponse(Call<QuickInviteResponse> call,
                                           Response<QuickInviteResponse> response) {
                        DialogBuilder.cancelDialog(dialog);
                        showWorkerInviteSent(name);
                        fetchWorkers(getArguments().getInt(Constants.KEY_JOB_ID));
                    }

                    @Override
                    public void onFailure(Call<QuickInviteResponse> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (data.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
            } else {
                noMatches.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onConnectorSuccess() {
        fetchWorkers(getArguments().getInt(Constants.KEY_JOB_ID));
    }
}