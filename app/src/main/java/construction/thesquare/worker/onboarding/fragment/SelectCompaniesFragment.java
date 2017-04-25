package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Company;
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
import construction.thesquare.worker.onboarding.adapter.CompanyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/21/2016.
 */

public class SelectCompaniesFragment extends Fragment
        implements CompanyAdapter.CompanyListener {

    public static final String TAG = "SelectCompaniesFragment";
    private int workerId;

    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.onboarding_companies)
    RecyclerView list;

    private List<Company> data = new ArrayList<>();
    private List<Company> filtered = new ArrayList<>();
    private CompanyAdapter adapter;
    private Worker currentWorker;
    private List<Company> selected = new ArrayList<>();

    public static SelectCompaniesFragment newInstance(boolean singleEdition) {
        SelectCompaniesFragment selectCompaniesFragment = new SelectCompaniesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectCompaniesFragment.setArguments(bundle);
        return selectCompaniesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_COMPANIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_companies, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        filter.addTextChangedListener(filterTextWatcher);
    }

    private void fetchCompanies() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchCompanies()
                .enqueue(new Callback<ResponseObject<List<Company>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Company>>> call,
                                           Response<ResponseObject<List<Company>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processCompanies(response.body().getResponse());

                            populateData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Company>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processCompanies(List<Company> companies) {
        try {
            data.clear();
            data.addAll(companies);

            filtered.clear();
            filtered.addAll(data);

            adapter = new CompanyAdapter(filtered);
            adapter.setListener(this);

            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @OnClick(R.id.clear_filter)
    public void clear() {
        filter.setText("");
    }

    @OnClick(R.id.next)
    public void next() {
        selected.clear();
        for (Company company : data) {
            if (company.selected) {
                selected.add(company);
            }
        }

        patchWorker();
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
                            fetchCompanies();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            body[i] = selected.get(i).id;
        }

        HashMap<String, Object> request = new HashMap<>();
        request.put("worked_companies_ids", body);

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
                .replace(R.id.onboarding_content, SelectAvailabilityFragment.newInstance(false))
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
                // no filter
                filtered.clear();
                filtered.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                filtered.clear();
                for (Company o : data) {
                    if (o != null && o.name != null
                            && o.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
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

    public void onCompany(Company company) {
        if (company.selected) {
            company.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (Company company1 : filtered) {
                if (company1.selected)
                    count++;
            }
            if (count < 5) {
                company.selected = true;
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
        if (getActivity() == null || !isAdded()) return;

        if (currentWorker != null) {
            selected.clear();
            if (!CollectionUtils.isEmpty(currentWorker.companies))
                selected.addAll(currentWorker.companies);

            for (Company company : filtered) {
                for (Company selectedCompany : selected) {
                    if (company.id == selectedCompany.id)
                        company.selected = true;
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
            for (Company company : data) {
                if (company.selected) {
                    selected.add(company);
                }
            }

            currentWorker.companies = selected;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
