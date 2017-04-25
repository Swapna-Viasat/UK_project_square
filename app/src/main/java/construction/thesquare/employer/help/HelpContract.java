package construction.thesquare.employer.help;

import java.util.List;

import construction.thesquare.shared.models.Help;

/**
 * Created by swapna on 3/15/2017.
 */

public class HelpContract {
    interface View {
        void displaySearchData(List<Help> data);
    }
    interface UserActionListener {
        void fetchSearch(String question);
    }
}
