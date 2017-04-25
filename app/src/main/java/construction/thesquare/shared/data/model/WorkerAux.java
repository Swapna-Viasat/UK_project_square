package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by juanmaggi on 12/9/16.
 */
public class WorkerAux implements Parcelable {

    private SinglePreference[] english_level;

    public SinglePreference[] getEnglish_level() {
        return english_level;
    }

    public void setEnglish_level(SinglePreference[] english_level) {
        this.english_level = english_level;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelableArray(english_level, 0);
    }

    public int describeContents()  {
        return 0;
    }

    private WorkerAux(Parcel in) {
       Parcelable[] parcelableArray;
       parcelableArray =
                in.readParcelableArray(SinglePreference.class.getClassLoader());
        if (parcelableArray != null)
            english_level = Arrays.copyOf(parcelableArray, parcelableArray.length, SinglePreference[].class);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public WorkerAux createFromParcel(Parcel in) {
            return new WorkerAux(in);
        }

        public WorkerAux[] newArray(int size) {
            return new WorkerAux[size];
        }
    };
}
