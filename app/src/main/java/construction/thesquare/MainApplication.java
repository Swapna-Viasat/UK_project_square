package construction.thesquare;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import construction.thesquare.shared.analytics.AnalyticStorage;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.login.LoginModule;
import construction.thesquare.shared.login.controller.EmailLoginFragment;
import dagger.Component;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends MultiDexApplication {

    private static Context context;

    private static Tracker mTracker;
    private static GoogleAnalytics googleAnalytics;

    @Singleton
    @Component(
            modules = { LoginModule.class }
    )

    public interface ApplicationComponent {
        void inject(EmailLoginFragment emailLoginFragment);
    }

    private ApplicationComponent component;

    public ApplicationComponent component() {
        return component;
    }

    public void onCreate() {
        super.onCreate();

        component = DaggerMainApplication_ApplicationComponent.create();

        googleAnalytics = GoogleAnalytics.getInstance(this);
        mTracker = getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);
        context = getApplicationContext();

        String clientId = mTracker.get("&cid");
        AnalyticStorage.persistGAClientId(this, clientId);

        SharedPreferencesManager.saveDeviceId(this);

        Branch.getAutoInstance(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

    ApplicationComponent getComponent() {
        return component;
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            mTracker = googleAnalytics.newTracker(construction.thesquare.R.xml.global_tracker_settings);
        }
        return mTracker;
    }

    public static Context getAppContext() {
        return context;
    }
}
