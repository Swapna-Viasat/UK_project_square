package construction.thesquare.shared.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Locale;

/**
 * Created by maizaga on 5/11/16.
 *
 */

public class Invoice implements Parcelable {
    public enum InvoiceStatus {APPROVED, PAID}

    private Timesheet timesheet;
    private InvoiceStatus status;
    private Date paidDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(timesheet, 0);
        out.writeInt(status.ordinal());
        out.writeLong(paidDate == null ? 0 : paidDate.getTime());
    }

    public Invoice(Timesheet timesheet, InvoiceStatus status, Date paidDate) {
        this.timesheet = timesheet;
        this.status = status;
        this.paidDate = paidDate;
    }

    private Invoice(Parcel in) {
        timesheet = in.readParcelable(Timesheet.class.getClassLoader());
        int status = in.readInt();
        switch (status) {
            case 0:
                this.status = InvoiceStatus.APPROVED;
                break;
            default:
                this.status = InvoiceStatus.PAID;
        }
        long paidLong = in.readLong();
        if (paidLong == 0) paidDate = null;
        else paidDate = new Date(paidLong);
    }

    public static final Parcelable.Creator<Invoice> CREATOR = new Parcelable.Creator<Invoice>() {
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public String getDaysWorkedAmout() {
        int hoursWorked = 0;
        int overtime = 0;
        for (TimesheetUnit timesheetUnit : getTimesheet().getTimesheetUnits()) {
            hoursWorked += timesheetUnit.getHoursWorked();
            overtime += timesheetUnit.getOvertime();
        }
        int daysWorked = hoursWorked / 8;
        int hoursLeft = hoursWorked % 8;

        return String.format(Locale.UK, "%d days and %d hours, %d overtime", daysWorked, hoursLeft, overtime);
    }
}
