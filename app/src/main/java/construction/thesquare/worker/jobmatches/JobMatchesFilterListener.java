package construction.thesquare.worker.jobmatches;

import construction.thesquare.worker.jobmatches.model.Ordering;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

public interface JobMatchesFilterListener {
    void onFilterSet(Ordering ordering, int commuteTime);
}
