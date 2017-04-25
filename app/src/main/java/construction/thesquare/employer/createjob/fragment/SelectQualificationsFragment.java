package construction.thesquare.employer.createjob.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.adapter.QualificationsAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectQualificationsFragment extends Fragment
        implements QualificationsAdapter.QualificationListener {

    public static final String TAG = "SelectQualifications";
    private boolean unfinished = true;
    private int selected;

    private CreateRequest request;

    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.create_job_qualification) RecyclerView list;

    private List<Qualification> data = new ArrayList<>();
    private List<Qualification> filtered = new ArrayList<>();
    private QualificationsAdapter adapter;

    public static SelectQualificationsFragment newInstance(CreateRequest request,
                                                           boolean singleEdit) {
        SelectQualificationsFragment selectQualificationsFragment =
                new SelectQualificationsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        bundle.putSerializable("request", request);
        selectQualificationsFragment.setArguments(bundle);
        return selectQualificationsFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_QUALIFICATIONS);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_qualification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        request = (CreateRequest) getArguments().getSerializable("request");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != request) {
            fetchQualifications(request.role);
        } else {
            try {
                fetchQualifications(((CreateRequest) getArguments().getSerializable("request")).role);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    private void fetchQualifications(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRoleQualifications(id)
                .enqueue(new Callback<ResponseObject<List<Qualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Qualification>>> call,
                                           Response<ResponseObject<List<Qualification>>> response) {
                        //
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            populate(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Qualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populate(List<Qualification> qualifications) {
        try {
            data.clear();
            data.addAll(qualifications);

            if (null != request) {
                if (null != request.qualifications) {
                    for (Qualification qualification : data) {
                        for (int id : request.qualifications) {
                            if (qualification.id == id) {
                                qualification.selected = true;
                            }
                        }
                    }
                }
            }

            filtered.clear();
            filtered.addAll(data);

            adapter = new QualificationsAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);


            filter.addTextChangedListener(filterTextWatcher);

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            if (null != request.qualifications) {
                for (Qualification qualification : data) {
                    for (int i : request.qualifications) {
                        if (qualification.id == i) {
                            qualification.selected = true;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_create_job) {
            unfinished = false;
            if (getActivity()
                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                    .edit()
                    .putInt(Constants.KEY_STEP, 0)
                    .remove(Constants.KEY_REQUEST)
                    .commit()) {
                getActivity().finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        List<Qualification> selected = new ArrayList<>();
        for (Qualification qualification : data) {
            if (qualification.selected) {
                selected.add(qualification);
            }
        }
        int[] selectedQualifications = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedQualifications[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        request.qualifications = selectedQualifications;
        request.qualificationObjects = selected;
        request.qualificationStrings = strings;

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_QUALIFICATIONS)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @OnClick(R.id.next)
    public void next() {
        List<Qualification> selected = new ArrayList<>();
        for (Qualification qualification : data) {
            if (qualification.selected) {
                selected.add(qualification);
            }
        }
        int[] selectedQualifications = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedQualifications[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        request.qualifications = selectedQualifications;
        request.qualificationObjects = selected;
        request.qualificationStrings = strings;

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.create_job_content, SelectSkillsFragment
                            .newInstance(request, false))
                    .addToBackStack("")
                    .commit();
        }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().trim().isEmpty()) {
//                 no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (Qualification o : data) {
                    if (TextTools.contains(o.name.toLowerCase(), charSequence.toString().toLowerCase())) {
                        filtered.add(o);
                    }
                }
                adapter.notifyDataSetChanged();
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
        selected = 0;
        for (Qualification q : data) {
            if (q.selected) {
                selected++;
            }
        }

        if (selected > 4) {
            if (qualification.selected) {
                //
                qualification.selected = !qualification.selected;
                adapter.notifyDataSetChanged();
                //
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage(getString(R.string.create_job_5))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
        } else {
            //
            qualification.selected = !qualification.selected;
            adapter.notifyDataSetChanged();
        }

        TextTools.log(TAG, String.valueOf(selected));
    }

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        construction.thesquare.worker.onboarding.dialog.RoleDialog roleDialog = construction.thesquare.worker.onboarding.dialog.RoleDialog.newInstance(getResources().getString(R.string.suggest_qual_title),Constants.SELECTED_QUALIFICATION_SUGGESTION, new construction.thesquare.worker.onboarding.dialog.RoleDialog.RoleListener() {
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