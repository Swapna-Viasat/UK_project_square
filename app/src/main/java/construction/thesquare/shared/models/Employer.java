package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/29/2016.
 */

public class Employer implements Serializable {
    public int id;
    public String identifier;
    @SerializedName("user_type") public int userType;
    @SerializedName("onboarding_done") public boolean onboardingDone;
    @SerializedName("valid_company_employer") public boolean validCompanyEmployer;
    @SerializedName("reviews_amount") public int reviewCount;
    @SerializedName("reviews_avg") public int reviewInt;
    @Nullable
    @SerializedName("first_name") public String firstName;
    @Nullable
    @SerializedName("last_name") public String lastName;
    @SerializedName("email_confirmed") public boolean emailConfirmed;
    @SerializedName("onboarding_skipped") public boolean onboardingSkipped;
    @Nullable
    public String email;
    @Nullable
    @SerializedName("job_title") public String jobTitle;
    @SerializedName("worker_app_notifications") public boolean workerAppNotifications;
    @SerializedName("job_notifications") public boolean jobNotifications;
    @SerializedName("reviews_notifications") public boolean reviewNotifications;
    @Nullable
    public Company company;
    @Nullable
    public String picture;

    // subscription related fields
    @SerializedName("plan_bookings") public int bookedWithPlan;
    @SerializedName("max_plan_bookings") public int maxForPlan;
    @SerializedName("topup_bookings") public int bookedWithTopups;
    @SerializedName("max_topup_bookings") public int maxForTopups;
    @Nullable
    @SerializedName("plan_end_date") public String planExpiration;
    @Nullable
    @SerializedName("topup_end_date") public String topupExpiration;
    @Nullable
    @SerializedName("plan_name") public String planName;
    @SerializedName("payments_detail") public int subscriptionId;

    @Nullable
    @SerializedName("stripe_customer_token") public String stripeToken;
    @Nullable
    @SerializedName("review_data")
    public ReviewData reviewData;
}