package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import construction.thesquare.shared.utils.DateUtils;

/**
 * Created by maizaga on 13/11/16.
 *
 */

public class TimesheetUnit implements Parcelable {
    private int hoursWorked;
    private int overtime;
    private Date day;

    public TimesheetUnit(int hoursWorked, int overtime, Date day) {
        this.hoursWorked = hoursWorked;
        this.overtime = overtime;
        this.day = day;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public int getOvertime() {
        return overtime;
    }

    public Date getDay() {
        return day;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getShortDayText() {
        return DateUtils.formatShortDayUpperCase(day);
    }

    public String getDayText() {
        return DateUtils.formatDayOnly(day);
    }

    public boolean hasWorkedHours() {
        return hoursWorked > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(hoursWorked);
        out.writeInt(overtime);
        out.writeLong(day.getTime());
    }

    private TimesheetUnit(Parcel in) {
        hoursWorked = in.readInt();
        overtime = in.readInt();
        day = new Date(in.readLong());
    }

    public static final Parcelable.Creator<TimesheetUnit> CREATOR = new Parcelable.Creator<TimesheetUnit>() {
        @Override
        public TimesheetUnit createFromParcel(Parcel parcel) {
            return new TimesheetUnit(parcel);
        }

        @Override
        public TimesheetUnit[] newArray(int size) {
            return new TimesheetUnit[size];
        }
    };
}
