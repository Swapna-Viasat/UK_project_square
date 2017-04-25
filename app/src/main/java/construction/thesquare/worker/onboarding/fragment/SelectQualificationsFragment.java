package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.adapter.QualificationsAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.RolesRequest;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.onboarding.dialog.RoleDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectQualificationsFragment extends Fragment
        implements QualificationsAdapter.QualificationListener {

    public static final String TAG = "SelectQualificationsFragment";
    private int workerId;

    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.create_job_qualification)
    RecyclerView list;
    @BindView(R.id.title)
    JosefinSansTextView title;

    private List<Qualification> data = new ArrayList<>();
    private List<Qualification> filtered = new ArrayList<>();
    private QualificationsAdapter adapter;
    private Worker currentWorker;
    private List<Qualification> selected = new ArrayList<>();

    public static SelectQualificationsFragment newInstance(boolean singleEdition) {
        SelectQualificationsFragment selectQualificationsFragment = new SelectQualificationsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectQualificationsFragment.setArguments(bundle);
        return selectQualificationsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_QUALIFICATIONS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_qualification, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            title.setText(getString(R.string.onboarding_qualifications));
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        filter.addTextChangedListener(filterTextWatcher);
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
                            TextTools.log(TAG, "success");
                            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT))
                                currentWorker = response.body().getResponse();

                            fetchQualifications(response.body().getResponse().roles);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchQualifications(List<Role> userRoles) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        List<Integer> roleIds = new ArrayList<>();

        if (userRoles != null && !userRoles.isEmpty()) {
            for (Role role : userRoles) {
                roleIds.add(role.id);
            }
        }

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRoleQualifications(new RolesRequest(roleIds))
                .enqueue(new Callback<ResponseObject<List<Qualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Qualification>>> call,
                                           Response<ResponseObject<List<Qualification>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            onSuccessfulResponse(response.body().getResponse());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Qualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void onSuccessfulResponse(List<Qualification> response) {
        try {
            processData(response);
            populateData();
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void processData(List<Qualification> fetchedQualifications) {
        data.clear();

        data.addAll(fetchedQualifications);

        filtered.clear();
        filtered.addAll(data);

        adapter = new QualificationsAdapter(filtered);
        adapter.setListener(this);

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
    }

    @OnClick(R.id.next)
    public void next() {
        selected.clear();
        for (Qualification qualification : data) {
            if (qualification.selected) {
                selected.add(qualification);
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
        request.put("update_filtered", "qualifications");

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
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void proceed() {
        if (getActivity() == null || !isAdded()) return;

        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectSkillsFragment
                        .newInstance(false))
                .addToBackStack("")
                .commit();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("")) {
//                 no filter

                try {
                    filtered.clear();
                    filtered.addAll(data);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }

            } else {

                try {
                    filtered.clear();
                    for (Qualification o : data) {
                        if (o.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(o);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @OnClick(R.id.clear_filter)
    public void clear() {
        filter.setText("");
    }

    @Override
    public void onQualification(Qualification qualification) {
        if (qualification.selected) {
            qualification.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (Qualification qualification1 : filtered) {
                if (qualification1.selected)
                    count++;
            }
            if (count < 5) {
                qualification.selected = true;
                adapter.notifyDataSetChanged();
            } else DialogBuilder
                    .showStandardDialog(getContext(), "", getString(R.string.onboarding_selected_max, 5));
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

    private void populateData() {
        if (currentWorker != null) {
            selected.clear();

            if (!CollectionUtils.isEmpty(currentWorker.qualifications))
                for (Qualification qualification : filtered) {
                    for (Qualification selectedQualification : currentWorker.qualifications) {
                        if (qualification.id == selectedQualification.id && !selectedQualification.onExperience) {
                            qualification.selected = true;
                            selected.add(qualification);
                        }
                    }
                }

            adapter.notifyDataSetChanged();
        }
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private void persistProgress() {
        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) return;

        if (currentWorker != null) {

            selected.clear();

            if (!CollectionUtils.isEmpty(currentWorker.qualifications)) {
                List<Qualification> workerQualifications = new ArrayList<>(currentWorker.qualifications);
                for (Qualification q : currentWorker.qualifications) {
                    if (!q.onExperience) workerQualifications.remove(q);
                }
                currentWorker.qualifications = workerQualifications;
            }

            for (Qualification qualification : filtered) {
                if (qualification.selected) {
                    selected.add(qualification);
                }
            }

            currentWorker.qualifications.addAll(selected);
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        RoleDialog roleDialog = RoleDialog.newInstance(getResources().getString(R.string.suggest_qual_title), Constants.SELECTED_QUALIFICATION_SUGGESTION, new RoleDialog.RoleListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    DialogBuilder.showStandardDialog(getContext(), "", getResources().getString(R.string.suggest_qual_thanks));
                }
            }
        });
        roleDialog.show(getChildFragmentManager(), "");
    }
}