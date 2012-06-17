package com.sugaishun.atndsearch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.google.ads.*;

public class Banner extends Activity {
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create the adView
		adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);

		// Lookup your LinearLayout assuming it’s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.footer);

		// Add the adView to it
		layout.addView(adView);
		
		// for Test
		AdRequest adrequest = new AdRequest();
		adrequest.addTestDevice(AdRequest.TEST_EMULATOR);
		
		// Initiate a generic request to load it with an ad
		adView.loadAd(adrequest);
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
}