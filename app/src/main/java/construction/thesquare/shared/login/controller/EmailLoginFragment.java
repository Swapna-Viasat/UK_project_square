package construction.thesquare.shared.login.controller;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.MainApplication;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.onboarding.OnboardingEmployerActivity;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.LoginUser;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.login.model.AccountValidator;
import construction.thesquare.shared.login.presenter.LoginPresenter;
import construction.thesquare.shared.login.view.LoginForm;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.veriphone.fragment.ForgotPasswordFragment;
import construction.thesquare.worker.main.ui.MainWorkerActivity;
import construction.thesquare.worker.onboarding.OnboardingWorkerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * refactored by Evgheni Gherghelejiu
 * on 3/29/2017
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class EmailLoginFragment extends Fragment
        implements LoginController {

    public static final String TAG = "LoginFragment";

    private static final int TYPE_EMPLOYER = 1;
    private static final int TYPE_WORKER = 2;

    @BindView(R.id.login_form) LoginForm mLoginFormView;
    private Dialog progressDialog;
    @Inject LoginPresenter loginPresenter;
    AccountValidator accountValidator;

    public EmailLoginFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountValidator = new AccountValidator();
        ((MainApplication) getActivity().getApplication()).component()
                .inject(this);
        loginPresenter.register(this);

        Analytics.recordCurrentScreen(getActivity(), ConstantsAnalytics.SCREEN_LOGIN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_email, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginFormView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    @OnClick(R.id.forgotPass)
    void goToForgotPasswordScreen() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.phone_verify_content, new ForgotPasswordFragment())
                .commit();
    }

    private void callApi(HashMap<String, Object> body) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .loginUser(body)
                .enqueue(new Callback<ResponseObject<LoginUser>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<LoginUser>> call,
                                           Response<ResponseObject<LoginUser>> response) {
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            processResponse(response);
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<LoginUser>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void processResponse(Response<ResponseObject<LoginUser>> response) {
        if (getActivity() == null || !isAdded()) return;

        if (response.body() != null && response.body().getResponse() != null
                && response.body().getResponse().user != null) {

            if (response.body().getResponse().user.userType == TYPE_EMPLOYER) {
                processEmployer(response);

            } else if (response.body().getResponse().user.userType == TYPE_WORKER) {
                processWorker(response);
            }
        } else DialogBuilder.showStandardDialog(getContext(), "",
                getString(R.string.login_error));
    }

    private void processWorker(Response<ResponseObject<LoginUser>> response) {
        if (getActivity() == null || !isAdded()) return;

        String name = response.body().getResponse().
                user.getFirst_name() + " " +
                response.body().getResponse()
                        .user.getLast_name();

        SharedPreferencesManager.getInstance(getContext())
                .persistSessionInfoWorker(response.body().getResponse().token,
                        response.body().getResponse().user,
                        response.body().getResponse().user.getCountryCode(),
                        response.body().getResponse().user.getPhoneNumber(), name);
        if (response.body().getResponse().user.isOnboarding_done()) {
            startAnotherActivity(new Intent(getContext(), MainWorkerActivity.class));
        } else {
            Intent onboardingIntent = new Intent(getActivity(), OnboardingWorkerActivity.class);
            onboardingIntent.putExtra(Constants.KEY_HAVE_ACCOUNT, true);
            startAnotherActivity(onboardingIntent);
        }
    }

    private void processEmployer(Response<ResponseObject<LoginUser>> response) {
        if (getActivity() == null || !isAdded()) return;

        String name = response.body().getResponse().
                user.getFirst_name() + " " +
                response.body().getResponse()
                        .user.getLast_name();

        SharedPreferencesManager.getInstance(getContext())
                .persistSessionInfoEmployer2(response.body().getResponse().token,
                        response.body().getResponse().user,
                        response.body().getResponse().user.getCountryCode(),
                        response.body().getResponse().user.getPhoneNumber(), name);
        if (response.body().getResponse().user.isOnboarding_done()) {
            startAnotherActivity(new Intent(getContext(), MainEmployerActivity.class));
        } else {
            startAnotherActivity(new Intent(getActivity(), OnboardingEmployerActivity.class));
        }
    }

    private void startAnotherActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = DialogBuilder.showCustomDialog(getContext());
            }
        } else {
            if (null != progressDialog) {
                DialogBuilder.cancelDialog(progressDialog);
            }
        }
    }

    @Override
    public void showRetrofitError(Throwable throwable) {
        //
        if (null != throwable) {
            TextTools.log(TAG, (null != throwable.getMessage()) ? throwable.getMessage() : "null");
        }
    }

    @Override
    public void showError(Response<ResponseObject<LoginUser>> response) {
        try {
            if (response.code() == 404) {
                HandleErrors.parseError(getContext(), null, response);
//                i'm just gonna leave this here commented out because
//                they'll prolly change their mind again
//                DialogBuilder.showStandardDialog(getContext(), "",
//                        getString(R.string.login_error));
            } else {
                HandleErrors.parseError(getContext(), null, response);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void showSuccess(Response<ResponseObject<LoginUser>> response) {
        TextTools.log(TAG, "successful login - proceeding...");
        processResponse(response);
    }

    private void attemptLogin() {
        TextTools.log(TAG, "attempting login...");
        loginPresenter.onLoginButtonClick(mLoginFormView.getEmail(),
                mLoginFormView.getPassword());
    }
}
