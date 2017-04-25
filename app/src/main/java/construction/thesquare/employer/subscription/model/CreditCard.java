package construction.thesquare.employer.subscription.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gherg on 12/29/2016.
 */

public class CreditCard {
    public int id;
    @SerializedName("card_last_digits") public String last4;
    @SerializedName("expiration_date") public String exp;
    @SerializedName("cardholder_name") public String name;
}
