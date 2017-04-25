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
import construction.thesquare.shared.models.Trade;
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
import construction.thesquare.worker.onboarding.adapter.TradesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectTradeFragment extends Fragment
        implements TradesAdapter.TradesListener {

    public static final String TAG = "SelectTradeFragment";

    private int workerId;

    @BindView(R.id.filter)
    JosefinSansEditText filter;
    @BindView(R.id.title)
    JosefinSansTextView title;
    @BindView(R.id.create_job_trades)
    RecyclerView list;

    private List<Trade> data = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();
    private List<Trade> selectedTrades = new ArrayList<>();
    private TradesAdapter adapter;
    private Worker currentWorker;

    public static SelectTradeFragment newInstance(boolean singleEdition) {
        SelectTradeFragment selectTradeFragment = new SelectTradeFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectTradeFragment.setArguments(bundle);
        return selectTradeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_TRADES);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_trade, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        try {
            title.setText(getString(R.string.onboarding_trades));
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void fetchTrades() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchTrades()
                .enqueue(new Callback<ResponseObject<List<Trade>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Trade>>> call,
                                           Response<ResponseObject<List<Trade>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processTrades(response.body().getResponse());
                            populateData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Trade>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processTrades(List<Trade> fetchedTrades) {
        try {
            data.clear();
            data.addAll(fetchedTrades);
            trades.clear();
            trades.addAll(data);
            adapter = new TradesAdapter(trades);
            adapter.setListener(this);
            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

            filter.addTextChangedListener(filterTextWatcher);

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @OnClick(R.id.next)
    public void next() {
        patchWorker();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        int[] body = new int[selectedTrades.size()];
        for (int i = 0; i < selectedTrades.size(); i++) {
            body[i] = selectedTrades.get(i).id;
        }
        HashMap<String, Object> request = new HashMap<>();
        request.put("trades_ids", body);

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
        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectExperienceFragment
                        .newInstance(false))
                .addToBackStack("")
                .commit();
    }

    @Override
    public void onTradeClick(final Trade trade) {
        if (trade.selected) {
            trade.selected = false;
            adapter.notifyDataSetChanged();
        } else {
            int count = 0;
            for (Trade trade1 : trades) {
                if (trade1.selected) count++;
            }

            if (count <= 2) {
                trade.selected = true;
                adapter.notifyDataSetChanged();
            } else DialogBuilder
                    .showStandardDialog(getContext(), "", getString(R.string.onboarding_selected_max, 3));
        }

        selectedTrades.clear();
        for (Trade trade1 : trades) {
            if (trade1.selected) {
                selectedTrades.add(trade1);
            }
        }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().isEmpty()) {
                // no filter
                trades.clear();
                trades.addAll(data);
                adapter.notifyDataSetChanged();
            } else {
                trades.clear();
                for (Trade o : data) {
                    if (o.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        trades.add(o);
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
    public void onResume() {
        super.onResume();
        loadWorker();
        fetchTrades();
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
            selectedTrades.clear();
            if (!CollectionUtils.isEmpty(currentWorker.trades)) {
                selectedTrades.addAll(currentWorker.trades);

                for (Trade trade : trades) {
                    for (Trade selectedTrade : selectedTrades) {
                        if (trade.id == selectedTrade.id)
                            trade.selected = true;
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
            currentWorker.trades = selectedTrades;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
