package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by juanmaggi on 8/9/16.
 */
public class Role extends SinglePreference {

    private String image;
    private SinglePreference[] associated_skills;
    private Qualification[] associated_qualifications;
    private boolean has_trades;
    public int workerAmount;

    public Role() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public SinglePreference[] getAssociated_skills() {
        return associated_skills;
    }

    public void setAssociated_skills(SinglePreference[] associated_skills) {
        this.associated_skills = associated_skills;
    }

    public Qualification[] getAssociated_qualifications() {
        return associated_qualifications;
    }

    public void setAssociated_qualifications(Qualification[] associated_qualifications) {
        this.associated_qualifications = associated_qualifications;
    }

    public boolean isHas_trades() {
        return has_trades;
    }

    public void setHas_trades(boolean has_trades) {
        this.has_trades = has_trades;
    }

    public int describeContents()  {
        return 0;
    }

    public String toString() {
        return getName();
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(image);
        out.writeParcelableArray(associated_skills, 0);
        out.writeParcelableArray(associated_qualifications, 0);
        out.writeByte((byte) (has_trades ? 1 : 0));

    }

    private Role(Parcel in) {
        Parcelable[] parcelableArray;
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        //selected= in.readBo();
        image = in.readString();
        parcelableArray =
                in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
            associated_skills = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);
        parcelableArray =
                in.readParcelableArray(Qualification.class.getClassLoader());
        if (parcelableArray != null)
            associated_qualifications = Arrays.copyOf(parcelableArray, parcelableArray.length, Qualification[].class);
        has_trades = in.readByte() != 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

}
