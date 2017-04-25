/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.shared.models;

import java.io.Serializable;
import java.util.List;

public class RolesRequest implements Serializable {
    private List<Integer> roles;

    public RolesRequest(List<Integer> roles) {
        this.roles = roles;
    }
}
