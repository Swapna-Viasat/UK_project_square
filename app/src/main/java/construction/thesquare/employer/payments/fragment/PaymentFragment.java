package construction.thesquare.employer.payments.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.subscription.model.CreateCardRequest;
import construction.thesquare.employer.subscription.model.CreateCardResponse;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.response.PricePlanResponse;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    public static final String TAG = "PaymentFragment";

    @BindView(R.id.first) JosefinSansEditText first;
    @BindView(R.id.last) JosefinSansEditText last;
    @BindView(R.id.number) JosefinSansEditText number;
    @BindView(R.id.month) JosefinSansEditText month;
    @BindView(R.id.year) JosefinSansEditText year;
    @BindView(R.id.cvc) JosefinSansEditText cvc;
    // address fields
    @BindView(R.id.voucher) JosefinSansEditText voucher;
    @BindView(R.id.address1) JosefinSansEditText address;
    @BindView(R.id.city) JosefinSansEditText city;
    @BindView(R.id.country) JosefinSansEditText country;
    @BindView(R.id.postcode) JosefinSansEditText postcode;
    private int plan;
    private boolean draftJobInLimbo;
    private int draftJobInLimboId;

    public static PaymentFragment newInstance(int selectedPlan) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_SELECTED_PLAN, selectedPlan);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        if (getArguments() != null) {
            //
        }

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_PAYMENT_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setUpCreditCardNumberInput(JosefinSansEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //
        setUpCreditCardNumberInput(number);
        plan = getArguments().getInt(Constants.KEY_SELECTED_PLAN);

        draftJobInLimbo = getActivity()
                .getSharedPreferences(Constants.CREATE_JOB_FLOW, Context.MODE_PRIVATE)
                .getBoolean(Constants.DRAFT_JOB_AWAIT_PLAN, false);
        draftJobInLimboId = getActivity()
                .getSharedPreferences(Constants.CREATE_JOB_FLOW, Context.MODE_PRIVATE)
                .getInt(Constants.DRAFT_JOB_ID, 0);

        TextTools.log(TAG, String.valueOf(plan));
    }

    private void setUpPlan(int planId, String cardToken) {
        final Dialog resultDialog = new Dialog(getContext());
        resultDialog.setCancelable(false);
        resultDialog.setContentView(R.layout.dialog_payment_success);
        resultDialog.findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultDialog.dismiss();
                        exit();
                    }
                });
        if (draftJobInLimbo) {
            //
            if (voucher.getText().toString().trim().equals("")) {
                //
                ((TextView) resultDialog.findViewById(R.id.dialog_payment_success))
                        .setText(getString(R.string.payments_payment_success_job));
            } else {
                //
                ((TextView) resultDialog.findViewById(R.id.dialog_payment_success))
                        .setText(getString(R.string.payments_payment_success_voucher_job));
            }
        } else {
            //
            if (voucher.getText().toString().trim().equals("")) {
                //
                ((TextView) resultDialog.findViewById(R.id.dialog_payment_success))
                        .setText(getString(R.string.payments_payment_success));
            } else {
                //
                ((TextView) resultDialog.findViewById(R.id.dialog_payment_success))
                        .setText(getString(R.string.payments_payment_success_voucher));
            }
        }

        //
        HashMap<String, Object> body = new HashMap<>();
        // body.put("stripe_id", "pk_test_iUGx8ZpCWm6GeSwBpfkdqjSQ");
        body.put("stripe_source_token", cardToken);
        body.put("payment_detail", planId);
        if (!voucher.getText().toString().trim().equals("")) {
            body.put("voucher_code", voucher.getText().toString());
        }
        if (draftJobInLimbo) {
            body.put("job_id", draftJobInLimboId);
        }
        HttpRestServiceConsumer.getBaseApiClient()
                .setupPayment(body)
                .enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call,
                                           Response<ResponseObject> response) {
                        //
                        // TODO:
                        if (response.isSuccessful()) {
                            if (draftJobInLimbo) {
                                Analytics.recordEvent(getActivity(),
                                        ConstantsAnalytics.EVENT_CATEGORY_BILLING,
                                        ConstantsAnalytics.EVENT_EMPLOYER_SUBSCRIBED);
                                //
                                resultDialog.show();
                                //Toast.makeText(getContext(), "Success!", Toast.LENGTH_LONG).show();
                                //exit();
                                //
                            } else {
                                //
                                resultDialog.show();
                                //Toast.makeText(getContext(), "Success!", Toast.LENGTH_LONG).show();
                                //exit();
                            }
                        } else {
                            Toast.makeText(getContext(), "Something's not right.", Toast.LENGTH_LONG).show();
                            exit();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        //
                        Toast.makeText(getContext(), "Something's not right.", Toast.LENGTH_LONG).show();
                        exit();
                    }
                });
    }

    @OnClick(R.id.confirm)
    public void submit() {
        if (!voucher.getText().toString().trim().equals("")) {
            if (voucher.getText().toString().equalsIgnoreCase("SQUAREVIP")) {
                getCard();
            } else {
                final Dialog resultDialog = new Dialog(getContext());
                resultDialog.setCancelable(false);
                resultDialog.setContentView(R.layout.dialog_payment_success);
                resultDialog.findViewById(R.id.yes)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resultDialog.dismiss();
                            }
                        });
                ((TextView) resultDialog.findViewById(R.id.dialog_payment_success))
                        .setText(getString(R.string.payments_voucher_wrong));
                resultDialog.show();
            }
        } else {
            getCard();
        }
    }

    private Card getCard() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        int monthInt = 0;
        int yearInt = 0;

        try {
            //
            monthInt = Integer.valueOf(month.getText().toString());
            yearInt = Integer.valueOf(year.getText().toString());
            //
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        Card card = new Card(number.getText().toString(),
                                monthInt,
                                yearInt,
                                cvc.getText().toString(),
                                first.getText().toString() + " " + last.getText().toString(),
                                address.getText().toString(), null,
                                city.getText().toString(), null,
                                postcode.getText().toString(),
                                country.getText().toString(), null);

        if (card.validateNumber()) {
            try {
                final Stripe stripe = new Stripe("pk_live_bR97E0AcQgxBJtxbIS7X2oth");
                stripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        DialogBuilder.cancelDialog(dialog);
                        new AlertDialog.Builder(getContext())
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                        CrashLogHelper.logException(error);
                    }

                    @Override
                    public void onSuccess(Token token) {
                        DialogBuilder.cancelDialog(dialog);
                        TextTools.log(TAG, token.getId());
                        // proceed
                        setUpPlan(plan, token.getId());
                        //
                    }
                });
            } catch (Exception e) {
                DialogBuilder.cancelDialog(dialog);
                CrashLogHelper.logException(e);
            }
        } else {
            TextTools.log(TAG, "couldn't validate");
            DialogBuilder.cancelDialog(dialog);
            new AlertDialog.Builder(getContext())
                    .setMessage("Something went wrong.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }
        return card;
    }

    private void exit() {
        getActivity()
                .getSharedPreferences(Constants.CREATE_JOB_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.DRAFT_JOB_AWAIT_PLAN, false)
                .remove(Constants.DRAFT_JOB_ID)
                .commit();
        getActivity().finish();
        getActivity().startActivity(new Intent(getActivity(), MainEmployerActivity.class));
    }
}