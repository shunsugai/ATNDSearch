package com.sugaishun.atndsearch;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import net.java.textilej.parser.markup.textile.TextileDialect;
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
		browser = (WebView) findViewById(R.id.Webkit);
		browser.loadDataWithBaseURL("empty", buildHTML(), "text/html", "UTF-8", null);
	}
	
//	class MyWebView extends WebView {
//		private Context context;
//		private GestureDetector gesDetect;
//		
//		public MyWebView(Context context) {
//			super(context);
//			this.context = context;
//			gesDetect = new GestureDetector(context, onGestureListener);
//		}
//		
//		public MyWebView(Context context, AttributeSet attrs, int defStyle,
//				boolean privateBrowsing) {
//			super(context, attrs, defStyle, privateBrowsing);
//			this.context = context;
//			gesDetect = new GestureDetector(context, onGestureListener);
//		}
//
//		public MyWebView(Context context, AttributeSet attrs, int defStyle) {
//			super(context, attrs, defStyle);
//			this.context = context;
//			gesDetect = new GestureDetector(context, onGestureListener);
//		}
//
//		public MyWebView(Context context, AttributeSet attrs) {
//			super(context, attrs);
//			this.context = context;
//			gesDetect = new GestureDetector(context, onGestureListener);
//		}
//
//		@Override
//		public boolean onTouchEvent(MotionEvent event) {
//			gesDetect.onTouchEvent(event);
//			return false;
//		}
//		
//		SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
//			@Override
//			public boolean onDown(MotionEvent e) {
//				return super.onDown(e);
//			}
//
//			@Override
//			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//				Log.v(TAG, "x: " + velocityX);
//				if (velocityX < -1000)
//					finish();
//				return super.onFling(e1, e2, velocityX, velocityY);
//			}
//		};
//	}
	
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
		String css = "body,h1,h2,p{padding:0;margin:0}body{background-color:#F5F5F5;font-size:13px;padding:10px}h1{font-size:20px}h2{font-size:16px}";
		String html = "<html><head><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\">"
				+ "<style type=\"text/css\">" + css + "</style></head><body>"
				+ "<h1>" + title + "</h1>"
				+ "<h2>日時:</h2>" + "<p>" + DateHelper.shortDate(date) + " " + DateHelper.time(date) + "</p>"
				+ "<h2>場所:</h2>" + "<p>" + address + "</p>"
				+ "<h2>イベント概要:</h2>" + TextileToHtml(description) 
				+ "</body></html>";
		return html;
	}
}
