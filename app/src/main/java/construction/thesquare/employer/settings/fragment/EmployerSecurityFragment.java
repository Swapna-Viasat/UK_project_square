package construction.thesquare.employer.settings.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerSecurityFragment extends Fragment {

    public static final String TAG = "EmployerSecurity";
    @BindView(R.id.password_input) TextInputLayout passwordInput;
    @BindView(R.id.password2_input) TextInputLayout password2Input;

    public EmployerSecurityFragment() {
        // Required empty public constructor
    }

    public static EmployerSecurityFragment newInstance() {
        EmployerSecurityFragment fragment = new EmployerSecurityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_security, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.save)
    public void save() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        //
                        if (response.isSuccessful()) {
                            int id = response.body().getResponse().id;
                            HashMap<String, Object> body = new HashMap<>();
                            body.put("password", passwordInput.getEditText().getText().toString());
                            body.put("password2", password2Input.getEditText().getText().toString());

                            HttpRestServiceConsumer.getBaseApiClient()
                                    .patchEmployer(id, body)
                                    .enqueue(new Callback<ResponseObject<Employer>>() {
                                        @Override
                                        public void onResponse(Call<ResponseObject<Employer>> call,
                                                               Response<ResponseObject<Employer>> response) {
                                            //
                                            if (response.isSuccessful()) {
                                                DialogBuilder.cancelDialog(dialog);
                                                Toast.makeText(getContext(),
                                                        getString(R.string.employer_password_update_success),
                                                        Toast.LENGTH_LONG).show();
                                                getActivity().getSupportFragmentManager()
                                                        .popBackStack();
                                            } else {
                                                HandleErrors.parseError(getContext(), dialog, response);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                                            HandleErrors.parseFailureError(getContext(), dialog, t);
                                        }
                                    });
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
