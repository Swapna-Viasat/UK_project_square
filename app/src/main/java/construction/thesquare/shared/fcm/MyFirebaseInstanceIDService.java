package construction.thesquare.shared.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//import io.intercom.android.sdk.push.IntercomPushClient;

/**
 * Created by gherg on 3/1/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TAG = "firebase";

//    private final IntercomPushClient intercomPushClient = new IntercomPushClient();

    @Override
    public void onTokenRefresh() {
        //
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

//        intercomPushClient.sendTokenToIntercom(getApplication(), refreshedToken);
    }
}
