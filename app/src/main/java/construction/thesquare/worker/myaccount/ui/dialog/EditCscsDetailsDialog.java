package construction.thesquare.worker.myaccount.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCscsDetailsDialog extends DialogFragment {

    @BindView(R.id.surname)
    JosefinSansEditText surname;
    @BindViews({R.id.reg_01, R.id.reg_02, R.id.reg_03, R.id.reg_04, R.id.reg_05,
            R.id.reg_06, R.id.reg_07, R.id.reg_08})
    List<JosefinSansEditText> reg;

    private static final String KEY_LAST_NAME = "KEY_LAST_NAME";
    private EditText current;
    private EditText next;

    private String lastName;
    private OnCscsDetailsUpdatedListener listener;

    public interface OnCscsDetailsUpdatedListener {
        void onCscsUpdated(int status);
    }

    public static EditCscsDetailsDialog newInstance(String surname, OnCscsDetailsUpdatedListener listener) {
        EditCscsDetailsDialog dialog = new EditCscsDetailsDialog();
        Bundle args = new Bundle();
        args.putString(KEY_LAST_NAME, surname);
        dialog.setArguments(args);
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastName = getArguments().getString(KEY_LAST_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_worker_cscs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.verify_cscs).setVisibility(View.GONE);
        surname.setText(lastName);
        surname.setEnabled(false);
        current = reg.get(0);
        reg.get(0).requestFocus();
        for (EditText e : reg) {
            e.addTextChangedListener(regListener);
        }
    }

    @OnClick({R.id.cancel, R.id.done})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                verify();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    public void verify() {
        try {
            final HashMap<String, Object> request = new HashMap<>();
            request.put("surname", lastName);
            request.put("registration_number", getReg());
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .persistOnboardingWorkerCSCSCard(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), request)
                    .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                               Response<ResponseObject<CSCSCardWorker>> response) {
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                                if (response.body().getResponse() != null)
                                    proceed(response.body().getResponse());
                            } else {
                                HandleErrors.parseError(getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<CSCSCardWorker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void proceed(CSCSCardWorker cscsCardWorker) {
        int cscsStatus = cscsCardWorker.verificationStatus;
        if (listener != null) listener.onCscsUpdated(cscsStatus);
        this.dismiss();
    }

    private String getReg() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!reg.isEmpty()) {
            for (EditText e : reg) {
                stringBuilder.append(e.getText().toString());
            }
        }
        return stringBuilder.toString();
    }

    private TextWatcher regListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
            if (charSequence.toString().isEmpty()) {
                next = nextReg(current, false);
                if (null != next) {
                    next.requestFocus();
                    current = next;
                    if (!current.getText().toString().isEmpty()) {
                        current.setSelection(0, 1);
                    }
                }
            } else if (charSequence.toString().length() == 1) {
                next = nextReg(current, true);
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

        }
    };

    private EditText nextReg(EditText currentEditText, boolean goRight) {
        switch (currentEditText.getId()) {
            case R.id.reg_01:
                return goRight ? reg.get(1) : null;
            case R.id.reg_02:
                return goRight ? reg.get(2) : reg.get(0);
            case R.id.reg_03:
                return goRight ? reg.get(3) : reg.get(1);
            case R.id.reg_04:
                return goRight ? reg.get(4) : reg.get(2);
            case R.id.reg_05:
                return goRight ? reg.get(5) : reg.get(3);
            case R.id.reg_06:
                return goRight ? reg.get(6) : reg.get(4);
            case R.id.reg_07:
                return goRight ? reg.get(7) : reg.get(5);
            case R.id.reg_08:
                return goRight ? null : reg.get(6);
        }
        return null;
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
