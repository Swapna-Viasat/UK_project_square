package construction.thesquare.employer.payments.fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.utils.CrashLogHelper;

public class UnderstandingPlanFirstFragment extends Fragment {

    public static final String TAG = "UnderstandingPricePlan";

    @BindViews({R.id.understand_1, R.id.understand_2, R.id.understand_3,
                R.id.understand_4, R.id.understand_5, R.id.understand_6,
                R.id.understand_7})
    public List<CardView> headers;
    @BindViews({R.id.understand_1_body, R.id.understand_2_body, R.id.understand_3_body,
            R.id.understand_4_body, R.id.understand_5_body, R.id.understand_6_body,
            R.id.understand_7_body})
    public List<ExpandableLinearLayout> bodies;

    private boolean firstTime;

    public UnderstandingPlanFirstFragment() {
        // Required empty public constructor
    }

    public static UnderstandingPlanFirstFragment newInstance(boolean firstTime) {
        UnderstandingPlanFirstFragment fragment = new UnderstandingPlanFirstFragment();
        Bundle args = new Bundle();
        args.putBoolean("first_time", firstTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firstTime = getArguments().getBoolean("first_time");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_understanding_plan_first, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        try {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar().setTitle("Understanding My Price Plan");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        setupExpandables();
    }

    private void setupExpandables() {
        for (int i = 0; i < headers.size() -1; i++) {
            setupExpansionHandler(headers.get(i), bodies.get(i));
        }

        headers.get(headers.size()-1)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bodies.get(bodies.size()-1).toggle();
                        CountDownTimer timer = new CountDownTimer(300, 300) {
                            @Override
                            public void onTick(long l) {
                                //
                            }

                            @Override
                            public void onFinish() {
                                try {
                                    getView().scrollTo(0, getView().getHeight());
                                } catch (Exception e) {
                                    CrashLogHelper.logException(e);
                                }
                            }
                        };

                        timer.start();
                    }
                });
    }

    private void setupExpansionHandler(CardView header,
                                       final ExpandableLinearLayout body) {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                body.toggle();
            }
        });
    }
}
