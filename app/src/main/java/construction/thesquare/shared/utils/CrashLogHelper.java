/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.shared.utils;

import com.crashlytics.android.Crashlytics;

import construction.thesquare.BuildConfig;

public class CrashLogHelper {

    public static void logException(Throwable throwable) {
        if (!BuildConfig.DEBUG) {
            Crashlytics.logException(throwable);
        } else {
            throwable.printStackTrace();
        }
    }
}
