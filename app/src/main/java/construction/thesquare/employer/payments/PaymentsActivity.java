package construction.thesquare.employer.payments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.payments.fragment.PaymentFragment;
import construction.thesquare.employer.payments.fragment.SubscriptionFragment;
import construction.thesquare.shared.utils.TextTools;

public class PaymentsActivity extends AppCompatActivity {

    public static final String TAG = "PaymentsActivity";

    @BindViews({R.id.payments_subscription,
            R.id.payments_cards, R.id.payments_finish})
    List<ImageView> imgViews;
    @BindViews({R.id.payments_subscription_label,
            R.id.payments_cards_label, R.id.payments_finish_label})
    List<TextView> labels;
    private int selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            select(selection);
        } else {
            select(savedInstanceState.getInt("tab"));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.payments_content, SubscriptionFragment.newInstance(false, false))
                .commit();
    }

    @OnClick({R.id.payments_1, R.id.payments_2, R.id.payments_3})
    public void onAction(View view) {
        switch (view.getId()) {
            case R.id.payments_1:
                selection = 0;
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.payments_content, SubscriptionFragment.newInstance(false, false))
                        .commit();
                break;
            case R.id.payments_2:
                selection = 1;
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.payments_content, PaymentFragment.newInstance(0))
                        .commit();
                break;
            case R.id.payments_3:
                selection = 2;
                finish();
                break;
        }
        select(selection);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", selection);
        TextTools.log(TAG, "saving instance");
    }

    public void select(int id) {
        for (int i = 0; i < 3; i++) {
            if (i != id) {
                labels.get(i).setTextColor(ContextCompat
                        .getColor(getApplicationContext(), R.color.graySquareColor));
                imgViews.get(i).setColorFilter(ContextCompat
                        .getColor(getApplicationContext(), R.color.graySquareColor), PorterDuff.Mode.SRC_ATOP);
            } else {
                labels.get(i).setTextColor(ContextCompat
                        .getColor(getApplicationContext(), R.color.whiteSquareColor));
                imgViews.get(i).setColorFilter(ContextCompat
                        .getColor(getApplicationContext(), R.color.whiteSquareColor));
            }
        }
    }
}