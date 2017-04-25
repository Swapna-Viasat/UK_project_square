package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 2/28/2017.
 */
public class WorkerSummary implements Serializable {
    public int id;
    @Nullable
    @SerializedName("role") public String role;
    @Nullable
    @SerializedName("picture") public String picture;
    @Nullable
    @SerializedName("name") public String name;

    public WorkerSummary(int id, String role, String name, String picture) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.picture = picture;
    }
}
