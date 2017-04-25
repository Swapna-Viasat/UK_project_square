package construction.thesquare.employer.createjob;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import construction.thesquare.shared.data.model.Location;
import construction.thesquare.shared.models.ExperienceType;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.Trade;

/**
 * Created by gherg on 12/12/2016.
 */

public class CreateRequest implements Serializable {

    public int id;
    public Calendar rawDate;
    public boolean detailsLowerPart;

    @SerializedName("is_connect") public boolean isConnect;
    @SerializedName("connect_email") public String connectEmail;
    public String name;
    public boolean isLive;

    public int status;
    public int role;
    public Role roleObject;
    public String roleName;
    public int experience;
    @SerializedName("workers_quantity") public int workersQuantity;
    // trades
    public int[] trades;
    public List<Trade> tradeObjects;
    public List<String> tradeStrings;
    // experience qualifications
    public int[] requirements;
    public List<Qualification> requirementObjects;
    public List<String> requirementStrings;
    // qualifications
    public int[] qualifications;
    public List<Qualification> qualificationObjects;
    public List<String> qualificationStrings;
    // experience types
    @SerializedName("experience_type") public int[] experienceTypes;
    public List<ExperienceType> experienceTypeObjects;
    public List<String> experienceTypeStrings;
    // skills
    public  int[] skills;
    public List<String> skillStrings;
    @SerializedName("english_level") public int english;
    public String englishLevelString;
    @SerializedName("budget_type") public int budgetType;
    public float budget;
    @SerializedName("pay_overtime") public boolean overtime;
    @SerializedName("pay_overtime_value") public float overtimeValue;
    @SerializedName("contact_name") public String contactName;
    @SerializedName("contact_phone") public String contactPhone;
    @SerializedName("contact_phone_number") public long contactPhoneNumber;
    @SerializedName("contact_country_code") public int contactCountryCode;

    public String num;
    public String cd;

    public String address;
    public String description;
    @SerializedName("extra_notes") public String notes;

    public String date;
    public String time;
    public String logo;

    @SerializedName("location_name") public String locationName;
    public Location location;
}