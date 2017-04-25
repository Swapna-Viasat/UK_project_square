/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.ExperienceQualification;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.onboarding.adapter.ExperienceAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectRequirementsFragment extends Fragment implements ExperienceAdapter.ExperienceListener {
    public static final String TAG = "SelectExperienceFragment";
    private int workerId;

    @BindView(R.id.others)
    RecyclerView others;
    @BindView(R.id.top)
    JosefinSansTextView top;

    private ExperienceAdapter experienceAdapter;
    private List<ExperienceQualification> qualifications = new ArrayList<>();
    private Worker currentWorker;
    private List<ExperienceQualification> selected = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_QUALIFICATIONS);
    }

    public static SelectRequirementsFragment newInstance(boolean singleEdition) {
        SelectRequirementsFragment selectRequirementsFragment = new SelectRequirementsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectRequirementsFragment.setArguments(bundle);
        return selectRequirementsFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_requirements, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();
        top.setText(getString(R.string.employer_worker_details_requirements));
    }

    private void fetchRequirements() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchExperienceQualifications()
                .enqueue(new Callback<ResponseObject<List<ExperienceQualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ExperienceQualification>>> call,
                                           Response<ResponseObject<List<ExperienceQualification>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processQualifications(response.body().getResponse());
                            populateSavedRequirements();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<ExperienceQualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processQualifications(List<ExperienceQualification> fetchedQualifications) {
        try {
            qualifications.clear();
            qualifications.addAll(fetchedQualifications);
            experienceAdapter = new ExperienceAdapter(qualifications);
            experienceAdapter.setListener(this);
            others.setLayoutManager(new LinearLayoutManager(getContext()));
            others.setAdapter(experienceAdapter);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @OnClick(R.id.next)
    public void next() {
        selected.clear();
        for (ExperienceQualification exp : qualifications) {
            if (exp.selected) {
                selected.add(exp);
            }
        }

        patchWorker();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            body[i] = selected.get(i).id;
        }

        HashMap<String, Object> request = new HashMap<>();
        request.put("qualifications_ids", body);
        request.put("update_filtered", "requirements");

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            proceed();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void proceed() {
        if (getActivity() == null || !isAdded()) return;

        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    @Override
    public void onExperience(ExperienceQualification experience) {
        //
        if (experience != null && experience.name != null && experience.name.equals("CSCS Card")) {
            experience.selected = !experience.selected;
            experienceAdapter.notifyDataSetChanged();
        } else if (experience != null) {
            experience.selected = !experience.selected;
            experienceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchMe();
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void fetchMe() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT))
                                currentWorker = response.body().getResponse();
                            fetchRequirements();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }


    private void populateSavedRequirements() {
        if (currentWorker != null && !CollectionUtils.isEmpty(qualifications)
                && !CollectionUtils.isEmpty(currentWorker.qualifications)) {

            for (ExperienceQualification qualification : qualifications) {
                for (Qualification selectedQualification : currentWorker.qualifications) {
                    if (qualification.id == selectedQualification.id && qualification.onExperience)
                        qualification.selected = true;
                }
            }
            experienceAdapter.notifyDataSetChanged();
        }
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private List<Qualification> requirementsToQualifications(List<ExperienceQualification> list) {
        List<Qualification> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (ExperienceQualification e : list) {
                result.add(new Qualification(e));
            }
        }
        return result;
    }

    private void persistProgress() {
        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) return;

        if (currentWorker != null) {
            selected.clear();
            for (ExperienceQualification exp : qualifications) {
                if (exp.selected) {
                    selected.add(exp);
                }
            }

            if (!CollectionUtils.isEmpty(currentWorker.qualifications)) {
                List<Qualification> workerQualifications = new ArrayList<>(currentWorker.qualifications);

                for (Qualification qualification : currentWorker.qualifications) {
                    if (qualification.onExperience) workerQualifications.remove(qualification);
                }

                currentWorker.qualifications = workerQualifications;
            }

            currentWorker.qualifications.addAll(requirementsToQualifications(selected));
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
