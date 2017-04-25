package construction.thesquare.employer.subscription.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/29/2016.
 */

public class SubscriptionFragment extends Fragment {

    private int selection;
    public static final int SELECTION_TRIAL = 0;
    public static final int SELECTION_UPGRADE = 1;
    public static final String TAG = "SubscriptionFragment";
    private static SubscriptionFragment subscriptionFragment;
    @BindView(R.id.upgrade_top)
    JosefinSansTextView upgradeTop;
    @BindView(R.id.upgrade_bottom) JosefinSansTextView upgradeBottom;
    @BindView(R.id.trial_top) JosefinSansTextView trialTop;
    @BindView(R.id.trial_bottom) JosefinSansTextView trialBottom;
    @BindView(R.id.check_trial)
    AppCompatCheckBox checkBoxTrial;
    @BindView(R.id.check_upgrade) AppCompatCheckBox checkBoxUpgrade;

    public static SubscriptionFragment newInstance() {
        if (subscriptionFragment == null) {
            subscriptionFragment = new SubscriptionFragment();
        }
        return subscriptionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
        checkBoxUpgrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selection = SELECTION_UPGRADE;
                    update();
                } else {
                    selection = SELECTION_TRIAL;
                    update();
                }
            }
        });
        checkBoxTrial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selection = SELECTION_TRIAL;
                    update();
                } else {
                    selection = SELECTION_UPGRADE;
                    update();
                }
            }
        });
    }

    private void update() {
        switch (selection) {
            case SELECTION_TRIAL:
                checkBoxTrial.setChecked(true);
                checkBoxUpgrade.setChecked(false);
                upgradeBottom.setTextColor(ContextCompat.getColor(getContext(), R.color.blackSquareColor));
                upgradeTop.setTextColor(ContextCompat.getColor(getContext(), R.color.blackSquareColor));
                trialBottom.setTextColor(ContextCompat.getColor(getContext(), R.color.redSquareColor));
                trialTop.setTextColor(ContextCompat.getColor(getContext(), R.color.redSquareColor));
                break;
            case SELECTION_UPGRADE:
                checkBoxUpgrade.setChecked(true);
                checkBoxTrial.setChecked(false);
                upgradeBottom.setTextColor(ContextCompat.getColor(getContext(), R.color.redSquareColor));
                upgradeTop.setTextColor(ContextCompat.getColor(getContext(), R.color.redSquareColor));
                trialBottom.setTextColor(ContextCompat.getColor(getContext(), R.color.blackSquareColor));
                trialTop.setTextColor(ContextCompat.getColor(getContext(), R.color.blackSquareColor));
                break;
        }
    }

    @OnClick({R.id.ll_trial, R.id.ll_upgrade, R.id.action0})
    public void change(View view) {
        switch (view.getId()) {
            case R.id.ll_trial:
                selection = SELECTION_TRIAL;
                update();
                break;
            case R.id.ll_upgrade:
                selection = SELECTION_UPGRADE;
                update();
                break;
            case R.id.action0:
                //
                break;
        }
    }
}
