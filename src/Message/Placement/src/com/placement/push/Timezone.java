package com.placement.push;

import java.util.*;
/**
 * This class will give me the correct date for the timezone.
 * @author Varun
 *
 */
public class Timezone 
{

	/**
	 * The timezone where the app is primarily deployed
	 */
	private static String timeZone = "Asia/kolkata";
	
	/**
	 * 
	 * @param currentDate The Date and time encapsulated inside a date object(of the server machine)
	 * @param timeZoneId The required Timezone, ie the timezone in which the date is required 
	 * @return Date object of the specified timezone
	 */
	private static Date getDateInTimeZone(Date currentDate, String timeZoneId)
	{
		Calendar mbCal = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
		mbCal.setTimeInMillis(currentDate.getTime());
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));
		
		return cal.getTime();
	}
	/**
	 * 
	 * @return Gives the date in a format tailored to our application
	 */
	public static String getStringedDate()
		{
			Date d = Timezone.getDateInTimeZone(new Date(),Timezone.timeZone );
			String date[] = d.toString().split(" ");
			String result ="";
			result+= date[2];
			switch(date[1].toLowerCase())
			{
				case "jan":result+="01";
					break;
	
				case "feb":result+="02";
					break;
	
				case "mar":result+="03";
						break;
		
				case "apr":result+="04";
						break;
		
				case "may":result+="05";
						break;
		
				case "jun":result+="06";
						break;
		
				case "jul":result+="07";
						break;
	
				case "aug":result+="08";
						break;
	
				case "sep":result+="09";
						break;
	
				case "oct":result+="10";
						break;
	
				case "nov":result+="11";
						break;
	
				case "dec":result+="12";
						break;
				default: result="";
							
			}
			result+= date[5];
			return result;
		}
}