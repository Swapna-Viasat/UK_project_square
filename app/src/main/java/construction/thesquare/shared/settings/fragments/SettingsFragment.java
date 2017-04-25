package construction.thesquare.shared.settings.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.settings.dialog.UpdateEmailDialog;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.Logout;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.main.activity.MainActivity;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.ShareUtils;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment
        implements UpdateEmailDialog.UpdateEmailListener {

    @BindView(R.id.phoneValue)
    JosefinSansTextView phoneValueTextView;
    @BindView(R.id.emailValue)
    JosefinSansTextView emailValueTextView;
    private UpdateEmailDialog updateEmailDialog;
    private Employer currentEmployer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.settings);
        fetchMe();
    }

    @Override
    public void onEmailUpdate(String email) {
        updateEmailDialog.dismiss();
        emailValueTextView.setText(email);
    }

    @OnClick({R.id.phone, R.id.email, R.id.terms,
            R.id.about, R.id.share, R.id.logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone:
                break;
            case R.id.email:
                //
                updateEmailDialog = UpdateEmailDialog.newInstance(
                        emailValueTextView.getText().toString(),
                        this
                );
                updateEmailDialog.setCancelable(false);
                updateEmailDialog.show(getChildFragmentManager(), "updateEmail");
                break;
            case R.id.terms:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsDocsFragment.newInstance())
                        .addToBackStack("docs")
                        .commit();
                break;
            case R.id.about:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, SettingsAboutFragment.newInstance())
                        .addToBackStack("about")
                        .commit();
                break;
            case R.id.share:
                ShareUtils.employerLink(getContext());
                break;
            case R.id.logout:
                DialogBuilder.afterShowSetProperties(new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.employer_settings_logout_prompt))
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }), getContext());
                break;
        }
    }

    private void logout() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .logoutEmployer()
                .enqueue(new Callback<ResponseObject<Logout>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Logout>> call,
                                           Response<ResponseObject<Logout>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            onLogoutSuccess();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Logout>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    public void onLogoutSuccess() {
        SharedPreferencesManager.getInstance(getContext()).deleteToken();
        SharedPreferencesManager.getInstance(getContext()).deleteSessionInfoEmployer();
        SharedPreferencesManager.getInstance(getContext()).deleteIsInComingSoon();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void populateData() {
        populatePhoneNumber();

        if (currentEmployer != null && currentEmployer.email != null)
            emailValueTextView.setText(currentEmployer.email);
    }

    private void fetchMe() {
        try {
            List<String> requiredFields = Arrays.asList("email");

            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .getFilteredEmployer(SharedPreferencesManager.getInstance(getContext()).getEmployerId(), requiredFields)
                    .enqueue(new Callback<ResponseObject<Employer>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Employer>> call,
                                               Response<ResponseObject<Employer>> response) {

                            DialogBuilder.cancelDialog(dialog);

                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().getResponse() != null) {
                                    currentEmployer = response.body().getResponse();
                                    populateData();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void populatePhoneNumber() {
        String phone;
        String countryCode;

        phone = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getPhoneNumber();
        countryCode = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getCountryCode();

        phoneValueTextView.setText(countryCode + phone);
    }
}
