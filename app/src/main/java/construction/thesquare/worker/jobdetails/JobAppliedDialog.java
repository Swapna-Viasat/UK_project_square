package construction.thesquare.worker.jobdetails;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import construction.thesquare.R;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.worker.jobmatches.model.Job;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */
public class JobAppliedDialog extends DialogFragment implements View.OnClickListener {
    private static final String KEY_JOB = "KEY_JOB";
    private Job currentJob;
    private TextView titleTextView;

    public JobAppliedDialog() {
    }

    public static JobAppliedDialog newInstance(Job job) {
        JobAppliedDialog dialog = new JobAppliedDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_JOB, job);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentJob = (Job) getArguments().getSerializable(KEY_JOB);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_job_applied, container, false);
        setupViews(view);
        populateData();
        return view;
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
    }

    private void setupViews(View view) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        titleTextView = (TextView) view.findViewById(R.id.titleText);
        view.findViewById(R.id.btnClose).setOnClickListener(this);
        view.findViewById(R.id.closeImage).setOnClickListener(this);
    }

    private void populateData() {
        if (currentJob == null) return;
        SpannableString spannableString;

        titleTextView.setText(getString(R.string.job_applied_title_1));
        if (currentJob.role != null && currentJob.role.name != null) {
            spannableString = new SpannableString(currentJob.role.name);
            titleTextView.append(" ");
            titleTextView.append(spannableString);
        }

        titleTextView.append(" ");
        titleTextView.append(getString(R.string.job_applied_title_2));

        if (currentJob.company != null && currentJob.company.name != null) {
            spannableString = new SpannableString(currentJob.company.name);
            titleTextView.append(" ");
            titleTextView.append(spannableString);
        }

        titleTextView.append(" ");
        titleTextView.append(getString(R.string.job_applied_title_3));
        titleTextView.append(" ");
        if (!TextUtils.isEmpty(currentJob.startTime)) {
            spannableString = new SpannableString(DateUtils.formatDateDayAndMonth(currentJob.startTime, true));
            titleTextView.append(spannableString);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeImage:
                dismiss();
                break;
            case R.id.btnClose:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
