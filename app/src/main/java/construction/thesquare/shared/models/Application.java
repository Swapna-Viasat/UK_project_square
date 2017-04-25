package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 2/7/17.
 */

public class Application implements Serializable {

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_APPROVED = 2;
    public static final int STATUS_DENIED = 3;
    public static final int STATUS_CANCELLED = 4;
    public static final int STATUS_ENDED_CONTRACT = 5;

    public int id;
    @SerializedName("is_offer") public boolean isOffer;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("accepted_at") public String acceptedAt;
    @SerializedName("denied_at") public String deniedAt;
    @SerializedName("cancelled_at") public String cancelledAt;
    @SerializedName("end_contract_at") public String endContractAt;
    @SerializedName("worker_worked") public boolean workerWorked;
    @SerializedName("last_worked_day") public String lastWorkedDay;
    @SerializedName("showed_to_job") public boolean showedToJob;
    @SerializedName("job") public int jobId;
    @SerializedName("worker") public int workerId;
    public Status status;

    public class Status implements Serializable {
        public int id;
        public String name;
    }
}
