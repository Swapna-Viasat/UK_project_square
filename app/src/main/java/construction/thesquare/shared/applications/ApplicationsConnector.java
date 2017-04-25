package construction.thesquare.shared.applications;

import android.app.Dialog;
import android.content.Context;

import construction.thesquare.shared.applications.model.Feedback;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.jobmatches.model.Application;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 The Square Tech. All rights reserved.
 */

public class ApplicationsConnector {
    private Callback callback;

    public interface Callback {
        void onApplicationCancelled();
    }

    public ApplicationsConnector(Callback callback) {
        this.callback = callback;
    }

    public void cancelBooking(final Context context, int applicationId, String feedback) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .cancelBooking(applicationId, new Feedback(feedback))
                    .enqueue(new retrofit2.Callback<ResponseObject<Application>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Application>> call,
                                               Response<ResponseObject<Application>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onApplicationCancelled();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Application>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            dialog.dismiss();
        }
    }
}
