package construction.thesquare.employer.settings.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.NotificationPreference;
import construction.thesquare.shared.models.SingleNotificationPreference;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerSettingsNotifyFragment extends Fragment {

    @BindView(R.id.switchesList)
    LinearLayout switchesList;

    private List<NotificationPreference> notificationPreferences;
    private List<SingleNotificationPreference> employerNotificationPreferences;

    public static EmployerSettingsNotifyFragment newInstance() {
        return new EmployerSettingsNotifyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_settings_notify, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void fetchNotificationsPreferences() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchNotificationPreferences()
                .enqueue(new Callback<ResponseObject<List<NotificationPreference>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<NotificationPreference>>> call,
                                           Response<ResponseObject<List<NotificationPreference>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            notificationPreferences = response.body().getResponse();
                            fetchEmployerNotificationPreferences();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<NotificationPreference>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchEmployerNotificationPreferences() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchEmployerNotificationPreferences()
                .enqueue(new Callback<ResponseObject<List<SingleNotificationPreference>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<SingleNotificationPreference>>> call,
                                           Response<ResponseObject<List<SingleNotificationPreference>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            employerNotificationPreferences = response.body().getResponse();
                            proceed();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<SingleNotificationPreference>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void proceed() {
        switchesList.removeAllViews();
        if (!CollectionUtils.isEmpty(notificationPreferences)
                && !CollectionUtils.isEmpty(employerNotificationPreferences)) {

            for (final SingleNotificationPreference employerPreference : employerNotificationPreferences) {
                View switchView = LayoutInflater.from(getContext()).inflate(R.layout.view_notification_switch, null, false);
                TextView textView = (TextView) switchView.findViewById(R.id.switchText);
                final Switch notificationSwitch = (Switch) switchView.findViewById(R.id.notificationSwitch);
                notificationSwitch.setChecked(employerPreference.enabled == 1);

                for (NotificationPreference p : notificationPreferences)
                    if (p.id == employerPreference.id) textView.setText(p.name);

                notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        employerPreference.enabled = notificationSwitch.isChecked() ? 1 : 0;
                        togglePreference(employerPreference);
                    }
                });
                switchesList.addView(switchView);
            }
        }
    }

    private void togglePreference(SingleNotificationPreference preference) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .toggleEmployerNotification(preference)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {

                        DialogBuilder.cancelDialog(dialog);
                        fetchNotificationsPreferences();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.settings_notifications));
        fetchNotificationsPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }
}
