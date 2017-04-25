package construction.thesquare.employer.mygraftrs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Evgheni on 10/21/2016.
 */

public class Worker implements Serializable {

    public static final int STATUS_APPLIED = 11;
    public static final int STATUS_BOOKED = 12;
    public static final int STATUS_DECLINED = 13;
    public static final int STATUS_OFFERED = 14;
    public static final int STATUS_PREVIOUS = 15;

    public int id;
    public String identifier;
    @SerializedName("user_type") public int userType;
    @SerializedName("onboarding_done") public boolean onboardingDone;
    public String name;
    @SerializedName("first_name") public String firstName;
    @SerializedName("last_name") public String lastName;
    public String ocupation;
    public int rating;
    public String availability;
    public boolean liked;
    public List<Device> devices;
    public List<Role> roles;
    public List<Qualification> qualifications;
    public List<Skill> skills;
    public Status status;
    @SerializedName("worked_companies") public List<Company> workedCompanies;
    @SerializedName("english_level") public EnglishLevel englishLevel;
    @SerializedName("years_experience") public int yearsExperience;
    @SerializedName("available_now") public boolean availableNow;
    @SerializedName("min_rate") public int minRate;
    public String picture;

    public class EnglishLevel implements Serializable {
        public int id;
        public String name;
    }
    public class Company implements Serializable {
        public int id;
        public String name;
        public String description;
    }
    public class Skill implements Serializable {
        public int id;
        public String name;
    }
    public class Qualification implements Serializable {
        public int id;
        public String name;
        public String description;
        @SerializedName("construction_specific") public boolean constructionSpecific;
        @SerializedName("dedicated_model") public boolean dedicatedModel;
        @SerializedName("dedicated_model_name") public String dedicatedModelName;
        public int order;
    }
    public class Role implements Serializable {
        public int id;
        public String name;
        public String description;
        public String image;
    }
    public class Device implements Serializable {
        public int id;
        @SerializedName("country_code") public String countryCode;
        @SerializedName("phone_number") public String phoneNumber;
        public String phone;
        @SerializedName("device_type") public DeviceType deviceType;
        @SerializedName("verified_number") public boolean verifiedNumber;
        public class DeviceType implements Serializable {
            public int id;
            public String name;
        }
    }
    public class Status implements Serializable {
        public int id;
        public String name;
    }
}
