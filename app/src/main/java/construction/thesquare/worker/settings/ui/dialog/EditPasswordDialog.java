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

public class EditPasswordDialog extends DialogFragment {
    private static final String TAG = "EditPasswordDialog";

    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.password2_layout)
    TextInputLayout password2Layout;

    private PasswordChangedListener listener;

    public interface PasswordChangedListener {
        void onPasswordChanged(String password);
    }

    public static EditPasswordDialog newInstance(PasswordChangedListener listener) {
        EditPasswordDialog dialog = new EditPasswordDialog();
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (validateData()) {
                    this.dismiss();
                    if (listener != null)
                        listener.onPasswordChanged(passwordLayout.getEditText().getText().toString());
                }
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    private boolean validateData() {
        boolean result = true;
        if ((TextUtils.isEmpty(passwordLayout.getEditText().getText().toString()))) {
            passwordLayout.setError(getString(R.string.validate_password));
            result = false;
        } else if ((TextUtils.isEmpty(password2Layout.getEditText().getText().toString()))) {
            password2Layout.setError(getString(R.string.validate_password_reenter));
            result = false;
        } else if ((!(passwordLayout.getEditText().getText().toString()
                .equals(password2Layout.getEditText().getText().toString())))) {
            password2Layout.setError(getString(R.string.validate_password_match));
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
                TextTools.resetInputLayout(password2Layout);
                TextTools.resetInputLayout(passwordLayout);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    };

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
