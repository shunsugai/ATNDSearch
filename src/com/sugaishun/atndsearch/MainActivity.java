package com.sugaishun.atndsearch;

import java.io.IOException;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int MYREQUEST = 1;
	
	private GetDataTask getData;
	private String keyword, prefecture, result;
	private String ym = null;
	private Handler handler;
	private JSONArray eventArray;
	
	private static final String MY_AD_UNIT_ID = "a14fdd0d7d55ff6";
	private AdView adView;
	
	private static final String[] prefectures = {
		"全国", "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", 
		"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県", 
		"新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県","静岡県", "愛知県",
		"三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県", 
		"鳥取県", "島根県", "岡山県", "広島県", "山口県",
		"徳島県", "香川県", "愛媛県", "高知県",
		"福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"};
	
	private static final String[] dates = {"すべての期間", "本日", "明日", "今週", "来週", "今月", "来月"};
	
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setSearchButton();
		setSpinnerPref();
		setSpinnerDate();
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

		// for Test
		AdRequest adrequest = new AdRequest();
		adrequest.addTestDevice(AdRequest.TEST_EMULATOR);

		// Initiate a generic request to load it with an ad
		adView.loadAd(adrequest);
		// adView.loadAd(new AdRequest());	
    }

	private void setSearchButton() {
        Button button = (Button) this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.editText1);
				keyword = text.getText().toString();
				Log.d(TAG, "Accept Keyword: " + keyword);
				
				//Start async task
				getData = new GetDataTask();
				getData.execute();
			}
        });
    }
    
    private void setSpinnerPref() {
        ArrayAdapter<String> aaPref = new ArrayAdapter<String>(
        		this, android.R.layout.simple_spinner_item, prefectures);

        Spinner spinPref = (Spinner) findViewById(R.id.spinner1);
        spinPref.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
				prefecture = prefectures[position];
				if(prefecture == "全国")
					prefecture = null;
				Log.d(TAG, "Accept Pref: " + prefecture);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				prefecture = null;
			}
        });

        aaPref.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPref.setAdapter(aaPref);
    }
    
    private void setSpinnerDate() {
    	ArrayAdapter<String> aaDate = new ArrayAdapter<String>(
    			this, android.R.layout.simple_spinner_item, dates);
    	
    	Spinner spinDate = (Spinner) findViewById(R.id.spinner2);
    	spinDate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				ym = getYM(position);
				Log.d(TAG, "ym:" + ym);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
    	
    	aaDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinDate.setAdapter(aaDate);
    }
    
	//HTMLRequest AsyncTask
	class GetDataTask extends AsyncTask<Void, String, Void> {
		private ProgressDialog myDialog;
		private AlertDialog.Builder adb;
		
		public GetDataTask() {
			myDialog = new ProgressDialog(MainActivity.this);
			adb = new AlertDialog.Builder(MainActivity.this);
			handler = new Handler();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				HttpGet url = getRequestUrl();
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				try {
					result = httpClient.execute(url,new ResponseHandler<String>() {
						@Override
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException,IOException {
							switch (response.getStatusLine().getStatusCode()) {
								case HttpStatus.SC_OK:
									return EntityUtils.toString(response.getEntity(), "UTF-8");
								case HttpStatus.SC_NOT_FOUND:
									throw new RuntimeException("No data");
								default:
									throw new RuntimeException("Connection Error");
							}
						}
					});
				} catch (Exception e) {
					Log.d(TAG, "Exception raised: " + e.getStackTrace() + e.getLocalizedMessage());
					handler.post(new Runnable(){

						@Override
						public void run() {
							closeDialog();
							setAlert("Connection Errorです");
							showAlert();
						}
					});
					return null;
				} finally {
					httpClient.getConnectionManager().shutdown();
					Log.d(TAG, "Connection closed");
				}
				
				if(isCancelled()) {
					Log.d(TAG, "doInBackground()内のHttpClientで接続した後でキャンセル");
					return null;
				}
				
				JSONObject rootObject = new JSONObject(result);				
				eventArray = rootObject.getJSONArray("events");
				Log.d(TAG, "Set data to eventArray");
			} catch (Exception e) {
				Log.d(TAG, "Exception raised: " + e.getStackTrace());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			super.onPostExecute(unused);
			Log.d(TAG, "onPostExecute");
			if (result != null && eventArray.length() == 0) {
				closeDialog();
				setAlert("検索結果は0件でした");
				showAlert();
			} else if (result != null) {
				Intent intent = new Intent(MainActivity.this,
						EventListActivity.class);
				intent.putExtra("jsonArray", eventArray.toString());
				startActivityForResult(intent, MYREQUEST);
				closeDialog();
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(TAG, "onCanceled");
			getData = null;
			closeDialog();
			super.onCancelled();
		}
		
		protected void showDialog() {
			myDialog.setIndeterminate(true);
			myDialog.setMessage("読み込んでいます…");
			myDialog.setCancelable(true);
			myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					getData.cancel(true);
					Log.d(TAG, "back buttonでcancelされた！");
				}
			});
			myDialog.setButton(
					DialogInterface.BUTTON_NEGATIVE,
					"キャンセル", 
					new DialogInterface.OnClickListener() {
						
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getData.cancel(true);
					Log.d(TAG, "cancelボタンが押された！");
				}
			});
			myDialog.show();			
		}
		
		protected void setAlert(String message) {
			adb.setTitle("ATND Search");
			adb.setMessage(message);
			adb.setPositiveButton("もどる", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}
		
		protected void showAlert() {
			AlertDialog ad = adb.create();
			ad.show();
		}
		
		protected void closeDialog() {
			if(myDialog != null && myDialog.isShowing())
				myDialog.dismiss();
		}
	}
	
	private HttpGet getRequestUrl() {
		Uri.Builder b = new Uri.Builder();
		b.scheme("http");
		b.encodedAuthority("api.atnd.org");
		b.path("/events/");
		b.appendQueryParameter("keyword", keyword);
		// 場所情報を追加
		if(prefecture != null)
			b.appendQueryParameter("keyword", prefecture);
		
		// 日程情報を追加
		if(ym != null && ym.length() == 6) {
			b.appendQueryParameter("ym", ym);
		} 
		else if(ym != null && ym.length() == 8) {
			b.appendQueryParameter("ymd", ym);
		}
		else if(ym == "thisweek_") {
			Calendar cal = Calendar.getInstance();
			int intW = cal.get(Calendar.DAY_OF_WEEK);
			for(int i = 0; i < (8 - intW); i++) {
				Calendar c = Calendar.getInstance();
				String date = "";
				c.add(Calendar.DAY_OF_MONTH, i);
				int intY = c.get(Calendar.YEAR);
				int intM = c.get(Calendar.MONTH) + 1;
				int intD = c.get(Calendar.DATE);
				String y = String.valueOf(intY);
				String m = String.valueOf(intM);
				String d = String.valueOf(intD);
				if(m.length() == 1)
					m = "0" + m;
				if(d.length() == 1)
					d = "0" + d;
				date = y + m + d;
				b.appendQueryParameter("ymd", date);
			}
		}
		else if(ym == "nextweek_") {
			Calendar cal = Calendar.getInstance();
			int intW = cal.get(Calendar.DAY_OF_WEEK);
			for(int i = 0; i < 7; i++) {
				Calendar c = Calendar.getInstance();
				String date = "";
				c.add(Calendar.DAY_OF_MONTH, i + (8 -intW));
				int intY = c.get(Calendar.YEAR);
				int intM = c.get(Calendar.MONTH) + 1;
				int intD = c.get(Calendar.DATE);
				String y = String.valueOf(intY);
				String m = String.valueOf(intM);
				String d = String.valueOf(intD);
				if(m.length() == 1)
					m = "0" + m;
				if(d.length() == 1)
					d = "0" + d;
				date = y + m + d;
				b.appendQueryParameter("ymd", date);
			}
		}
		
		b.appendQueryParameter("count", "20");
		b.appendQueryParameter("format", "json");
		
		HttpGet request = new HttpGet(b.build().toString());
		Log.d(TAG, "Request url: " + request.getURI().toString());
		
		return request;
	}
	
	public static String getYM(int pos){
		// 現在の日時を取得
		Calendar cal = Calendar.getInstance();
		int intY = cal.get(Calendar.YEAR);
		int intM = cal.get(Calendar.MONTH) + 1;
		int intD = cal.get(Calendar.DATE);
		int intW = cal.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "day of week :" + intW);
		String y, m, d;
		String ymd = null;
		
		// posの値によって場合わけ
		switch(pos) {
		case 0: // all
			ymd =  null;
			break;
			
		case 1: // today
			y = String.valueOf(intY);
			m = String.valueOf(intM);
			d = String.valueOf(intD);
			if(m.length() == 1)
				m = "0" + m;
			if(d.length() == 1)
				d = "0" + d;
			ymd =  y + m + d;
			break;
			
		case 2: // tomorrow
			intD += 1;
			y = String.valueOf(intY);
			m = String.valueOf(intM);
			d = String.valueOf(intD);
			if(m.length() == 1)
				m = "0" + m;
			if(d.length() == 1)
				d = "0" + d;
			ymd = y + m + d;
			break;
			
		case 3: // this week
			ymd = "thisweek_";
			break;
		
		case 4: // next week
			ymd = "nextweek_";
			break;
			
		case 5: // this month
			y = String.valueOf(intY);
			m = String.valueOf(intM);
			if(m.length() == 1)
				m = "0" + m;
			ymd = y + m;
			break;
			
		case 6: // next month
			intM += 1;
			y = String.valueOf(intY);
			m = String.valueOf(intM);
			if(m.length() == 1)
				m = "0" + m;
			ymd = y + m;
			break;
		}
		
		return ymd;
	}
}