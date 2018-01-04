package com.github.novskey.novabot.core;

import java.util.ResourceBundle;

/**
 * Created by Owner on 14/05/2017.
 */
public enum TimeUnit {
    Seconds,
    Minutes,
    Hours,
    Days,
    Weeks,
    Months,
    Years;

    private static ResourceBundle bundle;

    public static void SetBundle (ResourceBundle bundle){
        TimeUnit.bundle = bundle;
    }

    public static TimeUnit fromString(String str) {
        if (str.equals(bundle.getString("second")) || str.equals(bundle.getString("seconds")))
            return Seconds;

        if (str.equals(bundle.getString("minute")) || str.equals(bundle.getString("minutes")))
            return Minutes;

        if (str.equals(bundle.getString("hour")) || str.equals(bundle.getString("hours")))
            return Hours;

        if (str.equals(bundle.getString("day")) || str.equals(bundle.getString("days")))
            return Days;

        if (str.equals(bundle.getString("week")) || str.equals(bundle.getString("weeks")))
            return Weeks;

        if (str.equals(bundle.getString("month")) || str.equals(bundle.getString("months")))
            return Months;

        if (str.equals(bundle.getString("year")) || str.equals(bundle.getString("years")))
            return Years;

        return null;
    }

    public String toDbString() {
        switch (this) {
            case Seconds:
                return "SECOND";
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
            case Years:
                return "YEAR";
        }
        return null;
    }
}
