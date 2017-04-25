package construction.thesquare.worker.signup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

import construction.thesquare.shared.data.model.Location;
import construction.thesquare.shared.data.model.Qualification;
import construction.thesquare.shared.data.model.Role;
import construction.thesquare.shared.data.model.SinglePreference;
import construction.thesquare.shared.data.model.User;

/**
 * Created by juanmaggi on 26/7/16.
 */
public class Worker extends User {
        //implements Parcelable {

    private String post_code;
    private String address;
    private Location location;
    private int commute_time;
    private int years_experience;
    private SinglePreference english_level;
    private int min_rate;
    private boolean available_now;
    private Role[] roles;
    private Qualification[] qualifications;
    private SinglePreference[] trades;
    private SinglePreference[] experience_types;
    private SinglePreference[] skills;
    private SinglePreference[] worked_companies;
    private SinglePreference[] related_roles_skills;
    private SinglePreference[] related_roles_qualifications;
    //cscs_card


    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public int getCommute_time() {
        return commute_time;
    }

    public int getYears_experience() {
        return years_experience;
    }

    public void setYears_experience(int years_experience) {
        this.years_experience = years_experience;
    }

    public SinglePreference getEnglish_level() {
        return english_level;
    }

    public void setEnglish_level(SinglePreference english_level) {
        this.english_level = english_level;
    }

    public int getMin_rate() {
        return min_rate;
    }

    public void setMin_rate(int min_rate) {
        this.min_rate = min_rate;
    }

    public boolean isAvailable_now() {
        return available_now;
    }

    public void setAvailable_now(boolean available_now) {
        this.available_now = available_now;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    public Qualification[] getQualifications() {
        return qualifications;
    }

    public void setQualifications(Qualification[] qualifications) {
        this.qualifications = qualifications;
    }

    public SinglePreference[] getTrades() {
        return trades;
    }

    public SinglePreference[] getExperience_types() {
        return experience_types;
    }

    public SinglePreference[] getSkills() {
        return skills;
    }

    public void setSkills(SinglePreference[] skills) {
        this.skills = skills;
    }

    public SinglePreference[] getWorked_companies() {
        return worked_companies;
    }

    public void setWorked_companies(SinglePreference[] worked_companies) {
        this.worked_companies = worked_companies;
    }

    public SinglePreference[] getRelated_roles_skills() {
        return related_roles_skills;
    }

    public void setRelated_roles_skills(SinglePreference[] related_roles_skills) {
        this.related_roles_skills = related_roles_skills;
    }

    public SinglePreference[] getRelated_roles_qualifications() {
        return related_roles_qualifications;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(first_name);
        out.writeString(last_name);
        out.writeString(picture);
        out.writeString(email);
        out.writeByte((byte) (onboarding_done ? 1 : 0));
        out.writeByte((byte) (onboarding_skipped ? 1 : 0));

        out.writeString(post_code);
        out.writeString(address);
        out.writeParcelable(location, 0);
        out.writeInt(commute_time);
        out.writeInt(years_experience);
        out.writeParcelable(english_level, 0);
        out.writeInt(min_rate);
        out.writeByte((byte) (available_now ? 1 : 0));
        out.writeParcelableArray(roles, 0);
        out.writeParcelableArray(qualifications, 0);
        out.writeParcelableArray(trades, 0);
        out.writeParcelableArray(experience_types, 0);
        out.writeParcelableArray(skills, 0);

        out.writeParcelableArray(worked_companies, 0);
        out.writeParcelableArray(related_roles_skills, 0);
        out.writeParcelableArray(related_roles_qualifications, 0);
    }

    private Worker(Parcel in) {
        Parcelable[] parcelableArray;
        id = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        picture = in.readString();
        email = in.readString();
        onboarding_done = in.readByte() != 0;
        onboarding_skipped = in.readByte() != 0;

        post_code = in.readString();
        address = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        commute_time = in.readInt();
        years_experience = in.readInt();
        english_level = in.readParcelable(SinglePreference.class.getClassLoader());
        min_rate = in.readInt();
        available_now = in.readByte() != 0;

        parcelableArray = in.readParcelableArray(Role.class.getClassLoader());
        if (parcelableArray != null)
           roles = Arrays.copyOf(parcelableArray, parcelableArray.length, Role[].class);

        parcelableArray = in.readParcelableArray(Qualification.class.getClassLoader());
        if (parcelableArray != null)
           qualifications = Arrays.copyOf(parcelableArray, parcelableArray.length, Qualification[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
            trades = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
            experience_types = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
           skills = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
           worked_companies = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
           related_roles_skills = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);

        parcelableArray = in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
            related_roles_qualifications = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);
    }

    public static final Parcelable.Creator<Worker> CREATOR = new Parcelable.Creator<Worker>() {

        public Worker createFromParcel(Parcel in) {
            return new Worker(in);
        }

        public Worker[] newArray(int size) {
            return new Worker[size];
        }
    };

}
