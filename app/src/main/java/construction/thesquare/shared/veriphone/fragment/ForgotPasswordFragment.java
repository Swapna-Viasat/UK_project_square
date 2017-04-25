package construction.thesquare.shared.veriphone.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.models.StatusMessageResponse;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class ForgotPasswordFragment extends Fragment {

    @BindView(R.id.emailEditText)
    EditText emailInput;

    public ForgotPasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.loginButton)
    void forgotEmail() {
        if (validateInput()) {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("email", emailInput.getText().toString());
            callApi(payload);
        }
    }

    private boolean validateInput() {
        boolean result = true;
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.empty_email));
            result = false;
        } else if (!TextTools.isEmailValid(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.validate_email));
            result = false;
        }
        return result;
    }

    private void callApi(HashMap<String, String> body) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .forgotPassword(body)
                .enqueue(new Callback<StatusMessageResponse>() {
                    @Override
                    public void onResponse(Call<StatusMessageResponse> call,
                                           Response<StatusMessageResponse> response) {
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            DialogBuilder.showStandardDialog(getContext(), "", response.body().response.message,
                                    new DialogBuilder.OnClickStandardDialog() {
                                        @Override
                                        public void onOKClickStandardDialog(Context context) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }
                                    });
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusMessageResponse> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }
}
