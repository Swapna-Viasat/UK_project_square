package construction.thesquare.worker.help;



import java.util.List;

import construction.thesquare.shared.models.Help;

/**
 * Created by Swapna on 13/03/2017.
 */

public interface HelpContract {
    interface View {
        void displaySearchData(List<Help> data);
       }
    interface UserActionListener {
        void fetchSearch(String question);
    }
}
