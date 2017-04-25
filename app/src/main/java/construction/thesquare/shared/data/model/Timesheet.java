package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by maizaga on 2/11/16.
 */

public class Timesheet implements Parcelable {

    public enum TimesheetStatus {DUE, AWAITING, APPROVED}

    private long id;
    private String companyLogo; // TODO Get from company data
    private String companyName; // TODO Get from company data
    private float hourRate; // TODO Get from worker data
    private Date from;
    private Date to;
    private TimesheetStatus status;
    private boolean rejected;
    private String rejectedReason;
    private TimesheetUnit[] timesheetUnits;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(companyLogo);
        out.writeString(companyName);
        out.writeFloat(hourRate);
        out.writeLong(from.getTime());
        out.writeLong(to.getTime());
        out.writeInt(status.ordinal());
        out.writeInt(rejected ? 1 : 0);
        out.writeString(rejectedReason);
        out.writeParcelableArray(timesheetUnits, 0);
    }

    // TODO Verify if needed after having endpoint (currently mocking)
    public Timesheet(long id, String companyName, Date from, Date to, TimesheetStatus status, boolean rejected, String rejectedReason) {
        this(id, companyName, from, to, status, rejected, rejectedReason, null);
    }

    public Timesheet(long id, String companyName, Date from, Date to, TimesheetStatus status, boolean rejected, String rejectedReason, TimesheetUnit[] timesheetUnits) {
        this.id = id;
        this.companyLogo = "";
        this.companyName = companyName;
        this.hourRate = 50.0f;
        this.from = from;
        this.to = to;
        this.status = status;
        this.rejected = rejected;
        this.rejectedReason = rejectedReason;
        if (timesheetUnits == null) createTimesheetUnits();
        else this.timesheetUnits = timesheetUnits;
    }

    private void createTimesheetUnits() {
        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);
        end.add(Calendar.DATE, 1); // We want to consider the end date too.
        List<TimesheetUnit> units = new ArrayList<>();

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            TimesheetUnit unit = new TimesheetUnit(0, 0, date);
            units.add(unit);
        }

        timesheetUnits = units.toArray(new TimesheetUnit[units.size()]);
    }

    private Timesheet(Parcel in) {
        id = in.readLong();
        companyLogo = in.readString();
        companyName = in.readString();
        hourRate = in.readFloat();
        from = new Date(in.readLong());
        to = new Date(in.readLong());
        int status = in.readInt();
        switch (status) {
            case 0:
                this.status = TimesheetStatus.DUE;
                break;
            case 1:
                this.status = TimesheetStatus.AWAITING;
                break;
            default:
                this.status = TimesheetStatus.APPROVED;
        }
        rejected = in.readInt() > 0;
        rejectedReason = in.readString();

        Parcelable[] parcelableArray = in.readParcelableArray(TimesheetUnit.class.getClassLoader());
        if (parcelableArray != null) timesheetUnits = Arrays.copyOf(parcelableArray, parcelableArray.length, TimesheetUnit[].class);
    }

    public static final Parcelable.Creator<Timesheet> CREATOR = new Parcelable.Creator<Timesheet>() {
        public Timesheet createFromParcel(Parcel in) {
            return new Timesheet(in);
        }

        public Timesheet[] newArray(int size) {
            return new Timesheet[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public float getHourRate() {
        return hourRate;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public void setStatus(TimesheetStatus status) {
        this.status = status;
    }

    public TimesheetStatus getStatus() {
        return status;
    }

    public boolean isRejected() {
        return rejected;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public TimesheetUnit[] getTimesheetUnits() {
        return timesheetUnits;
    }

    public float getResultIncome() {
        int hoursWorked = 0;
        int overtime = 0;

        for (TimesheetUnit timesheetUnit : getTimesheetUnits()) {
            hoursWorked += timesheetUnit.getHoursWorked();
            overtime += timesheetUnit.getOvertime();
        }

        // TODO Find out rate change when overtime. Hardcoding to 50%
        return hourRate * hoursWorked + hourRate * 1.5f * overtime;
    }
}
