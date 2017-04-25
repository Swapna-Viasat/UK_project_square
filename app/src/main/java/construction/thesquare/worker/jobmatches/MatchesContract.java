package construction.thesquare.worker.jobmatches;

import android.content.Context;

import java.util.List;

import construction.thesquare.worker.jobmatches.model.Job;
import construction.thesquare.worker.jobmatches.model.Ordering;

/**
 * Created by Evgheni on 11/1/2016.
 */

public interface MatchesContract {

    interface View {
        void displayMatches(List<Job> data);
        void displayProgress(boolean show);
        void displayHint(boolean show);
        Context getContext();
    }

    interface UserActionListener {
        void onShowDetails(Context context, Job job);
        void onLikeJobClick(Context context, Job job);
        void fetchJobMatches();
        void setMatchesFilters(Ordering ordering, int commuteTime);
        Ordering getOrdering();
        void fetchMe(Context context);
    }
}
