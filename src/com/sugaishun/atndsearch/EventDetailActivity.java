package com.sugaishun.atndsearch;

import net.java.textilej.parser.markup.textile.TextileDialect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class EventDetailActivity extends Activity {
//	private static final String TAG = EventDetailActivity.class.getSimpleName();
	WebView browser;
	private Intent intent;
	private String title, date, address, description;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventdetail);
		getDataThroughIntent();	
		setBrowser();
	}
	
	private void getDataThroughIntent() {
		intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null) {
			title = extras.getString("TITLE");
			date = extras.getString("DATE");
			address = extras.getString("ADDRESS");
			description = extras.getString("DESCRIPTION");
		}
	}
	
	private void setBrowser() {
		browser = (WebView) findViewById(R.id.Webkit);
		browser.loadDataWithBaseURL("empty", buildHTML(), "text/html", "UTF-8", null);
	}
	
	private String TextileToHtml(String text) {
		String html = "";
		try {
			html = EventDetailHelper.textToHtml(text, TextileDialect.class, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	private String buildHTML() {
		String html = "<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\"></head><body>"
				+ "<h1>" + title + "</h1>"
				+ "<h2>date:</h2>" + "<p>" + DateHelper.shortDate(date) + "</p>"
				+ "<h2>address:</h2>" + "<p>" + address + "</p>"
				+ "<h2>description:</h2>" + TextileToHtml(description) 
				+ "</body></html>";
		return html;
	}
}
