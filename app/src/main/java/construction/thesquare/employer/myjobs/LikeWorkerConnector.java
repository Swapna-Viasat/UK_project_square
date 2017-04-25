package construction.thesquare.employer.myjobs;

import android.app.Dialog;
import android.content.Context;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.StatusMessageResponse;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class LikeWorkerConnector {
    private Callback callback;

    public interface Callback {
        void onConnectorSuccess();
    }

    public LikeWorkerConnector(Callback callback) {
        this.callback = callback;
    }

    public void likeWorker(final Context context, int workerId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .likeWorker(workerId)
                    .enqueue(new retrofit2.Callback<ResponseObject<StatusMessageResponse>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<StatusMessageResponse>> call,
                                               Response<ResponseObject<StatusMessageResponse>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onConnectorSuccess();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<StatusMessageResponse>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            dialog.dismiss();
        }
    }

    public void unlikeWorker(final Context context, int workerId) {
        if (context == null) {
            return;
        }
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .unlikeWorker(workerId)
                    .enqueue(new retrofit2.Callback<ResponseObject<StatusMessageResponse>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<StatusMessageResponse>> call,
                                               Response<ResponseObject<StatusMessageResponse>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                callback.onConnectorSuccess();
                            } else {
                                HandleErrors.parseError(context, dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<StatusMessageResponse>> call, Throwable t) {
                            HandleErrors.parseFailureError(context, dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            dialog.dismiss();
        }
    }
}

