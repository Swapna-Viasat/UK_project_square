/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.myaccount.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;

public class EditAccountDetailsDialog extends DialogFragment {
    private static final String TAG = "EditAccountDetailsDialog";
    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_INITIAL_VALUE = "KEY_INITIAL_VALUE";
    private static final String KEY_ONLY_DIGITS = "KEY_ONLY_DIGITS";

    @BindView(R.id.job_details_input)
    EditText input;
    @BindView(R.id.title)
    TextView titleTextView;

    private InputFinishedListener listener;
    private String title, initialValue;
    private boolean onlyDigits;

    public interface InputFinishedListener {
        void onDone(String result, boolean onlyDigits);
    }

    public static EditAccountDetailsDialog newInstance(String title, String initialValue, boolean onlyDigits,
                                                       InputFinishedListener inputFinishedListener) {
        EditAccountDetailsDialog dialog = new EditAccountDetailsDialog();
        dialog.listener = inputFinishedListener;
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_INITIAL_VALUE, initialValue);
        args.putBoolean(KEY_ONLY_DIGITS, onlyDigits);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString(KEY_TITLE);
        initialValue = getArguments().getString(KEY_INITIAL_VALUE);
        onlyDigits = getArguments().getBoolean(KEY_ONLY_DIGITS, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_worker_bio, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView.setText(title);
        if (!TextUtils.isEmpty(initialValue)) input.setText(initialValue);
        if (onlyDigits) input.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (listener != null) {
                    listener.onDone(input.getText().toString(), onlyDigits);
                }
                this.dismiss();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes((WindowManager.LayoutParams) params);
        }
        super.onResume();
    }
}
