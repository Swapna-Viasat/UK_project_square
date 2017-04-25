package construction.thesquare.shared.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by swapna on 3/3/2017.
 */
public class ReviewData implements Serializable {
    @SerializedName("quality")
    public int quality;
    @SerializedName("reliability")
    public int reliability;
    @SerializedName("attitude")
    public int attitude;
    @SerializedName("safe")
    public int safe;
    @SerializedName("would_work_total")
    public int wouldWorkTotal;
    @SerializedName("showed_to_work_total")
    public int showedToWorkTotal;
    @SerializedName("reviews_count")
    public int reviewsCount;
    @SerializedName("environment")
    public int environment;
    @SerializedName("team")
    public int team;
    @SerializedName("payers")
    public int payers;
    @SerializedName("induction")
    public int induction;

    public ReviewData(int environment, int team, int payers, int induction, int globalRating) {
        this.environment = environment;
        this.team = team;
        this.payers = payers;
        this.induction = induction;
        this.globalRating = globalRating;
    }


    public ReviewData(int quality, int reliability, int attitude, int safe, int wouldWorkTotal, int showedToWorkTotal, int reviewsCount, int globalRating) {
        this.quality = quality;
        this.reliability = reliability;
        this.attitude = attitude;
        this.safe = safe;
        this.wouldWorkTotal = wouldWorkTotal;
        this.showedToWorkTotal = showedToWorkTotal;
        this.reviewsCount = reviewsCount;
        this.globalRating = globalRating;
    }

    @SerializedName("global_rating")
    public int globalRating;
}
