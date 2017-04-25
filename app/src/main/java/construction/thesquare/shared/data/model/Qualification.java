package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanmaggi on 27/7/16.
 */
public class Qualification extends SinglePreference {

    private boolean construction_specific;

    public Qualification() {

    }

    public int describeContents()  {
        return 0;
    }

    public String toString() {
        return getName();
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeByte((byte) (construction_specific ? 1 : 0));
    }

    private Qualification(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        construction_specific = in.readByte() != 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Qualification createFromParcel(Parcel in) {
            return new Qualification(in);
        }

        public Qualification[] newArray(int size) {
            return new Qualification[size];
        }
    };


}
