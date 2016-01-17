/**
 * DateUtil.java 2009-4-30 ����11:36:28
 */
package common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class DateUtil {
	/**
	 * ����ת������
	 */

		public static final String C_DATE_DIVISION = "-";

		public static final String C_TIME_PATTON_DEFAULT = "yyyy-MM-dd HH:mm:ss";

		public static final String C_DATE_PATTON_DEFAULT = "yyyy-MM-dd";

		public static final String C_DATA_PATTON_YYYYMMDD = "yyyyMMdd";

		public static final String C_TIME_PATTON_HHMMSS = "HH:mm:ss";

		public static final int C_ONE_SECOND = 1000;

		public static final int C_ONE_MINUTE = 60 * C_ONE_SECOND;

		public static final int C_ONE_HOUR = 60 * C_ONE_MINUTE;

		public static final long C_ONE_DAY = 24 * C_ONE_HOUR;

		/**
		 * Return the current date
		 * 
		 * @return �� DATE<br>
		 */
		public static Date getCurrentDate() {
			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();

			return currDate;
		}

		/**
		 * Return the current date string
		 * 
		 * @return �� �����������ַ���<br>
		 */
		public static String getCurrentDateStr() {
			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();

			return format(currDate);
		}

		/**
		 * Return the current date in the specified format
		 * 
		 * @param strFormat
		 * @return
		 */
		public static String getCurrentDateStr(String strFormat) {
			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();

			return format(currDate, strFormat);
		}
		
		public static String getDateStr(String dateStr,String strFormat) {
			
			if(dateStr==null||dateStr.toLowerCase().equals("null")){
				return "";
			}
			Date date = DateUtil.parseDate(strFormat,dateStr);
			return format(date, strFormat);
		}
		
		public static String getDateStr2(String dateStr,String strFormat) {
			
			if(dateStr==null||dateStr.toLowerCase().equals("null")){
				return "";
			}
			Date date = DateUtil.parseDate("yyyy-MM-dd HH:mm:ss",dateStr);
			return format(date, strFormat);
		}

		/**
		 * Parse a string and return a date value
		 * 
		 * @param dateValue
		 * @return
		 * @throws Exception
		 */
		public static Date parseDate(String dateValue) {
			return parseDate(C_DATE_PATTON_DEFAULT, dateValue);
		}

		/**
		 * Parse a strign and return a datetime value
		 * 
		 * @param dateValue
		 * @return
		 */
		public static Date parseDateTime(String dateValue) {
			return parseDate(C_TIME_PATTON_DEFAULT, dateValue);
		}

		/**
		 * Parse a string and return the date value in the specified format
		 * 
		 * @param strFormat
		 * @param dateValue
		 * @return
		 * @throws ParseException
		 * @throws Exception
		 */
		public static Date parseDate(String strFormat, String dateValue) {
			if (dateValue == null)
				return null;

			if (strFormat == null)
				strFormat = C_TIME_PATTON_DEFAULT;

			SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
			Date newDate = null;

			try {
				newDate = dateFormat.parse(dateValue);
			} catch (ParseException pe) {
				newDate = null;
			}

			return newDate;
		}

		/**
		 * ��Timestamp���͵�����ת��Ϊϵͳ��������ĸ�ʽ���ַ�����
		 * 
		 * @param aTs_Datetime
		 *            ��Ҫת�������ڡ�
		 * @return ת������ϸ�����ʽ�������ַ���
		 */
		public static String format(Date aTs_Datetime) {
			return format(aTs_Datetime, C_DATE_PATTON_DEFAULT);
		}

		/**
		 * ��Timestamp���͵�����ת��Ϊϵͳ��������ĸ�ʽ���ַ�����
		 * 
		 * @param aTs_Datetime
		 *            ��Ҫת�������ڡ�
		 * @return ת������ϸ�����ʽ�������ַ���
		 */
		public static String formatTime(Date aTs_Datetime) {
			return format(aTs_Datetime, C_TIME_PATTON_DEFAULT);
		}

		/**
		 * ��Date���͵�����ת��Ϊϵͳ��������ĸ�ʽ���ַ�����
		 * 
		 * @param aTs_Datetime
		 * @param as_Pattern
		 * @return
		 */
		public static String format(Date aTs_Datetime, String as_Pattern) {
			if (aTs_Datetime == null || as_Pattern == null)
				return null;

			SimpleDateFormat dateFromat = new SimpleDateFormat();
			dateFromat.applyPattern(as_Pattern);

			return dateFromat.format(aTs_Datetime);
		}

		/**
		 * @param aTs_Datetime
		 * @param as_Format
		 * @return
		 */
		public static String formatTime(Date aTs_Datetime, String as_Format) {
			if (aTs_Datetime == null || as_Format == null)
				return null;

			SimpleDateFormat dateFromat = new SimpleDateFormat();
			dateFromat.applyPattern(as_Format);

			return dateFromat.format(aTs_Datetime);
		}

		public static String getFormatTime(Date dateTime) {
			return formatTime(dateTime, C_TIME_PATTON_HHMMSS);
		}

		/**
		 * @param aTs_Datetime
		 * @param as_Pattern
		 * @return
		 */
		public static String format(Timestamp aTs_Datetime, String as_Pattern) {
			if (aTs_Datetime == null || as_Pattern == null)
				return null;

			SimpleDateFormat dateFromat = new SimpleDateFormat();
			dateFromat.applyPattern(as_Pattern);

			return dateFromat.format(aTs_Datetime);
		}

		/**
		 * ȡ��ָ������N��������
		 * 
		 * @param date
		 * @param days
		 * @return
		 */
		public static Date addDays(Date date, int days) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			cal.add(Calendar.DAY_OF_MONTH, days);

			return cal.getTime();
		}

		/**
		 * ������������֮����������
		 * 
		 * @param date1
		 * @param date2
		 * @return
		 */
		public static int daysBetween(Date date1, Date date2) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(date2);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);

			return Integer.parseInt(String.valueOf(between_days));
		}

		/**
		 * ���㵱ǰ���������"1977-12-01"������
		 * 
		 * @param date
		 * @return
		 */
		public static long getRelativeDays(Date date) {
			Date relativeDate = DateUtil.parseDate("yyyy-MM-dd", "1977-12-01");

			return DateUtil.daysBetween(relativeDate, date);
		}

		public static Date getDateBeforTwelveMonth() {
			String date = "";
			Calendar cla = Calendar.getInstance();
			cla.setTime(getCurrentDate());
			int year = cla.get(Calendar.YEAR) - 1;
			int month = cla.get(Calendar.MONTH) + 1;
			if (month > 9) {
				date = String.valueOf(year) + C_DATE_DIVISION
						+ String.valueOf(month) + C_DATE_DIVISION + "01";
			} else {
				date = String.valueOf(year) + C_DATE_DIVISION + "0"
						+ String.valueOf(month) + C_DATE_DIVISION + "01";
			}

			Date dateBefore = parseDate(date);
			return dateBefore;
		}

		/**
		 * ����ʱ���ַ���,��һ��󷵻�Date
		 * 
		 * @param date
		 *            ʱ�� ��ʽ YYYY-MM-DD
		 * @return
		 */
		public static Date addDate(String date) {
			if (date == null) {
				return null;
			}

			Date tempDate = parseDate(C_DATE_PATTON_DEFAULT, date);
			String year = format(tempDate, "yyyy");
			String month = format(tempDate, "MM");
			String day = format(tempDate, "dd");

			GregorianCalendar calendar = new GregorianCalendar(Integer
					.parseInt(year), Integer.parseInt(month) - 1, Integer
					.parseInt(day));

			calendar.add(GregorianCalendar.DATE, 1);
			return calendar.getTime();
		}

		public static void main(String[] args) {
			// Date date1 = DateUtil.addDays(DateUtil.getCurrentDate(),1);
			// Date date2 = DateUtil.addDays(DateUtil.getCurrentDate(),101);
			//	  
			// System.out.println(DateUtil.getRelativeDays(date1));
			// System.out.println(DateUtil.getRelativeDays(date2));

			// Timestamp date = new Timestamp(801);
			//	        
			// System.out.println(date);
			// String strDate = DateUtil.format(date, C_DATA_PATTON_YYYYMMDD);
			//	        
			// System.out.println(strDate);

//			String date = "2006-07-31";
//			System.out.println(date);
//			Date date2 = addDate(date);
//			System.out.println(date2);

			Date d = new Date();
			String s = DateUtil.format(d, "yyyy-MM-dd HH:00:00");
			System.out.println(s);
		}

		/**
		 * @param date
		 * @return
		 */
		public static String myDay(String currentDate) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			int y = Integer.parseInt(currentDate.substring(0, 4));
			int m = Integer.parseInt(currentDate.substring(4, 6));
			int d = Integer.parseInt(currentDate.substring(6, 8));
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m - 1);
			c.set(Calendar.DATE, d);
			return DateUtil.format(c.getTime(), "yyyy-MM-dd");

		}
		public static String preDay(String currentDate, int i,String format) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			int y = Integer.parseInt(currentDate.substring(0, 4));
			int m = Integer.parseInt(currentDate.substring(4, 6));
			int d = Integer.parseInt(currentDate.substring(6, 8));
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m - 1);
			c.set(Calendar.DATE, d - i);

			return DateUtil.format(c.getTime(), format);
		}
		/**
		 * @param date
		 * @param i
		 * @return
		 */
		public static String nextDay(String currentDate, int i) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			int y = Integer.parseInt(currentDate.substring(0, 4));
			int m = Integer.parseInt(currentDate.substring(4, 6));
			int d = Integer.parseInt(currentDate.substring(6, 8));
			c.set(Calendar.YEAR, y);
			c.set(Calendar.MONTH, m - 1);
			c.set(Calendar.DATE, d + i);

			return DateUtil.format(c.getTime(), "yyyyMMdd");
		}
		
		
		public static String nextDay(String currentDate, int i,String form) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			if(currentDate.length()==8){//yyyyMMdd
				int y = Integer.parseInt(currentDate.substring(0, 4));
				int m = Integer.parseInt(currentDate.substring(4, 6));
				int d = Integer.parseInt(currentDate.substring(6, 8));
				c.set(Calendar.YEAR, y);
				c.set(Calendar.MONTH, m - 1);
				c.set(Calendar.DATE, d + i);
			}else if(currentDate.length()==4){//MMdd
				int m = Integer.parseInt(currentDate.substring(0, 2));
				int d = Integer.parseInt(currentDate.substring(2, 4));
				c.set(Calendar.MONTH, m - 1);
				c.set(Calendar.DATE, d + i);
			}
			return DateUtil.format(c.getTime(), form);
		}
	

}
