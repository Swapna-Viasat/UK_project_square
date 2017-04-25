package construction.thesquare.shared.utils;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import construction.thesquare.MainApplication;
import construction.thesquare.R;

/**
 * Created by juanmaggi on 27/9/16.
 */
public class DateUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private String[] months = {"January", "February", "March",
                                "April", "June", "July",
                                "August", "September", "October",
                                "November", "December"};

    public static String magicDate(String notMagicDate) {
        String magicDate = "";
        if (null != notMagicDate) {
            try {
                String[] parts = notMagicDate.split("-");
                if (parts.length == 3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(parts[2]);
                    stringBuilder.append("-");
                    stringBuilder.append(parts[1]);
                    stringBuilder.append("-");
                    stringBuilder.append(parts[0]);
                    magicDate = stringBuilder.toString();
                }
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
        if (magicDate.equals("")) {
            magicDate = notMagicDate;
        }
        return magicDate;
    }

    public static int[] extractDate(String strDate) {
        try {
            int[] result = new int[3];
            String year = strDate.substring(0, 4);
            String month = strDate.substring(5, 7);
            String day = strDate.substring(8, 10);
            result[0] = Integer.valueOf(strDate.substring(0, 4));
            result[1] = Integer.valueOf(strDate.substring(5, 7));
            result[2] = Integer.valueOf(strDate.substring(8, 10));
            return result;
        } catch (Exception e) {

        }
        return null;
    }

    public static Date stringToDate(String strDate) {
        //String dtStart = "2010-10-15T09:27:37Z";
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            //Date date = format.parse(strDate);
            Date date = format.parse(strDate.split("\\+")[0]);
            return date;
        } catch (ParseException e) {
            CrashLogHelper.logException(e);
            return null;
            // TODO Auto-generated catch block
            //CrashLogHelper.logException(e);
        }
    }

    public static String formatDateMonthDayAndTime(String strDate) {
        DateTime date = ISODateTimeFormat.dateTimeParser().parseDateTime(strDate);
        String stringMonth = date.toString(DateTimeFormat.forPattern("MMMM"));
        String day = date.toString(DateTimeFormat.forPattern("dd"));
        String time = date.toLocalTime().toString(DateTimeFormat.forPattern("h:mma"));
        return stringMonth + " " + day + getSuffixDayOfMonth(Integer.valueOf(day)) + " - " + time;
    }

    public static String formatDateDayAndMonth(String strDate, boolean shortStringMoth) {
        Date date = stringToDate(strDate);
        String maskStrMonth = (shortStringMoth) ? "MMM" : "MMMM";
        String stringMonth = (String) android.text.format.DateFormat.format(maskStrMonth, date); //Jun
        String day = (String) android.text.format.DateFormat.format("dd", date); //20
        return day + " " + getSuffixDayOfMonth(Integer.valueOf(day)) + " " + stringMonth;
    }

    public static String getFormattedJobDate(String strDate) {
        DateTime date = ISODateTimeFormat.dateTimeParser().parseDateTime(strDate);
        return date.toLocalDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
    }

    private static String getSuffixDayOfMonth(int dayOfMonth) {
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return "th";
        }
        switch (dayOfMonth % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String formatWeekFromDayToDay(Date from, Date to) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE dd.MM.yyyy", Locale.UK);

        return MainApplication.getAppContext().getString(R.string.days_worked_from_to, formatter.format(from), formatter.format(to));
    }

    public static String formatShortDayUpperCase(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE", Locale.UK);

        return formatter.format(date).toUpperCase(Locale.UK);
    }

    public static String formatDayOnly(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd", Locale.UK);

        return formatter.format(date);
    }

    public static String suffix(int day) {
        String result = "th";
        if (day == 1 || day == 21 || day == 31) {
            result = "st";
        } else if (day == 2 || day == 22) {
            result = "nd";
        } else if (day == 3 || day == 23) {
            result = "rd";
        }
        return result;
    }

    public static String monthShort(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May",
                "Jun", "Jul", "Aug", "Sep", "Oct",
                "Nov", "Dec"};
        return months[month];
    }

    public static String minute(int min) {
        StringBuilder stringBuilder = new StringBuilder();
        if (min < 10) {
            stringBuilder.append("0" + String.valueOf(min));
        } else {
            stringBuilder.append(String.valueOf(min));
        }
        return stringBuilder.toString();
    }

    public static String hour(int hour) {
        StringBuilder stringBuilder = new StringBuilder();
        if (hour < 10) {
            stringBuilder.append("0" + String.valueOf(hour));
        } else {
            stringBuilder.append(String.valueOf(hour));
        }
        return stringBuilder.toString();
    }

    public static String time(int hour, int min) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hour(hour));
        stringBuilder.append(":");
        stringBuilder.append(minute(min));
        return stringBuilder.toString();
    }

    public static String toPayloadDate(Calendar calendar) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(calendar.get(Calendar.YEAR));
        stringBuilder.append("-");
        stringBuilder.append(String.valueOf(calendar.get(Calendar.MONTH)+1));
        stringBuilder.append("-");
        stringBuilder.append(calendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append("T");
        stringBuilder.append(time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE)));
        return stringBuilder.toString();
    }

    public static String getParsedBirthDate(String birthDate) {
        if (TextUtils.isEmpty(birthDate)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            return formatter.parseLocalDate(birthDate).toString("d MMMM yyyy");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            return null;
        }
    }

    public static LocalDate getParsedLocalDate(String date) {
        if (TextUtils.isEmpty(date)) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            return formatter.parseLocalDate(date);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            return null;
        }
    }

    public static String getCscsExpirationDate(String date) {
        if (TextUtils.isEmpty(date)) return null;

        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            return formatter.parseLocalDate(date).toString("MMMM, yyyy");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
            return null;
        }
    }
}