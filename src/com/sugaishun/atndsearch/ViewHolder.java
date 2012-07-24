package com.sugaishun.atndsearch;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolder {
	LinearLayout layout = null;
	TextView date = null;
	TextView title = null;
	TextView subTitle = null;
	
	ViewHolder(View base) {
		this.layout   = (LinearLayout) base.findViewById(R.id.row_text_area);
		this.date     = (TextView) layout.findViewById(R.id.date);
		this.title    = (TextView) layout.findViewById(R.id.title);
		this.subTitle = (TextView) layout.findViewById(R.id.subtitle);
	}
}
