package construction.thesquare.employer.createjob.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 1/24/17.
 */

public class CRNDialog extends DialogFragment {

    public static final String TAG = "CRNDialog";

    @BindView(R.id.dialog_crn_input) EditText in;
    private CRNListener listener;

    public interface CRNListener {
        void onResult(boolean success);
    }

    public static CRNDialog newInstance(CRNListener crnListener) {
        CRNDialog dialog = new CRNDialog();
        dialog.setCancelable(false);
        //dialog.setStyle();
        dialog.listener = crnListener;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_crn, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        // have to make this call otherwise shows blank white space in the title area
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //
    }


    public void onDone() {
        if (validate()) {
            HashMap<String, Object> request = new HashMap<>();
            request.put("crn", in.getText().toString());
            final Dialog dialog = DialogBuilder.showCustomDialog(getActivity());
            HttpRestServiceConsumer.getBaseApiClient()
                    .patchEmployer(SharedPreferencesManager
                            .getInstance(getContext())
                            .loadSessionInfoEmployer()
                            .getUserId(), request)
                    .enqueue(new Callback<ResponseObject<Employer>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Employer>> call,
                                               Response<ResponseObject<Employer>> response) {
                            //
                            if (response.isSuccessful()) {
                                DialogBuilder.cancelDialog(dialog);
                                TextTools.log(TAG, "successful crn call");
                                listener.onResult(true);
                            } else {
                                DialogBuilder.cancelDialog(dialog);
                                TextTools.log(TAG, "unsuccessful crn call");
                                listener.onResult(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            listener.onResult(false);
                        }
                    });
            dismiss();
        }
    }

    @OnClick({R.id.dialog_crn_done, R.id.close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.dialog_crn_done:
                onDone();
                break;
        }
    }
    private boolean validate() {
        boolean result = true;
        if (in.getText().toString().equals("")) {
            result = false;
        }
        return result;
    }
}