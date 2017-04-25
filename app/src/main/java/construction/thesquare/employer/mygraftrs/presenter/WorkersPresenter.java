package construction.thesquare.employer.mygraftrs.presenter;

import java.util.List;

import construction.thesquare.employer.mygraftrs.model.Worker;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 10/21/2016.
 */

public class WorkersPresenter implements WorkerContract.UserActionsListener {

    private WorkerContract.View mWorkersView;

    public WorkersPresenter(WorkerContract.View contract) {
        this.mWorkersView = contract;
    }

    public void quickInvite() {
        //
    }

    public void viewWorkerDetails() {
        //
    }

    public void fetchLikedWorkers(int id) {
        mWorkersView.showProgress(true);
        Call<ResponseObject<List<Worker>>> call =
                HttpRestServiceConsumer.getBaseApiClient().fetchLikedWorkers(id);
        call.enqueue(new Callback<ResponseObject<List<Worker>>>() {
            @Override
            public void onResponse(Call<ResponseObject<List<Worker>>> call,
                                   Response<ResponseObject<List<Worker>>> response) {
                mWorkersView.showProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (!response.body().getResponse().isEmpty()) {
                            mWorkersView.showWorkerList(response.body().getResponse());
                        } else {
                            mWorkersView.showEmptyList();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<List<Worker>>> call, Throwable t) {
                mWorkersView.showProgress(false);
                mWorkersView.showEmptyList();
                mWorkersView.showError((null != t.getMessage()) ? t.getMessage() : "Something went wrong");
            }
        });
    }
    public void fetchBookedWorkers(int id) {
        mWorkersView.showProgress(true);
        Call<ResponseObject<List<Worker>>> call =
                HttpRestServiceConsumer.getBaseApiClient().fetchBookedWorkers(id);
        call.enqueue(new Callback<ResponseObject<List<Worker>>>() {
            @Override
            public void onResponse(Call<ResponseObject<List<Worker>>> call,
                                   Response<ResponseObject<List<Worker>>> response) {
                mWorkersView.showProgress(false);
                if (null != response) {
                    if (null != response.body()) {
                        if (!response.body().getResponse().isEmpty()) {
                            mWorkersView.showWorkerList(response.body().getResponse());
                        } else {
                            mWorkersView.showEmptyList();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<List<Worker>>> call, Throwable t) {
                mWorkersView.showProgress(false);
                mWorkersView.showEmptyList();
                mWorkersView.showError((null != t.getMessage()) ? t.getMessage() : "Something went wrong");
            }
        });
    }
}
