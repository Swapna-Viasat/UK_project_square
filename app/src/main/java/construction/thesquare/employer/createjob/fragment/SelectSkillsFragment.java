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
import construction.thesquare.employer.createjob.adapter.SkillsAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Skill;
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

public class SelectSkillsFragment extends Fragment
        implements SkillsAdapter.SkillListener {

    public static final String TAG = "SelectSkillsFragment";
    private boolean unfinished = true;
    private int selected;

    private CreateRequest request;

    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.create_job_skills) RecyclerView list;

    private List<Skill> data = new ArrayList<>();
    private List<Skill> filtered = new ArrayList<>();
    private SkillsAdapter adapter;

    public static SelectSkillsFragment newInstance(CreateRequest request,
                                                   boolean singleEdit) {
        SelectSkillsFragment selectSkillsFragment = new SelectSkillsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        selectSkillsFragment.setArguments(bundle);
        return selectSkillsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_SKILLS);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_skills, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        request = (CreateRequest) getArguments().getSerializable("request");
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

    private void fetchRoleSkills(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRoleSkills(id)
                .enqueue(new Callback<ResponseObject<List<Skill>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Skill>>> call,
                                           Response<ResponseObject<List<Skill>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populateRoleSkills(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Skill>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateRoleSkills(List<Skill> skills) {
        try {
            data.clear();
            data.addAll(skills);

            if (null != request) {
                if (null != request.skills) {
                    for (Skill skill : data) {
                        for (int id : request.skills) {
                            if (skill.id == id) {
                                skill.selected = true;
                            }
                        }
                    }
                }
            }

            filtered.clear();
            filtered.addAll(data);

            adapter = new SkillsAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

            filter.addTextChangedListener(filterTextWatcher);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        try {

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (Skill skill : data) {
                    for (int skillID : request.skills) {
                        if (skill.id == skillID) {
                            skill.selected = true;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != request) {
            fetchRoleSkills(request.role);
        } else {
            request = (CreateRequest) getArguments().getSerializable("request");
            if (request != null)
                fetchRoleSkills(request.role);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        List<Skill> selected = new ArrayList<>();
        for (Skill skill : data) {
            if (skill.selected) {
                selected.add(skill);
            }
        }
        int[] selectedSkills = new int[selected.size()];
        List<String> selectedSkillStrings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedSkills[i] = selected.get(i).id;
            selectedSkillStrings.add(selected.get(i).name);
        }

        request.skills = selectedSkills;
        request.skillStrings = selectedSkillStrings;

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_SKILLS)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @OnClick(R.id.next)
    public void next() {
        List<Skill> selected = new ArrayList<>();
        for (Skill skill : data) {
            if (skill.selected) {
                selected.add(skill);
            }
        }
        int[] selectedSkills = new int[selected.size()];
        List<String> selectedSkillStrings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedSkills[i] = selected.get(i).id;
            selectedSkillStrings.add(selected.get(i).name);
        }

        request.skills = selectedSkills;
        request.skillStrings = selectedSkillStrings;

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            //
            Bundle bundle = new Bundle();
            bundle.putSerializable("request", request);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                    .commit();
            //
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.create_job_content, SelectExperienceTypeFragment
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
//                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (Skill o : data) {
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
    public void onSkill(Skill skill) {
        selected = 0;
        for (Skill s : data) {
            if (s.selected) {
                selected++;
            }
        }

        if (selected > 4) {
            if (skill.selected) {
                //
                skill.selected = !skill.selected;
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
            skill.selected = !skill.selected;
            adapter.notifyDataSetChanged();
        }

    }

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        construction.thesquare.worker.onboarding.dialog.RoleDialog roleDialog = construction.thesquare.worker.onboarding.dialog.RoleDialog.newInstance(getResources().getString(R.string.suggest_skill_title), Constants.SELECTED_SKILL_SUGGESTION,new construction.thesquare.worker.onboarding.dialog.RoleDialog.RoleListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    DialogBuilder.showStandardDialog(getContext(), "", getResources().getString(R.string.suggest_skill_thanks));
                }
            }
        });
        roleDialog.show(getChildFragmentManager(), "");
    }
}