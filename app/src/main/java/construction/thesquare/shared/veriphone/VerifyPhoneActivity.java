package construction.thesquare.shared.veriphone;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.login.controller.EmailLoginFragment;
import construction.thesquare.shared.veriphone.fragment.VerifyPhoneFragment;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;

/**
 * Created by gherg on 12/27/2016.
 */

public class VerifyPhoneActivity extends AppCompatActivity {

    public static final String TAG = "VerifyPhoneActivity";

    @BindView(R.id.toolbar_verify_phone)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ButterKnife.bind(this);
        setToolbar();

        int i = getIntent().getIntExtra(Constants.KEY_VERIFY_PHONE, 1);

        try {
            if (i == Constants.KEY_VERIFY_PHONE_LOGIN)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.phone_verify_content, new EmailLoginFragment())
                        .commit();
            else getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.phone_verify_content, VerifyPhoneFragment.newInstance(i))
                    .commit();
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(getMenuIcon());
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setElevation(0);
            ab.setTitle("");
        }
    }

    private Drawable getMenuIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.redSquareColor));
        return drawable;
    }
}