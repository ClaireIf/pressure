package client.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public final class DateUtils {

	public static final SimpleDateFormat FORMAT_YYYY_MM;
	public static final SimpleDateFormat FORMAT_YYYY_MM_DD;
	public static final SimpleDateFormat FORMAT_YMDHMS;
	public static final Pattern PATTERN_YYYY_MM;
	public static final Pattern PATTERN_YYYY_MM_DD;
	public static final Pattern PATTERN_YYYY_MM_DD_HH_MM_SS;

	static {
		FORMAT_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
		FORMAT_YYYY_MM = new SimpleDateFormat("yyyy-MM");
		PATTERN_YYYY_MM_DD_HH_MM_SS = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}");
		PATTERN_YYYY_MM_DD = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
		PATTERN_YYYY_MM = Pattern.compile("[0-9]{4}-[0-9]{1,2}");
	}

	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis() ;
	}

	public static long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	public static String getFullString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getYMDString() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	public static String getYMString() {
		return new SimpleDateFormat("yyyyMM").format(new Date());
	}
	public static String getTimeString(long millis) {
		return FORMAT_YMDHMS.format(new Date(millis));
	}
	public static Date toDate(String strDate) {
		strDate = strDate.replaceAll("/", "-");
		try {
			if (PATTERN_YYYY_MM_DD_HH_MM_SS.matcher(strDate).find())
				return FORMAT_YMDHMS.parse(strDate);
			else if (PATTERN_YYYY_MM_DD.matcher(strDate).find())
				return FORMAT_YYYY_MM_DD.parse(strDate);
			else if (PATTERN_YYYY_MM.matcher(strDate).find())
				return FORMAT_YYYY_MM.parse(strDate);
			else
				throw new RuntimeException("unknown date format string");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long toTimestamp(String strDate) {
		return toDate(strDate).getTime();
	}

	public static long toSeconds(String strDate) {
		return toTimestamp(strDate) / 1000;
	}


	/**
	 * Compare whether the year, month, and day of two times are the same
	 * @param d1  time1
	 * @param d2  time2
	 * @return
	 */
	public static boolean compareDayTime(Calendar d1,Calendar d2){
		int d1_year=d1.get(Calendar.YEAR);
		int d1_month=d1.get(Calendar.MONTH);
		int d1_day=d1.get(Calendar.DAY_OF_MONTH);
		int d2_year=d2.get(Calendar.YEAR);
		int d2_month=d2.get(Calendar.MONTH);
		int d2_day=d2.get(Calendar.DAY_OF_MONTH);
		if(d1_year==d2_year&&d1_month==d2_month&&d1_day==d2_day)return true;
	    return false;
	}
	/**
	 * Compare whether the year and month of two times are the same
	 * @param d1  time1
	 * @param d2  time2
	 * @return
	 */
	public static boolean compareMonthTime(Calendar d1,Calendar d2){
		int d1_year=d1.get(Calendar.YEAR);
		int d1_month=d1.get(Calendar.MONTH);
		int d2_year=d2.get(Calendar.YEAR);
		int d2_month=d2.get(Calendar.MONTH);
		if(d1_year==d2_year&&d1_month==d2_month)return true;
	    return false;
	}
	/**
	 * Get the prefix of the upload path
	 * @return
	 */
	/*public static String getUploadPathPrefix(){
		 ResourceBundle bundle = ResourceBundle.getBundle("application");  
		  return bundle.getString("upload.path.prefix");  
	}*/
	
	
	public static Date getNextDay(Date currentDay){    
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDay);
		calendar.add(Calendar.DATE, 1);
		Date nextDay = calendar.getTime();
		return nextDay;
	}
	/**
	 *  Get the time at 0:00 of the day
	 * @return
	 */
	public static Date getTodayMorning() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	/**
	 *  Get the 24 o'clock time of the day
	 * @return
	 */
	public static Date getTodayNight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return  cal.getTime();
	}

	/** @Description:(Get the current time after nth)
	* @param times current time
	* @param days  nth day
	* @param type  0: next n days, 1: previous n days
	* @return
	**/
	public static long getOnedayNextDay(long times,int days,int type){
		final long ruleTimes = 86400;
		return (0 == type ? times+(ruleTimes*days) : times-(ruleTimes*days));
	}

	/** @Description:(Get the current time after nth)
	 * @param times current time
	 * @param days  nth day
	 * @param type  0: next n days, 1: previous n days
	 * @return
	 **/
	public static long getAfterOrNextDay(long times,int days,int type){
		final long ruleTimes = 24*60*60*1000;
		return (0 == type ? times+(ruleTimes*days) : times-(ruleTimes*days));
	}



	/**
	 *  Get the time at 0:00 yesterday
	 * @return
	 */
	public static Date getYesterdayMorning() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	/**
	 *  Get the time at 23.59 o'clock yesterday
	 * @return
	 */
	public static Date getYesterdayLastTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND,00);
		return cal.getTime();
	}
	/**
	 *  Get yesterday's 24 o'clock time
	 * @return
	 */
	public static Date getYesterdayNight() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return  cal.getTime();
	}

	/**
	 *  Get the time at 0:00 tomorrow
	 * @return
	 */
	public static Date getTomorrowMorning() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	/**
	 *  Get the time at 23.59 o'clock tomorrow
	 * @return
	 */
	public static Date getTomorrowLastTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND,00);
		return cal.getTime();
	}
	/**
	 *  Get tomorrow's 24 o'clock time
	 * @return
	 */
	public static Date getTomorrowNight() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return  cal.getTime();
	}

	/**
	 *  Get this Monday at 0:00
	 * @return
	 */
	public static Date getWeekMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return  cal.getTime();
	}

	/**
	 * Get the 24 o'clock time of this Sunday
	 * @return
	 */
	public  static Date getWeekNight() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getWeekMorning());
		cal.add(Calendar.DAY_OF_WEEK, 7);
		return cal.getTime();
	}

	/**
	 * Get the time at 0:00 on the first day of the month
	 * @return
	 */
	public static Date getMonthMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return  cal.getTime();
	}

	/**
	 *  Get the 24 o'clock time of the last day of the month
	 * @return
	 */
	public static Date getMonthNight() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 24);
		return cal.getTime();
	}

	/**
	 *  Get the time at 0:00 on the first day of the previous month
	 * @return
	 */
	public static Date getLastMonthMorning(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY,00);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Get Last Monday at 0:00
	 * @return
	 */
	public static Date getPreviousWeekday(){
		 Calendar cal = Calendar.getInstance();
		 cal.setFirstDayOfWeek(Calendar.MONDAY);
			cal.add(Calendar.DATE, -7);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}



	/**
	 * Get the 0 o'clock of the previous year of the current time
	 */
	public static Date getLastYear(){
		Calendar cal = Calendar.getInstance();
		 cal.setTime(new Date());
		 cal.add(Calendar.YEAR, -1);
		 cal.set(Calendar.HOUR_OF_DAY,00);
		 cal.set(Calendar.SECOND, 0);
		 cal.set(Calendar.MINUTE, 0);
		 cal.set(Calendar.MILLISECOND, 0);
		 return cal.getTime();
	}



	/**
	 *  Get current time one month ago
	 * @return
	 */
	public static Date getLastMonth(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY,00);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}



	/**
	 *  Get the time three months ago of the current time
	 * @return
	 */
	public static Date getLast3Month(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -3);
		return cal.getTime();
	}


	/**
	 * Get last Sunday at 23:59
	 * @return
	 */
	public static Date getPreviousWeekSunday(){
		 Calendar cal = Calendar.getInstance();
		 cal.setFirstDayOfWeek(Calendar.MONDAY);
		 cal.add(Calendar.DATE, -7);
		 cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		 cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.MILLISECOND,00);
		return cal.getTime();
	}

	/**
	* Convert timestamp to year, month, and day
	* @author dingyongli
	* @param  strDateTime：string form of datetime
	* @return timestamp
	* @throws
	*/
	public static String strToDateTime(long strDateTime){
		String timestamp;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(strDateTime*1000);
		timestamp = simpleDateFormat.format(date);
		return ("1970-01-01 08:00:00".equals(timestamp)?null:timestamp);
	}

	public static String TimeToStr(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(date);
	}

	public static String dateToStr(Date date,String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}


	public static Date strYYMMDDToDate(String strYYMMDD) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
		try {
			return format.parse(strYYMMDD);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convert string to Date type
	 * @param time string time
	 * @param pattern match format
	 * @return Return Date format time
	 * @throws ParseException conversion exception
	 */
	public static Date getDate(String time,String pattern) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		if(!StringUtils.isEmpty(time))
		  return sdf.parse(time);
		return null;
	}
	/**
	 * Convert Date type to string
	 * @param date time
	 * @param pattern match format
	 * @return Returns the time in string format
	 */
	public static String getDateStr(Date date,String pattern){
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * Description: Get the number of seconds between the current time and the next morning
	 * @Auther: Claire
	 * @param
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getSecondsFromCurrentToNextMorning() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getTomorrowMorning());
		return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
	}

	/**
	 * Description: Get the start date of the specified time
	 * @Auther: Claire
	 * @param dayTimeString
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getDayStartTime(String dayTimeString) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate(dayTimeString));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * Description: Get the end time of the specified time
	 * @Auther: Claire
	 * @param
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getDayEndTime(String dayTimeString) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate(dayTimeString));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTimeInMillis();
	}

	/**
	 * Description: Get the start date of the specified time
	 * @Auther: Claire
	 * @param date
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getDayStartTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * Description: Get the end time of the specified time
	 * @Auther: Claire
	 * @param date
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getDayEndTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTimeInMillis();
	}

	/**
	 * Description: Get the start date of the current day
	 * @Auther: Claire
	 * @param
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getCurrentDayStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * Description: Get the end time of the current day
	 * @Auther: Claire
	 * @param
	 * @date: 2019-03-23
	 * @return: java.lang.Long
	 */
	public static Long getCurrentDayEndTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTimeInMillis();
	}
		
	// public static String getDiff(long start, long end) {
	// long diff = end - start;
	// long day = diff / 86400;
	// long hour = diff % 86400 / 3600;
	// long minute = (diff % 86400 % 3600) / 60;
	// return MessageFormat.format("{0}天{1}时{2}分", diff / 86400, diff % 86400 /
	// 3600, (diff % 86400 % 3600) / 60);
	// }
	//
	// public static void main(String... args) {
	// long a = System.currentTimeMillis() + 86400 * 7;
	// System.out.println(a + 121);
	// System.out.println(getDiff(System.currentTimeMillis(), a + 79504));
	// System.out.println(getDiff(System.currentTimeMillis(), a + 121));
	// System.out.println(getDiff(System.currentTimeMillis(),
	// System.currentTimeMillis() + 121));
	//
	// long b = 1420788389937L;
	// System.out.println(b % 86400);
	// System.out.println(b % 86400 % 3600);
	// System.out.println((b % 86400) % 3600);
	// }


	public static long getTomorrowZeroSeconds() {
		long current = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long tomorrowzero = calendar.getTimeInMillis();
		long tomorrowzeroSeconds = (tomorrowzero - current) / 1000;
		return tomorrowzeroSeconds;
	}


	/**
	 * Description: Get the current time
	 * @Author: Claire
	 * @param
	 * @date: 2019-05-09
	 * @return: java.lang.String
	 */
	public static String getCurrentDay(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(cal.get(Calendar.YEAR));
		sb.append("-");
		sb.append(cal.get(Calendar.MONTH)+1);
		sb.append("-");
		sb.append(cal.get(Calendar.DAY_OF_MONTH));
		return sb.toString();
	}
}
