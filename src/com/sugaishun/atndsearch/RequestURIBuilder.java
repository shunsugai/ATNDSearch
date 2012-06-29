package com.sugaishun.atndsearch;

import java.util.Calendar;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.util.Log;

public class RequestURIBuilder {
	private static final String TAG = RequestURIBuilder.class.getSimpleName();
	Uri.Builder builder;
	private String keyword;
	private String prefecture;
	private int period;
	private int startPosition = 1;
	
	public RequestURIBuilder(String keyword, String prefecture, int period) {
		this.keyword = keyword;
		this.prefecture = prefecture;
		this.period = period;
	}
	
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	
	public void appendStartPosition(int position) {
		builder.appendQueryParameter("start", String.valueOf(position));
	}
	
	// 長すぎひどい。あとで分割する。
	public HttpGet getRequestURI() {	
		builder = new Uri.Builder();
		builder.scheme("http");
		builder.encodedAuthority("api.atnd.org");
		builder.path("/events/");
		builder.appendQueryParameter("count", "20");
		builder.appendQueryParameter("format", "json");
		if (startPosition != 1)
			builder.appendQueryParameter("start", String.valueOf(startPosition));
			
		if (keyword != null)
			builder.appendQueryParameter("keyword", keyword);

		if (prefecture != null)
			builder.appendQueryParameter("keyword", prefecture);
		
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

		switch (period) {
		case 0: // all
			break;
		case 1: // today
			builder.appendQueryParameter("ymd", getStringDate(now));
			break;
		case 2: // tomorrow
			now.add(Calendar.DAY_OF_MONTH, 1);
			builder.appendQueryParameter("ymd", getStringDate(now));
			break;
		case 3: // this week
			for(int i = 0; i < (8 - dayOfWeek); i++) {
				now.add(Calendar.DAY_OF_MONTH, 1);
				builder.appendQueryParameter("ymd", getStringDate(now));
			}
			break;
		case 4: // next week
			now.add(Calendar.DAY_OF_MONTH, 8 - dayOfWeek);
			for(int i = 0; i < 7; i++) {
				now.add(Calendar.DAY_OF_MONTH, 1);
				builder.appendQueryParameter("ymd", getStringDate(now));
			}
			break;
		case 5: // this month
			builder.appendQueryParameter("ym", getStringMonth(now));
			break;
		case 6: // next month
			now.add(Calendar.MONTH, 1);
			builder.appendQueryParameter("ym", getStringMonth(now));
			break;
		}
		
		HttpGet requestUrl = new HttpGet(builder.build().toString());
		return requestUrl;
	}
	
	private String getStringDate(Calendar now) {
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int date = now.get(Calendar.DATE);
		return String.format("%04d%02d%02d", year, month, date);
	}
	
	private String getStringMonth(Calendar now) {
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		return String.format("%04d%02d", year, month);
	}
}
