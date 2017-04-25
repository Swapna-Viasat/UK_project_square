/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;

import construction.thesquare.shared.redirects.PaymentRedirect;
import construction.thesquare.shared.data.model.ResponseError;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.start.activity.StartActivity;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Juan on 11/12/2015.
 */
public class HandleErrors {

    private static final String TAG = "HandleErrors";
    private static final String standardError = "Oops... \n" +
            "Something's not right.\n" +
            "Please try again in a few seconds.\n";

    public static void parseError(Context context, Dialog dialog, Response<?> response) {
        if (dialog != null)
            DialogBuilder.cancelDialog(dialog);
        Converter<ResponseBody, ResponseError> converter =
                construction.thesquare.shared.data.HttpRestServiceConsumer.getRetrofitInstance()
                        .responseBodyConverter(ResponseError.class, new Annotation[0]);
        ResponseError responseError = null;
        try {
            responseError = converter.convert(response.errorBody());

            if (responseError.getError().getMessage().contains("Invalid token")) {
                DialogBuilder.showStandardDialog(context, "", responseError.getError().getMessage(),
                        onOkClickCallback);
            } else {
                DialogBuilder.showStandardDialog(context, "", responseError.getError().getMessage());
            }

        } catch (Exception exception) {
            //
            DialogBuilder.showStandardDialog(context, "", standardError);
        }
    }

    public static void parseError(Context context, Dialog dialog,
                                  Response<?> response,
                                  final PaymentRedirect payRedirect,
                                  final DialogInterface.OnClickListener gotoPaymentListener,
                                  final DialogInterface.OnClickListener listener) {
        if (dialog != null) {
            Log.d(TAG, String.valueOf(dialog.hashCode()));
            DialogBuilder.cancelDialog(dialog);
        }

        Converter<ResponseBody, ResponseError> converter =
                construction.thesquare.shared.data
                        .HttpRestServiceConsumer.getRetrofitInstance()
                        .responseBodyConverter(ResponseError.class, new Annotation[0]);
        ResponseError responseError;

        try {
            responseError = converter.convert(response.errorBody());

            if (responseError.getError().getMessage().contains("Invalid token")) {
                DialogBuilder.showStandardDialog(context, "",
                        responseError.getError().getMessage(),
                        onOkClickCallback);
            } else if (responseError.getError().code == 103) {
                //
                DialogBuilder.showStandardDialog(context, "",
                        responseError.getError().getMessage(), listener);
            } else if (responseError.getError().code == 101) {
                // no active subscription
//                DialogBuilder.showStandardDialog(context, "Error",
//                        responseError.getError().getMessage(), gotoPaymentListener);
                //
                // as per jira ticket redirect without prompt
                if (null != payRedirect) {
                    payRedirect.onRedirect();
                }

            } else if (responseError.getError().getMessage().contains("We already have email address")) {
                DialogBuilder.showStandardDialog(context, "",
                        responseError.getError().getMessage(), listener);
            } else {
                DialogBuilder.showStandardDialog(context, "", responseError.getError().getMessage());
            }

        } catch (Exception exception) {
            TextTools.log(TAG, "Response error: " + response.errorBody().toString());
            CrashLogHelper.logException(exception);
            DialogBuilder.showStandardDialog(context, "", standardError);
        }
    }

    public static void parseFailureError(Context context, Dialog dialog, Throwable throwable) {
        String error = standardError;
        DialogBuilder.cancelDialog(dialog);

        if (throwable instanceof IOException)
            error = "Oops!\nThe network connection\nwas lost.";

        if (throwable instanceof SocketTimeoutException)
            error = "Oops!\nConnection time out";

        DialogBuilder.showStandardDialog(context, "", error);
    }

    private static DialogBuilder.OnClickStandardDialog onOkClickCallback = new DialogBuilder.OnClickStandardDialog() {
        @Override
        public void onOKClickStandardDialog(Context context) {
            SharedPreferencesManager.getInstance(context).deleteToken();
            if (SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId() > 0) {
                SharedPreferencesManager.getInstance(context).deleteSessionInfoWorker();
                context.getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE).edit().clear().apply();
            } else if (SharedPreferencesManager.getInstance(context).loadSessionInfoEmployer().getUserId() > 0)
                SharedPreferencesManager.getInstance(context).deleteSessionInfoEmployer();

            Intent intent = new Intent(context, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    };

}
