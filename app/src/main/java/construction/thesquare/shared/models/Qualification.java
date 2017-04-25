package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class Qualification implements Serializable {
    public int id;
    public String name;
    public String description;
    @SerializedName("construction_specific") public boolean constructionSpecific;
    @SerializedName("dedicated_model") public boolean dedicatedModel;
    @SerializedName("dedicated_model_name") public String dedicatedModelName;
    public int order;
    @SerializedName("on_experience") public boolean onExperience;
    public int weight;

    public boolean selected;

    public Qualification() {
    }

    public Qualification(ExperienceQualification input) {
        this.id = input.id;
        this.name = input.name;
        this.description = input.description;
        this.constructionSpecific = input.constructionSpecific;
        this.dedicatedModel = input.dedicatedModel;
        this.dedicatedModelName = input.dedicatedModelName;
        this.order = input.order;
        this.onExperience = input.onExperience;
        this.weight = input.weight;
    }
}
