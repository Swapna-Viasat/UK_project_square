package construction.thesquare.worker.signup.datasource;

import android.app.Dialog;
import android.content.Context;

import java.util.HashMap;

import construction.thesquare.shared.data.model.SMSSent;
import construction.thesquare.worker.signup.model.WorkerVerify;

/**
 * Created by juanmaggi on 25/7/16.
 */
public interface SignUpWorkerServiceConsumer {

    interface OnVerificationWorkerNumberFinishedListener {
        void onPostVerificationNumberWorkerSuccess(Dialog dialog, WorkerVerify workerVerify);
    }

    void verifyWorkerNumber(Context aContext, HashMap<String, Object> verificationRequest,
                            OnVerificationWorkerNumberFinishedListener listener);

    interface OnSMSWorkerResendFinishedListener {
        void onSMSSentWorkerSuccess(Dialog dialog, SMSSent smsSent);
    }

    void resendSMSWorker(Context aContext, HashMap<String, String> resendSMSRequest, OnSMSWorkerResendFinishedListener listener);
}
