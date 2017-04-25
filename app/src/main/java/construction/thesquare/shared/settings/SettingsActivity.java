package construction.thesquare.shared.settings;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;

public abstract class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.frame)
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // find the title text view
        TextView toolbarTitle;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                // set my custom font
                Typeface typeface = Typeface.createFromAsset(getAssets(),
                        "fonts/JosefinSans-Italic.ttf");
                toolbarTitle.setTypeface(typeface);
                break;
            }
        }
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            final Drawable menu = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
            ab.setHomeAsUpIndicator(menu);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setElevation(24);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                    return true;
                } else {
                    getSupportFragmentManager().popBackStack();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
