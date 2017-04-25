package construction.thesquare.shared.analytics;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Locale;

import construction.thesquare.BuildConfig;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 4/19/17.
 */

public class Analytics {

    public static final String TAG = "Analytics";

    public static void recordEvent(Context c,
                                   String eventCategory,
                                   String event) {
        //
        final HashMap<String, Object> body = new HashMap<>();
        body.put("tracking_id", AnalyticStorage.getGAClientId(c));
        body.put("session_id", SharedPreferencesManager.getInstance(c).getToken());
        body.put("hit_type", "event");
        body.put("name", event);
        body.put("event_category", eventCategory);
        body.put("screen_resolution",
                String.valueOf(c.getResources().getDisplayMetrics().heightPixels) + "x" +
                String.valueOf(c.getResources().getDisplayMetrics().widthPixels));
        body.put("user_language", String.valueOf(Locale.getDefault().getDisplayLanguage()));
        body.put("application_id", "construction.thesquare");
        body.put("application_version", String.valueOf(BuildConfig.VERSION_NAME));
        body.put("data_source", "app");
        body.put("user_agent",
                String.valueOf("") + " " + " Android API Version: " +
                        String.valueOf(Build.VERSION.SDK_INT));
        body.put("campaign_name", AnalyticStorage.getCampaignName(c));
        body.put("google_ads_id", AnalyticStorage.getAdsId(c));
        HttpRestServiceConsumer.getBaseApiClient()
                .track(body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public static void recordCurrentScreen(Context c,
                                           String screen) {
        //
        final HashMap<String, Object> body = new HashMap<>();
        body.put("tracking_id", AnalyticStorage.getGAClientId(c));
        body.put("session_id", SharedPreferencesManager.getInstance(c).getToken());
        body.put("hit_type", "screenview");
        body.put("screen_name", screen);
        body.put("screen_resolution",
                String.valueOf(c.getResources().getDisplayMetrics().heightPixels) + "x" +
                String.valueOf(c.getResources().getDisplayMetrics().widthPixels));
        body.put("user_language", String.valueOf(Locale.getDefault().getDisplayLanguage()));
        body.put("application_id", "construction.thesquare");
        body.put("application_version", String.valueOf(BuildConfig.VERSION_NAME));
        body.put("data_source", "app");
        body.put("user_agent",
                String.valueOf("") + " " + " Android API Version: " +
                String.valueOf(Build.VERSION.SDK_INT));
        body.put("campaign_name", AnalyticStorage.getCampaignName(c));
        body.put("google_ads_id", AnalyticStorage.getAdsId(c));
        HttpRestServiceConsumer.getBaseApiClient()
                .track(body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

}