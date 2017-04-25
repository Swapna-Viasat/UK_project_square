package construction.thesquare.shared.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square. All rights reserved.
 */

public class KeyboardUtils {

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
