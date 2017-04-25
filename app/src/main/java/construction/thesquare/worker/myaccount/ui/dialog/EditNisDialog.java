/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.worker.myaccount.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNisDialog extends DialogFragment {

    @BindViews({R.id.nis_01, R.id.nis_02, R.id.nis_03, R.id.nis_04, R.id.nis_05,
            R.id.nis_06, R.id.nis_07, R.id.nis_08, R.id.nis_09})
    List<JosefinSansEditText> nis;
    private EditText current;
    private EditText next;

    private EditNationalityDialog.ChangeDetailsListener listener;
    private Worker currentWorker;

    public static EditNisDialog newInstance(EditNationalityDialog.ChangeDetailsListener listener) {
        EditNisDialog dialog = new EditNisDialog();
        dialog.setListener(listener);
        return dialog;
    }

    private void setListener(EditNationalityDialog.ChangeDetailsListener listener) {
        this.listener = listener;
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
        View view = inflater.inflate(R.layout.dialog_edit_worker_nis, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (EditText e : nis) {
            e.addTextChangedListener(nisListener);
        }
    }

    private void fetchCurrentWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                currentWorker = response.body().getResponse();
                                populateNis();
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
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

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                patchWorker();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> request = new HashMap<>();
        request.put("ni_number", getNIS());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            if (listener != null) listener.onDataChanged();
                            dismiss();
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

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes((WindowManager.LayoutParams) params);
        }
        super.onResume();

        fetchCurrentWorker();
    }

    private void populateNis() {
        if (currentWorker != null && !TextUtils.isEmpty(currentWorker.niNumber)) {
            for (EditText e : nis) {
                e.removeTextChangedListener(nisListener);
            }

            final char ni[] = currentWorker.niNumber.toCharArray();
            ButterKnife.Setter<JosefinSansEditText, Boolean> ENABLED = new ButterKnife.Setter<JosefinSansEditText, Boolean>() {
                @Override
                public void set(JosefinSansEditText view, Boolean value, int index) {

                    switch (view.getId()) {
                        case R.id.nis_01:
                            nis.get(0).setText(Character.toString(ni[0]));
                        case R.id.nis_02:
                            nis.get(1).setText(Character.toString(ni[1]));
                        case R.id.nis_03:
                            nis.get(2).setText(Character.toString(ni[2]));
                        case R.id.nis_04:
                            nis.get(3).setText(Character.toString(ni[3]));
                        case R.id.nis_05:
                            nis.get(4).setText(Character.toString(ni[4]));
                        case R.id.nis_06:
                            nis.get(5).setText(Character.toString(ni[5]));
                        case R.id.nis_07:
                            nis.get(6).setText(Character.toString(ni[6]));
                        case R.id.nis_08:
                            nis.get(7).setText(Character.toString(ni[7]));
                        case R.id.nis_09:
                            nis.get(8).setText(Character.toString(ni[8]));
                    }
                }
            };
            ButterKnife.apply(nis, ENABLED, true);

            for (EditText e : nis) {
                e.addTextChangedListener(nisListener);
            }
        }
    }

    private String getNIS() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!nis.isEmpty()) {
            for (EditText e : nis) {
                stringBuilder.append(e.getText().toString());
            }
        }
        return stringBuilder.toString();
    }

    private EditText nextNis(EditText currentEditText, boolean goRight) {
        switch (currentEditText.getId()) {
            case R.id.nis_01:
                return goRight ? nis.get(1) : null;
            case R.id.nis_02:
                return goRight ? nis.get(2) : nis.get(0);
            case R.id.nis_03:
                return goRight ? nis.get(3) : nis.get(1);
            case R.id.nis_04:
                return goRight ? nis.get(4) : nis.get(2);
            case R.id.nis_05:
                return goRight ? nis.get(5) : nis.get(3);
            case R.id.nis_06:
                return goRight ? nis.get(6) : nis.get(4);
            case R.id.nis_07:
                return goRight ? nis.get(7) : nis.get(5);
            case R.id.nis_08:
                return goRight ? nis.get(8) : nis.get(6);
            case R.id.nis_09:
                return goRight ? null : nis.get(7);
        }
        return null;
    }

    private TextWatcher nisListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
            if (charSequence.toString().isEmpty()) {
                next = nextNis(current, false);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            } else if (charSequence.toString().length() == 1) {
                next = nextNis(current, true);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    @OnFocusChange({
            R.id.nis_01, R.id.nis_02, R.id.nis_03, R.id.nis_04,
            R.id.nis_05, R.id.nis_06, R.id.nis_07, R.id.nis_08, R.id.nis_09
    })
    void onFocusChange(View view, boolean hasFocus) {
        current = (EditText) view;
        if (!current.getText().toString().isEmpty()) {
            current.setSelection(0, 1);
        }

        DrawableCompat.setTint(current.getBackground(),
                ContextCompat.getColor(getContext(), hasFocus ? R.color.redSquareColor : R.color.graySquareColor));

    }
}
