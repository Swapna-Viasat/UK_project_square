package construction.thesquare.shared.suggestions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 4/6/2017.
 */

public class Suggestion implements Serializable {
    @SerializedName("category")
    public String category;
    @SerializedName("description")
    public String description;
}
