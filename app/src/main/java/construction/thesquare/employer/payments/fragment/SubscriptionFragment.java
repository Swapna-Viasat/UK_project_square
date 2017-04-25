package construction.thesquare.employer.payments.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.payments.adapter.PaymentsAdapter;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    @BindView(R.id.subscription_pager) ViewPager pager;
    private int selectedPlan;

    @BindViews({R.id.top_basic, R.id.top_standard, R.id.top_premium})
    List<ViewGroup> top;

    @BindView(R.id.first_time_user_text) TextView firstTime;
    @BindView(R.id.understanding) LinearLayout understanding;

    public static SubscriptionFragment newInstance(boolean hasStripeToken,
                                                   boolean hasPlan) {
        SubscriptionFragment fragment = new SubscriptionFragment();
        Bundle args = new Bundle();
        args.putBoolean("has_plan", hasPlan);
        args.putBoolean("has_token", hasStripeToken);
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
                ConstantsAnalytics.SCREEN_EMPLOYER_CHOOSE_PLAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.payments_continue)
    public void proceed() {
        if (!getArguments().getBoolean("has_token")) {
            if (!getArguments().getBoolean("has_plan")) {
                getActivity().
                        getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_employer_content,
                                PaymentFragment.newInstance(selectedPlan + 2))
                        .addToBackStack("")
                        .commit();
            } else {
                verifyPassword();
            }
        } else {
            verifyPassword();
        }
    }

    private void verifyPassword() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_verify_password_plan);
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
        body.put("payment_detail", selectedPlan + 2);
        body.put("password", password);
        HttpRestServiceConsumer.getBaseApiClient()
                .subscribe(body)
                .enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call,
                                           Response<ResponseObject> response) {
                        //
                        if (response.isSuccessful()) {
                            //
                            final Dialog dialog1 = new Dialog(getContext());
                            dialog1.setCancelable(false);
                            dialog1.setContentView(R.layout.dialog_subscription_success);
                            dialog1.findViewById(R.id.yes)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog1.dismiss();
                                            getActivity().getSupportFragmentManager()
                                                    .popBackStack();
                                        }
                                    });
                            //
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            if (!getArguments().getBoolean("has_token")) {
                if (!getArguments().getBoolean("has_plan")) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar()
                            .setTitle("Choose Plan");
                    firstTime.setVisibility(View.VISIBLE);
                    understanding.setVisibility(View.VISIBLE);
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar()
                            .setTitle("Change Plan");
                    firstTime.setVisibility(View.GONE);
                    understanding.setVisibility(View.GONE);
                }
            } else {
                ((AppCompatActivity) getActivity()).getSupportActionBar()
                        .setTitle("Change Plan");
                firstTime.setVisibility(View.GONE);
                understanding.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        select(1);
        PaymentsAdapter adapter = new PaymentsAdapter();
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                select(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
    }

    @OnClick({R.id.top_premium, R.id.top_basic, R.id.top_standard})
    public void go(View view) {
        switch (view.getId()) {
            case R.id.top_basic:
                pager.setCurrentItem(0);
                select(0);
                break;
            case R.id.top_standard:
                pager.setCurrentItem(1);
                select(1);
                break;
            case R.id.top_premium:
                pager.setCurrentItem(2);
                select(2);
                break;
        }
    }

    private void select(int id) {
        selectedPlan = id;
        for (int i = 0; i < 3; i++) {
            if (i == id) {
                top.get(i).setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.redSquareColor));
                for (int j = 0; j < top.get(i).getChildCount(); j++) {
                    if (top.get(i).getChildAt(j) instanceof TextView) {
                        ((TextView) top.get(i).getChildAt(j)).setTextColor(ContextCompat
                                    .getColor(getContext(), R.color.whiteSquareColor));
                    } else if (top.get(i).getChildAt(j) instanceof ImageView) {
                        top.get(i).getChildAt(j)
                                .setBackgroundColor(ContextCompat
                                        .getColor(getContext(), R.color.whiteSquareColor));
                    }
                }
            } else {
                top.get(i).setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.whiteSquareColor));
                for (int j = 0; j < top.get(i).getChildCount(); j++) {
                    if (top.get(i).getChildAt(j) instanceof TextView) {
                        ((TextView) top.get(i).getChildAt(j)).setTextColor(ContextCompat
                                .getColor(getContext(), R.color.redSquareColor));
                    } else if (top.get(i).getChildAt(j) instanceof ImageView) {
                        top.get(i).getChildAt(j)
                                .setBackgroundColor(ContextCompat
                                        .getColor(getContext(), R.color.redSquareColor));
                    }
                }
            }
        }
    }

    @OnClick(R.id.understanding)
    public void understanding() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_employer_content,
                        UnderstandingPlanFirstFragment.newInstance(true))
                .addToBackStack("understanding")
                .commit();
    }
}