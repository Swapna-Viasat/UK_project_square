package construction.thesquare.worker.myjobs;

import android.content.Context;

import java.util.List;

import construction.thesquare.worker.jobmatches.model.Job;

/**
 * Created by Evgheni on 11/3/2016.
 */

public interface JobsContract {

    interface View {
        void displayJobs(List<Job> data);
        void displayProgress(boolean show);
        Context getContext();
    }

    interface UserActionsListener {
        void onShowDetails(Job job);
        void init(int jobType);
    }
}
