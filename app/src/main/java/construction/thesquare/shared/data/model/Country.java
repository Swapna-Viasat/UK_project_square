package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanmaggi on 29/6/16.
 */
public class Country implements Parcelable {

    private int id;
    private String name;
    private String short_code;
    private String phone_prefix;
    private String flag;

    public Country() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_code() {
        return short_code;
    }

    public void setShort_code(String short_code) {
        this.short_code = short_code;
    }

    public String getPhone_prefix() {
        return phone_prefix;
    }

    public void setPhone_prefix(String phone_prefix) {
        this.phone_prefix = phone_prefix;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int describeContents()  {
        return 0;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(short_code);
        out.writeString(phone_prefix);
        out.writeString(flag);
    }

    private Country(Parcel in) {
        id = in.readInt();
        name = in.readString();
        short_code = in.readString();
        phone_prefix = in.readString();
        flag = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };


}
