package construction.thesquare.shared.view.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import construction.thesquare.R;
import construction.thesquare.shared.start.ActionBarViewHolder;

/**
 * Created by juanmaggi on 12/5/16.
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {

    protected construction.thesquare.shared.view.activity.BasePresenter presenter;
    protected ActionBarViewHolder actionBarViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
    }

    public abstract void setPresenter(construction.thesquare.shared.view.activity.BasePresenter presenter);

    public void setActionBar() {
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_sign_up, null, false);
        actionBarViewHolder = new ActionBarViewHolder(actionBarView, 0);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.whiteSquareColor)));
            actionBar.setCustomView(actionBarView, layoutParams);
        }
        actionBarViewHolder.enableBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle header native back arrow
        onBackPressed();
        return false;
    }
}
