package cn.hawy.quick.modular.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author linmingming
 * @create 2016年12月15日 上午10:17:45
 */
public class DateUtils {
	
	protected static Logger logger  =  LoggerFactory.getLogger(DateUtils.class);

	public static final long daySpan = 24 * 60 * 60 * 1000;
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", java.util.Locale.CHINA);

	public static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss", java.util.Locale.CHINA);

	public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA);

	public static final DateFormat DATE_TIME_FORMAT_IMAGE = new SimpleDateFormat(
			"yyyyMMddHHmmss", java.util.Locale.CHINA);

	public static final String PATTERN_DATE = "yyyy-MM-dd";

	public static final String PATTERN_DATE_NO_HYPHEN = "yyyyMMdd";

	public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

	public static final String PATTERN_DATE_MILLISECOND = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String PATTERN_MONTH = "yyyy-MM";

	public static SimpleDateFormat sdf_date = new SimpleDateFormat("yyyyMMdd");
	/**
	 * 格式化当前日期
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrTimeStr(String pattern) {
		return formatDate(new Date(), pattern);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			return DATE_TIME_FORMAT.format(date);
		}
		DateFormat dateFormat = new SimpleDateFormat(pattern,
				java.util.Locale.CHINA);
		return dateFormat.format(date);
	}
	/**
	 * 获取当前日期(yyyyMMdd)
	 * @return
	 */
	public static String getDate(){
		Date date = new Date();
		String strDate = sdf_date.format(date);
		return strDate;
	}
	
	/**
	 * 昨天
	 * 
	 * @return
	 */
	public static Date getYesterday() {
		Date today = new Date();
//		Date yesterday = DateUtils.addDays(today, -1);
		return null;
	}

	/**
	 * 根据日期字符串解析得到date类型日期
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		return parseDate(dateStr, PATTERN_DATE);
	}

	/**
	 * 根据日期字符串和日期格式解析得到date类型日期
	 * 
	 * @param dateStr
	 * @param datePattern
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateStr, String datePattern)
			throws ParseException {
//		DateTime dt = DateTimeFormat.forPattern(datePattern).parseDateTime(
//				dateStr);
//		return dt.toDate();
		return null;
	}

	/**
	 * 获取所传日期的本月第一天
	 * 
	 * @param dt
	 * @return
	 */
	public static String getFirstOfMonthDay(Date dt) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String firstDay = formatDate(c.getTime(), PATTERN_DATE);
		return firstDay;
	}

	/**
	 * 获取所传日期的本月最后一天
	 * 
	 * @param dt
	 * @return
	 */
	public static String getEndOfMonthDay(Date dt) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDay = formatDate(c.getTime(), PATTERN_DATE);
		return lastDay;
	}
	public static String getFirstOfMonthDay(String year,String month){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,Integer.valueOf(year));
		c.set(Calendar.MONTH,Integer.valueOf(month)-1);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String firstDay =DateUtils.formatDate(c.getTime(),DateUtils.PATTERN_DATE);
		return firstDay;
	}
	public static String getEndOfMonthDay(String year,String month){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR,Integer.valueOf(year));
		c.set(Calendar.MONTH,Integer.valueOf(month)-1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		String firstDay =DateUtils.formatDate(c.getTime(),DateUtils.PATTERN_DATE);
		return firstDay;
	}
	
	/**
	 * 明天
	 * @return
	 */
	public static Date getTomorrow() {
		Date today = new Date();
//		Date tomorrow = DateUtils.addDays(today, +1);
		Date tomorrow =null;
		return tomorrow;
	}
	
	/**
	 * 	获取上月：
	 * @return
	 */

	public static String getLastMonth() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		SimpleDateFormat format =  new SimpleDateFormat("yyyyMM");
		String time = format.format(c.getTime());
	return time;
	}

	/**
	 * 返回当前时间 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurrentTimeStr() {
		return DateUtils.formatDate(new Date(), DateUtils.PATTERN_DATE_TIME);
	}

	public static Date now() {
		return new Date();
	}
	
	/**
	 * 获取当前时间(yyyyMMddHHmmssSSSS年月日时分秒毫秒)
	 * @return
	 */
	public static String getAllTime(){
		return new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date());
	}
	
	/**
	 * 获取n天之后的日期，n可为负
	 * @param format
	 * @param n
	 * @return
	 */
	public static String getNDaysAfter(String oldDate,String format,int n){
		String strDate = "";
		try{
			SimpleDateFormat sf = new SimpleDateFormat(format);
			Date date = new Date(sf.parse(oldDate).getTime() + n*daySpan);
			strDate = sf.format(date);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return strDate;
	}

	/**
	 * 获得n个月前的日期(未处理n>12的情况)
	 * @param format
	 * @param n
	 * @return
	 */
	public static String getNMonthAgo(String format,int n){
		try{
			int dateInt = Integer.parseInt(getDate());
			int yearInt = dateInt/10000;
			int monthInt = (dateInt/100)%100;
			if(monthInt<n+1){
				yearInt--;
				monthInt = monthInt-n+12;
			}else{
				monthInt = monthInt-n;
			}
			String rstStr = yearInt+""+SysStringUtil.padding("left", 2, "0", String.valueOf(monthInt)) +""+SysStringUtil.padding("left", 2, "0", String.valueOf(dateInt%100));
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf1.parse(rstStr);
			SimpleDateFormat sdf2 = new SimpleDateFormat(format);
			return sdf2.format(date);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得oldDate y年m月后的日期（m为负值则为前m个月，y同理）
	 * @param oldDate 格式需符合format，为null则默认为今天
	 * @return
	 */
	public static String getRelativeDate(String oldDate,String format,int y,int m){
		try{
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat(format);
			int dateInt = Integer.parseInt(getDate());
			if(oldDate!=null){
				dateInt = Integer.parseInt(sdf1.format(sdf2.parse(oldDate)));
			}
			int yearInt = dateInt/10000;
			int monthInt = (dateInt/100)%100;
			while(monthInt+m>12){
				m=m-12;
				yearInt++;
			}
			while(monthInt+m<=0){
				m=m+12;
				yearInt--;
			}
			monthInt = monthInt+m;
			yearInt = yearInt+y;
			String rstStr = yearInt+""+SysStringUtil.padding("left", 2, "0", String.valueOf(monthInt)) +""+SysStringUtil.padding("left", 2, "0", String.valueOf(dateInt%100));
			Date date = sdf1.parse(rstStr);
			return sdf2.format(date);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
//	public static boolean  isOutTime(String date,Date now){
//		Date time = new Date();
//		try{
//			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			time = new Date(sf.parse(date).getTime());
//		}catch(Exception e){
//			e.printStackTrace();
//			return false;
//		}
//		long times =now.getTime()-time.getTime();
//		int minuts=0;
//		if(times!=0) {
//			minuts=(int) (times/(1000*60));
//		}
//		if(minuts>=30) {
//			return true;
//		}
//		return false;
//		
//	}
	
	
	
//	public static void main(String[] args) {
//		System.out.println(getNDaysAfter(formatDate(new Date(),"yyyy-MM-dd"),"yyyy-MM-dd",-2));
//	}
}
