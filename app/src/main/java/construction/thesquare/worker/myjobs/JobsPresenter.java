package construction.thesquare.worker.myjobs;

import android.content.Context;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Application;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.myjobs.model.JobsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/3/2016.
 */

public class JobsPresenter implements JobsContract.UserActionsListener {

    private JobsContract.View mJobsView;
    private Context context;

    public JobsPresenter(Context context, JobsContract.View mJobsView) {
        this.mJobsView = mJobsView;
        this.context = context;
    }

    @Override
    public void init(int jobType) {
        switch (jobType) {
            case Job.TYPE_BOOKED:
                // 2 = Live job
                fetchJobs(Application.STATUS_APPROVED, 2, false, false);
                break;
            case Job.TYPE_OFFER:
                fetchJobs(Application.STATUS_PENDING, null, false, false);
                break;
            case Job.TYPE_COMPLETED:
                fetchJobs(Application.STATUS_ENDED_CONTRACT, null, false, false);
                break;
            case Job.TYPE_LIKED:
                fetchJobs(null, null, true, false);
                break;
            case Job.TYPE_OLD:
                //fetch Old jobs
                fetchJobs(null, 3, false, false);
                break;
            default:
                break;
        }
    }

    private void fetchJobs(Integer applicationStatus, Integer jobStatus, boolean liked, boolean isOffer) {
        mJobsView.displayProgress(true);
        int id = SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId();
        Call<JobsResponse> call = HttpRestServiceConsumer
                .getBaseApiClient().getMyJobs(id, applicationStatus, jobStatus, liked, isOffer);
        call.enqueue(new Callback<JobsResponse>() {
            @Override
            public void onResponse(Call<JobsResponse> call, Response<JobsResponse> response) {
                mJobsView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        mJobsView.displayJobs(response.body().response);
                    }
                }
            }

            @Override
            public void onFailure(Call<JobsResponse> call, Throwable t) {
                mJobsView.displayProgress(false);
                HandleErrors.parseFailureError(mJobsView.getContext(), null, t);
            }
        });
    }

    @Override
    public void onShowDetails(Job job) {
        //
    }
}
