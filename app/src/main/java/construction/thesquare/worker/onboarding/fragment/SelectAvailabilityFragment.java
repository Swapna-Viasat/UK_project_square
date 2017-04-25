package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.main.ui.MainWorkerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/21/2016.
 */

public class SelectAvailabilityFragment extends Fragment {

    static final String TAG = "SelectRateFragment";
    private int workerId;

    @BindView(R.id.check_looking)
    CheckBox checkLooking;
    @BindView(R.id.check_available)
    CheckBox checkAvailable;

    private boolean availableNow;

    private Worker currentWorker;

    public static SelectAvailabilityFragment newInstance(boolean singleEdition) {
        SelectAvailabilityFragment selectAvailabilityFragment = new SelectAvailabilityFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        selectAvailabilityFragment.setArguments(bundle);
        return selectAvailabilityFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_AVAILABILITY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();
    }

    @OnCheckedChanged({R.id.check_available, R.id.check_looking})
    public void availability(CompoundButton view, boolean checked) {
        switch (view.getId()) {
            case R.id.check_available:
                checkAvailable.setChecked(checked);
                checkLooking.setChecked(!checked);
                availableNow = checked;
                break;
            case R.id.check_looking:
                checkLooking.setChecked(checked);
                checkAvailable.setChecked(!checked);
                availableNow = !checked;
                break;
        }
    }

    @OnClick(R.id.next)
    public void next() {
        patchWorker();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> request = new HashMap<>();
        request.put("available_now", availableNow);

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

        Analytics.recordEvent(getActivity(),
                ConstantsAnalytics.EVENT_CATEGORY_ONBOARDING,
                ConstantsAnalytics.EVENT_WORKER_ONBOARDED);

        Intent intent = new Intent(getActivity(), MainWorkerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();
        populateData();
    }

    @Override
    public void onPause() {
        persistProgress();
        super.onPause();
    }

    private void populateData() {
        if (currentWorker != null) {
            checkAvailable.setChecked(currentWorker.now);
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
            currentWorker.now = availableNow;
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
