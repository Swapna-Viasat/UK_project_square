package construction.thesquare.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.Subscription;
import construction.thesquare.shared.data.model.response.PricePlanResponse;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricePlanFragment extends Fragment {

    public static final String TAG = "PricePlanFragment";

    @BindView(R.id.due_date) TextView dueDate;
    @BindView(R.id.plan) TextView plan;
    @BindView(R.id.change_plan) TextView changePlan;

    @BindView(R.id.topup_digits) TextView topupDigits;
    @BindView(R.id.plan_digits) TextView planDigits;

    @BindView(R.id.plan_expiration) TextView planExpiration;
    @BindView(R.id.topup_expiration) TextView topupExpiration;

    private String stripeToken;
    private int currentPlan;
    private List<Subscription> subscriptions = new ArrayList<>();

    public PricePlanFragment() {
        // Required empty public constructor
    }

    public static PricePlanFragment newInstance() {
        PricePlanFragment fragment = new PricePlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //
        }

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_PRICE_PLAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_plan, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("My Price Plan");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //
        fetchEmployer();



        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchPaymentPlans()
                .enqueue(new Callback<PricePlanResponse>() {
                    @Override
                    public void onResponse(Call<PricePlanResponse> call,
                                           Response<PricePlanResponse> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            try {
                                subscriptions = response.body().response;
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<PricePlanResponse> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchEmployer() {
        //
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            if (null != response.body()) {
                                if (null != response.body().getResponse()) {
                                    populate(response.body().getResponse());
                                    if (null != response.body().getResponse().stripeToken) {
                                        stripeToken = response.body().getResponse().stripeToken;
                                    }
                                    currentPlan = response.body().getResponse().subscriptionId;
                                }
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

    private void displayCurrentPlan(String planName) {
        try {
            if (!planName.equalsIgnoreCase("NONE")) {
                changePlan.setText("Change my plan");
            }
            plan.setText(planName);
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void displayBillDueDate(String date) {
        try {
            dueDate.setText(String.format(getString(R.string.payments_bill_due_format), DateUtils.magicDate(date)));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void populate(Employer employer) {
        //
        if (null != employer) {
            displayBookings(employer.maxForPlan, employer.bookedWithPlan,
                    employer.maxForTopups, employer.bookedWithTopups);
            if (null != employer.planExpiration) {
                displayBillDueDate(employer.planExpiration);
                displayPlanExpiration(employer.planExpiration);
            }
            if (null != employer.topupExpiration) {
                displayTopupExpiration(employer.topupExpiration);
            }
            if (null != employer.planName) {
                displayCurrentPlan(employer.planName);
            }
        }
    }

    private void displayPlanExpiration(String planExpiry) {
        try {
            planExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), DateUtils.magicDate(planExpiry)));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void displayTopupExpiration(String topupExpiry) {
        try {
            topupExpiration.setText(String
                    .format(getString(R.string.payments_expiration_format), DateUtils.magicDate(topupExpiry)));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void displayBookings(int planMax, int planUsed, int topMax, int topUsed) {
        try {
            planDigits.setText(String.format(getString(R.string.payments_plans_display_bookings),
                    planUsed, planMax));
            topupDigits.setText(String.format(getString(R.string.payments_topups_display_bookings),
                    topUsed, topMax));
        } catch (IllegalStateException e) {
            TextTools.log(TAG, "illegal state exception");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
    
    @OnClick(R.id.change_plan)
    public void changePlan() {
        //Toast.makeText(getContext(), "Change", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content,
                        SubscriptionFragment.newInstance(0 != currentPlan,
                                subscriptions.size() > 0))
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.cancel)
    public void cancelPlan() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_cancel_plan);
        dialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                proceedWithCancelling();
            }
        });
        dialog.show();
    }

    private void proceedWithCancelling() {
        Toast.makeText(getContext(), "Cancelling...", Toast.LENGTH_LONG).show();
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .cancelAll()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            //
                            fetchEmployer();
                            final Dialog dialog1 = new Dialog(getContext());
                            dialog1.setCancelable(false);
                            dialog1.setContentView(R.layout.dialog_cancelled);
                            dialog1.findViewById(R.id.yes)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog1.dismiss();
                                        }
                                    });
                            dialog1.show();
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

    @OnClick(R.id.change_card)
    public void changeCard() {
        //Toast.makeText(getContext(), "Change card", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content, PaymentFragment.newInstance(1))
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.top_up)
    public void topUp() {
        //Toast.makeText(getContext(), "Top Up", Toast.LENGTH_LONG).show();
        if (stripeToken == null) {
            //
            final Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_topup_error);
            dialog.findViewById(R.id.yes)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            //
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_employer_content,
                            TopUpFragment.newInstance(currentPlan))
                    .addToBackStack("")
                    .commit();
        }
    }

    @OnClick(R.id.alternative_payment)
    public void alternative() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content,
                        AlternativePayFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @OnClick(R.id.understand)
    public void understand() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content,
                        UnderstandingPlanFragment.newInstance())
                .addToBackStack("")
                .commit();
    }
}