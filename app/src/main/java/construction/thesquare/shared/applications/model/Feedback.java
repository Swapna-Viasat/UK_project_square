package construction.thesquare.shared.applications.model;

import java.io.Serializable;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 The Square Tech. All rights reserved.
 */

public class Feedback implements Serializable {
    public String feedback;

    public Feedback(String feedback) {
        this.feedback = feedback;
    }
}
