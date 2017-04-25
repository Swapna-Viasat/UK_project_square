package construction.thesquare.shared.view.activity;

import android.app.Activity;

/**
 * Created by sebastiancorradi on 4/5/16.
 */
public abstract class BasePresenter{

    public BasePresenter(Activity activity) {
        this.setActivity(activity);
    }

    public BasePresenter() {

    }


    protected abstract void setActivity(Activity activity);

    protected abstract Activity getActivity();
}
