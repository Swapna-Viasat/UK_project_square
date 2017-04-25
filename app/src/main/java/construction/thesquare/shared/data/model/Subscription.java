package construction.thesquare.shared.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gherg on 3/21/17.
 */

public class Subscription {

    public int id;
    public int status;
    public int employer;
    @SerializedName("payments_detail") public int paymentDetail;
    @SerializedName("coupon_code") public String couponCode;
    @SerializedName("purchase_order_number") public String purchaseOrderNumber;
    @SerializedName("stripe_customer_id") public String stripeCustomerId;
    @SerializedName("subscription_id") public String subscriptionId;
    public String created;
    public String modified;
    @SerializedName("bill_payers_name") public String billPayerName;
    @SerializedName("bill_payers_email") public String billPayerEmail;
    @SerializedName("current_period_end") public long currentPeriodEnd;
    @SerializedName("current_period_start") public long currentPeriodStart;
    @SerializedName("stripe_created_at") public long stripeCreatedAt;
    @SerializedName("canceled_at") public long cancelledAt;
    @SerializedName("trial_start") public long trialStart;
    @SerializedName("trial_end") public long trialEnd;

}
