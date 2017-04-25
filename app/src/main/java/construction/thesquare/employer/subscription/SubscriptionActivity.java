package construction.thesquare.employer.subscription;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.subscription.adapter.SubscriptionTabAdapter;

/**
 * Created by gherg on 12/29/2016.
 */

public class SubscriptionActivity extends AppCompatActivity {

    @BindView(R.id.tl_subscription) TabLayout tl;
    @BindView(R.id.vp_subscription) ViewPager vp;
    private SubscriptionTabAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        ButterKnife.bind(this);
        adapter = new SubscriptionTabAdapter(this, getSupportFragmentManager());
        vp.setAdapter(adapter); tl.setupWithViewPager(vp);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }
}
