package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class Company implements Serializable {
    public int id;
    @Nullable
    public String name;
    @Nullable
    public String description;
    public int owner;
    @Nullable
    @SerializedName("worked_company")
    public String workedCompany;
    public boolean verified;
    @Nullable
    @SerializedName("registration_number")
    public String registrationNumber;
    @Nullable
    @SerializedName("contact_first_name")
    public String contactFirstName;
    @Nullable
    @SerializedName("contact_last_name")
    public String contactLastName;
    @Nullable
    @SerializedName("contact_phone")
    public String contactPhone;
    @Nullable
    @SerializedName("contact_email")
    public String contactEmail;
    @Nullable
    public String logo;
    @Nullable
    @SerializedName("post_code")
    public String postCode;
    @SerializedName("worker_payments")
    public boolean workerPayments;
    @SerializedName("workers_submit_timesheets")
    public boolean workersSubmitTimesheets;
    @SerializedName("pay_workers_directly")
    public boolean payWorkersDirectly;

    public boolean selected;
}
