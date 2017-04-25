/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationPreference implements Serializable {
    public int id;
    @SerializedName("display_name")
    public String name;
}
