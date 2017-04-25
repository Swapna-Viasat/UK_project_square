package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gherg on 12/6/2016.
 */

public class ExperienceQualification implements Serializable {
    public int id;
    @Nullable
    public String name;
    @Nullable
    public String description;
    @SerializedName("construction_specific") public boolean constructionSpecific;
    @SerializedName("dedicated_model") public boolean dedicatedModel;
    @SerializedName("dedicated_model_name") public String dedicatedModelName;
    public int order;
    @SerializedName("on_experience") public boolean onExperience;
    public int weight;

    public boolean selected;
}
