package construction.thesquare.worker.jobmatches;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.jobdetails.JobDetailActivity;
import construction.thesquare.worker.jobdetails.LikeJobConnector;
import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.jobmatches.model.MatchesResponse;
import construction.thesquare.worker.jobmatches.model.Ordering;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/1/2016.
 */

public class MatchesPresenter implements MatchesContract.UserActionListener, LikeJobConnector.Callback {

    private final MatchesContract.View mMatchesView;
    private LikeJobConnector likeJobConnector;
    private Ordering currentOrdering;
    private Integer currentCommuteTime;

    public MatchesPresenter(MatchesContract.View view) {
        this.mMatchesView = view;
        likeJobConnector = new LikeJobConnector(this);
    }

    @Override
    public void onShowDetails(Context context, Job job) {
        if (context == null || job == null) return;
        Intent intent = new Intent(context, JobDetailActivity.class);
        intent.putExtra(JobDetailActivity.JOB_ARG, job.id);
        context.startActivity(intent);
    }

    @Override
    public void onLikeJobClick(Context context, Job job) {
        if (job == null) return;

        if (job.liked) likeJobConnector.unlikeJob(context, job.id);
        else likeJobConnector.likeJob(context, job.id);
    }

    @Override
    public void fetchJobMatches() {
        mMatchesView.displayProgress(true);
        Call<MatchesResponse> call = HttpRestServiceConsumer.getBaseApiClient()
                .getJobMatches(currentOrdering, currentCommuteTime);
        call.enqueue(new Callback<MatchesResponse>() {
            @Override
            public void onResponse(Call<MatchesResponse> call, Response<MatchesResponse> response) {
                mMatchesView.displayProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (null != response.body().response) {
                            mMatchesView.displayMatches(response.body().response);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MatchesResponse> call, Throwable t) {
                mMatchesView.displayProgress(false);
                HandleErrors.parseFailureError(mMatchesView.getContext(), null, t);
            }
        });
    }

    @Override
    public void setMatchesFilters(Ordering ordering, int commuteTime) {
        currentOrdering = ordering;
        currentCommuteTime = commuteTime;
    }

    @Override
    public Ordering getOrdering() {
        return currentOrdering;
    }

    @Override
    public void fetchMe(Context context) {
        if (context == null) return;
        List<String> requiredFields = Arrays.asList("onboarding_skipped");
        HttpRestServiceConsumer.getBaseApiClient()
                .getFilteredWorker(SharedPreferencesManager.getInstance(context).getWorkerId(), requiredFields)
                .enqueue(new Callback<ResponseObject<Worker>>() {
            @Override
            public void onResponse(Call<ResponseObject<Worker>> call,
                                   Response<ResponseObject<Worker>> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body().getResponse() != null)
                            mMatchesView.displayHint(response.body().getResponse().onboardingSkipped);
                    } catch (Exception e) {
                        CrashLogHelper.logException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                //
            }
        });
    }

    @Override
    public void onConnectorSuccess() {
        fetchJobMatches();
    }
}
