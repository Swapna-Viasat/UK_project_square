package construction.thesquare.shared.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import construction.thesquare.R;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by gherg on 2/24/17.
 */

public class ShareUtils {

    public static final String TAG = "ShareUtils";

    public static void workerLink(final Context context) {
        BranchUniversalObject object = new BranchUniversalObject()
                .setCanonicalIdentifier("worker/12345")
                //
                .setTitle(context.getString(R.string.share_description))
                //.setContentDescription(context.getString(R.string.share_description))
                //
                .setContentImageUrl(context.getString(R.string.share_worker_general_image_url))
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property1", "blue")
                .addContentMetadata("property2", "red");
        LinkProperties linkProperties = new LinkProperties()
                .setChannel("android")
                .setFeature("android")
                .setCampaign("androidAppWorker")
                .addControlParameter("$desktop_url", "http://www.thesquareapp.tech/")
                .addControlParameter("$ios_url", "http://www.thesquareapp.tech/")
                .addControlParameter("$android_url", context.getString(R.string.share_play_store_url));
        object.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (null == error) {
                    Log.d(TAG, url);
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    String name = SharedPreferencesManager.getInstance(context)
                            .loadSessionInfoWorker()
                            .name;
                    String emailSubject = String
                            .format(context.getString(R.string.share_worker_subject), name);
                    intent2.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                    intent2.putExtra(Intent.EXTRA_TEXT,
                            context.getString(R.string.share_generic) +
                                    "\n\n" + url);
                    intent2.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent2, "Share The Square"));
                } else {
                    Log.d(TAG, "error generating link");
                }
            }
        });
    }

    public static void employerLink(final Context context) {
        BranchUniversalObject object = new BranchUniversalObject()
                .setCanonicalIdentifier("employer/12345")
                //
                .setTitle(context.getString(R.string.share_description))
                //.setContentDescription(context.getString(R.string.share_description))
                //
                .setContentImageUrl(context.getString(R.string.share_employer_general_image_url))
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property1", "blue")
                .addContentMetadata("property2", "red");
        LinkProperties linkProperties = new LinkProperties()
                .setChannel("android")
                .setFeature("android")
                .setCampaign("androidAppEmployer")
                .addControlParameter("$desktop_url", "http://www.thesquareapp.tech/")
                .addControlParameter("$ios_url", "http://www.thesquareapp.tech/")
                .addControlParameter("$android_url", context.getString(R.string.share_play_store_url));
        object.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (null == error) {
                    Log.d(TAG, url);
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    String name = SharedPreferencesManager.getInstance(context)
                            .loadSessionInfoEmployer()
                            .name;
                    String emailSubject = String
                            .format(context.getString(R.string.share_employer_subject), name);
                    intent2.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                    intent2.putExtra(Intent.EXTRA_TEXT,
                            context.getString(R.string.share_generic) +
                                    "\n\n" + url);
                    intent2.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent2, "Share The Square"));
                } else {
                    Log.d(TAG, "error generating link " + error.getMessage());
                }
            }
        });
    }

    public static final String jobLink(int jobId) {
        return "";
    }
}
