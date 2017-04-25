package construction.thesquare.worker.jobmatches.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.CommuteTimeSeekBar;
import construction.thesquare.worker.jobmatches.JobMatchesFilterListener;
import construction.thesquare.worker.jobmatches.model.Ordering;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

public class JobMatchesFilterDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY_ORDERING = "KEY_ORDERING";
    private JobMatchesFilterListener listener;
    private CommuteTimeSeekBar seekBar;
    private LinearLayout[] optionLayouts = new LinearLayout[4];
    private Ordering previousOrdering;
    private int previousCommuteTime;

    public JobMatchesFilterDialog() {
    }

    public static JobMatchesFilterDialog newInstance(JobMatchesFilterListener listener,
                                                     Ordering previousOrdering) {
        JobMatchesFilterDialog fragment = new JobMatchesFilterDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_ORDERING, previousOrdering);
        fragment.setListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previousOrdering = (Ordering) getArguments().getSerializable(KEY_ORDERING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_matches_filter, container, false);
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        seekBar = (CommuteTimeSeekBar) view.findViewById(R.id.sbCommute);
        view.findViewById(R.id.closeImage).setOnClickListener(this);
        optionLayouts[0] = (LinearLayout) view.findViewById(R.id.bestPaidLayout);
        optionLayouts[1] = (LinearLayout) view.findViewById(R.id.startDateLayout);
        optionLayouts[2] = (LinearLayout) view.findViewById(R.id.nearestMeLayout);
        optionLayouts[3] = (LinearLayout) view.findViewById(R.id.bestEmployersLayout);
        setupClickListener();
    }

    private void setupClickListener() {
        for (LinearLayout view : optionLayouts) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeImage) {
            setData();
        } else if (v.getId() == R.id.bestPaidLayout) {
            int viewIndex = Arrays.asList(optionLayouts).indexOf(v);
            if (viewIndex > -1) {
                updateOptionView(viewIndex);
            }
            setData();
        } else if (v.getId() == R.id.startDateLayout) {
            int viewIndex = Arrays.asList(optionLayouts).indexOf(v);
            if (viewIndex > -1) {
                updateOptionView(viewIndex);
            }
            setData();
        } else if (v.getId() == R.id.nearestMeLayout) {
            int viewIndex = Arrays.asList(optionLayouts).indexOf(v);
            if (viewIndex > -1) {
                updateOptionView(viewIndex);
            }
            setData();
        } else if (v.getId() == R.id.bestEmployersLayout) {
            int viewIndex = Arrays.asList(optionLayouts).indexOf(v);
            if (viewIndex > -1) {
                updateOptionView(viewIndex);
            }
            setData();
        } else {
            LinearLayout option = (LinearLayout) v;
            int viewIndex = Arrays.asList(optionLayouts).indexOf(option);
            if (viewIndex > -1) {
                updateOptionView(viewIndex);
            }
        }
    }

    private void updateOptionView(int index) {
        for (int i = 0; i < optionLayouts.length; i++) {
            LinearLayout view = optionLayouts[i];
            if (view.isSelected()) view.setSelected(false);
            else
                view.setSelected(i == index);
        }
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.white);
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes((android.view.WindowManager.LayoutParams) params);
        }
        super.onResume();

        if (previousOrdering != null)
            updateOptionView(previousOrdering.ordinal());
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMe();
    }

    private void setListener(JobMatchesFilterListener listener) {
        this.listener = listener;
    }

    private void setData() {
        if (listener != null) {
            listener.onFilterSet(getOrdering(), getCommuteTime());
            this.dismiss();
        }
    }

    private int getCommuteTime() {
        return seekBar.getRate();
    }

    @Nullable
    private Ordering getOrdering() {
        int index = -1;
        for (int i = 0; i < optionLayouts.length; i++) {
            if (optionLayouts[i].isSelected()) {
                index = i;
                break;
            }
        }
        if (index > -1) return Ordering.values()[index];
        else return null;
    }

    private void fetchMe() {
        try {
            List<String> requiredFields = Arrays.asList("filter_commute_time");
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .getFilteredWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), requiredFields)
                    .enqueue(new Callback<ResponseObject<Worker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Worker>> call,
                                               Response<ResponseObject<Worker>> response) {

                            DialogBuilder.cancelDialog(dialog);

                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().getResponse() != null)
                                    previousCommuteTime = response.body().getResponse().filterCommuteTime;
                                seekBar.setRate(previousCommuteTime);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
}
