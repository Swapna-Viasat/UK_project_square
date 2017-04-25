package construction.thesquare.employer.myjobs;

import android.app.Dialog;
import android.content.Context;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.response.EmployerJobResponse;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/30/2016.
 */

public class JobsPresenter implements JobsContract.UserActionsListener {

    public static final String TAG = "JobsPresenter";

    private JobsContract.View mView;
    private Context context;

    public JobsPresenter(JobsContract.View view, Context context) {
        this.mView = view;
        this.context = context;
    }

    @Override
    public void fetchJobs(int status) {
        final Dialog dialog = DialogBuilder.showCustomDialog(context);

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJobs(status)
                .enqueue(new Callback<EmployerJobResponse>() {
                    @Override
                    public void onResponse(Call<EmployerJobResponse> call,
                                           Response<EmployerJobResponse> response) {
                        //
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            mView.displayJobs(response.body().response);

                        } else {
                            HandleErrors.parseError(context, dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployerJobResponse> call, Throwable t) {
                        //
                        TextTools.log(TAG, t.getMessage() == null ? "" : t.getMessage());
                        HandleErrors.parseFailureError(context, dialog, t);
                    }
                });
    }
}