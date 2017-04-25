package construction.thesquare.shared.view.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by juanmaggi on 3/5/16.
 */
public abstract class BaseActivity extends Activity {

    protected construction.thesquare.shared.view.activity.BasePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public abstract void setPresenter(construction.thesquare.shared.view.activity.BasePresenter presenter);
}


