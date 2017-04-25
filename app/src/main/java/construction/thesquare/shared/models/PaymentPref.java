package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class PaymentPref implements Serializable {
    public int id;
    public String name;
    @SerializedName("jobs_ad_count") public int jobsAdCount;
    @SerializedName("hires_count") public int hiresCount;
    @SerializedName("amount_per_hire") public int amountPerHire;
    public int amount;
}
