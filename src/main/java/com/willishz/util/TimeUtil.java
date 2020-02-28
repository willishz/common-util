package com.willishz.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    private static Log log = LogFactory.getLog(TimeUtil.class);
    
    public static final String FMT_YYYY_MM_DD_HH24_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    public static final String FMT_YYYY_MM_DD = "yyyy-MM-dd";
    
    public static final String FMT_YYYYMMDD = "yyyyMMdd";
    
    public static final String FMT_YYYY_MM_DD_CN = "yyyy年MM月dd日";

    private TimeUtil() {
    }

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN_DAY = "yyyy-MM-dd";
    public static final String DATE_PATTERN_MONTH = "yyyy-MM";
    public static final String DAY_EARLY_TIME = " 00:00:00";
    public static final String DAY_LAST_TIME = " 23:59:59";
    public static final String DATE_PATTERN_MINUTE_CHINESE = "MM月dd日 HH:mm";
    public static final String DATE_TIME_PATTERN_ESCROW = "yyyyMMddHHmmss";
    public static final String DATE_TIME_PATTERN_ESCROW_8 = "yyyyMMdd";

    public static int getMaxDayOfLastMonth() {
        Date now = new Date();
        Date lastMonth = DateUtils.addMonths(now, -1);
        lastMonth = getMaxDateOfMonth(lastMonth);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastMonth);
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        return maxDay;
    }

    public static int getYearOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int yearOfLastMonth = calendar.get(Calendar.YEAR);
        return yearOfLastMonth;
    }

    public static int getMonthOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int lastMonth = calendar.get(Calendar.MONTH) + 1;
        return lastMonth;
    }

    public static Date addMonths(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, i);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }

    public static int getCurrentStatYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    // 获取当前月份，cal.get(Calendar.MONTH)是从零开始。
    public static int getCurrentStatMonth() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        return month;
    }

    public static int getCurrentStatDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 时间校验: 开始时间不能大于当前时间.
     */
    public static Date validateStartDate(Date startDate) {
        Date today = new Date();
        // 开始时间不能大于当前时间.
        if (startDate.compareTo(today) == 1) {
            log.warn("startDate.compareTo(today)==1, set startDate = today:" + today);
            startDate = today;
        }
        return startDate;
    }

    /**
     * 时间校验: 不能晚于当前时间(如果晚于当前时间，则替换为当前时间)
     */
    public static Date notAfterNow(Date myDate) {
        Date today = new Date();
        if (myDate.after(today)) {
            log.warn("myDate.after(today), set myDate = today:" + today);
            myDate = today;
        }
        return myDate;
    }

    /**
     * 时间校验: 不能晚于昨天(如果晚于昨天，则替换为昨天)
     */
    public static Date notAfterYesterday(Date myDate) {
        Date today = new Date();
        Date yesterday = DateUtils.addDays(today, -1);
        ;
        // 3. 结束时间不能大于昨天.
        if (myDate.after(yesterday)) {
            log.warn("myDate.after(yesterday), set myDate = yesterday:" + yesterday);
            myDate = yesterday;
        }
        return myDate;
    }

    /**
     * 时间校验: 不能晚于上一个月(如果晚于上一个月，则替换为上一个月)
     */
    public static Date notAfterLastMonth(Date myDate) {
        Date today = new Date();
        Date lastMonth = DateUtils.addMonths(today, -1);
        lastMonth = TimeUtil.getMaxDateOfMonth(lastMonth);
        // 3. 结束时间不能大于上一个月.
        if (myDate.after(lastMonth)) {
            log.warn("myDate.after(lastMonth), set myDate = lastMonth:" + lastMonth);
            myDate = lastMonth;
        }
        return myDate;
    }

    /**
     * 时间校验: 不能晚于上一年(如果晚于上一年，则替换为上一年)
     */
    public static Date notAfterLastYear(Date myDate) {
        Date today = new Date();
        Date lastYear = DateUtils.addYears(today, -1);
        lastYear = TimeUtil.getMaxDateOfYear(lastYear);
        // 3. 结束时间不能大于上一年.
        if (myDate.after(lastYear)) {
            log.warn("myDate.after(lastYear), set myDate = lastYear:" + lastYear);
            myDate = lastYear;
        }
        return myDate;
    }

    /**
     * 时间校验: myDate不能早于basicDate(如果早于basicDate，则替换为basicDate)
     *
     * @throws Exception
     */
    public static Date notBefore(Date myDate, String basicStr) throws Exception {
        Date basicDate = TimeUtil.stringToDateTime(basicStr);
        // Date today = new Date();
        // Date yesterday = DateUtils.addDays(today, -1);;
        // 3. 结束时间不能大于昨天.
        if (myDate.before(basicDate)) {
            log.warn("myDate.before(basicDate), set myDate = basicDate:" + basicDate);
            myDate = basicDate;
        }
        return myDate;
    }

    /***
     * 将日期转化为字符串。 字符串格式("yyyy-MM-dd HH:mm:ss")。
     */
    public static String dateTime2String(Date date) {
        return dateToString(date, DATE_PATTERN_DEFAULT);
    }

    /***
     * 将日期转化为字符串。 字符串格式("yyyy-MM-dd")，小时、分、秒被忽略。
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATE_PATTERN_DAY);
    }

    /***
     * 将日期转化为字符串
     */
    public static String dateToString(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        String str = "";
        try {
            SimpleDateFormat formater = new SimpleDateFormat(pattern);
            str = formater.format(date);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将传入的年月日转化为Date类型
     */
    public static Date YmdToDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    /**
     * 将字符串转化为日期
     */
    public static Date stringToDateTime(String str) throws Exception {
        return getDateFormatOfDefault().parse(str);
    }

    /**
     * 将字符串转化为日期
     */
    public static Date stringToMediumDateTime(String str) throws ParseException {
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return format.parse(str);
    }

    /**
     * 获取默认的DateFormat
     */
    public static DateFormat getDateFormatOfDefault() {
        return new SimpleDateFormat(DATE_PATTERN_DEFAULT);
    }

    /**
     * 将字符串转化为日期。 字符串格式("YYYY-MM-DD")。
     * 例如："2012-07-01"或者"2012-7-1"或者"2012-7-01"或者"2012-07-1"是等价的。
     */
    public static Date stringToDate(String str, String pattern) {
        Date dateTime = null;
        try {
            SimpleDateFormat formater = new SimpleDateFormat(pattern);
            dateTime = formater.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 将字符串转化为日期(从一种格式到另一种格式)。
     */
    public static String StringPatternToPattern(String str, String pattern1, String pattern2) {
        Date dateTime = null;
        String productStr = "";
        try {
            if (!(str == null || "".equals(str))) {
                SimpleDateFormat formater = new SimpleDateFormat(pattern1);
                dateTime = formater.parse(str);

                SimpleDateFormat formater1 = new SimpleDateFormat(pattern2);
                productStr = formater1.format(dateTime);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productStr;
    }

    /**
     * 日期时间带时分秒的Timestamp表示
     */
    public static Timestamp stringToDateHMS(String str) {
        Timestamp time = null;
        try {
            time = java.sql.Timestamp.valueOf(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return time;

    }

    /**
     * 取得一个date对象对应的日期的0分0秒时刻的Date对象。
     */
    public static Date getMinDateOfHour(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    /**
     * 取得一个date对象对应的日期的0点0分0秒时刻的Date对象。
     */
    public static Date getMinDateOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getMinDateOfHour(date));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        return calendar.getTime();
    }

    /**
     * 取得一年中的最早一天。
     */
    public static Date getMinDateOfYear(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 取得一年中的最后一天
     */
    public static Date getMaxDateOfYear(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 取得一周中的最早一天。
     */
    public static Date getMinDateOfWeek(Date date, Locale locale) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));

        if (locale == null) {
            locale = Locale.CHINESE;
        }
        Date tmpDate = calendar.getTime();
        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            if (day_of_week == 1) {// 星期天
                tmpDate = DateUtils.addDays(tmpDate, -6);
            } else {
                tmpDate = DateUtils.addDays(tmpDate, 1);
            }
        }

        return tmpDate;
    }

    /**
     * 取得一周中的最后一天
     */
    public static Date getMaxDateOfWeek(Date date, Locale locale) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        if (locale == null) {
            locale = Locale.CHINESE;
        }
        Date tmpDate = calendar.getTime();
        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            if (day_of_week == 1) {// 星期天
                tmpDate = DateUtils.addDays(tmpDate, -6);
            } else {
                tmpDate = DateUtils.addDays(tmpDate, 1);
            }
        }

        return tmpDate;
    }

    /**
     * 取得一月中的最早一天。
     */
    public static Date getMinDateOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 取得一月中的最后一天
     */
    public static Date getMaxDateOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 取得一个date对象对应的日期的23点59分59秒时刻的Date对象。
     */
    public static Date getMaxDateOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 取得一个date对象对应的小时的59分59秒时刻的Date对象。
     */
    public static Date getMaxDateOfHour(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 获取2个时间相隔几秒
     */
    public static int getBetweenSecondNumber(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }

        if (startDate.after(endDate)) {
            Date tmp = endDate;
            endDate = startDate;
            startDate = tmp;
        }

        long timeNumber = -1L;
        long TIME = 1000L;
        try {
            timeNumber = (endDate.getTime() - startDate.getTime()) / TIME;

        } catch (Exception e) {
            log.error(e);
        }
        return (int) timeNumber;
    }

    /**
     * 获取2个时间相隔几分钟
     */
    public static int getBetweenMinuteNumber(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }

        if (startDate.after(endDate)) {
            Date tmp = endDate;
            endDate = startDate;
            startDate = tmp;
        }

        long timeNumber = -1L;
        long TIME = 60L * 1000L;
        try {
            timeNumber = (endDate.getTime() - startDate.getTime()) / TIME;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) timeNumber;
    }

    /**
     * 获取2个时间相隔几小时
     */
    public static int getBetweenHourNumber(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }

        if (startDate.after(endDate)) {
            Date tmp = endDate;
            endDate = startDate;
            startDate = tmp;
        }

        long timeNumber = -1L;
        long TIME = 60L * 60L * 1000L;
        try {
            timeNumber = (endDate.getTime() - startDate.getTime()) / TIME;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) timeNumber;
    }

    /**
     * 获取2个时间相隔几天(endDate+1s)
     * "2010-08-01 00:00:00 --- 2010-08-03 23:59:59"算三天
     */
    public static int getBetweenDayNumber(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }

        if (startDate.after(endDate)) {
            Date tmp = endDate;
            endDate = startDate;
            startDate = tmp;
        }

        long dayNumber = -1L;
        long DAY = 24L * 60L * 60L * 1000L;
        try {
            // "2010-08-01 00:00:00 --- 2010-08-03 23:59:59"算三天
            dayNumber = (endDate.getTime() + 1000 - startDate.getTime()) / DAY;

        } catch (Exception e) {
            log.error(e);
        }
        return (int) dayNumber;
    }

    /**
     * 获取2个时间相隔几天
     *
     * @param startDate
     * @param endDate   "2010-08-01 00:00:00 --- 2010-08-03 23:59:59"算两天
     * @return
     */
    public static int getBetweenDayNumberForNormal(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        }

        if (startDate.after(endDate)) {
            Date tmp = endDate;
            endDate = startDate;
            startDate = tmp;
        }

        long dayNumber = -1L;
        long DAY = 24L * 60L * 60L * 1000L;
        try {
            // "2010-08-01 00:00:00 --- 2010-08-03 23:59:59"算两天
            dayNumber = (endDate.getTime() - startDate.getTime()) / DAY;

        } catch (Exception e) {
            log.error(e);
        }
        return (int) dayNumber;
    }

    /**
     * 获取2个时间相隔几月
     */
    public static int getBetweenMonthNumber(Date startDate, Date endDate) {
        int result = 0;
        try {
            if (startDate == null || endDate == null) {
                return -1;
            }

            // swap start and end date
            if (startDate.after(endDate)) {
                Date tmp = endDate;
                endDate = startDate;
                startDate = tmp;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            int monthS = calendar.get(Calendar.MONTH);
            int yearS = calendar.get(Calendar.YEAR);

            calendar.setTime(endDate);
            int monthE = calendar.get(Calendar.MONTH);
            int yearE = calendar.get(Calendar.YEAR);

            if (yearE - yearS == 0) {
                result = monthE - monthS;
            } else {
                result = (yearE - yearS - 1) * 12 + (12 - monthS) + monthE;
            }

        } catch (Exception e) {
            log.error(e);
        }
        return result;
    }

    /**
     * 获取2个时间相隔几年
     */
    public static int getBetweenYearNumber(Date startDate, Date endDate) {
        int result = 0;
        try {
            if (startDate == null || endDate == null) {
                return -1;
            }

            // swap start and end date
            if (startDate.after(endDate)) {
                Date tmp = endDate;
                endDate = startDate;
                startDate = tmp;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int yearS = calendar.get(Calendar.YEAR);

            calendar.setTime(endDate);
            int yearE = calendar.get(Calendar.YEAR);

            result = yearE - yearS;

        } catch (Exception e) {
            log.error(e);
        }
        return result;
    }

    /**
     * 按天拆分时间
     */
    public static List<Date> splitDateByDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        List<Date> dateList = new ArrayList<Date>();
        dateList.add(startDate);

        int num = getBetweenDayNumber(startDate, endDate);
        for (int i = 1; i <= num; i++) {
            dateList.add(DateUtils.addDays(startDate, i));
        }

        return dateList;
    }

    /**
     * 按月拆分时间
     */
    public static List<Date> splitDateByMonth(Date startDate, Date endDate) {
        List<Date> dateList = new ArrayList<Date>();

        if (startDate == null || endDate == null) {
            return dateList;
        }

        dateList.add(startDate);
        int num = getBetweenMonthNumber(startDate, endDate);
        for (int i = 1; i <= num; i++) {
            dateList.add(DateUtils.addMonths(startDate, i));
        }

        return dateList;
    }

    /**
     * 按年拆分时间
     */
    public static List<Date> splitDateByYear(Date startDate, Date endDate) {
        List<Date> dateList = new ArrayList<Date>();

        if (startDate == null || endDate == null) {
            return dateList;
        }

        dateList.add(startDate);
        int num = getBetweenYearNumber(startDate, endDate);
        for (int i = 1; i <= num; i++) {
            dateList.add(DateUtils.addYears(startDate, i));
        }

        return dateList;
    }

    /**
     * 本季度
     */
    public static List<Date> getCurrentQuarter() {
        List<Date> dateList = new ArrayList<Date>();
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);// 一月为0

        dateList.add(1, calendar.getTime());// 结束时间设置为当前时间

        if (month >= 0 && month <= 2) {// 第一季度
            calendar.set(Calendar.MONTH, 0);
        } else if (month >= 3 && month <= 5) {// 第二季度
            calendar.set(Calendar.MONTH, 3);
        } else if (month >= 6 && month <= 8) {// 第三季度
            calendar.set(Calendar.MONTH, 6);
        } else {// 第四季度
            calendar.set(Calendar.MONTH, 9);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        dateList.add(0, calendar.getTime());

        return dateList;
    }

    /**
     * 上季度
     */
    public static List<Date> getLastQuarter() {
        List<Date> dateList = new ArrayList<Date>();
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);// 一月为0

        // 如果是第一季度则返回去年的第四季度
        if (month >= 0 && month <= 2) {// 当前第一季度
            calendar.add(Calendar.YEAR, -1);// 退到去年
            calendar.set(Calendar.MONTH, 9);// 去年十月
        } else if (month >= 3 && month <= 5) {// 当前第二季度
            calendar.set(Calendar.MONTH, 0);
        } else if (month >= 6 && month <= 8) {// 当前第三季度
            calendar.set(Calendar.MONTH, 3);
        } else {// 当前第四季度
            calendar.set(Calendar.MONTH, 6);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        dateList.add(0, calendar.getTime());

        calendar.add(Calendar.MONTH, 3);// 加3个月，到下个季度的第一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);// 退一天，得到上季度的最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        dateList.add(1, calendar.getTime());

        return dateList;
    }

    /**
     * 返回2个日期中的大者
     */
    public static Date max(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        if (date1 == null) {
            return date2;
        }
        if (date2 == null) {
            return date1;
        }
        if (date1.after(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * 返回不大于date2的日期 如果 date1 >= date2 返回date2 如果 date1 < date2 返回date1
     */
    public static Date ceil(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        if (date1 == null) {
            return date2;
        }
        if (date2 == null) {
            return date1;
        }
        if (date1.after(date2)) {
            return date2;
        } else {
            return date1;
        }
    }

    /**
     * 返回不小于date2的日期 如果 date1 >= date2 返回date1 如果 date1 < date2 返回date2
     */
    public static Date floor(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        if (date1 == null) {
            return date2;
        }
        if (date2 == null) {
            return date1;
        }
        if (date1.after(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * 返回2个日期中的小者
     */
    public static Date min(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        }
        if (date1 == null) {
            return date2;
        }
        if (date2 == null) {
            return date1;
        }
        if (date1.after(date2)) {
            return date2;
        } else {
            return date1;
        }
    }

    /**
     * 判断输入日期是否是一天中的最大时刻
     */
    public static boolean isMaxDayOfDay(Date date1, String precision) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMaxDateOfDay(date1);
        int diffNum = 0;
        if ("HH".equals(precision)) {
            diffNum = getBetweenHourNumber(date1, date2);
        } else if ("mm".equals(precision)) {
            diffNum = getBetweenMinuteNumber(date1, date2);
        } else {
            diffNum = getBetweenSecondNumber(date1, date2);
        }
        return diffNum == 0;
    }

    /**
     * 判断输入日期是否是一天中的最小时刻
     */
    public static boolean isMinDayOfDay(Date date1, String precision) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMinDateOfDay(date1);
        int diffNum = 0;
        if ("HH".equals(precision)) {
            diffNum = getBetweenHourNumber(date1, date2);
        } else if ("mm".equals(precision)) {
            diffNum = getBetweenMinuteNumber(date1, date2);
        } else {
            diffNum = getBetweenSecondNumber(date1, date2);
        }
        return diffNum == 0;
    }

    /**
     * 判断输入日期是否是一天中的最大时刻
     */
    public static boolean isMaxDayOfDay(Date date1) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMaxDateOfDay(date1);
        int secondNum = getBetweenSecondNumber(date1, date2);
        return secondNum == 0;
    }

    /**
     * 判断输入日期是否是一天中的最小时刻
     */
    public static boolean isMinDayOfDay(Date date1) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMinDateOfDay(date1);
        int secondNum = getBetweenSecondNumber(date1, date2);
        return secondNum == 0;
    }

    /**
     * 判断输入日期是否是一月中的最大时刻
     */
    public static boolean isMaxDayOfMonth(Date date1) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMaxDateOfMonth(date1);
        int secondNum = getBetweenSecondNumber(date1, date2);
        return secondNum == 0;
    }

    /**
     * 判断输入日期是否是一月中的最小时刻
     */
    public static boolean isMinDayOfMonth(Date date1) {
        if (date1 == null) {
            return false;
        }
        Date date2 = getMinDateOfMonth(date1);
        int secondNum = getBetweenSecondNumber(date1, date2);
        return secondNum == 0;
    }

    /**
     * 输入日期是否为同一天.
     */
    public static boolean isTheSameDay(Date startDate, Date endDate) {
        String startDateStr = dateToString(startDate);
        String endDateStr = dateToString(endDate);
        return startDateStr.equals(endDateStr);
    }

    /**
     * 功能：获取昨天最大时间。 输入: 2010-01-31 00:00:00 返回：2010-01-30 23:59:59
     */
    public static Date getLastMaxDay(Date startDate) {
        if (startDate == null) {
            return null;
        }
        startDate = DateUtils.addDays(startDate, -1);
        return TimeUtil.getMaxDateOfDay(startDate);
    }

    /**
     * 根据字符串时间,返回Calendar
     */
    public static Calendar getCalendar(String datetimeStr) {
        Calendar cal = Calendar.getInstance();
        if (StringUtils.isNotBlank(datetimeStr)) {
            Date date = TimeUtil.stringToDate(datetimeStr, DATE_PATTERN_DEFAULT);
            cal.setTime(date);
        }
        return cal;
    }

    /**
     * startStr 或者 startStr-endStr
     */
    public static String getDifferentStr(String startStr, String endStr) {
        String dateRangeStr = "";
        if (startStr.equals(endStr)) {
            dateRangeStr = startStr;
        } else {
            dateRangeStr = startStr + "-" + endStr;
        }
        return dateRangeStr;
    }

    /**
     * 给定一个日期和天数，得到这个日期+天数的日期
     *
     * @param date 指定日期
     * @param num  天数
     * @return
     */
    public static String getNextDay(String date, int num) {
        Date d = stringToDate(date, DATE_PATTERN_DAY);
        Calendar ca = Calendar.getInstance();
        ca.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        ca.setTime(d);

        int day = ca.get(Calendar.DATE);
        day = day + num;
        ca.set(Calendar.DATE, day);
        return getFormatDateTime(ca.getTime(), DATE_PATTERN_DAY);

    }

    /**
     * 给定一个日期和天数，得到这个日期+天数的日期
     *
     * @param date
     * @param num
     * @return
     */
    public static Date getNextDay(Date date, int num) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        ca.setTime(date);

        int day = ca.get(Calendar.DATE);
        day = day + num;
        ca.set(Calendar.DATE, day);
        return ca.getTime();
    }

    /**
     * 根据指定格式获取日期数据
     *
     * @param date    ：指定日期
     * @param pattern ：日期格式
     * @return
     */
    private static String getFormatDateTime(Date date, String pattern) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.format(date);
    }

    /**
     * 获取给定日期的下一个月的日期的最晚时间
     *
     * @param startDate
     * @return
     */
    public static Date getNextMonthDay(Date startDate) {
        // 是不是
        // int month = startDate.getMonth();
        Date monthEndDate = getMaxDateOfMonth(startDate);
        Date nextMonth = DateUtils.addMonths(startDate, 1);
        nextMonth = stringToDate(dateToString(nextMonth, DATE_PATTERN_DAY) + DAY_LAST_TIME, DATE_PATTERN_DEFAULT);
        if (isTheSameDay(startDate, monthEndDate)) {
            nextMonth = getMaxDateOfMonth(nextMonth);
        }
        return nextMonth;
    }

    /**
     * 获取给定日期的下一个月的日期的最晚时间
     *
     * @param startDate
     * @return
     */
    public static Date getPreviousMonthDay(Date startDate) {
        // 是不是
        // int month = startDate.getMonth();
        Date monthEndDate = getMaxDateOfMonth(startDate);
        Date nextMonth = DateUtils.addMonths(startDate, -1);
        nextMonth = stringToDate(dateToString(nextMonth, DATE_PATTERN_DAY) + DAY_LAST_TIME, DATE_PATTERN_DEFAULT);
        if (isTheSameDay(startDate, monthEndDate)) {
            nextMonth = getMaxDateOfMonth(nextMonth);
        }
        return nextMonth;
    }

    /**
     * 获取一天最早时间 如2014-11-11 00:00:00
     *
     * @param date
     * @return
     */
    public static Date getDateEarlyTime(Date date) {
        try {
            return stringToDateTime(TimeUtil.dateToString(date) + DAY_EARLY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一天最晚时间 如2014-11-11 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getDateLastTime(Date date) {
        try {
            return stringToDateTime(TimeUtil.dateToString(date) + DAY_LAST_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据秒获取还剩时分秒
     *
     * @param seconds
     * @return
     */
    public static String getDateStrBySecond(long seconds) {

        long hour = seconds / (60 * 60);//时
        long minute = (seconds - (60 * 60 * hour)) / 60;
        long second = seconds - (60 * 60 * hour) - (60 * minute);
        String dateStr = "";
        if (hour > 0) {
            dateStr = dateStr + hour + "小时";
        }
        if (minute > 0) {
            dateStr = dateStr + minute + "分钟";
        }
        if (second > 0) {
            dateStr = dateStr + second + "秒";
        }

        return dateStr;
    }

    /**
     * 获取当前一年后时间
     *
     * @return
     */
    public static Date getNextYear(Date from) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.add(Calendar.YEAR, 1);
        from = calendar.getTime();
        return from;
    }
    
    /**
     * 检查date是否过期
     * 
     * @param date
     *            待检查时间
     * @param expiration
     *            过期时间
     * @return 若过期返回<code>true</code>
     */
    public static boolean isExpired(Date date, long expiration) {
        if (date == null) {
            return false;
        }

        Date now = new Date();
        return date.getTime() + expiration < now.getTime();
    }

    /**
     * 获取距离24点还剩多少秒
     * 
     * @return
     */
    public static int getSecondsFrom24Points() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long _24Points = cal.getTimeInMillis();
        cal.setTime(new Date());
        return (int) (_24Points - cal.getTimeInMillis()) / 1000;
    }

    /**
     * 获取一个时间的当天0点0分0秒
     *
     * @return
     */
    public static Date getBeginOfDate(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取两个时间差的月数. 注意：作差时不考虑月以下的时间单位，即2010-6-1与2010-5-31相差1个月
     * 
     * @param from
     *            起始时间
     * @param to
     *            终止时间
     * @return 相差月数
     */
    public static int getDeltaMonths(Date from, Date to) {
        if (from == null || to == null || to.before(from)) {
            throw new IllegalArgumentException("\"from\" should be before \"to\"");
        }

        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(from);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(to);

        return (calTo.get(Calendar.YEAR) - calFrom.get(Calendar.YEAR)) * 12 + calTo.get(Calendar.MONTH)
                - calFrom.get(Calendar.MONTH);
    }

    /**
     * 获取两个时间差的年数. 注意：作差时不考虑年以下的时间单位，即2010-1-1与2009-12-31相差1年
     * 
     * @param from
     *            起始时间
     * @param to
     *            终止时间
     * @return 相差年数
     */
    public static int getDeltaYear(Date from, Date to) {
        if (from == null || to == null || to.before(from)) {
            throw new IllegalArgumentException("\"from\" should be before \"to\"");
        }

        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(from);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(to);

        return calTo.get(Calendar.YEAR) - calFrom.get(Calendar.YEAR);
    }

    /**
     * 获取两个时间差的秒数. 注意：作差时不考虑年以下的时间单位，即2010-1-1与2009-12-31相差1年
     *
     * @param from
     *            起始时间
     * @param to
     *            终止时间
     * @return 相差年数
     */
    public static int getDeltaSeconds(Date from, Date to) {
        if (from == null || to == null || to.before(from)) {
            return 0;
        }

        long d = to.getTime() - from.getTime();

        d /= 1000; // to second

        return (int) d;
    }

    /**
     * 获取两个时间差的天数
     * 
     * @param from
     *            起始时间
     * @param to
     *            终止时间
     * @return 相差天数
     */
    public static int getDeltaDays(Date from, Date to) {
        if (from == null || to == null || to.before(from)) {
            throw new IllegalArgumentException("\"from\" should be before \"to\"");
        }

        long d = to.getTime() - from.getTime();

        d /= 1000; // to second

        d /= 60; // to minute

        d /= 60; // to hour

        d /= 24; // to day

        return (int) d;
    }

    /**
     * 若gap之前是“昨天”则返回true
     * 
     * @param gap
     *            毫秒
     * @return
     */
    public static boolean isDayChanged(long gap) {
        Calendar today = Calendar.getInstance();
        int thisDay = today.get(Calendar.DAY_OF_YEAR);
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(new Date(today.getTime().getTime() - gap));
        int previousDay = yesterday.get(Calendar.DAY_OF_YEAR);

        log.debug("Today: " + today.getTime() + "(" + thisDay + "), yesterday: " + yesterday.getTime() + "("
                + previousDay + ")");

        return previousDay != thisDay;
    }

    /** 计算年龄 */
    public static String getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }

        return age + "";
    }
    
    public static String getBirthDayFIdentity(String identity) throws Exception{
    	 String age ="";
    	 if(!StringUtil.isEmpty(identity)){
    		 //读取到出生年月日
    		 identity = identity.substring(6,14);
    		 //分别得到 年 月 日
    		 int year = Integer.parseInt(identity.substring(0,4));
    		 int month = Integer.parseInt(identity.substring(4,6));
    		 int day = Integer.parseInt(identity.substring(6,8));
    		 //形成date
    		 Calendar cal = Calendar.getInstance();
    		 cal.set(year, month, day);
    		 Date birthDay = cal.getTime();
    		 age = getAge(birthDay);
    	 }
    	 return age;
    }


    /**
     * 将一个日期对象格式化为 yyyy-MM-dd HH:mm:ss 格式的字符串
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, FMT_YYYY_MM_DD_HH24_MM_SS);
    }
    
    /**
     * 将一个日期对象格式化为字符串
     * @param date 
     * @param fmt 格式
     * @param def 转换失败,返回默认值
     * @return
     */
    public static String format(Date date, String fmt, String def) {
        if (date == null) {
            return def;
        }
        try {
            return format(date, fmt);
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return def;
        }
    }
    
    /**
     * 将一个日期对象格式化为字符串 
     * @param date
     * @param fmt
     * @return
     */
    public static String format(Date date, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    /**
     * 将一个 <code>yyyy-MM-dd HH:mm:ss</code> 格式的字符串转换为日期对象
     * @param time 字符串
     * @param def 转换失败返回的默认值
     * @return
     */
    public static Date parse(String time, Date def) {
        return parse(time, FMT_YYYY_MM_DD_HH24_MM_SS, def);
    }
    
    /**
     * 将一个字符串转换为日期对象
     * @param time 字符串
     * @param fmt 日期格式
     * @param def 转换失败返回的默认值
     * @return
     */
    public static Date parse(String time, String fmt, Date def) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fmt);
            return sdf.parse(time);
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return def;
        }
    }
    
    /**
     * 比例两个Date对象的日期部分是否相同
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isDateEqual(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        
        return TimeUtil.format(d1, FMT_YYYYMMDD).equals(TimeUtil.format(d2, FMT_YYYYMMDD));
    }
    
    /**
     * 返回还款期序格式的数字<p/>
     * 如 <code>2015-04-25 12:12:12</code> 还款的期序是 <code>201404</code>
     * @param date
     * @return 如果 date == null ,返回0
     */
    public static int getRepayPhase(Date date) {
        if (date == null) {
            return 0;
        }
        
        String s = format(date, "yyyyMM");
        return NumberUtils.toInt(s);
    }

    /**
     * 增加日期
     * @param date
     * @param day
     * @return
     */
    public static Date incDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    /**
     * 增加分钟
     * @param date
     * @param minute
     * @return
     */
    public static Date incMinute(Date date, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    /**
     * 增加秒
     * @param date
     * @param second
     * @return
     */
    public static Date incSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 取互联网时间,非本地服务器时间
     * @return
     */
    public static Date getWebsiteDatetime() {
        String webUrl = "http://www.baidu.com";
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            return new Date(ld);// 转换为标准时间对象
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int x; // 日期属性：年
    private int y; // 日期属性：月
    private int z; // 日期属性：日
    public static final SimpleDateFormat MD_FORMAT = new SimpleDateFormat("MM月dd日");
    public static final SimpleDateFormat HM_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * 日期间隔N天
     * N=1  当前日期
     * N=2 当前日期+1
     * N=-1 当前日期-1
     */
    public static Date previousNDays(Integer n) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, n);
        date = calendar.getTime();
        return date;
    }

    public static boolean isBefore(Date date, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d2 = null;
        try {
            d2 = df.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null != date && null != d2 && date.before(d2);
    }
    public static boolean isBefore(Date date, Date date2) {
        return null != date && date.before(date2);
    }

    public static boolean isAfter(Date date, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d2 = null;
        try {
            d2 = df.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null != date && null != d2 && date.after(d2);
    }

    public static boolean isBetween(Date date, String date2, String date3) {
        return null != date && isAfter(date, date2) && isBefore(date, date3);
    }

    /**
     * 某一个日期n月相对应某一天 n 为负值表示向前 n 为正值表示向后
     */
    public static Date calDateForMonth(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, n);
        return c.getTime();
    }
    /**
     * 某一个日期n天相对应某一天 n 为负值表示向前 n 为正值表示向后
     */
    public static Date calDateForDay(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, n);
        return c.getTime();
    }
    public static Date calDateForDay(String date, int n) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DAY_OF_MONTH, n);
        return c.getTime();
    }
    /**
     * 某一个日期n小时相对应某一天 n 为负值表示向前 n 为正值表示向后
     */
    public static Date calDateForHour(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, n);
        return c.getTime();
    }

    /**
     * 某一个日期n天相对应某一天 n 为负值表示向前 n 为正值表示向后
     */
    public static Date calDateForYear(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, n);
        return c.getTime();
    }

    /**
     * 某一时刻相对应这是时刻n分钟 为负值表示向前 n 为正值表示向后
     *
     * @param date
     * @param n
     * @return
     */
    public static Date calDateForMinute(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, n);
        return c.getTime();
    }

    /**
     * 功能：得到当前月份月初 格式为：xxxx-yy-zz (eg: 2007-12-01)
     *
     * @return String
     *
     */
    public static String toMonth( Date time) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd HH:mm");
        String LockStart = sdf1.format(time);
        return LockStart;
    }

    /**
     * 功能：得到当前时间格式化
     *
     * @return String
     *
     */
    public static String toYear( Date time) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String LockStart = sdf1.format(time);
        return LockStart;
    }

    /**
     * 功能：判断输入年份是否为闰年<br>
     *
     * @param year
     * @return 是：true 否：false
     *
     */
    public boolean leapYear(int year) {
        boolean leap;
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    leap = true;
                } else {
                    leap = false;
                }
            } else {
                leap = true;
            }
        } else {
            leap = false;
        }
        return leap;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate
     *            较小的时间
     * @param bdate
     *            较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate
     *            较小的时间
     * @param bdate
     *            较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算两个时间相差多少分钟
     *
     * @param smdate
     *            较小的时间
     * @param bdate
     *            较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static BigDecimal minsBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin=dfs.parse(dfs.format(smdate));
        Date end = dfs.parse(dfs.format(bdate));
        long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒
        long min=between/60;

        return new BigDecimal(String.valueOf(min));
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "时" + min + "分";
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(Date one, Date two) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return day + "天" + hour + "时" + min + "分" + sec + "秒";
    }

    /**
     * 两个时间相差距离多少天多少小时多少分
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTimeM(Date one, Date two) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return day + "天" + hour + "时" + min + "分";
        }
        return hour + "时" + min + "分" + sec + "秒";
    }

    /**
     * 两个时间相差距离多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx小时xx分xx秒
     */
    public static String getDistanceTimeHour(long diff) {
        if (diff < 0) {
            diff = -diff;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        day = diff / (24 * 60 * 60);
        hour = (diff / (60 * 60) - day * 24);
        min = ((diff / (60)) - day * 24 * 60 - hour * 60);
        sec = (diff - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return ((hour + day * 24) == 0 ? "" : ((hour + day * 24) + "时")) + min + "分" + sec + "秒";
    }

    /**
     * 两个时间相差距离多少天多少小时多少分
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分
     */
    public static String getDistanceTimeMM(Date one, Date two) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        return day + "天" + hour + "时" + min + "分";
    }

    public static String getAmOrPm(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12) {
            return "下午";
        } else {
            return "上午";
        }
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static long getDistanceMillon(Date one, Date two) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        return diff;
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistance(Date one, Date two) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return day + "天" + hour + "时" + min + "分" + sec + "秒";
        } else {
            if (hour != 0) {
                return hour + "时" + min + "分" + sec + "秒";
            } else {
                if (min != 0) {
                    return min + "分" + sec + "秒";
                } else {
                    return sec + "秒";
                }
            }
        }

    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistance(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        Date one;
        Date two;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (day != 0) {
                return day + "天" + hour + "时" + min + "分" + sec + "秒";
            } else {
                if (hour != 0) {
                    return hour + "时" + min + "分" + sec + "秒";
                } else {
                    if (min != 0) {
                        return min + "分" + sec + "秒";
                    } else {
                        return sec + "秒";
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒,
     *  如果不够1分钟则只显示秒；
        *  如果不够1小时则显示分秒；
        *  如果不够一天则显示时分秒；
        *  如果大于1天则显示天时分
     *
     *
     * @param str1
     *            时间参数 1 格式：1990-01-01 12:00:00
     * @param str2
     *            时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceLevel(Date one, Date two) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return day + "天" + hour + "时" + min + "分";
        } else {
            if (hour != 0) {
                return hour + "时" + min + "分" + sec + "秒";
            } else {
                if (min != 0) {
                    return min + "分" + sec + "秒";
                } else {
                    return sec + "秒";
                }
            }
        }

    }

    /**
     * 当天剩余秒数
     * @return int
     */
    public static int getExpire() {
        long now = new Date().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long nextDay = calendar.getTimeInMillis();
        int expire = (int) ((nextDay - now) / 1000);
        return expire;
    }

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(calDateForDay("2018-06-06 18:37:06",2)));
    }
}
