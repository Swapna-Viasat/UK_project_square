package construction.thesquare.shared.veriphone.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.signup.model.Employer;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.start.activity.TermsActivity;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.worker.signup.model.Worker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/27/2016.
 */

public class VerifyPhoneFragment extends Fragment {

    public static final String TAG = "VerifyPhoneFragment";

    @BindView(R.id.tvAskForPhoneFirstTitle)
    TextView tvAskForPhoneFirstTitle;
    @BindView(R.id.tvAskForPhoneSecondTitle)
    TextView tvAskForPhoneSecondTitle;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.askForPhonePhoneNumberEditText)
    EditText editTextPhoneNumber;
    @BindView(R.id.askForPhonePhoneNumberEditTextWrapper)
    TextInputLayout phoneLayout;
    @BindView(R.id.askForEmailEditTextWrapper)
    TextInputLayout emailLayout;

    private int action;

    public static VerifyPhoneFragment newInstance(int key) {
        VerifyPhoneFragment fragment = new VerifyPhoneFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_VERIFY_PHONE, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_phone, container, false);
        ButterKnife.bind(this, view);

        action = getActivity().getIntent().getIntExtra(Constants.KEY_VERIFY_PHONE, 1);
        editTextPhoneNumber.setOnEditorActionListener(imeActionListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.terms)
    public void openTerms() {
        startActivity(new Intent(getActivity(), TermsActivity.class));
     /*   getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, SettingsTermsConditionsFragment.newInstance())
                .addToBackStack("contact")
                .commit();*/
    }

    @OnClick(R.id.verify)
    public void validatePhone(View view) {
        if (validateFields()) {
            callApi();
        }
    }

    private void callApi() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, String> registrationRequest = new HashMap<>();
        registrationRequest.put("phone_number", editTextPhoneNumber.getText().toString());
        registrationRequest.put("country_code", ccp.getSelectedCountryCodeWithPlus());

        if (action == Constants.KEY_VERIFY_PHONE_WORKER) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .registrationWorker(registrationRequest)
                    .enqueue(new Callback<ResponseObject<Worker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Worker>> call,
                                               Response<ResponseObject<Worker>> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {

                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("phone", editTextPhoneNumber.getText().toString());
                                    bundle.putString("country", ccp.getSelectedCountryCodeWithPlus());
                                    bundle.putString("email", emailLayout.getEditText().getText().toString());
                                    bundle.putInt(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_WORKER);


                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.phone_verify_content, EnterCodeFragment
                                                    .newInstance(bundle))
                                            .addToBackStack("")
                                            .commit();
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
        } else if (action == Constants.KEY_VERIFY_PHONE_EMPLOYER) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .registrationEmployer(registrationRequest)
                    .enqueue(new Callback<ResponseObject<Employer>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Employer>> call,
                                               Response<ResponseObject<Employer>> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {

                                try {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("phone", editTextPhoneNumber.getText().toString());
                                    bundle.putString("country", ccp.getSelectedCountryCodeWithPlus());
                                    bundle.putString("email", emailLayout.getEditText().getText().toString());
                                    bundle.putInt(Constants.KEY_VERIFY_PHONE, Constants.KEY_VERIFY_PHONE_EMPLOYER);

                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.phone_verify_content, EnterCodeFragment
                                                    .newInstance(bundle))
                                            .addToBackStack("")
                                            .commit();
                                } catch (Exception e) {
                                    CrashLogHelper.logException(e);
                                }

                                //
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
    }

    private boolean validateFields() {
        boolean result = true;
        if (TextUtils.isEmpty(emailLayout.getEditText().getText().toString())) {
            result = false;
            emailLayout.setError(getString(R.string.empty_email));
        } else if (!TextTools.isEmailValid(emailLayout.getEditText().getText().toString())) {
            result = false;
            emailLayout.setError(getString(R.string.validate_email));
        } else if ((TextUtils.isEmpty(phoneLayout.getEditText().getText().toString()))) {
            result = false;
            phoneLayout.setError("Please enter phone number");
        }
        return result;
    }

    private TextView.OnEditorActionListener imeActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validatePhone(v);
                handled = true;
            }
            return handled;
        }
    };
}