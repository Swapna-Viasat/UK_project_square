package construction.thesquare.employer.mygraftrs;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.mygraftrs.fragment.MyWorkerProfileFragment;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.TextTools;

public class WorkerDetailsActivity extends AppCompatActivity {

    public static final String TAG = "WorkerDetailsActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ButterKnife.bind(this);

        setToolbar();

        Intent intent = getIntent();
        int workerId = intent.getIntExtra(Constants.KEY_WORKER_ID, 0);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,
                            MyWorkerProfileFragment.newInstance(workerId))
                    .commit();
        }
    }

    private void setToolbar() {
        TextTools.log(TAG, "setting up toolbar");
        setSupportActionBar(toolbar);
        // find the title text view
        TextView toolbarTitle;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            TextTools.log(TAG, "looping: " + String.valueOf(i));
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                TextTools.log(TAG, "found title: " + String.valueOf(i));
                toolbarTitle = (TextView) child;
                // set my custom font
                Typeface typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/JosefinSans-Italic.ttf");
                toolbarTitle.setTypeface(typeface);
            }
        }
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.view_profile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
