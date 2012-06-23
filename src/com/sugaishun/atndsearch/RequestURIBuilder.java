package com.sugaishun.atndsearch;

import java.util.Calendar;

import org.apache.http.client.methods.HttpGet;

import android.net.Uri;
import android.util.Log;

public class RequestURIBuilder {
	private static final String TAG = RequestURIBuilder.class.getSimpleName();
	private String keyword;
	private String prefecture;
	private int period;
	
	public RequestURIBuilder(String keyword, String prefecture, int period) {
		this.keyword = keyword;
		this.prefecture = prefecture;
		this.period = period;
		Log.d(TAG, "KEYWORD:"+keyword+" PREFECTURE:"+prefecture+" PERIOD:"+period);
	}
	
	public HttpGet getRequestURI() {		
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.encodedAuthority("api.atnd.org");
		builder.path("/events/");
		builder.appendQueryParameter("count", "20");
		builder.appendQueryParameter("format", "json");
		if (keyword != null)
			builder.appendQueryParameter("keyword", keyword);

		if (prefecture != null)
			builder.appendQueryParameter("keyword", prefecture);
		
		Calendar now = Calendar.getInstance();
		int intY = now.get(Calendar.YEAR);
		int intM = now.get(Calendar.MONTH) + 1;
		int intD = now.get(Calendar.DATE);
		int intW = now.get(Calendar.DAY_OF_WEEK);
		String ymd = "";

		switch (period) {
		case 0: // all
			break;
		case 1: // today
			ymd = String.format("%04d%02d%02d", intY, intM, intD);			
			builder.appendQueryParameter("ymd", ymd);
			break;
		case 2: // tomorrow
			now.add(Calendar.DAY_OF_MONTH, 1);
			intY = now.get(Calendar.YEAR);
			intM = now.get(Calendar.MONTH) + 1;
			intD = now.get(Calendar.DATE);
			ymd = String.format("%04d%02d%02d", intY, intM, intD);
			builder.appendQueryParameter("ymd", ymd);
			break;
		case 3: // this week
			for(int i = 0; i < (8 - intW); i++) {
				now.add(Calendar.DAY_OF_MONTH, 1);
				intY = now.get(Calendar.YEAR);
				intM = now.get(Calendar.MONTH) + 1;
				intD = now.get(Calendar.DATE);
				ymd = String.format("%04d%02d%02d", intY, intM, intD);
				builder.appendQueryParameter("ymd", ymd);
			}
			break;
		case 4: // next week
			now.add(Calendar.DAY_OF_MONTH, 8 - intW);
			for(int i = 0; i < 7; i++) {
				now.add(Calendar.DAY_OF_MONTH, 1);
				intY = now.get(Calendar.YEAR);
				intM = now.get(Calendar.MONTH) + 1;
				intD = now.get(Calendar.DATE);
				ymd = String.format("%04d%02d%02d", intY, intM, intD);
				builder.appendQueryParameter("ymd", ymd);
			}
			break;
		case 5: // this month
			ymd = String.format("%04d%02d", intY, intM);	
			builder.appendQueryParameter("ym", ymd);
			break;
		case 6: // next month
			now.add(Calendar.MONTH, 1);
			intY = now.get(Calendar.YEAR);
			intM = now.get(Calendar.MONTH) + 1;
			ymd = String.format("%04d%02d", intY, intM);
			builder.appendQueryParameter("ym", ymd);
			break;
		}
		
		HttpGet requestUrl = new HttpGet(builder.build().toString());
		return requestUrl;
	}
}
