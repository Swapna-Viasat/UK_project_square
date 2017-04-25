package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gherg on 12/6/2016.
 */

public class Role implements Serializable {
    public int id;
    @SerializedName("is_apprentice") public boolean isApprentice;
    public String name;
    public String description;
    public String image;
    @SerializedName("has_trades") public boolean hasTrades;
    @SerializedName("associated_skills") public List<Skill> associatedSkills;
    @SerializedName("associated_qualifications") public List<Qualification> qualifications;

    public boolean selected;
    public int amountWorkers;
    public boolean filtered;
}
