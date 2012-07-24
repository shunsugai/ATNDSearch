package com.sugaishun.atndsearch;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

public class EventAdapter extends BaseAdapter {
//	private static final String TAG = EventAdapter.class.getSimpleName();
	private Context ctx;
	private List<Event> events;
	
	public EventAdapter(Context context, List<Event> events) {
		this.ctx = context;
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
	public View getView(int position, View contentView, ViewGroup parent) {
		Event event = events.get(position);
		return new EventListView(ctx, event.getTitle(), event.getDate(), event.getCatchcopy());
	}
	
	private final class EventListView extends LinearLayout {
		private TextView v_title;
		private TextView v_date;
		private TextView v_catchcopy;

		public EventListView(Context context, String title, String date, String catchcopy) {
			super(context);
			
			setOrientation(LinearLayout.VERTICAL);
			setBackgroundDrawable(context.getResources().getDrawable(R.drawable.atnd_list_background));
			setPadding(10, 5, 10, 5);
			
			LinearLayout.LayoutParams param = 
					new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
			param.setMargins(3, 3, 3, 3);
			
			v_date = new TextView(context);
			v_date.setText(getFormattedDateTime(date));
			v_date.setTextSize(13f);
			v_date.setTextColor(Color.DKGRAY);
			addView(v_date, param);

			v_title = new TextView(context);
			v_title.setText(title);
			v_title.setTextSize(16f);
			v_title.setTextColor(Color.BLACK);
			v_title.setMaxLines(2);
			v_title.setMinLines(2);
			v_title.setEllipsize(TextUtils.TruncateAt.END);
			addView(v_title, param);
			
			v_catchcopy = new TextView(context);
			v_catchcopy.setText(catchcopy);
			v_catchcopy.setTextSize(13f);
			v_catchcopy.setTextColor(Color.DKGRAY);
			v_catchcopy.setSingleLine(true);
			v_catchcopy.setEllipsize(TextUtils.TruncateAt.END);
			addView(v_catchcopy, param);
		}
	}
	
	public String getFormattedDateTime(String ISO8601DateTime) {
		DateTime dt = new DateTime(ISO8601DateTime);
		return dt.toString("yyyy年MM月dd日 HH:mm", Locale.JAPAN);
	}
}
