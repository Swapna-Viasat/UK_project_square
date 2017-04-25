/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.settings.ui.dialog;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.TextTools;

public class EditNameDialog extends DialogFragment {
    private static final String TAG = "EditNameDialog";
    private static final String KEY_FIRST_NAME = "KEY_FIRST_NAME";
    private static final String KEY_LAST_NAME = "KEY_LAST_NAME";

    @BindView(R.id.nameLayout)
    TextInputLayout nameLayout;
    @BindView(R.id.surnameLayout)
    TextInputLayout surnameLayout;

    private NameChangedListener listener;
    private String firstName, lastName;

    public interface NameChangedListener {
        void onNameChanged(String name, String surname);
    }

    public static EditNameDialog newInstance(String firstName, String lastName, NameChangedListener listener) {
        EditNameDialog dialog = new EditNameDialog();
        Bundle args = new Bundle();
        args.putString(KEY_FIRST_NAME, firstName);
        args.putString(KEY_LAST_NAME, lastName);
        dialog.setArguments(args);
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstName = getArguments().getString(KEY_FIRST_NAME);
        lastName = getArguments().getString(KEY_LAST_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_name, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(firstName)) nameLayout.getEditText().setText(firstName);
        if (!TextUtils.isEmpty(lastName)) surnameLayout.getEditText().setText(lastName);
    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (validateData()) {
                    this.dismiss();
                    if (listener != null)
                        listener.onNameChanged(nameLayout.getEditText().getText().toString(), surnameLayout.getEditText().getText().toString());
                }
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    private boolean validateData() {
        boolean result = true;
        if ((TextUtils.isEmpty(nameLayout.getEditText().getText().toString()))) {
            nameLayout.setError(getString(R.string.validate_first));
            result = false;
        } else if ((TextUtils.isEmpty(surnameLayout.getEditText().getText().toString()))) {
            surnameLayout.setError(getString(R.string.validate_last));
            result = false;
        }
        if (!result) resetInputErrors.start();
        return result;
    }

    private CountDownTimer resetInputErrors = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            try {
                TextTools.resetInputLayout(nameLayout);
                TextTools.resetInputLayout(surnameLayout);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    };

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        ViewGroup.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes((WindowManager.LayoutParams) params);
        super.onResume();
    }
}
