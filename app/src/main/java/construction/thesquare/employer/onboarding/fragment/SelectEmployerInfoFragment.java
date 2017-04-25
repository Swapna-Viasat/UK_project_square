package construction.thesquare.employer.onboarding.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/29/2016.
 */

public class SelectEmployerInfoFragment extends Fragment {

    public static final String TAG = "EmployerInfoFragment";

    private Employer currentEmployer;

    @BindView(R.id.first_name_input)
    TextInputLayout firstNameInput;
    @BindView(R.id.last_name_input)
    TextInputLayout lastNameInput;
    @BindView(R.id.title_input)
    TextInputLayout titleInput;
    @BindView(R.id.password_input)
    TextInputLayout passwordInput;
    @BindView(R.id.password2_input)
    TextInputLayout password2Input;
    @BindView(R.id.company_number_input)
    TextInputLayout companyNumberInput;

    public static SelectEmployerInfoFragment newInstance(Employer employer) {
        SelectEmployerInfoFragment fragment = new SelectEmployerInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_CURRENT_EMPLOYER, employer);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_select_info_employer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (null != getArguments().getSerializable(Constants.KEY_CURRENT_EMPLOYER)) {
            currentEmployer = (Employer) getArguments()
                    .getSerializable(Constants.KEY_CURRENT_EMPLOYER);
            populate(currentEmployer);
        }
    }

    private void populate(Employer employer) {
        try {

            firstNameInput.getEditText().setText(employer.firstName);
            lastNameInput.getEditText().setText(employer.lastName);
            titleInput.getEditText().setText(employer.jobTitle);
//            companyNumberInput.getEditText().setText(employer.compan);
            passwordInput.getEditText().setHint("Create password*");
            password2Input.getEditText().setHint("Re-enter password*");

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void patchEmployer() {

        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> request = new HashMap();
        request.put("password", passwordInput.getEditText().getText().toString());
        request.put("job_title", titleInput.getEditText().getText().toString());
        request.put("crn", companyNumberInput.getEditText().getText().toString());
        request.put("first_name", firstNameInput.getEditText().getText().toString());
        request.put("last_name", lastNameInput.getEditText().getText().toString());
        request.put("password2", password2Input.getEditText().getText().toString());
        HttpRestServiceConsumer.getBaseApiClient()
                .patchEmployer(currentEmployer.id, request)
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {

                            proceed();

                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.next)
    public void next() {
        if (validate()) {
            patchEmployer();
        }
    }

    private void proceed() {
        Analytics.recordEvent(getActivity(),
                ConstantsAnalytics.EVENT_CATEGORY_ONBOARDING,
                ConstantsAnalytics.EVENT_EMPLOYER_ONBOARDED);

        startActivity(new Intent(getActivity(), MainEmployerActivity.class));
    }

    private boolean validate() {
        boolean result = true;
        if (TextUtils.isEmpty(firstNameInput.getEditText().getText().toString())) {
            firstNameInput.setError(getString(R.string.validate_first));
            result = false;
        } else if (TextUtils.isEmpty(lastNameInput.getEditText().getText().toString())) {
            lastNameInput.setError(getString(R.string.validate_last));
            result = false;
        } else if (
                (TextUtils.isEmpty(passwordInput.getEditText().getText().toString()))) {
            passwordInput.setError(getString(R.string.validate_password));
            result = false;
        } else if (
                (TextUtils.isEmpty(password2Input.getEditText().getText().toString()))) {
            password2Input.setError(getString(R.string.validate_password_reenter));
            result = false;
        } else if (
                (!(passwordInput.getEditText().getText().toString()
                        .equals(password2Input.getEditText().getText().toString())))) {
            password2Input.setError(getString(R.string.validate_password_match));
            result = false;
        }

        if (!result) resetInputErrors.start();

        return result;
    }

    private CountDownTimer resetInputErrors = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long l) {
            //
        }

        @Override
        public void onFinish() {
            try {
                TextTools.resetInputLayout(password2Input);
                TextTools.resetInputLayout(passwordInput);
                TextTools.resetInputLayout(lastNameInput);
                TextTools.resetInputLayout(firstNameInput);
                TextTools.resetInputLayout(titleInput);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    };
}