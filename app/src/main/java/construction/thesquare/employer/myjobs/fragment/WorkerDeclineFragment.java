package construction.thesquare.employer.myjobs.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.adapter.DeclineReasonsAdapter;
import construction.thesquare.shared.applications.model.Feedback;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Reason;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.jobmatches.model.Application;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WorkerDeclineFragment extends Fragment {

    public static final String TAG = "WorkerDeclineFragment";

    @BindView(R.id.rv) RecyclerView rv;
    private String declineReason = "";
    private int applicationId;
    private List<Reason> data = new ArrayList<>();

    public WorkerDeclineFragment() {
        // Required empty public constructor
    }

    public static WorkerDeclineFragment newInstance(int applicationId) {
        WorkerDeclineFragment fragment = new WorkerDeclineFragment();
        Bundle args = new Bundle();
        args.putInt("app_id", applicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            applicationId = getArguments().getInt("app_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_decline, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        if (!data.isEmpty()) data.clear();
        String[] reasons = getResources().getStringArray(R.array.decline_worker_reasons);
        for (int i = 0; i < reasons.length; i++) {
            data.add(new Reason(reasons[i], i));
        }
        //
        final DeclineReasonsAdapter adapter = new DeclineReasonsAdapter(getContext(), data);
        adapter.setListener(new DeclineReasonsAdapter.DeclineReasonListener() {
            @Override
            public void onReason(Reason reason) {
//                for (Reason reason1 : data) {
//                    if (reason.id != reason1.id) {
//                        reason.selected = false;
//                    }
//                }
                declineReason = reason.name;
                reason.selected = !reason.selected;
                adapter.notifyDataSetChanged();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }

    @OnClick(R.id.done)
    public void onDone() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .rejectApplicant(applicationId, new Feedback(declineReason))
                .enqueue(new Callback<ResponseObject<Application>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Application>> call,
                                           Response<ResponseObject<Application>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            if (getActivity() != null)
                                getActivity().finish();
                            //
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<construction
                            .thesquare.worker.jobmatches.model.Application>> call,
                                          Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }
}