package construction.thesquare.worker.jobmatches.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import construction.thesquare.shared.models.ExperienceType;

/**
 * Created by Evgheni on 10/28/2016.
 */

public class Job implements Serializable {

    public static final int TYPE_BOOKED = 345;
    public static final int TYPE_OFFER = 346;
    public static final int TYPE_APP = 347;
    public static final int TYPE_LIKED = 348;
    public static final int TYPE_COMPLETED = 349;
    public static final int TYPE_OLD = 350;

    public boolean liked;
    public int id;
    public Bitmap logoBitmap;
    @SerializedName("start_datetime")
    public String startTime;
    @SerializedName("workers_quantity")
    public int workersQuantity;
    @SerializedName("is_apprenticeship")
    public boolean isApprenticeship;
    @SerializedName("available_now")
    public boolean availableNow;
    @SerializedName("english_level")
    public int englishLevel;
    public int experience;
    public String description;
    public String budget;
    @SerializedName("budget_type")
    public BudgetType budgetType;
    @SerializedName("pay_overtime")
    public boolean payOvertime;
    @SerializedName("pay_overtime_value")
    public float payOvertimeValue;
    @SerializedName("contact_name")
    public String contactName;
    @SerializedName("contact_phone")
    public String contactPhone;
    public String address;
    @SerializedName("extra_notes")
    public String extraNotes;
    @SerializedName("job_type")
    public int jobType;
    @SerializedName("application_status")
    public ApplicationStatus appStatus;
    public List<Qualification> qualifications;
    public List<Skill> skills;
    @SerializedName("experience_type") public List<ExperienceType> experienceTypes;
    public Company company;
    public Role role;
    public Location location;
    @SerializedName("location_name") public String locationName;
    public List<Application> application;
    @SerializedName("job_ref")
    public String jobRef;
    public Owner owner;
    public Status status;
    @SerializedName("is_connect")
    public boolean isConnect;
    @SerializedName("connect_email")
    public String connectEmail;

    public List<String> getSkillsList() {
        List<String> result = new ArrayList<>();
        if (skills != null) {
            for (Job.Skill skill : skills) {
                result.add(skill.name);
            }
        }
        return result;
    }

    public List<String> getExperienceTypesList() {
        List<String> result = new ArrayList<>();
        if (experienceTypes != null) {
            for (ExperienceType experienceType : experienceTypes) {
                result.add(experienceType.name);
            }
        }
        return result;
    }

    public List<String> getQualificationsList() {
        List<String> result = new ArrayList<>();
        if (qualifications != null) {
            for (Job.Qualification qualification : qualifications) {
                if (!qualification.onExperience) {
                    result.add(qualification.name);
                }
            }
        }
        return result;
    }

    public List<String> getRequirementsList() {
        List<String> result = new ArrayList<>();
        if (qualifications != null) {
            for (Job.Qualification qualification : qualifications) {
                if (qualification.onExperience) {
                    result.add(qualification.name);
                }
            }
        }
        return result;
    }

    public class Qualification implements Serializable {
        public String name;
        public int id;
        @SerializedName("on_experience") public boolean onExperience;
    }

    public class Skill implements Serializable {
        public String name;
        public int id;
    }

    public class BudgetType implements Serializable {
        public String name;
        public int id;
    }

    public class Location implements Serializable {
        public String latitude;
        public String longitude;
    }

    public class Company implements Serializable {
        public int id;
        public int owner;
        public String name;
        public String logo;
        @SerializedName("post_code") public String postCode;
        @SerializedName("address_line1")
        public String addressFirstLine;
        @SerializedName("address_line2")
        public String addressSecondLine;
    }

    public class Role implements Serializable {
        public String name;
        public String description;
        public String image;
        public int id;
    }

    public class Owner implements Serializable {
        public String picture;
    }

    public class Status implements Serializable {
        public int id;
        public String name;
    }
}
