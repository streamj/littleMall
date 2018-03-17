package cc.freecloudfx.littlemall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author StReaM on 3/10/2018
 */
public class DateTimeUtil {
    // joda-Time
    public static final String STANDRAD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // strToDate
    public static Date strToDate(String dateTimeStr, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    // DateToStr
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    // strToDate overload
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDRAD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    // DateToStr overload
    public static String dateToStr(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDRAD_FORMAT);
    }

    public static void main(String[] args) {

        System.out.println(DateTimeUtil.dateToStr(new Date(), STANDRAD_FORMAT));
        System.out.println(DateTimeUtil.strToDate("2010-01-31 19:09:33", STANDRAD_FORMAT));
    }
}
