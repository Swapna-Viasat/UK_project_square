package construction.thesquare.worker.jobdetails;

import android.app.Dialog;
import android.widget.Toast;

import construction.thesquare.shared.applications.ApplicationsConnector;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.jobmatches.model.Application;
import construction.thesquare.worker.jobmatches.model.Job;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

class JobDetailsPresenter implements ApplicationsConnector.Callback, LikeJobConnector.Callback {

    private JobDetailsContract contract;
    private Job currentJob;
    private LikeJobConnector likeJobConnector;
    private ApplicationsConnector applicationsConnector;

    JobDetailsPresenter(JobDetailsContract contract) {
        this.contract = contract;
        likeJobConnector = new LikeJobConnector(this);
        applicationsConnector = new ApplicationsConnector(this);
    }

    void fetchJob(int jobId) {
        if (contract == null || contract.getContext() == null) return;
        final Dialog dialog = DialogBuilder.showCustomDialog(contract.getContext());

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .fetchSingleJob(jobId)
                    .enqueue(new Callback<ResponseObject<Job>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Job>> call,
                                               Response<ResponseObject<Job>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                currentJob = response.body().getResponse();
                                contract.onJobFetched();
                            } else {
                                HandleErrors.parseError(contract.getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                            HandleErrors.parseFailureError(contract.getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            dialog.dismiss();
        }
    }

    void applyToJob(int jobId) {
        if (contract == null || contract.getContext() == null) return;
        final Dialog dialog = DialogBuilder.showCustomDialog(contract.getContext());

        try {
            HttpRestServiceConsumer.getBaseApiClient()
                    .applyJob(jobId)
                    .enqueue(new Callback<ResponseObject<Application>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Application>> call,
                                               Response<ResponseObject<Application>> response) {

                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                contract.onJobApply(response.body().getResponse());
                            } else {
                                HandleErrors.parseError(contract.getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Application>> call, Throwable t) {
                            HandleErrors.parseFailureError(contract.getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            dialog.dismiss();
        }
    }

    void onLikeJobClick() {
        if (contract == null || contract.getContext() == null || currentJob == null) return;

        likeJobConnector.likeJob(contract.getContext(), currentJob.id);
    }

    void onUnlikeJobClick() {
        if (contract == null || contract.getContext() == null || currentJob == null) return;

        likeJobConnector.unlikeJob(contract.getContext(), currentJob.id);
    }

    /*void onShareJobClick() {
        if (contract == null || contract.getContext() == null) return;
        Toast.makeText(contract.getContext(), "In Development", Toast.LENGTH_SHORT).show();
    }*/

    Job getCurrentJob() {
        return currentJob;
    }

    void cancelBooking(int applicationId, String feedback) {
        if (contract != null && contract.getContext() != null)
            applicationsConnector.cancelBooking(contract.getContext(), applicationId, feedback);
    }

    @Override
    public void onApplicationCancelled() {
        if (contract != null) contract.onBookingCanceled();
    }

    @Override
    public void onConnectorSuccess() {
        if (currentJob != null)
            fetchJob(currentJob.id);
    }
}
