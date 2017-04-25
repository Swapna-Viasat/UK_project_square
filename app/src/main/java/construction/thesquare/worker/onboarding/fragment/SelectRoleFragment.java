package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.onboarding.adapter.RolesAdapter;
import construction.thesquare.worker.onboarding.dialog.RoleDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectRoleFragment extends Fragment
        implements RolesAdapter.RolesListener {

    public static final String TAG = "SelectRoleFragment";
    private int workerId;

    @BindView(R.id.create_job_roles)
    RecyclerView list;
    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.title)
    JosefinSansTextView title;
    private List<Role> selectedRoles = new ArrayList<>();
    private List<Role> data = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<Role> tradeRoles = new ArrayList<>();
    private RolesAdapter adapter;
    private Worker currentWorker;
    private boolean singleEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_ROLES);
    }

    public static SelectRoleFragment newInstance(boolean singleEdition, Worker worker) {
        SelectRoleFragment selectRoleFragment = new SelectRoleFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        bundle.putSerializable(Constants.KEY_CURRENT_WORKER, worker);
        selectRoleFragment.setArguments(bundle);
        return selectRoleFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_role, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();
        currentWorker = (Worker) getArguments().getSerializable(Constants.KEY_CURRENT_WORKER);
        singleEdit = getArguments().getBoolean(Constants.KEY_SINGLE_EDIT);

        if (currentWorker != null) {
            String name = currentWorker.firstName;
            title.setText(String.format(getString(R.string.onboarding_roles), name));
        }
    }

    @OnClick(R.id.next)
    public void next() {
        if (!selectedRoles.isEmpty()) {
            patchWorker();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Please select at least one role")
                    .show();
        }
    }

    private void proceed() {

        if (getActivity() == null || !isAdded()) return;

        tradeRoles.clear();
        for (Role role : data) {
            if (role.selected) {
                if (role.hasTrades) {
                    tradeRoles.add(role);
                }
            }
        }

        if (tradeRoles.size() == 0) {
            proceedToExperience();
        } else {
            proceedToTrades();
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

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processRoles(response.body().getResponse());
                            populateData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Role>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processRoles(List<Role> fetchedRoles) {
        try {
            data.clear();
            data.addAll(fetchedRoles);
            roles.clear();
            roles.addAll(data);
            adapter = new RolesAdapter(roles);
            adapter.setListener(this);
            list.setLayoutManager(new GridLayoutManager(getContext(), 2));
            list.setAdapter(adapter);

            filter.addTextChangedListener(filterTextWatcher);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selectedRoles.size()];
        for (int i = 0; i < selectedRoles.size(); i++) {
            body[i] = selectedRoles.get(i).id;
        }
        HashMap<String, Object> request = new HashMap<>();
        request.put("roles_ids", body);

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);

                        Analytics.recordEvent(getActivity(),
                                ConstantsAnalytics.EVENT_CATEGORY_ONBOARDING,
                                ConstantsAnalytics.EVENT_WORKER_ROLE_ENTERED);
                        //
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

    @Override
    public void onRoleTapped(Role role, View itemView) {

        if (role.selected) {
            role.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (Role role1 : roles) {
                if (role1.selected) count++;
            }

            if (count > 2) {
                DialogBuilder
                        .showStandardDialog(getContext(), "", getString(R.string.onboarding_selected_max, 3));
            } else {
                role.selected = true;
                adapter.notifyDataSetChanged();
            }
        }

        selectedRoles.clear();
        for (Role role1 : roles) {
            if (role1.selected) {
                selectedRoles.add(role1);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().isEmpty()) {
                // no filter
                roles.clear();
                roles.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                roles.clear();
                for (Role o : data) {
                    if (o.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        roles.add(o);
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

    private void proceedToTrades() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectTradeFragment.newInstance(singleEdit))
                .addToBackStack("")
                .commit();
    }

    private void proceedToExperience() {
        if (getArguments() != null && singleEdit) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectExperienceFragment.newInstance(false))
                .addToBackStack("")
                .commit();
    }


    @OnEditorAction(R.id.filter)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == KeyEvent.KEYCODE_ENTER || actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchRoles();
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void populateData() {
        if (currentWorker != null) {
            selectedRoles.clear();
            if (!CollectionUtils.isEmpty(currentWorker.roles)) {
                selectedRoles.addAll(currentWorker.roles);

                for (Role role : roles) {
                    for (Role selectedRole : selectedRoles) {
                        if (role.id == selectedRole.id)
                            role.selected = true;
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
            currentWorker.roles = selectedRoles;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }

    //New feature
    @OnClick(R.id.suggest_role)
    public void suggestRole() {
        RoleDialog roleDialog = RoleDialog.newInstance(getResources().getString(R.string.suggest_role_title),Constants.SELECTED_ROLE_SUGGESTION, new RoleDialog.RoleListener() {
            @Override
            public void onResult(boolean success) {
                if (success) {
                    DialogBuilder.showStandardDialog(getContext(), "", getResources().getString(R.string.suggest_role_thanks));
                }
            }
        });
        roleDialog.show(getChildFragmentManager(), "");
    }
}