package com.sugaishun.atndsearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class EventListActivity extends ListActivity {
	private static final String TAG = EventListActivity.class.getSimpleName();
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private static final int MYREQUEST = 2;
	private EventAdapter adapter;
	private List<Event> events;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);
		Intent intent = getIntent();
		jsonArrayToEvent(intent.getStringExtra("jsonArray"));
		setAdapter();
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

	@Override
	protected void onListItemClick(ListView parent, View v, int position, long id) {
		Intent intent = new Intent(this, EventDetailActivity.class);
		Event event = events.get(position);
		
		intent.putExtra("TITLE", event.getTitle());
		intent.putExtra("DATE", event.getDate());
		intent.putExtra("ADDRESS", event.getAddress());
		intent.putExtra("DESCRIPTION", event.getDescription());
		
		startActivityForResult(intent, MYREQUEST);
	}
	
	private void setAdapter() {
		adapter = new EventAdapter(EventListActivity.this, events);
        setListAdapter(adapter);
	}

	private void jsonArrayToEvent(String jsonArray) {
		try {
			JSONArray array = new JSONArray(jsonArray);			
			events = new ArrayList<Event>();
			
			if(array != null) {
				for(int i = 0; i < array.length(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					Event event = new Event();
					event.setTitle(jsonObject.getString("title"));					
					event.setDate(jsonObject.getString("started_at"));
					event.setAddress(jsonObject.getString("address"));
					event.setCatchcopy(jsonObject.getString("catch"));
					event.setDescription(jsonObject.getString("description"));
					events.add(event);
				}
			}
		} catch (JSONException e) { e.printStackTrace(); }
	}
}
