package core;

/**
 * Created by Owner on 14/05/2017.
 */
public enum TimeUnit {
    Minutes,
    Hours,
    Days,
    Weeks,
    Months;

    @Override
    public String toString() {
        switch(this){
            case Minutes:
                return "MINUTE";
            case Hours:
                return "HOUR";
            case Days:
                return "DAY";
            case Weeks:
                return "WEEK";
            case Months:
                return "MONTH";
        }
        return null;
    }

    public static TimeUnit fromString(String str){

        if(str.equals("minute") || str.equals("minutes"))
            return Minutes;

        if(str.equals("hour") || str.equals("hours"))
            return Hours;

        if(str.equals("day") || str.equals("days"))
            return Days;

        if(str.equals("week") || str.equals("weeks"))
            return Weeks;

        if(str.equals("month") || str.equals("months"))
            return Months;

        return null;
    }
}
