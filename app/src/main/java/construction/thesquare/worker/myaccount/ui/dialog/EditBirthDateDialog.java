/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.worker.myaccount.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.myaccount.ui.dialog.EditNationalityDialog.ChangeDetailsListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBirthDateDialog extends DialogFragment {

    @BindView(R.id.spinner_day)
    Spinner spinnerDay;
    @BindView(R.id.spinner_month)
    Spinner spinnerMonth;
    @BindView(R.id.spinner_year)
    Spinner spinnerYear;

    private ChangeDetailsListener listener;
    private ArrayAdapter<CharSequence> monthAdapter;
    private ArrayAdapter<CharSequence> dayAdapter;
    private ArrayAdapter<CharSequence> yearAdapter;
    private Worker currentWorker;

    public static EditBirthDateDialog newInstance(ChangeDetailsListener listener) {
        EditBirthDateDialog dialog = new EditBirthDateDialog();
        dialog.setListener(listener);
        return dialog;
    }

    private void setListener(ChangeDetailsListener listener) {
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
        View view = inflater.inflate(R.layout.dialog_edit_worker_dob, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Create an ArrayAdapter using the string array and a default spinner
        dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_day,
                android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        monthAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_month,
                android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        yearAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_year,
                android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
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
                                populateDateOfBirth();
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
        request.put("date_of_birth", getDateOfBirth());

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

    private String getDateOfBirth() {
        String birthDate;
        int monthValue = spinnerMonth.getSelectedItemPosition();
        if (spinnerDay.getSelectedItemPosition() > 0 & spinnerMonth.getSelectedItemPosition() > 0 & spinnerYear.getSelectedItemPosition() > 0) {
            birthDate = spinnerYear.getSelectedItem().toString() + "-" +
                    ((monthValue > 9) ? String.valueOf(monthValue) : "0" +
                            String.valueOf(monthValue)) + "-" +
                    spinnerDay.getSelectedItem().toString();
        } else {
            birthDate = null;
        }
        return birthDate;
    }

    private void populateDateOfBirth() {
        List<String> days = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_day));
        List<String> months = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_month));
        List<String> years = Arrays.asList(getContext().getResources().getStringArray(R.array.spinner_year));

        if (currentWorker != null && !TextUtils.isEmpty(currentWorker.dateOfBirth)) {
            LocalDate dateOfBirth = DateUtils.getParsedLocalDate(currentWorker.dateOfBirth);

            for (String day : days) {
                if (dateOfBirth != null && TextUtils.equals(day.startsWith("0") ? day.substring(1) : day,
                        String.valueOf(dateOfBirth.getDayOfMonth()))) {
                    spinnerDay.setSelection(days.indexOf(day));
                }
            }

            for (String month : months) {
                if (dateOfBirth != null && TextUtils.equals(month, dateOfBirth.toString("MMMM"))) {
                    spinnerMonth.setSelection(months.indexOf(month));
                }
            }

            for (String year : years) {
                if (dateOfBirth != null && TextUtils.equals(year, String.valueOf(dateOfBirth.getYear()))) {
                    spinnerYear.setSelection(years.indexOf(year));
                }
            }
        }
    }
}
