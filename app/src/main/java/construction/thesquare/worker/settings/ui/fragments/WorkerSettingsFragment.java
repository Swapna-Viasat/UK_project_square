package construction.thesquare.worker.settings.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.ZipCodeVerifier;
import construction.thesquare.shared.data.model.Logout;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.ZipResponse;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.main.activity.MainActivity;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.settings.fragments.SettingsAboutFragment;
import construction.thesquare.shared.settings.fragments.SettingsDocsFragment;
import construction.thesquare.shared.settings.fragments.SettingsSocialFragment;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.ShareUtils;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.myaccount.ui.dialog.EditAccountDetailsDialog;
import construction.thesquare.worker.settings.ui.dialog.EditPasswordDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerSettingsFragment extends Fragment {

    @BindView(R.id.phoneValue)
    JosefinSansTextView phoneValueTextView;

    @BindView(R.id.emailValue)
    JosefinSansTextView emailTextView;

    @BindView(R.id.zipValue)
    JosefinSansTextView zipTextView;

    private Worker currentWorker;
    private String address;

    public static WorkerSettingsFragment newInstance() {
        return new WorkerSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.settings);
        fetchMe();
    }


    @OnClick({R.id.phone, R.id.terms, R.id.about, R.id.share, R.id.logout, R.id.notify,
            R.id.editEmail, R.id.editZip, R.id.pass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone:
                break;
           /* case R.id.social:
                openSocialSettings();
                break;*/
            case R.id.terms:
                openTerms();
                break;
            case R.id.about:
                openAbout();
                break;
            case R.id.share:
                openShareDialog();
                break;
            case R.id.logout:
                openLogoutDialog();
                break;
            case R.id.notify:
                openNotificationsSettings();
                break;
            case R.id.editEmail:
                editEmail();
                break;
            case R.id.editZip:
                editPostCode();
                break;
            case R.id.pass:
                editPassword();
                break;
           /* case R.id.editNameIcon:
                editName();*/
        }
    }

    private void openSocialSettings() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, SettingsSocialFragment.newInstance())
                .addToBackStack("social")
                .commit();
    }

    private void openTerms() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, SettingsDocsFragment.newInstance())
                .addToBackStack("docs")
                .commit();
    }

    private void openAbout() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, SettingsAboutFragment.newInstance())
                .addToBackStack("about")
                .commit();
    }

    private void openShareDialog() {
        ShareUtils.workerLink(getContext());
    }

    private void openLogoutDialog() {
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
    }

    private void openNotificationsSettings() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, WorkerSettingsNotifyFragment.newInstance())
                .addToBackStack("notifications")
                .commit();
    }

    private void editEmail() {
        EditAccountDetailsDialog.newInstance("Email", emailTextView.getText().toString(), false,
                new EditAccountDetailsDialog.InputFinishedListener() {
                    @Override
                    public void onDone(String input, boolean onlyDigits) {
                        if (TextTools.isEmailValid(input)) {

                            HashMap<String, Object> payload = new HashMap<>();
                            payload.put("email", input);
                            patchWorker(payload);
                        } else
                            DialogBuilder.showStandardDialog(getContext(), "", getString(R.string.validate_email));
                    }
                }).show(getFragmentManager(), "");
    }

    private void editPostCode() {
        EditAccountDetailsDialog.newInstance("Post code",
                (currentWorker != null && currentWorker.zip != null) ? currentWorker.zip : "", false,
                new EditAccountDetailsDialog.InputFinishedListener() {
                    @Override
                    public void onDone(String input, boolean onlyDigits) {
                        validateZip(input);
                    }
                }).show(getFragmentManager(), "");
    }

    private void editPassword() {
        EditPasswordDialog.newInstance(new EditPasswordDialog.PasswordChangedListener() {
            @Override
            public void onPasswordChanged(String password) {
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("password", password);
                payload.put("password2", password);
                patchWorker(payload);
            }
        }).show(getFragmentManager(), "");
    }

    /*private void editName() {
        String firstName = null;
        String lastName = null;
        if (currentWorker != null) {
            firstName = currentWorker.firstName;
            lastName = currentWorker.lastName;
        }

        EditNameDialog.newInstance(firstName, lastName, new EditNameDialog.NameChangedListener() {
            @Override
            public void onNameChanged(String name, String surname) {
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("first_name", name);
                payload.put("last_name", surname);
                patchWorker(payload);
            }
        }).show(getFragmentManager(), "");
    }*/

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
        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void populateData() {
        populatePhoneNumber();

        if (currentWorker != null) {
            fillAddress();
            if (!TextUtils.isEmpty(currentWorker.email)) emailTextView.setText(currentWorker.email);

            /*StringBuilder workerName = new StringBuilder();
            if (!TextUtils.isEmpty(currentWorker.firstName))
                workerName.append(currentWorker.firstName);
            if (!TextUtils.isEmpty(currentWorker.lastName))
                workerName.append(" ").append(currentWorker.lastName);
            nameTextView.setText(workerName);*/
        }
    }

    private void fillAddress() {
        StringBuilder address = new StringBuilder();
        if (!TextUtils.isEmpty(currentWorker.address)) address.append(currentWorker.address);
        zipTextView.setText(address.toString());
    }

    private void populatePhoneNumber() {
        String phone;
        String countryCode;

        phone = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getPhoneNumber();
        countryCode = SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getCountryCode();

        phoneValueTextView.setText(countryCode + phone);
    }

    private void fetchMe() {
        try {
            List<String> requiredFields = Arrays.asList("post_code", "email", "first_name", "last_name", "address");

            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .getFilteredWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), requiredFields)
                    .enqueue(new Callback<ResponseObject<Worker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Worker>> call,
                                               Response<ResponseObject<Worker>> response) {

                            DialogBuilder.cancelDialog(dialog);

                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().getResponse() != null) {
                                    currentWorker = response.body().getResponse();
                                    populateData();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void patchWorker(HashMap<String, Object> payload) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), payload)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        //
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            currentWorker = response.body().getResponse();
                            populateData();
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

    private void validateZip(final String zipCode) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        ZipCodeVerifier.getInstance()
                .api()
                .verify(zipCode, ZipCodeVerifier.API_KEY)
                .enqueue(new Callback<ZipResponse>() {
                    @Override
                    public void onResponse(Call<ZipResponse> call, Response<ZipResponse> response) {
                        DialogBuilder.cancelDialog(dialog);

                        if (null != response.body()) {
                            if (null != response.body().message) {
                                if (response.body().message.equals(ZipCodeVerifier.BAD_REQUEST)) {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.validate_zip))
                                            .show();
                                } else {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.validate_zip))
                                            .show();
                                }
                            } else {
                                if (!CollectionUtils.isEmpty(response.body().addresses)) {
                                    showAddressDialog(zipCode, response.body().addresses);
                                }
                                // all good
                            }
                        } else {
                            // response body null
                            new android.app.AlertDialog.Builder(getContext())
                                    .setMessage(getString(R.string.validate_zip))
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ZipResponse> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void showAddressDialog(final String zipCode, final List<String> result) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.autocomplete);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels * 8 / 12);
        ((TextView) dialog.findViewById(R.id.autocomplete_title))
                .setText(getString(R.string.create_job_address));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, result);
        ListView listView = (ListView) dialog.findViewById(R.id.autocomplete_rv);
        final EditText search = (EditText) dialog.findViewById(R.id.autocomplete_search);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search.setText(result.get(position));
            }
        });

        dialog.findViewById(R.id.autocomple_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.autocomple_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // all good
                address = search.getText().toString();
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("post_code", zipCode);
                if (address != null)
                    payload.put("address", address.replace(", , , ,", ", ") + ", " + zipCode);
                patchWorker(payload);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
