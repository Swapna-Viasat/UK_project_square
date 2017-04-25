package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanmaggi on 4/5/16.
 */
public class SinglePreference implements Parcelable  {

    protected int id;
    protected String name;
    protected String description;
    protected boolean selected;

    public SinglePreference() {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int describeContents()  {
        return 0;
    }


    public String toString() {
        return getName();
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(description);
        //out.writeBoolean(selected);
    }

    private SinglePreference(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public SinglePreference createFromParcel(Parcel in) {
            return new SinglePreference(in);
        }

        public SinglePreference[] newArray(int size) {
            return new SinglePreference[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SinglePreference that = (SinglePreference) o;

        if (id != that.id) return false;
        if (selected != that.selected) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (selected ? 1 : 0);
        return result;
    }
}