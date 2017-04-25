package construction.thesquare.employer.signup.model;

import android.os.Parcel;
import android.os.Parcelable;

import construction.thesquare.shared.data.model.User;

/**
 * Created by juanmaggi on 22/7/16.
 */
public class Employer extends User {

    private String identifier;
    private String last_login;
    private boolean phone_confirmed;
    private boolean valid_company_employer;
    private String job_title;
    private Company company;
    private int reviews_avg;
    private int reviews_amount;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public boolean isPhone_confirmed() {
        return phone_confirmed;
    }

    public void setPhone_confirmed(boolean phone_confirmed) {
        this.phone_confirmed = phone_confirmed;
    }

    public boolean isValid_company_employer() {
        return valid_company_employer;
    }

    public void setValid_company_employer(boolean valid_company_employer) {
        this.valid_company_employer = valid_company_employer;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getReviews_avg() {
        return reviews_avg;
    }

    public int getReviews_amount() {
        return reviews_amount;
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

        out.writeString(identifier);
        out.writeString(last_login);
        out.writeByte((byte) (phone_confirmed ? 1 : 0));
        out.writeByte((byte) (valid_company_employer ? 1 : 0));
        out.writeString(job_title);
        out.writeParcelable(company, 0);
        out.writeInt(reviews_amount);
        out.writeInt(reviews_avg);
    }

    private Employer(Parcel in) {
        id = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        picture = in.readString();
        email = in.readString();
        onboarding_done = in.readByte() != 0;
        onboarding_skipped = in.readByte() != 0;

        identifier = in.readString();
        last_login = in.readString();
        phone_confirmed =  in.readByte() != 0;
        valid_company_employer = in.readByte() != 0;
        job_title = in.readString();
        company = in.readParcelable(Company.class.getClassLoader());
        reviews_amount = in.readInt();
        reviews_avg = in.readInt();
    }

    public static final Parcelable.Creator<Employer> CREATOR = new Parcelable.Creator<Employer>() {
        public Employer createFromParcel(Parcel in) {
            return new Employer(in);
        }

        public Employer[] newArray(int size) {
            return new Employer[size];
        }
    };

}
