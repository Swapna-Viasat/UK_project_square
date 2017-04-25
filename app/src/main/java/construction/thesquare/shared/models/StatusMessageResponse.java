package construction.thesquare.shared.models;

import java.io.Serializable;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class StatusMessageResponse implements Serializable {

    public MessageResponse response;

    public class MessageResponse {
        public String message;
    }
}
