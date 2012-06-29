package com.sugaishun.atndsearch;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private AdView adView;
	private FetchDataTask fetchData;
	private String keyword, prefecture;
	private int period;

	private static final String[] prefectures = { "全国", 
			"北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", 
			"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県","東京都", "神奈川県", 
			"新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県",
			"三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
			"鳥取県", "島根県", "岡山県", "広島県", "山口県", 
			"徳島県", "香川県", "愛媛県", "高知県",
			"福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県" };

	private static final String[] dates = { "すべての期間", "本日", "明日", "今週", "来週", "今月", "来月" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setSearchButton();
		setSpinnerPrefecture();
		setSpinnerDate();
		setAd();
	}

	private void setAd() {
		adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.footer);
		layout.addView(adView);
		// for Test
		AdRequest adrequest = new AdRequest();
		adrequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(adrequest);
		// adView.loadAd(new AdRequest());
	}
	
	// ネストが深くなるのがイヤだったので内部クラスにした。
	private void setSearchButton() {
		Button button = (Button) this.findViewById(R.id.button1);
		button.setOnClickListener(new SearchButtonOnClickListener());
	}
	
	public class SearchButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			EditText text = (EditText) findViewById(R.id.editText1);
			keyword = text.getText().toString();
			
			RequestURIBuilder rub = new RequestURIBuilder(keyword, prefecture, period);
			HttpGet requestURL = rub.getRequestURI();
			
			fetchData = new FetchDataTask(MainActivity.this, requestURL);
			fetchData.execute();
//			fetchData.setOnCallBack(new MyCallBackTask());
		}
	}
	
//	public class MyCallBackTask extends FetchDataTask.CallBackTask {
//		@Override
//		public void CallBack(String result) {
//			try {
//				JSONObject rootObject = new JSONObject(result);
//				JSONArray eventArray = rootObject.getJSONArray("events");
//				
//				Intent intent = new Intent(MainActivity.this, EventListActivity.class);
//				intent.putExtra("jsonArray", eventArray.toString());
//				startActivity(intent);
//			} catch (Exception e) {}
//		}
//	}

	private void setSpinnerPrefecture() {
		ArrayAdapter<String> aaPref = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, prefectures);
		Spinner spinPref = (Spinner) findViewById(R.id.spinner1);

		spinPref.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				prefecture = prefectures[position];
				if (prefecture == "全国")
					prefecture = null;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		aaPref.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinPref.setAdapter(aaPref);
	}

	private void setSpinnerDate() {
		ArrayAdapter<String> aaDate = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dates);
		Spinner spinDate = (Spinner) findViewById(R.id.spinner2);

		spinDate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				period = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				period = 0;
			}
		});

		aaDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinDate.setAdapter(aaDate);
	}
}
