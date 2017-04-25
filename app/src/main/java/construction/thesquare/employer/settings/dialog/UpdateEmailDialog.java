package construction.thesquare.employer.settings.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.AccountType;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 3/9/17.
 */

public class UpdateEmailDialog extends DialogFragment {

    public static final String TAG = "UpdateEmailDialog";

    @BindView(R.id.title) TextView title;
    @BindView(R.id.update_email_input) EditText input;
    private UpdateEmailListener listener;
    private String initialText;

    public static UpdateEmailDialog newInstance(String initial,
                                                UpdateEmailListener updateEmailListener) {
        //
        UpdateEmailDialog dialog = new UpdateEmailDialog();
        dialog.initialText = initial;
        dialog.listener = updateEmailListener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_email, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        title.setText("Update Email");
        if (null != initialText) {
            input.setText(initialText);
            input.setSelection(0, initialText.length());
        }
    }

    public interface UpdateEmailListener {
        void onEmailUpdate(String email);
    }

    @OnClick(R.id.done)
    public void done() {
        //
//        if (null != listener) {
//            listener.onEmailUpdate("");
//        }
        if (getAccountType() != null) {
            if (getAccountType() == AccountType.employer) {
                updateEmail(input.getText().toString());
            }
        }
    }

    private void updateEmail(final String newEmail) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        int id = SharedPreferencesManager.getInstance(getContext()).getEmployerId();
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", newEmail);
        HttpRestServiceConsumer.getBaseApiClient()
                .patchEmployer(id, body)
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            if (null != listener) {
                                listener.onEmailUpdate(newEmail);
                            }
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

    @OnClick(R.id.cancel)
    public void cancel() {
        //
        if (null != listener) {
            listener.onEmailUpdate(initialText);
        }
    }

    @Nullable
    private AccountType getAccountType() {
        if (SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoEmployer().getUserId() > 0) {
            return AccountType.employer;
        } else if (SharedPreferencesManager.getInstance(getActivity()).loadSessionInfoWorker().getUserId() > 0) {
            return AccountType.worker;
        }
        return null;
    }
}
