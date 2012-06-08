package com.sugaishun.atndsearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class EventListActivity extends ListActivity {
	private static final String TAG = EventListActivity.class.getSimpleName();
	private static final int MYREQUEST = 2;
	private EventAdapter adapter;
	private List<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlist);
		Intent intent = getIntent();
		jsonArrayToEvent(intent.getStringExtra("jsonArray"));
		setAdapter();
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
			Log.d(TAG, "JsonArray‚ðŽó‚¯Žæ‚Á‚½");
			
			events = new ArrayList<Event>();
			
			if(array != null) {
				for(int i = 0; i < array.length(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					Event e = new Event();
					e.setTitle(jsonObject.getString("title"));					
					e.setDate(jsonObject.getString("started_at"));
					e.setAddress(jsonObject.getString("address"));
					e.setCatchcopy(jsonObject.getString("catch"));
					e.setDescription(jsonObject.getString("description"));
					events.add(e);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
