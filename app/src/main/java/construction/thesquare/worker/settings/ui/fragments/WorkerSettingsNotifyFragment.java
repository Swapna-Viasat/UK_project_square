package construction.thesquare.worker.settings.ui.fragments;

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

public class WorkerSettingsNotifyFragment extends Fragment {

    @BindView(R.id.switchesList)
    LinearLayout switchesList;

    private List<NotificationPreference> notificationPreferences;
    private List<SingleNotificationPreference> workerNotificationPreferences;

    public static WorkerSettingsNotifyFragment newInstance() {
        return new WorkerSettingsNotifyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_settings_notify, container, false);
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
                            fetchWorkerNotificationsPreferences();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<NotificationPreference>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchWorkerNotificationsPreferences() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchWorkerNotificationPreferences()
                .enqueue(new Callback<ResponseObject<List<SingleNotificationPreference>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<SingleNotificationPreference>>> call,
                                           Response<ResponseObject<List<SingleNotificationPreference>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            workerNotificationPreferences = response.body().getResponse();
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
                && !CollectionUtils.isEmpty(workerNotificationPreferences)) {

            for (final SingleNotificationPreference workerPreference : workerNotificationPreferences) {
                View switchView = LayoutInflater.from(getContext()).inflate(R.layout.view_notification_switch, null, false);
                TextView textView = (TextView) switchView.findViewById(R.id.switchText);
                final Switch notificationSwitch = (Switch) switchView.findViewById(R.id.notificationSwitch);
                notificationSwitch.setChecked(workerPreference.enabled == 1);

                for (NotificationPreference p : notificationPreferences)
                    if (p.id == workerPreference.id) textView.setText(p.name);

                notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        workerPreference.enabled = notificationSwitch.isChecked() ? 1 : 0;
                        togglePreference(workerPreference);
                    }
                });
                switchesList.addView(switchView);
            }
        }
    }

    private void togglePreference(SingleNotificationPreference preference) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .toggleWorkerNotification(preference)
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
