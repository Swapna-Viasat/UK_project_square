package construction.thesquare.employer.createjob.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.adapter.ExperienceAdapter;
import construction.thesquare.employer.createjob.adapter.FluencyAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.EnglishLevel;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectExperienceFragment extends Fragment
        implements FluencyAdapter.FluencyListener, ExperienceAdapter.ExperienceListener {

    public static final String TAG = "SelectExperienceFragment";
    private boolean unfinished = true;

    private int english;
    private String englishString = "Basic";
    private int experience;
    private CreateRequest createRequest;

    @BindView(R.id.years) JosefinSansTextView years;
    @BindView(R.id.seek) SeekBar seekBar;
    @BindView(R.id.english) RecyclerView fluency;
    @BindView(R.id.others) RecyclerView others;

    private FluencyAdapter fluencyAdapter;
    private List<EnglishLevel> levels = new ArrayList<>();
    private ExperienceAdapter experienceAdapter;
    private List<Qualification> requirements = new ArrayList<>();

    public static SelectExperienceFragment newInstance(CreateRequest request,
                                                       boolean singleEdit) {
        SelectExperienceFragment selectExperienceFragment = new SelectExperienceFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        bundle.putSerializable("request", request);
        selectExperienceFragment.setArguments(bundle);
        return selectExperienceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_INFO);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_experience_employer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createRequest = (CreateRequest) getArguments().getSerializable("request");

        if (null != createRequest) {
            int i = createRequest.experience;
            experience = i;
            seekBar.setProgress(i);
            years.setText(String.valueOf(i)
                    + ((seekBar.getMax() == i) ? "+ " : " ")
                    + getResources().getQuantityString(R.plurals.year_plural, i));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                years.setText(String.valueOf(i)
                        + ((seekBar.getMax() == i) ? "+ " : " ")
                        + getResources().getQuantityString(R.plurals.year_plural, i));
                experience = i;
                if (null != createRequest) {
                    createRequest.experience = experience;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        fetchData();
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        List<Qualification> selected = new ArrayList<>();
        selected.clear();
        for (Qualification exp : requirements) {
            if (exp.selected) {
                selected.add(exp);
            }
        }
        int[] quals = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            quals[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        createRequest.requirements = quals;
        createRequest.requirementObjects = selected;
        createRequest.requirementStrings = strings;

        createRequest.english = english;

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_EXPERIENCE)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(createRequest))
                .commit();
    }

    private void fetchData() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRequirements()
                .enqueue(new Callback<ResponseObject<List<Qualification>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Qualification>>> call,
                                           Response<ResponseObject<List<Qualification>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateExpQualifications(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Qualification>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchEnglishLevels()
                .enqueue(new Callback<ResponseObject<List<EnglishLevel>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<EnglishLevel>>> call,
                                           Response<ResponseObject<List<EnglishLevel>>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateEnglishLevels(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<EnglishLevel>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateExpQualifications(List<Qualification> data) {
        try {
            requirements.clear();
            requirements.addAll(data);
            if (null != createRequest && null != createRequest.requirements) {
                for (Qualification requirement : data) {
                    for (int id : createRequest.requirements) {
                        if (requirement.id == id) {
                            requirement.selected = true;
                        }
                    }
                }
            }
            experienceAdapter = new ExperienceAdapter(requirements);
            experienceAdapter.setListener(this);
            others.setLayoutManager(new LinearLayoutManager(getContext()));
            others.setAdapter(experienceAdapter);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        try {
            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (Qualification e : requirements) {
                    for (int i : createRequest.requirements) {
                        if (i == e.id) {
                            e.selected = true;
                        }
                    }
                }
                experienceAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void populateEnglishLevels(List<EnglishLevel> data) {
        try {
            levels.clear();
            levels.addAll(data);
            if (null != createRequest) {
                for (EnglishLevel level : data) {
                    if (level.id == createRequest.english) {
                        level.selected = true;
                    }
                }
            }
            fluencyAdapter = new FluencyAdapter(levels);
            fluencyAdapter.setListener(this);
            fluency.setLayoutManager(new LinearLayoutManager(getContext()));
            fluency.setAdapter(fluencyAdapter);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }


        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            seekBar.setProgress(createRequest.experience);
            for (EnglishLevel e : levels) {
                if (e.id == createRequest.english) {
                    e.selected = true;
                    english = createRequest.english;
                }
            }
            fluencyAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.next)
    public void next() {
        if (validate()) {
            TextTools.log(TAG, "English level " + String.valueOf(english));
            createRequest.english = english;
            createRequest.englishLevelString = englishString;
            createRequest.experience = experience;

            List<Qualification> selected = new ArrayList<>();
            selected.clear();
            for (Qualification exp : requirements) {
                if (exp.selected) {
                    selected.add(exp);
                }
            }
            int[] quals = new int[selected.size()];
            List<String> strings = new ArrayList<>();
            for (int i = 0; i < selected.size(); i++) {
                quals[i] = selected.get(i).id;
                strings.add(selected.get(i).name);
            }
            createRequest.requirements = quals;
            createRequest.requirementObjects = selected;
            createRequest.requirementStrings = strings;

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                //
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, PreviewJobFragment.newInstance(createRequest, false))
                        .commit();
                //
            } else {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.create_job_content, SelectQualificationsFragment
                                .newInstance(createRequest, false))
                        .addToBackStack("")
                        .commit();
            }
        }
    }

    private boolean validate() {
        boolean result = true;
        if (english == 0) {
            english = 1;
            // SC228 - if no english selected, go with Basic
            // leaving this commented in case they change their mind again
            // very likely, judging by their history
//            result = false;
//            new AlertDialog.Builder(getActivity())
//                    .setMessage("Please select an \nEnglish proficiency level.")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .show();
        }
        return result;
    }

    @Override
    public void onFluency(EnglishLevel level) {
        for (EnglishLevel e : levels) {
            if (e.id != level.id) {
                e.selected = false;
            }
        }
        english = level.id;
        englishString = level.name;
        level.selected = true;
        fluencyAdapter.notifyDataSetChanged();
        if (null != createRequest) {
            createRequest.english = level.id;
            createRequest.englishLevelString = level.name;
        }
    }

    @Override
    public void onRequirement(Qualification experience) {
        experience.selected = !experience.selected;
        experienceAdapter.notifyDataSetChanged();
    }
}