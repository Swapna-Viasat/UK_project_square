package construction.thesquare.shared.help;

import com.google.gson.annotations.SerializedName;

/**
 * Created by swapna on 3/22/2017.
 */

public class HelpClickedResponse {
    @SerializedName("id") public int id;
    @SerializedName("question") public String question;
    @SerializedName("answer") public String answer;
    @SerializedName("order") public String order;
}
