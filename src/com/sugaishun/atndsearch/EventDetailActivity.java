package com.sugaishun.atndsearch;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class EventDetailActivity extends Activity {
	private static final String TAG = EventDetailActivity.class.getSimpleName();
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private AdView adView;
	private WebView browser;
	private Intent intent;
	private String title, date, address, description;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventdetail);
		getDataThroughIntent();	
		setBrowser();
		setAd();
	}

	private void setAd() {
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);

		// Lookup your LinearLayout assuming it’s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.footer);

		// Add the adView to it
		layout.addView(adView);

		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());		
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
		EventDetailHTMLBuilder htmlBuilder = new EventDetailHTMLBuilder(title, date, address, description);
		String html = htmlBuilder.getStringHtml();
		browser = (WebView) findViewById(R.id.Webkit);
		browser.loadDataWithBaseURL("empty", html, "text/html", "UTF-8", null);
	}
}
