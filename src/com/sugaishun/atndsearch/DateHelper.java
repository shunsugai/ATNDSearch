package com.sugaishun.atndsearch;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateHelper {
//	private static final String TAG = DateHelper.class.getSimpleName();
	
	public static String shortDate(String date) {
		DateTimeFormatter timeParser = ISODateTimeFormat.dateTimeNoMillis();
		DateTime dateTime = timeParser.withZone(DateTimeZone.forID("Asia/Tokyo")).parseDateTime(date);
		return dateTime.toString(DateTimeFormat.fullDate());
	}
	public static String time(String date) {
		DateTimeFormatter timeParser = ISODateTimeFormat.dateTimeNoMillis();
		DateTime dateTime = timeParser.withZone(DateTimeZone.forID("Asia/Tokyo")).parseDateTime(date);		
		return dateTime.toString(DateTimeFormat.shortTime());
	}
}
