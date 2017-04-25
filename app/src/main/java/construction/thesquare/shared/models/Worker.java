package construction.thesquare.shared.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import construction.thesquare.shared.data.model.Location;

/**
 * Created by gherg on 12/6/2016.
 */

public class Worker implements Serializable {
    public int id;
    @Nullable
    public String identifier;
    @Nullable
    @SerializedName("user_type")
    public int userType;
    @Nullable
    public String picture;
    @Nullable
    @SerializedName("post_code")
    public String zip;
    @SerializedName("onboarding_done")
    public boolean onboardingDone;
    @Nullable
    @SerializedName("first_name")
    public String firstName;
    @Nullable
    @SerializedName("last_name")
    public String lastName;
    @Nullable
    public String email;
    @Nullable
    @SerializedName("english_levels")
    public List<EnglishLevel> englishLevels;
    @SerializedName("reviews_avg")
    public float rating;
    @SerializedName("reviews_amount")
    public int numReviews;
    @SerializedName("available_now")
    public boolean now;
    @Nullable
    public List<Role> roles;
    @Nullable
    public List<Trade> trades;
    @SerializedName("years_experience")
    public int yearsExperience;
    @Nullable
    public List<Qualification> qualifications;
    @Nullable
    public List<Skill> skills;
    @Nullable
    @SerializedName("worked_companies")
    public List<Company> companies;
    @Nullable
    public Location location;
    @SerializedName("onboarding_skipped")
    public boolean onboardingSkipped;
    @SerializedName("new_job_matches_notifications")
    public boolean newJobMatchesNotifications;
    @SerializedName("job_offers_notifications")
    public boolean jobOffersNotifications;
    @SerializedName("job_booking_declines_notifications")
    public boolean jobBookingDeclinesNotifications;
    @SerializedName("review_notifications")
    public boolean reviewNotifications;
    @Nullable
    public String address;
    @Nullable
    public String bio;
    @Nullable
    @SerializedName("english_level")
    public EnglishLevel englishLevel;
    @Nullable
    @SerializedName("experience_types")
    public List<ExperienceType> experienceTypes;
    @SerializedName("commute_time")
    public int commuteTime;
    @SerializedName("filter_commute_time")
    public int filterCommuteTime;
    @Nullable
    @SerializedName("passport_upload")
    public String passportUpload;
    @Nullable
    @SerializedName("date_of_birth")
    public String dateOfBirth;
    @Nullable
    public List<Application> applications;
    @Nullable
    @SerializedName("matched_role")
    public Role matchedRole;
    @Nullable
    public Nationality nationality;
    @Nullable
    @SerializedName("ni_number")
    public String niNumber;
    @Nullable
    public List<Language> languages;
    @Nullable
    @SerializedName("review_data")
    public ReviewData reviewData;
    @Nullable
    public List<Device> devices;
    public boolean liked;

    public class Device implements Serializable {
        @Nullable
        @SerializedName("phone_number")
        public String phoneNumber;
    }
}