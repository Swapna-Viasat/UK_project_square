package construction.thesquare.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpFragment extends Fragment {

    public static final String TAG = "TopUpFragment";
    private int currentPlan;

    public TopUpFragment() {
        // Required empty public constructor
    }

    public static TopUpFragment newInstance(int planId) {
        TopUpFragment fragment = new TopUpFragment();
        Bundle args = new Bundle();
        args.putInt("plan_id", planId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPlan = getArguments().getInt("plan_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Top Up");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @OnClick(R.id.continue_top_up)
    public void topUp() {
        verifyPassword();
    }

    private void verifyPassword() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_verify_password);
        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final EditText password = (EditText)
                dialog.findViewById(R.id.dialog_payment_password);
        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(password.getText().toString())) {
                    //
                    dialog.dismiss();
                    callApi(password.getText().toString());
                } else {
                    password.setError("Please enter your password!");
                }
            }
        });
        dialog.show();
    }

    private void callApi(String password) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, Object> body = new HashMap<>();
        body.put("password", password);
        HttpRestServiceConsumer.getBaseApiClient()
                .topup(body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            final Dialog dialog1 = new Dialog(getContext());
                            dialog1.setCancelable(false);
                            dialog1.setContentView(R.layout.dialog_topup_success);
                            dialog1.findViewById(R.id.yes)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getActivity().getSupportFragmentManager()
                                                    .popBackStack();
                                        }
                                    });

//                            Toast.makeText(getContext(), "Top Up Successful",
//                                    Toast.LENGTH_LONG).show();
                            //
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @OnClick(R.id.change_plan)
    public void changePlan() {
        getActivity().getSupportFragmentManager()
                .popBackStack();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, SubscriptionFragment
                        .newInstance(currentPlan != 0, true))
                .addToBackStack("")
                .commit();
    }
}