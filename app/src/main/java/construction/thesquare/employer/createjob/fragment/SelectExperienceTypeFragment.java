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
import construction.thesquare.employer.createjob.adapter.ExperienceTypeAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.ExperienceType;
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

public class SelectExperienceTypeFragment extends Fragment
        implements ExperienceTypeAdapter.ExperienceTypeListener {

    public static final String TAG = "SelectExpTypeFragment";
    private boolean unfinished = true;
    private int selected;

    private CreateRequest request;

    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.create_job_experience_type) RecyclerView list;

    private List<ExperienceType> data = new ArrayList<>();
    private List<ExperienceType> filtered = new ArrayList<>();
    private ExperienceTypeAdapter adapter;

    public static SelectExperienceTypeFragment newInstance(CreateRequest request,
                                                           boolean singleEdit) {
        SelectExperienceTypeFragment selectExperienceTypeFragment
                = new SelectExperienceTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        selectExperienceTypeFragment.setArguments(bundle);
        return selectExperienceTypeFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_EXPERIENCE);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_experience_type, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ExperienceTypeAdapter(filtered);
        adapter.setListener(this);

        filter.addTextChangedListener(filterTextWatcher);
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
    public void onResume() {
        super.onResume();
        request = (CreateRequest) getArguments().getSerializable("request");
        fetchExperienceTypes();
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }
    private void persistProgress() {
        List<ExperienceType> selected = new ArrayList<>();
        for (ExperienceType experienceType : data) {
            if (experienceType.selected) {
                selected.add(experienceType);
            }
        }

        int[] selectedExperienceTypes = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedExperienceTypes[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        request.experienceTypes = selectedExperienceTypes;
        request.experienceTypeObjects = selected;
        request.experienceTypeStrings = strings;

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_EXPERIENCE_TYPE)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    private void populate(List<ExperienceType> experienceTypes) {
        try {
            data.clear();
            data.addAll(experienceTypes);

            if (null != request) {
                if (null != request.experienceTypes) {
                    for (ExperienceType expType : data) {
                        for (int id : request.experienceTypes) {
                            if (expType.id == id) {
                                expType.selected = true;
                            }
                        }
                    }
                }
            }

            filtered.clear();
            filtered.addAll(data);
            adapter.notifyDataSetChanged();

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            for (ExperienceType e : data) {
                for (int i : request.experienceTypes) {
                    if (e.id == i) {
                        e.selected = true;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchExperienceTypes() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchExperienceTypes()
                .enqueue(new Callback<ResponseObject<List<ExperienceType>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ExperienceType>>> call,
                                           Response<ResponseObject<List<ExperienceType>>> response) {
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
                    public void onFailure(Call<ResponseObject<List<ExperienceType>>> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.next)
    public void next() {

        List<ExperienceType> selected = new ArrayList<>();
        for (ExperienceType experienceType : data) {
            if (experienceType.selected) {
                selected.add(experienceType);
            }
        }

        int[] selectedExperienceTypes = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedExperienceTypes[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        request.experienceTypes = selectedExperienceTypes;
        request.experienceTypeObjects = selected;
        request.experienceTypeStrings = strings;

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.create_job_content, SelectLocationFragment
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
                // no filter
                // and safety first
                try {
                    filtered.clear();
                    filtered.addAll(data);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            } else {
                // safety first
                try {
                    filtered.clear();
                    for (ExperienceType o : data) {
                        if (TextTools.contains(o.name.toLowerCase(), charSequence.toString().toLowerCase())) {
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
    public void onExperienceType(ExperienceType experienceType) {
        selected = 0;
        for (ExperienceType e : data) {
            if (e.selected) {
                selected++;
            }
        }

        if (selected > 4) {
            if (experienceType.selected) {
                //
                experienceType.selected = !experienceType.selected;
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
            experienceType.selected = !experienceType.selected;
            adapter.notifyDataSetChanged();
        }
    }

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        construction.thesquare.worker.onboarding.dialog.RoleDialog roleDialog = construction.thesquare.worker.onboarding.dialog.RoleDialog.newInstance(getResources().getString(R.string.suggest_experience_title),Constants.SELECTED_EXPERIENCE_SUGGESTION,  new construction.thesquare.worker.onboarding.dialog.RoleDialog.RoleListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    DialogBuilder.showStandardDialog(getContext(), "", getResources().getString(R.string.suggest_experience_thanks));
                }
            }
        });
        roleDialog.show(getChildFragmentManager(), "");
    }
}