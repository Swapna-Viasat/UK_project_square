package construction.thesquare.employer.subscription.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gherg on 12/29/2016.
 */

public class CreateCardRequest {
    @SerializedName("cardholder_name") public String name;
    @SerializedName("stripe_source_token") public String token;
}
