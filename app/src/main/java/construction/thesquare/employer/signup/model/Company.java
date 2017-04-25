package construction.thesquare.employer.signup.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanmaggi on 31/8/16.
 */
public class Company implements Parcelable {


    private int id;
    private int owner;
    private String worked_company;
    private boolean verified;
    private String registration_number;
    private String contact_first_name;
    private String contact_last_name;
    private String contact_phone;
    private String contact_email;
    private String name;
    private String logo;
    private String post_code;
    private String description;
    private boolean worker_payments;
    private boolean workers_submit_timesheets;
    private boolean pay_workers_directly;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getWorked_company() {
        return worked_company;
    }

    public void setWorked_company(String worked_company) {
        this.worked_company = worked_company;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getContact_first_name() {
        return contact_first_name;
    }

    public void setContact_first_name(String contact_first_name) {
        this.contact_first_name = contact_first_name;
    }

    public String getContact_last_name() {
        return contact_last_name;
    }

    public void setContact_last_name(String contact_last_name) {
        this.contact_last_name = contact_last_name;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWorker_payments() {
        return worker_payments;
    }

    public void setWorker_payments(boolean worker_payments) {
        this.worker_payments = worker_payments;
    }

    public boolean isWorkers_submit_timesheets() {
        return workers_submit_timesheets;
    }

    public void setWorkers_submit_timesheets(boolean workers_submit_timesheets) {
        this.workers_submit_timesheets = workers_submit_timesheets;
    }

    public boolean isPay_workers_directly() {
        return pay_workers_directly;
    }

    public void setPay_workers_directly(boolean pay_workers_directly) {
        this.pay_workers_directly = pay_workers_directly;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(owner);
        out.writeString(worked_company);
        out.writeByte((byte) (verified ? 1 : 0));
        out.writeString(registration_number);
        out.writeString(contact_first_name);
        out.writeString(contact_last_name);
        out.writeString(contact_phone);
        out.writeString(contact_email);
        out.writeString(name);
        out.writeString(logo);
        out.writeString(post_code);
        out.writeString(description);
        out.writeByte((byte) (worker_payments ? 1 : 0));
        out.writeByte((byte) (workers_submit_timesheets ? 1 : 0));
        out.writeByte((byte) (pay_workers_directly ? 1 : 0));
    }

    private Company(Parcel in) {
        Parcelable[] parcelableArray;
        id = in.readInt();
        owner = in.readInt();
        worked_company = in.readString();
        verified = in.readByte() != 0;
        registration_number = in.readString();
        contact_first_name = in.readString();
        contact_last_name = in.readString();
        contact_phone = in.readString();
        contact_email = in.readString();
        name = in.readString();
        logo = in.readString();
        post_code = in.readString();
        description = in.readString();
        worker_payments = in.readByte() != 0;
        workers_submit_timesheets = in.readByte() != 0;
        pay_workers_directly = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {

        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
