package construction.thesquare.employer.createjob.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.adapter.RolesAdapter;
import construction.thesquare.employer.createjob.dialog.RoleDialog;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectRoleFragment extends Fragment
        implements RolesAdapter.RolesListener {

    public static final String TAG = "SelectRoleFragment";
    private boolean unfinished = true;
    private boolean firstTime = true;

    @BindView(R.id.create_job_roles) RecyclerView list;
    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.title) JosefinSansTextView title;

    private List<Role> data = new ArrayList<>();
    private List<Role> filtered = new ArrayList<>();
    private List<Role> tradeRoles = new ArrayList<>();
    private RolesAdapter adapter;

    private CreateRequest request;
    private Role selectedRole;

    public static SelectRoleFragment newInstance(CreateRequest createRequest,
                                                 boolean singleEdit) {
        SelectRoleFragment fragment = new SelectRoleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", createRequest);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setHasOptionsMenu(true);


        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_ROLE);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_create_job) {
            unfinished = false;
            firstTime = false;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_role, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(getString(R.string.create_job_role));
    }

    public void onResume() {
        super.onResume();
        TextTools.log(TAG, "on resume");

        request = ((CreateRequest) getArguments().getSerializable("request"));

        if (null != request) {
            selectedRole = request.roleObject;
        }

        fetchRoles();
    }

    private void populate(List<Role> roles) {
        try {
            data.clear();
            data.addAll(roles);

            filtered.clear();
            filtered.addAll(data);

            adapter = new RolesAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new GridLayoutManager(getContext(), 2));
            list.setAdapter(adapter);


            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (Role role : data) {
                    if (role.id == request.role) {
                        role.selected = true;
                        role.amountWorkers = request.workersQuantity;
                    }
                }
            }

            if (getActivity()
                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                    .getBoolean(Constants.KEY_UNFINISHED, false)) {
                for (Role role : data) {
                    if (role.id == request.role) {
                        role.selected = true;
                        role.amountWorkers = request.workersQuantity;
                    }
                }
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        try {
            filter.addTextChangedListener(filterTextWatcher);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
    private void fetchRoles() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchRolesBrief()
                .enqueue(new Callback<ResponseObject<List<Role>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Role>>> call,
                                           Response<ResponseObject<List<Role>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populate(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Role>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.next)
    public void next() {
        if (null != selectedRole) {
            tradeRoles.clear();
            for (Role role : data) {
                if (role.selected) {
                    if (role.hasTrades) {
                        tradeRoles.add(role);
                    }
                }
            }

            if (null == request) {
                request = new CreateRequest();
            }

            request.role = selectedRole.id;
            request.roleName = selectedRole.name;
            // instead of passing the role object separately
            // attaching it to the create job request object
            for (Role role : data) {
                if (role.id == selectedRole.id) {
                    selectedRole = role;
                }
            }
            request.roleObject = selectedRole;
            request.workersQuantity = selectedRole.amountWorkers;

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {

                if (tradeRoles.size() == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("request", request);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame, SelectTradeFragment
                                    .newInstance(request, true))
                            .commit();
                }

            } else {


                if (tradeRoles.size() == 0) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .replace(R.id.create_job_content, SelectExperienceFragment
                                    .newInstance(request, false))
                            .addToBackStack("")
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                            .replace(R.id.create_job_content, SelectTradeFragment
                                    .newInstance(request, false))
                            .addToBackStack("")
                            .commit();
                }
                ////
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Please select at least one role")
                    .show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        try {
            // safety first
            request.workersQuantity = selectedRole.amountWorkers;
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        TextTools.log(TAG, "fragment");
        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_ROLE)
                .putBoolean(Constants.KEY_UNFINISHED, firstTime ? !unfinished : unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @Override
    public void onRoleTapped(Role role, View itemView) {
        for (Role item : data) {
            if (role.id != item.id) {
                item.selected = false;
                item.amountWorkers = 1;
            }
        }
        role.selected = !role.selected;
        role.amountWorkers = 1;
        adapter.notifyDataSetChanged();

        if (null == request) {
            request = new CreateRequest();
        }
        request.role = role.id;
        request.roleName = role.name;
        request.roleObject = role;
        selectedRole = role;
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().trim().isEmpty()) {
                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();

                if (!data.isEmpty()) {
                    for (Role o : data) {
                        if (TextTools.contains(o.name.toLowerCase(),
                                charSequence.toString().toLowerCase())) {
                            filtered.add(o);
                        }
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

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        construction.thesquare.worker.onboarding.dialog.RoleDialog roleDialog =
                construction.thesquare.worker.onboarding.dialog.RoleDialog.newInstance(getResources().getString(R.string.suggest_role_title),Constants.SELECTED_ROLE_SUGGESTION, new construction.thesquare.worker.onboarding.dialog.RoleDialog.RoleListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    DialogBuilder.showStandardDialog(getContext(), "", getResources().getString(R.string.suggest_role_thanks));
                }
            }
        });
        roleDialog.show(getChildFragmentManager(), "");
    }

//    @OnEditorAction(R.id.filter)
//    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (actionId == KeyEvent.KEYCODE_ENTER
//                || actionId == KeyEvent.ACTION_DOWN
//                ||  actionId== EditorInfo.IME_ACTION_DONE ) {
//            InputMethodManager imm = (InputMethodManager)
//                    v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        }
//        return false;
//    }
}