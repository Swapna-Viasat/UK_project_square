package construction.thesquare.shared.analytics;

import android.content.Context;

/**
 * Created by gherg on 4/19/17.
 */

public class AnalyticStorage {

    public static final String TAG = "AnalyticStorage";
    private static final String sharedPrefName = "analytics_storage";
    private static final String keyId = "key_client_id";
    private static final String keyCampaignName = "key_campaign_name";
    private static final String keyAdsId = "key_ads_id";

    public static void persistGAClientId(Context c, String key) {
        try {
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .edit().putString(keyId, key).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getGAClientId(Context c) {
        try {
            return
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .getString(keyId, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void persistCampaignName(Context c, String name) {
        try {
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .edit().putString(keyCampaignName, name).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCampaignName(Context c) {
        try {
            return
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .getString(keyCampaignName, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void persistAdsId(Context c, String id) {
        try {
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .edit().putString(keyAdsId, id).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAdsId(Context c) {
        try {
            return
            c.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
                    .getString(keyAdsId, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
