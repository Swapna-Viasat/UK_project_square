package construction.thesquare.worker.jobmatches.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class Application implements Serializable {

    public int id;
    public ApplicationStatus status;
    @SerializedName("is_offer")
    public boolean isOffer;
    @SerializedName("worker_worked")
    public boolean workerWorked;
    @SerializedName("showed_to_job")
    public boolean showedToJob;
    @SerializedName("job")
    public int jobId;
    @SerializedName("worker")
    public int workerId;
}
