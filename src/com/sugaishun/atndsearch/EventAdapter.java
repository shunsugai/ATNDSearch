package com.sugaishun.atndsearch;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

public class EventAdapter extends BaseAdapter {
//	private static final String TAG = EventAdapter.class.getSimpleName();
	private Context context;
	private List<Event> events;
	
	public EventAdapter(Context context, List<Event> events) {
		this.context = context;
		this.events = events;
	}
	
	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int psositon) {
		return events.get(psositon);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Event event = events.get(position);
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.row, parent, false);
		}
		
		ViewHolder holder = (ViewHolder) row.getTag();
		
		if (holder == null) {
			holder = new ViewHolder(row);
			row.setTag(holder);
		}
		
		holder.date.setText(getFormattedDateTime(event.getDate()));
		holder.title.setText(event.getTitle());
		holder.subTitle.setText(event.getCatchcopy());
		
		return row;
	}

	public String getFormattedDateTime(String ISO8601DateTime) {
		DateTime dt = new DateTime(ISO8601DateTime);
		return dt.toString("yyyy年MM月dd日 HH:mm", Locale.JAPAN);
	}
}
