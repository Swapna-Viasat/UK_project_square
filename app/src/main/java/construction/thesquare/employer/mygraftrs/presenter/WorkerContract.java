package construction.thesquare.employer.mygraftrs.presenter;

import java.util.List;

import construction.thesquare.employer.mygraftrs.model.Worker;

/**
 * Created by Evgheni on 10/21/2016.
 */

public interface WorkerContract {

    interface View {
        void showWorkerList(List<Worker> data);
        void showEmptyList();
        void showProgress(boolean show);
        void showError(String message);
    }

    interface UserActionsListener {
        void fetchLikedWorkers(int id);
        void fetchBookedWorkers(int id);
        void viewWorkerDetails();
        void quickInvite();
    }
}
