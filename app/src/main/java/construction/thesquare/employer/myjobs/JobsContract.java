package construction.thesquare.employer.myjobs;

import java.util.List;

import construction.thesquare.shared.models.Job;

/**
 * Created by gherg on 12/30/2016.
 */

public interface JobsContract {

    interface View {
        void showProgress(boolean show);
        void displayJobs(List<Job> data);
    }

    interface UserActionsListener {
        void fetchJobs(int status);
    }
}