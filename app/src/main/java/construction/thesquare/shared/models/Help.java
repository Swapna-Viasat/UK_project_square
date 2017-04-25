package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 3/13/2017.
 */

public class Help implements Serializable {
    public int id;
    @Nullable
    @SerializedName("question") public String question;
    @Nullable
    @SerializedName("answer") public String answer;
    @Nullable
    @SerializedName("order") public String order;
}
