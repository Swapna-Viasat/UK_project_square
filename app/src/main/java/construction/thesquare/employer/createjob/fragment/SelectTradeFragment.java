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
import construction.thesquare.employer.createjob.adapter.TradesAdapter;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.Trade;
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

public class SelectTradeFragment extends Fragment
        implements TradesAdapter.TradesListener {

    public static final String TAG = "SelectTradeFragment";
    private boolean unfinished = true;

    private CreateRequest request;

    @BindView(R.id.filter) JosefinSansEditText filter;
    @BindView(R.id.title) JosefinSansTextView title;
    @BindView(R.id.create_job_trades) RecyclerView list;

    private List<Trade> data = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private TradesAdapter adapter;

    public static SelectTradeFragment newInstance(CreateRequest createRequest,
                                                  boolean singleEdit) {
        SelectTradeFragment selectTradeFragment = new SelectTradeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", createRequest);
        bundle.putSerializable(Constants.KEY_SINGLE_EDIT, singleEdit);
        selectTradeFragment.setArguments(bundle);
        return selectTradeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_TRADE);
    }

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_trades, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void fetchTrades() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchTrades()
                .enqueue(new Callback<ResponseObject<List<Trade>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Trade>>> call,
                                           Response<ResponseObject<List<Trade>>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            populate(response.body().getResponse());
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Trade>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }
    private void populate(List<Trade> tradesList) {
        try {
            data.clear();
            data.addAll(tradesList);
            trades.clear();
            trades.addAll(data);
            adapter = new TradesAdapter(trades);
            adapter.setListener(this);
            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setAdapter(adapter);

            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
                for (Trade trade : data) {
                    for (int id : request.trades) {
                        if (trade.id == id) {
                            trade.selected = true;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            filter.addTextChangedListener(filterTextWatcher);

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        request = ((CreateRequest) getArguments().getSerializable("request"));
        roles.clear();
        roles.add(request.roleObject);

        try {
            if (request.roleObject.isApprentice) {
                title.setText(String.format(getResources()
                                .getString(R.string.create_job_apprentice), request.roleName));
            } else {
                title.setText(String.format(getResources()
                                .getString(R.string.create_job_trade),
                        request.roleName));
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTrades();
    }

    @OnClick(R.id.next)
    public void next() {

        List<Trade> selected = new ArrayList<>();
        selected.clear();
        for (Trade trade : trades) {
            if (trade.selected) {
                selected.add(trade);
            }
        }
        int[] selectedTrades = new int[selected.size()];
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            selectedTrades[i] = selected.get(i).id;
            strings.add(selected.get(i).name);
        }
        request.trades = selectedTrades;
        request.tradeObjects = selected;
        request.tradeStrings = strings;

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            //
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                    .commit();
            //
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.create_job_content,
                            SelectExperienceFragment.newInstance(request, false))
                    .addToBackStack("")
                    .commit();
        }
    }

    @Override
    public void onTradeChecked(final Trade trade) {
        int numSelected = 0;
        for (Trade trade1 : trades) {
            if (trade1.selected) {
                numSelected++;
            }
        }

        if (numSelected > 2) {
            if (trade.selected) {
                // deselect still possible of course
                trade.selected = !trade.selected;
                adapter.notifyDataSetChanged();
                //
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage(getString(R.string.create_job_3))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
        } else {
            // no problems - select
            trade.selected = !trade.selected;
            adapter.notifyDataSetChanged();
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
                // safety first
                try {
                    trades.clear();
                    trades.addAll(data);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            } else {
                // safety first
                try {
                    trades.clear();
                    for (Trade o : data) {
                        if (TextTools.contains(o.name.toLowerCase(), charSequence.toString().toLowerCase())) {
                            trades.add(o);
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

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_TRADE)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @OnClick(R.id.clear_filter)
    public void clear() {
        filter.setText("");
    }
}