package construction.thesquare.worker.jobmatches.model;

import java.io.Serializable;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class ApplicationStatus implements Serializable {
    public static int STATUS_PENDING = 1;
    public static int STATUS_APPROVED = 2;
    public static int STATUS_DENIED = 3;
    public static int STATUS_CANCELLED = 4;
    public static int STATUS_END_CONTRACT = 5;

    public String name;
    public int id;
}
